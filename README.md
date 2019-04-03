# Seckill
利用Mybatis、Spring、SpringMVC编写的一个小的秒杀API程序

创建Maven项目-->war

主要分为三个阶段：
Mybatis：dao层开发
Spring：service开发
SpringMVC：Web层开fa

第一步：数据库的设计
主要有一个秒杀商品表和一个秒杀成功明细表
create table seckill(
`seckill_id` bigint not null auto_increment comment '商品库存',
`name` varchar(20) not null comment '商品名称',
`number` int not null comment '库存数量',
`start_time` timestamp not null comment '秒杀开始时间',
`end_time` timestamp not null comment '秒杀结束时间',
`create_time` timestamp not null default current_timestamp comment '创建时间',
primary key(`seckill_id`),
key idx_start_time(`start_time`),
key idx_end_time(`end_time`),
key idx_create_time(`create_time`)
)ENGINE=INNODB auto_increment=1000 DEFAULT charset=utf8 comment '秒杀库存表';

drop table seckill;

insert into seckill(seckill_id,name,number,start_time,end_time) 
values
(default,'1000元秒杀iPhoneXRS',100,'2019-04-02 00:00:00','2019-04-05 00:00:00'),
(default,'500元秒杀iPad',200,'2019-04-03 00:00:00','2019-04-05 00:00:00'),
(default,'300元秒杀小米8',300,'2019-04-02 00:00:00','2019-04-05 00:00:00'),
(default,'100元秒杀红米note7Pro',400,'2019-04-04 00:00:00','2019-04-05 00:00:00');

select * from seckill;

create table success_killed(
`seckill_id` bigint not null comment '秒杀商品id',
`user_phone` bigint not null comment '用户手机号',
`state` tinyint not null default -1 comment '状态标示：-1表无效',
`create_time` timestamp not null comment'创建时间',
primary key(seckill_id,user_phone),
key idx_create_time(`create_time`)
)ENGINE=INNODB default charset=utf8 comment '秒杀成功明细表';

第二步针对表创建响应的实体属性：entity包

第三部开始Mybatis的Dao层的设计

主要涉及到的是初始接口的设计，接口的设计需要站在用户的角度考虑
对于秒杀的商品表，需要完成的主要的三个功能是：
1：商品库存的数量变化（减库存）int reduceNumber
2：根据响应的产品id找到商品信息（针对单个商品）Seckill queryById
3：根据偏移量来查找所有的秒杀商品（如果商品数量过大，会要求分页处理，以确保页面显示）List<Seckill> queryAll

对于秒杀成功的明细表：
1：插入秒杀成功以后的相应的信息（创建的时间也要插入，否则在查询信息的时候，时间为null是会报错的） int insertSuccessKilled
2：根据id查询成功秒杀的商品，并携带秒杀商品的信息（因为在成功秒杀明细中有商品的信息，所以需要查询时候携带）SuccessKilled queryByIdWithSeckill

这里需要注意在Dao接口中如果方法传递的参数大于一个的时候，需要使用@Param("XX") XX来表示，否则在sql语句总会显示绑定失败：not found!

对于dao接口，需要创建具体的mapper.xml来用sql语句实现：
这里使用的是xml文件来实现具体Dao接口功能的sql语句，因为xml文件对于sql语句的细节掌握的更好，可以根据不同的场景使用不同的语句，更加灵活。

在mybatis-config.xml中配置mybatis的全局属性，以便实现响应属性的自动注入

第二部分是关于service的设计
service中的也是接口，接口的设计需要站在使用者的角度来设计

这里使用了一个dto，数据传输包的概念，就是在service和web之间多构建了一个包装类，dto可以对数据进行二次封装（根据不同的构造方法来）,获取到想要的数据。

对于秒杀service，SeckillService中的接口有：
1：获取详情页（得到所有秒杀商品的信息 ：List<Seckill> getSeckillList();
2：根据seckillId获取到指定的商品信息 ：Seckill getById(long seckillId);
3：暴露用户秒杀的地址（用了一个包装类，主要是用于告诉系统是否开启秒杀，以及在其中加入加密的md5码，获取系统的当前时间，开始时间，结束时间，根据不同的构造方法实现返回不同的封装数据）：Exposer exportSeckillUrl(long seckillId)
4：执行秒杀（也用了一个封装类，告诉系统秒杀的状态，以及秒杀状态码，以及秒杀详细信息）：ExecutionSeckill executeSeckill(long seckillId, long userPhone, String md5)
还运用了exception异常处理，创建一个exception的包，将在秒杀过程中可能出现的所有异常所有封装成具体的类，用于在不同的场景下抛出。

SeckillServiceImpl的设计，实现SeckillService接口，实现其中的方法。
-- 自动注入SecKillDao和SuccessKilledDao对象

重点介绍暴露用户秒杀地址和执行秒杀过程的实现
public Exposer exportSeckillUrl(long seckillId) {
		//首先获得这个对象
		Seckill seckill = seckillDao.queryById(seckillId);
		if(seckill == null) {
			return new Exposer(false,seckillId);
		}
		Date startTime = seckill.getStartTime();
		
		Date endTime = seckill.getEndTime();
		
		Date nowTime = new Date();
		
		if(nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
			return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
		}
		String md5 = getMd5(seckillId);
		return new Exposer(true, md5, seckillId);
	}
代码如上：
具体功能描述：根据id查询到商品对象，判断对象是否存在，不存在的话直接利用封装的数据类返回失败的数据。存在的话获取商品开始秒杀的时间，结束时间，以及系统当前的时间，对时间做一个逻辑的判断，看系统时间是否满足暴露接口的条件，满足的话，获取md5值，返回成功的用户秒杀地址。

private String getMd5(long seckillId) {
		String base = seckillId + "/" + slat;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}
md5值的产生用了一个私有的方法：getMD5()，利用商品的id掺杂盐值slat，然后通过一个工具类生成md5码，MD5码具有不可还原性。

执行秒杀的实现：
public ExecutionSeckill executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, RepeatException, SeckillEndException {
		try {
			//首先判断md5值是否相同
			if(md5==null || !md5.equals(getMd5(seckillId))) {
				//直接归类为总的异常
				throw new SeckillException("data rewrite");
			}
			//开始秒杀，执行减库存+插入记录
			Date nowTime = new Date();
			//减库存--如果减库存失败，需要返回一个什么
			int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
			if(updateCount <= 0) {
				//没有更新到数据 秒杀结束
				throw new SeckillEndException("seckill is closed");
			}else {
				//插入记录
				Date createTime = new Date();
				int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone,createTime);
				if(insertCount <=0) {
					//插入数据失败，重复秒杀
					throw new RepeatException("seckill repeat");
				}else {
					SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
					return new ExecutionSeckill(seckillId, SeckillEnums.SUCCESS, successKilled);
				}
			}
		} catch(SeckillEndException e) {
			throw e;
		}catch(RepeatException e) {
			throw e;
		}catch (Exception e) {
			logger.info(e.getMessage());
			throw new SeckillEndException("系统异常："+e.getMessage());
		}
	}
代码如上：
 首先判断md5的值是否相同，防止用户通过第三方插件伪造秒杀地址执行秒杀。执行秒杀，获取系统当前时间，执行商品减库存的操作，判断减库存执行后的状态，根据更新的成功或者失败做进一步的处理，成功则插入明细，不成功直接抛出之前定义的异常，告知系统不能更新的原因。成功的话下一步：插入到秒杀成功明细表当中，判断插入的状态，根据插入的返回的int状态码，判断成功或者失败，失败的或返回秒杀重复给系统，成功的话一个流程就走完了，秒杀结束。
 
这里由于发生了update和insert操作，需要添加声明式事务来管理，推荐使用的是@Transactional+xml配置，使用注解可以让我们更好的知道在这段代码使用了声明式事务。

这里还有一个小的知识点就是：将所有的系统执行状态码和状态明细使用Enum枚举类来表示，更加直观。

第三部分就是web层的设计。

web层的设计具体的url使用的是Restful风格的设计，/模块/资源/{数据}/细分，更加的直观。

以及对于返回统一的json数据格式，使用produces = {"application/json;charset=UTF-8"}

资源的占位，比如/seckill/{seckillId}/{md5}/execution，中使用了两个占位，当设计执行方法的时候，需要使用@PathVariable()的方式指定这个参数是多少，否则会发生参数未定义的情况。

这里还有一个就是javascript的模块化：
//代码封装
//javascript模块化
var seckill={
	//封装秒杀的ajax相关的url地址
	URL : {
		now : '/seckill/seckill/time/now',
		exposer : function(seckillId){
			return '/seckill/seckill/'+seckillId+'/exposer';
		},
		execution : function(seckillId,md5){
			return '/seckill/seckill/'+seckillId+'/'+md5+'/execution';
		}
	},
	validatePhone : function(phone){
		if(phone && phone.length == 11 && !isNaN(phone) ){
			return true;
		}else{
			return false;
		}
	},
	handlerSeckillkill : function(seckillId,node){
		node.hide()
			.html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
		$.post(seckill.URL.exposer(seckillId),{},function(result){
			//暴露秒杀接口地址
			if(result && result['success']){
				//获取到暴露的接口地址数据
				var exposer = result['data'];
				var md5 = exposer['md5'];
				var killUrl = seckill.URL.execution(seckillId,md5);
				//可以执行秒杀了
				if(exposer['exposed']){
						//按键的时间绑定
						$('#killBtn').one('click',function(){
							$(this).addClass('disabled');             //按键失效
							$.post(killUrl,{},function(result){
								if(result && result['success']){
									//获取秒杀的数据
									var execute = result['data'];
									//获取秒杀的状态
									var state = execute['state'];
									//获取秒杀的信息
									var stateInfo = execute['stateInfo'];
									//显示秒杀结果
									node.html('<span class="label label-success">'+stateInfo+'</span>');
								}
							});
					});
				}else{
					//不能执行秒杀获取系统当前时间 重新走到倒计时界面
					var now = exposer['now'];
					var start = exposer['start'];
					var end = exposer['end'];
					seckill.countDown(seckillId,now,start,end);
				}
				node.show();
			} else{
				console.log("result:"+result);
			}
		});
	},
	countDown : function(seckillId,nowTime,startTime,endTime){
		//根据时间来验证
		var seckillBox = $('#seckill-box');
		if(nowTime > endTime){
			seckillBox.html('秒杀结束！');
		} else if(nowTime < startTime){
//			seckillBox.html('秒杀未开始！');
			//启动倒计时 计时事件绑定
			var killTime = new Date(startTime+1000);
			seckillBox.countdown(killTime,function(event){
				//时间格式
				var format = event.strftime("秒杀倒计时：%D天  %H时  %M分  %S秒");
				seckillBox.html(format);
			}).on('finished.countdown',function(){
				//获取秒杀地址 实现现实逻辑
				seckill.handlerSeckillkill(seckillId,seckillBox);
			});
		} else{
			//秒杀开始
			seckill.handlerSeckillkill(seckillId,seckillBox);
		}
	},
	//详情页封装
	detail : {
		//初始化
		init : function(params){
			//手机验证和登录 交互
			//规划交互流程
			//验证手机号
			//在cookie中获取手机号
			var killPhone = $.cookie('killPhone');
			//开始验证手机号
			//验证不通过
			if(!seckill.validatePhone(killPhone)){
				//提醒用户注册
				//绑定phone
				var killPhoneModal = $('#killPhoneModal');
				killPhoneModal.modal({
					show : true,//显示弹出层
					dropback : 'static',//禁止拖拽事件
					keyboard : false//关闭键盘事件
				});
				//点击提交按钮的时候，触发此函数
				$('#killPhoneBtn').click(function(){
					var inputPhone = $('#killPhoneKey').val();
					//在验证一遍
					if(seckill.validatePhone(inputPhone)){
						//可以查看详情页
						//将其写进cookie
						$.cookie('killPhone',inputPhone,{expires:7,path:'/seckill'});
						//页面重新刷新就好
						window.location.reload();
					} else{
						$('#killPhoneMessage').hide().html('<label class="text-danger">手机号错误！</label>').show(300);
					}
				});
			}
			//已经登录
			var startTime = params['startTime'];
			var endTime = params['endTime'];
			var seckillId = params['seckillId'];
			$.get(seckill.URL.now,{},function(result){
				//result['success']是time中的一个封装好的success为true才有data
				if(result && result['success']){
					var nowTime = result['data'];
					seckill.countDown(seckillId,nowTime,startTime,endTime);
				}else{
					console.log("result:"+result);
				}
			});
		}
	}
};

 $(function(){
	//使用EL表达式传递参数
		seckill.detail.init({
			seckillId : ${seckill.seckillId},
			startTime : ${seckill.startTime.time},//单位毫秒 EL表达式的time可以直接转换成为毫秒
			endTime : ${seckill.endTime.time}
		});
 });
 
 这里的主要逻辑：
 秒杀的逻辑：
 首先是对于详情页的封装：
 
详情页中的要的数据在detail.jsp使用EL表达式就可以在Controller中传过来的model中取到，可以通过上面的代码传入具体的参数；
具体流程：从cookie中取得手机号，验证手机号，如果手机号验证不通过（就是不满足验证的条件），弹出一个框提醒用户输入手机号进行注册绑定，当用户输入之后点击提交按钮的时候，继续做一次手机号的验证，如果手机号验证通过则将数据写入浏览器的cookie中，并刷新当前的页面，如果验证不通过则显示手机号错误的信息提醒用户再次输入手机号，直到成功。
 
用户手机号验证成功之后可以到达商品秒杀界面，此时需要根据当前时间和是商品秒杀开始和结束时间作对比，以此来执行不同的操作，若当前时间大于结束时间，提醒用户秒杀结束，如果当前时间小于开始时间，则使用倒计时插件执行倒计时，告知用户需要等待多久才可以秒杀。当前时间处于开始结束之间，直接开启秒杀。倒计时的话，直接使用倒计时插件，设置指定的输出格式即可。倒计时结束需要设置完成倒计时之后开启秒杀。

秒杀业务：首先需要获取暴露的接口地址，从得到的数据判断是否接口暴露，暴露的话继续判断秒杀状态，否则直接输出错误的信息。判断秒杀状态，判断是否开启秒杀，开启秒杀则获取md5值，开始执行秒杀，否则获取当前时间，开始时间，结束时间，继续倒计时。开始秒杀点击之后获取执行秒杀需要的数据传入，得到秒杀的状态以及明细，最后显示秒杀结果。

Spring-dao.xml配置文件
<!-- 资源文件放置的位置 -->
        <context:property-placeholder location="classpath:db.properties"/>
        <!-- 配置数据库连接池 -->
        <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        	<property name="driverClass" value="${driver}"></property>
        	<property name="jdbcUrl" value="${url}"></property>
        	<property name="user" value="root"></property>
        	<property name="password" value="${password}"></property>
        	
        	<!-- 数据库连接池的私有属性配置 -->
        	<!-- 连接池最大容量 -->
        	<property name="maxPoolSize" value="30"></property>
        	<!-- 连接池最小容量 -->
        	<property name="minPoolSize" value="10"></property>
        	<!-- 不自动提交 -->
        	<property name="autoCommitOnClose" value="false"></property>
        	<!-- 检查延时时间 大于未连接就报错 -->
        	<property name="checkoutTimeout" value="10000"></property>
        	<!-- 连接失败重试次数 -->
        	<property name="acquireRetryAttempts" value="2"></property>
        </bean>
        
        <!-- 配置mybatis工厂，整合Spring -->
        <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        	<!-- 数据来源 注入数据库连接池 -->
        	<property name="dataSource" ref="dataSource"></property>
        	<!-- 配置mybatis全局配置文件 -->
        	<property name="configLocation" value="classpath:mybatis-config.xml"></property>
        	<!-- 配置扫描entity包文件 -->
        	<property name="typeAliasesPackage" value="org.seckill.entity"></property>
        	<!-- 配置扫描存在的mapper需要的文件 -->
        	<property name="mapperLocations" value="classpath:mapper/*.xml"></property>
        </bean>
        
        <!-- 配置扫描Dao的接口包，动态实现Dao接口，注入到Spring容器中 -->
        <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        	<!-- 扫描Dao，实现自动注入 -->
        	<property name="basePackage" value="org.seckill.dao"></property>
        	<!-- 实现注入sqlSessionFactory-->
        	<property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"></property>
        </bean>

spring-service.xml配置文件：
        <!-- 注解扫描 service包下的文件 -->
        <context:component-scan base-package="org.seckill.service"></context:component-scan>
        <!-- 配置声明式事务 -->
        <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        	<!-- 注入dataSource -->
        	<property name="dataSource" ref="dataSource"></property>
        </bean>
        
        <!-- 声明式事务注解驱动 -->
        <tx:annotation-driven transaction-manager="transactionManager"/>

spring-web.xml配置文件：
        <!-- 注解扫描激活 
        	整合的步骤 mybatis->spring->springMVC
        -->
       	<mvc:annotation-driven></mvc:annotation-driven>
        <!-- 静态资源的路径访问设置使用‘/’ -->
        <mvc:default-servlet-handler/>
        <!-- 视图解析器 -->
        <bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        	<property name="viewClass" value="org.springframework.web.servlet.view.JstlView"></property>
        	<property name="prefix" value="/WEB-INF/jsp/"/>
   			<property name="suffix" value=".jsp"/>
        </bean>
        <!-- 包扫描 -->
        <context:component-scan base-package="org.seckill.web"></context:component-scan>

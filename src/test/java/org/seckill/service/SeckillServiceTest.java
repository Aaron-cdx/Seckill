package org.seckill.service;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.ExecutionSeckill;
import org.seckill.dto.Exposer;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatException;
import org.seckill.exception.SeckillEndException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml","classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
	private static final Logger logger = LoggerFactory.getLogger(SeckillServiceTest.class);
	
	@Resource
	private SeckillService seckillService;
	@Test
	public void testGetSeckillList() {
		List<Seckill> seckillList = seckillService.getSeckillList();
		logger.info("seckillList={}"+seckillList);
	}
	
	/*
	 * byId=Seckill [seckillId=1000, 
	 * name=1000元秒杀iPhoneXRS, 
	 * number=100, startTime=Tue 
	 * Apr 02 08:00:00 CST 2019, 
	 * endTime=Fri Apr 05 08:00:00 CST 2019, 
	 * createTime=Tue Apr 02 03:04:17 CST 2019]
	 */
	@Test
	public void testGetById() {
		long seckillId = 1000L;
		Seckill byId = seckillService.getById(seckillId);
		logger.info("byId={}",byId);
	}
	/**
	 * md5=7eeda0d015ebd4869fdcbcd09e77a4c9
	 * 下面是一个集成逻辑测试，将接口暴露和执行秒杀放在一起
	 * 注意，秒杀是开启的才可以执行手机号和md5的获取，要是未开启就不用执行秒杀业务了
	 */
	@Test
	public void SeckillLogic() {
		//1001时间是不允许的,所以返回的是系统异常
		long seckillId = 1001L;
		Exposer exportSeckillUrl = seckillService.exportSeckillUrl(seckillId);
		if(exportSeckillUrl.isExposed()) {
			String md5 = exportSeckillUrl.getMd5();
			long userPhone = 15779236475L;
			try {
				ExecutionSeckill executeSeckill = seckillService.executeSeckill(seckillId, userPhone, md5);
				logger.info("result={}", executeSeckill);
			} catch (RepeatException e) {
				logger.info(e.getMessage());
			} catch (SeckillEndException e) {
				logger.info(e.getMessage());
			}
		}else {
			//秒杀未开启
			logger.warn("exposer={}",exportSeckillUrl);
		}
		
		
	}

//	@Test
//	public void testExecuteSeckill() {
//		try {
//			long seckillId = 1000L;
//			long userPhone = 15779236475L;
//			ExecutionSeckill executeSeckill = seckillService.executeSeckill(seckillId, userPhone, md5);
//			logger.info("executonSeckill={}", executeSeckill);
//		} catch (RepeatException e) {
//			logger.info(e.getMessage());
//		}
//	}

}

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
	 * name=1000Ԫ��ɱiPhoneXRS, 
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
	 * ������һ�������߼����ԣ����ӿڱ�¶��ִ����ɱ����һ��
	 * ע�⣬��ɱ�ǿ����Ĳſ���ִ���ֻ��ź�md5�Ļ�ȡ��Ҫ��δ�����Ͳ���ִ����ɱҵ����
	 */
	@Test
	public void SeckillLogic() {
		//1001ʱ���ǲ������,���Է��ص���ϵͳ�쳣
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
			//��ɱδ����
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

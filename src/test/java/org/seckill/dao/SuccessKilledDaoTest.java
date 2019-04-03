package org.seckill.dao;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {
	@Resource
	private SuccessKilledDao successKilledDao;
	@Test
	public void testInsertSuccessKilled() {
		long id = 1000L;
		long userPhone = 15779236476L;
		Date createTime = new Date();
		int insertCount = successKilledDao.insertSuccessKilled(id, userPhone,createTime);
		System.out.println(insertCount);
		
	}

	@Test
	public void testQueryByIdWithSeckill() {
		long id = 1000L;
		long userPhone = 15779236476L;
		SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id, userPhone);
		System.out.println(successKilled);
		System.out.println(successKilled.getSeckill());
	}
	/**
	 * SuccessKilled [seckillId=1000, 
	 * userPhone=15779236476, state=0, 
	 * createTime=Tue Apr 02 05:10:51 CST 2019, 
	 * seckill=
	 * Seckill [seckillId=1000, 
	 * name=1000ÔªÃëÉ±iPhoneXRS,
	 *  number=100, 
	 *  startTime=Tue Apr 02 08:00:00 CST 2019, endTime=Fri Apr 05 08:00:00 CST 2019, createTime=Tue Apr 02 03:04:17 CST 2019]]
Seckill [seckillId=1000, name=1000ÔªÃëÉ±iPhoneXRS, number=100, startTime=Tue Apr 02 08:00:00 CST 2019, endTime=Fri Apr 05 08:00:00 CST 2019, createTime=Tue Apr 02 03:04:17 CST 2019]
	 */

}

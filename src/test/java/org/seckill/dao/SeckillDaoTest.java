package org.seckill.dao;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {
	@Resource
	private SeckillDao seckillDao;
	
	
	@Test
	public void testReduceNumber() {
		long seckillId = 1000L;
		Date killTime = new Date();
		int reduceNumber = seckillDao.reduceNumber(seckillId, killTime);
		System.out.println(reduceNumber);
	}
	
	/*
	 * Seckill [seckillId=1000, 
	 * name=1000ÔªÃëÉ±iPhoneXRS,
	 *  number=100,
	 *   startTime=Tue Apr 02 08:00:00 CST 2019, 
	 *   endTime=Fri Apr 05 08:00:00 CST 2019,
	 *    createTime=Tue Apr 02 03:04:17 CST 2019]
	 */
	@Test
	public void testQueryById() {
		long id = 1000L;
		Seckill seckill = seckillDao.queryById(id);
		System.out.println(seckill);
		
		
	}

	@Test
	public void testQuerAll() {
		List<Seckill> querAll = seckillDao.queryAll(0, 100);
		for (Seckill seckill : querAll) {
			System.out.println(seckill);
		}
	}

}

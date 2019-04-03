package org.seckill.service.impl;

import java.util.Date;
import java.util.List;

import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.ExecutionSeckill;
import org.seckill.dto.Exposer;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillEnums;
import org.seckill.exception.RepeatException;
import org.seckill.exception.SeckillEndException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
@Service
public class SeckillServiceImpl implements SeckillService{
	private static final Logger logger = LoggerFactory.getLogger(SeckillServiceImpl.class);
	@Autowired
	private SeckillDao seckillDao;
	@Autowired
	private SuccessKilledDao successKilledDao;
	
	//定义一个混淆md5值的盐值
	private static final String slat = "aohfaoidhfiahfoweh97341*hifah&";
	@Override
	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
	}

	@Override
	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	@Override
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
	
	private String getMd5(long seckillId) {
		String base = seckillId + "/" + slat;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}
	
	
	
	@Override
	@Transactional
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

}

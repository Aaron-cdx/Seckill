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
	
	//����һ������md5ֵ����ֵ
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
		//���Ȼ���������
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
			//�����ж�md5ֵ�Ƿ���ͬ
			if(md5==null || !md5.equals(getMd5(seckillId))) {
				//ֱ�ӹ���Ϊ�ܵ��쳣
				throw new SeckillException("data rewrite");
			}
			//��ʼ��ɱ��ִ�м����+�����¼
			Date nowTime = new Date();
			//�����--��������ʧ�ܣ���Ҫ����һ��ʲô
			int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
			if(updateCount <= 0) {
				//û�и��µ����� ��ɱ����
				throw new SeckillEndException("seckill is closed");
			}else {
				//�����¼
				Date createTime = new Date();
				int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone,createTime);
				if(insertCount <=0) {
					//��������ʧ�ܣ��ظ���ɱ
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
			throw new SeckillEndException("ϵͳ�쳣��"+e.getMessage());
		}
	}

}

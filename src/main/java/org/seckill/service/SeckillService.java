package org.seckill.service;

import java.util.List;

import org.seckill.dto.ExecutionSeckill;
import org.seckill.dto.Exposer;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatException;
import org.seckill.exception.SeckillEndException;
import org.seckill.exception.SeckillException;

/**
 * վ��ʹ���ߵĽǶ���ƽӿ�
 * @author Duanxi Cao
 *
 */
public interface SeckillService {
	/**
	 * ��ȡ����ҳ
	 * @return
	 */
	public List<Seckill> getSeckillList();
	/**
	 * ����id���һ����Ʒ����Ϣ
	 * @param seckillId
	 * @return
	 */
	public Seckill getById(long seckillId);
	/**
	 * ��ɱ��ʼ����¶��ɱ�ӿڵĵ�ַ������ӿڵ�ַ
	 * ��ɱʧ�ܣ��򷵻�ϵͳ��ǰʱ�����ɱʱ��
	 * @param seckillId
	 */
	Exposer exportSeckillUrl(long seckillId);
	/**
	 * ִ����ɱ
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 */
	ExecutionSeckill executeSeckill(long seckillId, long userPhone, String md5)
		throws SeckillException,RepeatException,SeckillEndException;
	
	
}

package org.seckill.service;

import java.util.List;

import org.seckill.dto.ExecutionSeckill;
import org.seckill.dto.Exposer;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatException;
import org.seckill.exception.SeckillEndException;
import org.seckill.exception.SeckillException;

/**
 * 站在使用者的角度设计接口
 * @author Duanxi Cao
 *
 */
public interface SeckillService {
	/**
	 * 获取详情页
	 * @return
	 */
	public List<Seckill> getSeckillList();
	/**
	 * 根据id获得一个商品的信息
	 * @param seckillId
	 * @return
	 */
	public Seckill getById(long seckillId);
	/**
	 * 秒杀开始，暴露秒杀接口的地址，输出接口地址
	 * 秒杀失败，则返回系统当前时间和秒杀时间
	 * @param seckillId
	 */
	Exposer exportSeckillUrl(long seckillId);
	/**
	 * 执行秒杀
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 */
	ExecutionSeckill executeSeckill(long seckillId, long userPhone, String md5)
		throws SeckillException,RepeatException,SeckillEndException;
	
	
}

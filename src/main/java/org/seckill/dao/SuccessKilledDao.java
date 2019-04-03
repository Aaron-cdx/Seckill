package org.seckill.dao;


import java.util.Date;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

public interface SuccessKilledDao {
	
	/**
	 * 插入成功秒杀的信息 可以过滤重复 通过设计数据表的时候的联合主键
	 * @param seckillId
	 * @param userPhone
	 * @return
	 * @Param("createTime") Date createTime
	 */ 
	public int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone")long userPhone,@Param("createTime") Date createTime);
	/**
	 * 根据id查询成功秒杀的商品，并携带秒杀商品的信息
	 * @param seckillId
	 * @return
	 */
	public SuccessKilled queryByIdWithSeckill(@Param("seckillId")long seckillId,@Param("userPhone")long userPhone);
}

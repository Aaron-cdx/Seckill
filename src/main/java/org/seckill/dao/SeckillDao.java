package org.seckill.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

public interface SeckillDao {
	
	/**
	 * 	减库存
	 * @param seckillId
	 * @param killTime
	 * @return
	 */
	public int reduceNumber(@Param("seckillId")long seckillId,@Param("killTime") Date killTime);
	/**
	 *	 根据id查询秒杀商品对象
	 * @param seckillId
	 * @return
	 */
	public Seckill queryById(long seckillId);
	/**
	 * 根据偏移量查询所有秒杀商品
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<Seckill> queryAll(@Param("offset")int offset, @Param("limit")int limit);
	 
	
}

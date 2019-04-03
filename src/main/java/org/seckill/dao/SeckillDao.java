package org.seckill.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

public interface SeckillDao {
	
	/**
	 * 	�����
	 * @param seckillId
	 * @param killTime
	 * @return
	 */
	public int reduceNumber(@Param("seckillId")long seckillId,@Param("killTime") Date killTime);
	/**
	 *	 ����id��ѯ��ɱ��Ʒ����
	 * @param seckillId
	 * @return
	 */
	public Seckill queryById(long seckillId);
	/**
	 * ����ƫ������ѯ������ɱ��Ʒ
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<Seckill> queryAll(@Param("offset")int offset, @Param("limit")int limit);
	 
	
}

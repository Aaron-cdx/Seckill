package org.seckill.dao;


import java.util.Date;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

public interface SuccessKilledDao {
	
	/**
	 * ����ɹ���ɱ����Ϣ ���Թ����ظ� ͨ��������ݱ��ʱ�����������
	 * @param seckillId
	 * @param userPhone
	 * @return
	 * @Param("createTime") Date createTime
	 */ 
	public int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone")long userPhone,@Param("createTime") Date createTime);
	/**
	 * ����id��ѯ�ɹ���ɱ����Ʒ����Я����ɱ��Ʒ����Ϣ
	 * @param seckillId
	 * @return
	 */
	public SuccessKilled queryByIdWithSeckill(@Param("seckillId")long seckillId,@Param("userPhone")long userPhone);
}

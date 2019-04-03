package org.seckill.dto;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillEnums;

/**
 * ִ����ɱ
 * @author Duanxi Cao
 *
 */
public class ExecutionSeckill {
	private long seckillId;
	//��ɱ��״̬
	private int state;
	//��ɱ�����ʾ
	private String stateInfo;
	//�ɹ���ɱ�Ķ���
	private SuccessKilled successKilled;
	
	//�ɹ���ɱ����ȡ���е���Ϣ
	public ExecutionSeckill(long seckillId, SeckillEnums state, SuccessKilled successKilled) {
		super();
		this.seckillId = seckillId;
		this.state = state.getState();
		this.stateInfo = state.getStateInfo();
		this.successKilled = successKilled;
	}
	//ʧ����ɱ���õ�����
	public ExecutionSeckill(long seckillId,SeckillEnums state) {
		super();
		this.seckillId = seckillId;
		this.state = state.getState();
		this.stateInfo = state.getStateInfo();
	}

	public long getSeckillId() {
		return seckillId;
	}

	public void setSeckillId(long seckillId) {
		this.seckillId = seckillId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getStateInfo() {
		return stateInfo;
	}

	public void setStateInfo(String stateInfo) {
		this.stateInfo = stateInfo;
	}

	public SuccessKilled getSuccessKilled() {
		return successKilled;
	}

	public void setSuccessKilled(SuccessKilled successKilled) {
		this.successKilled = successKilled;
	}
	@Override
	public String toString() {
		return "ExecutionSeckill [seckillId=" + seckillId + ", state=" + state + ", stateInfo=" + stateInfo
				+ ", successKilled=" + successKilled + "]";
	}
	
}

package org.seckill.dto;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillEnums;

/**
 * 执行秒杀
 * @author Duanxi Cao
 *
 */
public class ExecutionSeckill {
	private long seckillId;
	//秒杀的状态
	private int state;
	//秒杀详情表示
	private String stateInfo;
	//成功秒杀的对象
	private SuccessKilled successKilled;
	
	//成功秒杀。获取所有的信息
	public ExecutionSeckill(long seckillId, SeckillEnums state, SuccessKilled successKilled) {
		super();
		this.seckillId = seckillId;
		this.state = state.getState();
		this.stateInfo = state.getStateInfo();
		this.successKilled = successKilled;
	}
	//失败秒杀，得到详情
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

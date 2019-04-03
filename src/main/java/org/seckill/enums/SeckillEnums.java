package org.seckill.enums;

public enum SeckillEnums {
	SUCCESS(1,"秒杀成功"),
	END(0,"秒杀关闭"),
	REPEAT_KILL(-1,"重复秒杀"),
	INNER_ERROR(-2,"系统异常"),
	DATA_REWRITE(-3,"数据篡改");
	
	private int state;
	private String stateInfo;
	
	private SeckillEnums(int state, String stateInfo) {
		this.state = state;
		this.stateInfo = stateInfo;
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
	//返回的是一个枚举类的封装对象
	public static SeckillEnums getEnum(int index) {
		for (SeckillEnums state: values()) {
			if(index == state.getState()) {
				return state;
			}
		}
		return null;
	}
	
	
	
	
	
}

package org.seckill.enums;

public enum SeckillEnums {
	SUCCESS(1,"��ɱ�ɹ�"),
	END(0,"��ɱ�ر�"),
	REPEAT_KILL(-1,"�ظ���ɱ"),
	INNER_ERROR(-2,"ϵͳ�쳣"),
	DATA_REWRITE(-3,"���ݴ۸�");
	
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
	//���ص���һ��ö����ķ�װ����
	public static SeckillEnums getEnum(int index) {
		for (SeckillEnums state: values()) {
			if(index == state.getState()) {
				return state;
			}
		}
		return null;
	}
	
	
	
	
	
}

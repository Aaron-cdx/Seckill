package org.seckill.dto;

import java.util.Date;
/**
 * 暴露接口地址
 * @author Duanxi Cao
 *
 */
public class Exposer {
	//是否开启秒杀
	private boolean exposed;
	//加密策略
	private String md5;
	
	private long seckillId;
	//当前时间(单位：毫秒)
	private long now;
	//秒杀开始时间
	private long start;
	//秒杀结束时间
	private long end;
	
	//如果没有开启，那就意味着没有对象，直接返回false+seckillId
	public Exposer(boolean exposed, long seckillId) {
		super();
		this.exposed = exposed;
		this.seckillId = seckillId;
	}
	//是否开启秒杀+md5+id
	public Exposer(boolean exposed, String md5, long seckillId) {
		super();
		this.exposed = exposed;
		this.md5 = md5;
		this.seckillId = seckillId;
	}
	//是否开启秒杀+now+start+end
	public Exposer(boolean exposed,long seckillId, long now, long start, long end) {
		super();
		this.exposed = exposed;
		this.seckillId = seckillId;
		this.now = now;
		this.start = start;
		this.end = end;
	}
	public boolean isExposed() {
		return exposed;
	}
	public void setExposed(boolean exposed) {
		this.exposed = exposed;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public long getSeckillId() {
		return seckillId;
	}
	public void setSeckillId(long seckillId) {
		this.seckillId = seckillId;
	}
	public long getNow() {
		return now;
	}
	public void setNow(long now) {
		this.now = now;
	}
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(long end) {
		this.end = end;
	}
	@Override
	public String toString() {
		return "Exposer [exposed=" + exposed + ", md5=" + md5 + ", seckillId=" + seckillId + ", now=" + now + ", start="
				+ start + ", end=" + end + "]";
	}

	
	
}

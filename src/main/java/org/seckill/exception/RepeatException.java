package org.seckill.exception;
/**
 * �ظ���ɱ
 * @author Duanxi Cao
 *
 */
public class RepeatException extends SeckillException{

	public RepeatException(String message, Throwable cause) {
		super(message, cause);
	}

	public RepeatException(String message) {
		super(message);
	}
	

}

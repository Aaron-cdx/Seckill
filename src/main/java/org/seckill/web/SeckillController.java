package org.seckill.web;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.seckill.dto.ExecutionSeckill;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillEnums;
import org.seckill.exception.RepeatException;
import org.seckill.exception.SeckillEndException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/seckill") //url风格：/模块/资源/{id}/细分
public class SeckillController {
	
	private static final Logger logger = LoggerFactory.getLogger(SeckillController.class);
	@Resource
	private SeckillService seckillService;
	
	
	/**
	 * 列表页
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public String list(Model model) {
		List<Seckill> list = seckillService.getSeckillList();
		
		model.addAttribute("list", list);
		
		return "list";
	}
	
	/**
	 * 详情页
	 * @param seckillId
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/{seckillId}/detail",method=RequestMethod.GET)
	public String detail(@PathVariable("seckillId") Long seckillId,Model model) {
		if(seckillId == null) {
			return "redirect:/seckill/list";
		}
		Seckill seckill = seckillService.getById(seckillId);
		if(seckill == null) {
			return "forward:/seckill/list";
		}
		model.addAttribute("seckill", seckill);
		return "detail";
	}
	
	/**
	 * 接口暴露 根据seckillId来暴露接口的一系列信息
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 */
	@RequestMapping(value="/{seckillId}/exposer",
			method = RequestMethod.POST,
			produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<Exposer> exposer(@PathVariable("seckillId")Long seckillId) {
		SeckillResult<Exposer> result;
		try {
			Exposer exposer = seckillService.exportSeckillUrl(seckillId);
			result =new SeckillResult<Exposer>(true,exposer);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new SeckillResult<Exposer>(false,e.getMessage());
		}
		return result;
	}
	
	@RequestMapping(value="/{seckillId}/{md5}/execution",
			method = RequestMethod.POST,
			produces= {"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<ExecutionSeckill> execute(@PathVariable("seckillId")Long seckillId,
			@PathVariable("md5") String md5,
			@CookieValue(value="killPhone", required=false)Long userPhone){
		SeckillResult<ExecutionSeckill> result;
		if(userPhone == null) {
			return new SeckillResult<ExecutionSeckill>(false,"手机号未注册");
		}
		try {
			ExecutionSeckill execute = seckillService.executeSeckill(seckillId, userPhone, md5);
			result = new SeckillResult<ExecutionSeckill>(true,execute);
		} catch(RepeatException e) {
			ExecutionSeckill execute = new ExecutionSeckill(seckillId,SeckillEnums.REPEAT_KILL);
			result = new SeckillResult<ExecutionSeckill>(true,execute);
		} catch(SeckillEndException e) {
			ExecutionSeckill execute = new ExecutionSeckill(seckillId,SeckillEnums.END);
			result = new SeckillResult<ExecutionSeckill>(true,execute);
		} catch (SeckillException e) {
			logger.error(e.getMessage());
			ExecutionSeckill execute = new ExecutionSeckill(seckillId,SeckillEnums.INNER_ERROR);
			result = new SeckillResult<ExecutionSeckill>(true,execute);
		}
		return result;
	}
	
	//获取系统当前时间!!!!!结果都统一封装到了这个结果类中
	@RequestMapping("/time/now")
	@ResponseBody
	public SeckillResult<Long> getTime() {
		Date now = new Date();
		return new SeckillResult<Long>(true,now.getTime());
	}
	
	
	
	
}

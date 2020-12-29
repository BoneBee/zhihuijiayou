package com.ncse.zhhygis.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ncse.zhhygis.collect.WebSocketServer;
import com.ncse.zhhygis.entity.ApiReturnObject;
import com.ncse.zhhygis.utils.projectUtils.ApiReturnUtil;

@Controller
@RequestMapping("checkcenter")
@CrossOrigin(allowCredentials = "true")
public class CheckCenterController {

	//页面请求
	@RequestMapping("/socket/{cid}")
	public ModelAndView socket(@PathVariable String cid) {
		ModelAndView mav=new ModelAndView("webTest");
		mav.addObject("cid", cid);
		return mav;
	}
	//推送数据接口
	@ResponseBody
	@RequestMapping("/socket/push/{cid}")
	public ApiReturnObject pushToWeb(@PathVariable String cid,String message) {  
		try {
			WebSocketServer.sendInfo(message,cid);
		} catch (IOException e) {
			e.printStackTrace();
			return ApiReturnUtil.error(cid+"#"+e.getMessage());
		}  
		return ApiReturnUtil.success(cid);
	} 
	
	@RequestMapping("/test")
	public String test() {
		return "webTest";
	}
	
} 

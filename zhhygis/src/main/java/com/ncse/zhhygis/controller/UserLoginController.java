package com.ncse.zhhygis.controller;

import com.ncse.zhhygis.entity.ParkBaseInfo;
import com.ncse.zhhygis.service.ParkBaseInfoService;
import com.zh.bean.login.MyStaff;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ncse.zhhygis.collect.UserLoginService;
import com.ncse.zhhygis.collect.WebSocketServer;
import com.ncse.zhhygis.utils.ServerResponse;
import com.ncse.zhhygis.utils.baseUtils.RedisUtils;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

/**
 * 登陆相关
 */
@RestController/**自动返回的是json格式数据***/
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true")
public class UserLoginController {
	
	 private final Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private UserLoginService userLoginService;

	@Autowired
	private ParkBaseInfoService parkBaseInfoService;
	
	@Resource
	private RedisUtils redisUtils;
	
	/**
	 * 获取登陆机场ID。待修改
	 * @param aircode
	 * @return
	 */
	@RequestMapping("/log")
	public ServerResponse log(String aircode) {
		userLoginService.setAirCode(aircode);
		return ServerResponse.createBySuccess();
	}

	/**
	 * 获取登陆机场ID。待修改
	 * @param aircode
	 * @return
	 */
	@RequestMapping("/getParkName")
	public ServerResponse getParkName(MyStaff staff,@RequestParam String aircode) {
		Map map = new HashMap();
		aircode = staff.getStaffAirportCode();
		ParkBaseInfo parkBaseInfo = parkBaseInfoService.selectByAircode(aircode);
		map.put("data",parkBaseInfo);
		map.put("code","0");
		try {
			String keys = redisUtils.get(aircode+"carStatusKey");
			if(!StringUtils.isEmpty(keys)) {
				String[] keyss=keys.split(",");
				for(String key:keyss) {
					log.info("推送gps缓存数据到前端"+key+":"+redisUtils.get(aircode+"carStatus"+key));
					Thread.sleep(500);
					WebSocketServer.sendInfo(redisUtils.get(aircode+"carStatus"+key) ,  aircode+ "carGis");
				}
			}
			return ServerResponse.createBySuccess(map);
		}catch (Exception e){
			return ServerResponse.createBySuccess(map);
		}
	}
}

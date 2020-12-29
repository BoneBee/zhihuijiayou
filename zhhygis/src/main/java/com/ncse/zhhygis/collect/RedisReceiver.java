package com.ncse.zhhygis.collect;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ncse.zhhygis.service.CarAirInterfaceService;
import com.zh.bean.login.MyStaff;

@Service
public class RedisReceiver {

	private final Logger log = Logger.getLogger(this.getClass());

	@Autowired
	RedisReceiver redisService;

	@Autowired
	private UserLoginService userLoginService;

	@Autowired
	private CarAirInterfaceService carAirInterfaceService;

	@Value("${spring.redis2.type}")
	private String type;

	@Value("${spring.redis2.oiltype}")
	private String oiltype;

	public void receiveMessage(String message) {
		log.info("收到的订阅消息（（redis））:"+message);
		String token = "";
		JSONObject jso=JSON.parseObject(message),jsweb,jscontent;
		String msgtype=jso.getString("type");
		//获取所有登陆用户
		Map<String,MyStaff> users ;
		Iterator<Map.Entry<String, MyStaff>> entries;
		Map map;
		String msg;
		Entry<String, MyStaff> entry;

		MyStaff mystaff;
		jscontent=jso.getJSONObject("content");
		if(oiltype.equals(msgtype)) { //油料
			//油料的话重新调接口，进行推送
			users = userLoginService.getUsers();
			entries = users.entrySet().iterator();
			try {
				//循环推送每个已经登陆的人
				while (entries.hasNext()) {
					entry = entries.next();
					String oil=carAirInterfaceService.tjOilFuel(entry.getValue(),token);
					//判断机场和用户属于同一个机场


					log.info("推送de油料信息（redis）："+oil);
					WebSocketServer.sendInfo(oil,entry.getKey()+"oil" );
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//jsweb=jscontent.getJSONObject("");
		}else { //否则判断任务及航线信息
			String[] types=type.split(",");
			//int index = Arrays.binarySearch(types,msgtype);
    		/*if(index<0) {
    			return;
    		}*/
			boolean flag = Arrays.asList(types).contains(msgtype);
			if(!flag) {
				return;
			}

			//航班相关信息
			jsweb=jscontent.getJSONObject("selfFlight");

			if(jsweb==null) {
				jsweb=jscontent.getJSONObject("flight");
			}
			if(jsweb==null) {
				return;
			}

			//判断起飞时间，因为有可能存在未来数据，即排班数据他们已经传递过来，但是还未到达或起飞,这种数据不进行处理。
			String flgtAAtot=jsweb.getString("flgtAAtot");
			if(flgtAAtot==null) {
				return;
			}

			String flgtAirportCode = jsweb.getString("flgtAirportCode");//机场ID

			//拼接返回数据
			Map flightMap=new HashMap();
			flightMap.put("aircraftID",jsweb.getString("flgtId"));
//    		flightMap.put("aircraftNum",jsweb.getString("flgtFfid"));
			flightMap.put("aircraftNum",jsweb.getString("flgtRegn"));
			flightMap.put("seatID",jsweb.getString("flgtPlacecode"));

			String flgtAdid=jsweb.getString("flgtAdid");
			if("A".equals(flgtAdid)) {
				flightMap.put("landFlightN",jsweb.getString("flgtFlno"));
			}else {
				flightMap.put("takeOffFlightN",jsweb.getString("flgtFlno"));
			}
			flightMap.put("farNear",jsweb.getString("flgtFnflag"));
			flightMap.put("model",jsweb.getString("flgtAcname"));
			flightMap.put("vip",jsweb.getString(""));
			flightMap.put("flightState",jsweb.getString("flgtFtyp"));
			flightMap.put("companyName",jsweb.getString("flgtAlcname"));
			flightMap.put("airPlaneType",jsweb.getString(""));
			flightMap.put("taskStatus",jsweb.getString("taskStatus"));
			flightMap.put("isShow", StringUtils.isEmpty(jsweb.getString("flgtDAtot"))?"true":"false");

			users = userLoginService.getUsers();
			entries = users.entrySet().iterator();
			String aircode;
			try {
				//循环推送给登陆的人
				while (entries.hasNext()) {
					entry = entries.next();
					//判断机场和用户属于同一个机场
					if(entry.getValue().getStaffAirportCode().equals(flgtAirportCode)) {
						log.info("推送de航线信息（redis）："+entry+JSON.toJSONString(flightMap));
						WebSocketServer.sendInfo(JSON.toJSONString(flightMap),entry.getKey()+"fli" );
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
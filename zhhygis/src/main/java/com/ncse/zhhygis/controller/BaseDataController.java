package com.ncse.zhhygis.controller;


import java.io.IOException;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ncse.zhhygis.utils.baseUtils.HttpURL;
import com.ncse.zhhygis.utils.baseUtils.TokenUtil;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ncse.zhhygis.collect.UserLoginService;
import com.ncse.zhhygis.collect.WebSocketServer;
import com.ncse.zhhygis.entity.OilEntity;
import com.ncse.zhhygis.service.CarAirInterfaceService;
import com.ncse.zhhygis.utils.ServerResponse;
import com.ncse.zhhygis.utils.baseUtils.MessageUtils;
import com.ncse.zhhygis.utils.baseUtils.RedisUtils;
import com.zh.bean.login.MyStaff;


/**
 * 基础数据类，获取飞机，汽车，机位，等基本信息，由客户端主动发起请求
 */
@RestController/**自动返回的是json格式数据***/
@RequestMapping("baseData")
@CrossOrigin(allowCredentials = "true")
public class BaseDataController {

	@Resource
	private RedisUtils redisUtils;

	@Autowired
	private CarAirInterfaceService carAirInterfaceService;

	@Autowired
	private UserLoginService userLoginService;

	private final Logger log = Logger.getLogger(this.getClass());


	/**
	 * 获取所有飞机基本信息，初始化时使用(登陆时候保存用户信息)
	 * @param mystaff aircode  机场ID
	 * @return
	 */
	@RequestMapping("/airAllInfos")
	public ServerResponse airAllInfos(MyStaff mystaff, HttpServletRequest request){

		String aircode=mystaff.getStaffAirportCode();
		String token = request.getHeader("token");
		synchronized (TokenUtil.class){TokenUtil.getMap().put("token",token);}

		if(aircode==null || aircode.equals("")) {
			return ServerResponse.createBySuccessMessage(MessageUtils.AIR_CODE_NOTNULLJCDM);
		}
		log.info("aircode参数为："+aircode);

		JSONObject job;
		try {
			String datas = carAirInterfaceService.airGetAllInfos(aircode,token);
			job= JSON.parseObject(datas);
			//新增根据flgtAAtot 字段排序
			/*JSONArray jsonArray = job.getJSONArray("data");
			JSONArray arraySort = jsonArraySort(jsonArray, "flgtAAtot");
			job.put("data", arraySort);
			log.info("排序过后的数据结构为"+job);*/
		} catch (IOException e) {
			job=new JSONObject();
			job.put("error", e.getMessage());
			// TODO Auto-generated catch block
			log.error(e);
		}
		return ServerResponse.createBySuccess(job);
	}

	public static void main(String[] args) {
		JSONObject job = JSON.parseObject("");
		JSONArray data = (JSONArray) job.get("data");
		String s =JSONObject.toJSONString(data, SerializerFeature.WriteMapNullValue);
		System.out.println(s);
	}
	/**
	 * 获取所有汽车信息
	 * @param mystaff aircode  机场ID
	 * @return
	 */
	@RequestMapping("/carAllInfos")
	public ServerResponse carAllInfos(MyStaff mystaff,HttpServletRequest request){
		String token = request.getHeader("token");
		userLoginService.setUsers(mystaff);
		String aircode=mystaff.getStaffAirportCode();
		if(aircode==null || aircode.equals("")) {
			return ServerResponse.createBySuccessMessage(MessageUtils.AIR_CODE_NOTNULLJCDM);
		}
		log.info("aircode参数为："+aircode);
		JSONObject job;
		try {
			String datas = carAirInterfaceService.carGetAllInfos(aircode,token);
			job= JSON.parseObject(datas);
			log.info("carGetAllInfos接口数据为："+datas);


		} catch (IOException e) {
			job=new JSONObject();
			job.put("error", e.getMessage());

			// TODO Auto-generated catch block
			log.error(e);
		}
		return ServerResponse.createBySuccess(job);
	}

	/**
	 * 获取所有汽车信息(后台)
	 * @param  mystaff  机场ID
	 * @return
	 */
	@RequestMapping("/carAllInfosSys")
	public ServerResponse carAllInfosSys(MyStaff mystaff,HttpServletRequest request){
		String token = request.getHeader("token");
		userLoginService.setUsers(mystaff);
//    	String aircode="2901";
		String aircode=mystaff.getStaffAirportCode();
		if(aircode==null || aircode.equals("")) {
			return ServerResponse.createBySuccessMessage(MessageUtils.AIR_CODE_NOTNULLJCDM);
		}
		log.info("aircode参数为："+aircode);
		JSONObject job;
		JSONArray dataArrays = new JSONArray();
		try {
			String datas = carAirInterfaceService.carGetAllInfos(aircode,token);
			job= JSON.parseObject(datas);
//			JSONObject data = job.getJSONObject("data");
			JSONArray data = job.getJSONArray("data");
			for (Object datum : data) {
				JSONObject object = JSON.parseObject(String.valueOf(datum));
				String carNum = object.getString("carNum");
				String key = "cargps:" + aircode + ":" + carNum;
				String resoult = redisUtils.get(key);
				if (resoult!=null) {
					JSONObject resoultJs = JSONObject.parseObject(resoult);
					String timeStamp = resoultJs.getString("timeStamp");
					String addTime = resoultJs.getString("addTime");
					object.put("timeStamp", timeStamp);
					object.put("addTime", addTime);
				}else{
					object.put("timeStamp", "");
					object.put("addTime", "");
				}
				dataArrays.add(object);
			}
			log.info("carGetAllInfos接口数据为："+datas);
		} catch (IOException e) {
			job=new JSONObject();
			job.put("error", e.getMessage());

			// TODO Auto-generated catch block
			log.error(e);
		}
		//新增根据timeStamp 升序排列
		JSONArray jsonArray = jsonArraySort(dataArrays,"timeStamp");
		return ServerResponse.createBySuccess(jsonArray);
	}


	/**
	 * @Author: zhangweixia
	 * @Description:   按照JSONArray中的对象的某个字段进行排序(采用fastJson)
	 * @Date：Created in 17:57 2019/11/21
	 * @Modified：
	 */
	    protected   JSONArray jsonArraySort(JSONArray jsonArr,String sortKey) {
		//JSONArray jsonArr = JSON.parseArray(jsonArrStr);
		JSONArray sortedJsonArray = new JSONArray();
		List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		for (int i = 0; i < jsonArr.size(); i++) {
			jsonValues.add(jsonArr.getJSONObject(i));
		}
		Collections.sort(jsonValues, new Comparator<JSONObject>() {
			// You can change "KEY_NAME" with "sortKey" if you want to sort by sortKey
			private  final String KEY_NAME = sortKey;

			@Override
			public int compare(JSONObject a, JSONObject b) {
				String valA = new String();
				String valB = new String();
				try {
					String aStr = a.getString(KEY_NAME);
					valA = aStr.replaceAll("-", "");
					String bStr = b.getString(KEY_NAME);
					valB = bStr.replaceAll("-", "");
				} catch (JSONException e) {
					// do something
				}
				return -valB.compareTo(valA);
				// if you want to change the sort order, simply use the following:
				// return -valA.compareTo(valB);
			}
		});
		for (int i = 0; i < jsonArr.size(); i++) {
			sortedJsonArray.add(jsonValues.get(i));
		}
		return sortedJsonArray;
	}

	/**
	 * 获取机位信息
	 * @param oilEntity aircode  机场ID
	 * @param oilEntity placecode  机位ID
	 * @return
	 */
	@RequestMapping("/airPortInfo")
	public ServerResponse airPortInfo(OilEntity oilEntity,HttpServletRequest request){
		String token = request.getHeader("token");
		String aircode=oilEntity.getStaffAirportCode();
		if(aircode==null || aircode.equals("")) {
			return ServerResponse.createBySuccessMessage(MessageUtils.AIR_CODE_NOTNULLJCDM);
		}
		String placecode=oilEntity.getPlacecode();
		if(placecode==null || placecode.equals("")) {
			return  ServerResponse.createBySuccessMessage(MessageUtils.AIR_CODE_NOTNULLJWDM);
		}
		log.info("aircode参数为："+aircode);
		JSONObject job;

		try {
			String datas = carAirInterfaceService.airportInfo(aircode,placecode,token);
			job= JSON.parseObject(datas);
			log.info("airportInfo接口数据为："+datas);
		} catch (IOException e) {
			job=new JSONObject();
			job.put("error", e.getMessage());
			// TODO Auto-generated catch block
			log.error(e);
		}
		return ServerResponse.createBySuccess(job);
	}

	/**
	 * 点击某个飞机时展示信息。包括当前航班，下一步转机航班信息
	 * @param oilEntity staff aircode  机场ID
	 * @param oilEntity flightId  飞机ID
	 * @return
	 */
	@RequestMapping("/airFlightdetails")
	public ServerResponse airFlightdetails(OilEntity oilEntity,@RequestParam Map parmsMap,HttpServletRequest request){
		String token = request.getHeader("token");
		String aircode=oilEntity.getStaffAirportCode();
		if(aircode==null || aircode.equals("")) {
			return ServerResponse.createBySuccessMessage(MessageUtils.AIR_CODE_NOTNULLJCDM);
		}
		String flightIdA=oilEntity.getFlightIdA();
		String flightIdD=oilEntity.getFlightIdD();
		log.info("aircode参数为（ 点击某个飞机时展示信息。包括当前航班，下一步转机航班信息）："+aircode);
		JSONObject job;

		try {
			String aircraftID = String.valueOf(parmsMap.get("aircraftID"));
			String datas = carAirInterfaceService.airFlightdetails(aircode,aircraftID,flightIdA,flightIdD,token);
			job= JSON.parseObject(datas);
			log.info("airportInfo接口数据为："+datas);
		} catch (IOException e) {
			job=new JSONObject();
			job.put("error", e.getMessage());
			// TODO Auto-generated catch block
			log.error(e);
		}
		return ServerResponse.createBySuccess(job);
	}

	/**
	 * 油料参数
	 * @param oilEntity staff :aircode
	 * @return
	 */
	@RequestMapping("/tjOilFuel")
	public ServerResponse tjOilFuel(OilEntity oilEntity,HttpServletRequest request){
		String token = request.getHeader("token");
		String aircode=oilEntity.getStaffAirportCode();
		if(aircode==null || aircode.equals("")) {
			return ServerResponse.createBySuccessMessage(MessageUtils.AIR_CODE_NOTNULLJCDM);
		}
		log.info("aircode参数为："+aircode);
		JSONObject job;

		try {
			MyStaff loginUserIn = oilEntity.getLoginUserIn();
			String datas = carAirInterfaceService.tjOilFuel(oilEntity,token);
			job= JSON.parseObject(datas);
			log.info("油料统计接口数据为："+datas);
		} catch (IOException e) {
			job=new JSONObject();
			job.put("error", e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServerResponse.createBySuccess(job);
	}

	/**
	 * 退出
	 * @param staff :aircode
	 * @return
	 */
	@RequestMapping("/signgOut")
	public ServerResponse signgOut(MyStaff staff,@RequestParam Map  map,HttpServletRequest request){
		String token = request.getHeader("token");
		JSONObject job;
		try {
			String datas = carAirInterfaceService.signgOut(map,token);
			job= JSON.parseObject(datas);
			log.info("退出结果："+datas);
		} catch (IOException e) {
			job=new JSONObject();
			job.put("error", e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServerResponse.createBySuccess(job);
	}

	/**
	 * 推送缓存的GPS信息
	 * @param mystaff
	 * @return
	 */
	@RequestMapping("/carStatusSend")
	public ServerResponse carStatusSend(MyStaff mystaff,HttpServletRequest request) {
		String token = request.getHeader("token");
		String aircode=mystaff.getStaffAirportCode();
		if(aircode==null || aircode.equals("")) {
			return ServerResponse.createBySuccessMessage(MessageUtils.AIR_CODE_NOTNULLJCDM);
		}
		/******************获取redis的汽车数据推送到前端********************/
		String keys = redisUtils.get(aircode+"carStatusKey");
		String[] keyss=keys.split(",");
		JSONObject job=new JSONObject();
		for(String key:keyss) {
			try {
				WebSocketServer.sendInfo(redisUtils.get(aircode+"carStatus"+key) ,  aircode+ "carGis");

				job.put("success", "推送成功");
			} catch (IOException e) {

				job.put("error", e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/******************************************/
		return ServerResponse.createBySuccess(job);
	}
}
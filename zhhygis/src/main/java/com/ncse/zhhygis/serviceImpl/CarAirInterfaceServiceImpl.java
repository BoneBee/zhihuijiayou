package com.ncse.zhhygis.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ncse.zhhygis.entity.OilEntity;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.ncse.zhhygis.collect.UserLoginService;
import com.ncse.zhhygis.service.CarAirInterfaceService;
import com.ncse.zhhygis.utils.baseUtils.HttpURL;
import com.ncse.zhhygis.utils.baseUtils.MessageUtils;
import com.zh.bean.login.MyStaff;

@Service
public class CarAirInterfaceServiceImpl implements CarAirInterfaceService {

	private final Logger log = Logger.getLogger(this.getClass());

	@Value("${aircarinterfaceurl80}")
	private String interUrl80;
	@Value("${aircarinterfaceurl81}")
	private String interUrl81;
	@Value("${aircarinterfaceurl82}")
	private String interUrl82;
	@Value("${aircarinterfaceurl83}")
	private String interUrl83;

	@Autowired
	private UserLoginService userLoginService;

	@Override
	public String airGetAllInfos(String aircode,String token) throws IOException {
		String url = interUrl80 + MessageUtils.INTERFACE_AIRGETALLINFOS_81;
		Map map = new HashMap();
		map.put("flgtAirportCode", aircode);
		String result = HttpURL.getContent(url, map,token);
		return result;
	}

	@Override
	public String carGetAllInfos(String aircode,String token) throws IOException {
		String url = interUrl80 + MessageUtils.INTERFACE_CARGETALLINFOS_80;
		Map map = new HashMap();
		map.put("flgtAirportCode", aircode);
		String result = HttpURL.getContent(url, map,token);
		//汽车基本 信息传入缓存
		userLoginService.updatecarbaseinfo(aircode,result);
		return result;
	}

	@Override
	public String carAllInfosSys(String aircode,String token) throws IOException {
		String url = interUrl80 + MessageUtils.INTERFACE_CARGETALLINFOS_80;
		Map map = new HashMap();
		map.put("flgtAirportCode", aircode);
		String result = HttpURL.getContent(url, map,token);
		//汽车基本 信息传入缓存
		userLoginService.updatecarbaseinfo(aircode,result);
		return result;
	}

	@Override
	public String airportInfo(String aircode, String placecode,String token) throws IOException {
		String url = interUrl80 + MessageUtils.INTERFACE_AIRPORTINFO_81;
		Map map = new HashMap();
		map.put("flgtAirportCode", aircode);
		map.put("flgtPlacecode", placecode);
		String result = HttpURL.getContent(url, map,token);
		return result;
	}

	@Override
	public String airFlightdetails(String aircode,String aircraftID, String na,String nd,String token) throws IOException {
		String url = interUrl80 + MessageUtils.INTERFACE_AIRFLIGHTDETAILS_81;
		Map map = new HashMap();
		map.put("flgtAirportCode", aircode);
		map.put("aircraftID", aircraftID);
		if (!"".equals(na)&&na!=null){
			map.put("flightNA", na);
		}
		if (!"".equals(nd)&&nd!=null) {
			map.put("flightND", nd);
		}
		log.info("http报无管理员权限的入参是："+map);
		String result = HttpURL.getContent(url, map,token);
		return result;
	}

	@Override
	public String tjAirCount(MyStaff mystaff,String token) throws IOException {
		String url = interUrl80 + MessageUtils.INTERFACE_TJ_AIRCOUNT_81;
		Map map = new HashMap();
		Map pmap = new HashMap();

		pmap.put("staffId", mystaff.getStaffId());
		pmap.put("staffAirportCode", mystaff.getStaffAirportCode());
		pmap.put("staffAptareaCode", mystaff.getStaffAptareaCode());

		//map.put("loginUserIn", pmap);
		String result = HttpURL.getContent(url, pmap,token);
		return result;
	}

	@Override
	public String tjTaskamCount(MyStaff mystaff,String token) throws IOException {
		String url = interUrl80 + MessageUtils.INTERFACE_TJ_TASKAMCOUNT_81;
		Map map = new HashMap();
		Map pmap = new HashMap();

		pmap.put("staffId", mystaff.getStaffId());
		pmap.put("staffAirportCode", mystaff.getStaffAirportCode());
		pmap.put("staffAptareaCode", mystaff.getStaffAptareaCode());

		//map.put("loginUserIn", pmap);
		String result = HttpURL.getContent(url, pmap,token);
		return result;
	}

	@Override
	public String tjSoulCount(MyStaff mystaff,String token) throws IOException {
		String url = interUrl80 + MessageUtils.INTERFACE_TJ_SOULCOUNT_81;
		Map map = new HashMap();
		Map pmap = new HashMap();

		pmap.put("staffId", mystaff.getStaffId());
		pmap.put("staffAirportCode", mystaff.getStaffAirportCode());
		pmap.put("staffAptareaCode", mystaff.getStaffAptareaCode());

		//map.put("loginUserIn", pmap);
		String result = HttpURL.getContent(url, pmap,token);
		return result;
	}

	@Override
	public String tjAlOil(MyStaff mystaff,String token) throws IOException {
		String url = interUrl80 + MessageUtils.INTERFACE_TJ_ALOIL_81;
		Map map = new HashMap();
		Map pmap = new HashMap();

		pmap.put("staffId", mystaff.getStaffId());
		pmap.put("staffAirportCode", mystaff.getStaffAirportCode());
		pmap.put("staffAptareaCode", mystaff.getStaffAptareaCode());

		//map.put("loginUserIn", pmap);
		String result = HttpURL.getContent(url, pmap,token);
		return result;
	}

	@Override
	public String tjVehiSum(MyStaff mystaff,String token) throws IOException {
		String url = interUrl80 + MessageUtils.INTERFACE_TJ_VEHISUM_80;
		Map map = new HashMap();
		Map pmap = new HashMap();

		pmap.put("staffId", mystaff.getStaffId());
		pmap.put("staffAirportCode", mystaff.getStaffAirportCode());
		pmap.put("staffAptareaCode", mystaff.getStaffAptareaCode());

		//map.put("loginUserIn", pmap);
		String result = HttpURL.getContent(url, pmap,token);
		return result;
	}

	@Override
	public String tjOilFuel(MyStaff mystaff,String token) throws IOException {
		String url = interUrl80 + MessageUtils.INTERFACE_TJ_OILFUEL_80;
		Map map = new HashMap();
		Map pmap = new HashMap();

//		pmap.put("staffId", oilEntity.getStaffId());
		pmap.put("staffAirportCode", mystaff.getStaffAirportCode());
		pmap.put("staffAptareaCode", mystaff.getStaffAptareaCode());

		map.put("loginUserIn", pmap);
		String result = HttpURL.getContent(url, map,token);
		return result;
	}

	@Override
	public String signgOut(Map map,String token) throws IOException {
		String url = interUrl80 + MessageUtils.INTERFACE_TJ_SIGNOUT_80;
		String result = HttpURL.getContent(url, map,token);
		return result;
	}

	@Override
	public String ryxq(String staffId) throws IOException {
		String url = interUrl82+MessageUtils.INTERFACE_RYXQ_80;
		Map map = new HashMap();
		map.put("staffId", staffId);
		String result = null;
		try {
			result = HttpURL.getRenyuan(url,map);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("获取人员接口失败"+e);
		}
		return result;
	}

	@Override
	public String rwlb(String staffId) throws IOException {
		String url = interUrl83+MessageUtils.INTERFACE_RWLB_81;
		Map map = new HashMap();
		MyStaff my=new MyStaff();
		my.setStaffId(staffId);
		map.put("loginUserIn", my);
		String result = null;
		try {
			result = HttpURL.getRenyuan(url,map);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("获取任务接口失败"+e);
		}
		// TODO Auto-generated method stub
		return result;
	}

}

package com.ncse.zhhygis.collect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.ncse.zhhygis.utils.baseUtils.JSONUtil;
import com.zh.bean.login.MyStaff;

/**
 * 登陆信息存储
 * 
 * @author Administrator
 *
 */
@Service
public class UserLoginService {
	private final Logger log = Logger.getLogger(this.getClass());

	// 汽车基本信息，放入缓存，随取随用
	/**
	 * 存储格式：key:机场ID,value:<车牌号，车辆信息>
	 */
	private Map<String, Map<String, Map>> carbaseinfo = new HashMap();
	
	private Map<String,MyStaff> users=new HashMap();

	private List<String> airCodes = new ArrayList();
	
	
	public void setUsers(MyStaff staff) {
		users.put(staff.getStaffId(), staff);
	}
	
	public void removeUsers(String staffId) {
		users.remove(staffId);
	}
	
	public Map<String,MyStaff> getUsers() {
		return users;
	}

	public List<String> getAirCodes() {
		return airCodes;
	}

	public void setAirCodes(List<String> airCodes) {
		this.airCodes = airCodes;
	}
	
	public void removeAircode(String aircode) {
		if(airCodes.contains(aircode)) {
			airCodes.remove(aircode);
		}
	}

	/**
	 * 添加机场
	 * 
	 * @param airCode
	 *            机场ID
	 */
	public void setAirCode(String airCode) {
		if (!airCodes.contains(airCode)) {
			this.airCodes.add(airCode);
		}
	}

	// 将汽车基本信息，存入缓存

	public void updatecarbaseinfo(String aircode, String carinfo) {
		Map<String, Map> map = carbaseinfo.get(aircode);
		if (map == null) {// 如果没有该机场的数据，则添加进去
			map = new HashMap();
		}
		Object json = JSON.parse(carinfo);
		if (json instanceof JSONObject) { // 如果是单条，先查询是否存在，如果存在，进行修改，不存在，新增
			JSONObject jsonObject = (JSONObject) json;
			Map carinfoMap = JSONUtil.reflect(jsonObject);
			map.put((String) carinfoMap.get("carNum"), carinfoMap);

		} else if (json instanceof JSONArray) {
			JSONArray jsonArray = (JSONArray) json;
			List<Object> carinfoList = JSONUtil.reflect(jsonArray);
			Map carinfoMap;
			for (Object o : carinfoList) {
				carinfoMap = (Map) o;
				map.put((String) carinfoMap.get("carNum"), carinfoMap);
			}
		}
		carbaseinfo.put(aircode, map);

	}

	/**
	 * 获取汽车基本信息
	 * 
	 * @param aircode
	 * @return
	 */

	public Map<String, Map> getCarBaseInfo(String aircode) {
		return carbaseinfo.get(aircode);
	}
}

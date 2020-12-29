package com.ncse.zhhygis.controller;

import com.ncse.zhhygis.service.MapManagerService;
import com.ncse.zhhygis.service.RoutePlanService;
import com.ncse.zhhygis.utils.ServerResponse;
import com.ncse.zhhygis.utils.baseUtils.ClassUtil;
import com.zh.bean.login.MyStaff;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 画图相关
 */
@RestController/**自动返回的是json格式数据***/
@RequestMapping("draw")
@CrossOrigin(allowCredentials = "true")
public class MapManagerController {

	private final Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private MapManagerService mapManagerService;
	
	@Autowired
	private RoutePlanService routePlanService;

	/**
	 * 新增区域面
	 * @param map 前台获取面参数
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/geoRegionAdd")
	public ServerResponse geoRegionAdd(MyStaff staff,@RequestParam Map  map,HttpServletRequest request){
		try {
			ClassUtil.MygetFiledName(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		map.put("aircode", staff.getStaffAirportCode());
		mapManagerService.geoRegionAdd(map);
		return ServerResponse.myCreateBySuccess();
	}
	
	/**
	 * 修改区域面
	 * @param map
	 * @return
	 */
	@RequestMapping("/updateRegion")
	public ServerResponse updateRegion(MyStaff staff,@RequestParam Map map) {
		try {
			ClassUtil.MygetFiledName(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//map.put("aircode", staff.getStaffAirportCode());
		mapManagerService.updateRegion(map);
		return ServerResponse.myCreateBySuccess();
	}
	
	/**
	 * 获取导航及测距信息
	 * @param map
	 * @return
	 */
	@RequestMapping("/getDistanct")
	public ServerResponse getDistanct(MyStaff staff,@RequestParam Map  map) {
		try {
			ClassUtil.MygetFiledName(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.put("aircode", staff.getStaffAirportCode());
		Map m=routePlanService.findPath(map);
		return ServerResponse.createBySuccess(m);
	}
	
	/**
	 * 获取机位面信息
	 * @param staff aircode
	 * @return
	 */
	@RequestMapping("/getAParkRegion")
	public ServerResponse getAParkGeoRegion(MyStaff staff) {
		String aircode=staff.getStaffAirportCode();
		log.info("#getAParkGeoRegion#" + aircode);
		Map	m = mapManagerService.getAParkGeoRegion(aircode);
		return ServerResponse.createBySuccess(m);
	}
	/**
	 * @Author: zhangweixia
	 * @Description:   获取油管线json数据,开关控制是否显示油管线
	 * @Date：Created in 16:42 2019/12/17
	 * @Modified：
	 */
	@RequestMapping("/getOilWellLine")
	public ServerResponse getOilWellLine(MyStaff staff){
		String aircode = staff.getStaffAirportCode();
		Map lineMap = null;
		try {
			lineMap = mapManagerService.getOilWellLine(aircode);
		} catch (Exception e) {
			e.printStackTrace();
			return ServerResponse.createByErrorMessage("获取油管线数据报错:"+e.getMessage());
		}
		return ServerResponse.createBySuccess(lineMap);
	}

	/**
	 * 获取油井列表信息
	 * @param staff aircode
	 * @return
	 */
	@RequestMapping("/getOilWell")
	public ServerResponse getOilRegion(MyStaff staff) {
		String aircode=staff.getStaffAirportCode();
		log.info("#getOilRegion#" + aircode);
		Map m = mapManagerService.getOilRegion(aircode);
		return ServerResponse.createBySuccess(m);
	}
	
	/**
	 * 获取油井列表信息
	 * @param aircode
	 * @return
	 */
	@RequestMapping("/getOilWell1")
	public ServerResponse getOilRegion1(String aircode) {
		//String aircode=staff.getStaffAirportCode();
		Map m=null;
		try {
			m = mapManagerService.getOilRegion(aircode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServerResponse.createBySuccess(m);
	}
	
	/**
	 * 获取机位面信息
	 * @param aircode
	 * @return
	 */
	@RequestMapping("/getAParkRegion1")
	public ServerResponse getAParkGeoRegion1(String aircode) {
		//String aircode=staff.getStaffAirportCode();
		Map m=null;
		String aa="";
		try {
			m = mapManagerService.getAParkGeoRegion(aircode);
		} catch (Exception e) {
			System.out.println("11111"+e.getMessage());
			aa=e.getMessage();
			e.printStackTrace();
		}
		return ServerResponse.createBySuccess(m);
	}
	
	@RequestMapping("/getAParkRegion2")
	public ServerResponse getAParkGeoRegion2(String aircode) {
		//String aircode=staff.getStaffAirportCode();
		Map m=mapManagerService.getAParkGeoRegion2(aircode);
		return ServerResponse.createBySuccess(m);
	}
	/**
	 * 获取自定义区域面信息
	 * @param staff aircode
	 * @return
	 */
	@RequestMapping("/getCustomGeoRegion")
	public ServerResponse getCustomGeoRegion(MyStaff staff) {
		String aircode=staff.getStaffAirportCode();
		Map m=mapManagerService.getCustomGeoRegion(aircode);
		return ServerResponse.createBySuccess(m);
	}

	/**
	 * 获取自定义区域面基本属性信息
	 * @param staff aircode
	 * @return
	 */
	@RequestMapping("/getCustomGeoAttr")
	public ServerResponse getCustomGeoAttr(MyStaff staff) {
		String aircode=staff.getStaffAirportCode();
		Map m=mapManagerService.getCustomGeoAttr(aircode);
		return ServerResponse.createBySuccess(m);
	}

	/**
	 * 根据id获取自定义区域面基本信息
	 * @param map
	 * @return
	 */
	@RequestMapping("/getCustomGeoRegionById")
	public ServerResponse getCustomGeoRegionById(MyStaff staff,@RequestParam Map  map) {
		try {
			ClassUtil.MygetFiledName(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//String aircode=(String)map.get("airCode");
		map.put("aircode", staff.getStaffAirportCode());
		Map m=mapManagerService.getCustomGeoRegionById(map);
		return ServerResponse.createBySuccess(m);
	}

	/**
	 * 删除区域面
	 * @param map regid 面id，aircode 机场编号，regionType 1管理区域 2非管理区域
	 * @return
	 */
	@RequestMapping("/deleteRegion")
	public ServerResponse deleteRegion(MyStaff staff,@RequestParam Map map) {
		try {
			ClassUtil.MygetFiledName(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.put("aircode", staff.getStaffAirportCode());
		mapManagerService.deleteRegion(map);
		return ServerResponse.myCreateBySuccess();
	}

	/**
	 * 获取自定义区域面信息(管理区域信息，提供客户接口)
	 * @param aircode
	 * @return
	 */
	@RequestMapping("/getManageGeoRegion")
	public ServerResponse getManageGeoRegion(String  aircode) {
		Map m=mapManagerService.getManageGeoRegion(aircode);
		return ServerResponse.createBySuccess(m);
	}
}

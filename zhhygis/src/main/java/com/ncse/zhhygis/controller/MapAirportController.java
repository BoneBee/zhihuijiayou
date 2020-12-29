package com.ncse.zhhygis.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ncse.zhhygis.service.MapAirportService;
import com.ncse.zhhygis.utils.ServerResponse;
import com.ncse.zhhygis.utils.baseUtils.ClassUtil;
import com.zh.bean.login.MyStaff;

/**
 * 画图相关（机位和油井）
 */
@RestController/**自动返回的是json格式数据***/
@RequestMapping("drawAir")
@CrossOrigin(allowCredentials = "true")
public class MapAirportController {
	
	 private final Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private MapAirportService mapAirportService;

	/**
	 * 新增机位区域面
	 * @param map 前台获取面参数
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/geoRegionAirAdd")
	public ServerResponse geoRegionAdd(MyStaff staff,@RequestParam Map  map,HttpServletRequest request){
		try {
			ClassUtil.MygetFiledName(map);
			map.put("aircode", staff.getStaffAirportCode());
			mapAirportService.geoAirportRegionAdd(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return ServerResponse.createByErrorMessage("报错信息是:"+e);
		}
		return ServerResponse.myCreateBySuccess();
	}

	/**
	 * 新增油井区域面
	 * @param map 前台获取面参数
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/geoOilRegionAdd")
	public ServerResponse geoOilRegionAdd(MyStaff staff,@RequestParam Map  map,HttpServletRequest request){
		try {
			ClassUtil.MygetFiledName(map);
			map.put("aircode", staff.getStaffAirportCode());
			mapAirportService.geoOilRegionAdd(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return ServerResponse.createByErrorMessage("报错信息是："+e);
		}

		return ServerResponse.myCreateBySuccess();
	}

	/**
	 * 删除机位或者油井
	 * @param map name 油井名称或者机位面编号，aircode 机场编号，flg 01机位 02油井
	 * @return
	 */
	@RequestMapping("/deleteAirportRegion")
	public ServerResponse deleteAirportRegion(MyStaff staff,@RequestParam Map map) {
		log.info("map:"+map);
		try {
			ClassUtil.MygetFiledName(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info(e.getMessage());
			e.printStackTrace();
		}
		map.put("aircode", staff.getStaffAirportCode());
//		map.put("aircode", "2901");
		mapAirportService.deleteAirportRegion(map);
		return ServerResponse.myCreateBySuccess();
	}
	
	
	/**
	 * 删除机位或者油井
	 * @param map name 油井名称或者机位面编号，aircode 机场编号，flg 01机位 02油井
	 * @return
	 */
	@RequestMapping("/deleteAirportRegion1")
	public ServerResponse deleteAirportRegion1(String aircode,String name,String flg) {
		Map map=new HashMap();
		map.put("aircode", aircode);
		map.put("name", name);
		map.put("flg", flg);
		log.info("map:"+map);
		try {
			ClassUtil.MygetFiledName(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info(e.getMessage());
			e.printStackTrace();
		}
		//map.put("aircode", staff.getStaffAirportCode());
//		map.put("aircode", "2901");
		mapAirportService.deleteAirportRegion(map);
		return ServerResponse.myCreateBySuccess();
	}
	
	/**
	 * 修改机位或者油井
	 * @param map 前台获取面参数，flg 01机位 02油井，updatetype 01修改属性，02修改点,03修改面，04同时修改中心点和面,oldName以前的名称必传
	 * @return
	 */
	@RequestMapping("/updateAirportRegion")
	public ServerResponse updateAirportRegion(MyStaff staff,@RequestParam Map map) {
		try {
			ClassUtil.MygetFiledName(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.put("aircode", staff.getStaffAirportCode());
//		map.put("aircode", "2901");
		mapAirportService.updateAirportRegion(map);
		return ServerResponse.myCreateBySuccess();
	}
	
	
	/**
	 * 修改机位或者油井
	 * @param map 前台获取面参数，flg 01机位 02油井，updatetype 01修改属性，02修改点,03修改面，04同时修改中心点和面,oldName以前的名称必传
	 * @return
	 */
	@RequestMapping("/updateAirportRegion1")
	public ServerResponse updateAirportRegion1(@RequestParam Map map) {
		try {
			ClassUtil.MygetFiledName(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		map.put("aircode", "2901");
		mapAirportService.updateAirportRegion(map);
		return ServerResponse.myCreateBySuccess();
	}
}

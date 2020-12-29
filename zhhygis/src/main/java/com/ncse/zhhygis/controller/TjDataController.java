package com.ncse.zhhygis.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ncse.zhhygis.collect.UserLoginService;
import com.ncse.zhhygis.collect.WebSocketServer;
import com.ncse.zhhygis.mapper.MaplogInfosMapper;
import com.ncse.zhhygis.service.CarAirInterfaceService;
import com.ncse.zhhygis.service.CarGisDataService;
import com.ncse.zhhygis.utils.baseUtils.RedisUtils;
import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.zh.bean.login.MyStaff;

/***
 * MethodName:  [TjDataController]
 * Description:  [主页面统计方法]
 * @param:
 * @return:
 */
@RestController/**自动返回的是json格式数据***/
@RequestMapping("tj")
@CrossOrigin(allowCredentials = "true")
public class TjDataController {

	@Autowired
	private CarAirInterfaceService carAirInterfaceService;

	@Autowired
	private CarGisDataService carGisDataService;

	@Autowired
	private UserLoginService userLoginService;

	@Autowired
	private RedisUtils redisUtils;


	@Resource
	private Datasource zhhydatasource;

	@Autowired
	private MaplogInfosMapper maplogInfosMapper;

	private final Logger log = Logger.getLogger(this.getClass());

    /*@RequestMapping("/airCount")
    public String airCount(String aircode){
    	if(aircode==null || aircode.equals("")) {
    		return "机场代码不能为空";
    	}
        log.info("aircode参数为："+aircode);
        String datas="";
		try {
			datas = carAirInterfaceService.tjAirCount(aircode);
			log.info("airCount接口数据为："+datas);
		} catch (IOException e) {
			datas = e.getMessage();
			// TODO Auto-generated catch block
            log.error(e);
		}
        return datas;
    }*/

	@RequestMapping("/test")
	public String test(String aircode){
		log.info("aircode参数为："+aircode);

		return "11111111111";
	}

	@RequestMapping("/send1")
	public String send1() throws IOException{
		JSONObject json=JSON.parseObject("{\"airport\":\"111\",\"bool_data\":\"\",\"wd\":\"25.10666319895339\",\"timeStamp\":\"2018-11-14 15:01:34\",\"termVer\":\"1.0\",\"gd\":\"\",\"hp\":\"民航C6602\",\"jd\":\"102.94283866882326\",\"num_data\":\"0.0,0.0,2.1\",\"stars\":0,\"flag\":0,\"code_param\":0,\"code\":-1}");
		WebSocketServer.sendInfo(JSON.toJSONString(json) ,  "111carGis");

		return "11111111111";
	}
	@RequestMapping("/send2")
	public String send2() throws IOException{
		JSONObject json=JSON.parseObject("{\"airport\":\"111\",\"bool_data\":\"\",\"wd\":\"25.10413722870487\",\"timeStamp\":\"2018-11-14 15:01:34\",\"termVer\":\"1.0\",\"gd\":\"\",\"hp\":\"民航C6603\",\"jd\":\"102.94107913970949\",\"num_data\":\"0.0,0.0,2.1\",\"stars\":0,\"flag\":0,\"code_param\":0,\"code\":-1}");
		WebSocketServer.sendInfo(JSON.toJSONString(json) ,  "111carGis");

		return "2222222222";
	}
	@RequestMapping("/send3")
	public String send3() throws IOException{
		JSONObject json=JSON.parseObject("{\"airport\":\"111\",\"bool_data\":\"\",\"wd\":\"25.101650068558666\",\"timeStamp\":\"2018-11-14 15:01:34\",\"termVer\":\"1.0\",\"gd\":\"\",\"hp\":\"民航C6602\",\"jd\":\"102.939190864563\",\"num_data\":\"0.0,0.0,2.1\",\"stars\":0,\"flag\":0,\"code_param\":0,\"code\":-1}");
		WebSocketServer.sendInfo(JSON.toJSONString(json) ,  "111carGis");

		return "333333333333";
	}

	@RequestMapping("/adduser")
	public String adduser(String user,String aircode) {
		MyStaff myst=new MyStaff();
		myst.setStaffId(user);
		myst.setStaffAirportCode(aircode);
		userLoginService.setUsers(myst);
		return "1111111";
	}

	@RequestMapping("/ryxq")
	public String ryxq(String staffId, HttpServletRequest request) throws IOException {
		//String re=carAirInterfaceService.ryxq(staffId);

		JSONObject ryxq=JSON.parseObject(carAirInterfaceService.ryxq(staffId));
		JSONObject ryxqdata=ryxq.getJSONObject("data");
		String ryname=ryxqdata.getString("staffName");

		return ryname;
	}

	@RequestMapping("/rylb")
	public List rylb(String staffId,HttpServletRequest request) throws IOException {

		//String re=carAirInterfaceService.rwlb(staffId);
    	/*JSONObject rwlb=JSON.parseObject(carAirInterfaceService.rwlb(staffId));
		JSONArray rwlblist=rwlb.getJSONArray("data");*/
		List list = new ArrayList();
		//人员详情获取人员名称
		JSONObject ryxq=JSON.parseObject(carAirInterfaceService.ryxq(staffId));
		JSONObject ryxqdata=ryxq.getJSONObject("data");
		String ryname=ryxqdata.getString("staffName");
		//任务列表里获取任务展示在前端
		JSONObject rwlb=JSON.parseObject(carAirInterfaceService.rwlb(staffId));
		JSONArray rwlblist=rwlb.getJSONArray("data");

		Map m;
		JSONObject json;
		String status;
		for(int i=0;i<rwlblist.size();i++) {
			json=rwlblist.getJSONObject(i);
			status=json.getString("taskStatus");
			m=new HashMap();
			m.put("flightNum", json.getString("flgtFlno"));
			m.put("status", status);
			m.put("placeCode", json.getString("flgtPlacecode"));
			m.put("dstot", json.getString("flgtDStot"));
			list.add(m);

		}
		return list;
	}

	@RequestMapping("/cs")
	public String csgps() throws IOException {
		String gisJson="{\"addTime\":\"2019-04-30 13:43:31\",\"airport\":\"2230\",\"angle\":\"7.96\",\"code\":-1,\"code_param\":0,\"flag\":2,\"gd\":\"203.0\",\"hp\":\"民航EC849\",\"jd\":\"12541.5217\",\"no\":\"D13\",\"num_data\":[0.0,0.0,0.0,2.6519638E7,0.0,0.0,0.0,0.0,0.0,0.0],\"stars\":11,\"status\":0,\"termVer\":\"2.3843\",\"timeStamp\":\"2019-04-30 14:05:30\",\"update_idx\":[238,0,10,7,1],\"wd\":\"4359.6073\"}";
		carGisDataService.carGisDataHandle(gisJson, "real");
		return "11111";
	}

	@RequestMapping("/worktest")
	public String worktest() {
    	/*AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext(BeansConfiguration.class);
    	Datasource spaceDatasource= (Datasource) context.getBean("spaceDatasource");*/
		StringBuffer sb=new StringBuffer();
		System.out.println(zhhydatasource.getDatasets().getCount());
		if (zhhydatasource.getDatasets().getCount() > 0) {
			for (int i = 0; i < zhhydatasource.getDatasets().getCount(); i++) {
				sb.append("name:"+zhhydatasource.getDatasets().get(i).getName()).
						append("      type:"+zhhydatasource.getDatasets().get(i).getType()).append(";");

				DatasetVector datasetVector=(DatasetVector) zhhydatasource.getDatasets().get(zhhydatasource.getDatasets().get(i).getName());
				sb.append(",数据量为："+datasetVector.getRecordset(false, CursorType.STATIC).getRecordCount()+"条");
			}
		}

		return sb.toString();
	}

	@RequestMapping("/getdatabase")
	public List getdatabase() {
		return maplogInfosMapper.queryAllDatabase();
	}




}
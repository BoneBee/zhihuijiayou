package com.ncse.zhhygis.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.supermap.data.*;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ncse.zhhygis.collect.SpaceDatasource;
import com.ncse.zhhygis.mapper.MaplogInfosMapper;
import com.ncse.zhhygis.service.MapManagerService;
import com.ncse.zhhygis.utils.baseUtils.StringUtils;
import com.ncse.zhhygis.utils.projectUtils.GeoRegionUtil;

/**
 * 面相关操作
 * @author Administrator
 *
 */
@Service
public  class MapManagerServiceImpl implements MapManagerService {

	private final Logger log = Logger.getLogger(this.getClass());

	@Value("${supermap.server}")
	private String server;
	/*
	 * @Value("${supermap.database}") private String database;
	 */
	@Value("${supermap.user}")
	private String user;
	@Value("${supermap.password}")
	private String password;

	@Value("${draw.managerRegionName}")
	private String managerRegionName;

	@Value("${draw.functinalRegionName}")
	private String functinalRegionName;

	@Value("${draw.aParkRegionName}")
	private String aParkRegionName;

	@Value("${draw.cRoadeRegionName}")
	private String cRoadeRegionName;

	@Value("${draw.pAircraftName}")
	private String pAirName;

    @Value("${draw.poilwell}")
    private String poilwell;
    //新增油井线
    @Value("${draw.oilLine}")
    private String oilLine;

	@Autowired
	private MaplogInfosMapper maplogInfosMapper;
	
	@Resource
    private Datasource zhhydatasource;

	@Override
	public void geoRegionAdd(Map paramMap) {
		String aircode = (String) paramMap.get("aircode");

		String regionType = (String) paramMap.get("regionType");
		String regionName = "";

		/*switch (regionType) {
		case "1":
			regionName = managerRegionName;
		case "2":
			regionName = functinalRegionName;
		}*/
		boolean updateMemflag=false;//是否修改内存数据集
		if("1".equals(regionType)) {
			regionName = managerRegionName;
		}else {
			regionName = functinalRegionName;
			updateMemflag=true;
		}
		boolean idflag = true;

		String pointmsg = (String) paramMap.get("region");

		GeoRegion geoRegion = GeoRegionUtil.pointToGeoRegion(pointmsg);
		String datebase=maplogInfosMapper.queryDatabase(aircode);
		log.info("获取数据库名称 :"+datebase);
		// TODO Auto-generated method stub
		/*Workspace workspace = new Workspace();
		// Datasources datasources=workspace.getDatasources();
		DatasourceConnectionInfo datasourceconnection = new DatasourceConnectionInfo();
		datasourceconnection.setEngineType(EngineType.MYSQL);
		datasourceconnection.setServer(server);
		datasourceconnection.setDatabase(datebase);
		datasourceconnection.setUser(user);
		datasourceconnection.setPassword(password);
		datasourceconnection.setAlias(user);
		// 打开数据源
		Datasource datasource = workspace.getDatasources().open(datasourceconnection);*/
		//Datasource datasource =SpaceDatasource.mysqlDatasources.get(maplogInfosMapper.queryDatabase(aircode));
		Datasource datasource =SpaceDatasource.getDataSource(datebase);
		DatasetVector datasetVector = SpaceDatasource.getDataVector(datebase,regionName);
		//DatasetVector datasetVector = (DatasetVector) datasource.getDatasets().get(regionName);
		boolean addfileFlag = false;
		if (datasetVector == null) {
			log.info("未获取到自定义面数据集，将新建数据集");
			DatasetVectorInfo datasetVectorInfo = new DatasetVectorInfo();
			datasetVectorInfo.setName(regionName);
			datasetVectorInfo.setType(DatasetType.REGION);
			datasetVector = datasource.getDatasets().create(datasetVectorInfo);
			SpaceDatasource.putDataVector(datasetVector,datebase,regionName);
			addfileFlag = true;
		}
		//int regid = datasetVector.getRecordCount();
		/***********************获取regid***********************/
		Map seqmap=new HashMap();
		seqmap.put("sequncename",regionName);
		seqmap.put("pcellname","regid");
    	maplogInfosMapper.querySequnce(seqmap);
    	int regid = (Integer)seqmap.get("sequncenvalue");
    	/***********************获取regid***********************/
    	
    	
		Map copyParamMap = new HashMap();
		copyParamMap.putAll(paramMap);

		copyParamMap.remove("region");
		copyParamMap.remove("aircode");
		if (addfileFlag) {
			//Iterator<Map.Entry<String, String>> entries = copyParamMap.entrySet().iterator();
			/*while (entries.hasNext()) {
				Map.Entry<String, String> entry = entries.next();
				FieldInfo fieldInfo = new FieldInfo(entry.getKey(), FieldType.TEXT);
				datasetVector.getFieldInfos().add(fieldInfo);
				fieldInfo.dispose();
			}*/

			FieldInfo fieldInfo = new FieldInfo("regname", FieldType.TEXT);
			datasetVector.getFieldInfos().add(fieldInfo);
			fieldInfo.dispose();
			fieldInfo = new FieldInfo("topSpeed", FieldType.TEXT);
			datasetVector.getFieldInfos().add(fieldInfo);
			fieldInfo.dispose();
			fieldInfo = new FieldInfo("maxHeight", FieldType.TEXT);
			datasetVector.getFieldInfos().add(fieldInfo);
			fieldInfo.dispose();
			fieldInfo = new FieldInfo("maxtime", FieldType.TEXT);
			datasetVector.getFieldInfos().add(fieldInfo);
			fieldInfo.dispose();

			fieldInfo = new FieldInfo("regionType", FieldType.TEXT);
			datasetVector.getFieldInfos().add(fieldInfo);
			fieldInfo.dispose();
			// 增加ID字段
			if (idflag) {
				fieldInfo = new FieldInfo("regid", FieldType.TEXT);
				datasetVector.getFieldInfos().add(fieldInfo);
				fieldInfo.dispose();
			}
			
			fieldInfo = new FieldInfo("updatetime", FieldType.DATETIME);
			datasetVector.getFieldInfos().add(fieldInfo);
			fieldInfo.dispose();
			
		}

		Recordset rs = datasetVector.getRecordset(true, CursorType.DYNAMIC);
		rs.edit();//数据集进入编辑状态

		rs.addNew(geoRegion);
		if (idflag) {
			rs.setFieldValue("regid", String.valueOf(regid + 1));
		}
		log.info("添加属性：regid，值为：" + (regid+1));
		rs.setFieldValue("updatetime",new Date());
		Iterator<Map.Entry<String, String>> entries2 = copyParamMap.entrySet().iterator();
		while (entries2.hasNext()) {
			Map.Entry<String, String> entry = entries2.next();
			rs.setFieldValue(entry.getKey(), entry.getValue());
			log.info("添加属性：" + entry.getKey() + "，值为：" + entry.getValue());
		}
		
		
		rs.update();//游标修改，相当于commit
		rs.moveNext();//下一条，在此处相当于结束游标。
		//rs.close();//关闭数据集
		rs.dispose();
		
		//需要对内存数据集进行处理
		if(updateMemflag) {
			SpaceDatasource.copydataset(zhhydatasource, datebase, datasetVector);
		}
		//datasetVector.close();//关闭该数据集的数据
		/*datasource.close();//关闭数据源
		datasourceconnection.dispose();//关闭连接
		workspace.dispose();//关闭工作空间
*/	}

	@Override
	public  Map getAParkGeoRegion(String aircode){
		Map reMap = new HashMap();
		/*List list = new ArrayList<>();*/
		Map<String,Map> regMap=new HashMap<>();
		Map centerMap;

		// code保留，应该不同的数据库或者不同数据集，待以后修改
		/*Workspace workspace = new Workspace();
		// Datasources datasources=workspace.getDatasources();
		DatasourceConnectionInfo datasourceconnection = new DatasourceConnectionInfo();
		datasourceconnection.setEngineType(EngineType.MYSQL);
		datasourceconnection.setServer(server);
		datasourceconnection.setDatabase(maplogInfosMapper.queryDatabase(aircode));
		datasourceconnection.setUser(user);
		datasourceconnection.setPassword(password);
		datasourceconnection.setAlias(user);
		// 打开数据源
		Datasource datasource = workspace.getDatasources().open(datasourceconnection);*/
		
//		Datasource datasource =SpaceDatasource.mysqlDatasources.get(maplogInfosMapper.queryDatabase(aircode));
//		DatasetVector datasetVector = (DatasetVector) datasource.getDatasets().get(aParkRegionName);

		DatasetVector datasetVector = SpaceDatasource.getDataVector(maplogInfosMapper.queryDatabase(aircode),aParkRegionName);
		if (datasetVector == null) {
			//datasource.close();
			/*datasourceconnection.dispose();
			workspace.dispose();*/
			return null;
		}
		//DatasetVector pdatasetVector = (DatasetVector) datasource.getDatasets().get(pAirName);
		DatasetVector pdatasetVector = SpaceDatasource.getDataVector(maplogInfosMapper.queryDatabase(aircode),pAirName);
		if (pdatasetVector == null) {
			//datasource.close();
			//datasetVector.close();
			/*datasourceconnection.dispose();
			workspace.dispose();*/
			return null;
		}
		//Recordset recordset = datasetVector.getRecordset(false, CursorType.STATIC);
		Recordset recordset =datasetVector.query("Name is not null ", CursorType.STATIC);
		if(recordset==null) {
			return null;
		}
		GeoRegion region = null;
		int regionid;
		double weight;
		String beginPoint;
		int t=0;
		Map map = new HashMap<>(), m = new HashMap<>();
		StringBuffer sb = new StringBuffer();
		Recordset prs;
		for (int i = 0; i < recordset.getRecordCount(); i++) {
			centerMap = new HashMap();
			map = new HashMap<>();
			m = new HashMap<>();
			region = (GeoRegion) recordset.getGeometry();
			
			String name = recordset.getString("Name");
			
			if (!StringUtils.isEmpty(name)) {
				map.put("name", name);
				prs = pdatasetVector.query("Name='" + name + "'", CursorType.STATIC);
				if (prs != null) {
					map.put("farNear", prs.getString("farNear"));
					map.put("rotate", prs.getDouble("rotate"));
					//新增有相同的机位一样返回计算中心点，取消返回重名条数
					/*if (prs.getRecordCount() > 1) {
						map.put("center", prs.getRecordCount());
					} else {*/
						GeoPoint point = (GeoPoint) prs.getGeometry();
						if (point == null) {
							sb.append(name).append(",");
						} else {
							centerMap.put("lng", point.getX());
							centerMap.put("lat", point.getY());
							map.put("center", centerMap);
						}
					/*}*/
				}
				if(prs!=null) {
					prs.dispose();
					//prs.close();
				}
				
				StringBuffer points = new StringBuffer();
				for (int j = 0; j < region.getPartCount(); j++) {
					Point2D[] point2ds = region.getPart(j).toArray();
					/*
					 * for (Point2D p : point2ds) {
					 * points.append(p.getX()).append(",").append(p.getY()).append(";"); }
					 */
					m.put("part" + j, point2ds);
				}
				map.put("points", m);
				//list.add(map);
				regMap.put(name, map);

				recordset.moveNext();
			}/*else {
				//如果为空，不返回面给前端了
				
				name="id"+t;
				t++;
			}*/
			
		}
		if (sb.length() > 0) {
			log.info("未查询到飞机点的机位号为：" + sb.toString());
		}
		reMap.put("code", "0");
		reMap.put("data", regMap);

		if(recordset!=null) {
			recordset.dispose();
			//recordset.close();
		}
		/*if(pdatasetVector!=null) {
			pdatasetVector.close();
		}
		if(datasetVector!=null) {
			datasetVector.close();
		}*/

		/*datasource.close();
		datasourceconnection.dispose();
		workspace.dispose();*/

		return reMap;
	}
	
	
	
	
	
	@Override
	public Map getAParkGeoRegion2(String aircode) {
	

		String datebase=maplogInfosMapper.queryDatabase(aircode);
		log.info("获取数据库名称 :"+datebase);
		// TODO Auto-generated method stub
		/*Workspace workspace = new Workspace();
		// Datasources datasources=workspace.getDatasources();
		DatasourceConnectionInfo datasourceconnection = new DatasourceConnectionInfo();
		datasourceconnection.setEngineType(EngineType.MYSQL);
		datasourceconnection.setServer(server);
		datasourceconnection.setDatabase(datebase);
		datasourceconnection.setUser(user);
		datasourceconnection.setPassword(password);
		datasourceconnection.setAlias(user);
		// 打开数据源
		Datasource datasource = workspace.getDatasources().open(datasourceconnection);*/
		//Datasource datasource =SpaceDatasource.mysqlDatasources.get(datebase);
		//DatasetVector datasetVector = (DatasetVector) datasource.getDatasets().get(pAirName);
		DatasetVector datasetVector = SpaceDatasource.getDataVector(datebase,pAirName);
		FieldInfo fieldInfo = new FieldInfo("farNear", FieldType.TEXT);
		datasetVector.getFieldInfos().add(fieldInfo);
		fieldInfo.dispose();
		//datasetVector.close();//关闭该数据集的数据
		/*datasource.close();//关闭数据源
		datasourceconnection.dispose();//关闭连接
		workspace.dispose();//关闭工作空间
*/		return null;
	}
	

	@Override
	public void updateRegion(Map paramMap) {
		
		String regionid = (String) paramMap.get("regid");
		String aircode = (String) paramMap.get("aircode");
		String regionType = (String) paramMap.get("regionType");
		String regionName = "";
		
		boolean updateMemflag=false;//是否修改内存数据集
		if("1".equals(regionType)) {
			regionName = managerRegionName;
		}else {
			regionName = functinalRegionName;
			updateMemflag=true;
		}
		/*switch (regionType) {
		case "1":
			regionName = managerRegionName;
		case "2":
			regionName = functinalRegionName;
		}*/
		
		/*Workspace workspace = new Workspace();
		
		DatasourceConnectionInfo datasourceconnection = new DatasourceConnectionInfo();
		datasourceconnection.setEngineType(EngineType.MYSQL);
		datasourceconnection.setServer(server);
		datasourceconnection.setDatabase(maplogInfosMapper.queryDatabase(aircode));
		datasourceconnection.setUser(user);
		datasourceconnection.setPassword(password);
		datasourceconnection.setAlias(user);
		// 打开数据源
		Datasource datasource = workspace.getDatasources().open(datasourceconnection);*/
//		Datasource datasource =SpaceDatasource.mysqlDatasources.get(maplogInfosMapper.queryDatabase(aircode));
//		DatasetVector datasetVector = (DatasetVector) datasource.getDatasets().get(regionName);
		DatasetVector datasetVector = SpaceDatasource.getDataVector(maplogInfosMapper.queryDatabase(aircode),regionName);
		Recordset rs = datasetVector.query("regid=" + regionid, CursorType.DYNAMIC);
		String pointmsg = (String) paramMap.get("region");
		GeoRegion geoRegion = GeoRegionUtil.pointToGeoRegion(pointmsg);
		Map copyParamMap = new HashMap();
		copyParamMap.putAll(paramMap);
		copyParamMap.remove("region");
		copyParamMap.remove("regid");
		rs.edit();

		rs.setGeometry(geoRegion);
		Iterator<Map.Entry<String, String>> entries = copyParamMap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, String> entry = entries.next();
			log.info("修改属性：" + entry.getKey() + "，值为：" + entry.getValue());
			if (!"aircode".equals(entry.getKey())) {
				rs.setFieldValue(entry.getKey(), entry.getValue());
			}
		}
		rs.setFieldValue("updatetime",new Date());
		rs.update();
		rs.moveNext();
		//rs.close();
		rs.dispose();
		//需要对内存数据集进行处理
		if(updateMemflag) {
			SpaceDatasource.copydataset(zhhydatasource, maplogInfosMapper.queryDatabase(aircode), datasetVector);
		}

		//datasetVector.close();
		/*datasource.close();
		datasourceconnection.dispose();
		workspace.dispose();*/
	}

	@Override
	public Map getCustomGeoRegion(String aircode) {
		Map reMap = new HashMap();
		List list = new ArrayList<>();
		Map centerMap;
		
		
		/*Workspace workspace = new Workspace();
		
		DatasourceConnectionInfo datasourceconnection = new DatasourceConnectionInfo();
		datasourceconnection.setEngineType(EngineType.MYSQL);
		datasourceconnection.setServer(server);
		datasourceconnection.setDatabase(maplogInfosMapper.queryDatabase(aircode));
		datasourceconnection.setUser(user);
		datasourceconnection.setPassword(password);
		datasourceconnection.setAlias(user);
		// 打开数据源
		Datasource datasource = workspace.getDatasources().open(datasourceconnection);*/
//		Datasource datasource =SpaceDatasource.mysqlDatasources.get(maplogInfosMapper.queryDatabase(aircode));
//		DatasetVector datasetVector = (DatasetVector) datasource.getDatasets().get(managerRegionName);
		DatasetVector datasetVector = SpaceDatasource.getDataVector(maplogInfosMapper.queryDatabase(aircode),managerRegionName);
		if (datasetVector == null) {
			/*datasource.close();
			datasourceconnection.dispose();
			workspace.dispose();*/
			return null;
		}
		//DatasetVector datasetVector2 = (DatasetVector) datasource.getDatasets().get(pAirName);
		DatasetVector datasetVector2 = SpaceDatasource.getDataVector(maplogInfosMapper.queryDatabase(aircode),pAirName);

		Recordset recordset = datasetVector.getRecordset(false, CursorType.STATIC);//管理面集
		Recordset recordset2 = datasetVector2.getRecordset(false, CursorType.STATIC);//飞机点集
		Map<String,Geometry> airMap=new HashMap();  //飞机点集合。因为使用数据集遍历只能一次，所以这里使用集合来存储
		String airname;
		for(int t=0;t<recordset2.getRecordCount();t++) {
			airname=recordset2.getString("Name");
			if(!StringUtils.isEmpty(airname)) {
				airMap.put(airname, recordset2.getGeometry());
			}
			recordset2.moveNext();
		}
		recordset2.dispose();
//		recordset2.close();
//		datasetVector2.close();
		
		
		GeoRegion region = null;
		int regionid;
		double weight;
		String beginPoint;

		Map map , m ;
		StringBuffer sb = new StringBuffer();
		StringBuffer parkList;
		Iterator<Map.Entry<String, Geometry>> entries;
		Map.Entry<String, Geometry> entry;
		StringBuffer points;
		String name;
		Point2D[] point2ds;
		for (int i = 0; i < recordset.getRecordCount(); i++) {
			parkList = new StringBuffer();
			centerMap = new HashMap();
			map = new HashMap<>();
			m = new HashMap<>();
			region = (GeoRegion) recordset.getGeometry();
			name = recordset.getString("regname");
			points = new StringBuffer();
			for (int j = 0; j < region.getPartCount(); j++) {
				point2ds = region.getPart(j).toArray();
				m.put("part" + j, point2ds);
			}
			map.put("name", name);
			map.put("points", m);
			
			//遍历飞机点集合，判断点面相交，如果是，加入机位号。
			entries = airMap.entrySet().iterator();
			while (entries.hasNext()) {
				entry = entries.next();
				if(Geometrist.hasIntersection(region,entry.getValue())) {
					parkList.append(entry.getKey()).append(",");
				}
			}		
			map.put("parkList", parkList.toString());
			list.add(map);
			recordset.moveNext();
		}
		if (sb.length() > 0) {
			log.info("未查询到飞机点的机位号为：" + sb.toString());
		}
		reMap.put("code", "0");
		reMap.put("data", list);
		recordset.dispose();
//		recordset.close();
//		datasetVector.close();
		/*datasource.close();
		datasourceconnection.dispose();
		workspace.dispose();*/
		return reMap;
	}

	@Override
	public Map getCustomGeoAttr(String aircode) {
		Map reMap = new HashMap();
		List list = new ArrayList<>();
		String dataname=maplogInfosMapper.queryDatabase(aircode);
		/*Workspace workspace = new Workspace();
		DatasourceConnectionInfo datasourceconnection = new DatasourceConnectionInfo();
		datasourceconnection.setEngineType(EngineType.MYSQL);
		datasourceconnection.setServer(server);
		datasourceconnection.setDatabase(maplogInfosMapper.queryDatabase(aircode));
		datasourceconnection.setUser(user);
		datasourceconnection.setPassword(password);
		datasourceconnection.setAlias(user);
		// 打开数据源
		Datasource datasource = workspace.getDatasources().open(datasourceconnection);*/
		//Datasource datasource =SpaceDatasource.mysqlDatasources.get(maplogInfosMapper.queryDatabase(aircode));
		//管理面
		//DatasetVector datasetVector = (DatasetVector) datasource.getDatasets().get(managerRegionName);
		DatasetVector datasetVector = SpaceDatasource.getDataVector(dataname,managerRegionName);
		//功能面
		//DatasetVector datasetVector2 = (DatasetVector) datasource.getDatasets().get(functinalRegionName);
		DatasetVector datasetVector2 = SpaceDatasource.getDataVector(dataname,functinalRegionName);
		Map map;
		if (datasetVector != null) {
			Recordset recordset = datasetVector.getRecordset(false, CursorType.STATIC);//管理面集
			for (int i = 0; i < recordset.getRecordCount(); i++) {
				map=new HashMap();
				map.put("regid", recordset.getString("regid"));
				map.put("name", recordset.getString("regname"));
				map.put("updatetime", recordset.getDateTime("updatetime"));
				map.put("topSpeed", recordset.getString("topSpeed"));
				map.put("maxHeight", recordset.getString("maxHeight"));
				map.put("maxtime", recordset.getString("maxtime"));
				map.put("regionType", recordset.getString("regionType"));
				list.add(map);
				recordset.moveNext();
			}
			recordset.dispose();
//			recordset.close();
//			datasetVector.close();
		}
		if (datasetVector2 != null) {
			Recordset recordset = datasetVector2.getRecordset(false, CursorType.STATIC);//功能面集
			for (int i = 0; i < recordset.getRecordCount(); i++) {
				map=new HashMap();
				map.put("regid", recordset.getString("regid"));
				map.put("name", recordset.getString("regname"));
				map.put("updatetime", recordset.getDateTime("updatetime"));
				map.put("topSpeed", recordset.getString("topSpeed"));
				map.put("maxHeight", recordset.getString("maxHeight"));
				map.put("maxtime", recordset.getString("maxtime"));
				map.put("regionType", recordset.getString("regionType"));
				list.add(map);
				recordset.moveNext();
			}
			recordset.dispose();
//			recordset.close();
//			datasetVector2.close();
		}
		reMap.put("code", "0");
		reMap.put("data", list);
		/*datasource.close();
		datasourceconnection.dispose();
		workspace.dispose();*/
		return reMap;
	}

	@Override
	public Map getCustomGeoRegionById(Map paramMap) {
		
		String aircode = (String) paramMap.get("aircode");
		String regionType = (String) paramMap.get("regionType");
		String regid = (String) paramMap.get("regid");
		
		String regionName = "";

		if("1".equals(regionType)) {
			regionName = managerRegionName;
		}else {
			regionName = functinalRegionName;
		}
		
		Map reMap = new HashMap(),regMap=new HashMap();
		
		
		/*Workspace workspace = new Workspace();
		DatasourceConnectionInfo datasourceconnection = new DatasourceConnectionInfo();
		datasourceconnection.setEngineType(EngineType.MYSQL);
		datasourceconnection.setServer(server);
		datasourceconnection.setDatabase(maplogInfosMapper.queryDatabase(aircode));
		datasourceconnection.setUser(user);
		datasourceconnection.setPassword(password);
		datasourceconnection.setAlias(user);
	
		
		Datasource datasource = workspace.getDatasources().open(datasourceconnection);*/
		String database=maplogInfosMapper.queryDatabase(aircode);
//		Datasource datasource =SpaceDatasource.mysqlDatasources.get(maplogInfosMapper.queryDatabase(aircode));
//				
//		DatasetVector datasetVector = (DatasetVector) datasource.getDatasets().get(regionName);
		DatasetVector datasetVector = SpaceDatasource.getDataVector(database,regionName);
		Recordset recordset=datasetVector.query("regid='" + regid + "'", CursorType.STATIC);
		GeoRegion region = (GeoRegion) recordset.getGeometry();

		for (int i = 0; i < recordset.getRecordCount(); i++) {
			regMap.put("regid", recordset.getString("regid"));
			regMap.put("name", recordset.getString("regname"));
			regMap.put("updatetime", recordset.getDateTime("updatetime"));
			regMap.put("topSpeed", recordset.getString("topSpeed"));
			regMap.put("maxHeight", recordset.getString("maxHeight"));
			regMap.put("maxtime", recordset.getString("maxtime"));
			regMap.put("regionType", recordset.getString("regionType"));
			recordset.moveNext();
		}
		
		
		Map m=new HashMap();
		StringBuffer points = new StringBuffer();
		if (region==null){
			reMap.put("code", "1");
			recordset.dispose();
			return reMap;
		}
		for (int j = 0; j < region.getPartCount(); j++) {
			Point2D[] point2ds = region.getPart(j).toArray();
			m.put("part" + j, point2ds);
		}
		recordset.dispose();
		regMap.put("points", m);
		regMap.putAll(paramMap);
		// TODO Auto-generated method stub
		reMap.put("code", "0");
		reMap.put("data", regMap);
		/*datasource.close();
		datasourceconnection.dispose();
		workspace.dispose();*/
		return reMap;
	}

	/***
	 * MethodName:  [方法名称] 
	 * Description:  [删除面]
	 * @param: [paramMap]
	 * @return: void
	 */
	@Override
	public void deleteRegion(Map paramMap) {
		String regionid = (String) paramMap.get("regid");
		String aircode = (String) paramMap.get("aircode");
		String regionType = (String) paramMap.get("regionType");
		String regionName = "";
		
		boolean updateMemflag=false;//是否修改内存数据集
		if("1".equals(regionType)) {
			regionName = managerRegionName;
		}else {
			regionName = functinalRegionName;
			updateMemflag=true;
		}
		/*Workspace workspace = new Workspace();
		DatasourceConnectionInfo datasourceconnection = new DatasourceConnectionInfo();
		datasourceconnection.setEngineType(EngineType.MYSQL);
		datasourceconnection.setServer(server);
		datasourceconnection.setDatabase(maplogInfosMapper.queryDatabase(aircode));
		datasourceconnection.setUser(user);
		datasourceconnection.setPassword(password);
		datasourceconnection.setAlias(user);
		// 打开数据源
		Datasource datasource = workspace.getDatasources().open(datasourceconnection);*/
//		Datasource datasource =SpaceDatasource.mysqlDatasources.get(maplogInfosMapper.queryDatabase(aircode));
//		DatasetVector datasetVector = (DatasetVector) datasource.getDatasets().get(regionName);
		DatasetVector datasetVector = SpaceDatasource.getDataVector(maplogInfosMapper.queryDatabase(aircode),regionName);
		Recordset rs = datasetVector.query("regid=" + regionid, CursorType.DYNAMIC);
		Map copyParamMap = new HashMap();
		copyParamMap.putAll(paramMap);
		copyParamMap.remove("region");
		copyParamMap.remove("regid");
		rs.edit();
		rs.delete();
		rs.moveNext();
		rs.dispose();
		//rs.close();
		//需要对内存数据集进行处理
		if(updateMemflag) {
			SpaceDatasource.copydataset(zhhydatasource, maplogInfosMapper.queryDatabase(aircode), datasetVector);
		}
		//datasetVector.close();
		/*datasource.close();
		datasourceconnection.dispose();
		workspace.dispose();*/
	}

    /**
     * 获取自定义区域面信息(管理区域信息，提供客户接口)
     * @param aircode
     * @return
     */
    @Override
    public Map getManageGeoRegion(String aircode) {
        Map reMap = new HashMap();
        List list = new ArrayList<>();
        Map centerMap;
        /*Workspace workspace = new Workspace();
        DatasourceConnectionInfo datasourceconnection = new DatasourceConnectionInfo();
        datasourceconnection.setEngineType(EngineType.MYSQL);
        datasourceconnection.setServer(server);
        datasourceconnection.setDatabase(maplogInfosMapper.queryDatabase(aircode));
        datasourceconnection.setUser(user);
        datasourceconnection.setPassword(password);
        datasourceconnection.setAlias(user);
        // 打开数据源
        Datasource datasource = workspace.getDatasources().open(datasourceconnection);*/
//        Datasource datasource =SpaceDatasource.mysqlDatasources.get(maplogInfosMapper.queryDatabase(aircode));
//        DatasetVector datasetVector = (DatasetVector) datasource.getDatasets().get(managerRegionName);
        String dataname=maplogInfosMapper.queryDatabase(aircode);
        DatasetVector datasetVector = SpaceDatasource.getDataVector(dataname,managerRegionName);
        if (datasetVector == null) {
           /* datasource.close();
            datasourceconnection.dispose();
            workspace.dispose();*/
            return null;
        }
//        DatasetVector datasetVector2 = (DatasetVector) datasource.getDatasets().get(pAirName);//机场
//        DatasetVector datasetVector3 = (DatasetVector) datasource.getDatasets().get(poilwell);//油井
        DatasetVector datasetVector2 = SpaceDatasource.getDataVector(dataname,pAirName);
        DatasetVector datasetVector3 = SpaceDatasource.getDataVector(dataname,poilwell);
        Recordset recordset = datasetVector.getRecordset(false, CursorType.STATIC);//管理面集
        Recordset recordset2 = datasetVector2.getRecordset(false, CursorType.STATIC);//飞机点集
        Map<String,Geometry> airMap=new HashMap();  //飞机点集合。因为使用数据集遍历只能一次，所以这里使用集合来存储
        String airname;
        for(int t=0;t<recordset2.getRecordCount();t++) {
            airname=recordset2.getString("Name");
            if(!StringUtils.isEmpty(airname)) {
                airMap.put(airname, recordset2.getGeometry());
            }
            recordset2.moveNext();
        }
        recordset2.dispose();
//        recordset2.close();
//        datasetVector2.close();


        GeoRegion region = null;
        int regionid;
        double weight;
        String beginPoint;

        Map map , m ;
        StringBuffer sb = new StringBuffer();
        StringBuffer parkList;
        Iterator<Map.Entry<String, Geometry>> entries;
        Map.Entry<String, Geometry> entry;
        StringBuffer points;
        String name;
        Point2D[] point2ds;
        for (int i = 0; i < recordset.getRecordCount(); i++) {
            parkList = new StringBuffer();
            centerMap = new HashMap();
            map = new HashMap<>();
            m = new HashMap<>();
            region = (GeoRegion) recordset.getGeometry();
            name = recordset.getString("regname");
            String regid = recordset.getString("regid");
            points = new StringBuffer();
            for (int j = 0; j < region.getPartCount(); j++) {
                point2ds = region.getPart(j).toArray();
                m.put("part" + j, point2ds);
            }
            map.put("name", name);
            map.put("SmID", regid);
            map.put("points", m);

            //遍历飞机点集合，判断点面相交，如果是，加入机位号。
            entries = airMap.entrySet().iterator();
            while (entries.hasNext()) {
                entry = entries.next();
                if(Geometrist.hasIntersection(region,entry.getValue())) {
                    String aircraft = entry.getKey();
                    Recordset prs = datasetVector3.query("Name LIKE'" + aircraft + "%'", CursorType.STATIC);
                    String oilnames = "";
                    for(int k=0;k<prs.getRecordCount();k++) {//获取游标总条数进行for循环
                        oilnames = oilnames+prs.getString("Name") + ",";
                        prs.moveNext();
                    }
                    oilnames = oilnames.substring(0,oilnames.length() - 1);
                    parkList.append("'"+entry.getKey()).append(","+oilnames+"',");
                    prs.dispose();
                }
            }
            String parkResult = parkList.toString();
            parkResult = parkResult.substring(0,parkResult.length() - 1);
            map.put("parkList", parkResult);
            list.add(map);
            recordset.moveNext();
        }
        if (sb.length() > 0) {
            log.info("未查询到飞机点的机位号为：" + sb.toString());
        }
        reMap.put("code", "0");
        reMap.put("data", list);
        recordset.dispose();
//        recordset.close();
//        datasetVector.close();
       /* datasource.close();
        datasourceconnection.dispose();
        workspace.dispose();*/
        return reMap;
    }

	@Override
	public Map getOilWellLine(String aircode) throws Exception {
		Map reMap = new HashMap();
		List list = new ArrayList<>();
		DatasetVector datasetVector = SpaceDatasource.getDataVector(maplogInfosMapper.queryDatabase(aircode),oilLine);


		Recordset recordset = datasetVector.getRecordset(false, CursorType.STATIC);
		//线数据
		GeoLine  geoLine = null;

		int t=0;
		Map map ,m ;
		for (int i = 0; i < recordset.getRecordCount(); i++) {
			map = new HashMap<>();
			m = new HashMap<>();
			//线数据GeoLine
			GeometryType type = recordset.getGeometry().getType();
			geoLine = (GeoLine) recordset.getGeometry();
			log.info(geoLine);
			for (int j = 0; j < geoLine.getPartCount(); j++) {
				Point2D[] point2DS = geoLine.getPart(j).toArray();
				m.put("part"+j,point2DS);

			}

			map.put("points",m);
			map.put("length",geoLine.getLength());
			list.add(map);
			recordset.moveNext();
		}

		reMap.put("code", "0");
		reMap.put("data", list);
		recordset.dispose();

		return reMap;

	}

	@Override
	public Map getOilRegion(String aircode) {
		Map reMap = new HashMap();
		List list = new ArrayList<>();
		Map centerMap;

		// code保留，应该不同的数据库或者不同数据集，待以后修改
		/*Workspace workspace = new Workspace();
		// Datasources datasources=workspace.getDatasources();
		DatasourceConnectionInfo datasourceconnection = new DatasourceConnectionInfo();
		datasourceconnection.setEngineType(EngineType.MYSQL);
		datasourceconnection.setServer(server);
		datasourceconnection.setDatabase(maplogInfosMapper.queryDatabase(aircode));
		datasourceconnection.setUser(user);
		datasourceconnection.setPassword(password);
		datasourceconnection.setAlias(user);
		// 打开数据源
		Datasource datasource = workspace.getDatasources().open(datasourceconnection);*/
//		Datasource datasource =SpaceDatasource.mysqlDatasources.get(maplogInfosMapper.queryDatabase(aircode));
//		DatasetVector datasetVector = (DatasetVector) datasource.getDatasets().get(poilwell);
		DatasetVector datasetVector = SpaceDatasource.getDataVector(maplogInfosMapper.queryDatabase(aircode),poilwell);

		
		Recordset recordset = datasetVector.getRecordset(false, CursorType.STATIC);
		GeoPoint region = null;
		int regionid;
		double weight;
		String beginPoint;
		
		int t=0;
		Map map = new HashMap<>(), m ;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < recordset.getRecordCount(); i++) {
			map = new HashMap<>();
			m = new HashMap<>();
			region = (GeoPoint) recordset.getGeometry();
			map.put("name", recordset.getString("Name"));
			map.put("nameType", recordset.getString("NameType"));
			m.put("x", region.getX());
			m.put("y", region.getY());
			map.put("point",m);
			list.add(map);
			recordset.moveNext();
		}
		
		reMap.put("code", "0");
		reMap.put("data", list);
		recordset.dispose();
		//recordset.close();

		//datasetVector.close();
		/*datasource.close();
		datasourceconnection.dispose();
		workspace.dispose();*/

		return reMap;
	}

}

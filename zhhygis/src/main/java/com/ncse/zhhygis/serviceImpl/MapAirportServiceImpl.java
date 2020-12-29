package com.ncse.zhhygis.serviceImpl;

import com.ncse.zhhygis.collect.SpaceDatasource;
import com.ncse.zhhygis.mapper.MaplogInfosMapper;
import com.ncse.zhhygis.service.MapAirportService;
import com.ncse.zhhygis.service.MapManagerService;
import com.ncse.zhhygis.utils.baseUtils.StringUtils;
import com.ncse.zhhygis.utils.projectUtils.GeoRegionUtil;
import com.ncse.zhhygis.utils.projectUtils.Topoint;
import com.supermap.data.*;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 机场面相关操作
 * @author Administrator
 *
 */
@Service
public  class MapAirportServiceImpl implements MapAirportService {

	private final Logger log = Logger.getLogger(this.getClass());

	@Value("${supermap.server}")
	private String server;
	@Value("${supermap.user}")
	private String user;
	@Value("${supermap.password}")
	private String password;

	//飞机中心点
	@Value("${draw.pAircraftName}")
	private String pAircraftName;
	//飞机机位
	@Value("${draw.aParkRegionName}")
	private String aParkRegionName;
	//油井
    @Value("${draw.poilwell}")
    private String poilwell;

	@Autowired
	private MaplogInfosMapper maplogInfosMapper;

	/**
	 * 机位添加
	 * @param paramMap
	 */
	@Override
	public void geoAirportRegionAdd(Map paramMap) throws Exception {
		String aircode = (String) paramMap.get("aircode");
		String regionName = aParkRegionName;
		String aircraftName = pAircraftName;

		boolean idflag = true;
		String pointmsg = (String) paramMap.get("region");
		String airPoint = (String) paramMap.get("airPoint");
		GeoRegion geoRegion = GeoRegionUtil.pointToGeoRegion(pointmsg);
		//名称为空则不允许提交
		String IsName = (String) paramMap.get("Name");
		if (StringUtils.isEmpty(IsName)){
			throw new Exception("名称不能为空");
		}
		//新增解决新增机位重名校验
        DatasetVector datasetVectorName = SpaceDatasource.getDataVector(maplogInfosMapper.queryDatabase(aircode),aParkRegionName);
        Recordset recordset =datasetVectorName.query("Name is not null ", CursorType.STATIC);
        for (int i = 0; i < recordset.getRecordCount(); i++) {
            String name = recordset.getString("Name");
            String AddName = (String) paramMap.get("Name");
				if (AddName.equalsIgnoreCase(name)){
					throw new Exception("该机位名称已存在，请从新输入");
				}
            //切换到下一个recordset,遍历查找是否有相同的name
           recordset.moveNext();

        }

		String[] split = airPoint.split(",");
		double[] doubles = Topoint.lonLatToMercator(Double.parseDouble(split[1]), Double.parseDouble(split[0]));
		Point2D point2D = new Point2D(doubles[0], doubles[1]);
		GeoPoint airPointRegion = new GeoPoint(new Point2D(point2D));
		String datebase=maplogInfosMapper.queryDatabase(aircode);
		log.info("获取数据库名称 :"+datebase);
		// TODO Auto-generated method stub
		Datasource datasource =SpaceDatasource.getDataSource(datebase);
		//新增修改面数据使其能正常入空间数据库，解决新建机位不成功
		DatasetVector datasetVector = SpaceDatasource.getDataVector(datebase,regionName);
		DatasetVector airDatasetVector = SpaceDatasource.getDataVector(datebase,aircraftName);
		boolean addfileFlag = false;
		if (datasetVector == null) {
			log.info("未获取到自定义面数据集，将新建数据集");
			DatasetVectorInfo datasetVectorInfo = new DatasetVectorInfo();
			datasetVectorInfo.setName(regionName);
			datasetVectorInfo.setType(DatasetType.REGION);
			//datasetVector = datasource.getDatasets().create(datasetVectorInfo);
			datasetVector=datasource.getDatasets().create(datasetVectorInfo);
			SpaceDatasource.putDataVector(datasetVector,datebase,regionName);
			addfileFlag = true;
		}
		if (airDatasetVector ==null){
			log.info("未获取到自定义面数据集，将新建数据集");
			DatasetVectorInfo datasetVectorInfo = new DatasetVectorInfo();
			datasetVectorInfo.setName(aircraftName);
			datasetVectorInfo.setType(DatasetType.POINT);
			airDatasetVector = datasource.getDatasets().create(datasetVectorInfo);
			SpaceDatasource.putDataVector(datasetVector,datebase,aircraftName);
			addfileFlag = true;
		}
		/***********************获取regid***********************/
		Map seqmap=new HashMap();
		seqmap.put("sequncename",regionName);
		seqmap.put("pcellname","regid");
    	maplogInfosMapper.querySequnce(seqmap);
//    	int regid = (Integer)seqmap.get("sequncenvalue");
    	/***********************获取regid***********************/
		Map copyParamMap = new HashMap();
		copyParamMap.putAll(paramMap);
		//去除其他参数，只保留需要添加得参数
		copyParamMap.remove("region");
		copyParamMap.remove("airPoint");
		copyParamMap.remove("aircode");
		if (addfileFlag) {
			FieldInfo fieldInfo = new FieldInfo("Name", FieldType.TEXT);
			datasetVector.getFieldInfos().add(fieldInfo);
			airDatasetVector.getFieldInfos().add(fieldInfo);
			fieldInfo.dispose();
		}
		Recordset rs = datasetVector.getRecordset(true, CursorType.DYNAMIC);
		Recordset airRs = airDatasetVector.getRecordset(true, CursorType.DYNAMIC);
		rs.edit();//数据集进入编辑状态
		airRs.edit();//数据集进入编辑状态
		rs.addNew(geoRegion);
		airRs.addNew(airPointRegion);
		//面只有Name属性，点有Name和farNear属性
		rs.setFieldValue("Name", paramMap.get("Name"));
		airRs.setFieldValue("Name", paramMap.get("Name"));
		airRs.setFieldValue("farNear", paramMap.get("farNear"));
		airRs.setFieldValue("rotate", paramMap.get("rotate"));
		rs.update();//游标修改，相当于commit
		airRs.update();//游标修改，相当于commit
		rs.moveNext();//下一条，在此处相当于结束游标。
		airRs.moveNext();//下一条，在此处相当于结束游标。
		rs.dispose();//关闭数据集
		airRs.dispose();//关闭数据集

	}

	/**
	 * 油井添加
	 * @param paramMap
	 */
	@Override
	public void geoOilRegionAdd(Map paramMap) throws Exception {
		String aircode = (String) paramMap.get("aircode");
		String regionName = poilwell;
		String oilPoint = (String) paramMap.get("oilPoint");
		//油井名称不能为空
		String NameIs = (String) paramMap.get("Name");
		if (StringUtils.isEmpty(NameIs)){
			throw new Exception("油井名称不能为空");
		}
		//油井重名校验
		DatasetVector datasetVectorName = SpaceDatasource.getDataVector(maplogInfosMapper.queryDatabase(aircode),poilwell);
		Recordset recordset = datasetVectorName.getRecordset(false, CursorType.STATIC);
		for (int i = 0; i < recordset.getRecordCount(); i++) {
			String name = recordset.getString("Name");
			String AddName = (String) paramMap.get("Name");
			if (AddName.equalsIgnoreCase(name)){
				throw new Exception("该油井名称已存在，请从新输入");
			}
			//切换到下一个recordset,遍历查找是否有相同的name
			recordset.moveNext();

		}
		String[] split = oilPoint.split(",");
		double[] doubles = Topoint.lonLatToMercator(Double.parseDouble(split[1]), Double.parseDouble(split[0]));
		Point2D point2D = new Point2D(doubles[0], doubles[1]);
		GeoPoint oilPointRegion = new GeoPoint(new Point2D(point2D));
		String datebase=maplogInfosMapper.queryDatabase(aircode);
		log.info("获取数据库名称 :"+datebase);
		/*Workspace workspace = new Workspace();
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
		//DatasetVector datasetVector = (DatasetVector) datasource.getDatasets().get(regionName);
		DatasetVector datasetVector =SpaceDatasource.getDataVector(datebase,regionName);
		boolean addfileFlag = false;
		if (datasetVector == null) {
			log.info("未获取到自定义面数据集，将新建数据集");
			DatasetVectorInfo datasetVectorInfo = new DatasetVectorInfo();
			datasetVectorInfo.setName(regionName);
			datasetVectorInfo.setType(DatasetType.POINT);
			datasetVector = datasource.getDatasets().create(datasetVectorInfo);
			SpaceDatasource.putDataVector(datasetVector,datebase,regionName);
			addfileFlag = true;
		}
		/***********************获取regid***********************/
		Map seqmap=new HashMap();
		seqmap.put("sequncename",regionName);
		seqmap.put("pcellname","regid");
		maplogInfosMapper.querySequnce(seqmap);
		/***********************获取regid***********************/
		Map copyParamMap = new HashMap();
		copyParamMap.putAll(paramMap);
		//去除其他参数，只保留需要添加得参数
		copyParamMap.remove("oilPoint");
		copyParamMap.remove("aircode");
		if (addfileFlag) {
			FieldInfo fieldInfo = new FieldInfo("Name", FieldType.TEXT);
			datasetVector.getFieldInfos().add(fieldInfo);
			fieldInfo.dispose();
			fieldInfo = new FieldInfo("NameType", FieldType.TEXT);
			datasetVector.getFieldInfos().add(fieldInfo);
			fieldInfo.dispose();
		}
		Recordset rs = datasetVector.getRecordset(true, CursorType.DYNAMIC);
		rs.edit();//数据集进入编辑状态
		rs.addNew(oilPointRegion);
		Iterator<Map.Entry<String, String>> entries2 = copyParamMap.entrySet().iterator();
		while (entries2.hasNext()) {
			Map.Entry<String, String> entry = entries2.next();
			rs.setFieldValue(entry.getKey(), entry.getValue());
			log.info("添加属性：" + entry.getKey() + "，值为：" + entry.getValue());
		}
		rs.update();//游标修改，相当于commit
		rs.moveNext();//下一条，在此处相当于结束游标。
		rs.dispose();//关闭数据集
		//datasetVector.close();//关闭该数据集的数据
		/*datasource.close();//关闭数据源
		datasourceconnection.dispose();//关闭连接
		workspace.dispose();//关闭工作空间
*/	}

	/**
	 * 删除机位面或者油井
	 * @param paramMap
	 */
	@Override
	public void deleteAirportRegion(Map paramMap) {
		String name = (String) paramMap.get("name");
		String aircode = (String) paramMap.get("aircode");
		String flg = (String) paramMap.get("flg");
		//机位中心点
		String airName = pAircraftName;
		//机位
		String regionName = aParkRegionName;
		//油井
		String poil = poilwell;
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
		String dataname=maplogInfosMapper.queryDatabase(aircode);
		//01删除机位和中心点
		if ("01".equals(flg)) {
			DatasetVector datasetVector =SpaceDatasource.getDataVector(dataname,regionName);
//			DatasetVector datasetVector = (DatasetVector) datasource.getDatasets().get(regionName);
//			DatasetVector airdatasetVector = (DatasetVector) datasource.getDatasets().get(airName);
			DatasetVector airdatasetVector =SpaceDatasource.getDataVector(dataname,airName);

			Recordset rs = datasetVector.query("Name='" + name+"'", CursorType.DYNAMIC);
			Recordset airrs = airdatasetVector.query("Name='" + name+"'", CursorType.DYNAMIC);
			Map copyParamMap = new HashMap();
			copyParamMap.putAll(paramMap);
			copyParamMap.remove("region");
			copyParamMap.remove("name");
			rs.edit();
			airrs.edit();
			rs.delete();
			airrs.delete();
			rs.moveNext();
			airrs.moveNext();
			rs.dispose();
			airrs.dispose();
//			rs.close();
//			airrs.close();
//			datasetVector.close();
//			airdatasetVector.close();
			//02删除油井
		}else if ("02".equals(flg)){
			//DatasetVector datasetVector = (DatasetVector) datasource.getDatasets().get(poil);
			DatasetVector datasetVector=SpaceDatasource.getDataVector(dataname,poil);
			Recordset rs = datasetVector.query("Name='" + name+"'", CursorType.DYNAMIC);
			Map copyParamMap = new HashMap();
			copyParamMap.putAll(paramMap);
			copyParamMap.remove("name");
			rs.edit();
			rs.delete();
			rs.moveNext();
			rs.dispose();
			//rs.close();
			//datasetVector.close();
		}
		/*datasource.close();
		datasourceconnection.dispose();
		workspace.dispose();*/
	}

	/**
	 * 获取机位面
	 * @param aircode
	 * @return
	 */
	@Override
	public Map getAirportRegion(String aircode) {
		return null;
	}

	/**
	 * 修改机位面
	 * @param paramMap
	 */
	@Override
	public void updateAirportRegion(Map paramMap) {
		String aircode = (String) paramMap.get("aircode");
		String regionName = aParkRegionName;
		String airName = pAircraftName;
		String poil = poilwell;
		String flg = (String) paramMap.get("flg");
		String updatetype=(String) paramMap.get("updatetype");
		
		
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
		String dataname=maplogInfosMapper.queryDatabase(aircode);
		String name = (String) paramMap.get("oldName");
		//修改机位
		if ("01".equals(flg)) {
			
			
			switch (updatetype) {
			//修改属性
			case "01":
//				DatasetVector datasetVector = (DatasetVector) datasource.getDatasets().get(regionName);
//				DatasetVector airdatasetVector = (DatasetVector) datasource.getDatasets().get(airName);
				DatasetVector datasetVector =SpaceDatasource.getDataVector(dataname,regionName);
				DatasetVector airdatasetVector =SpaceDatasource.getDataVector(dataname,airName);
				Recordset rs = datasetVector.query("Name='" + name+"'", CursorType.DYNAMIC);
				Recordset airrs = airdatasetVector.query("Name='" + name+"'", CursorType.DYNAMIC);
				rs.edit();
				airrs.edit();
				rs.setFieldValue("Name", paramMap.get("Name"));
				airrs.setFieldValue("Name", paramMap.get("Name"));
				airrs.setFieldValue("farNear", paramMap.get("farNear"));
				airrs.setFieldValue("rotate", paramMap.get("rotate"));
				rs.update(); 
				airrs.update();
				rs.moveNext();
				airrs.moveNext();
				rs.dispose();
				airrs.dispose();
//				rs.close();
//				airrs.close();
//				datasetVector.close();
//				airdatasetVector.close();
				break;
			//修改中心点	
			case "02":	
				//DatasetVector airdatasetVector02 = (DatasetVector) datasource.getDatasets().get(airName);
				DatasetVector airdatasetVector02 =SpaceDatasource.getDataVector(dataname,airName);
				Recordset airrs02 = airdatasetVector02.query("Name='" + name+"'", CursorType.DYNAMIC);
				String airPoint = (String) paramMap.get("airPoint");
				String[] split = airPoint.split(",");
				double[] doubles = Topoint.lonLatToMercator(Double.parseDouble(split[1]), Double.parseDouble(split[0]));
				Point2D point2D = new Point2D(doubles[0], doubles[1]);
				GeoPoint airPointRegion = new GeoPoint(new Point2D(point2D));
				
				airrs02.edit();
				airrs02.setGeometry(airPointRegion);
				airrs02.update(); 
				airrs02.moveNext();
				airrs02.dispose();
//				airrs02.close();
//				airdatasetVector02.close();
				break;
			//修改机位面	
			case "03":	
				String pointmsg = (String) paramMap.get("region");
				GeoRegion geoRegion = GeoRegionUtil.pointToGeoRegion(pointmsg);
				//DatasetVector datasetVector03 = (DatasetVector) datasource.getDatasets().get(regionName);
				DatasetVector datasetVector03 =SpaceDatasource.getDataVector(dataname,airName);
				Recordset rs03 = datasetVector03.query("Name='" + name+"'", CursorType.DYNAMIC);
				rs03.edit();
				rs03.setGeometry(geoRegion);
				rs03.update(); 
				rs03.moveNext();
				rs03.dispose();
//				rs03.close();
//				datasetVector03.close();
				break;
			//同时修改中心点和面	
			case "04":	
				String pointmsg04 = (String) paramMap.get("region");
				GeoRegion geoRegion04 = GeoRegionUtil.pointToGeoRegion(pointmsg04);
				String airPoint04 = (String) paramMap.get("airPoint");
				String[] split04 = airPoint04.split(",");
				double[] doubles04 = Topoint.lonLatToMercator(Double.parseDouble(split04[1]), Double.parseDouble(split04[0]));
				Point2D point2D04 = new Point2D(doubles04[0], doubles04[1]);
				GeoPoint airPointRegion04 = new GeoPoint(new Point2D(point2D04));
				
				
//				DatasetVector datasetVector04 = (DatasetVector) datasource.getDatasets().get(regionName);
//				DatasetVector airdatasetVector04 = (DatasetVector) datasource.getDatasets().get(airName);
				DatasetVector datasetVector04 =SpaceDatasource.getDataVector(dataname,regionName);
				DatasetVector airdatasetVector04 =SpaceDatasource.getDataVector(dataname,airName);
				Recordset rs04 = datasetVector04.query("Name='" + name+"'", CursorType.DYNAMIC);
				Recordset airrs04 = airdatasetVector04.query("Name='" + name+"'", CursorType.DYNAMIC);
				rs04.edit();
				airrs04.edit();
				
				rs04.setGeometry(geoRegion04);
				airrs04.setGeometry(airPointRegion04);
				
				rs04.update(); 
				airrs04.update();
				rs04.moveNext();
				airrs04.moveNext();
				rs04.dispose();
				airrs04.dispose();
//				rs04.close();
//				airrs04.close();
//				datasetVector04.close();
//				airdatasetVector04.close();
				break;		
			default:
				break;
			}
		//油井	
		}else if("02".equals(flg)) {
			
			//DatasetVector airdatasetVector02 = (DatasetVector) datasource.getDatasets().get(poil);
			DatasetVector airdatasetVector02 =SpaceDatasource.getDataVector(dataname,poil);
			Recordset airrs02 = airdatasetVector02.query("Name='" + name+"'", CursorType.DYNAMIC);
			//修改属性
			if("01".equals(updatetype)) {
				airrs02.edit();
				airrs02.setFieldValue("Name", paramMap.get("Name"));
				airrs02.setFieldValue("NameType", paramMap.get("NameType"));
				airrs02.update(); 
				airrs02.moveNext();
			}else if("02".equals(updatetype)) {
				String airPoint = (String) paramMap.get("airPoint");
				String[] split = airPoint.split(",");
				double[] doubles = Topoint.lonLatToMercator(Double.parseDouble(split[1]), Double.parseDouble(split[0]));
				Point2D point2D = new Point2D(doubles[0], doubles[1]);
				GeoPoint airPointRegion = new GeoPoint(new Point2D(point2D));
				airrs02.edit();
				airrs02.setGeometry(airPointRegion);
				airrs02.update(); 
				airrs02.moveNext();
			}
			airrs02.dispose();
//			airrs02.close();
//			airdatasetVector02.close();
		}
		
		/*datasource.close();
		datasourceconnection.dispose();
		workspace.dispose();*/
	}

	/**
	 * 获取油井
	 * @param aircode
	 * @return
	 */
	@Override
	public Map getOilRegion(String aircode) {
		return null;
	}

	/**
	 * 获取单个面详细信息
	 * @param paramMap
	 * @return
	 */
	@Override
	public Map getAirportGeoRegionById(Map paramMap) {
		return null;
	}
}

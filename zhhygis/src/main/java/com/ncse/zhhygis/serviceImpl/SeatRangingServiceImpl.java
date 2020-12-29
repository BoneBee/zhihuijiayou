package com.ncse.zhhygis.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import com.ncse.zhhygis.collect.SpaceDatasource;
import com.ncse.zhhygis.mapper.MaplogInfosMapper;
import com.ncse.zhhygis.service.SeatRangingService;
import com.ncse.zhhygis.utils.baseUtils.RedisUtils;
import com.ncse.zhhygis.utils.projectUtils.GeoRegionUtil;
import com.ncse.zhhygis.utils.projectUtils.Point;
import com.ncse.zhhygis.utils.projectUtils.Topoint;
import com.supermap.analyst.networkanalyst.*;
import com.supermap.data.*;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:  [功能名称]
 * Description:  [测距]
 * Date:  2018/11/30 16 11
 *
 * @author Xugn
 * @version 1.0.0
 */
@Service
public class SeatRangingServiceImpl implements SeatRangingService {

    private final Logger log = Logger.getLogger(this.getClass());
    /*@Autowired
    @Resource(name = "DefaultStringRedisTemplate")
    private StringRedisTemplate defaultStringRedis;*/

    @Resource
    private RedisUtils redisUtils;

    @Value("${routeNetName}")
    private String routeNetName;

    @Value("${draw.aParkRegionName}")
    private String aParkRegionName;

    @Value("${draw.pAircraftName}")
    private String pAircraftName;

    @Value("${draw.functinalRegionName}")
    private String functinalRegionName;

   /* private Workspace workspace;

    private Datasource datasource;*/

    @Value("${supermap.server}")
    private String server;
    @Value("${supermap.user}")
    private String user;
    @Value("${supermap.password}")
    private String password;

    private DatasetVector datasetVector;

    /*private DatasourceConnectionInfo datasourceconnection;*/

    private static String m_nodeID = "SmNodeID";

    private static String m_edgeID = "SmEdgeID";

    private TransportationAnalyst m_analyst;

    ArrayList<Integer> m_barrierNodes;
    ArrayList<Integer> m_barrierEdges;

    private TransportationAnalystResult m_result;

    @Autowired
    private MaplogInfosMapper maplogInfosMapper;

    //根据车辆编号和机位号，获取导航及测距信息
    public Map getSeatRanging(Map map) {
        Map returnMap;
        String aircode=(String)map.get("aircode");
        m_barrierNodes = new ArrayList<Integer>();
        m_barrierEdges = new ArrayList<Integer>();
        returnMap = new HashMap();
      /*  workspace = new Workspace();
        setDatasourceconnection(aircode);
        datasource = workspace.getDatasources().open(datasourceconnection);*/
//        Datasource datasource =SpaceDatasource.mysqlDatasources.get(maplogInfosMapper.queryDatabase(aircode));
//        datasetVector = (DatasetVector) datasource.getDatasets().get(routeNetName);
        DatasetVector datasetVector = SpaceDatasource.getDataVector(maplogInfosMapper.queryDatabase(aircode),routeNetName);
        getRedis(map);//获取reids车辆信息
        getMapAircraft(map);//获取目标机位经纬度
        load(returnMap);//设置网络分析基本环境
        analyst(map,returnMap);//路径分析

        m_analyst.dispose();
        //datasetVector.close();
       /* datasource.close();
        datasourceconnection.dispose();
        workspace.dispose();*/
        return returnMap;
    }

    /**
     * 根据机位号和车辆gps信息，判断是否走错机位（提供客户接口）
     * @param map aircode 机场,carcode 车牌号，aircraft 机位
     * @return
     */
    @Override
    public Map wrongPosition(Map map) {
        Map returnMap;
        String aircode=(String)map.get("aircode");
        String aircraft=(String)map.get("aircraft");
        m_barrierNodes = new ArrayList<Integer>();
        m_barrierEdges = new ArrayList<Integer>();
        returnMap = new HashMap();
        /* Workspace aParkworkspace = new Workspace();
        DatasourceConnectionInfo aParkdatasourceconnection = new DatasourceConnectionInfo();
        aParkdatasourceconnection.setEngineType(EngineType.MYSQL);
        aParkdatasourceconnection.setServer(server);
        aParkdatasourceconnection.setDatabase(maplogInfosMapper.queryDatabase(aircode));
        aParkdatasourceconnection.setUser(user);
        aParkdatasourceconnection.setPassword(password);
        aParkdatasourceconnection.setAlias(user);
        // 打开数据源
        Datasource  aParkdatasource = aParkworkspace.getDatasources().open(aParkdatasourceconnection);*/
//        Datasource datasource =SpaceDatasource.mysqlDatasources.get(maplogInfosMapper.queryDatabase(aircode));
//        DatasetVector aParkVector = (DatasetVector) datasource.getDatasets().get(aParkRegionName);
        DatasetVector aParkVector = SpaceDatasource.getDataVector(maplogInfosMapper.queryDatabase(aircode),aParkRegionName);
        Recordset aParkset = aParkVector.query("Name LIKE'" + aircraft + "%'", CursorType.STATIC);
        GeoRegion aParkregion = (GeoRegion) aParkset.getGeometry();
        String carcode=(String)map.get("carcode");
        String key = "cargps:" + aircode + ":" + carcode;
        String resoult = redisUtils.get(key);
//        String resoult = defaultStringRedis.opsForValue().get(key);
        JSONObject resoultJs = JSONObject.parseObject(resoult);
       /* Double dox =  resoultJs.getDouble("jd");
        Double doy =  resoultJs.getDouble("wd");*/
        double x=0.0, y=0.0;
        //将GPS坐标转换为墨卡托
        if(!StringUtils.isEmpty(resoultJs.getString("jd"))) {
			//x = Double.parseDouble((String) objmap.get("jd"));
			x=GeoRegionUtil.GpsJWStrToDouble(resoultJs.getString("jd"));
		}
		
		if(!StringUtils.isEmpty(resoultJs.getString("wd"))) {
			//y = Double.parseDouble((String) objmap.get("wd"));
			y=GeoRegionUtil.GpsJWStrToDouble(resoultJs.getString("wd"));
		}
		Point p=new Point(x,y);
		Point t=GeoRegionUtil.realGPSToGcj02ll(p);
        
        //boolean istrue = GeoRegionUtil.istrue(dox, doy, aParkregion);
		boolean istrue = GeoRegionUtil.istrue(t.getY(), t.getX(), aParkregion);
        returnMap.put("status",istrue);
        aParkset.dispose();
        //aParkset.close();
        //aParkVector.close();
       /* aParkdatasource.close();
        aParkdatasourceconnection.dispose();
        aParkworkspace.dispose();*/
        return returnMap;
    }

    //获取redis里面车辆信息
    public void getRedis(Map map) {
        String aircode=(String)map.get("aircode");
        String carcode=(String)map.get("carcode");
        String key = "cargps:" + aircode + ":" + carcode;
        String resoult = redisUtils.get(key);
//        String resoult = defaultStringRedis.opsForValue().get(key);
        JSONObject resoultJs = JSONObject.parseObject(resoult);
        double x=0.0, y=0.0;
        //将GPS坐标转换为墨卡托
        if(!StringUtils.isEmpty(resoultJs.getString("jd"))) {
			//x = Double.parseDouble((String) objmap.get("jd"));
			x=GeoRegionUtil.GpsJWStrToDouble(resoultJs.getString("jd"));
		}
		
		if(!StringUtils.isEmpty(resoultJs.getString("wd"))) {
			//y = Double.parseDouble((String) objmap.get("wd"));
			y=GeoRegionUtil.GpsJWStrToDouble(resoultJs.getString("wd"));
		}
		Point p=new Point(x,y);
		Point t=GeoRegionUtil.realGPSToGcj02ll(p);
        //map.put("points",resoultJs.get("wd")+","+resoultJs.get("jd")+";");
		map.put("points", t.getY()+","+t.getX()+";");
        map.put("carHight",resoultJs.get("gd"));
    }

    //打开数据集
   /* public void setDatasourceconnection(String aircode) {
        if (datasourceconnection != null) {
            datasourceconnection.dispose();
        }
        datasourceconnection = new DatasourceConnectionInfo();
        datasourceconnection.setEngineType(EngineType.MYSQL);
        datasourceconnection.setServer(server);
        datasourceconnection.setDatabase(maplogInfosMapper.queryDatabase(aircode));
        datasourceconnection.setUser(user);
        datasourceconnection.setPassword(password);
        datasourceconnection.setAlias(user);
    }*/

    //根据机位id获取机位信息
    public void getMapAircraft(Map map) {
        String aircode=(String)map.get("aircode");
        String aircraft=(String)map.get("aircraft");
//        setDatasourceconnection(aircode);
//        Datasource datasource =SpaceDatasource.mysqlDatasources.get(maplogInfosMapper.queryDatabase(aircode));
//        // 打开数据源，飞机点
//        DatasetVector pAircraftNameVector = (DatasetVector) datasource.getDatasets().get(pAircraftName);
        //判断数据集是否为空
        DatasetVector pAircraftNameVector = SpaceDatasource.getDataVector(maplogInfosMapper.queryDatabase(aircode),pAircraftName);
       /* if (pAircraftNameVector == null) {
            pAircraftNameVector.close();
        }*/
        Recordset prs = pAircraftNameVector.query("Name LIKE'" + aircraft + "%'", CursorType.STATIC);
        Point po = new Point();
        String aircrafts = "";
        for(int i=0;i<prs.getRecordCount();i++) {//获取游标总条数进行for循环
            po.setX(prs.getDouble("SmX"));
            po.setY(prs.getDouble("SmY"));
            Point point = Topoint.mercatorToLonLat(po);
            aircrafts = point.getY()+","+point.getX();
            prs.moveNext();
        }
        map.put("points",map.get("points")+aircrafts);
        prs.dispose();
//        prs.close();
//        pAircraftNameVector.close();
        
    }

    public void load(Map returnMap) {
        try {
            // 设置网络分析基本环境，这一步骤需要设置 分析权重、节点、弧段标识字段、容限
            TransportationAnalystSetting setting = new TransportationAnalystSetting();
            setting.setNetworkDataset(datasetVector);
            setting.setEdgeIDField(m_edgeID);
            setting.setNodeIDField(m_nodeID);
            setting.setEdgeNameField("Name");
            setting.setTolerance(0.001);

            WeightFieldInfos weightFieldInfos = new WeightFieldInfos();
            WeightFieldInfo weightFieldInfo = new WeightFieldInfo();
            weightFieldInfo.setFTWeightField("SmLength");
            weightFieldInfo.setTFWeightField("SmLength");
            weightFieldInfo.setName("length");
            weightFieldInfos.add(weightFieldInfo);
            setting.setWeightFieldInfos(weightFieldInfos);
            setting.setFNodeIDField("SmFNode");
            setting.setTNodeIDField("SmTNode");

            // 构造交通网络分析对象，加载环境设置对象
            m_analyst = new TransportationAnalyst();
            m_analyst.setAnalystSetting(setting);
            m_analyst.load();
        } catch (Exception e) {
            log.info(e.getMessage());
            returnMap.put("msg", e.getMessage());
        }
    }

    /**
     * 进行最短路径分析
     */
    public boolean analyst(Map map, Map returnMap) {
        //try {
        // m_count = 0;
        TransportationAnalystParameter parameter = new TransportationAnalystParameter();
        // 设置障碍点及障碍边
        //setBarrier();//设置路障
        int[] barrierEdges = new int[m_barrierEdges.size()];
        for (int i = 0; i < barrierEdges.length; i++) {
            barrierEdges[i] = m_barrierEdges.get(i);
        }
        parameter.setBarrierEdges(barrierEdges);

        int[] barrierNodes = new int[m_barrierNodes.size()];
        for (int i = 0; i < barrierNodes.length; i++) {
            barrierNodes[i] = m_barrierNodes.get(i);
        }
        parameter.setBarrierNodes(barrierNodes);
        parameter.setWeightName("length");

        // 设置最佳路径分析的返回对象
        String points = (String) map.get("points");
        Point2Ds point2ds = GeoRegionUtil.getPoint2ds(points);
        parameter.setPoints(point2ds);
        parameter.setNodesReturn(true);
        parameter.setEdgesReturn(true);
        parameter.setPathGuidesReturn(true);
        parameter.setRoutesReturn(true);

        // 进行分析并显示结果
        m_result = m_analyst.findPath(parameter, false);
        if (m_result == null) {
            returnMap.put("msg", "路径解析失败");
            return false;
        }
        showResult(returnMap,map);
        return true;
    }

    /**
     * 获取相交管理区域
     */
    public List<Map> getFunctinalRegionName(String aircode) {
        List<Map> functinalrecordList = new ArrayList();
        Map functinalmap;
//        Datasource datasource =SpaceDatasource.mysqlDatasources.get(maplogInfosMapper.queryDatabase(aircode));
//        // 打开数据源，非管理区域
//        DatasetVector functinalVector = (DatasetVector) datasource.getDatasets().get(functinalRegionName);
        DatasetVector functinalVector = SpaceDatasource.getDataVector(maplogInfosMapper.queryDatabase(aircode),functinalRegionName);
        if (functinalVector == null) {
            log.info("未获取到非管理区域信息");
            return null;
        }
        Recordset functinalrecordset = functinalVector.getRecordset(false, CursorType.STATIC);
        for (int t = 0; t < functinalrecordset.getRecordCount(); t++) {
            functinalmap = new HashMap();
            functinalmap.put("geo", (GeoRegion) functinalrecordset.getGeometry());
            functinalmap.put("regname", functinalrecordset.getString("regname"));
            functinalmap.put("regid", functinalrecordset.getString("regid"));
            functinalmap.put("topSpeed", functinalrecordset.getString("topSpeed"));
            functinalmap.put("maxHeight", functinalrecordset.getString("maxHeight"));
            functinalmap.put("maxtime", functinalrecordset.getString("maxtime"));
            functinalrecordList.add(functinalmap);
            functinalrecordset.moveNext();
        }
        functinalrecordset.dispose();
//        functinalrecordset.close();
//        functinalVector.close();
        return functinalrecordList;
    }

    /**
     * 显示结果
     */
    public void showResult(Map returnMap, Map map) {
        try {
            fillResultTable(0,returnMap);
            StringBuffer sb=new StringBuffer();
            for (int i = 0; i < m_result.getRoutes().length; i++) {
                GeoLineM geoLineM = m_result.getRoutes()[0];
                for(int x=0;x<geoLineM.getPartCount();x++) {
                    PointMs pointms=geoLineM.getPart(x);
                    for(int y=0;y<pointms.getCount();y++) {
                        PointM pointM=pointms.getItem(y);
                        sb.append(pointM.getX()).append(",").append(pointM.getY()).append(";");
                        //进行点面相交判断，判断最短路径经过哪些面
                        List<Map> functinalRegionName = getFunctinalRegionName((String)map.get("aircode"));
                        for (Map m : functinalRegionName) {
                            GeoRegion functinalregion = (GeoRegion) m.get("geo");
                            Point p = new Point();
                            p.setX(pointM.getX());
                            p.setY(pointM.getY());
                            Point point = Topoint.mercatorToLonLat(p);
                            boolean istrue = GeoRegionUtil.istrue(point.getX(), point.getY(), functinalregion);
                            if (istrue){
                                double maxHeight = Double.parseDouble((String) m.get("maxHeight"));//非管理区域面车辆最高限额
                                double carHight = Double.parseDouble((String) map.get("carHight"));//车辆高度
                                if (maxHeight<carHight){
                                    returnMap.put("hightStatus", "01");//01代表有超高
                                    break;
                                }
                            }
                        }
                        if ("".equals(returnMap.get("hightStatus"))||returnMap.get("hightStatus")==null){
                            returnMap.put("hightStatus", "00");//00代表没有超高
                        }
                    }
                }
            }
            returnMap.put("points", sb.toString());
        } catch (Exception e) {
            log.info(e.getMessage());
            returnMap.put("msg", e.getMessage());
        }
    }

    /**
     * 对结果表进行填充
     */
    public  void fillResultTable(int pathNum,Map returnMap) {
        try {
            // 清除原数据，添加初始点信息
            // 得到行驶导引对象，根据导引子项类型的不同进行不同的填充
            PathGuide[] pathGuides = m_result.getPathGuides();
            PathGuide pathGuide = pathGuides[pathNum];
            double distanct=0.0;
            for (int j = 1; j < pathGuide.getCount(); j++) {
                PathGuideItem item = pathGuide.get(j);
                // 导引子项为站点的添加方式
                if (item.isStop()) {
                    distanct +=item.getDistance();
                }
                // 导引子项为弧段的添加方式
                if (item.isEdge()) {
                    distanct +=item.getLength();
                }
            }
            //根据路径规划，返回距离。
            returnMap.put("totalDistance", distanct);
        } catch (Exception e) {
            log.info(e.getMessage());
            returnMap.put("msg", e.getMessage());
        }
    }

    /***
     * MethodName:  [getAirRanging]
     * Description:  [后台管理测距方法]
     * @param: [map]
     * @return: java.util.Map
     */
    public Map getAirRanging(Map map) throws Exception {
        List m_barrierNodes = new ArrayList<Integer>();
        List m_barrierEdges = new ArrayList<Integer>();
        TransportationAnalyst m_analyst_s = new TransportationAnalyst();
        Map returnMap;
        String aircode=(String)map.get("aircode");
        String beginxy=(String)map.get("beginxy");
        String endxy=(String)map.get("endxy");
        map.put("points",beginxy+";"+endxy);
        returnMap = new HashMap();
       /* Workspace workspaceRanging = new Workspace();
        DatasourceConnectionInfo datasourceconnectionRanging = new DatasourceConnectionInfo();
        datasourceconnectionRanging.setEngineType(EngineType.MYSQL);
        datasourceconnectionRanging.setServer(server);
        datasourceconnectionRanging.setDatabase(maplogInfosMapper.queryDatabase(aircode));
        datasourceconnectionRanging.setUser(user);
        datasourceconnectionRanging.setPassword(password);
        datasourceconnectionRanging.setAlias(user);
        Datasource datasourceRanging = workspaceRanging.getDatasources().open(datasourceconnectionRanging);*/
//        Datasource datasource =SpaceDatasource.mysqlDatasources.get(maplogInfosMapper.queryDatabase(aircode));
//        DatasetVector datasetVectorRanging = (DatasetVector) datasource.getDatasets().get(routeNetName);
        DatasetVector datasetVectorRanging = SpaceDatasource.getDataVector(maplogInfosMapper.queryDatabase(aircode),routeNetName);
//设置网络分析基本环境
        // 设置网络分析基本环境，这一步骤需要设置 分析权重、节点、弧段标识字段、容限
        TransportationAnalystSetting setting = new TransportationAnalystSetting();
        setting.setNetworkDataset(datasetVectorRanging);
        setting.setEdgeIDField(m_edgeID);
        setting.setNodeIDField(m_nodeID);
        setting.setEdgeNameField("Name");
        setting.setTolerance(0.001);

        WeightFieldInfos weightFieldInfos = new WeightFieldInfos();
        WeightFieldInfo weightFieldInfo = new WeightFieldInfo();
        weightFieldInfo.setFTWeightField("SmLength");
        weightFieldInfo.setTFWeightField("SmLength");
        weightFieldInfo.setName("length");
        weightFieldInfos.add(weightFieldInfo);
        setting.setWeightFieldInfos(weightFieldInfos);
        setting.setFNodeIDField("SmFNode");
        setting.setTNodeIDField("SmTNode");

        // 构造交通网络分析对象，加载环境设置对象
        m_analyst_s.setAnalystSetting(setting);
        m_analyst_s.load();

//路径分析
        TransportationAnalystParameter parameter = new TransportationAnalystParameter();
        // 设置障碍点及障碍边
        //setBarrier();//设置路障
        int[] barrierEdges = new int[m_barrierEdges.size()];
        for (int i = 0; i < barrierEdges.length; i++) {
            barrierEdges[i] = (int) m_barrierEdges.get(i);
        }
        parameter.setBarrierEdges(barrierEdges);

        int[] barrierNodes = new int[m_barrierNodes.size()];
        for (int i = 0; i < barrierNodes.length; i++) {
            barrierNodes[i] = (int) m_barrierNodes.get(i);
        }
        parameter.setBarrierNodes(barrierNodes);
        parameter.setWeightName("length");

        // 设置最佳路径分析的返回对象
        String points = (String) map.get("points");
        Point2Ds point2ds = GeoRegionUtil.getPoint2ds(points);
        parameter.setPoints(point2ds);
        parameter.setNodesReturn(true);
        parameter.setEdgesReturn(true);
        parameter.setPathGuidesReturn(true);
        parameter.setRoutesReturn(true);
        // 进行分析并显示结果
        TransportationAnalystResult m_result = m_analyst_s.findPath(parameter, false);
        if (m_result == null) {
            //m_result.dispose();
            m_analyst_s.dispose();
            //datasetVectorRanging.close();
           /* datasourceRanging.close();
            datasourceconnectionRanging.dispose();
            workspaceRanging.dispose();*/
            returnMap.put("msg", "路径解析失败");
        }

 // 对结果表进行填充
        // 清除原数据，添加初始点信息
        // 得到行驶导引对象，根据导引子项类型的不同进行不同的填充
        PathGuide[] pathGuides = m_result.getPathGuides();
        PathGuide pathGuide = pathGuides[0];
        double distanct=0.0;
        for (int j = 1; j < pathGuide.getCount(); j++) {
            PathGuideItem item = pathGuide.get(j);
            // 导引子项为站点的添加方式
            if (item.isStop()) {
                distanct +=item.getDistance();
            }
            // 导引子项为弧段的添加方式
            if (item.isEdge()) {
                distanct +=item.getLength();
            }
        }
        //根据路径规划，返回距离。
        returnMap.put("totalDistance", distanct);
//        StringBuffer sb=new StringBuffer();
        List pointList =new ArrayList();
        for (int i = 0; i < m_result.getRoutes().length; i++) {
            GeoLineM geoLineM = m_result.getRoutes()[0];
            for(int x=0;x<geoLineM.getPartCount();x++) {
                PointMs pointms=geoLineM.getPart(x);
                for(int y=0;y<pointms.getCount();y++) {
                    Point p = new Point();
                    PointM pointM=pointms.getItem(y);
                    p.setX(pointM.getX());
                    p.setY(pointM.getY());
                    Point point = Topoint.mercatorToLonLat(p);
//                    sb.append(pointM.getX()).append(",").append(pointM.getY()).append(";");
                    if (point!=null) {
                        pointList.add(String.valueOf(point.getY()) + "," + String.valueOf(point.getX()));
                    }
                }
            }
        }
        returnMap.put("points", pointList);
        returnMap.put("code", "0");

        m_result.dispose();
        m_analyst_s.dispose();
        //datasetVectorRanging.close();
        /*datasourceRanging.close();
        datasourceconnectionRanging.dispose();
        workspaceRanging.dispose();*/
        return returnMap;
    }

    /***
     * MethodName:  [方法名称]
     * Description:  [测试电面相交]
     * @param: [map] x 经度，y 纬度
     * @return: java.lang.Boolean
     */
   /* @Override
    public Boolean testceju(Map map) {
        List list=new ArrayList<>();
        //code保留，应该不同的数据库或者不同数据集，待以后修改
        Workspace workspace=new Workspace();
        //Datasources datasources=workspace.getDatasources();
        DatasourceConnectionInfo datasourceconnection = new DatasourceConnectionInfo();
        datasourceconnection.setEngineType(EngineType.MYSQL);
        datasourceconnection.setServer(server);
        datasourceconnection.setDatabase(maplogInfosMapper.queryDatabase((String) map.get("aircode")));
        datasourceconnection.setUser(user);
        datasourceconnection.setPassword(password);
        datasourceconnection.setAlias(user);
        // 打开数据源
        Datasource datasource = workspace.getDatasources().open(datasourceconnection);
        List<Map> functinalrecordList = new ArrayList();
        Map functinalmap;
        // 打开数据源，非管理区域
        DatasetVector functinalVector = (DatasetVector) datasource.getDatasets().get(functinalRegionName);
        if (functinalVector == null) {
            log.info("未获取到非管理区域信息");
            functinalVector.close();
        }
        Recordset functinalrecordset = functinalVector.getRecordset(false, CursorType.STATIC);
        for (int t = 0; t < functinalrecordset.getRecordCount(); t++) {
            functinalmap = new HashMap();
            functinalmap.put("geo", (GeoRegion) functinalrecordset.getGeometry());
            functinalmap.put("regname", functinalrecordset.getString("regname"));
            functinalmap.put("regid", functinalrecordset.getString("regid"));
            functinalmap.put("topSpeed", functinalrecordset.getString("topSpeed"));
            functinalmap.put("maxHeight", functinalrecordset.getString("maxHeight"));
            functinalmap.put("maxtime", functinalrecordset.getString("maxtime"));
            functinalrecordList.add(functinalmap);
            functinalrecordset.moveNext();
        }
        Boolean resoultBoolen = false;
        GeoRegion functinalregion;
        String regname;
        for (Map m : functinalrecordList) {
            functinalregion = (GeoRegion) m.get("geo");
            regname = (String) m.get("regname");
            Double x = Double.parseDouble((String) map.get("x"));
            Double y = Double.parseDouble((String) map.get("y"));
            if (regname.equals("map")) {
                resoultBoolen = GeoRegionUtil.istrue(x, y, functinalregion);
                if (resoultBoolen) {
                    double maxHeight = Double.parseDouble((String) m.get("maxHeight"));//非管理区域面车辆最高限额
                    double carHight = 1;//车辆高度
                    if (maxHeight < carHight) {
                        resoultBoolen = true;
                        System.out.println("有超高的======================");
                        break;
                    }
                }
            }
        }
        return resoultBoolen;
    }*/
}

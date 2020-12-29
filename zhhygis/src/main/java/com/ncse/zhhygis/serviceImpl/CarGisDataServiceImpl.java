package com.ncse.zhhygis.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ncse.zhhygis.collect.KafkaProServer;
import com.ncse.zhhygis.collect.UserLoginService;
import com.ncse.zhhygis.collect.WebSocketServer;
import com.ncse.zhhygis.mapper.MaplogInfosMapper;
import com.ncse.zhhygis.service.CarGisDataService;
import com.ncse.zhhygis.utils.baseUtils.DateUtil;
import com.ncse.zhhygis.utils.baseUtils.JSONUtil;
import com.ncse.zhhygis.utils.baseUtils.RedisUtils;
import com.ncse.zhhygis.utils.projectUtils.GeoRegionUtil;
import com.ncse.zhhygis.utils.projectUtils.Point;
import com.supermap.data.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

@Service
public class CarGisDataServiceImpl implements CarGisDataService {

    private final Logger log = Logger.getLogger(this.getClass());

    @Value("${supermap.server}")
    private String server;
    @Value("${supermap.user}")
    private String user;
    @Value("${supermap.password}")
    private String password;

    @Value("${draw.functinalRegionName}")
    private String functinalRegion;

    @Value("${consumer.keyreal}")
    private String real;

    @Autowired
    private MaplogInfosMapper maplogInfosMapper;

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private KafkaProServer kafkaProServer;

    @Autowired
    private RedisUtils redisUtils;

    @Resource
    private Datasource zhhydatasource;

    @Autowired
    private ExecutorService executorService;

    @Override
    public void carGisDataHandle(List<ConsumerRecord<?, ?>> records) throws IOException {
        //1.将取得所有消息入库
        for (ConsumerRecord<?, ?> consumerRecord : records) {
            //2.解析最新一条msg
            String dataType = String.valueOf(consumerRecord.key());
            String gisData = String.valueOf(consumerRecord.value());
            //3.转换坐标
            JSONObject gisJson = parsePoint(gisData);
            //4.判定航班编号
            String aircode = Objects.requireNonNull(gisJson).getString("airport");
            if (StringUtils.isEmpty(aircode) || StringUtils.isEmpty(aircode.trim())) {
                throw new RuntimeException("aircode is null");
            }
            //5.更新redis加油车信息
            JSONObject jsonData = updateCarData(gisJson);
            executorService.execute(() -> {
                /*String eleType = String.valueOf(consumerRecord.key());
                String eleData = String.valueOf(consumerRecord.value());
                JSONObject object = parsePoint(eleData);
                JSONObject carData = updateCarData(object);*/
                if (jsonData != null && jsonData.getDoubleValue("jd") != 0 && jsonData.getDoubleValue("wd") != 0) {
                    newAlarmData(jsonData, dataType);
                }
            });
            //6.推送页面更新信息
            if ((real.equals(dataType))) { //如果最新数据，推送到前端
                //推送到前端
                WebSocketServer.sendInfo(JSON.toJSONString(jsonData), aircode + "carGis");
            }
        }
    }

    private JSONObject parsePoint(String gisData) {
        JSONObject gisJson = JSON.parseObject(gisData);
        double x = 0.0, y = 0.0;
        if (!StringUtils.isEmpty(gisJson.getString("jd"))) {
            /*if (gisJson.getString("jd").equals("0.0")) {
                return null;
            }*/
            //x = Double.parseDouble((String) objmap.get("jd"));
            x = GpsJWStrToDouble(gisJson.getString("jd"));
        }

        if (!StringUtils.isEmpty(gisJson.getString("wd"))) {
            /*if (gisJson.getString("wd").equals("0.0")) {
                return null;
            }*/
            //y = Double.parseDouble((String) objmap.get("wd"));
            y = GpsJWStrToDouble(gisJson.getString("wd"));
        }
        //原坐标需转gcj02
        Point p = new Point(x, y);
        Point t = GeoRegionUtil.realGPSToGcj02ll(p);
        if (t != null) {
            x = t.getX();
            y = t.getY();
        }
        log.info("=======================转换后的经纬度为:x=" + x + ",y=" + y);
        if (x == 0 || y == 0) {
            log.error("[经纬度为0] 错误数据为 = " + gisData);
        }
        gisJson.put("jd", x);
        gisJson.put("wd", y);
        return gisJson;
    }

    private JSONObject updateCarData(JSONObject gisJson) {
        String aircode = gisJson.getString("airport");
        String carNum = gisJson.getString("hp");
        //获取存储的状态信息
        String redisJson = redisUtils.get(aircode + "carStatus" + carNum);
        String carStatus;
        JSONObject jsondata;
        if (StringUtils.isEmpty(redisJson)) {
            //carStatus="1";
            jsondata = new JSONObject();
            jsondata.put("carStatus", "1");
        } else {
            redisUtils.remove(aircode + "carStatus" + carNum);//先删除redis数据
            jsondata = JSON.parseObject(redisJson);
            //carStatus=jsondata.getString("carStatus");
        }
        //String carStatus = StringUtils.isEmpty(redisUtils.get("carStatus"+carNum)) ? "1" : redisUtils.get("carStatus"+carNum);
        //将获取的数据重新更新到redis
        //gisJson.put("carStatus",carStatus);
        //状态数据更新
        jsondata.putAll(gisJson);
        redisUtils.set(aircode + "carStatus" + carNum, JSON.toJSONString(jsondata), 600L);//汽车数据
        String keys = redisUtils.get(aircode + "carStatusKey");//获取汽车数据所有key
        if (StringUtils.isEmpty(keys)) {
            keys = carNum;
        }
        if (redisUtils.exists(aircode + "carStatusKey")) {
            redisUtils.remove(aircode + "carStatusKey");//先删除redis汽车key数据
            if (keys.indexOf(carNum) < 0) {
                keys += carNum + ",";
            }
        } else {
            keys = carNum;
        }
        redisUtils.set(aircode + "carStatusKey", keys, 600L);
        log.info("修改车辆GPS信息：当前在redis还存在的车辆有：" + keys);
        return jsondata;
    }

    @Override
    public String carGisDataHandle(String gisdata, String datatype) throws IOException {

        JSONObject gisJson = JSON.parseObject(gisdata);

        double x = 0.0, y = 0.0;
        if (!StringUtils.isEmpty(gisJson.getString("jd"))) {
            if (gisJson.getString("jd").equals("0.0")) {
                return null;
            }
            //x = Double.parseDouble((String) objmap.get("jd"));
            x = GpsJWStrToDouble(gisJson.getString("jd"));
        }

        if (!StringUtils.isEmpty(gisJson.getString("wd"))) {
            if (gisJson.getString("wd").equals("0.0")) {
                return null;
            }
            //y = Double.parseDouble((String) objmap.get("wd"));
            y = GpsJWStrToDouble(gisJson.getString("wd"));
        }
        log.info("=======================转换后的经纬度为:x=" + x + ",y=" + y);
        Point p = new Point(x, y);
        Point t = GeoRegionUtil.realGPSToGcj02ll(p);
        if (t != null) {
            x = t.getX();
            y = t.getY();
        }
        log.info("=======================转换后的经纬度为:x=" + x + ",y=" + y);
        gisJson.put("jd", x);
        gisJson.put("wd", y);

        String aircode = gisJson.getString("airport");

        if (StringUtils.isEmpty(aircode) || StringUtils.isEmpty(aircode.trim())) {
            return null;
        }


        String carNum = gisJson.getString("hp");
        //获取存储的状态信息
        String redisJson = redisUtils.get(aircode + "carStatus" + carNum);
        String carStatus;
        JSONObject jsondata;
        if (StringUtils.isEmpty(redisJson)) {
            //carStatus="1";
            jsondata = new JSONObject();
            jsondata.put("carStatus", "1");
        } else {
            redisUtils.remove(aircode + "carStatus" + carNum);//先删除redis数据
            jsondata = JSON.parseObject(redisJson);
            //carStatus=jsondata.getString("carStatus");

        }
        //String carStatus = StringUtils.isEmpty(redisUtils.get("carStatus"+carNum)) ? "1" : redisUtils.get("carStatus"+carNum);
        //将获取的数据重新更新到redis
        //gisJson.put("carStatus",carStatus);

        //状态数据更新
        jsondata.putAll(gisJson);

        redisUtils.set(aircode + "carStatus" + carNum, JSON.toJSONString(jsondata), 600L);//汽车数据
        String keys = redisUtils.get(aircode + "carStatusKey");//获取汽车数据所有key
        if (StringUtils.isEmpty(keys)) {
            keys = carNum;
        }
        if (redisUtils.exists(aircode + "carStatusKey")) {
            redisUtils.remove(aircode + "carStatusKey");//先删除redis汽车key数据
            if (keys.indexOf(carNum) < 0) {
                keys += carNum + ",";
            }
        } else {
            keys = carNum;
        }

        redisUtils.set(aircode + "carStatusKey", keys, 600L);

        log.info("修改车辆GPS信息：当前在redis还存在的车辆有：" + keys);

        if ((real.equals(datatype))) { //如果最新数据，推送到前端
            //推送到前端
            WebSocketServer.sendInfo(JSON.toJSONString(jsondata), aircode + "carGis");
        }


        if (x != 0 && y != 0) {
            /***************************多线程处理报警信息*************************/
			/*AramLogThread aramLogThread=new AramLogThread();
			aramLogThread.setJson(gisJson);
			aramLogThread.setDatatype(datatype);
			new Thread(aramLogThread).start();*/

            alarmData(gisJson, datatype);
            /*****************************************************/
        }
        return gisdata;
    }


    private synchronized List<Map> getRegionList(String aircode) {
        List<Map> airList = new ArrayList();
		/*Workspace workspace = new Workspace();
		
		DatasourceConnectionInfo datasourceconnection = new DatasourceConnectionInfo();
		datasourceconnection.setEngineType(EngineType.MYSQL);
		datasourceconnection.setServer(server);
		datasourceconnection.setDatabase(maplogInfosMapper.queryDatabase(aircode));
		datasourceconnection.setUser(user);
		datasourceconnection.setPassword(password);
		datasourceconnection.setAlias(user);
		// 打开数据源
		Datasource datasource;
		try {
			datasource = workspace.getDatasources().open(datasourceconnection);
			log.info("连接空间数据库成功："+aircode);
		}catch (Exception e) {
			log.info("未连接上空间数据库：传入的aircode是："+aircode);
			datasourceconnection.dispose();
			workspace.dispose();
			return null;
		}*/
        //使用内存数据集

        DatasetVector datasetVector = (DatasetVector) zhhydatasource.getDatasets().get(maplogInfosMapper.queryDatabase(aircode));
        if (datasetVector == null) {
            log.info("未获取到非管理区域信息");
			/*datasource.close();
			datasourceconnection.dispose();
			workspace.dispose();*/
            return airList;

        }
        Recordset recordset = datasetVector.getRecordset(false, CursorType.STATIC);
        if (recordset == null) {
            return null;
        }

        Map map;

        /*************************将面信息取出放入缓存，因为需要多次使用**********************/
        for (int t = 0; t < recordset.getRecordCount(); t++) {
            map = new HashMap();
            map.put("geo", (GeoRegion) recordset.getGeometry());
            map.put("regname", recordset.getString("regname"));
            map.put("regid", recordset.getString("regid"));
            map.put("topSpeed", recordset.getString("topSpeed"));
            map.put("maxHeight", recordset.getString("maxHeight"));
            map.put("maxtime", recordset.getString("maxtime"));
            airList.add(map);
            recordset.moveNext();
        }
        /*************************将面信息取出放入缓存************************************/
        if (recordset != null) {
            recordset.close();
        }
        if (datasetVector != null) {
            datasetVector.close();
        }
		
		/*datasource.close();
		datasourceconnection.dispose();
		workspace.dispose();*/
        return airList;
    }


    private void alarmData(JSONObject gisdata, String datatype) {
        String aircode = gisdata.getString("airport");
        List<Map> airList = getRegionList(aircode);
        List<Map> yjlist = new ArrayList(), yjaddlist, // 越界日志,
                cslist = new ArrayList(), // 超速日志
                cglist = new ArrayList(),// 超高日志
                tempyjlist, tempcslist, tempcglist;//临时过渡使用
        Map<String, List> warnning = new HashMap();  //存放最新的报警信息
        List warnninglist, gpsList = new ArrayList();//报警信息列表,轨迹信息
        Map qcMap = new HashMap(), qcMaplist = new HashMap(), logMap = new HashMap();
        //是否在该区域，是否存在该区域的数据
        boolean yjflag, insertinfo = false;
        //限制速度，实际速度，限制高度，实际高度
        double speed, sjspeed, carheight, sjcarheight = 0.0;
        //kafka传过来的速度，获取中间件
        String[] data_num;
        //车牌号，时间中间层，进出标志，数据库存在的进出标志
        String cph, time, istrue, logistrue;
        //面对象
        GeoRegion region;


        long armcount;
        //获取缓存的汽车信息
        Map<String, Map> carBaseinfo = userLoginService.getCarBaseInfo(aircode);// 汽车基本信息
        if (carBaseinfo == null) {
            carBaseinfo = new HashMap();
        }
        //数据库存在的区域信息，区域信息，用于写入数据库以及中间层使用
        Map carIntoAreaMap, yjaddmap;
        //获取数据库信息参数
        Map carInfoParamMap = new HashMap();
        //Map test01 = (HashMap)super.clone();
        carInfoParamMap.put("aircode", aircode);

        Map objmap = JSONUtil.reflect(gisdata);

        warnninglist = new ArrayList();
        tempyjlist = new ArrayList();
        tempcslist = new ArrayList();
        tempcglist = new ArrayList();
        yjaddlist = new ArrayList();
        List timeoutaddlist = new ArrayList();

        Map hpcarMap = carBaseinfo.get(objmap.get("hp"));
        objmap.put("carnum", objmap.get("hp"));
        objmap.put("updatetime", objmap.get("timeStamp"));
        objmap.put("isonline", real.equals(datatype) ? "1" : "0");//0离线，1在线
        objmap.put("ischeck", "0");//未参与计算
        if (hpcarMap != null) {
            objmap.putAll(carBaseinfo.get(objmap.get("hp"))); // 将汽车的基本信息加入
        }
        //坐标
        double x = gisdata.getDoubleValue("jd"), y = gisdata.getDoubleValue("wd");
        objmap.put("x", x);
        objmap.put("y", y);
        List datalist = (List) objmap.get("num_data");
        sjspeed = 0;
        if (datalist.size() >= 3) {
            sjspeed = ((BigDecimal) datalist.get(2)).doubleValue();
        }

        if (!StringUtils.isEmpty(objmap.get("vehiHeight"))) {
            sjcarheight = Double.parseDouble((String) objmap.get("vehiHeight"));
        }


        objmap.put("sjspeed", sjspeed);
        objmap.put("aircode", aircode);
        objmap.put("alarmsign", "0");
        carInfoParamMap.put("carnum", objmap.get("hp"));

        // 遍历飞机点集合，判断点面相交，如果是，加入机位号。
        for (Map m : airList) {
            insertinfo = false;
            region = (GeoRegion) m.get("geo");
            yjflag = GeoRegionUtil.istrue(x, y, region);

            carInfoParamMap.put("regid", m.get("regid"));
            objmap.put("regid", m.get("regid"));

            objmap.put("isalarm", "0");

            carIntoAreaMap = maplogInfosMapper.queryyjinfo(carInfoParamMap);//获取汽车在当前区域状态
            if (carIntoAreaMap == null) {   //如果没有数据，需要新增
                insertinfo = true;
            }
            /**
             * 判断超时，当前处理方式是10分钟一次处理离线数据，我们获取最新数据的时候进行判断
             * 在11分钟内是否存在轨迹数据，如果不存在，表示离线，则不进行报警处理，如果有数据，
             * 则需要进行判断。进出区域的报警和超时报警，都根据这个来进行提醒。
             */
            boolean sendLog = maplogInfosMapper.isSendAlarmLog(objmap) > 0 ? true : false;
            logMap = maplogInfosMapper.queryyjlog(objmap);
            // 判断点面相交
            if (yjflag) {
                /******************** 1.超速判断 ***********************************/

                if (!StringUtils.isEmpty(m.get("topSpeed"))) {
                    speed = Double.parseDouble((String) m.get("topSpeed"));
                    if (sjspeed > speed) {

                        objmap.put("limitSpeed", speed);
                        objmap.put("regname", m.get("regname"));// 添加超速的面名称
                        objmap.put("warnningType", "speedlimit");
                        objmap.put("alarmsign", "1");
                        tempcslist.add(objmap);
                    }
                }

                /******************** 1.超速判断 ***********************************/
                /******************** 2.限高判断 ***********************************/
                if (!StringUtils.isEmpty(m.get("maxHeight"))) {
                    carheight = Double.parseDouble((String) m.get("maxHeight"));
                    if (sjcarheight > carheight) {
                        objmap.put("limitHeight", carheight);
                        objmap.put("regname", m.get("regname"));// 添加超速的面名称
                        objmap.put("warnningType", "heightlimit");
                        objmap.put("alarmsign", "1");
                        tempcglist.add(objmap);
                    }
                }

                /******************** 2.限高判断 ***********************************/
                /******************** 3.跨界判断 ***********************************/
                istrue = "1";//进出标志，1进入，2出
                //判断超时
                /**
                 * 判断超时，当前处理方式是10分钟一次处理离线数据，我们获取最新数据的时候进行判断
                 * 在11分钟内是否存在轨迹数据，如果不存在，表示离线，则不进行报警处理，如果有数据，
                 * 则需要进行判断。进出区域的报警和超时报警，都根据这个来进行提醒。
                 */

                if (!insertinfo && real.equals(datatype) && sendLog) { //如果存在状态数据并且是最新数据并且存在数据
                    logistrue = (String) carIntoAreaMap.get("istrue");//获取当前汽车状态
                    String maxtime = (String) m.get("maxtime");//获取面超时时间
                    if (!StringUtils.isEmpty(maxtime)) {
                        if ("1".equals(logistrue)) { //如果当前还是进入标志

                            String timecon = String.valueOf(DateUtil.getDistanceTimes((String) carIntoAreaMap.get("updatetime"), (String) objmap.get("timeStamp"), "min"));

                            if (maxtime.compareTo(timecon) <= 0) { //如果时间超过，加入报警信息

//								logMap=maplogInfosMapper.queryyjlog(objmap);
                                if (logMap == null) {
                                    log.info("出现异常，进去区域未查询到");
                                    return;
                                }
                                armcount = (long) logMap.get("armcount");
                                if (armcount == 0) {
                                    yjaddmap = new HashMap();
                                    yjaddmap.putAll(objmap);
                                    yjaddmap.put("timecon", timecon);
                                    yjaddmap.put("maxtime", maxtime);
                                    yjaddmap.put("intotime", carIntoAreaMap.get("updatetime"));
                                    yjaddmap.put("outtime", objmap.get("timeStamp"));
                                    yjaddmap.put("regname", m.get("regname"));
                                    yjaddmap.put("warnningtype", "timeover");
                                    yjaddmap.put("intoarealogid", logMap.get("id"));
                                    timeoutaddlist.add(yjaddmap);
                                }
                            }
                        }
                    }

                }

                /******************** 3.跨界判断 ***********************************/

            } else {
                /******************** 3.跨界判断 ***********************************/
                istrue = "2";//进出标志，1进入，2出
                /******************** 3.跨界判断 ***********************************/
            }

            if (real.equals(datatype) && sendLog) {     //如果是最新数据并且11分钟以内存在数据，才进行判断
                yjaddmap = new HashMap();
                yjaddmap.putAll(objmap);
                yjaddmap.put("aircode", aircode);
                yjaddmap.put("istrue", istrue);
                yjaddmap.put("x", x);
                yjaddmap.put("y", y);
                yjaddmap.put("driver", objmap.get("driver"));
                yjaddmap.put("carnum", (String) objmap.get("hp"));
                yjaddmap.put("regid", m.get("regid"));
                yjaddmap.put("regname", m.get("regname"));
                //yjaddmap.put("updatetime", objmap.get("updatetime"));
                if (insertinfo) {//如果没有数据，则写入
                    yjaddmap.put("warnningtype", "arealimit");
                    objmap.put("alarmsign", "1");
                    yjaddlist.add(yjaddmap);
                    tempyjlist.add(yjaddmap);
                } else {   //存在数据，则判断进出标志是否为一样，一样就需要修改 ，否则不修改
                    logistrue = (String) carIntoAreaMap.get("istrue");
                    if (!istrue.equals(logistrue)) { //如果是一样，不需要修改，否则需要修改
                        if ("2".equals(istrue)) {//如果是出，计算耗时
                            String timecon = String.valueOf(DateUtil.getDistanceTimes((String) carIntoAreaMap.get("updatetime"), (String) objmap.get("timeStamp"), "min"));
                            yjaddmap.put("timecon", timecon);
                        }
                        yjaddmap.put("warnningtype", "arealimit");
                        yjaddmap.put("updatetime", objmap.get("timeStamp"));
                        objmap.put("alarmsign", "1");
                        if (logMap != null && logMap.size() > 0) {
                            yjaddmap.put("id", carIntoAreaMap.get("id"));
                            maplogInfosMapper.updateyjinfo(yjaddmap);
                            maplogInfosMapper.updateyjinfoTemp(yjaddmap);
                        }
                        tempyjlist.add(yjaddmap);
                    }
                }
            }


        }
        if (yjaddlist.size() > 0) {
            maplogInfosMapper.addyjinfo(yjaddlist);  //写入汽车在区域的状态
        }

        cslist.addAll(tempcslist);
        yjlist.addAll(tempyjlist);
        cglist.addAll(tempcglist);
        gpsList.add(objmap);

        if (yjlist.size() > 0) {
            maplogInfosMapper.addyjloginfo(yjlist);//进出区域报警写入数据库
        }

        //超速报警写入数据库
        if (cslist.size() > 0) {
            maplogInfosMapper.addspeedAlarm(cslist);
        }
        //超高报警写入数据库
        if (cglist.size() > 0) {
            maplogInfosMapper.addheightAlarm(cglist);
        }


        //超时报警写入数据库
        if (timeoutaddlist.size() > 0) {
            maplogInfosMapper.addAreaAlarminfo(timeoutaddlist);
        }
        //轨迹信息
        maplogInfosMapper.addTrajcetory(gpsList);


        List valueList = new ArrayList(qcMaplist.values());

        //最新的数据的报警信息
        //List kfkWarnning=new ArrayList(warnning.values());
        List kfkWarnning = new ArrayList();
        kfkWarnning.addAll(cslist);
        kfkWarnning.addAll(yjlist);
        kfkWarnning.addAll(cglist);
        kfkWarnning.addAll(timeoutaddlist);
        if (kfkWarnning.size() > 0 && (real.equals(datatype))) {
            //发送到kafka
            kafkaProServer.sendKafka(kfkWarnning);
            //报警信息推送到前端
            try {
                WebSocketServer.sendInfo(JSON.toJSONString(kfkWarnning), aircode + "carWarnning");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                log.info("推送报警信息出错：" + e.getMessage());
                e.printStackTrace();
            }
        }

    }

    private void  newAlarmData(JSONObject gisdata, String datatype) {
        //1.获取机场编号
        String aircode = gisdata.getString("airport");
        //2.获取机位面数据
        List<Map> airList = getRegionList(aircode);
        //3.获取缓存的汽车信息
        Map<String, Map> carBaseinfo = userLoginService.getCarBaseInfo(aircode) != null ? userLoginService.getCarBaseInfo(aircode) : new ConcurrentHashMap<>();
        //4.解析参数信息
        //Map objmap = JSONUtil.reflect(gisdata);
        Map<String, Object> objmap = JSONObject.parseObject(gisdata.toJSONString(), new TypeReference<Map<String, Object>>() {
        });
        //5.封装通用objmap
        objmap.put("carnum", objmap.get("hp"));
        objmap.put("updatetime", objmap.get("timeStamp"));
        objmap.put("isonline", real.equals(datatype) ? "1" : "0");//0离线，1在线
        objmap.put("ischeck", "0");//未参与计算
        Map hpcarMap = carBaseinfo.get(objmap.get("hp"));
        if (hpcarMap != null) {
            objmap.putAll(hpcarMap); // 将汽车的基本信息加入
        }
        double x = gisdata.getDoubleValue("jd"), y = gisdata.getDoubleValue("wd");
        objmap.put("x", x);
        objmap.put("y", y);
        List datalist = (List) objmap.get("num_data");
        double sjspeed = datalist.size() >= 3 ? ((BigDecimal) datalist.get(2)).doubleValue() : 0,
                sjcarheight = 0.0, speed, carheight;
//        if (!StringUtils.isEmpty(objmap.get("vehiHeight"))) {
//            sjcarheight = Double.parseDouble((String) objmap.get("vehiHeight"));
//        }
        //kafka车辆高度字段为gd
        if (!StringUtils.isEmpty(objmap.get("gd"))) {
            sjcarheight = Double.parseDouble((String) objmap.get("gd"));
        }
        objmap.put("sjspeed", sjspeed);
        objmap.put("aircode", aircode);
        objmap.put("alarmsign", "0");
        objmap.put("isalarm", "0");
        objmap.put("driver", objmap.get("name"));
        //6.判断机位面是否点面相交
        List<Map> yjlist = new CopyOnWriteArrayList<>(), cslist = new CopyOnWriteArrayList<>(), cglist = new CopyOnWriteArrayList<>(),
                timeoutaddlist = new CopyOnWriteArrayList<>(), yjaddlist = new CopyOnWriteArrayList<>();
        //用于接收超时时间
        List<Map>carOutTimeList = new CopyOnWriteArrayList<>();
        String istrue, logistrue;
        for (Map airData : airList) {
            GeoRegion region = (GeoRegion) airData.get("geo");
            objmap.put("regid", airData.get("regid"));
            Map carIntoAreaMap = maplogInfosMapper.queryyjinfo(objmap);//获取汽车在当前区域状态
            boolean insertinfo = carIntoAreaMap == null;
            boolean sendLog = maplogInfosMapper.isSendAlarmLog(objmap) > 0;
            Map logMap = maplogInfosMapper.queryyjlog(objmap);
            if (GeoRegionUtil.istrue(x, y, region)) {
                //超速报警
                if (!StringUtils.isEmpty(airData.get("topSpeed"))) {
                    speed = Double.parseDouble((String) airData.get("topSpeed"));
                    if (sjspeed > speed) {
                        Map<String, Object> speedMap = new HashMap<>();
                        speedMap.putAll(objmap);
                        speedMap.put("limitSpeed", speed);
                        speedMap.put("regname", airData.get("regname"));// 添加超速的面名称
                        speedMap.put("warnningType", "speedlimit");
                        speedMap.put("alarmsign", "1");
                        if (real.equals(datatype)) speedMap.put("isalarm", "1");//报警标识
                        cslist.add(speedMap);
                    }
                }
                //超高判定
                if (!StringUtils.isEmpty(airData.get("maxHeight"))) {
                    carheight = Double.parseDouble((String) airData.get("maxHeight"));
                    if (sjcarheight > carheight) {
                        Map<String, Object> heightMap = new HashMap<>();
                        heightMap.putAll(objmap);
                        heightMap.put("limitHeight", carheight);
                        heightMap.put("regname", airData.get("regname"));// 添加超速的面名称
                        heightMap.put("warnningType", "heightlimit");
                        heightMap.put("alarmsign", "1");//shifou
                        if (real.equals(datatype)) heightMap.put("isalarm", "1");//报警标识
                        heightMap.put("vehiHeight", sjcarheight);//实际高度
                        cglist.add(heightMap);
                    }
                }
                istrue = "1";//进出标志，1进入，2出
                if (!insertinfo && real.equals(datatype) && sendLog) { //如果存在状态数据并且是最新数据并且存在数据
                    logistrue = (String) carIntoAreaMap.get("istrue");//获取当前汽车状态
                    String maxtime = (String) airData.get("maxtime");//获取面超时时间
                    if (!StringUtils.isEmpty(maxtime)) {
                        if ("1".equals(logistrue)) { //如果当前还是进入标志
                            String timecon = String.valueOf(DateUtil.getDistanceTimes((String) carIntoAreaMap.get("updatetime"),
                                    (String) objmap.get("timeStamp"), "min"));
                            if (Double.valueOf(maxtime) <= Double.valueOf(timecon)) { //如果时间超过，加入报警信息
                                if (logMap == null) {
                                    log.info("出现异常，进去区域未查询到");
                                    return;
                                }
                                long armcount = (long) logMap.get("armcount");
                                if (armcount == 0) {
                                    Map<String, Object> timeoutMap = new HashMap<>();
                                    timeoutMap.putAll(objmap);
                                    timeoutMap.put("timecon", timecon);
                                    timeoutMap.put("maxtime", maxtime);
                                    timeoutMap.put("intotime", carIntoAreaMap.get("updatetime"));
                                    timeoutMap.put("outtime", objmap.get("timeStamp"));
                                    timeoutMap.put("regname", airData.get("regname"));
                                    timeoutMap.put("warnningtype", "timeover");
                                    timeoutMap.put("intoarealogid", logMap.get("id"));
                                    timeoutaddlist.add(timeoutMap);
                                }
                            }
                        }
                    }
                }
            } else {
                istrue = "2";
            }
            //新增判断超时报警进出区域查询
            if (real.equals(datatype) && sendLog){//当前车辆没在区域内
                if (!insertinfo && "2".equals(istrue)){//车辆没在区域里面
                    logistrue = (String) carIntoAreaMap.get("istrue");//获取上一次数据
                    if ("1".equals(logistrue)){//上一次再区域中,这一次再外面
                        //只查询报警过得车辆出区域的最近时间
                        Map<String, Object> mapOutTime = new HashMap<>();
                        mapOutTime.putAll(objmap);
                        mapOutTime.put("regname",airData.get("regname"));
                        Map  carOutTimeMap = maplogInfosMapper.getCarOutTime(mapOutTime);

                            if (carOutTimeMap != null && carOutTimeMap.size()>0){
                                //查询 表carTimeInfo 当前车辆进去时间是否已存在，如果存在则不新增
                                int count  = maplogInfosMapper.getMinOutTime(carOutTimeMap);
                                if (count < 1) {
                                    //判断是否是最新的
                                    Map<String, Object> carOutTimeAddMap = new HashMap<>();
                                    carOutTimeAddMap.put("aircode",carOutTimeMap.get("aircode"));
                                    carOutTimeAddMap.put("carnum",carOutTimeMap.get("carnum"));
                                    carOutTimeAddMap.put("intotime",carOutTimeMap.get("intotime"));
                                    carOutTimeAddMap.put("outtime",objmap.get("timeStamp"));
                                    String timecon = String.valueOf(DateUtil.getDistanceTimes((String) carOutTimeMap.get("intotime"),
                                            (String) objmap.get("timeStamp"), "min"));
                                    carOutTimeAddMap.put("regname",carOutTimeMap.get("regname"));
                                    carOutTimeAddMap.put("timecon",timecon);
                                    carOutTimeAddMap.put("isonline",objmap.get("isonline"));
                                    carOutTimeAddMap.put("maxtime",carOutTimeMap.get("maxtime"));
                                    carOutTimeAddMap.put("intoarealogid",carOutTimeMap.get("intoarealogid"));
                                    carOutTimeAddMap.put("driver",carOutTimeMap.get("driver"));
                                    //数据入库
                                    carOutTimeList.add(carOutTimeAddMap);
                                }

                            }


                    }

                }

            }
            //如果是最新数据并且11分钟以内存在数据，才进行判断
            if (real.equals(datatype) && sendLog) {
                Map<String, Object> yjMap = new HashMap<>();
                yjMap.putAll(objmap);
                yjMap.put("aircode", aircode);
                yjMap.put("istrue", istrue);
                yjMap.put("x", x);
                yjMap.put("y", y);
                yjMap.put("carnum", objmap.get("hp"));
                yjMap.put("regid", airData.get("regid"));
                yjMap.put("regname", airData.get("regname"));
                //如果没有数据，并且当前在区域内则写入一条数据
                if (insertinfo && "1".equals(istrue)) {
                    yjMap.put("warnningtype", "arealimit");
                    objmap.put("alarmsign", "1");
                    yjaddlist.add(yjMap);
                    yjlist.add(yjMap);
                } else if (!insertinfo) {   //存在数据
                    logistrue = (String) carIntoAreaMap.get("istrue");//获取上一次数据
                    if (!istrue.equals(logistrue)) { //如果是一样，不需要修改，否则需要修改
                        if ("2".equals(istrue)) {//如果是出，计算耗时
                            String timecon = String.valueOf(DateUtil.getDistanceTimes((String) carIntoAreaMap.get("updatetime"), (String) objmap.get("timeStamp"), "min"));
                            yjMap.put("timecon", timecon);
                        }
                        yjMap.put("warnningtype", "arealimit");
                        objmap.put("alarmsign", "1");
                        if (logMap != null && logMap.size() > 0) {
                            yjMap.put("id", carIntoAreaMap.get("id"));
                            maplogInfosMapper.updateyjinfo(yjMap);
                            maplogInfosMapper.updateyjinfoTemp(yjMap);
                        }
                        yjlist.add(yjMap);
                    }
                }
            }
        }
        //轨迹信息
        List<Map> gpsList = new CopyOnWriteArrayList<>();
        gpsList.add(objmap);
        //以下写入数据库的均增加 isonline 字段。0离线1在线
        //新增轨迹日志信息
        maplogInfosMapper.addTrajcetory(gpsList);
        if (yjaddlist.size() > 0) {
            maplogInfosMapper.addyjinfo(yjaddlist);  //写入汽车在区域的状态
        }
        if (yjlist.size() > 0) {
            maplogInfosMapper.addyjloginfo(yjlist);//进出区域报警写入数据库
        }
        //超速报警写入数据库
        if (cslist.size() > 0) {
            maplogInfosMapper.addspeedAlarm(cslist);
        }
        //超高报警写入数据库
        if (cglist.size() > 0) {
            maplogInfosMapper.addheightAlarm(cglist);
        }
        //超时报警写入数据库
        if (timeoutaddlist.size() > 0) {
            maplogInfosMapper.addAreaAlarminfo(timeoutaddlist);
        }
        //超时报警耗时入库
        if (carOutTimeList.size()>0){
            maplogInfosMapper.insertCarOutTime(carOutTimeList);
        }
        //最新的数据的报警信息
        List<Map> kfkWarnning = new CopyOnWriteArrayList<>();
        kfkWarnning.addAll(cslist);
        kfkWarnning.addAll(yjlist);
        kfkWarnning.addAll(cglist);
        kfkWarnning.addAll(timeoutaddlist);
        if (kfkWarnning.size() > 0 && (real.equals(datatype))) {
            //发送到kafka
            kafkaProServer.sendKafka(kfkWarnning);
            //报警信息推送到前端
            try {
                WebSocketServer.sendInfo(JSON.toJSONString(kfkWarnning), aircode + "carWarnning");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                log.error("推送报警信息出错：" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 日期比较
     *
     * @param date1
     * @param date2
     * @return
     */
    public boolean compareDate(String date1, String date2) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d1 = dateFormat.parse(date1);
            Date d2 = dateFormat.parse(date2);
            if (d1.before(d2)) {

                return true;

            } else if (d1.after(d2)) {

                return false;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            return false;
        }
        return false;
    }

    /**
     * 重写排序方法，由于kafka传过来数据是乱的，对日期进行排序
     *
     * @author Administrator
     */
    class SortByDraw implements Comparator {
        public int compare(Object o1, Object o2) {
            Map s1 = (Map) o1;
            Map s2 = (Map) o2;
            String date1 = (String) s1.get("timeStamp");
            String date2 = (String) s1.get("timeStamp");
            return compareDate(date1, date2) ? -1 : 1;

        }
    }

    class SortByDraw2 implements Comparator {
        public int compare(Object o1, Object o2) {
            Map s1 = (Map) o1;
            Map s2 = (Map) o2;

            String car1 = (String) s1.get("CARNUM");
            String car2 = (String) s2.get("CARNUM");
            int cr = 0;
            int a = car1.compareTo(car2);
            if (a != 0) {
                cr = a;
            } else {
                String date1 = (String) s1.get("CREATETIME");
                String date2 = (String) s2.get("CREATETIME");
                cr = compareDate(date1, date2) ? -1 : 1;
            }

            return cr;
        }
    }

    /**
     * 计算离线数据
     *
     * @param aircodeList
     */
    public void offlinecheck(List<String> aircodeList) {
        List<Map> offlist, regionList, loginfoList = new ArrayList<>(), areaalarmList = new ArrayList<>(); //未处理数据，面列表
        String updatetime, carnum; //修改时间，车牌号
        Map paramMap, carIntoAreaMap, addMap, logMap;//查询参数map，汽车区域状态map,增加map
        double x, y;
        boolean isinto;//是否相交
        GeoRegion region;//面对象
        String istrue, istruedata, maxtime;//进入进出标志，1进入，2出,耗时
        long armcount;
        for (String aircode : aircodeList) {
            offlist = maplogInfosMapper.getOfflinedata(aircode);
            if (offlist == null || offlist.size() == 0) {
                continue;
            }
            //Collections.sort(offlist, new SortByDraw2());//对数据进行车牌号，日期进行排序
            regionList = getRegionList(aircode); //获取面对象

            for (Map carm : offlist) { //遍历汽车
                paramMap = new HashMap();
                updatetime = (String) carm.get("UPDATETIME");
                carnum = (String) carm.get("CARNUM");
                paramMap.put("carnum", carnum);
                paramMap.put("aircode", aircode);
                paramMap.put("updatetime", updatetime);
                x = Double.parseDouble((String) carm.get("LONGITUDE"));
                y = Double.parseDouble((String) carm.get("LATITUDE"));

                carm.put("carnum", carnum);
                carm.put("aircode", aircode);
                for (Map regMap : regionList) { //遍历面
                    maxtime = (String) regMap.get("maxtime");
                    paramMap.put("regid", regMap.get("regid"));
                    paramMap.put("regname", regMap.get("regname"));
                    carIntoAreaMap = maplogInfosMapper.queryyjinfo(paramMap);//获取汽车在当前区域状态

                    if (carIntoAreaMap == null) { //如果不存在，则增加一条数据进去
                        maplogInfosMapper.addtemplog(paramMap);
                        carIntoAreaMap = maplogInfosMapper.queryyjinfo(paramMap);
                    }
                    istruedata = (String) carIntoAreaMap.get("istrue");
                    region = (GeoRegion) regMap.get("geo");
                    isinto = GeoRegionUtil.istrue(x, y, region);
                    if (isinto) {
                        istrue = "1";
                    } else {
                        istrue = "2";
                    }

                    addMap = new HashMap();
                    addMap.putAll(carm);
                    addMap.putAll(carIntoAreaMap);
                    addMap.putAll(paramMap);

                    addMap.put("istrue", istrue);
                    addMap.put("x", x);
                    addMap.put("y", y);
                    addMap.put("isonline", "0");
                    addMap.put("alarmsign", "1");
                    logMap = maplogInfosMapper.queryyjlog(paramMap);
                    if (!istruedata.equals(istrue)) {  //如果状态不同，不进行处理

                        if ("2".equals(istrue)) {//如果是出，计算耗时
                            String timecon = String.valueOf(DateUtil.getDistanceTimes((String) carIntoAreaMap.get("UPDATETIME"), updatetime, "min"));
                            addMap.put("timecon", timecon);
                        }
                        addMap.put("warnningtype", "arealimit");
                        addMap.put("updatetime", updatetime);

                        addMap.put("id", carIntoAreaMap.get("id"));
                        maplogInfosMapper.updateyjinfo(addMap);
                        loginfoList.add(addMap);
                    } else {
                        if (!StringUtils.isEmpty(maxtime)) {
                            if ("1".equals(istrue)) {
                                String timecon = String.valueOf(DateUtil.getDistanceTimes((String) carIntoAreaMap.get("updatetime"), updatetime, "min"));
                                if (maxtime.compareTo(timecon) <= 0) { //如果时间超过，加入报警信息


                                    if (logMap == null) {
                                        log.error("出现异常，进去区域日出未查询到");
                                        return;
                                    }
                                    armcount = (long) logMap.get("ARMCOUNT");
                                    if (armcount == 0) {
                                        addMap.put("timecon", timecon);
                                        addMap.put("maxtime", maxtime);
                                        addMap.put("intotime", carIntoAreaMap.get("updatetime"));
                                        addMap.put("outtime", updatetime);
                                        addMap.put("warnningtype", "timeover");
                                        addMap.put("intoarealogid", logMap.get("ID"));
                                        areaalarmList.add(addMap);
                                    }
                                }
                            }
                        }
                    }
                }
            }


            //修改轨迹表计算标志
            //maplogInfosMapper.updateTrajectory(offlist);
            maplogInfosMapper.updateTrajectory2(offlist);
        }

        //记录超时表
        if (areaalarmList.size() > 0) {
            maplogInfosMapper.addAreaAlarminfo(areaalarmList);
        }
        //记录区域日志表
        if (loginfoList.size() > 0) {
            maplogInfosMapper.addyjloginfo(loginfoList);
        }

        //清除临时表数据
        maplogInfosMapper.removeTemp(null);

    }

    //监听油量改变量，并推送给前端
    @Override
    public void oilChange(String value, String key) {
        try {
            WebSocketServer.sendInfo(value, "oilchange");
            log.info("推送油量改变量，kafaka的key是=" + key + "oilchange");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("推送油量改变量失败");
        }
    }


    //kafka转经纬度坐标转换
    public double GpsJWStrToDouble(String str) {
        double res = 0;// 结果
        String s = "";
        int i;
        for (i = 0; i < str.length(); i++) {
            if (str.charAt(i + 2) == '.') {
                try {
                    res = Integer.parseInt(s);
                } catch (Exception e) {
                    res = 0;
                }
                break;
            } else {
                s = s + str.charAt(i);
            }
        }
        s = "";
        for (int j = i; j < str.length(); j++) {
            s = s + str.charAt(j);
        }
        try {
            res = res + Double.valueOf(s) / 60;

        } catch (Exception e) {
            res = 0;
        }
        return res;
    }


    /**
     * 多线程处理报警事宜。
     *
     * @author Administrator
     */
    class AramLogThread implements Runnable {

        JSONObject json;

        String datatype;

        public void setJson(JSONObject json) {
            this.json = json;
        }

        public void setDatatype(String datatype) {
            this.datatype = datatype;
        }

        @Override
        public void run() {
            alarmData(json, datatype);
        }

    }


}

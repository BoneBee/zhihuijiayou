package com.ncse.zhhygis.utils.baseUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:  [MessageUtils]
 * Description:  [返回信息]
 * Date:  2018/11/10 13 50
 *
 * @author Xugn
 * @version 1.0.0
 */
public class MessageUtils {

    //失败
    public static final String CONFIG_IS_ERRO = "操作失败";
    public static final String AIR_CODE_NOTNULLJCDM = "机场代码不能为空";
    public static final String AIR_CODE_NOTNULLJWDM = "机位代码不能为空";
    public static final String AIR_CODE_NOTNULLFJID = "飞机ID不能为空";
    //成功
    public static final String CONFIG_ADD_SUCCESS = "添加成功";
    public static final String CONFIG_UPDATE_SUCCESS = "修改成功";
    //飞机信息接口
    public static final String INTERFACE_AIRGETALLINFOS_81 = "/flight/flightController/getFlightList";
    //汽车信息接口
    public static final String INTERFACE_CARGETALLINFOS_80 = "/base/vehiController/getVehiList";
    //机位信息接口
    public static final String INTERFACE_AIRPORTINFO_81 = "/flight/recpt/flightlist";
    //单独飞机信息
	public static final String INTERFACE_AIRFLIGHTDETAILS_81 = "/flight/recpt/FlightDetails";
    
    //统计：航班量
    public static final String INTERFACE_TJ_AIRCOUNT_81 = "/flight/flightController/getFlightAmount";
    public static final String INTERFACE_TJ_AIRCOUNT_NAME = "airCount";

    //统计：保障量
    public static final String INTERFACE_TJ_TASKAMCOUNT_81 = "/flight/taskController/getTaskAmount";
    public static final String INTERFACE_TJ_TASKAMCOUNT_NAME = "taskAmCount";
    //统计：油单
    public static final String INTERFACE_TJ_SOULCOUNT_81 = "/flight/recpt/total";
    public static final String INTERFACE_TJ_SOULCOUNT_NAME = "soulCount";
    //统计：已供油量
    public static final String INTERFACE_TJ_ALOIL_81 = "/flight/recpt/fightOil";
    public static final String INTERFACE_TJ_ALOIL_NAME = "alOil";
    //统计：车辆
    public static final String INTERFACE_TJ_VEHISUM_80 = "/base/vehiController/getVehiSum";
    public static final String INTERFACE_TJ_VEHISUM_NAME = "vehiSum";
   
    
    
    //统计：油量参数
    public static final String INTERFACE_TJ_OILFUEL_80 = "/base/fuelopt/oilfuel";
    //退出
    public static final String INTERFACE_TJ_SIGNOUT_80 = "/base/staffController/PCremoveStaff";
    
    //人员详情,跟以前一样的，不带token，base  21001
    public static final String INTERFACE_RYXQ_80="/base/staffvehi/staffonefind";
    
    //任务列表，数据传入格式不同，flight 21002，{"loginUserIn":{"staffId":"22023"}}
    public static final String INTERFACE_RWLB_81="/flight/recpt/staffTask";
    
    //接口返回代码
    public static final String INTERFACE_CODE = "0"; 
    
    //越界
    public static final String ALARM_YJ="YJ";
    //超速
    public static final String ALARM_CS="CS";
    
    
    public static final Map taskStatus= new HashMap() {{    
        put("0", "未下发");    
        put("1", "待接受");  
        put("2", "申请待批（预留）");    
        put("3", "已接受");  
        put("4", "到位（预留）");    
        put("5", "加油完成");  
        put("6", "油单待审核（预留）");    
        put("7", "任务完成");  
        put("8", "拒绝（预留）");    
        put("9", "不加油");  
    }};  
    
} 

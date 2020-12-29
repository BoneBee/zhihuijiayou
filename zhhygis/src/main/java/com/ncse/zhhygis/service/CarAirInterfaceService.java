package com.ncse.zhhygis.service;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ncse.zhhygis.entity.OilEntity;
import com.zh.bean.login.MyStaff;

public interface CarAirInterfaceService {




    /**
     * 飞机信息接口
     * @param aircode  机场号
     * @return
     */
    String airGetAllInfos(String aircode,String token) throws IOException;

    /**
     * 汽车信息接口
     * @param aircode
     * @return
     */
    String carGetAllInfos(String aircode,String token) throws IOException;

    /**
     * 汽车信息接口（后台）
     * @param aircode
     * @return
     */
    String carAllInfosSys(String aircode,String token) throws IOException;

    /**
     * 机位信息接口
     * @param aircode
     * @param placecode
     * @return
     */
    String airportInfo(String aircode,String placecode,String token) throws IOException;

    /**
     * 飞机详细信息
     * @param aircode
     * @param placecode
     * @return
     */
    String airFlightdetails(String aircode,String aircraftID,String flightNA,String flightND,String token) throws IOException;

    /**
     * 统计航班量接口
     * @param aircode
     * @return
     */
    String tjAirCount(MyStaff mystaff,String token) throws IOException;

    /**
     * 统计保障量接口
     * @param aircode
     * @return
     */
    String tjTaskamCount(MyStaff mystaff,String token) throws IOException;
    /**
     * 统计油单接口
     * @param aircode
     * @return
     */
    String tjSoulCount(MyStaff mystaff,String token) throws IOException;
    /**
     * 统计以供油量接口
     * @param aircode
     * @return
     */
    String tjAlOil(MyStaff mystaff,String token) throws IOException;
    /**
     * 统计车辆接口
     * @param aircode
     * @return
     */
    String tjVehiSum(MyStaff mystaff,String token) throws IOException;
    /**
     * 统计油量参数接口
     * @param aircode
     * @return
     */
    String tjOilFuel(MyStaff mystaff,String token) throws IOException;

    /**
     * 退出
     * @param aircode
     * @return
     */
    String signgOut(Map map,String token) throws IOException;

    /**
     * 人员详情
     * @param aircode
     * @return
     */
    String ryxq(String staffId) throws IOException;

    /**
     * 任务列表
     * @param aircode
     * @return
     */
    String rwlb(String staffId) throws IOException;

}

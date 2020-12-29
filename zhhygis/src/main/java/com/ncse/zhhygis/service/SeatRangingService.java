package com.ncse.zhhygis.service;

import java.util.Map;

/**
 * ClassName:  [功能名称]
 * Description:  [测距]
 * Date:  2018/11/30 16 10
 *
 * @author Xugn
 * @version 1.0.0
 */
public interface SeatRangingService {

    //根据车辆编号和机位号，获取导航及测距信息
    public Map getSeatRanging(Map map);

    //根据机位号和车辆gps信息，判断是否走错机位（提供客户接口）
    public Map wrongPosition(Map map);

    //后台管理测距方法
    public Map getAirRanging(Map map) throws Exception;
}

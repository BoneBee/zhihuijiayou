package com.ncse.zhhygis.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 地图相关数据库操作
 */
@Mapper
public interface MaplogInfosMapper {
	/**
	 * 根据机场查询 数据库信息
	 * @param aircode
	 * @return
	 */
    String queryDatabase(String aircode);
    
    /**
     * 查询汽车在某个区域的信息
     * @param m
     * @return
     */
    Map queryyjinfo(Map m);
    
    /**
     * 修改汽车在某个区域的信息
     * @param m
     * @return
     */
    void updateyjinfo(Map m);
    
    /**
     * 修改汽车在某个区域的信息
     * @param m
     * @return
     */
    void updateyjinfoTemp(Map m);
    
    /**
     * 新增汽车区域信息
     * @param m
     * @return
     */
    void addyjinfo(List<Map> list);
    
    /**
     * 新增汽车进出区域日志信息
     * @param m
     * @return
     */
    void addyjloginfo(List<Map> list);
    /**
     * 新增限高报警日志信息
     * @param m
     * @return
     */
    void addheightAlarm(List<Map> list);
    /**
     * 新增超速日志信息
     * @param m
     * @return
     */
    void addspeedAlarm(List<Map> list);
    
    /**
     * 新增轨迹日志信息
     * @param m
     * @return
     */
    void addTrajcetory(List<Map> list);
    
    /**
     * 新增超时报警信息
     * @param m
     * @return
     */
    void addAreaAlarminfo(List<Map> list);
    
    /**
     * 查询离线数据
     * @param m
     * @return
     */
    List<Map> getOfflinedata(String aircode);
    
    /**
     * 查询汽车在某个区域的信息（离线数据处理）
     * @param m
     * @return
     */
    Map queryyjinfoOffline(Map m);
    
    /**
     * 插入临时汽车区域状态表
     * @param m
     */
    void addtemplog(Map m);
    
    /**
     * 清理临时数据
     * @param m
     */
    void removeTemp(Map m);
    
    /**
     * 修改轨迹计算标志
     * @param m
     */
    void updateTrajectory(List<Map> list);
    
    /**
     * 修改轨迹计算标志
     * @param m
     */
    void updateTrajectory2(List list);
    
    /**
     * 查询汽车的进出区域日志记录，用于判断是否已经超时报警
     * @param m
     * @return
     */
    Map queryyjlog(Map m);
    
    /**
     * 判断该车在11分钟以内是否存在数据
     * @param m
     * @return
     */
    int isSendAlarmLog(Map m);
    
    /**
     * 获取面ID，通过存储过程
     * @param map
     */
    void querySequnce(Map map);
    
    
    List<String> queryAllDatabase();

    //查询汽车超时报警最近的进入时间
    Map getCarOutTime(Map<String, Object> objmap);
    //超时报警写入数据库
    void insertCarOutTime(List<Map> list);
    //查询最小的出去时间
    int getMinOutTime(Map<String, Object> objmap);


}
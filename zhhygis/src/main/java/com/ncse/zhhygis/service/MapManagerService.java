package com.ncse.zhhygis.service;


import java.util.List;
import java.util.Map;

/**
 * 关于地图方面相关操作
 * @author Administrator
 *
 */
public interface MapManagerService {
    
	/**
	 * 面添加
	 * @param paramMap
	 */
    void geoRegionAdd(Map paramMap);
    /**
     * 获取机位面
     * @param code
     * @return
     */
    Map getAParkGeoRegion(String code);
    
    
    /**
     * 获取油井
     * @param code
     * @return
     */
    Map getOilRegion(String code);
    
    Map getAParkGeoRegion2(String code);
    /**
     * 修改面，主要修改自定义面
     * @param paramMap
     */
    void updateRegion(Map paramMap);
    
    /**
     * 获取自定义面
     * @param code
     * @return
     */
    Map getCustomGeoRegion(String code);

    /**
     * 获取所有面ID，名称等熟悉信息
     * @param code
     * @return
     */
    Map getCustomGeoAttr(String code);

    /**
     * 获取单个面详细信息
     * @param code
     * @return
     */
    Map getCustomGeoRegionById(Map paramMap);

    /**
     * 删除自定义面
     * @param paramMap
     */
    void deleteRegion(Map paramMap);

    /**
     * 获取自定义区域面信息(管理区域信息，提供客户接口)
     * @param code
     * @return
     */
    Map getManageGeoRegion(String code);
    /**
     * @Author: zhangweixia
     * @Description:   获取油管线json数据
     * @Date：Created in 16:49 2019/12/17
     * @Modified：
     */
    Map getOilWellLine(String aircode) throws Exception;
}
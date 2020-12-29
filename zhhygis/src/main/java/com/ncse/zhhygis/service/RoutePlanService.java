package com.ncse.zhhygis.service;

import java.util.Map;

/**
 * 导航测距
 * @author Administrator
 *
 */
public interface RoutePlanService {
	/**
	 * 获取导航测距信息
	 * @param map
	 * @return
	 */
	public Map findPath(Map map);
}

package com.ncse.zhhygis.serviceImpl;


import com.ncse.zhhygis.entity.CarTrajectoryInfos;
import com.ncse.zhhygis.mapper.CarTrajectoryInfosMapper;
import com.ncse.zhhygis.service.CarTrajectoryInfosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 车辆轨迹信息
 */
@Service(value = "carTrajectoryInfosService")
public class CartrajectoryInfosServiceImpl implements CarTrajectoryInfosService {

    @Autowired
    private CarTrajectoryInfosMapper carTrajectoryInfosMapper;

    @Override
    public CarTrajectoryInfos selectByPrimaryKey(Integer id) {
        CarTrajectoryInfos carTrajectoryInfos = carTrajectoryInfosMapper.selectByPrimaryKey(id);
        return carTrajectoryInfos;
    }

    /***
     * MethodName:  [selectByParms]
     * Description:  [根据条件查询车辆轨迹信息]
     * @param: [pramsMap]
     * @return: java.util.List<com.ncse.zhhygis.entity.CarTrajectoryInfos>
     */
    @Override
    public List<CarTrajectoryInfos> selectByParms(Map<String,String> pramsMap) {
        List<CarTrajectoryInfos> carTrajectoryInfos = carTrajectoryInfosMapper.selectByParms(pramsMap);
        return carTrajectoryInfos;
    }

    /***
     * MethodName:  [deleteByDate]
     * Description:  [定时删除历史记录]
     * @param: [pramsMap]
     * @return: java.util.List<com.ncse.zhhygis.entity.CarTrajectoryInfos>
     */
    @Override
    public int deleteByDate(String updatetime) {
        int i = carTrajectoryInfosMapper.deleteByDate(updatetime);
        return i;
    }
}

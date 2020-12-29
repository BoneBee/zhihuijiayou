package com.ncse.zhhygis.serviceImpl;

import com.ncse.zhhygis.entity.SpeedAlarmInfos;
import com.ncse.zhhygis.mapper.SpeedAlarmInfosMapper;
import com.ncse.zhhygis.service.SpeedAlarmInfosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * ClassName:  [SpeedAlarmInfosServiceImpl]
 * Description:  [超速报警信息]
 * Date:  2018/11/16 10 31
 *
 * @author Xugn
 * @version 1.0.0
 */
@Service(value = "speedAlarmInfosService")
@Transactional(propagation = Propagation.NEVER)
public class SpeedAlarmInfosServiceImpl implements SpeedAlarmInfosService{

    @Autowired
    private SpeedAlarmInfosMapper speedAlarmInfosMapper;

    @Override
    public SpeedAlarmInfos selectByPrimaryKey(String id) {
        return speedAlarmInfosMapper.selectByPrimaryKey(id);
    }

    /***
     * MethodName:  [addSpeedAlarmInfos]
     * Description:  [添加超速报警信息]
     * @param: [speedAlarmInfos]
     * @return: int
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public int addSpeedAlarmInfos (SpeedAlarmInfos speedAlarmInfos){
        return speedAlarmInfosMapper.insertSelective(speedAlarmInfos);
    }

    /***
     * MethodName:  [selectByParms]
     * Description:  [根据条件查询超速报警信息]
     * @param: [pramsMap]
     * @return: java.util.List<com.ncse.zhhygis.entity.CarTrajectoryInfos>
     */
    @Override
    public List<SpeedAlarmInfos> selectByParms(Map<String,String> pramsMap) {
        List<SpeedAlarmInfos> speedAlarmInfos = speedAlarmInfosMapper.selectByParms(pramsMap);
        return speedAlarmInfos;
    }
}

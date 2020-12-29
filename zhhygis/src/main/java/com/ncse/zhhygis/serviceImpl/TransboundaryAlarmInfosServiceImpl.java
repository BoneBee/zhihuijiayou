package com.ncse.zhhygis.serviceImpl;

import com.ncse.zhhygis.entity.TransboundaryAlarmInfos;
import com.ncse.zhhygis.mapper.TransboundaryAlarmInfosMapper;
import com.ncse.zhhygis.service.TransboundaryAlarmInfosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * ClassName:  [功能名称]
 * Description:  [越界报警信息]
 * Date:  2018/11/16 10 34
 *
 * @author Xugn
 * @version 1.0.0
 */
@Service(value = "TransboundaryAlarmInfosService")
@Transactional(propagation = Propagation.NEVER)
public class TransboundaryAlarmInfosServiceImpl implements TransboundaryAlarmInfosService{

    @Autowired
    private TransboundaryAlarmInfosMapper transboundaryAlarmInfosMapper;

    @Override
    public TransboundaryAlarmInfos selectByPrimaryKey(String id) {
        return transboundaryAlarmInfosMapper.selectByPrimaryKey(id);
    }

    /***
     * MethodName:  [addTransboundaryAlarmInfos]
     * Description:  [添加越界报警信息]
     * @param: [transboundaryAlarmInfos]
     * @return: int
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public int addTransboundaryAlarmInfos (TransboundaryAlarmInfos transboundaryAlarmInfos){
        return transboundaryAlarmInfosMapper.insertSelective(transboundaryAlarmInfos);
    }

    /***
     * MethodName:  [selectByParms]
     * Description:  [根据条件查询超速报警信息]
     * @param: [pramsMap]
     * @return: java.util.List<com.ncse.zhhygis.entity.CarTrajectoryInfos>
     */
    @Override
    public List<TransboundaryAlarmInfos> selectByParms(Map<String,String> pramsMap) {
        List<TransboundaryAlarmInfos> transboundaryAlarmInfos = transboundaryAlarmInfosMapper.selectByParms(pramsMap);
        return transboundaryAlarmInfos;
    }
}

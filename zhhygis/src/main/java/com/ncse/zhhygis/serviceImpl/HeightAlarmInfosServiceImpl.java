package com.ncse.zhhygis.serviceImpl;

import com.ncse.zhhygis.entity.HeightAlarmInfos;
import com.ncse.zhhygis.entity.TransboundaryAlarmInfos;
import com.ncse.zhhygis.mapper.HeightAlarmInfosMapper;
import com.ncse.zhhygis.mapper.TransboundaryAlarmInfosMapper;
import com.ncse.zhhygis.service.HeightAlarmInfosService;
import com.ncse.zhhygis.service.TransboundaryAlarmInfosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * ClassName:  [功能名称]
 * Description:  [根据条件查询超高报警信息]
 * Date:  2018/11/16 10 34
 *
 * @author Xugnh
 * @version 1.0.0
 */
@Service(value = "eightAlarmInfosService")
@Transactional(propagation = Propagation.NEVER)
public class HeightAlarmInfosServiceImpl implements HeightAlarmInfosService {

    @Autowired
    private HeightAlarmInfosMapper heightAlarmInfosMapper;

    /***
     * MethodName:  [selectByParms]
     * Description:  [根据条件查询超高报警信息]
     * @param: [pramsMap]
     * @return: java.util.List<com.ncse.zhhygis.entity.heightAlarmInfos>
     */
    @Override
    public List<HeightAlarmInfos> selectByParms(Map<String,String> pramsMap) {
        List<HeightAlarmInfos> heightAlarmInfos = heightAlarmInfosMapper.selectByParms(pramsMap);
        return heightAlarmInfos;
    }
}

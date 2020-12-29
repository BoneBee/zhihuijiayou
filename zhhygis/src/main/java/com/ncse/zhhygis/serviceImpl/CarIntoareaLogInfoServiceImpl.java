package com.ncse.zhhygis.serviceImpl;

import com.ncse.zhhygis.entity.CarIntoareaLogInfo;
import com.ncse.zhhygis.entity.TransboundaryAlarmInfos;
import com.ncse.zhhygis.mapper.CarIntoareaLogInfoMapper;
import com.ncse.zhhygis.mapper.TransboundaryAlarmInfosMapper;
import com.ncse.zhhygis.service.CarIntoareaLogInfoService;
import com.ncse.zhhygis.service.TransboundaryAlarmInfosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * ClassName:  [功能名称]
 * Description:  [根据条件查询进出区域报警信息]
 * Date:  2018/11/16 10 34
 *
 * @author Xugnc
 * @version 1.0.0
 */
@Service(value = "carIntoareaLogInfoService")
@Transactional(propagation = Propagation.NEVER)
public class CarIntoareaLogInfoServiceImpl implements CarIntoareaLogInfoService {

    @Autowired
    private CarIntoareaLogInfoMapper carIntoareaLogInfoMapper;

    /***
     * MethodName:  [selectByParms]
     * Description:  [根据条件查询超速报警信息]
     * @param: [pramsMap]
     * @return: java.util.List<com.ncse.zhhygis.entity.carIntoareaLogInfo>
     */
    @Override
    public List<CarIntoareaLogInfo> selectByParms(Map<String,String> pramsMap) {
        List<CarIntoareaLogInfo> carIntoareaLogInfo = carIntoareaLogInfoMapper.selectByParms(pramsMap);
        return carIntoareaLogInfo;
    }
}

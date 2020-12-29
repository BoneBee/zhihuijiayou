package com.ncse.zhhygis.serviceImpl;

import com.ncse.zhhygis.entity.CarIntoAreaalarmInfos;
import com.ncse.zhhygis.mapper.CarIntoAreaalarmInfosMapper;
import com.ncse.zhhygis.service.CarIntoAreaalarmInfosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * ClassName:  [功能名称]
 * Description:  [越界报警信息]
 * Date:  2018/11/16 10 34
 *
 * @author Xugnc
 * @version 1.0.0c
 */
@Service(value = "carIntoAreaalarmInfosService")
public class CarIntoAreaalarmInfosServiceImpl implements CarIntoAreaalarmInfosService {

    @Autowired
    private CarIntoAreaalarmInfosMapper carIntoAreaalarmInfosMapper;

    /***
     * MethodName:  [selectByParms]
     * Description:  [根据条件查询超时报警信息]
     * @param: [pramsMap]
     * @return: java.util.List<com.ncse.zhhygis.entity.carIntoAreaalarmInfos>
     */
    @Override
    public List<CarIntoAreaalarmInfos> selectByParms(Map<String,String> pramsMap) {
        List<CarIntoAreaalarmInfos> carIntoAreaalarmInfos = carIntoAreaalarmInfosMapper.selectByParms(pramsMap);
        return carIntoAreaalarmInfos;
    }
}

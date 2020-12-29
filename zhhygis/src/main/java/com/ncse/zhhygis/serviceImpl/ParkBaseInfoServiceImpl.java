package com.ncse.zhhygis.serviceImpl;

import com.ncse.zhhygis.entity.ParkBaseInfo;
import com.ncse.zhhygis.mapper.ParkBaseInfoMapper;
import com.ncse.zhhygis.service.ParkBaseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * ClassName:  [ParkBaseInfoServiceImpl]
 * Description:  [获取机位信息]
 * Date:  2018/12/24 10 31
 *
 * @author Xugn
 * @version 1.0.0
 */
@Service(value = "parkBaseInfoService")
@Transactional(propagation = Propagation.NEVER)
public class ParkBaseInfoServiceImpl implements ParkBaseInfoService {

    @Autowired
    private ParkBaseInfoMapper parkBaseInfoMapper;

    @Override
    public ParkBaseInfo selectByAircode(String aircode) {
        return parkBaseInfoMapper.selectByAircode(aircode);
    }
}

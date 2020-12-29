package com.ncse.zhhygis.mapper;

import com.ncse.zhhygis.entity.ParkBaseInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ParkBaseInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ParkBaseInfo record);

    int insertSelective(ParkBaseInfo record);

    ParkBaseInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ParkBaseInfo record);

    int updateByPrimaryKey(ParkBaseInfo record);

    ParkBaseInfo selectByAircode(String aircode);
}
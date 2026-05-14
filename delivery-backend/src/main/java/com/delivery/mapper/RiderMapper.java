package com.delivery.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.delivery.entity.Rider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RiderMapper extends BaseMapper<Rider> {

    @Select("SELECT * FROM rider WHERE username = #{username} AND deleted = 0")
    Rider selectByUsername(String username);
}

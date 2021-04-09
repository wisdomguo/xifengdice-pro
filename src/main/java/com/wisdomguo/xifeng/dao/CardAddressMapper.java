package com.wisdomguo.xifeng.dao;

import com.wisdomguo.xifeng.entity.*;
import com.wisdomguo.xifeng.entity.CardAddress;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface CardAddressMapper {

    @Select("SELECT * from CardAddress where qq_id=#{qqid}")
    List<CardAddress> selectAllByQQID(@Param("qqid") String qqid);

    @Insert("insert into cardaddress value(default,#{cardName},#{fileAddress},#{qqId})")
    int addCardAddress(CardAddress cardAddress);

    @Delete("delete from cardaddress where card_id = #{id}")
    int deleteCardAddress(int id);

    @Select("select count(*) from cardaddress where card_id=#{id} and qq_id=#{qqid}")
    int checkIdAndQQ(@Param("id") int id, @Param("qqid") String qqid);
}

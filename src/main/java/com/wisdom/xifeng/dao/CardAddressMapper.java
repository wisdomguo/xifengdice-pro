package com.wisdom.xifeng.dao;

import com.wisdom.xifeng.entity.*;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface CardAddressMapper {

    @Select("SELECT * from CardAddress where QQid=#{qqid}")
    List<CardAddress> selectAllByQQID(@Param("qqid") String qqid);

    @Insert("insert into cardaddress value(default,#{cardname},#{fileaddress},#{qqid})")
    int addCardAddress(CardAddress cardAddress);

    @Delete("delete from cardaddress where cardid = #{id}")
    int deleteCardAddress(int id);

    @Select("select count(*) from cardaddress where cardid=#{id} and qqid=#{qqid}")
    int checkIdAndQQ(@Param("id") int id, @Param("qqid") String qqid);
}

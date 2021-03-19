package com.wisdom.xifeng.dao;

import com.wisdom.xifeng.entity.QQGroup;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


public interface QQGroupMapper {

    @Select("SELECT * from qqgroup")
    List<QQGroup> selectAll();

    @Select("SELECT * from qqgroup where groupid=#{id}")
    QQGroup selectAllByID(String id);

    @Insert("insert into qqgroup value(#{id},0,0,0)")
    int addQQGroup(String id);

    @Update("update qqgroup set xfopen =#{xfopen} where groupid=#{id}")
    int updateOpenCloseXF(@Param("id") String id, @Param("xfopen") int xfopen);

    @Update("update qqgroup set diceopen =#{diceopen} where groupid=#{id}")
    int updateOpenCloseDice(@Param("id") String id, @Param("diceopen") int diceopen);

    @Update("update qqgroup set otheropen =#{otheropen} where groupid=#{id}")
    int updateOpenCloseOther(@Param("id") String id, @Param("otheropen") int otheropen);
}

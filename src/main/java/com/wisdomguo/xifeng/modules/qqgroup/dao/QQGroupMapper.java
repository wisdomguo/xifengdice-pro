package com.wisdomguo.xifeng.modules.qqgroup.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wisdomguo.xifeng.modules.qqgroup.entity.QQGroup;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


public interface QQGroupMapper extends BaseMapper<QQGroup> {

    @Select("SELECT * from qqgroup")
    List<QQGroup> selectAll();

    @Select("SELECT * from qqgroup where group_id=#{id}")
    QQGroup selectAllByID(String id);

    @Insert("insert into qqgroup value(#{id},0,0,0)")
    int addQQGroup(String id);

    @Update("update qqgroup set xf_open =#{xfopen} where group_id=#{id}")
    int updateOpenCloseXF(@Param("id") String id, @Param("xfopen") int xfopen);

    @Update("update qqgroup set dice_open =#{diceopen} where group_id=#{id}")
    int updateOpenCloseDice(@Param("id") String id, @Param("diceopen") int diceopen);

    @Update("update qqgroup set other_open =#{otheropen} where group_id=#{id}")
    int updateOpenCloseOther(@Param("id") String id, @Param("otheropen") int otheropen);
}

package com.wisdomguo.xifeng.modules.game.explorepocket.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wisdomguo.xifeng.modules.game.explorepocket.entity.ExplorePocket;
import com.wisdomguo.xifeng.modules.qqgroup.entity.QQGroup;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ExplorePocketMapper
 *
 * @author wisdom-guo
 * @since 2021/7/8
 */
public interface ExplorePocketMapper  extends BaseMapper<ExplorePocket> {

    @Select("select s.qq_id,s.stardust,s.star_fragment,s.nick_name from (select (SUM(star_fragment)*100+SUM(stardust)) count,e.* from explore_pocket e  group by qq_id) s order by count desc limit 0,10 ")
    List<ExplorePocket> selectAllRanking();

}

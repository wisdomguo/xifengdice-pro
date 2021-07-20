package com.wisdomguo.xifeng.modules.game.farm.userinfo.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wisdomguo.xifeng.modules.game.farm.userinfo.entity.FarmUserInfo;
import org.apache.ibatis.annotations.Update;

/**
 * UserInfoMapper
 *
 * @author wisdom-guo
 * @since 2021/7/8
 */
public interface FarmUserInfoMapper extends BaseMapper<FarmUserInfo> {

    @Update("update farm_user_info set disaster_count = disaster_count - 1 where disaster_count >= 1")
    int updateUserDisasterCountWhereCountGtOne();


    @Update("update farm_user_info set quicken_count = quicken_count + 1")
    int updateUserQuickenCount();
}

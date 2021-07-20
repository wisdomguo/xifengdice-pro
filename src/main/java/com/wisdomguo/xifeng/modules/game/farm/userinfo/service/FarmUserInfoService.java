package com.wisdomguo.xifeng.modules.game.farm.userinfo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wisdomguo.xifeng.modules.game.farm.userinfo.entity.FarmUserInfo;

/**
 * UserInfoService
 *
 * @author wisdom-guo
 * @since 2021/7/8
 */
public interface FarmUserInfoService extends IService<FarmUserInfo> {

    /**
     * 根据用户Id查询用户信息
     * @param qqId
     * @return ExplorePocket
     */
    FarmUserInfo findByQqId(Long qqId);


    /**
     * 修改用户信息
     * @param userInfo
     * @return boolean
     */
    boolean changeUserInfo(FarmUserInfo userInfo);


    int updateUserDisasterCountWhereCountGtOne();


    int updateUserQuickenCount();
}

package com.wisdomguo.xifeng.modules.game.farm.userinfo.service.impl;



import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wisdomguo.xifeng.assist.AssemblyCache;
import com.wisdomguo.xifeng.modules.game.farm.userinfo.dao.FarmUserInfoMapper;
import com.wisdomguo.xifeng.modules.game.farm.userinfo.entity.FarmUserInfo;
import com.wisdomguo.xifeng.modules.game.farm.userinfo.service.FarmUserInfoService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * FruitServiceImpl
 *
 * @author wisdom-guo
 * @since 2021/7/8
 */
@Service
public class FarmUserInfoServiceImpl extends ServiceImpl<FarmUserInfoMapper, FarmUserInfo> implements FarmUserInfoService {


    /**
     * 根据用户Id查询用户信息
     *
     * @param qqId
     * @return UserInfo
     */
    @Override
    public FarmUserInfo findByQqId(Long qqId) {
        FarmUserInfo userInfos = AssemblyCache.userInfos.get(qqId);
        if (userInfos !=null){
            return userInfos;
        }else {
            userInfos =this.baseMapper.selectOne(Wrappers.<FarmUserInfo>lambdaQuery().eq(FarmUserInfo::getQqId,qqId));
            if(userInfos !=null){
                AssemblyCache.userInfos.put(qqId, userInfos);
                return userInfos;
            }else{
                FarmUserInfo userInfo=new FarmUserInfo(qqId,2,0,0,3, LocalDateTime.now());
                this.baseMapper.insert(userInfo);
                AssemblyCache.userInfos.put(qqId,userInfo);
                return userInfo;
            }
        }
    }

    /**
     * 修改用户信息
     *
     * @param userInfo
     * @return boolean
     */
    @Override
    public boolean changeUserInfo(FarmUserInfo userInfo) {
        boolean result=this.update(Wrappers.<FarmUserInfo>lambdaUpdate()
                .set(FarmUserInfo::getDisasterCount, userInfo.getDisasterCount())
                .set(FarmUserInfo::getFieldCount, userInfo.getFieldCount())
                .set(FarmUserInfo::getQuickenCount, userInfo.getQuickenCount())
                .eq (FarmUserInfo::getQqId, userInfo.getQqId()));
        AssemblyCache.userInfos.put(userInfo.getQqId(),userInfo);
        return result;
    }
}

package com.wisdomguo.xifeng.modules.game.explorepocket.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wisdomguo.xifeng.assist.AssemblyCache;
import com.wisdomguo.xifeng.modules.game.explorepocket.dao.ExplorePocketMapper;
import com.wisdomguo.xifeng.modules.game.explorepocket.entity.ExplorePocket;
import com.wisdomguo.xifeng.modules.game.explorepocket.service.ExplorePocketService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ExplorePocketServiceImpl
 *
 * @author wisdom-guo
 * @since 2021/7/8
 */
@Service
public class ExplorePocketServiceImpl extends ServiceImpl<ExplorePocketMapper,ExplorePocket> implements ExplorePocketService {

    @Override
    public ExplorePocket findByQqId(Long qqId,String nickName) {
        ExplorePocket explorePocket= AssemblyCache.explorePockets.get(qqId);
        if(explorePocket!=null){
            return explorePocket;
        }else{
            explorePocket=this.baseMapper.selectById(qqId);
            if(explorePocket!=null){
                AssemblyCache.explorePockets.put(qqId,explorePocket);
                return explorePocket;
            }else{
                explorePocket=new ExplorePocket(qqId,0,0,0,nickName);
                this.baseMapper.insert(explorePocket);
                AssemblyCache.explorePockets.put(qqId,explorePocket);
                return explorePocket;
            }

        }
    }

    @Override
    public boolean changeStars(ExplorePocket explorePocket) {

        boolean result=this.update(Wrappers.<ExplorePocket>lambdaUpdate()
                .set(ExplorePocket::getStardust, explorePocket.getStardust())
                .set(ExplorePocket::getNickName, explorePocket.getNickName())
                .set(ExplorePocket::getStarFragment, explorePocket.getStarFragment())
                .set(ExplorePocket::getStars, explorePocket.getStars())
                .eq(ExplorePocket::getQqId, explorePocket.getQqId()));
        AssemblyCache.explorePockets.put(explorePocket.getQqId(),explorePocket);
        return result;
    }

    @Override
    public List<ExplorePocket> selectAllRanking() {
        return this.baseMapper.selectAllRanking();
    }
}

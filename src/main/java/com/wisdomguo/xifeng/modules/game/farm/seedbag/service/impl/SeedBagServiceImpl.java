package com.wisdomguo.xifeng.modules.game.farm.seedbag.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wisdomguo.xifeng.assist.AssemblyCache;
import com.wisdomguo.xifeng.modules.game.farm.seedbag.dao.SeedBagMapper;
import com.wisdomguo.xifeng.modules.game.farm.seedbag.entity.SeedBag;
import com.wisdomguo.xifeng.modules.game.farm.seedbag.service.SeedBagService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * SeedBagServiceImpl
 *
 * @author wisdom-guo
 * @since 2021/7/8
 */
@Service
public class SeedBagServiceImpl extends ServiceImpl<SeedBagMapper, SeedBag> implements SeedBagService {

    /**
     * 根据Id查询种子数量
     *
     * @param qqId
     * @return ExplorePocket
     */
    @Override
    public List<SeedBag> findByQqId(Long qqId) {
        List<SeedBag> seedBags=AssemblyCache.seedBags.get(qqId);
        if (seedBags!=null){
            return seedBags;
        }else {
            seedBags=this.baseMapper.selectList(Wrappers.<SeedBag>lambdaQuery().eq(SeedBag::getQqId,qqId));
            if(seedBags!=null){
                AssemblyCache.seedBags.put(qqId,seedBags);
                return seedBags;
            }else{
                return new ArrayList<>();
            }
        }
    }

    /**
     * 修改口袋库存
     *
     * @param seedBag
     * @return boolean
     */
    @Override
    public boolean changeSeed(SeedBag seedBag) {
        SeedBag oldSeed=this.baseMapper.selectOne(Wrappers.<SeedBag>lambdaQuery()
                .eq(SeedBag::getQqId,seedBag.getQqId())
                .eq(SeedBag::getType,seedBag.getType())
                .eq(SeedBag::getSpeciesId,seedBag.getSpeciesId()));
        if(oldSeed!=null){
            oldSeed.setCount(oldSeed.getCount()+seedBag.getCount());
            this.baseMapper.updateById(oldSeed);
        }else{
            this.baseMapper.insert(seedBag);
        }
        List<SeedBag> seedBags=this.baseMapper.selectList(Wrappers.<SeedBag>lambdaQuery().eq(SeedBag::getQqId,seedBag.getQqId()));
        AssemblyCache.seedBags.put(seedBag.getQqId(),seedBags);
        return true;
    }
}

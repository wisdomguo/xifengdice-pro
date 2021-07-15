package com.wisdomguo.xifeng.modules.game.farm.seedbag.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wisdomguo.xifeng.modules.game.farm.seedbag.entity.SeedBag;

import java.util.List;

/**
 * SeedBagService
 *
 * @author wisdom-guo
 * @since 2021/7/8
 */
public interface SeedBagService extends IService<SeedBag> {

    /**
     * 根据Id查询口袋库存
     * @param qqId
     * @return ExplorePocket
     */
    List<SeedBag> findByQqId(Long qqId);


    /**
     * 修改口袋库存
     * @param seedBag
     * @return boolean
     */
    boolean changeSeed(SeedBag seedBag);
    
}

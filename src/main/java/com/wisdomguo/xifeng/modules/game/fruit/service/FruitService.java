package com.wisdomguo.xifeng.modules.game.fruit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wisdomguo.xifeng.modules.game.fruit.entity.Fruit;

import java.util.List;

/**
 * SeedBagService
 *
 * @author wisdom-guo
 * @since 2021/7/8
 */
public interface FruitService extends IService<Fruit> {

    /**
     * 根据Id查询作物数量
     * @param qqId
     * @return ExplorePocket
     */
    List<Fruit> findByQqId(Long qqId);


    /**
     * 修改作物库存
     * @param fruit
     * @return boolean
     */
    boolean changeFruit(Fruit fruit);
    
 /**
     * 修改作物库存
     * @param fruits
     * @return boolean
     */
    boolean changeFruitList(List<Fruit> fruits);

}

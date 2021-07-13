package com.wisdomguo.xifeng.modules.game.fruit.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wisdomguo.xifeng.assist.AssemblyCache;
import com.wisdomguo.xifeng.modules.game.fruit.dao.FruitMapper;
import com.wisdomguo.xifeng.modules.game.fruit.entity.Fruit;
import com.wisdomguo.xifeng.modules.game.fruit.service.FruitService;
import com.wisdomguo.xifeng.modules.game.seedbag.entity.SeedBag;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * FruitServiceImpl
 *
 * @author wisdom-guo
 * @since 2021/7/8
 */
@Service
public class FruitServiceImpl extends ServiceImpl<FruitMapper, Fruit> implements FruitService {


    /**
     * 根据Id查询作物数量
     *
     * @param qqId
     * @return ExplorePocket
     */
    @Override
    public List<Fruit> findByQqId(Long qqId) {
        List<Fruit> fruits=AssemblyCache.fruits.get(qqId);
        if (fruits!=null){
            return fruits;
        }else {
            fruits=this.baseMapper.selectList(Wrappers.<Fruit>lambdaQuery().eq(Fruit::getQqId,qqId));
            if(fruits!=null){
                AssemblyCache.fruits.put(qqId,fruits);
                return fruits;
            }else{
                return new ArrayList<>();
            }
        }
    }

    /**
     * 修改作物库存
     *
     * @param fruit
     * @return boolean
     */
    @Override
    public boolean changeFruit(Fruit fruit) {
        Fruit oldFruit=this.baseMapper.selectOne(Wrappers.<Fruit>lambdaQuery()
                .eq(Fruit::getQqId,fruit.getQqId())
                .eq(Fruit::getSpeciesId,fruit.getSpeciesId()));
        if(oldFruit!=null){
            oldFruit.setCount(oldFruit.getCount()+fruit.getCount());
            this.baseMapper.updateById(oldFruit);
        }else{
            this.baseMapper.insert(fruit);
        }
        List<Fruit> fruits=this.baseMapper.selectList(Wrappers.<Fruit>lambdaQuery().eq(Fruit::getQqId,fruit.getQqId()));
        AssemblyCache.fruits.put(fruit.getQqId(),fruits);
        return true;
    }


    /**
     * 修改作物库存
     *
     * @param fruits
     * @return boolean
     */
    @Override
    public boolean changeFruitList(List<Fruit> fruits) {
        fruits.stream().forEach(fruit->{
            Fruit oldFruit=this.baseMapper.selectOne(Wrappers.<Fruit>lambdaQuery()
                    .eq(Fruit::getQqId,fruit.getQqId())
                    .eq(Fruit::getSpeciesId,fruit.getSpeciesId()));
            if(oldFruit!=null){
                oldFruit.setCount(oldFruit.getCount()+fruit.getCount());
                this.baseMapper.updateById(oldFruit);
            }else{
                this.baseMapper.insert(fruit);
            }
        });
        List<Fruit> newfruits=this.baseMapper.selectList(Wrappers.<Fruit>lambdaQuery().eq(Fruit::getQqId,fruits.get(0).getQqId()));
        AssemblyCache.fruits.put(fruits.get(0).getQqId(),newfruits);
        return true;
    }
}

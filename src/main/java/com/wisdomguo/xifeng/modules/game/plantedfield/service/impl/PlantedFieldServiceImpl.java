package com.wisdomguo.xifeng.modules.game.plantedfield.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wisdomguo.xifeng.assist.AssemblyCache;
import com.wisdomguo.xifeng.modules.game.plantedfield.dao.PlantedFieldMapper;
import com.wisdomguo.xifeng.modules.game.plantedfield.entity.PlantedField;
import com.wisdomguo.xifeng.modules.game.plantedfield.service.PlantedFieldService;
import com.wisdomguo.xifeng.modules.game.seedbag.entity.SeedBag;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * PlantedFieldServiceImpl
 *
 * @author wisdom-guo
 * @since 2021/7/8
 */
@Service
public class PlantedFieldServiceImpl extends ServiceImpl<PlantedFieldMapper, PlantedField> implements PlantedFieldService {


    /**
     * 根据qqId查询田地作物
     *
     * @param qqId
     * @return ExplorePocket
     */
    @Override
    public List<PlantedField> findByQqId(Long qqId) {
        List<PlantedField> plantedFields= AssemblyCache.plantedFields.get(qqId);
        if (plantedFields!=null){
            return plantedFields;
        }else {
            plantedFields=this.baseMapper.selectList(Wrappers.<PlantedField>lambdaQuery().eq(PlantedField::getQqId,qqId));
            if(plantedFields!=null){
                AssemblyCache.plantedFields.put(qqId,plantedFields);
                return plantedFields;
            }else{
                return new ArrayList<>();
            }
        }
    }

    /**
     * 修改田地作物
     *
     * @param plantedField
     * @return boolean
     */
    @Override
    public boolean changeField(PlantedField plantedField) {
        PlantedField oldField=this.baseMapper.selectOne(Wrappers.<PlantedField>lambdaQuery()
                .eq(PlantedField::getQqId,plantedField.getQqId())
                .eq(PlantedField::getType,plantedField.getType())
                .eq(PlantedField::getSerial,plantedField.getSerial()));
        if(oldField!=null){
            oldField.setTimes(oldField.getTimes()+1);
            oldField.setPlantingTime(new Date());
            this.baseMapper.updateById(oldField);
        }else{
            this.baseMapper.insert(plantedField);
        }
        List<PlantedField> plantedFields=this.baseMapper.selectList(Wrappers.<PlantedField>lambdaQuery().eq(PlantedField::getQqId,plantedField.getQqId()));
        AssemblyCache.plantedFields.put(plantedField.getQqId(),plantedFields);
        return true;
    }

    /**
     * 删除田地作物
     *
     * @param plantedField
     * @return boolean
     */
    @Override
    public boolean deleteField(PlantedField plantedField) {
        PlantedField oldField=this.baseMapper.selectOne(Wrappers.<PlantedField>lambdaQuery()
                .eq(PlantedField::getQqId,plantedField.getQqId())
                .eq(PlantedField::getType,plantedField.getType())
                .eq(PlantedField::getSerial,plantedField.getSerial()));
        if(oldField!=null){
            this.baseMapper.deleteById(oldField.getId());
        }else{
            return false;
        }
        List<PlantedField> plantedFields=this.baseMapper.selectList(Wrappers.<PlantedField>lambdaQuery().eq(PlantedField::getQqId,plantedField.getQqId()));
        AssemblyCache.plantedFields.put(plantedField.getQqId(),plantedFields);
        return true;
    }
}
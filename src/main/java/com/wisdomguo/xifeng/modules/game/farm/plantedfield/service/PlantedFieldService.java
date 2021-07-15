package com.wisdomguo.xifeng.modules.game.farm.plantedfield.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wisdomguo.xifeng.modules.game.farm.plantedfield.entity.PlantedField;

import java.util.List;

/**
 * PlantedFieldService
 *
 * @author wisdom-guo
 * @since 2021/7/8
 */
public interface PlantedFieldService extends IService<PlantedField>{

    /**
     * 根据qqId查询田地作物
     * @param qqId
     * @return ExplorePocket
     */
    List<PlantedField> findByQqId(Long qqId);


    /**
     * 修改田地作物
     * @param plantedField
     * @return boolean
     */
    boolean changeField(PlantedField plantedField);

    /**
     * 删除田地作物
     * @param plantedField
     * @return boolean
     */
    boolean deleteField(PlantedField plantedField);
}

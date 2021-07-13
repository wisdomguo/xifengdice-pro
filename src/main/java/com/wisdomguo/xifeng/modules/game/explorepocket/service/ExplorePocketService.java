package com.wisdomguo.xifeng.modules.game.explorepocket.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wisdomguo.xifeng.modules.game.explorepocket.entity.*;

import java.util.List;

/**
 * ExplorePocketService
 *
 * @author wisdom-guo
 * @since 2021/7/8
 */
public interface ExplorePocketService extends IService<ExplorePocket>{

    /**
     * 根据Id查询口袋库存
     * @param qqId
     * @return ExplorePocket
     */
    ExplorePocket findByQqId(Long qqId,String nickName);


    /**
     * 修改口袋库存
     * @param explorePocket
     * @return boolean
     */
    boolean changeStars(ExplorePocket explorePocket);

    List<ExplorePocket> selectAllRanking();
}

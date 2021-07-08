package com.wisdomguo.xifeng.modules.qqgroup.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wisdomguo.xifeng.modules.qqgroup.entity.QQGroup;

import java.util.List;

public interface QQGroupSerivce extends IService<QQGroup> {

    List<QQGroup> selectAll();

    QQGroup selectAllByID(String id);

    int updateOpenCloseXF(String id, int xfopen);

    int updateOpenCloseDice(String id, int diceopen);

    int updateOpenCloseOther(String id, int otheropen);

}

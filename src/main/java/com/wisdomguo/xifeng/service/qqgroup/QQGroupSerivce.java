package com.wisdomguo.xifeng.service.qqgroup;

import com.wisdomguo.xifeng.entity.QQGroup;

import java.util.List;

public interface QQGroupSerivce {
    List<QQGroup> selectAll();

    QQGroup selectAllByID(String id);

    int updateOpenCloseXF(String id, int xfopen);

    int updateOpenCloseDice(String id, int diceopen);

    int updateOpenCloseOther(String id, int otheropen);

}

package com.wisdomguo.xifeng.service.qqgroup.impl;

import com.wisdomguo.xifeng.dao.QQGroupMapper;
import com.wisdomguo.xifeng.entity.QQGroup;
import com.wisdomguo.xifeng.service.qqgroup.QQGroupSerivce;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class QQGroupSerivceImpl implements QQGroupSerivce {

    @Resource
    private QQGroupMapper qqGroupMapper;

    @Override
    public List<QQGroup> selectAll() {
        return qqGroupMapper.selectAll();
    }

    @Override
    @Transactional
    public QQGroup selectAllByID(String id) {
        QQGroup qqGroup=qqGroupMapper.selectAllByID(id);
        if(qqGroup!=null){
            return qqGroup;

        }else{
            qqGroupMapper.addQQGroup(id);
            return qqGroupMapper.selectAllByID(id);
        }
    }

    @Override
    @Transactional
    public int updateOpenCloseXF(String id, int xfopen) {
        QQGroup qqGroup=qqGroupMapper.selectAllByID(id);
        if(qqGroup==null){
            qqGroupMapper.addQQGroup(id);
        }
        return qqGroupMapper.updateOpenCloseXF(id,xfopen);
    }

    @Override
    @Transactional
    public int updateOpenCloseDice(String id, int diceopen) {
        QQGroup qqGroup=qqGroupMapper.selectAllByID(id);
        if(qqGroup==null){
            qqGroupMapper.addQQGroup(id);
        }
        return qqGroupMapper.updateOpenCloseDice(id,diceopen);
    }

    @Override
    @Transactional
    public int updateOpenCloseOther(String id, int otheropen) {
        QQGroup qqGroup=qqGroupMapper.selectAllByID(id);
        if(qqGroup==null){
            qqGroupMapper.addQQGroup(id);
        }
        return qqGroupMapper.updateOpenCloseOther(id,otheropen);
    }
}

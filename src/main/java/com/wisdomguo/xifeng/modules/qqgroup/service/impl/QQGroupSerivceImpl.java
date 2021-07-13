package com.wisdomguo.xifeng.modules.qqgroup.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wisdomguo.xifeng.assist.AssemblyCache;
import com.wisdomguo.xifeng.modules.qqgroup.dao.QQGroupMapper;
import com.wisdomguo.xifeng.modules.qqgroup.entity.QQGroup;
import com.wisdomguo.xifeng.modules.qqgroup.service.QQGroupSerivce;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class QQGroupSerivceImpl extends ServiceImpl<QQGroupMapper, QQGroup> implements QQGroupSerivce {

    @Resource
    private QQGroupMapper qqGroupMapper;

    @Override
    public List<QQGroup> selectAll() {
        return qqGroupMapper.selectAll();
    }

    @Override
    public QQGroup selectAllByID(String id) {
        QQGroup qqGroup = AssemblyCache.qqGroups.get(id);
        if (qqGroup != null) {
            return qqGroup;
        } else {
            qqGroup = qqGroupMapper.selectAllByID(id);
            if(qqGroup!=null){
                AssemblyCache.qqGroups.put(qqGroup.getGroupId(), qqGroup);
                return qqGroup;
            }else{
                qqGroup=new QQGroup(id,0,0,0,0,0);
                this.qqGroupMapper.insert(qqGroup);
                AssemblyCache.qqGroups.put(qqGroup.getGroupId(), qqGroup);
                return qqGroup;
            }
        }

    }

    @Override
    public int updateOpenCloseXF(String id, int xfopen) {
        int result = qqGroupMapper.updateOpenCloseXF(id, xfopen);
        QQGroup qqGroup = qqGroupMapper.selectById(id);
        AssemblyCache.qqGroups.put(qqGroup.getGroupId(), qqGroup);
        return result;
    }

    @Override
    public int updateOpenCloseDice(String id, int diceopen) {
        int result = qqGroupMapper.updateOpenCloseDice(id, diceopen);
        QQGroup qqGroup = qqGroupMapper.selectById(id);
        AssemblyCache.qqGroups.put(qqGroup.getGroupId(), qqGroup);
        return result;

    }

    @Override
    public int updateOpenCloseOther(String id, int otheropen) {
        int result = qqGroupMapper.updateOpenCloseOther(id, otheropen);
        QQGroup qqGroup = qqGroupMapper.selectById(id);
        AssemblyCache.qqGroups.put(qqGroup.getGroupId(), qqGroup);
        return result;
    }

    @Override
    public boolean updateOpenCloseGame(String id, int gameopen) {
        boolean result= this.update(Wrappers.<QQGroup>lambdaUpdate().set(QQGroup::getGameOpen,gameopen).eq(QQGroup::getGroupId,id));
        QQGroup qqGroup = qqGroupMapper.selectById(id);
        AssemblyCache.qqGroups.put(qqGroup.getGroupId(), qqGroup);
        return result;
    }
}

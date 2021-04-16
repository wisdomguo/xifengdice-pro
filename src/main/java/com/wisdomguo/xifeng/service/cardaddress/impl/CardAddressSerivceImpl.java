package com.wisdomguo.xifeng.service.cardaddress.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wisdomguo.xifeng.dao.*;
import com.wisdomguo.xifeng.entity.*;
import com.wisdomguo.xifeng.entity.CardAddress;
import com.wisdomguo.xifeng.service.cardaddress.CardAddressSerivce;
import com.wisdomguo.xifeng.dao.CardAddressMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CardAddressSerivceImpl  extends ServiceImpl<CardAddressMapper, CardAddress> implements CardAddressSerivce {

    @Resource
    private CardAddressMapper cardAddressMapper;

    @Override
    public List<CardAddress> selectAllByQQID(String qqid) {
        return cardAddressMapper.selectAllByQQID(qqid);
    }

    @Override
    public int addCardAddress(CardAddress cardAddress) {
        return cardAddressMapper.addCardAddress(cardAddress);
    }

    @Override
    public int deleteCardAddress(int id, String qqid, boolean admin) {
        if(admin){
            return cardAddressMapper.deleteCardAddress(id);
        }else{
            if(cardAddressMapper.checkIdAndQQ(id,qqid)>0){
                return cardAddressMapper.deleteCardAddress(id);
            }else{
                return 0;
            }
        }


    }

}

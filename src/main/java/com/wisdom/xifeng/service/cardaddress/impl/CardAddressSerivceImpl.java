package com.wisdom.xifeng.service.cardaddress.impl;

import com.wisdom.xifeng.dao.*;
import com.wisdom.xifeng.entity.*;
import com.wisdom.xifeng.service.cardaddress.CardAddressSerivce;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CardAddressSerivceImpl implements CardAddressSerivce {

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
package com.wisdomguo.xifeng.modules.dice.cardaddress.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wisdomguo.xifeng.modules.dice.cardaddress.entity.CardAddress;

import java.util.List;


public interface CardAddressSerivce extends IService<CardAddress> {
    List<CardAddress> selectAllByQQID(String qqid);

    int addCardAddress(CardAddress cardAddress);

    int deleteCardAddress(int id, String qqid, boolean admin);
}

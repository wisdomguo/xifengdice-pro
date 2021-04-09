package com.wisdomguo.xifeng.service.cardaddress;

import com.wisdomguo.xifeng.entity.*;
import com.wisdomguo.xifeng.entity.CardAddress;

import java.util.List;


public interface CardAddressSerivce {
    List<CardAddress> selectAllByQQID(String qqid);

    int addCardAddress(CardAddress cardAddress);

    int deleteCardAddress(int id, String qqid, boolean admin);
}

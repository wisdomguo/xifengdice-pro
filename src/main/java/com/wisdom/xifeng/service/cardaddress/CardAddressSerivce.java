package com.wisdom.xifeng.service.cardaddress;

import com.wisdom.xifeng.entity.*;

import java.util.List;


public interface CardAddressSerivce {
    List<CardAddress> selectAllByQQID(String qqid);

    int addCardAddress(CardAddress cardAddress);

    int deleteCardAddress(int id, String qqid, boolean admin);
}

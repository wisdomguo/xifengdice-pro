package com.wisdomguo.xifeng.service.blacklist.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wisdomguo.xifeng.dao.BlackListMapper;
import com.wisdomguo.xifeng.dao.CharacterCocMaper;
import com.wisdomguo.xifeng.entity.BlackList;
import com.wisdomguo.xifeng.entity.CharacterCoC;
import com.wisdomguo.xifeng.service.blacklist.BlackListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * BlackListServiceImpl
 *
 * @author wisdom-guo
 * @since 2021/4/9
 */
@Service
public class BlackListServiceImpl extends ServiceImpl<BlackListMapper, BlackList> implements BlackListService {

}

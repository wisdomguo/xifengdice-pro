package com.wisdom.xifeng.service.blacklist.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wisdom.xifeng.dao.BlackListMapper;
import com.wisdom.xifeng.dao.CharacterCocMaper;
import com.wisdom.xifeng.entity.BlackList;
import com.wisdom.xifeng.entity.CharacterCoC;
import com.wisdom.xifeng.service.blacklist.BlackListService;
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

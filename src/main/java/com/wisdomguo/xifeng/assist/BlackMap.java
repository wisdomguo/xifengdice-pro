package com.wisdomguo.xifeng.assist;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wisdomguo.xifeng.modules.blacklist.entity.BlackList;
import com.wisdomguo.xifeng.modules.blacklist.service.BlackListService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * BlackMap
 *
 * @author wisdom-guo
 * @since 2021/4/15
 */
@Component
public class BlackMap {

    @Resource
    private BlackListService blackListService;

    private static Map<Long,BlackList> blackListMap;

    @PostConstruct
    private void init(){
        blackListMap=new HashMap<>();
        List<BlackList> blackLists=blackListService.list(Wrappers.<BlackList>lambdaQuery().eq(BlackList::getType,2).eq(BlackList::getForeverDelete,1).eq(BlackList::getIsDelete,1));
        for(BlackList blackList:blackLists){
            blackListMap.put(blackList.getQgId(),blackList);
        }
    }

    public static boolean returnBlackList(Long userId){
        if(Objects.isNull(blackListMap.get(userId))){
            return false;
        }else {
            return true;
        }
    }

    public static void removeBlackList(Long userId){
        blackListMap.remove(userId);
    }

    public static void addBlackList(BlackList blackList){
        blackListMap.put(blackList.getQgId(),blackList);
    }

}
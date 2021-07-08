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

    public static boolean returnBlackList(Long userId){
        if(Objects.isNull(AssemblyCache.blackLists.get(userId))){
            return false;
        }else {
            return true;
        }
    }

    public static void removeBlackList(Long userId){
        AssemblyCache.blackLists.remove(userId);
    }

    public static void addBlackList(BlackList blackList){
        AssemblyCache.blackLists.put(blackList.getQgId(),blackList);
    }

}

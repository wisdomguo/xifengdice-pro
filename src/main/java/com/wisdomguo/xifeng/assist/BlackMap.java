package com.wisdomguo.xifeng.assist;

import com.wisdomguo.xifeng.modules.botset.blacklist.entity.BlackList;
import org.springframework.stereotype.Component;

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

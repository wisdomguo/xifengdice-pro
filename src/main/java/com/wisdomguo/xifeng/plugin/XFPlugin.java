package com.wisdomguo.xifeng.plugin;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wisdomguo.xifeng.entity.BlackList;
import com.wisdomguo.xifeng.service.blacklist.BlackListService;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;


/**
 * 示例插件
 * 插件必须继承CQPlugin，上面要 @Component
 * <p>
 * 添加事件：光标移动到类中，按 Ctrl+O 添加事件(讨论组消息、加群请求、加好友请求等)
 * 查看API参数类型：光标移动到方法括号中按Ctrl+P
 * 查看API说明：光标移动到方法括号中按Ctrl+Q
 */

@Slf4j
@Component
public class XFPlugin extends BotPlugin {

    @Autowired
    private BlackListService blackListService;

    @Override
    public int onFriendRequest(@NotNull Bot bot, @NotNull OnebotEvent.FriendRequestEvent event) {
        String com=event.getComment();
        if(com.indexOf("1d20")!=-1){
            bot.setFriendAddRequest(event.getFlag(),true,"");
        }else{
            bot.setFriendAddRequest(event.getFlag(),false,"您的答案有误，请重新回答哦！");
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupRequest(@NotNull Bot bot, @NotNull OnebotEvent.GroupRequestEvent event) {
        bot.setGroupAddRequest(event.getFlag(),"",true,"");
        return MESSAGE_IGNORE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int onGroupDecreaseNotice(@NotNull Bot bot, @NotNull OnebotEvent.GroupDecreaseNoticeEvent event) {
        Long groupId=event.getGroupId();
        Long userId = event.getUserId();
        Long operatorId = event.getOperatorId();
        List<BlackList> blackLists=new ArrayList<>();
        if(userId==bot.getLoginInfo().getUserId()&&operatorId!=0){
            int countOperator=blackListService.count(Wrappers.<BlackList>lambdaQuery().eq(BlackList::getQgId,operatorId));
            if(countOperator==0){
                BlackList operator=new BlackList(null,operatorId,2,"违规操作:踢出",new Date(),new Date(),1,0);
                blackLists.add(operator);
            }else{
                blackListService.update(Wrappers.<BlackList>lambdaUpdate().set(BlackList::getIsDelete,1).set(BlackList::getForeverDelete,1).eq(BlackList::getQgId,operatorId));
            }
            int countGroup=blackListService.count(Wrappers.<BlackList>lambdaQuery().eq(BlackList::getQgId,groupId));
            if(countGroup==0){
                BlackList operator=new BlackList(null,groupId,1,"违规操作:踢出",new Date(),new Date(),1,0);
                blackLists.add(operator);
            }else{
                blackListService.update(Wrappers.<BlackList>lambdaUpdate().set(BlackList::getIsDelete,1).set(BlackList::getForeverDelete,1).eq(BlackList::getQgId,groupId));
            }
            if(blackLists.size()>0){
                blackListService.saveBatch(blackLists);
            }
            return MESSAGE_IGNORE;
        }
        return MESSAGE_IGNORE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int onGroupBanNotice(@NotNull Bot bot, @NotNull OnebotEvent.GroupBanNoticeEvent event) {
        Long groupId=event.getGroupId();
        Long userId = event.getUserId();
        Long operatorId = event.getOperatorId();
        if(userId==bot.getLoginInfo().getUserId()){
            int countOperator=blackListService.count(Wrappers.<BlackList>lambdaQuery().eq(BlackList::getQgId,operatorId));
            if(countOperator==0){
                BlackList operator=new BlackList(null,operatorId,2,"违规操作:禁言",new Date(),new Date(),1,0);
                BlackList group=new BlackList(null,groupId,1,"违规操作:禁言",new Date(),new Date(),1,0);
                blackListService.save(operator);
                blackListService.save(group);
                bot.setGroupLeave(groupId,true);
            }else{
                blackListService.update(Wrappers.<BlackList>lambdaUpdate().set(BlackList::getIsDelete,1).set(BlackList::getForeverDelete,1).eq(BlackList::getQgId,operatorId));
            }
            return MESSAGE_IGNORE;
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupIncreaseNotice(@NotNull Bot bot, @NotNull OnebotEvent.GroupIncreaseNoticeEvent event) {
        Long groupId=event.getGroupId();
        int count=blackListService.count(Wrappers.<BlackList>lambdaQuery().eq(BlackList::getQgId,groupId).eq(BlackList::getIsDelete,1));
        if(count!=0){
            bot.sendGroupMsg(groupId,"检测到该群在黑名单上!惜风已申请退群",false);
            bot.setGroupLeave(groupId,true);
        }
        return super.onGroupIncreaseNotice(bot, event);
    }
}

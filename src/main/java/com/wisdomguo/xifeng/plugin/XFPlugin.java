package com.wisdomguo.xifeng.plugin;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wisdomguo.xifeng.modules.botset.blacklist.entity.BlackList;
import com.wisdomguo.xifeng.modules.botset.blacklist.service.BlackListService;
import com.wisdomguo.xifeng.assist.BlackMap;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


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
        if(com.indexOf("1d100")!=-1){
            bot.setFriendAddRequest(event.getFlag(),true,"");
        }else{
            bot.setFriendAddRequest(event.getFlag(),false,"您的答案有误，请重新回答哦！");
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupRequest(@NotNull Bot bot, @NotNull OnebotEvent.GroupRequestEvent event) {
        //判断是不是加群邀请
        if(event.getSubType().indexOf("invite")!=-1) {
            bot.setGroupAddRequest(event.getFlag(), "", true, "");
        }
        return MESSAGE_IGNORE;
    }


    /**
     * 退群,判断是否是自己被踢在黑名单上并退群
     * @throws
     * @author wisdom-guo
     * @date 2021/4/16
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int onGroupDecreaseNotice(@NotNull Bot bot, @NotNull OnebotEvent.GroupDecreaseNoticeEvent event) {
        Long groupId=event.getGroupId();
        Long userId = event.getUserId();
        Long operatorId = event.getOperatorId();
        //如果没有执行人,则是自主退群
        if(Objects.isNull(operatorId)||operatorId.equals(1969077760L)){
            return MESSAGE_IGNORE;
        }
        //设置黑名单列表
        List<BlackList> blackLists=new ArrayList<>();
        //判断是否是自己被踢
        if(userId==bot.getLoginInfo().getUserId()&&operatorId!=0){
            //查询该执行人是否在黑名单上,如果在就永久拉黑
            int countOperator=blackListService.count(Wrappers.<BlackList>lambdaQuery().eq(BlackList::getQgId,operatorId));
            //增加执行人黑名单实例
            BlackList operator=new BlackList(null,operatorId,2,"违规操作:踢出",new Date(),new Date(),1,0);
            //执行成功判断
            if(countOperator==0){
                blackLists.add(operator);
            }else{
                operator.setForeverDelete(1);
                blackListService.update(Wrappers.<BlackList>lambdaUpdate().set(BlackList::getIsDelete,1).set(BlackList::getForeverDelete,1).eq(BlackList::getQgId,operatorId));
            }
            //查询群是否在黑名单上,如果在就永久拉黑
            int countGroup=blackListService.count(Wrappers.<BlackList>lambdaQuery().eq(BlackList::getQgId,groupId));
            if(countGroup==0){
                BlackList group=new BlackList(null,groupId,1,"违规操作:踢出",new Date(),new Date(),1,0);
                blackLists.add(group);
            }else{
                blackListService.update(Wrappers.<BlackList>lambdaUpdate().set(BlackList::getIsDelete,1).set(BlackList::getForeverDelete,1).eq(BlackList::getQgId,groupId));
            }
            if(blackLists.size()>0){
                blackListService.saveBatch(blackLists);
            }
            BlackMap.addBlackList(operator);
            return MESSAGE_IGNORE;
        }
        return MESSAGE_IGNORE;
    }

    /**
     * 群禁言事件,判断是否是自己被禁言并设置黑名单
     * @throws
     * @author wisdom-guo
     * @date 2021/4/16
     */
    @Override
    public int onGroupBanNotice(@NotNull Bot bot, @NotNull OnebotEvent.GroupBanNoticeEvent event) {
        Long groupId=event.getGroupId();
        Long userId = event.getUserId();
        Long operatorId = event.getOperatorId();
        if(operatorId.equals(1969077760L)){
            return MESSAGE_IGNORE;
        }
        if(userId==bot.getLoginInfo().getUserId()){
            int countOperator=blackListService.count(Wrappers.<BlackList>lambdaQuery().eq(BlackList::getQgId,operatorId));
            BlackList operator=new BlackList(null,operatorId,2,"违规操作:禁言",new Date(),new Date(),1,0);
            if(countOperator==0){
                BlackList group=new BlackList(null,groupId,1,"违规操作:禁言",new Date(),new Date(),1,0);
                blackListService.save(operator);
                blackListService.save(group);
                bot.setGroupLeave(groupId,true);
            }else{
                operator.setForeverDelete(1);
                blackListService.update(Wrappers.<BlackList>lambdaUpdate().set(BlackList::getIsDelete,1).set(BlackList::getForeverDelete,1).eq(BlackList::getQgId,operatorId));
            }
            BlackMap.addBlackList(operator);
            return MESSAGE_IGNORE;
        }
        return MESSAGE_IGNORE;
    }

    /**
      * 加群,判断是否在黑名单上并退群
      * @throws
      * @author wisdom-guo
      * @date 2021/4/16
      */
    @Override
    public int onGroupIncreaseNotice(@NotNull Bot bot, @NotNull OnebotEvent.GroupIncreaseNoticeEvent event) {
        Long groupId=event.getGroupId();
        int count=blackListService.count(Wrappers.<BlackList>lambdaQuery().eq(BlackList::getQgId,groupId).eq(BlackList::getIsDelete,1));
        if(count!=0){
            bot.sendGroupMsg(groupId,"检测到该群在黑名单上!惜风已申请退群",false);
            bot.setGroupLeave(groupId,true);
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        Long userId = event.getUserId();
        String msg=event.getRawMessage();
        Long changeId=Long.valueOf(msg.replaceAll(" ","").replaceAll(".blackdisgroup",""));
        if (msg.indexOf(".black disgroup")!=-1 && userId.equals(1969077760L)){
            boolean result=blackListService.update(Wrappers.<BlackList>lambdaUpdate().set(BlackList::getIsDelete,0).eq(BlackList::getQgId,changeId).eq(BlackList::getForeverDelete,0));
            if(result){
                bot.sendPrivateMsg(userId,"",false);
            }else{
                bot.sendPrivateMsg(userId,"",false);
            }
        }
        if (msg.indexOf(".black disuser")!=-1 && userId.equals(1969077760L)){
            boolean result=blackListService.update(Wrappers.<BlackList>lambdaUpdate().set(BlackList::getIsDelete,0).eq(BlackList::getQgId,changeId).eq(BlackList::getForeverDelete,0));
            if(result){
                BlackMap.removeBlackList(changeId);
                bot.sendPrivateMsg(userId,"",false);
            }else{
                bot.sendPrivateMsg(userId,"",false);
            }
        }
        return MESSAGE_IGNORE;
    }

}

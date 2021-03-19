package com.wisdom.xifeng.plugin;

import com.wisdom.xifeng.entity.QQGroup;
import com.wisdom.xifeng.service.qqgroup.QQGroupSerivce;
import com.wisdom.xifeng.util.BoolUtil;
import lombok.SneakyThrows;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotApi;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * 示例插件
 * 插件必须继承CQPlugin，上面要 @Component
 * <p>
 * 添加事件：光标移动到类中，按 Ctrl+O 添加事件(讨论组消息、加群请求、加好友请求等)
 * 查看API参数类型：光标移动到方法括号中按Ctrl+P
 * 查看API说明：光标移动到方法括号中按Ctrl+Q
 */

@Component
public class GroupPlugin extends BotPlugin {
    /**
     * 收到私聊消息时会调用这个方法
     *
     * @param cq    机器人对象，用于调用API，例如发送私聊消息 sendPrivateMsg
     * @param event 事件对象，用于获取消息内容、群号、发送者QQ等
     * @return 是否继续调用下一个插件，IGNORE表示继续，BLOCK表示不继续
     */

    @Resource
    private QQGroupSerivce qqGroupSerivce;


    @Override
    public int onPrivateMessage(@NotNull Bot cq, @NotNull OnebotEvent.PrivateMessageEvent event) {
        // 获取 发送者QQ 和 消息内容
        long userId = event.getUserId();
        String msg = event.getRawMessage();
        if (msg.equals("更新完成") && userId == 1969077760L) {
            OnebotApi.GetGroupListResp grouplist = cq.getGroupList();

            for(OnebotApi.GetGroupListResp.Group groupData:grouplist.getGroupList()){
                cq.sendGroupMsg(groupData.getGroupId(), "更新完成，本次更新内容请通过.help查看！", false);
            }

            // 不执行下一个插件
            return MESSAGE_IGNORE;
        }
        if (msg.startsWith(".tx")) {
            try {
                String[] msgs = msg.trim().split(" ");
                cq.setGroupSpecialTitle(Long.valueOf(msgs[1]), userId, msgs[2], -1);
                cq.sendPrivateMsg(userId, "惜风已为您成功更换头衔！", false);
            } catch (Exception e) {
                cq.sendPrivateMsg(userId, "操作失败，请重试！", false);
            }
            // 不执行下一个插件
            return MESSAGE_IGNORE;
        }
        if (msg.startsWith(".jy") && userId == 1969077760L) {
            try {
                String[] msgs = msg.trim().split(" ");
                cq.setGroupBan(Long.valueOf(msgs[1]), Long.valueOf(msgs[2]), Integer.valueOf(msgs[3]));
                cq.sendPrivateMsg(userId, "禁言成功！", false);
            } catch (Exception e) {
                cq.sendPrivateMsg(userId, "禁言失败，请重试！", false);
            }
            // 不执行下一个插件
            return MESSAGE_IGNORE;
        }
        if (msg.startsWith(".jj") && userId == 1969077760L) {
            try {
                String[] msgs = msg.trim().split(" ");
                cq.setGroupBan(Long.valueOf(msgs[1]), Long.valueOf(msgs[2]), 0);
                cq.sendPrivateMsg(userId, "解禁成功！", false);
            } catch (Exception e) {
                cq.sendPrivateMsg(userId, "解禁失败，请重试！", false);
            }
            // 不执行下一个插件
            return MESSAGE_IGNORE;
        }

        if (msg.equals("更新中") && userId == 1969077760L) {
            OnebotApi.GetGroupListResp grouplist = cq.getGroupList();

            for(OnebotApi.GetGroupListResp.Group groupData:grouplist.getGroupList()){
                cq.sendGroupMsg(groupData.getGroupId(), "更新中，大家请稍后~", false);
            }

            // 不执行下一个插件
            return MESSAGE_IGNORE;
        }

        // 继续执行下一个插件
        return MESSAGE_IGNORE;
    }


    /**
     * 收到群消息时会调用这个方法
     *
     * @param cq    机器人对象，用于调用API，例如发送群消息 sendGroupMsg
     * @param event 事件对象，用于获取消息内容、群号、发送者QQ等
     * @return 是否继续调用下一个插件，IGNORE表示继续，BLOCK表示不继续
     */
    @SneakyThrows
    @Override
    public int onGroupMessage(@NotNull Bot cq, @NotNull OnebotEvent.GroupMessageEvent event) {
        // 获取 消息内容 群号 发送者QQ
        //获取消息内容
        String msg = event.getRawMessage();
        //获取群号
        long groupId = event.getGroupId();
        //获取发送者QQ
        long userId = event.getUserId();


        String nickname=event.getSender().getNickname();

        if (BoolUtil.startByPoint(msg) || BoolUtil.startByFullStop(msg)) {
            QQGroup qqGroup = qqGroupSerivce.selectAllByID(String.valueOf(groupId));
            if (qqGroup.getXfOpen() == 1) {
                return MESSAGE_IGNORE;
            }
        } else {
            return MESSAGE_IGNORE;
        }

        if(changeTitle(cq,msg,userId,groupId,nickname)){
            return MESSAGE_IGNORE;
        }


        // 继续执行下一个插件
        return MESSAGE_IGNORE;
    }


    public boolean changeTitle(Bot cq,String msg,Long userId,Long groupId,String nickname){
        if (msg.startsWith(".tx")) {
            try {
                String[] msgs=msg.trim().split(" ");
                cq.setGroupSpecialTitle(groupId,userId,msgs[1],-1);
                cq.sendGroupMsg(groupId, nickname+":惜风已为您成功更换头衔", false);
            }catch (Exception e){
                cq.sendGroupMsg(groupId, nickname+" 更换失败，请重试", false);
            }
            // 不执行下一个插件
            return true;
        }else{
            return false;
        }

    }

}
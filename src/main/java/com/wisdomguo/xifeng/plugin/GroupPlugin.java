package com.wisdomguo.xifeng.plugin;

import com.wisdomguo.xifeng.entity.QQGroup;
import com.wisdomguo.xifeng.service.qqgroup.QQGroupSerivce;
import com.wisdomguo.xifeng.util.BlackMap;
import com.wisdomguo.xifeng.util.BoolUtil;
import lombok.SneakyThrows;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import onebot.OnebotApi;
import onebot.OnebotBase;
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

/**
 * GroupPlugin
 * 群管理
 *
 * @author wisdom-guo
 * @since 2020
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
        if(BlackMap.returnBlackList(userId)) {
            return MESSAGE_BLOCK;
        }
        String msg = event.getRawMessage().replaceAll("。",".");
        if ("更新完成".equals(msg) && userId == 1969077760L) {
            OnebotApi.GetGroupListResp grouplist = cq.getGroupList();

            for (OnebotApi.GetGroupListResp.Group groupData : grouplist.getGroupList()) {
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
        if (msg.startsWith(".speech") && userId == 1969077760L) {
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
        if (msg.startsWith(".remove") && userId == 1969077760L) {
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

        if ("更新中".equals(msg) && userId == 1969077760L) {
            OnebotApi.GetGroupListResp grouplist = cq.getGroupList();

            for (OnebotApi.GetGroupListResp.Group groupData : grouplist.getGroupList()) {
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
        String msg = event.getRawMessage().replaceAll("。",".");
        //获取群号
        long groupId = event.getGroupId();
        //获取发送者QQ
        long userId = event.getUserId();
        if(BlackMap.returnBlackList(userId)) {
            return MESSAGE_BLOCK;
        }

        String nickname = event.getSender().getNickname();

        if (BoolUtil.startByPoint(msg) || BoolUtil.startByFullStop(msg)) {
            QQGroup qqGroup = qqGroupSerivce.selectAllByID(String.valueOf(groupId));
            if (qqGroup.getXfOpen() == 1) {
                return MESSAGE_IGNORE;
            }
        } else {
            return MESSAGE_IGNORE;
        }

        if (changeTitle(cq, msg, userId, groupId, nickname)) {
            return MESSAGE_IGNORE;
        }
        String atUserId = "";
        if (event.getMessageList().size() > 1) {
            for (OnebotBase.Message message : event.getMessageList()) {
                if ("at".equals(message.getType())) {
                    atUserId = message.getDataMap().get("qq");
                }
            }
        }
        boolean judgment =  (("owner".equals(event.getSender().getRole())
                || "admin".equals(event.getSender().getRole()))
                || userId == 1969077760L);
        if (msg.startsWith(".speech")
                && judgment) {
            try {
                String[] msgs = msg.trim().split(" ");

                cq.setGroupBan(groupId, Long.valueOf(atUserId), Integer.valueOf(msgs[1]));
                cq.sendGroupMsg(groupId, "禁言成功！", false);
            } catch (Exception e) {
                cq.sendGroupMsg(groupId, "禁言失败，请重试！", false);
            }
            // 不执行下一个插件
            return MESSAGE_IGNORE;
        }

        if (msg.startsWith(".remove")
                &&judgment) {
            try {
                String[] msgs = msg.trim().split(" ");
                cq.setGroupBan(groupId, Long.valueOf(atUserId), 0);
                cq.sendGroupMsg(groupId, "解禁成功！", false);
            } catch (Exception e) {
                cq.sendGroupMsg(groupId, "解禁失败，请重试！", false);
            }
            // 不执行下一个插件
            return MESSAGE_IGNORE;
        }


        // 继续执行下一个插件
        return MESSAGE_IGNORE;
    }


    public boolean changeTitle(Bot cq, String msg, Long userId, Long groupId, String nickname) {
        if (msg.startsWith(".tx")) {
            try {
                String[] msgs = msg.trim().split(" ");
                cq.setGroupSpecialTitle(groupId, userId, msgs[1], -1);
                cq.sendGroupMsg(groupId, nickname + ":惜风已为您成功更换头衔", false);
            } catch (Exception e) {
                cq.sendGroupMsg(groupId, nickname + " 更换失败，请重试", false);
            }
            // 不执行下一个插件
            return true;
        } else {
            return false;
        }

    }

}

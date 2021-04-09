package com.wisdomguo.xifeng.plugin;

import com.wisdomguo.xifeng.entity.QQGroup;
import com.wisdomguo.xifeng.service.qqgroup.QQGroupSerivce;
import com.wisdomguo.xifeng.util.BoolUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
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

@Slf4j
@Component
public class SetXFPlugin extends BotPlugin {

    @Resource
    private QQGroupSerivce qqGroupSerivce;


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
        //获取发送者的所有信息

        setXf(cq, msg, groupId, event);
        if (BoolUtil.startByPoint(msg) || BoolUtil.startByFullStop(msg)) {
            QQGroup qqGroup = qqGroupSerivce.selectAllByID(String.valueOf(groupId));
            if (qqGroup.getXfOpen() == 1) {
                return MESSAGE_IGNORE;
            } else {
                setDice(cq, msg, groupId, event);
            }
        } else {
            return MESSAGE_IGNORE;
        }
        // 继续执行下一个插件
        return MESSAGE_IGNORE;
    }

    //开关骰娘
    private void setXf(@NotNull Bot cq, String msg, long groupId, @NotNull OnebotEvent.GroupMessageEvent event) {
        QQGroup qqGroup = qqGroupSerivce.selectAllByID(String.valueOf(groupId));
        if (msg.startsWith(".xf")&&(event.getSender().getRole().equals("owner") || event.getSender().getRole().equals("admin"))) {

            if (msg.startsWith(".xf")) {
                if (msg.equals(".xf off")) {
//                    coutType = 1;
                    if (qqGroup.getXfOpen() == 1) {
                        cq.sendGroupMsg(groupId, "惜风还未进行工作准备哦！", false);
                    } else {
                        qqGroupSerivce.updateOpenCloseXF(String.valueOf(groupId), 1);
                        cq.sendGroupMsg(groupId, "那我就先去休息了，惜风随叫随到哦！", false);
                    }
                } else if (msg.equals(".xf on")) {
//                    coutType = 0;
                    if (qqGroup.getXfOpen() == 0) {
                        cq.sendGroupMsg(groupId, "惜风还没有离开哦！", false);
                    } else {
                        qqGroupSerivce.updateOpenCloseXF(String.valueOf(groupId), 0);
                        cq.sendGroupMsg(groupId, "今天惜风又要开始工作了呢", false);
                    }

                }
            }

        } else {
            if (msg.startsWith(".xf")) {
                cq.sendGroupMsg(groupId, "只有管理员和群主可以进行操作哦！", false);
            }
        }
    }

    private void setDice(@NotNull Bot cq, String msg,long groupId, @NotNull OnebotEvent.GroupMessageEvent event) {
        QQGroup qqGroup = qqGroupSerivce.selectAllByID(String.valueOf(groupId));
        if (msg.startsWith(".dice")&&(event.getSender().getRole().equals("owner") || event.getSender().getRole().equals("admin"))) {

            if (msg.startsWith(".dice")) {
                if (msg.equals(".dice off")) {
//                    coutType = 1;
                    if (qqGroup.getDiceOpen() == 1) {
                        cq.sendGroupMsg(groupId, "宇宙真理检索系统尚未打开！", false);
                    } else {
                        qqGroupSerivce.updateOpenCloseDice(String.valueOf(groupId), 1);
                        cq.sendGroupMsg(groupId, "宇宙真理检索系统已经关闭，需要时惜风会帮您重启！", false);
                    }
                } else if (msg.equals(".dice on")) {
//                    coutType = 0;
                    if (qqGroup.getDiceOpen() == 0) {
                        cq.sendGroupMsg(groupId, "宇宙真理检索系统还未关闭！", false);
                    } else {
                        qqGroupSerivce.updateOpenCloseDice(String.valueOf(groupId), 0);
                        cq.sendGroupMsg(groupId, "宇宙真理检索系统已经重启，惜风将持续为您服务！", false);
                    }

                }
            }

        } else {
            if (msg.startsWith(".dice")) {
                cq.sendGroupMsg(groupId, "只有管理员和群主可以进行操作哦！", false);
            }
        }
    }

}

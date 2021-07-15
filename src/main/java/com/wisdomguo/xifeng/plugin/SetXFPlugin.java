package com.wisdomguo.xifeng.plugin;

import com.wisdomguo.xifeng.modules.botset.qqgroup.entity.QQGroup;
import com.wisdomguo.xifeng.modules.botset.qqgroup.service.QQGroupSerivce;
import com.wisdomguo.xifeng.assist.BlackMap;
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
        if(BlackMap.returnBlackList(userId)) {
            return MESSAGE_BLOCK;
        }

        //设置惜风开启
        setXf(cq, msg, groupId, event);

        if(msg.startsWith(".xf dismiss")){
            cq.sendGroupMsg(groupId, "那么，惜风就先回自己的实验室了，期待我们下一次的相遇。", false);
            cq.setGroupLeave(groupId,true);
        }

        if (BoolUtil.startByPoint(msg) || BoolUtil.startByFullStop(msg)) {
            QQGroup qqGroup = qqGroupSerivce.selectAllByID(String.valueOf(groupId));
            if (qqGroup.getXfOpen() == 1) {
                return MESSAGE_IGNORE;
            } else {
                //设置骰子开启
                setDice(cq, msg, groupId, event);
                //设置每日功能开启
                setOther(cq, msg, groupId, event);
                //设置娱乐功能开启
                setGame(cq, msg, groupId, event);
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
        if (msg.startsWith(".xf")&&(event.getSender().getRole().equals("owner") || event.getSender().getRole().equals("admin")|| event.getUserId() == 1969077760L)) {

            if (msg.startsWith(".xf")) {
                if (msg.equals(".xf off")) {
//                    coutType = 1;
                    if (qqGroup.getXfOpen() == 1) {
                        cq.sendGroupMsg(groupId, "惜风还未进行工作准备哦！（惜风未开启）", false);
                    } else {
                        qqGroupSerivce.updateOpenCloseXF(String.valueOf(groupId), 1);
                        cq.sendGroupMsg(groupId, "那我就先去休息了，惜风随叫随到哦！", false);
                    }
                } else if (msg.equals(".xf on")) {
//                    coutType = 0;
                    if (qqGroup.getXfOpen() == 0) {
                        cq.sendGroupMsg(groupId, "惜风还没有离开哦！（惜风未关闭）", false);
                    } else {
                        qqGroupSerivce.updateOpenCloseXF(String.valueOf(groupId), 0);
                        cq.sendGroupMsg(groupId, "今天惜风又要开始工作了呢！", false);
                    }

                }
            }

        } else {
            if (msg.startsWith(".xf")&& msg.indexOf("help")==-1) {
                cq.sendGroupMsg(groupId, "只有管理员和群主可以进行操作哦！", false);
            }
        }
    }

    private void setDice(@NotNull Bot cq, String msg,long groupId, @NotNull OnebotEvent.GroupMessageEvent event) {
        QQGroup qqGroup = qqGroupSerivce.selectAllByID(String.valueOf(groupId));
        if (msg.startsWith(".dice")&&(event.getSender().getRole().equals("owner") || event.getSender().getRole().equals("admin")|| event.getUserId() == 1969077760L)) {

            if (msg.startsWith(".dice")) {
                if (msg.equals(".dice off")) {
//                    coutType = 1;
                    if (qqGroup.getDiceOpen() == 1) {
                        cq.sendGroupMsg(groupId, "真理检索系统尚未打开！（骰子功能未开启）", false);
                    } else {
                        qqGroupSerivce.updateOpenCloseDice(String.valueOf(groupId), 1);
                        cq.sendGroupMsg(groupId, "真理检索系统已经关闭，需要时惜风会帮您重启！", false);
                    }
                } else if (msg.equals(".dice on")) {
//                    coutType = 0;
                    if (qqGroup.getDiceOpen() == 0) {
                        cq.sendGroupMsg(groupId, "真理检索系统还未关闭！（骰子功能未关闭）", false);
                    } else {
                        qqGroupSerivce.updateOpenCloseDice(String.valueOf(groupId), 0);
                        cq.sendGroupMsg(groupId, "真理检索系统已经重启，惜风将持续为您服务！", false);
                    }

                }
            }

        } else {
            if (msg.startsWith(".dice") && msg.indexOf("help")==-1) {
                cq.sendGroupMsg(groupId, "只有管理员和群主可以进行操作哦！", false);
            }
        }
    }

    private void setOther(@NotNull Bot cq, String msg,long groupId, @NotNull OnebotEvent.GroupMessageEvent event) {
        QQGroup qqGroup = qqGroupSerivce.selectAllByID(String.valueOf(groupId));
        if (msg.startsWith(".daily")&&(event.getSender().getRole().equals("owner") || event.getSender().getRole().equals("admin")|| event.getUserId() == 1969077760L)) {

            if (msg.startsWith(".daily")) {
                if (msg.equals(".daily off")) {
//                    coutType = 1;
                    if (qqGroup.getOtherOpen() == 1) {
                        cq.sendGroupMsg(groupId, "今日的运势如何呢？需要惜风帮你占卜一下吗？（每日功能未开启）", false);
                    } else {
                        qqGroupSerivce.updateOpenCloseOther(String.valueOf(groupId), 1);
                        cq.sendGroupMsg(groupId, "那么惜风就把这些收起来啦，记得要好好工作。", false);
                    }
                } else if (msg.equals(".daily on")) {
//                    coutType = 0;
                    if (qqGroup.getOtherOpen() == 0) {
                        cq.sendGroupMsg(groupId, "还是需要惜风帮忙占卜吗？不过其实更加努力才会发生更多好的事情哦！（每日功能未关闭）", false);
                    } else {
                        qqGroupSerivce.updateOpenCloseOther(String.valueOf(groupId), 0);
                        cq.sendGroupMsg(groupId, "今日的运势如何呢？让惜风来帮你看看吧！", false);
                    }

                }
            }

        } else {
            if (msg.startsWith(".daily") && msg.indexOf("help")==-1) {
                cq.sendGroupMsg(groupId, "只有管理员和群主可以进行操作哦！", false);
            }
        }
    }

    private void setGame(@NotNull Bot cq, String msg,long groupId, @NotNull OnebotEvent.GroupMessageEvent event) {
        QQGroup qqGroup = qqGroupSerivce.selectAllByID(String.valueOf(groupId));
        if (msg.startsWith(".game")&&(event.getSender().getRole().equals("owner") || event.getSender().getRole().equals("admin")|| event.getUserId() == 1969077760L)) {

            if (msg.startsWith(".game")) {
                if (msg.equals(".game off")) {
                    if (qqGroup.getGameOpen() == 1) {
                        cq.sendGroupMsg(groupId, "工作累了吗？需要惜风帮你放松吗？（游戏功能未开启）", false);
                    } else {
                        qqGroupSerivce.updateOpenCloseGame(String.valueOf(groupId), 1);
                        cq.sendGroupMsg(groupId, "惜风提醒您，要认真起来做正事了哦。", false);
                    }
                } else if (msg.equals(".game on")) {
                    if (qqGroup.getGameOpen() == 0) {
                        cq.sendGroupMsg(groupId, "还需要在休息一会吗？惜风还在这里陪着您哦！（游戏功能未关闭）", false);
                    } else {
                        qqGroupSerivce.updateOpenCloseGame(String.valueOf(groupId), 0);
                        cq.sendGroupMsg(groupId, "想要惜风陪您打发时间吗？这样子的悠闲时光也不坏呢。", false);
                    }

                }
            }

        } else {
            if (msg.startsWith(".game") && msg.indexOf("help")==-1) {
                cq.sendGroupMsg(groupId, "只有管理员和群主可以进行操作哦！", false);
            }
        }
    }

}

package com.wisdomguo.xifeng.plugin;

import com.wisdomguo.xifeng.service.cardaddress.CardAddressSerivce;
import com.wisdomguo.xifeng.service.qqgroup.QQGroupSerivce;
import com.wisdomguo.xifeng.util.BlackMap;
import lombok.SneakyThrows;
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
/**
 * HelpPlugin
 * 帮助文档
 * @author wisdom-guo
 * @since 2020
 */
@Component
public class HelpPlugin extends BotPlugin {
    /**
     * 收到私聊消息时会调用这个方法
     *
     * @param cq    机器人对象，用于调用API，例如发送私聊消息 sendPrivateMsg
     * @param event 事件对象，用于获取消息内容、群号、发送者QQ等
     * @return 是否继续调用下一个插件，IGNORE表示继续，BLOCK表示不继续
     */


    @Resource
    private CardAddressSerivce cardAddressSerivce;
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

        if (msg.startsWith(".help")) {
            StringBuffer sb = new StringBuffer("以下是惜风的功能列表，有什么需要帮助的嘛？");
            sb.append("\n.dice help\t骰娘系统功能");
            sb.append("\n.file help\t角色卡地址功能");
            sb.append("\n.group help\t群管理功能");
            sb.append("\n.other help\t群娱乐功能");
            sb.append("\n.xf on\t惜风开启");
            sb.append("\n.xf off\t惜风待机");
            sb.append("\n.dice on\t骰子开启");
            sb.append("\n.dice off\t骰子待机");

            sb.append("\n");
            sb.append("\n本次更新内容：\n惜风的开启关闭系统更新了！\t帮助命令：.help");
            cq.sendPrivateMsg(userId, sb.toString(), false);
            return MESSAGE_IGNORE;
        }

        if (msg.startsWith(".dice help")) {
            StringBuffer sb = getTrpgHelpList();
            cq.sendPrivateMsg(userId, sb.toString(), false);
            return MESSAGE_IGNORE;
        }

        if (msg.startsWith(".file help")) {
            StringBuffer sb = getCardAddressHelpList();
            cq.sendPrivateMsg(userId, sb.toString(), false);
            return MESSAGE_IGNORE;
        }

        if (msg.startsWith(".group help")) {
            StringBuffer sb = getGroupHelpList();
            cq.sendPrivateMsg(userId, sb.toString(), false);
            return MESSAGE_IGNORE;
        }

        if (msg.startsWith(".card help")) {
            StringBuffer sb = getGroupHelpList();
            cq.sendPrivateMsg(userId, sb.toString(), false);
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
         if (msg.startsWith(".help")) {
            StringBuffer sb = new StringBuffer("以下是惜风的功能列表，有什么需要帮助的嘛？");
             sb.append("\n.file help 角色卡地址功能");
             sb.append("\n.xf help   惜风管理功能");
            sb.append("\n.dice help  骰娘系统功能");
            sb.append("\n.group help 群管理功能");
            sb.append("\n.luck help  娱乐功能①");
            sb.append("\n.card help  coc角色卡");
            sb.append("\n");
            sb.append("\n本次更新内容：");
            sb.append("\n复合骰/黑名单/复读功能");
            sb.append("\n下版本更新：复合骰");
            cq.sendGroupMsg(groupId, sb.toString(), false);
            return MESSAGE_IGNORE;
        }

        if (msg.startsWith(".dice help")) {
            StringBuffer sb = getTrpgHelpList();
            cq.sendGroupMsg(groupId, sb.toString(), false);
            return MESSAGE_IGNORE;
        }
        if (msg.startsWith(".xf help")) {
            StringBuffer sb = getXfHelpList();
            cq.sendGroupMsg(groupId, sb.toString(), false);
            return MESSAGE_IGNORE;
        }

        if (msg.startsWith(".file help")) {
            StringBuffer sb = getCardAddressHelpList();
            cq.sendGroupMsg(groupId, sb.toString(), false);
            return MESSAGE_IGNORE;
        }

        if (msg.startsWith(".group help")) {
            StringBuffer sb = getGroupHelpList();
            cq.sendGroupMsg(groupId, sb.toString(), false);
            return MESSAGE_IGNORE;
        }

        if (msg.startsWith(".luck help")) {
            StringBuffer sb = getOtherHelpList();
            cq.sendGroupMsg(groupId, sb.toString(), false);
            return MESSAGE_IGNORE;
        }

        if (msg.startsWith(".card help")) {
            StringBuffer sb = getCardHelpList();
            cq.sendGroupMsg(groupId, sb.toString(), false);
            return MESSAGE_IGNORE;
        }

        // 继续执行下一个插件
        return MESSAGE_IGNORE;
    }

    private StringBuffer getTrpgHelpList() {
        StringBuffer sb = new StringBuffer("以下是惜风的TRPG系统功能：");
        sb.append("\n自动省略空格,trpg规则中无论参数多少，结果向下取整");
        sb.append("\n明投：  .r骰子数d点数");
        sb.append("\n明投100：  .rd");
        sb.append("\n暗投：  .rh骰子数d点数");
        sb.append("\n运算规则：");
        sb.append("\n先加减：  .r骰子数d点数+数值*数值");
        sb.append("\n先乘法：  .r骰子数d点数*数值+数值");
        sb.append("\n例1：.r1d2+1*2=(1+1)*2=4");
        sb.append("\n例2：.r1d2*2+2=(2*2)+2=6");
        sb.append("\n文字规则：  .r1d100(文字");
        sb.append("\ncoc车卡：  .coc车卡数量（不要过大");
        sb.append("\ndnd车卡：  .dnd车卡数量（不要过大");
        return sb;
    }

    private StringBuffer getXfHelpList() {
        StringBuffer sb = new StringBuffer("以下是惜风的TRPG系统功能：");
        sb.append("\n.xf on\t惜风开启");
        sb.append("\n.xf off\t惜风待机");
        sb.append("\n.dice on\t骰子开启");
        sb.append("\n.dice off\t骰子待机");
        return sb;
    }

    private StringBuffer getGroupHelpList() {
        StringBuffer sb = new StringBuffer("以下是惜风的群管理系统功能：");
        sb.append("\n私聊（需好友）：");
        sb.append("\n.speech 禁言群组ID 被禁言人ID 时间（单位秒）");
        sb.append("\n.remove 解禁群组ID 被解禁人ID");
        sb.append("\n.tx 更换群组ID 修改头衔");
        sb.append("\n群聊:");
        sb.append("\n.tx 修改头衔");
        sb.append("\n.speech 时间（单位秒） at被禁言人");
        sb.append("\n.remove at被禁言人");
        sb.append("\n注意：禁言解禁目前仅限开发者和群管理使用");
        return sb;
    }

    private StringBuffer getOtherHelpList() {
        StringBuffer sb = new StringBuffer("以下是惜风的娱乐功能：");
        sb.append("\n.jrrp 今日人品值（每日一次）");
        sb.append("\n.tarot 每日塔罗（每日一次）");
        sb.append("\n.magic xf 惜风的幸运魔法（每日一次）");
        return sb;
    }

    private StringBuffer getCardAddressHelpList() {
        StringBuffer sb = new StringBuffer("以下是惜风的角色卡（在线文档）功能：");
        sb.append("\n添加角色卡：  .addcard 卡名称 卡地址");
        sb.append("\n删除角色卡：  .delcard 角色卡id");
        sb.append("\n查看本人角色卡：  .findcard");
        sb.append("\n查看个人角色卡：  .findcard @群成员");
        sb.append("\n查看全群角色卡：  .findallcard");
        sb.append("\n注：请注意空格，删除只能本人或者管理员进行操作！");
        return sb;
    }

    private StringBuffer getCardHelpList() {
        StringBuffer sb = new StringBuffer("以下是惜风的角色卡功能：");
        sb.append("\n设置角色卡:   .st技能名技能数值");
        sb.append("\n带名称角色卡: .st 卡名 技能名技能数值");
        sb.append("\n本群角色列表: .pc list");
        sb.append("\n默认角色列表: .pc grp");
        sb.append("\n重命名角色卡: .pc rename 卡序号 卡名");
        sb.append("\n修改默认角色: .pc bind 卡序号");
        sb.append("\n删除角色卡:   .pc del 卡序号");
        sb.append("\n清理角色卡:   .pc clr");
        sb.append("\n修改默认技能: .ch技能名 技能数值");
        sb.append("\n修改默认San:  .stsan -1");
        sb.append("\n修改默认HP:   .sthp -1");
        sb.append("\n修改默认MP:   .stmp -1");
        sb.append("\nSan Check:   .sc 成功骰/失败骰");
        sb.append("\n技能判定:     .ra技能名");
        return sb;
    }

}

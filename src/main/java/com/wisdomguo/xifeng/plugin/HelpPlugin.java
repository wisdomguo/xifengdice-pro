package com.wisdomguo.xifeng.plugin;

import com.wisdomguo.xifeng.modules.dice.cardaddress.service.CardAddressSerivce;
import com.wisdomguo.xifeng.modules.botset.qqgroup.entity.QQGroup;
import com.wisdomguo.xifeng.modules.botset.qqgroup.service.QQGroupSerivce;
import com.wisdomguo.xifeng.assist.BlackMap;
import com.wisdomguo.xifeng.util.BoolUtil;
import lombok.SneakyThrows;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
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

        String atUserId = "";
        if (event.getMessageList().size() > 1) {
            for (OnebotBase.Message message : event.getMessageList()) {
                if ("at".equals(message.getType())) {
                    atUserId = message.getDataMap().get("qq");
                }
            }
        }

        if (BoolUtil.startByPoint(msg) || BoolUtil.startByFullStop(msg) || msg.equals("帮助") || msg.equals("#用户协议")) {
            QQGroup qqGroup = qqGroupSerivce.selectAllByID(String.valueOf(groupId));
            if (qqGroup.getXfOpen() == 1 && !atUserId.equals("1515044906")) {
                return MESSAGE_IGNORE;
            }
        } else {
            return MESSAGE_IGNORE;
        }

         if (msg.startsWith(".help") || msg.equals("帮助")) {
            StringBuffer sb = new StringBuffer("以下是惜风的功能列表，有什么需要帮助的嘛？");
             sb.append("\n.file help 角色卡地址功能");
             sb.append("\n.xf help   惜风管理功能");
            sb.append("\n.dice help  骰娘系统功能");
            sb.append("\n.group help 群管理功能");
            sb.append("\n.luck help  每日功能");
            sb.append("\n.card help  coc角色卡");
            sb.append("\n.game help  娱乐功能");
            sb.append("\n#用户协议    阅读用户协议");
            sb.append("\n惜风设计编程:\n不知归（1969077760）");
            sb.append("\n惜风文案编辑:\n法露特（984641292）");
             sb.append("\n请注意！请用惜风前请先阅读《用户协议》");
            sb.append("\n");
            sb.append("\n本次更新：星空·不删档测试版");
            sb.append("\n下版本更新：骰娘正经功能");
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

        if (msg.startsWith(".game help")) {
            StringBuffer sb = getGameHelpList();
            cq.sendGroupMsg(groupId, sb.toString(), false);
            return MESSAGE_IGNORE;
        }

        if (msg.startsWith("#用户协议")) {
            StringBuffer sb = new StringBuffer();
            sb.append("0.惜风为群友自制骰娘，但同样使用OlivaDice(DIXE)默认服务协议。如果你看到了这句话，意味着Master应用默认协议，请注意。\n" +
                    "1.邀请骰娘、使用掷骰服务和在群内阅读此协议视为同意并承诺遵守此协议，否则请使用.xf dismiss移出骰娘。\n" +
                    "2.不允许禁言、移出骰娘或刷屏掷骰等对骰娘的不友善行为，这些行为将会提高骰娘被制裁的风险。开关骰娘响应请使用.xf on/off。\n" +
                    "3.骰娘默认邀请行为已事先得到群内同意，因而会自动同意群邀请。因擅自邀请而使骰娘遭遇不友善行为时，邀请者因未履行预见义务而将承担连带责任。\n" +
                    "4.禁止将骰娘用于赌博及其他违法犯罪行为。\n" +
                    "5.对于设置敏感昵称等无法预见但有可能招致言论审查的行为，骰娘可能会出于自我保护而拒绝提供服务\n" +
                    "6.由于技术以及资金原因，我们无法保证机器人100%的时间稳定运行，可能不定时停机维护或遭遇冻结，但是相应情况会及时通过各种渠道进行通知，敬请谅解。临时停机的骰娘不会有任何响应，故而不会影响群内活动，此状态下仍然禁止不友善行为。\n" +
                    "7.对于违反协议的行为，骰娘将视情况终止对用户和所在群提供服务，并将不良记录共享给其他服务提供方。黑名单相关事宜可以与服务提供方协商，但最终裁定权在服务提供方。\n" +
                    "8.本协议内容随时有可能改动。请注意帮助信息、签名、空间、官方群等处的骰娘动态。\n" +
                    "9.骰娘提供掷骰服务是完全免费的，欢迎投食。\n" +
                    "10.本服务最终解释权归服务提供方所有。\n" +
                    "11.如有问题请加群583488577询问。");
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
        sb.append("\n.daily on\t每日开启");
        sb.append("\n.daily off\t每日关闭");
        sb.append("\n.game on\t娱乐开启");
        sb.append("\n.game off\t娱乐关闭");
        sb.append("\n.xf dismiss\t惜风退群");
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

    private StringBuffer getGameHelpList() {
        StringBuffer sb = new StringBuffer("以下是惜风的娱乐功能：");
        sb.append("\n星空探索:   进行一次星空探索");
        sb.append("\n查看背包:   查看个人星空背包");
        sb.append("\n星空排行:   查看星屑和星碎的整体排行");
        sb.append("\n星屑排行:   查看星屑排行");
        sb.append("\n星币排行:   查看用户的星币排行");
        sb.append("\n星空转轮:   10星碎抽奖一次,可获得星币和种子奖励");
        sb.append("\n种子口袋:   查看自己种子口袋");
        sb.append("\n作物仓库:   查看自己已成熟的果实");
        sb.append("\n我的农业:   查看自己目前的农业信息");
        sb.append("\n我的田地:   查看自己正在种植的作物");
        sb.append("\n农业商店:   查看可购买的作物列表");
        sb.append("\n购买[作物名][n]:   购买商店里的作物");
        sb.append("\n种植[作物名][n]:   在田地里种植该作物");
        sb.append("\n收获[作物名]:      收获所有该种作物");
        sb.append("\n出售[作物名][n]:   出售n个作物");
        sb.append("\n星碎兑换[n]:   n*100星屑兑换成n星碎");
        sb.append("\n修复田地[n]:   n*1000星屑修复n块损坏的田地");
        sb.append("\n购置田地:      花费n-1星币购买第n块田地");
        sb.append("\n随机加速:      使用加速卡随机加速一块田地");
        sb.append("\n随机偷菜:      随机偷菜，一天3次机会");
        sb.append("\n新手礼包:      获取新手礼包");
        sb.append("\n转账@用户，n:   给别人转账不超过500（n）的星屑");
        sb.append("\n备注1:星币和星碎作物有种植失败概率");
        sb.append("\n备注2:发送“星空系统详解”查看农场功能详细介绍");
        sb.append("\n备注3:每日会对星屑不足200玩家补足200星屑\n每两天会自动修复一块被侵蚀的田地\n每周会赠送一张加速卡");
        return sb;
    }

}

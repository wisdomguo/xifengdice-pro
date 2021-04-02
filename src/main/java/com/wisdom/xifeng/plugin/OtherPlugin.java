package com.wisdom.xifeng.plugin;

import com.wisdom.xifeng.entity.CardAddress;
import com.wisdom.xifeng.entity.QQGroup;
import com.wisdom.xifeng.service.cardaddress.CardAddressSerivce;
import com.wisdom.xifeng.service.qqgroup.QQGroupSerivce;
import com.wisdom.xifeng.util.BoolUtil;
import com.wisdom.xifeng.util.HttpClientUtil;
import lombok.SneakyThrows;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotContainer;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotApi;
import onebot.OnebotBase;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;


/**
 * 示例插件
 * 插件必须继承CQPlugin，上面要 @Component
 * <p>
 * 添加事件：光标移动到类中，按 Ctrl+O 添加事件(讨论组消息、加群请求、加好友请求等)
 * 查看API参数类型：光标移动到方法括号中按Ctrl+P
 * 查看API说明：光标移动到方法括号中按Ctrl+Q
 */

@Component
public class OtherPlugin extends BotPlugin {
    /**
     * 收到私聊消息时会调用这个方法
     *
     * @param cq    机器人对象，用于调用API，例如发送私聊消息 sendPrivateMsg
     * @param event 事件对象，用于获取消息内容、群号、发送者QQ等
     * @return 是否继续调用下一个插件，IGNORE表示继续，BLOCK表示不继续
     */


    @Autowired
    BotContainer botInf;

    @Resource
    private CardAddressSerivce cardAddressSerivce;

    @Resource
    private QQGroupSerivce qqGroupSerivce;

    private Map<String, Integer> jrrp = new HashMap();
    private Map<String, Integer> tarotList = new HashMap();
    private Map<String, Integer> magicList = new HashMap();

    private String getTarot(Integer id){
        switch(id){
            case 0:
                return "【0】愚者-正置";
            case 1:
                return "【I】魔术师-正置";
            case 2:
                return "【II】女祭司-正置";
            case 3:
                return "【III】女皇-正置";
            case 4:
                return "【IV】皇帝-正置";
            case 5:
                return "【V】教皇-正置";
            case 6:
                return "【VI】恋人-正置";
            case 7:
                return "【VII】战车-正置";
            case 8:
                return "【VIII】力量-正置";
            case 9:
                return "【IX】隐者-正置";
            case 10:
                return "【X】命运之轮-正置";
            case 11:
                return "【XI】正义-正置";
            case 12:
                return "【XII】倒吊人-正置";
            case 13:
                return "【XIII】死神-正置";
            case 14:
                return "【XIV】节制-正置";
            case 15:
                return "【XV】恶魔-正置";
            case 16:
                return "【XVI】塔-正置";
            case 17:
                return "【XVII】星星-正置";
            case 18:
                return "【XVIII】月亮-正置";
            case 19:
                return "【XIX】太阳-正置";
            case 20:
                return "【XX】审判-正置";
            case 21:
                return "【XXI】世界-正置";
            case 22:
                return "【0】愚者-倒置";
            case 23:
                return "【I】魔术师-倒置";
            case 24:
                return "【II】女祭司-倒置";
            case 25:
                return "【III】女皇-倒置";
            case 26:
                return "【IV】皇帝-倒置";
            case 27:
                return "【V】教皇-倒置";
            case 28:
                return "【VI】恋人-倒置";
            case 29:
                return "【VII】战车-倒置";
            case 30:
                return "【VIII】力量-倒置";
            case 31:
                return "【IX】隐者-倒置";
            case 32:
                return "【X】命运之轮-倒置";
            case 33:
                return "【XI】正义-倒置";
            case 34:
                return "【XII】倒吊人-倒置";
            case 35:
                return "【XIII】死神-倒置";
            case 36:
                return "【XIV】节制-倒置";
            case 37:
                return "【XV】恶魔-倒置";
            case 38:
                return "【XVI】塔-倒置";
            case 39:
                return "【XVII】星星-倒置";
            case 40:
                return "【XVIII】月亮-倒置";
            case 41:
                return "【XIX】太阳-倒置";
            case 42:
                return "【XX】审判-倒置";
            case 43:
                return "【XXI】审判-倒置";
            default:
                return "";
        }
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Shanghai")
    public void captainClub() {
        Map param = new HashMap();
        String result = HttpClientUtil.httpGetWithJSON("http://haruka.gruiheng.com/captain/todo", param);
        botInf.getBots().get(1515044906L).sendPrivateMsg(531995457L, "今日舰长名单已更新,结果为：" + result, false);
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Shanghai")
    public void jrrpClear() {
        jrrp = new HashMap<>();
        tarotList = new HashMap<>();
        magicList = new HashMap<>();
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

        String atUserId = "";
        if (event.getMessageList().size() > 1) {
            for (OnebotBase.Message message : event.getMessageList()) {
                if (message.getType().equals("at")) {
                    atUserId = message.getDataMap().get("qq");
                }
            }
        }
        //获取发送者的所有信息


        String nickname=event.getSender().getNickname();

        if (BoolUtil.startByPoint(msg) || BoolUtil.startByFullStop(msg)) {
            QQGroup qqGroup = qqGroupSerivce.selectAllByID(String.valueOf(groupId));
            if (qqGroup.getXfOpen() == 1) {
                return MESSAGE_IGNORE;
            }
        } else {
            return MESSAGE_IGNORE;
        }

        if (msg.indexOf(".jrrp") != -1) {

            String key = String.valueOf(userId);

            if (jrrp.get(key) == null) {
                Random random = new Random();
                int rp = random.nextInt(100) + 1;
                StringBuffer rpStr = new StringBuffer("今日人品值为：" + rp);
                if (rp == 100) {
                    rpStr.append("，运气这么好，难道今天是你的幸运日？");
                } else if (rp >= 75) {
                    rpStr.append("，运气还不错，祝你今天事事顺心！");
                } else if (rp >= 50) {
                    rpStr.append("，运气还可以，今天也会是平稳的一天！");
                } else {
                    rpStr.append("，需要惜风施展一次幸运魔法吗？请投掷.magic xf");
                }
                jrrp.put(key, rp);
                cq.sendGroupMsg(groupId, nickname+(rpStr.toString()), false);
            } else {
                cq.sendGroupMsg(groupId, nickname+"您今天已进行过人品测试(" + jrrp.get(key).toString() + ")，明天再来吧！", false);
            }

            return MESSAGE_IGNORE;
        }

        if (msg.indexOf(".magic xf") != -1) {

            String key = String.valueOf(userId);

            if(jrrp.get(key)==null){
                cq.sendGroupMsg(groupId, "您还没有投掷今日人品哦...", false);
                return MESSAGE_IGNORE;
            }
            if (magicList.get(key) == null && jrrp.get(key)<50) {
                Random random = new Random();
                int rp = random.nextInt(100) + 1;
                StringBuffer rpStr = new StringBuffer("今日人品值更新为：" );
                if (rp == 100) {
                    rpStr.append(rp+"，运气这么好，难道今天是你的幸运日？");
                } else if (rp >= 75) {
                    rpStr.append(rp+"，运气还不错，祝你今天事事顺心！");
                } else if (rp >= 50) {
                    rpStr.append(rp+"，运气还可以，今天也会是平稳的一天！");
                } else {
                    rp=random.nextInt(10)+50;
                    rpStr.append(rp+"，运气还可以，今天也会是平稳的一天！");
                }
                magicList.put(key, rp);
                jrrp.put(key, rp);
                cq.sendGroupMsg(groupId, "命运之神的骰子重新转动中...", false);
                Thread.sleep(1000);
                cq.sendGroupMsg(groupId, nickname+(rpStr.toString()), false);
            } else {
                cq.sendGroupMsg(groupId, nickname+"您现在不能使用惜风的魔法哦！", false);
            }

            return MESSAGE_IGNORE;
        }

        if (msg.indexOf(".tarot") != -1) {
            Random random = new Random();
            int tarot = random.nextInt(22);
            String tarotStr = "" + tarot;
            if (tarot < 10) {
                tarotStr = "0" + tarotStr;
            }
            int PN = random.nextInt(2);
            if(PN==1){
                tarot+=22;
            }
            String key = String.valueOf(userId);
            if (tarotList.get(key) == null) {
                tarotList.put(key, tarot);
                cq.sendGroupMsg(groupId, nickname+"您抽取到的为:"+getTarot(tarotList.get(key))+"", false);
                cq.sendGroupMsg(groupId, Msg.builder().image("http://ali.gruiheng.com:8888/" + tarotStr + ".png"), false);

            } else {
                cq.sendGroupMsg(groupId, nickname+"您今天已进行抽取过塔罗:\n"+getTarot(tarotList.get(key))+"\n明天再来吧！", false);
            }

            return MESSAGE_IGNORE;
        }

        if (msg.indexOf("card") != -1 && (msg.indexOf(".") != -1 || msg.indexOf("。") != -1)) {
            if (msg.startsWith(".addcard") || msg.startsWith("。addcard")) {
                String card[] = msg.split(" ");
                if (!atUserId.equals("")) {
                    userId = Long.valueOf(atUserId);
                }
                CardAddress cardAddress = new CardAddress(0, card[1], card[2], String.valueOf(userId));
                if (cardAddressSerivce.addCardAddress(cardAddress) > 0) {
                    cq.sendGroupMsg(groupId, "添加成功", false);
                } else {
                    cq.sendGroupMsg(groupId, "添加失败，请重试！", false);
                }
                return MESSAGE_IGNORE;
            }

            if (msg.startsWith(".delcard") || msg.startsWith("。delcard")) {
                String card[] = msg.split(" ");
                boolean admin = false;
                if ((event.getSender().getRole().equals("admin") || event.getSender().getRole().equals("owner"))) {
                    admin = true;
                }
                if (cardAddressSerivce.deleteCardAddress(Integer.valueOf(card[1]), String.valueOf(userId), admin) > 0) {
                    cq.sendGroupMsg(groupId, "删除成功", false);
                } else {
                    cq.sendGroupMsg(groupId, "删除失败，请重试！", false);
                }
                return MESSAGE_IGNORE;
            }

            if (msg.startsWith(".findcard") || msg.startsWith("。findcard")) {

                if (!atUserId.equals("")) {
                    userId = Long.valueOf(atUserId);
                }
                List<CardAddress> list = cardAddressSerivce.selectAllByQQID(String.valueOf(userId));
                if (list.size() > 0) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("您查看的角色卡列表为：");
                    for (CardAddress cardAddress : list) {
                        sb.append("\n卡ID：" + cardAddress.getCardId());
                        sb.append("\t卡名称：" + cardAddress.getCardName());
                        sb.append("\n卡地址：" + cardAddress.getFileAddress());
                    }
                    cq.sendGroupMsg(groupId, sb.toString(), false);
                }
                return MESSAGE_IGNORE;
            }

            if (msg.startsWith(".findallcard") || msg.startsWith("。findallcard")) {

                List<CardAddress> list = new ArrayList<>();
                OnebotApi.GetGroupMemberListResp group = cq.getGroupMemberList(groupId);
                for (OnebotApi.GetGroupMemberListResp.GroupMember groupMemberInfoData : group.getGroupMemberList()) {
                    list.addAll(cardAddressSerivce.selectAllByQQID(String.valueOf(groupMemberInfoData.getUserId())));
                }

                if (list.size() > 0) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("您查看的角色卡列表为：");
                    for (CardAddress cardAddress : list) {
                        sb.append("\n卡ID：" + cardAddress.getCardId());
                        sb.append("\t卡名称：" + cardAddress.getCardName());
                        sb.append("\n卡地址：" + cardAddress.getFileAddress());
                    }
                    cq.sendGroupMsg(groupId, sb.toString(), false);
                }

            }
        }

        // 继续执行下一个插件
        return MESSAGE_IGNORE;
    }


}

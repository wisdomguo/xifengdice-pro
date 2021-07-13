package com.wisdomguo.xifeng.plugin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wisdomguo.xifeng.assist.BlackMap;
import com.wisdomguo.xifeng.assist.Disaster;
import com.wisdomguo.xifeng.assist.ReportRead;
import com.wisdomguo.xifeng.assist.Tarot;
import com.wisdomguo.xifeng.modules.cardaddress.entity.CardAddress;
import com.wisdomguo.xifeng.modules.qqgroup.entity.QQGroup;
import com.wisdomguo.xifeng.modules.cardaddress.service.CardAddressSerivce;
import com.wisdomguo.xifeng.modules.qqgroup.service.QQGroupSerivce;
import com.wisdomguo.xifeng.util.*;
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
import java.io.IOException;
import java.io.InputStream;
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
    private Map<Long, ReportRead> repeatList = new HashMap();


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
        String oldMsg = event.getRawMessage().replaceAll("。",".").toLowerCase();
        String msg = event.getRawMessage();
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
                if (message.getType().equals("at")) {
                    atUserId = message.getDataMap().get("qq");
                }
            }
        }
        //获取发送者的所有信息
        String nickname=event.getSender().getNickname();
        QQGroup qqGroup = qqGroupSerivce.selectAllByID(String.valueOf(groupId));
        if (qqGroup.getXfOpen() == 1 || qqGroup.getOtherOpen() == 1) {
            return MESSAGE_IGNORE;
        }

        ReportRead reportRead= repeatList.get(groupId);
        if(oldMsg.indexOf(".jrrp")==-1
                && oldMsg.indexOf(".tarot")==-1
                && oldMsg.indexOf(".st")==-1
                && oldMsg.indexOf("help")==-1
                && !oldMsg.startsWith(".r")
                && !oldMsg.startsWith(".magic")
                && !oldMsg.startsWith(".coc")
                && !oldMsg.startsWith("星碎兑换")
                && !oldMsg.startsWith("星空排行")
                && !oldMsg.startsWith("星屑排行")
                && !oldMsg.startsWith("星币排行")
                && !oldMsg.startsWith("星空探索")
                && !oldMsg.startsWith("星空转轮")
                && !oldMsg.startsWith("种子口袋")
                && !oldMsg.startsWith("作物仓库")
                && !oldMsg.startsWith("我的田地")
                && !oldMsg.startsWith("查看背包")){
            if(msg.startsWith("<image")){
                msg="img:"+msg.split("-")[2];
            }
            if(Objects.isNull(reportRead)){
                ReportRead read=new ReportRead(userId,msg,1);
                repeatList.put(groupId,read);
            }else{
                if(reportRead.getMessage().equals(msg)){
                    if(!reportRead.getUserId().equals(userId) && reportRead.getCount().equals(1)){
                        reportRead.setCount(2);
                        repeatList.put(groupId,reportRead);
                        if(msg.startsWith("img:")){
                            cq.sendGroupMsg(groupId, Msg.builder().image(oldMsg.replaceAll(" ","").replaceAll("<imageurl=\"","").replaceAll("\"/>","")), false);
                        }else {
                            cq.sendGroupMsg(groupId, msg, false);
                        }
                    }else{
                        repeatList.put(groupId,reportRead);
                    }
                }else{
                    ReportRead read=new ReportRead(userId,msg,1);
                    repeatList.put(groupId,read);
                }
            }
        }

        if (oldMsg.indexOf(".jrrp") != -1) {

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
        if (oldMsg.indexOf(".magic xf") != -1) {

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
                if(tarotList.get(key)>21){
                    Tarot tarotObj=getTarotJson(tarotList.get(key)-22);
                    cq.sendGroupMsg(groupId, Msg.builder().text(nickname+"您抽取到的为:\n====================\n【逆位】/"+tarotObj.getName()+"\n"+tarotObj.getNegative()+"\n").image("http://ali.gruiheng.com:8888/" + tarotStr + "-inversion.png"), false);
                }else{
                    Tarot tarotObj=getTarotJson(tarotList.get(key));
                    cq.sendGroupMsg(groupId, Msg.builder().text(nickname+"您抽取到的为:\n====================\n【正位】/"+tarotObj.getName()+"\n"+tarotObj.getPositive()+"\n").image("http://ali.gruiheng.com:8888/" + tarotStr + ".png"), false);
                }

            } else {
                if(tarotList.get(key)>21){
                    Tarot tarotObj=getTarotJson(tarotList.get(key)-22);
                    cq.sendGroupMsg(groupId, Msg.builder().text(nickname+"您今天已进行抽取过塔罗:\n【逆位】/"+tarotObj.getName()+"\n"+tarotObj.getNegative()+"\n明天再来吧！"), false);
                }else{
                    Tarot tarotObj=getTarotJson(tarotList.get(key));
                    cq.sendGroupMsg(groupId, Msg.builder().text(nickname+"您今天已进行抽取过塔罗:\n【正位】/"+tarotObj.getName()+"\n"+tarotObj.getPositive()+"\n明天再来吧！"), false);
                }
            }
            return MESSAGE_IGNORE;
        }
        if (msg.indexOf("card") != -1 && (msg.indexOf(".") != -1)) {
            if (msg.startsWith(".addcard") ) {
                String[] card = msg.split(" ");
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
            if (msg.startsWith(".delcard")) {
                String[] card = msg.split(" ");
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

            if (msg.startsWith(".findcard") ) {

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

            if (msg.startsWith(".findallcard")) {

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

    public Tarot getTarotJson(int num) throws IOException {
        String path = "/tarot.json";
        InputStream config = getClass().getResourceAsStream(path);
        if (config == null) {
            throw new RuntimeException("读取文件失败");
        } else {
            JSONObject json = JSON.parseObject(config, JSONObject.class);
            JSONArray array = json.getJSONArray("tarot");
            List<Tarot> tarots = array.toJavaList(Tarot.class);
            for(Tarot tarot:tarots){
                if(tarot.getNum()==num){
                    return tarot;
                }
            }
        }
        return null;
    }
}

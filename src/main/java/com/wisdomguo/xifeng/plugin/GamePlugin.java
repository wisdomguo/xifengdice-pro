package com.wisdomguo.xifeng.plugin;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wisdomguo.xifeng.assist.BlackMap;
import com.wisdomguo.xifeng.modules.luckmute.entity.LuckMute;
import com.wisdomguo.xifeng.modules.luckmute.service.LuckMuteService;
import com.wisdomguo.xifeng.modules.qqgroup.entity.QQGroup;
import com.wisdomguo.xifeng.modules.qqgroup.service.QQGroupSerivce;
import lombok.SneakyThrows;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import onebot.OnebotBase;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


/**
 * 示例插件
 * 插件必须继承CQPlugin，上面要 @Component
 * <p>
 * 添加事件：光标移动到类中，按 Ctrl+O 添加事件(讨论组消息、加群请求、加好友请求等)
 * 查看API参数类型：光标移动到方法括号中按Ctrl+P
 * 查看API说明：光标移动到方法括号中按Ctrl+Q
 */

/**
 * LuckMutePlugin
 * 随机禁言功能模块
 *
 * @author wisdom-guo
 * @since 2021-5
 */
@Component
public class GamePlugin extends BotPlugin {
    /**
     * 收到私聊消息时会调用这个方法
     *
     * @param cq    机器人对象，用于调用API，例如发送私聊消息 sendPrivateMsg
     * @param event 事件对象，用于获取消息内容、群号、发送者QQ等
     * @return 是否继续调用下一个插件，IGNORE表示继续，BLOCK表示不继续
     */

    @Resource
    private QQGroupSerivce qqGroupSerivce;

    @Resource
    private LuckMuteService luckMuteService;

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

            QQGroup qqGroup = qqGroupSerivce.selectAllByID(String.valueOf(groupId));
            if (qqGroup.getXfOpen() == 1) {
                return MESSAGE_IGNORE;
            }

        boolean judgment =  (("owner".equals(event.getSender().getRole())
                || "admin".equals(event.getSender().getRole()))
                || userId == 1969077760L);

        String atUserId = "";
        if (event.getMessageList().size() > 1) {
            for (OnebotBase.Message message : event.getMessageList()) {
                if ("at".equals(message.getType())) {
                    atUserId = message.getDataMap().get("qq");
                }
            }
        }

        LuckMute luckMute=luckMuteService.getOne(Wrappers.<LuckMute>lambdaQuery().eq(LuckMute::getGroupId,groupId));
        String muter="";

        if(luckMute!=null){
            if(luckMute.getOpen()==1){

            }else {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                if (luckMute.getQqId().equals(userId)
                        && luckMute.getIntervaling() != null
                        && addDateMinute(format.format(luckMute.getIntervaling()), luckMute.getIntervalTime()).before(new Date())
                ) {
                    Random random = new Random();
                    int resultRandom = random.nextInt(101);
                    if (resultRandom < luckMute.getMuteProbability()) {
                        cq.setGroupBan(groupId, luckMute.getQqId(), luckMute.getSilenceTime() * 60);
                        return MESSAGE_IGNORE;
                    } else {
                        luckMute.setIntervaling(new Date());
                        luckMuteService.saveOrUpdate(luckMute);
                        if(luckMute.getQqId()!=null){
                            muter=cq.getGroupMemberInfo(groupId,luckMute.getQqId(),false).getCard();
                        }
                        cq.sendGroupMsg(groupId, muter + "已获得" + luckMute.getIntervalTime() + "分钟发言保护时间!", false);
                        return MESSAGE_IGNORE;
                    }
                }
            }
        }else{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            LuckMute newMute=new LuckMute();
            newMute.setGroupId(groupId);
            newMute.setIntervalTime(5);
            newMute.setSilenceTime(5);
            newMute.setOpen(1);
            newMute.setMuteProbability(20);
            newMute.setIntervaling(addDateMinute(format.format(new Date()),-5));
            luckMuteService.save(newMute);
            luckMute=newMute;

        }
        if (msg.startsWith(".mute")
                &&judgment) {
            try {

                if(msg.indexOf("type")!=-1){
                    if(luckMute.getQqId()!=null){
                        muter=cq.getGroupMemberInfo(groupId,luckMute.getQqId(),false).getCard();
                    }
                    StringBuilder builder=new StringBuilder();
                    builder.append("随机禁言功能详情\n被禁言人：");
                    builder.append(muter);
                    builder.append("\n被禁言概率：");
                    builder.append(luckMute.getMuteProbability());
                    builder.append("%\n禁言时间：");
                    builder.append(luckMute.getSilenceTime());
                    builder.append("min\n保护时间：");
                    builder.append(luckMute.getIntervalTime());
                    builder.append("min\n功能状态：");
                    if(luckMute.getOpen()==0){
                        builder.append("已开启");
                    }else {
                        builder.append("已关闭");
                    }

                    cq.sendGroupMsg(groupId, builder.toString(), false);
                    return MESSAGE_IGNORE;
                }

                if(msg.indexOf("open")!=-1){
                    String role=cq.getGroupMemberInfo(groupId, 1515044906L,false).getRole();
                    if("owner".equals(role) || "admin".equals(role)){
                        if(luckMute.getQqId()!=null){
                            luckMute.setOpen(0);
                            luckMuteService.saveOrUpdate(luckMute);
                            if(luckMute.getQqId()!=null){
                                muter=cq.getGroupMemberInfo(groupId,luckMute.getQqId(),false).getCard();
                            }
                            cq.sendGroupMsg(groupId, "随机禁言功能已打开！当前被禁言人为："+muter, false);
                            return MESSAGE_IGNORE;
                        }else{
                            cq.sendGroupMsg(groupId, "本群还未设置被禁言人!", false);
                            return MESSAGE_IGNORE;
                        }
                    }else{
                        cq.sendGroupMsg(groupId, "惜风不是群主或管理员，无法使用该功能！", false);
                        return MESSAGE_IGNORE;
                    }
                }

                if(msg.indexOf("close")!=-1){
                    luckMute.setOpen(1);
                    luckMuteService.saveOrUpdate(luckMute);
                    cq.sendGroupMsg(groupId, "随机禁言功能已关闭！", false);
                    return MESSAGE_IGNORE;
                }

                //概率
                if(msg.indexOf("pro")!=-1){
                    Integer pro=Integer.valueOf(msg.replaceAll(".mute pro ",""));
                    if(pro>40){
                        pro=40;
                    }else if(pro<0){
                        pro=1;
                    }
                    luckMute.setMuteProbability(pro);
                    luckMuteService.saveOrUpdate(luckMute);
                    cq.sendGroupMsg(groupId, "已设置禁言概率为"+pro+"%！", false);
                    return MESSAGE_IGNORE;
                }

                //保护时间
                if(msg.indexOf("int")!=-1){
                    Integer inte=Integer.valueOf(msg.replaceAll(".mute int ",""));
                    if(luckMute.getSilenceTime()-inte>5){
                        inte=luckMute.getSilenceTime()-5;
                    }else if(inte<5){
                        inte=5;
                    }else if(inte+5>luckMute.getSilenceTime()){
                        inte=luckMute.getSilenceTime()+5;
                    }
                    luckMute.setIntervalTime(inte);
                    luckMuteService.saveOrUpdate(luckMute);
                    cq.sendGroupMsg(groupId, "已设置发言保护时间为"+inte+"分钟！", false);
                    return MESSAGE_IGNORE;
                }

                //禁言时间
                if(msg.indexOf("sil")!=-1){
                    Integer sil=Integer.valueOf(msg.replaceAll(".mute sil ",""));
                    if(sil<0){
                        sil=1;
                    }else if(sil>15){
                        sil=15;
                    }
                    if(sil-luckMute.getIntervalTime()>5){
                        if (sil>5){
                            luckMute.setIntervalTime(sil-5);
                        }else {
                            luckMute.setIntervalTime(1);
                        }
                    }
                    luckMute.setSilenceTime(sil);
                    luckMuteService.saveOrUpdate(luckMute);
                    cq.sendGroupMsg(groupId, "已设置禁言时间为"+sil+"分钟！", false);
                    return MESSAGE_IGNORE;
                }

                if(!atUserId.equals("")){
                    luckMute.setQqId(Long.valueOf(atUserId));
                    luckMuteService.saveOrUpdate(luckMute);
                    if(luckMute.getQqId()!=null){
                        muter=cq.getGroupMemberInfo(groupId,luckMute.getQqId(),false).getCard();
                        if(muter.equals("")){
                            muter=cq.getGroupMemberInfo(groupId,luckMute.getQqId(),false).getNickname();
                        }
                    }
                    cq.sendGroupMsg(groupId, "当前被禁言人修改为："+muter, false);
                }

            } catch (Exception e) {
                e.printStackTrace();
                cq.sendGroupMsg(groupId, "设置失败,请重试！", false);
            }
            // 不执行下一个插件
            return MESSAGE_IGNORE;
        }
        // 继续执行下一个插件
        return MESSAGE_IGNORE;
    }


    /**
     * 日期添加到分钟得到新时间
     * @param day 开始时间
     * @param x	  相隔分钟数
     * @return
     *
     */
    public static Date addDateMinute(String day, int x) {
        //入参的格式
        // 24小时制
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = format.parse(day);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (date == null){
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 24小时制
        cal.add(Calendar.MINUTE, x);
        //得到结算后的结果 yyyy-MM-dd HH:mm
        date = cal.getTime();
        cal = null;
        return date;
    }
}

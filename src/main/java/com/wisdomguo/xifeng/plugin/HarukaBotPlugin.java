package com.wisdomguo.xifeng.plugin;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wisdomguo.xifeng.assist.BlackMap;
import com.wisdomguo.xifeng.modules.charactercoc.entity.CharacterCoC;
import com.wisdomguo.xifeng.modules.harukascheduling.entity.HarukaScheduling;
import com.wisdomguo.xifeng.modules.harukascheduling.service.HarukaSchedulingService;
import com.wisdomguo.xifeng.modules.qqgroup.entity.QQGroup;
import com.wisdomguo.xifeng.modules.qqgroup.service.QQGroupSerivce;
import com.wisdomguo.xifeng.util.BoolUtil;
import com.wisdomguo.xifeng.util.HttpClientUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotContainer;
import net.lz1998.pbbot.bot.BotPlugin;
import onebot.OnebotApi;
import onebot.OnebotBase;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


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
public class HarukaBotPlugin extends BotPlugin {

    @Resource
    private HarukaSchedulingService harukaSchedulingService;

    @Autowired
    BotContainer botInf;

    @Scheduled(cron = "0 0 8 ? * SUN", zone = "Asia/Shanghai")
    public void changHarukaScheduling() {
        HarukaScheduling scheduling=new HarukaScheduling();
        scheduling.setDate(new Date());
        harukaSchedulingService.save(scheduling);
        botInf.getBots().get(1515044906L).sendGroupMsg(1075109409L, "本周排版表已刷新" , false);
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

        //获取消息内容
        String msg = event.getRawMessage().replaceAll("。",".");
        //获取群号
        long groupId = event.getGroupId();
        //获取发送者QQ
        long userId = event.getUserId();
        //获取发送者的所有信息
        if(groupId!=939636498L||!msg.startsWith("#")){
//        if(groupId!=1075109409L||!msg.startsWith("#")){
            return MESSAGE_IGNORE;
        }

        boolean role =event.getSender().getRole().equals("owner") || event.getSender().getRole().equals("admin");
        String atUserId = "";
        if (event.getMessageList().size() > 1) {
            for (OnebotBase.Message message : event.getMessageList()) {
                if (message.getType().equals("at")) {
                    atUserId = message.getDataMap().get("qq");
                }
            }
        }

        if(role && !atUserId.equals("")){
            userId=Long.valueOf(atUserId);
        }else {
            role=false;
        }

        if(msg.startsWith("#预约")){
            String newMsg=msg.replaceAll(" ","").replaceAll("#预约","").replaceAll("<atqq=\""+atUserId+"\"/>","");
            HarukaScheduling harukaScheduling=harukaSchedulingService.getOne(Wrappers.<HarukaScheduling>lambdaQuery().orderByDesc(HarukaScheduling::getDate).last("limit 1"));
            Long setId=0L;
            Long scheduId=harukaScheduling.getId();

            switch (newMsg){
                case "周一":
                    setId=harukaScheduling.getMon();
                    if(setId!=null && !role){
                        cq.sendGroupMsg(groupId, "已有人进行预约!", false);
                        return MESSAGE_IGNORE;
                    }else{
                        boolean result=harukaSchedulingService.update(Wrappers.<HarukaScheduling>lambdaUpdate()
                                .set(HarukaScheduling::getMon,userId).eq(HarukaScheduling::getId,scheduId));
                        if(result){
                            cq.sendGroupMsg(groupId, "预约成功!", false);
                            return MESSAGE_IGNORE;
                        }
                    }
                    break;
                case "周二":
                    setId=harukaScheduling.getTue();
                    if(setId!=null && !role){
                        cq.sendGroupMsg(groupId, "已有人进行预约!", false);
                        return MESSAGE_IGNORE;
                    }else{
                        boolean result=harukaSchedulingService.update(Wrappers.<HarukaScheduling>lambdaUpdate()
                                .set(HarukaScheduling::getTue,userId).eq(HarukaScheduling::getId,scheduId));
                        if(result){
                            cq.sendGroupMsg(groupId, "预约成功!", false);
                            return MESSAGE_IGNORE;
                        }
                    }
                    break;
                case "周三":
                    setId=harukaScheduling.getWed();
                    if(setId!=null && !role){
                        cq.sendGroupMsg(groupId, "已有人进行预约!", false);
                        return MESSAGE_IGNORE;
                    }else{
                        boolean result=harukaSchedulingService.update(Wrappers.<HarukaScheduling>lambdaUpdate()
                                .set(HarukaScheduling::getWed,userId).eq(HarukaScheduling::getId,scheduId));
                        if(result){
                            cq.sendGroupMsg(groupId, "预约成功!", false);
                            return MESSAGE_IGNORE;
                        }
                    }
                    break;
                case "周四":
                    setId=harukaScheduling.getThu();
                    if(setId!=null && !role){
                        cq.sendGroupMsg(groupId, "已有人进行预约!", false);
                        return MESSAGE_IGNORE;
                    }else{
                        boolean result=harukaSchedulingService.update(Wrappers.<HarukaScheduling>lambdaUpdate()
                                .set(HarukaScheduling::getThu,userId).eq(HarukaScheduling::getId,scheduId));
                        if(result){
                            cq.sendGroupMsg(groupId, "预约成功!", false);
                            return MESSAGE_IGNORE;
                        }
                    }
                    break;
                case "周五":
                    setId=harukaScheduling.getFri();
                    if(setId!=null && !role){
                        cq.sendGroupMsg(groupId, "已有人进行预约!", false);
                        return MESSAGE_IGNORE;
                    }else{
                        boolean result=harukaSchedulingService.update(Wrappers.<HarukaScheduling>lambdaUpdate()
                                .set(HarukaScheduling::getFri,userId).eq(HarukaScheduling::getId,scheduId));
                        if(result){
                            cq.sendGroupMsg(groupId, "预约成功!", false);
                            return MESSAGE_IGNORE;
                        }
                    }
                    break;
                case "周六":
                    setId=harukaScheduling.getSat();
                    if(setId!=null && !role){
                        cq.sendGroupMsg(groupId, "已有人进行预约!", false);
                        return MESSAGE_IGNORE;
                    }else{
                        boolean result=harukaSchedulingService.update(Wrappers.<HarukaScheduling>lambdaUpdate()
                                .set(HarukaScheduling::getSat,userId).eq(HarukaScheduling::getId,scheduId));
                        if(result){
                            cq.sendGroupMsg(groupId, "预约成功!", false);
                            return MESSAGE_IGNORE;
                        }
                    }
                    break;
                case "周日":
                    setId=harukaScheduling.getSun();
                    if(setId!=null && !role){
                        cq.sendGroupMsg(groupId, "已有人进行预约!", false);
                        return MESSAGE_IGNORE;
                    }else{
                        boolean result=harukaSchedulingService.update(Wrappers.<HarukaScheduling>lambdaUpdate()
                                .set(HarukaScheduling::getSun,userId).eq(HarukaScheduling::getId,scheduId));
                        if(result){
                            cq.sendGroupMsg(groupId, "预约成功!", false);
                            return MESSAGE_IGNORE;
                        }
                    }
                    break;
                case "排版":
                    setId=harukaScheduling.getTypeSetting();
                    if(setId!=null && !role){
                        cq.sendGroupMsg(groupId, "已有人进行预约!", false);
                        return MESSAGE_IGNORE;
                    }else{
                        boolean result=harukaSchedulingService.update(Wrappers.<HarukaScheduling>lambdaUpdate()
                                .set(HarukaScheduling::getTypeSetting,userId).eq(HarukaScheduling::getId,scheduId));
                        if(result){
                            cq.sendGroupMsg(groupId, "预约成功!", false);
                            return MESSAGE_IGNORE;
                        }
                    }
                    break;
                case "审核":
                    setId=harukaScheduling.getToExamine();
                    if(setId!=null && !role){
                        cq.sendGroupMsg(groupId, "已有人进行预约!", false);
                        return MESSAGE_IGNORE;
                    }else{
                        boolean result=harukaSchedulingService.update(Wrappers.<HarukaScheduling>lambdaUpdate()
                                .set(HarukaScheduling::getToExamine,userId).eq(HarukaScheduling::getId,scheduId));
                        if(result){
                            cq.sendGroupMsg(groupId, "预约成功!", false);
                            return MESSAGE_IGNORE;
                        }
                    }
                    break;

                default:
                    cq.sendGroupMsg(groupId, "格式有误!", false);
                    return MESSAGE_IGNORE;
            }
        }

        if(msg.startsWith("#本周排班")){

            HarukaScheduling harukaScheduling=harukaSchedulingService.getOne(Wrappers.<HarukaScheduling>lambdaQuery().orderByDesc(HarukaScheduling::getDate).last("limit 1"));
            StringBuilder builder=new StringBuilder("本周排班如下:");
            if(harukaScheduling.getMon()!=null){
                String nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getMon(),false).getCard();
                if (nickname.equals("")){
                    nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getMon(),false).getNickname();
                }
                builder.append("\n周一文案："+nickname);
            }else {
                builder.append("\n周一文案：待定");
            }
            if(harukaScheduling.getTue()!=null){
                String nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getTue(),false).getCard();
                if (nickname.equals("")){
                    nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getTue(),false).getNickname();
                }
                builder.append("\n周二文案："+nickname);
            }else {
                builder.append("\n周二文案：待定");
            }
            if(harukaScheduling.getWed()!=null){
                String nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getWed(),false).getCard();
                if (nickname.equals("")){
                    nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getWed(),false).getNickname();
                }
                builder.append("\n周三文案："+nickname);
            }else {
                builder.append("\n周三文案：待定");
            }
            if(harukaScheduling.getThu()!=null){
                String nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getThu(),false).getCard();
                if (nickname.equals("")){
                    nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getThu(),false).getNickname();
                }
                builder.append("\n周四文案："+nickname);
            }else {
                builder.append("\n周四文案：待定");
            }
            if(harukaScheduling.getFri()!=null){
                String nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getFri(),false).getCard();
                if (nickname.equals("")){
                    nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getFri(),false).getNickname();
                }
                builder.append("\n周五文案："+nickname);
            }else {
                builder.append("\n周五文案：待定");
            }
            if(harukaScheduling.getSat()!=null){
                String nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getSat(),false).getCard();
                if (nickname.equals("")){
                    nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getSat(),false).getNickname();
                }
                builder.append("\n周六文案："+nickname);
            }else {
                builder.append("\n周六文案：待定");
            }
            if(harukaScheduling.getSun()!=null){
                String nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getSun(),false).getCard();
                if (nickname.equals("")){
                    nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getSun(),false).getNickname();
                }
                builder.append("\n周日文案："+nickname);
            }else {
                builder.append("\n周日文案：待定");
            }
            if(harukaScheduling.getTypeSetting()!=null){
                String nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getTypeSetting(),false).getCard();
                if (nickname.equals("")){
                    nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getTypeSetting(),false).getNickname();
                }
                builder.append("\n排版："+nickname);
            }else {
                builder.append("\n排版：待定");
            }
            if(harukaScheduling.getToExamine()!=null){
                String nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getToExamine(),false).getCard();
                if (nickname.equals("")){
                    nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getToExamine(),false).getNickname();
                }
                builder.append("\n审核："+nickname);
            }else {
                builder.append("\n审核：待定");
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String date=format.format(harukaScheduling.getDate());
            builder.append("\n本周开始日期:"+date);
            cq.sendGroupMsg(groupId, builder.toString(), false);
        }

        if(msg.startsWith("#上周排班")){
            HarukaScheduling harukaScheduling=harukaSchedulingService.getOne(Wrappers.<HarukaScheduling>lambdaQuery().orderByDesc(HarukaScheduling::getDate).last("limit 1,2"));
            StringBuilder builder=new StringBuilder("上周排班如下:");
            if(harukaScheduling.getMon()!=null){
                String nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getMon(),false).getCard();
                if (nickname.equals("")){
                    nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getMon(),false).getNickname();
                }
                builder.append("\n周一文案："+nickname);
            }else {
                builder.append("\n周一文案：待定");
            }
            if(harukaScheduling.getTue()!=null){
                String nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getTue(),false).getCard();
                if (nickname.equals("")){
                    nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getTue(),false).getNickname();
                }
                builder.append("\n周二文案："+nickname);
            }else {
                builder.append("\n周二文案：待定");
            }
            if(harukaScheduling.getWed()!=null){
                String nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getWed(),false).getCard();
                if (nickname.equals("")){
                    nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getWed(),false).getNickname();
                }
                builder.append("\n周三文案："+nickname);
            }else {
                builder.append("\n周三文案：待定");
            }
            if(harukaScheduling.getThu()!=null){
                String nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getThu(),false).getCard();
                if (nickname.equals("")){
                    nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getThu(),false).getNickname();
                }
                builder.append("\n周四文案："+nickname);
            }else {
                builder.append("\n周四文案：待定");
            }
            if(harukaScheduling.getFri()!=null){
                String nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getFri(),false).getCard();
                if (nickname.equals("")){
                    nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getFri(),false).getNickname();
                }
                builder.append("\n周五文案："+nickname);
            }else {
                builder.append("\n周五文案：待定");
            }
            if(harukaScheduling.getSat()!=null){
                String nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getSat(),false).getCard();
                if (nickname.equals("")){
                    nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getSat(),false).getNickname();
                }
                builder.append("\n周六文案："+nickname);
            }else {
                builder.append("\n周六文案：待定");
            }
            if(harukaScheduling.getSun()!=null){
                String nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getSun(),false).getCard();
                if (nickname.equals("")){
                    nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getSun(),false).getNickname();
                }
                builder.append("\n周日文案："+nickname);
            }else {
                builder.append("\n周日文案：待定");
            }
            if(harukaScheduling.getTypeSetting()!=null){
                String nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getTypeSetting(),false).getCard();
                if (nickname.equals("")){
                    nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getTypeSetting(),false).getNickname();
                }
                builder.append("\n排版："+nickname);
            }else {
                builder.append("\n排版：待定");
            }
            if(harukaScheduling.getToExamine()!=null){
                String nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getToExamine(),false).getCard();
                if (nickname.equals("")){
                    nickname=cq.getGroupMemberInfo(groupId,harukaScheduling.getToExamine(),false).getNickname();
                }
                builder.append("\n审核："+nickname);
            }else {
                builder.append("\n审核：待定");
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String date=format.format(harukaScheduling.getDate());
            builder.append("\n上周开始日期:"+date);
            cq.sendGroupMsg(groupId, builder.toString(), false);
        }
        return MESSAGE_IGNORE;
    }

}

package com.wisdomguo.xifeng.plugin;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;


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
public class XFPlugin extends BotPlugin {

    @Override
    public int onFriendRequest(@NotNull Bot bot, @NotNull OnebotEvent.FriendRequestEvent event) {
        String com=event.getComment();
        if(com.indexOf("1d20")!=-1){
            bot.setFriendAddRequest(event.getFlag(),true,"");
        }else{
            bot.setFriendAddRequest(event.getFlag(),false,"您的答案有误，请重新回答哦！");
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupRequest(@NotNull Bot bot, @NotNull OnebotEvent.GroupRequestEvent event) {
        bot.setGroupAddRequest(event.getFlag(),"",true,"");
        return MESSAGE_IGNORE;
    }

}

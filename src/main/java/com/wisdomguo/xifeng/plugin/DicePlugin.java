package com.wisdomguo.xifeng.plugin;

import com.wisdomguo.xifeng.assist.BlackMap;
import com.wisdomguo.xifeng.modules.botset.qqgroup.entity.QQGroup;
import com.wisdomguo.xifeng.modules.botset.qqgroup.service.QQGroupSerivce;
import com.wisdomguo.xifeng.util.BoolUtil;
import lombok.SneakyThrows;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
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
 * DicePlugin
 * 骰子
 *
 * @author wisdom-guo
 * @since 2020
 */
@Component
public class DicePlugin extends BotPlugin {
    /**
     * 收到私聊消息时会调用这个方法
     *
     * @param cq    机器人对象，用于调用API，例如发送私聊消息 sendPrivateMsg
     * @param event 事件对象，用于获取消息内容、群号、发送者QQ等
     * @return 是否继续调用下一个插件，IGNORE表示继续，BLOCK表示不继续
     */


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
        String msg = event.getRawMessage().replaceAll("。", ".");
        //获取群号
        long groupId = event.getGroupId();
        //获取发送者QQ
        long userId = event.getUserId();
        if(BlackMap.returnBlackList(userId)) {
            return MESSAGE_BLOCK;
        }
        //获取发送者的所有信息

        String nickname = event.getSender().getNickname();


        if (BoolUtil.startByPoint(msg) || BoolUtil.startByFullStop(msg)) {
            QQGroup qqGroup = qqGroupSerivce.selectAllByID(String.valueOf(groupId));
            if (qqGroup.getXfOpen() == 1) {
                return MESSAGE_IGNORE;
            }
        } else {
            return MESSAGE_IGNORE;
        }
        msg = msg.replaceAll("R", "r");
        msg = msg.replaceAll("C", "c");
        msg = msg.replaceAll("D", "d");
        msg = msg.replaceAll("N", "n");
        msg = msg.replaceAll("O", "o");

        //判断是否是骰娘操作
        if ((msg.indexOf(".r") != -1 || msg.indexOf(".coc") != -1 || msg.indexOf(".dnd") != -1)) {
            //不区分大小写中英文

            if (sendDice(cq, msg, groupId, userId, nickname)) {
                return MESSAGE_IGNORE;
            }
        }


        // 继续执行下一个插件
        return MESSAGE_IGNORE;
    }


    private boolean sendDice(Bot cq, String msg, long groupId, long userId, String nickname) {
//        原coutType

        if (qqGroupSerivce.selectAllByID(String.valueOf(groupId)).getDiceOpen() == 0) {
            //判断骰娘是否开启
            if (msg.startsWith(".rh")) {
                checkAndSendType(cq, msg, groupId, userId, ".rh", nickname);
            } else if (msg.split("d").length > 3) {

            } else if (msg.startsWith(".rd")) {
                checkAndSendType(cq, msg.replaceAll("rd", "r1d100("), groupId, userId, ".r", nickname);
            } else if (msg.startsWith(".r") && msg.indexOf("d") != -1) {
                checkAndSendType(cq, msg, groupId, userId, ".r", nickname);
            } else if (msg.startsWith(".coc")) {
                getCocDice(cq, msg, groupId, nickname);
            } else if (msg.startsWith(".dnd")) {
                getDndDice(cq, msg, groupId, nickname);
            }
            // 不执行下一个插件
            return true;
        }
        return false;
    }

    private void getDndDice(Bot cq, String msg, long groupId, String nickname) {
        String[] message = msg.replaceAll(" ", "").split(".dnd");
        String multiple = "1";
        if (message.length > 0) {
            multiple = message[1];
        }
        StringBuffer sb = new StringBuffer("您新车的dnd" + multiple + "个卡6属性分别为：");
        for (int i = 0; i < Integer.valueOf(multiple); i++) {
            sb.append("\n");
            sb.append("力量：" + getRandomDND(4) + " ");
            sb.append("敏捷：" + getRandomDND(4) + " ");
            sb.append("智力：" + getRandomDND(4) + " ");
            sb.append("体质：" + getRandomDND(4) + " ");
            sb.append("魅力：" + getRandomDND(4) + " ");
            sb.append("感知：" + getRandomDND(4) + " ");
        }
        cq.sendGroupMsg(groupId, nickname + " " + sb.toString(), false);
    }

    private void getCocDice(Bot cq, String msg, long groupId, String nickname) {
        String[] message = msg.replaceAll(" ", "").split(".coc");
        String multiple = "1";
        if (message.length > 0) {
            multiple = message[1];
        }
        StringBuffer sb = new StringBuffer("您新车的" + multiple + "个卡分别为：");
        for (int i = 0; i < Integer.valueOf(multiple); i++) {
            sb.append("\n");
            sb.append("力量:" + getRandom(3) * 5 + " ");
            sb.append("体质:" + getRandom(3) * 5 + " ");
            sb.append("体型:" + (getRandom(2) + 6) * 5 + " ");
            sb.append("敏捷:" + getRandom(3) * 5 + " ");
            sb.append("魅力:" + getRandom(3) * 5 + " ");
            sb.append("智力:" + (getRandom(2) + 6) * 5 + " ");
            sb.append("意志:" + getRandom(3) * 5 + " ");
            sb.append("教育:" + (getRandom(2) + 6) * 5 + " ");
            sb.append("幸运:" + getRandom(3) * 5 + ";");
        }
        cq.sendGroupMsg(groupId, nickname + " " + sb.toString(), false);
    }

    private void checkAndSendType(Bot cq, String msg, long groupId, long userId, String type, String nickname) {
        if (msg.indexOf("(") != -1) {
            msg = msg.split("\\(")[0];
        }
        if (msg.indexOf("（") != -1) {
            msg = msg.split("\\（")[0];
        }
        String[] message = msg.replaceAll(" ", "").split(type);


        if (message.length > 0) {

            //创建stringbuffer系列
            StringBuilder builder = new StringBuilder("您掷骰: " + msg.replaceAll("d","D").replaceAll("[*]","X").replaceAll("/","÷") + ":");
            try {
                List<String> dList=new ArrayList<>();
                List<String> symbols=new ArrayList<>();
                char[] msgs=message[1].toCharArray();
                String str="";
                int mDCount=0;
                mDCount = getmDCount(dList, symbols, msgs, str, mDCount);
                String result = getResult(dList);
                changeDiceList(dList, symbols, mDCount,2);
                builder.append(result);
                for(int i=0;i<symbols.size();i++){
                    String symbol=symbols.get(i).replaceAll("[*]","X").replaceAll("/","÷");
                    if (i == 0 && i!=symbols.size()-1) {
                        builder.append("="+dList.get(i)+symbol);
                    }else{
                        if(i==symbols.size()-1){
                            builder.append(dList.get(i)+symbol+dList.get(i+1));
                        }else{
                            builder.append(dList.get(i)+symbol);
                        }
                    }
                }
                int remaining=symbols.size();
                changeDiceList(dList, symbols, remaining,1);
                builder.append("合计："+dList.get(0));
            } catch (Exception e) {
                builder.append("合计：" + 0);
            }
            // 调用API发送消息
            if (msg.startsWith(".rh")) {
                //私发

//                cq.getApiSender().callApi(cq.getBotSession(),cq.getSelfId(), OnebotApi.SendMsgReq.newBuilder().setGroupId(groupId).setUserId(userId).addAllMessage(Msg.builder().text(builder.toString()).build()));
                cq.sendPrivateMsg(userId, builder.toString(), false);
            } else if (msg.startsWith(".r")) {
                //群聊
                cq.sendGroupMsg(groupId, nickname + "" + builder.toString(), false);
            }

        }

    }

    @NotNull
    private String getResult(List<String> dList) {
        String result="";
        for(int i=0;i<dList.size();i++){
            if (dList.get(i).indexOf("d")!=-1){
                if("".equals(result)){
                    result+="[";
                }else{
                    result+="，[";
                }
                String[] dice=dList.get(i).split("d");
                int resultValue=0;
                for(int j=0;j<Integer.valueOf(dice[0]);j++){
                    int diceValue=Integer.valueOf(dice[1]);
                    Random random=new Random();
                    int ranNum=random.nextInt(diceValue)+1;
                    resultValue+=ranNum;
                    if(j==0){
                        result+=String.valueOf(ranNum);
                    }else{
                        result+="，"+String.valueOf(ranNum);
                    }
                }
                result+="] ";
                dList.set(i,String.valueOf(resultValue));
            }
        }
        return result;
    }


    private int getRandom(int n) {
        int k = 0;
        int[] array = new int[n];
        for (int j = 0; j < n; j++) {
            Random random = new Random();
            k += random.nextInt(6) + 1;
        }
        return k;
    }

    private int getRandomDND(int n) {
        int k = 0;
        int[] array = new int[n];
        for (int j = 0; j < n; j++) {
            Random random = new Random();
            array[j] = random.nextInt(6) + 1;
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n - 1; j++) {
                if (array[j] < array[j + 1]) {
                    int p = array[j + 1];
                    array[j + 1] = array[j];
                    array[j] = p;
                }
            }
        }
        for (int i = 0; i < n; i++) {
            if (i != n - 1) {
                k += array[i];
            }
        }
        return k;
    }

    private static int getmDCount(List<String> dList, List<String> symbols, char[] msgs, String str, int mDCount) {
        for (int i = 0; i < msgs.length; i++) {
            String c = String.valueOf(msgs[i]);
            if (c.equals("+") || c.equals("-") || c.equals("*") || c.equals("/")) {
                dList.add(str);
                symbols.add(c);
                str = "";
                if (c.equals("*") || c.equals("/")) {
                    mDCount++;
                }
            } else {
                str += c;
            }
            if (i + 1 == msgs.length) {
                dList.add(str);
                str = "";
            }
        }
        return mDCount;
    }

    private static void changeDiceList(List<String> dList, List<String> symbols, int remaining, int type) {
        for (int i = 0; i < remaining; i++) {
            int z = 0;
            int result = 0;
            for (int j = 0; j < symbols.size(); j++) {
                if (type == 1) {
                    if (symbols.get(j).equals("+")) {
                        z = j;
                        result = Integer.valueOf(dList.get(j)) + Integer.valueOf(dList.get(j + 1));
                        break;
                    }
                    if (symbols.get(j).equals("-")) {
                        z = j;
                        result = Integer.valueOf(dList.get(j)) - Integer.valueOf(dList.get(j + 1));
                        break;
                    }
                } else {
                    if (symbols.get(j).equals("*")) {
                        z = j;
                        result = Integer.valueOf(dList.get(j)) * Integer.valueOf(dList.get(j + 1));
                        break;
                    }
                    if (symbols.get(j).equals("/")) {
                        z = j;
                        result = Integer.valueOf(dList.get(j)) / Integer.valueOf(dList.get(j + 1));
                        break;
                    }
                }
            }
            symbols.remove(z);
            dList.remove(z);
            dList.remove(z);
            dList.add(z, String.valueOf(result));
        }
    }


}

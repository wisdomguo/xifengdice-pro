package com.wisdom.xifeng.plugin;

import com.wisdom.xifeng.entity.QQGroup;
import com.wisdom.xifeng.service.qqgroup.QQGroupSerivce;
import com.wisdom.xifeng.util.BoolUtil;
import lombok.SneakyThrows;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotApi;
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
        String msg = event.getRawMessage();
        //获取群号
        long groupId = event.getGroupId();
        //获取发送者QQ
        long userId = event.getUserId();
        //获取发送者的所有信息

//        OnebotApi.GetGroupMemberListResp a = cq.getGroupMemberList(groupId);
        String nickname=event.getSender().getNickname();


        if (BoolUtil.startByPoint(msg) || BoolUtil.startByFullStop(msg)) {
            QQGroup qqGroup = qqGroupSerivce.selectAllByID(String.valueOf(groupId));
            if (qqGroup.getXfOpen() == 1) {
                return MESSAGE_IGNORE;
            }
        } else {
            return MESSAGE_IGNORE;
        }


        //判断是否是骰娘操作
        if (msg.indexOf("。r") != -1 || msg.indexOf("。R") != -1 || msg.indexOf(".R") != -1 || msg.indexOf(".r") != -1 || msg.indexOf(".coc") != -1 || msg.indexOf(".COC") != -1 || msg.indexOf(".dnd") != -1 || msg.indexOf(".DND") != -1) {
            //不区分大小写中英文
            msg = msg.replaceAll("R", "r");
            msg = msg.replaceAll("C", "c");
            msg = msg.replaceAll("D", "d");
            msg = msg.replaceAll("N", "n");
            msg = msg.replaceAll("O", "o");
            msg = msg.replaceAll("。r", ".r");
            msg = msg.replaceAll("。coc", ".coc");
            msg = msg.replaceAll("。dnd", ".dnd");
            //
            if (sendDice(cq, msg, groupId, userId,nickname)) {
                return MESSAGE_IGNORE;
            }
        }

        // 继续执行下一个插件
        return MESSAGE_IGNORE;
    }


    private boolean sendDice(Bot cq, String msg, long groupId, long userId,String nickname) {
//        原coutType

        if (qqGroupSerivce.selectAllByID(String.valueOf(groupId)).getDiceOpen() == 0) {
            //判断骰娘是否开启
            if (msg.startsWith(".rh")) {
                checkAndSendType(cq, msg, groupId, userId, ".rh",nickname);
            } else if (msg.startsWith(".rd")) {
                checkAndSendType(cq, msg.replaceAll("rd", "r1d100("), groupId, userId, ".r",nickname);
            } else if (msg.startsWith(".r")) {
                checkAndSendType(cq, msg, groupId, userId, ".r",nickname);
            } else if (msg.startsWith(".coc")) {
                checkAndSendType(cq, msg, groupId, userId, ".coc",nickname);
            } else if (msg.startsWith(".dnd")) {
                checkAndSendType(cq, msg, groupId, userId, ".dnd",nickname);
            }
            // 不执行下一个插件
            return true;
        }
        return false;
    }

    private void checkAndSendType(Bot cq, String msg, long groupId, long userId, String type,String nickname) {
        if (msg.indexOf("(") != -1) {
            msg = msg.split("\\(")[0];
        }
        if (msg.indexOf("（") != -1) {
            msg = msg.split("\\（")[0];
        }
        String message[] = msg.replaceAll(" ", "").split(type);
        if (type.equals(".coc") || type.equals(".COC")) {
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
            cq.sendGroupMsg(groupId, nickname+" "+sb.toString(), false);
        } else if (type.equals(".dnd") || type.equals(".DND")) {
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
            cq.sendGroupMsg(groupId, nickname+" "+sb.toString(), false);
        } else {

            if (message.length > 0) {
                String count[] = message[1].replaceAll("D", "d").split("d");
                if (count.length > 0) {
                    String countEnd = "";
                    String countStrart = count[0];
                    String multiple = "1";
                    String add = "0";

                    //创建stringbuffer系列
                    StringBuffer sb = new StringBuffer("您投出了" + msg + "：{");
                    //创建最终结果变量
                    double finalCount = 0;
                    try {
                        if (count[1].indexOf("*") != -1 && (count[1].indexOf("+") != -1 || count[1].indexOf("-") != -1)) {
                            if (count[1].indexOf("+") != -1) {
                                if (count[1].indexOf("*") < count[1].indexOf("+")) {
                                    String countCenter = count[1].split("\\*")[0];
                                    add = count[1].split("\\+")[1].split("\\*")[0];
                                    countEnd = countCenter;
                                    multiple = count[1].split("\\+")[0].split("\\*")[1];
                                    finalCount = getFinalCount(countEnd, countStrart, sb, finalCount);
                                    finalCount = finalCount * Double.valueOf(multiple.toString());
                                    finalCount += Double.valueOf(add.toString());
                                } else if (count[1].indexOf("*") > count[1].indexOf("+")) {
                                    String countCenter = count[1].split("\\+")[0];
                                    multiple = count[1].split("\\*")[1].split("\\*")[0];
                                    countEnd = countCenter;
                                    add = count[1].split("\\*")[0].split("\\+")[1];
                                    finalCount = getFinalCount(countEnd, countStrart, sb, finalCount);
                                    finalCount += Double.valueOf(add.toString());
                                    finalCount = finalCount * Double.valueOf(multiple.toString());
                                }
                            }

                            if (count[1].indexOf("-") != -1) {
                                if (count[1].indexOf("*") < count[1].indexOf("-")) {
                                    String countCenter = count[1].split("\\*")[0];
                                    add = count[1].split("\\-")[1].split("\\*")[0];
                                    countEnd = countCenter;
                                    multiple = count[1].split("\\-")[0].split("\\*")[1];
                                    finalCount = getFinalCount(countEnd, countStrart, sb, finalCount);
                                    finalCount = finalCount * Double.valueOf(multiple.toString());
                                    finalCount -= Double.valueOf(add.toString());
                                } else if (count[1].indexOf("*") > count[1].indexOf("-")) {
                                    String countCenter = count[1].split("\\-")[0];
                                    multiple = count[1].split("\\*")[1].split("\\*")[0];
                                    countEnd = countCenter;
                                    add = count[1].split("\\*")[0].split("\\-")[1];
                                    finalCount = getFinalCount(countEnd, countStrart, sb, finalCount);
                                    finalCount -= Double.valueOf(add.toString());
                                    finalCount = finalCount * Double.valueOf(multiple.toString());
                                }
                            }

                        } else {
                            if (count[1].indexOf("-") != -1) {
                                countEnd = count[1].split("\\-")[0];
                                add = count[1].split("\\-")[1];
                                finalCount = getFinalCount(countEnd, countStrart, sb, finalCount);
                                finalCount -= Double.valueOf(add.toString());
                            } else {
                                if (count[1].indexOf("*") != -1) {
                                    countEnd = count[1].split("\\*")[0];
                                    multiple = count[1].split("\\*")[1];
                                } else if (count[1].indexOf("+") != -1) {
                                    countEnd = count[1].split("\\+")[0];
                                    add = count[1].split("\\+")[1];
                                } else {
                                    countEnd = count[1];
                                }
                                finalCount = getFinalCount(countEnd, countStrart, sb, finalCount);
                                finalCount = finalCount * Double.valueOf(multiple.toString());
                                finalCount += Double.valueOf(add.toString());
                            }
                        }
                        sb.append("} 合计：" + String.valueOf((int) finalCount));
                    } catch (Exception e) {
                        sb.append("} 合计：" + 0);
                    }


                    // 调用API发送消息
                    if (msg.startsWith(".rh")) {
                        //私发
                        cq.sendPrivateMsg(userId, sb.toString(), false);
                    } else if (msg.startsWith(".r")) {
                        //群聊
                        cq.sendGroupMsg(groupId, nickname+" "+sb.toString(), false);
                    }

                }
            }
        }
    }

    private double getFinalCount(String countEnd, String countStrart, StringBuffer sb, double finalCount) {
        //创建骰子数据list
        List<Integer> dList = new ArrayList<>();
        //ndm创建n随机数
        for (int i = 0; i < Integer.parseInt(countStrart); i++) {
            Random random = new Random();
            int j = random.nextInt(Integer.parseInt(countEnd)) + 1;
            dList.add(j);
        }

        //循环拿到最终结果，并添加回复字符串
        for (int i = 0; i < dList.size(); i++) {
            finalCount += dList.get(i);
            sb.append(dList.get(i).toString());
            if (i != (dList.size() - 1)) {
                sb.append(",");
            }
        }
        return finalCount;
    }

    private int getRandom(int n) {
        int k = 0;
        int array[] = new int[n];
        for (int j = 0; j < n; j++) {
            Random random = new Random();
            k += random.nextInt(6) + 1;
        }
        return k;
    }

    private int getRandomDND(int n) {
        int k = 0;
        int array[] = new int[n];
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


}

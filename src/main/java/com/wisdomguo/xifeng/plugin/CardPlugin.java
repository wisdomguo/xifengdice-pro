package com.wisdomguo.xifeng.plugin;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wisdomguo.xifeng.modules.charactercoc.entity.CharacterCoC;
import com.wisdomguo.xifeng.modules.qqgroup.entity.QQGroup;
import com.wisdomguo.xifeng.modules.skillcoc.entity.SkillCoC;
import com.wisdomguo.xifeng.modules.charactercoc.service.CharacterCocService;
import com.wisdomguo.xifeng.modules.qqgroup.service.QQGroupSerivce;
import com.wisdomguo.xifeng.modules.skillcoc.service.SkillCocService;
import com.wisdomguo.xifeng.assist.BlackMap;
import com.wisdomguo.xifeng.assist.BonusDice;
import com.wisdomguo.xifeng.util.BoolUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;


/**
 * 示例插件
 * 插件必须继承CQPlugin，上面要 @Component
 * <p>
 * 添加事件：光标移动到类中，按 Ctrl+O 添加事件(讨论组消息、加群请求、加好友请求等)
 * 查看API参数类型：光标移动到方法括号中按Ctrl+P
 * 查看API说明：光标移动到方法括号中按Ctrl+Q
 */

/**
 * CardPlugin
 * 角色卡
 *
 * @author wisdom-guo
 * @since 2021/4/3
 */
@Slf4j
@Component
public class CardPlugin extends BotPlugin {

    @Autowired
    private CharacterCocService characterCocService;

    @Autowired
    private SkillCocService skillCocService;

    @Autowired
    private QQGroupSerivce qqGroupSerivce;

    @Override
    @SneakyThrows
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
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
        if (qqGroupSerivce.selectAllByID(String.valueOf(groupId)).getDiceOpen() == 0) {
            //修改hp
            if (msg.startsWith(".sthp")) {
                return changeHp(bot, msg, groupId, userId, nickname);
                //修改mp
            } else if (msg.startsWith(".stmp")) {
                return changeMp(bot, msg, groupId, userId, nickname);
                //修改san
            } else if (msg.startsWith(".stsan")) {
                return changeSan(bot, msg, groupId, userId, nickname);
                //设置角色卡
            } else if (msg.startsWith(".st show")) {
                return checkCard(bot, msg, groupId, userId, nickname);
            } else if (msg.startsWith(".st")) {
                if (setCocCard(msg, userId, groupId)) {
                    bot.sendGroupMsg(groupId, "嗯，是发现了新的星球吗？那么设置成功，这片星空上已经刻印下" + nickname + "的印记了", false);
                    return MESSAGE_IGNORE;
                } else {
                    bot.sendGroupMsg(groupId, "对不起，您的观测失败了呢，惜风找不到" + nickname + "所指定的星座。请重新再设置一次吧。", false);
                    return MESSAGE_IGNORE;
                }
            }
            if (msg.startsWith(".sc")) {
                return changeSanCheck(bot, msg, groupId, userId, nickname);
            }
            if (msg.startsWith(".ch")) {
                if (changeSkill(bot, msg, groupId, userId, nickname)) {
                    return MESSAGE_IGNORE;
                }
            }
            if (msg.startsWith(".pc")) {
                if (msg.indexOf("rename") != -1) {
                    return cardRename(bot, msg, groupId, userId, nickname);
                }
                if (msg.indexOf("del") != -1) {
                    return delCard(bot, msg, groupId, userId, nickname);
                }
                if (msg.indexOf("list") != -1) {
                    return getCardList(bot, groupId, userId, nickname);
                }
                if (msg.indexOf("grp") != -1) {
                    return getGroupList(bot, groupId, userId, nickname);
                }
                if (msg.indexOf("clr") != -1) {
                    return clearCard(bot, groupId, userId, nickname);
                }
                if (msg.indexOf("bind") != -1) {
                    return setDefaultCardOfGroup(bot, msg, groupId, userId, nickname);
                }
            }
            if (msg.startsWith(".ra")) {
                return appraisalSkill(bot, msg, groupId, userId, nickname, 0);
            } else if (msg.startsWith(".rb") && !"".equals(msg.replace("rb", ""))) {
                return appraisalSkill(bot, msg.replace("rb", "ra"), groupId, userId, nickname, 1);
            } else if (msg.startsWith(".rp") && !"".equals(msg.replace("rp", ""))) {
                return appraisalSkill(bot, msg.replace("rp", "ra"), groupId, userId, nickname, 2);
            } else if (msg.startsWith(".rb") && "".equals(msg.replace("rb", ""))) {
                return setPunAndRew(bot, msg, groupId, nickname, ".rb", 1, "[奖励骰:");
            } else if (msg.startsWith(".rp") && "".equals(msg.replace("rp", ""))) {
                return setPunAndRew(bot, msg, groupId, nickname, ".rp", 2, "[惩罚骰:");
            }
        }
        return MESSAGE_IGNORE;
    }

    private int setPunAndRew(@NotNull Bot bot, String msg, long groupId, String nickname, String s, int i, String s2) {
        String skill = msg.replace(s, "").replaceAll(" ", "");
        char[] strs = skill.toCharArray();
        String val = "";
        for (char c : strs) {
            if (isNumber(String.valueOf(c))) {
                val += c;
            }
        }
        BonusDice dice = getSkillResult(i, Integer.valueOf(val));
        bot.sendGroupMsg(groupId, nickname + "投掷" + skill + "=" + dice.getResultFirst() + s2 + dice.getResultTen() + "]=" + dice.getResult(), false);
        return MESSAGE_IGNORE;
    }

    private int checkCard(@NotNull Bot bot, String msg, long groupId, long userId, String nickname) {
        try {
            CharacterCoC characterCoC = null;
            StringBuilder builder = new StringBuilder(nickname + "您的该角色卡详情为:");
            if ("".equals(msg.replace(".st show", "").replace(" ", ""))) {
                characterCoC = characterCocService.getOne(
                        Wrappers.<CharacterCoC>lambdaQuery()
                                .eq(CharacterCoC::getQqId, userId)
                                .eq(CharacterCoC::getGroupId, groupId)
                                .eq(CharacterCoC::getDef, 0)
                                .orderByDesc(CharacterCoC::getCount)
                );
            } else {
                characterCoC = characterCocService.getOne(
                        Wrappers.<CharacterCoC>lambdaQuery()
                                .eq(CharacterCoC::getQqId, userId)
                                .eq(CharacterCoC::getGroupId, groupId)
                                .eq(CharacterCoC::getCount, Integer.valueOf(msg.replace(".st show", "")))
                                .orderByDesc(CharacterCoC::getCount)
                );
            }
            if (characterCoC == null) {
                bot.sendGroupMsg(groupId, "没有查询到人物卡...重新设置下试试吧!", false);
                return MESSAGE_IGNORE;
            }
            builder.append("\n姓名:" + characterCoC.getName());
            builder.append("\n体力:" + characterCoC.getHp());
            builder.append("\t魔法:" + characterCoC.getHp());
            builder.append("\n力量:" + characterCoC.getStr());
            builder.append("\t敏捷:" + characterCoC.getDex());
            builder.append("\t意志:" + characterCoC.getPow());
            builder.append("\n外貌:" + characterCoC.getApp());
            builder.append("\t教育:" + characterCoC.getEdu());
            builder.append("\t体型:" + characterCoC.getSiz());
            builder.append("\n灵感:" + characterCoC.getIns());
            builder.append("\t幸运:" + characterCoC.getLuck());
            builder.append("\t理智:" + characterCoC.getSan());
            bot.sendGroupMsg(groupId, builder.toString(), false);
            return MESSAGE_IGNORE;
        } catch (Exception e) {
            bot.sendGroupMsg(groupId, "没有查询到人物卡...重新设置下试试吧!", false);
            return MESSAGE_IGNORE;
        }
    }

    /**
     * 查看本群所有卡
     *
     * @param
     * @return int
     * @throws
     * @author wisdom-guo
     * @date 2021/4/5
     */
    private int getCardList(@NotNull Bot bot, long groupId, long userId, String nickname) {
        List<CharacterCoC> characterCoCs = characterCocService.list(Wrappers.<CharacterCoC>lambdaQuery()
                .eq(CharacterCoC::getQqId, userId)
                .eq(CharacterCoC::getGroupId, groupId)
        );
        StringBuilder builder = new StringBuilder(nickname + "您在本群的全部角色列表：");
        return getCharacterList(bot, groupId, userId, characterCoCs, builder, 0);
    }

    /**
     * 重新设置绑定卡
     *
     * @return int
     * @throws
     * @author wisdom-guo
     * @date 2021/4/5
     */
    private int setDefaultCardOfGroup(@NotNull Bot bot, String msg, long groupId, long userId, String nickname) {
        msg = msg.replace(".pc bind ", "").replace(" ", "");
        try {
            if (characterCocService.update(Wrappers.<CharacterCoC>lambdaUpdate()
                    .set(CharacterCoC::getDef, 1)
                    .eq(CharacterCoC::getQqId, userId)
                    .eq(CharacterCoC::getGroupId, groupId))
                    && characterCocService.update(Wrappers.<CharacterCoC>lambdaUpdate()
                    .set(CharacterCoC::getDef, 0)
                    .eq(CharacterCoC::getQqId, userId)
                    .eq(CharacterCoC::getCount, Integer.valueOf(msg))
                    .eq(CharacterCoC::getGroupId, groupId))
            ) {
                bot.sendGroupMsg(groupId, nickname + "这么钟情于这颗星吗？它确实十分美丽。就决定将它定为您的命之星吧。", false);
                return MESSAGE_IGNORE;
            } else {
                bot.sendGroupMsg(groupId, "抱歉，惜风现在无法为" + nickname + "设置命星。到底是哪里出了问题呢？命运真是扑朔迷离。", false);
                return MESSAGE_IGNORE;
            }
        } catch (Exception e) {
            bot.sendGroupMsg(groupId, "抱歉，惜风现在无法为" + nickname + "设置命星。到底是哪里出了问题呢？命运真是扑朔迷离。", false);
            return MESSAGE_IGNORE;
        }
    }

    /**
     * 查看所有群默认卡
     *
     * @return int
     * @throws
     * @author wisdom-guo
     * @date 2021/4/5
     */
    private int getGroupList(@NotNull Bot bot, long groupId, long userId, String nickname) {
        List<CharacterCoC> characterCoCs = characterCocService.list(Wrappers.<CharacterCoC>lambdaQuery()
                .eq(CharacterCoC::getQqId, userId)
                .eq(CharacterCoC::getDef, 0)
        );
        StringBuilder builder = new StringBuilder(nickname + "在各群的默认角色列表：");
        return getCharacterList(bot, groupId, userId, characterCoCs, builder, 1);
    }

    /**
     * sancheck
     *
     * @return int
     * @throws
     * @author wisdom-guo
     * @date 2021/4/5
     */
    private int changeSanCheck(@NotNull Bot bot, String msg, long groupId, long userId, String nickname) {
        try {
            CharacterCoC characterCoC = characterCocService.getOne(
                    Wrappers.<CharacterCoC>lambdaQuery()
                            .eq(CharacterCoC::getQqId, userId)
                            .eq(CharacterCoC::getGroupId, groupId)
                            .eq(CharacterCoC::getDef, 0)
            );
            if (characterCoC == null) {
                bot.sendGroupMsg(groupId, "没有查询到人物卡...重新设置下试试吧!", false);
                return MESSAGE_IGNORE;
            }

            Random random = new Random();
            //投掷骰子
            int powResult = random.nextInt(101);

            String newMsg = msg.replace(".sc", "").replace(" ", "");

            String[] msgs = newMsg.split("/");

            if (msgs.length > 1) {
                int san = 0;
                StringBuilder builder = new StringBuilder();
                if (powResult > characterCoC.getPow()) {
                    if (msgs[1].indexOf("d") != -1) {
                        for (int i = 0; i < Integer.valueOf(msgs[1].split("d")[0]); i++) {
                            san += random.nextInt(Integer.valueOf(msgs[1].split("d")[1]));
                        }
                    } else {
                        san = Integer.valueOf(msgs[1]);
                    }
                    builder.append("未知总是让人向往又惶恐，用您的勇气直面它，克服一切磨难吧！\n");
                    builder.append(nickname + "的San Check结果是：\n");
                    builder.append(nickname + "的意志臣服于恐惧了吗，San值降低" + san + "，剩余");
                } else {
                    if (msgs[0].indexOf("d") != -1) {
                        for (int i = 0; i < Integer.valueOf(msgs[0].split("d")[0]); i++) {
                            san += random.nextInt(Integer.valueOf(msgs[0].split("d")[1]));
                        }
                    } else {
                        san = Integer.valueOf(msgs[0]);
                    }

                    builder.append("未知总是让人向往又惶恐，用您的勇气直面它，克服一切磨难吧！\n");
                    builder.append(nickname + "的San Check结果是：\n");
                    builder.append("是" + nickname + "的意志战胜了未知，San值降低" + san + "，剩余");
                }


                if (characterCocService.update(
                        Wrappers.<CharacterCoC>lambdaUpdate()
                                .set(CharacterCoC::getSan, characterCoC.getSan() - san)
                                .eq(CharacterCoC::getQqId, userId)
                                .eq(CharacterCoC::getGroupId, groupId)
                                .eq(CharacterCoC::getDef, 0)
                )) {
                    //修改成功
                    builder.append(characterCoC.getSan() - san);
                    bot.sendGroupMsg(groupId, builder.toString(), false);
                    return MESSAGE_IGNORE;
                } else {
                    //修改失败
                    bot.sendGroupMsg(groupId, nickname + "的意志与未知之物的对抗结果是......嗯？对惜风来说也会有未知之物吗？", false);
                    return MESSAGE_IGNORE;
                }
            } else {
                //格式出错
                bot.sendGroupMsg(groupId, nickname + "的意志与未知之物的对抗结果是......嗯？对惜风来说也会有未知之物吗？", false);
                return MESSAGE_IGNORE;
            }
        } catch (Exception e) {
            //格式崩溃
            e.printStackTrace();
            bot.sendGroupMsg(groupId, nickname + "的意志与未知之物的对抗结果是......嗯？对惜风来说也会有未知之物吗？", false);
            return MESSAGE_IGNORE;
        }
    }

    /**
     * 修改sam值
     *
     * @param
     * @return int
     * @throws
     * @author wisdom-guo
     * @date 2021/4/5
     */
    private int changeSan(@NotNull Bot bot, String msg, long groupId, long userId, String nickname) {
        try {
            String symbol = "";
            if (msg.indexOf("+") != -1) {
                symbol = "+";
            }
            if (msg.indexOf("-") != -1) {
                symbol = "-";
            }
            int san = Integer.valueOf(msg.replaceAll(" ", "").replace(".stsan", ""));

            CharacterCoC characterCoC = characterCocService.getOne(Wrappers.<CharacterCoC>lambdaQuery()
                    .eq(CharacterCoC::getQqId, userId)
                    .eq(CharacterCoC::getDef, 0)
                    .eq(CharacterCoC::getGroupId, groupId));

            if (characterCoC == null) {
                bot.sendGroupMsg(groupId, "没有查询到人物卡...重新设置下试试吧!", false);
                return MESSAGE_IGNORE;
            }

            if (StringUtils.isNotBlank(symbol)) {
                san = characterCoC.getSan() + san;
            }
            if (characterCocService.update(
                    Wrappers.<CharacterCoC>lambdaUpdate()
                            .set(CharacterCoC::getSan, san)
                            .eq(CharacterCoC::getQqId, userId)
                            .eq(CharacterCoC::getDef, 0)
                            .eq(CharacterCoC::getGroupId, groupId)
            )) {
                bot.sendGroupMsg(groupId, nickname + "的San值参数修改成功，经过这次修正，越发接近世界的真实了呢。", false);
                return MESSAGE_IGNORE;
            } else {
                bot.sendGroupMsg(groupId, "目标定位成功，修改......" + nickname + "的San值参数修改失败。抱歉，惜风正在重新测算中。", false);
                return MESSAGE_IGNORE;
            }
        } catch (Exception e) {
            bot.sendGroupMsg(groupId, nickname + "也会发生这样的格式错误吗？这样，可是没办法探明世界的真实的哦。", false);
            return MESSAGE_IGNORE;
        }
    }

    /**
     * 修改mp
     *
     * @return int
     * @throws
     * @author wisdom-guo
     * @date 2021/4/5
     */
    private int changeMp(@NotNull Bot bot, String msg, long groupId, long userId, String nickname) {
        try {
            String symbol = "";
            if (msg.indexOf("+") != -1) {
                symbol = "+";
            }
            if (msg.indexOf("-") != -1) {
                symbol = "-";
            }
            int mp = Integer.valueOf(msg.replaceAll(" ", "").replace(".stmp", ""));

            CharacterCoC characterCoC = characterCocService.getOne(Wrappers.<CharacterCoC>lambdaQuery()
                    .eq(CharacterCoC::getQqId, userId)
                    .eq(CharacterCoC::getDef, 0)
                    .eq(CharacterCoC::getGroupId, groupId));

            if (characterCoC == null) {
                bot.sendGroupMsg(groupId, "没有查询到人物卡...重新设置下试试吧!", false);
                return MESSAGE_IGNORE;
            }

            if (StringUtils.isNotBlank(symbol)) {
                mp = characterCoC.getMp() + mp;
            }

            if (characterCocService.update(
                    Wrappers.<CharacterCoC>lambdaUpdate()
                            .set(CharacterCoC::getMp, mp)
                            .eq(CharacterCoC::getQqId, userId)
                            .eq(CharacterCoC::getDef, 0)
                            .eq(CharacterCoC::getGroupId, groupId)
            )) {
                bot.sendGroupMsg(groupId, nickname + "的Mp值参数修改成功，经过这次修正，越发接近世界的真实了呢。", false);
                return MESSAGE_IGNORE;
            } else {
                bot.sendGroupMsg(groupId, "目标定位成功，修改......" + nickname + "的Mp值参数修改失败。抱歉，惜风正在重新测算中。", false);
                return MESSAGE_IGNORE;
            }
        } catch (Exception e) {
            bot.sendGroupMsg(groupId, nickname + "也会发生这样的格式错误吗？这样，可是没办法探明世界的真实的哦。", false);
            return MESSAGE_IGNORE;
        }
    }

    /**
     * 修改生命值
     *
     * @return int
     * @throws
     * @author wisdom-guo
     * @date 2021/4/5
     */
    private int changeHp(@NotNull Bot bot, String msg, long groupId, long userId, String nickname) {
        try {
            String symbol = "";
            if (msg.indexOf("+") != -1) {
                symbol = "+";
            }
            if (msg.indexOf("-") != -1) {
                symbol = "-";
            }
            int hp = Integer.valueOf(msg.replaceAll(" ", "").replace(".sthp", ""));
            CharacterCoC characterCoC = characterCocService.getOne(Wrappers.<CharacterCoC>lambdaQuery()
                    .eq(CharacterCoC::getQqId, userId)
                    .eq(CharacterCoC::getDef, 0)
                    .eq(CharacterCoC::getGroupId, groupId));

            if (characterCoC == null) {
                bot.sendGroupMsg(groupId, "没有查询到人物卡...重新设置下试试吧!", false);
                return MESSAGE_IGNORE;
            }

            if (StringUtils.isNotBlank(symbol)) {
                hp = characterCoC.getHp() + hp;
            }
            if (characterCocService.update(
                    Wrappers.<CharacterCoC>lambdaUpdate()
                            .set(CharacterCoC::getHp, hp)
                            .eq(CharacterCoC::getQqId, userId)
                            .eq(CharacterCoC::getDef, 0)
                            .eq(CharacterCoC::getGroupId, groupId)
            )) {
                bot.sendGroupMsg(groupId, nickname + "的Hp值参数修改成功，经过这次修正，越发接近世界的真实了呢。", false);
                return MESSAGE_IGNORE;
            } else {
                bot.sendGroupMsg(groupId, "目标定位成功，修改......" + nickname + "的Hp值参数修改失败。抱歉，惜风正在重新测算中。", false);
                return MESSAGE_IGNORE;
            }
        } catch (Exception e) {
            bot.sendGroupMsg(groupId, nickname + "也会发生这样的格式错误吗？这样，可是没办法探明世界的真实的哦。", false);
            return MESSAGE_IGNORE;
        }
    }

    /**
     * 修改技能
     *
     * @return int
     * @throws
     * @author wisdom-guo
     * @date 2021/4/5
     */
    private boolean changeSkill(@NotNull Bot bot, String msg, long groupId, long userId, String nickname) {
        try {
            String[] msgs = msg.replace(".ch", "").split(" ");
            if (msgs.length > 1) {
                CharacterCoC characterCoC = characterCocService.getOne(
                        Wrappers.<CharacterCoC>lambdaQuery()
                                .eq(CharacterCoC::getQqId, userId)
                                .eq(CharacterCoC::getGroupId, groupId)
                                .eq(CharacterCoC::getDef, 0)
                );

                if (characterCoC == null) {
                    bot.sendGroupMsg(groupId, "没有查询到人物卡...重新设置下试试吧!", false);
                    return true;
                }

                if (skillCocService.update(
                        Wrappers.<SkillCoC>lambdaUpdate()
                                .set(SkillCoC::getValue, Integer.valueOf(msgs[1]))
                                .eq(SkillCoC::getCharacterId, characterCoC.getId())
                                .eq(SkillCoC::getName, msgs[0])
                )) {
                    bot.sendGroupMsg(groupId, nickname + "的技能值参数修改成功，经过这次修正，越发接近世界的真实了呢。", false);
                    return true;
                }
            } else {
                bot.sendGroupMsg(groupId, "目标定位成功，修改......" + nickname + "的技能值参数修改失败。抱歉，惜风正在重新测算中。", false);
                return true;
            }
        } catch (Exception e) {
            bot.sendGroupMsg(groupId, nickname + "也会发生这样的格式错误吗？这样，可是没办法探明世界的真实的哦。", false);
            return true;
        }
        return false;
    }

    /**
     * 删除本群所有卡
     *
     * @return int
     * @throws
     * @author wisdom-guo
     * @date 2021/4/5
     */
    private int clearCard(@NotNull Bot bot, long groupId, long userId, String nickname) {
        //获取本群所有卡
        List<CharacterCoC> characterCoCs = characterCocService.list(
                Wrappers.<CharacterCoC>lambdaQuery()
                        .eq(CharacterCoC::getQqId, userId)
                        .eq(CharacterCoC::getGroupId, groupId)
        );
        List<Long> idList = new ArrayList<>();
        //抓取所有卡ID
        for (CharacterCoC c : characterCoCs) {
            idList.add(c.getId());
        }
        //移除所有卡极其技能
        if (characterCocService.remove(
                Wrappers.<CharacterCoC>lambdaQuery()
                        .in(CharacterCoC::getId, idList)
        ) && skillCocService.remove(
                Wrappers.<SkillCoC>lambdaQuery()
                        .in(SkillCoC::getCharacterId, idList)
        )) {
            bot.sendGroupMsg(groupId, "您的卡清除成功，漫天繁星终将化为虚无...", false);
            return MESSAGE_IGNORE;
        } else {
            bot.sendGroupMsg(groupId, "抱歉哦，清除全部角色卡失败，我果然还是没法忘掉所有呢。惜风会继续努力的！", false);
            return MESSAGE_IGNORE;
        }
    }

    /**
     * 删除卡
     *
     * @return int
     * @throws
     * @author wisdom-guo
     * @date 2021/4/5
     */
    @Transactional(rollbackFor = Exception.class)
    public int delCard(@NotNull Bot bot, String msg, long groupId, long userId, String nickname) {
        int id = Integer.valueOf(msg.replace(".pc del ", ""));
        CharacterCoC characterCoC = characterCocService.getOne(
                Wrappers.<CharacterCoC>lambdaQuery()
                        .eq(CharacterCoC::getQqId, userId)
                        .eq(CharacterCoC::getGroupId, groupId)
                        .eq(CharacterCoC::getCount, id)
        );

        if (characterCoC == null) {
            bot.sendGroupMsg(groupId, "没有查询到人物卡...重新设置下试试吧!", false);
            return MESSAGE_IGNORE;
        }

        //查询该群中所剩下的卡
        List<CharacterCoC> cocs = characterCocService.list(Wrappers.<CharacterCoC>lambdaQuery().eq(CharacterCoC::getQqId, userId).eq(CharacterCoC::getGroupId, groupId).ne(CharacterCoC::getId, characterCoC.getId()));
        //删除对应的卡以及对应技能
        if (characterCocService.removeById(characterCoC.getId())
                && skillCocService.remove(
                Wrappers.<SkillCoC>lambdaQuery()
                        .eq(SkillCoC::getCharacterId, characterCoC.getId()
                        ))) {
            //将卡重新排序
            for (int i = 0; i < cocs.size(); i++) {
                if (i == 0 && characterCoC.getDef() == 0) {
                    characterCocService.update(Wrappers.<CharacterCoC>lambdaUpdate().set(CharacterCoC::getCount, i).set(CharacterCoC::getDef, 0).eq(CharacterCoC::getId, cocs.get(i).getId()));
                } else {
                    characterCocService.update(Wrappers.<CharacterCoC>lambdaUpdate().set(CharacterCoC::getCount, i).eq(CharacterCoC::getId, cocs.get(i).getId()));
                }
            }
            bot.sendGroupMsg(groupId, "删除成功，就让那片星域只留存在" + nickname + "的记忆中吧。", false);
            return MESSAGE_IGNORE;
        } else {
            bot.sendGroupMsg(groupId, "对不起，似乎还是能照常观测到那片星域，请" + nickname + "再删除一次吧。", false);
            return MESSAGE_IGNORE;
        }
    }

    /**
     * 重命名卡
     *
     * @return int
     * @throws
     * @author wisdom-guo
     * @date 2021/4/5
     */
    private int cardRename(@NotNull Bot bot, String msg, long groupId, long userId, String nickname) {
        //取出改名id和名称
        String[] msgs = msg.replace(".pc rename ", "").split(" ");
        int id = -1;
        String name = "";
        //判断是否符合格式
        if (msgs.length >= 2 && StringUtils.isNotBlank(msgs[1])) {
            name = msgs[1];
            id = Integer.valueOf(msgs[0]);
        } else {
            bot.sendGroupMsg(groupId, "惜风觉得这个命名似乎不妥，请" + nickname + "谨慎思考后再做商量吧。", false);
            return MESSAGE_IGNORE;
        }
        //修改名称
        if (characterCocService.update(
                Wrappers.<CharacterCoC>lambdaUpdate()
                        .set(CharacterCoC::getName, name)
                        .eq(CharacterCoC::getQqId, userId)
                        .eq(CharacterCoC::getGroupId, groupId)
                        .eq(CharacterCoC::getCount, id)
        )) {
            bot.sendGroupMsg(groupId, "好的," + nickname + "，那么这颗星星就以此为名吧。", false);
            return MESSAGE_IGNORE;
        } else {
            bot.sendGroupMsg(groupId, "命名失败了呢," + nickname + "重新尝试下吧", false);
            return MESSAGE_IGNORE;
        }
    }

    /**
     * 查看卡拼接
     *
     * @return int
     * @throws
     * @author wisdom-guo
     * @date 2021/4/5
     */
    private int getCharacterList(@NotNull Bot bot, long groupId, long userId, List<CharacterCoC> characterCoCs, StringBuilder builder, int type) {
        for (CharacterCoC c : characterCoCs) {
            builder.append("\n[" + c.getCount() + "]" + c.getName());
        }
        if (type == 0) {
            CharacterCoC card = characterCocService.getOne(
                    Wrappers.<CharacterCoC>lambdaQuery()
                            .eq(CharacterCoC::getQqId, userId)
                            .eq(CharacterCoC::getGroupId, groupId)
                            .eq(CharacterCoC::getDef, 0)
            );
            if (card != null) {
                builder.append("\ndefault:" + card.getName());
            } else {
                builder.append("\ndefault:");
            }
        }
        bot.sendGroupMsg(groupId, builder.toString(), false);
        return MESSAGE_IGNORE;
    }

    /**
     * 技能判定
     *
     * @return int
     * @throws
     * @author wisdom-guo
     * @date 2021/4/5
     */
    private int appraisalSkill(@NotNull Bot bot, String msg, long groupId, long userId, String nickname, int type) {
        try {
            //取出技能名
            String skill = msg.replace(".ra", "");
            char[] strs = skill.toCharArray();
            //技能判定值
            String val = "";
            //骰子数量
            String diceCount = "";
            //技能名
            String skillJudg = "";
            //设置技能值
            int skillValue = 0;
            //加值
            int addition = 0;
            //是否有符号
            String symbol = "";
            //第一个骰子
            String firstResult = "";
            //
            int skillResult = 0;
            //是否是数字
            boolean judgment = true;
            for (char c : strs) {
                //判断是否是奖惩骰
                if (type != 0) {
                    if (isNumber(String.valueOf(c))) {
                        if (judgment) {
                            diceCount += c;
                        } else {
                            val += c;
                        }
                    } else {
                        judgment = false;
                        skillJudg += c;
                    }
                } else {
                    if (isNumber(String.valueOf(c))) {
                        val += c;
                    }
                }
            }
            if (StringUtils.isNotBlank(skillJudg)) {
                skill = skillJudg + val;
            }
            if (!StringUtils.isNotBlank(diceCount)) {
                diceCount = "1";
            } else {
                if (Integer.valueOf(diceCount) > 10) {
                    diceCount = "10";
                }
            }
            //获取默认人物卡
            CharacterCoC characterCoC = characterCocService.getOne(Wrappers.<CharacterCoC>lambdaQuery().eq(CharacterCoC::getQqId, userId).eq(CharacterCoC::getGroupId, groupId).eq(CharacterCoC::getDef, 0));
            if (characterCoC == null && "".equals(val)) {
                bot.sendGroupMsg(groupId, "没有查询到人物卡...重新设置下试试吧!", false);
                return MESSAGE_IGNORE;
            }

            BonusDice dice = getSkillResult(type, Integer.valueOf(diceCount));
            skillResult = dice.getResult();
            //判断是否有加值减值
            if (skill.indexOf("+") != -1) {
                String[] skills = skill.split("\\+");
                skill = skills[0];
                addition = Integer.valueOf(skills[1]);
                symbol = "+";
            }
            if (skill.indexOf("-") != -1) {
                String[] skills = skill.split("\\-");
                skill = skills[0];
                addition = Integer.valueOf("-" + skills[1]);
                symbol = "-";
            }
            //查询对应技能
            SkillCoC skillCoC = null;
            if (characterCoC != null) {
                skillCoC = skillCocService.getOne(Wrappers.<SkillCoC>lambdaQuery().eq(SkillCoC::getCharacterId, characterCoC.getId()).eq(SkillCoC::getName, skill));
            }
            //没有该技能,并且技能判定值为空
            if (skillCoC == null && "".equals(val)) {
                bot.sendGroupMsg(groupId, "没有该技能呢...重新设置下试试吧!", false);
                return MESSAGE_IGNORE;
            }
            if (!StringUtils.isNotBlank(val) || StringUtils.isNotBlank(symbol)) {
                skillValue = skillCoC.getValue();
            } else {
                skillValue = Integer.valueOf(val);
            }
            //判断昵称是否为空
            if (characterCoC != null && StringUtils.isNotBlank(characterCoC.getName())) {
                nickname = characterCoC.getName();
            }
            StringBuilder builder = getRaBuilder(nickname, type, skill, skillValue, addition, symbol, firstResult, skillResult, dice);
            //发送信息
            bot.sendGroupMsg(groupId, builder.toString(), false);
            return MESSAGE_IGNORE;
        } catch (Exception e) {
            bot.sendGroupMsg(groupId, "指令出错了呢,再试试吧!", false);
            return MESSAGE_IGNORE;
        }
    }

    /**
      * ra获得发送字符串
      * @return StringBuilder
      * @throws
      * @author wisdom-guo
      * @date 2021/4/6
      */
    @NotNull
    private StringBuilder getRaBuilder(String nickname, int type, String skill, int skillValue, int addition, String symbol, String firstResult, int skillResult, BonusDice dice) {
        StringBuilder builder = new StringBuilder(nickname + "进行" + skill);
        if (StringUtils.isNotBlank(dice.getResultFirst())) {
            if (type == 1) {
                firstResult = dice.getResultFirst() + "[奖励骰:" + dice.getResultTen() + "]=";
            } else if (type == 2) {
                firstResult = dice.getResultFirst() + "[惩罚骰:" + dice.getResultTen() + "]=";
            }
        }
        if (StringUtils.isNotBlank(symbol)) {
            if ("-".equals(symbol)) {
                symbol = "";
            }
            builder.append(symbol + addition + "检定：1D100=" + firstResult + skillResult + "/" + (addition + skillValue));
        } else {
            builder.append("检定：1D100=" + firstResult + skillResult + "/" + (skillValue));
        }
        //判断结果
        if (skillResult <= 5) {
            builder.append(",大成功！看到了吗？这满天的星光，都只为你而闪耀。");
        } else if (skillResult >= 96) {
            builder.append(",大失败...当浩瀚星海都已燃尽黯淡的现在，这个世界…已经没有未来了吧......");
        } else if (skillResult <= ((skillValue + addition) / 5)) {
            builder.append(",极限成功！真是稀奇呢。不，原来是你的努力点亮了那片璀璨的星光吗？");
        } else if (skillResult <= ((skillValue + addition) / 2)) {
            builder.append(",困难成功！愿闪耀的启明星为你指引命运的方向。");
        } else if (skillResult <= (skillValue + addition)) {
            builder.append(",成功...在这片星海之中，总有一颗会照亮你的前路，继续前行吧。");
        } else {
            builder.append(",失败......星星...看不到了呢......");
        }
        return builder;
    }

    private BonusDice getSkillResult(int type, int diceCount) {
        BonusDice dice = new BonusDice();
        //创建随机数
        Random random = new Random();
        //投掷骰子
        int ten = random.nextInt(10);
        int ones = random.nextInt(10);
        int result = 1;

        if (ten == 0 && ones == 0) {
            dice.setResultFirst("100");
        } else {
            StringBuilder builder = new StringBuilder(String.valueOf(ten));
            builder.append(ones);
            dice.setResultFirst(builder.toString());
        }
        if (type == 1) {
            String reTens = "";
            for (int i = 0; i < diceCount; i++) {
                int reTen = random.nextInt(10);
                if (reTen == 0 && ones == 0) {
                    reTens += "10";
                } else {
                    reTens += String.valueOf(reTen);
                    if (reTen < ten) {
                        ten = reTen;
                    }
                }
                if (i + 1 < diceCount) {
                    reTens += " ";
                }
            }
            dice.setResultTen(reTens);
        } else if (type == 2) {
            String reTens = "";
            for (int i = 0; i < diceCount; i++) {
                int reTen = random.nextInt(10);
                if (reTen == 0 && ones == 0) {
                    reTens += "10";
                    ten = reTen;
                } else {
                    reTens += String.valueOf(reTen);
                    if (reTen > ten) {
                        ten = reTen;
                    }
                }
                if (i + 1 < diceCount) {
                    reTens += " ";
                }
            }
            dice.setResultTen(reTens);
        }

        if (ten == 0 && ones == 0) {
            dice.setResult(100);
        } else {
            StringBuilder builder = new StringBuilder(String.valueOf(ten));
            builder.append(ones);
            dice.setResult(Integer.valueOf(builder.toString()));
        }
        return dice;

    }

    /**
     * 设置角色卡
     *
     * @return int
     * @throws
     * @author wisdom-guo
     * @date 2021/4/5
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean setCocCard(String msg, long userId, long groupId) {
        Map<String, Integer> map = new HashMap<>();
        String[] msgs = msg.split(" ");
        int count = -1;
        CharacterCoC characterCoC = characterCocService.getOne(
                Wrappers.<CharacterCoC>lambdaQuery()
                        .eq(CharacterCoC::getQqId, userId)
                        .eq(CharacterCoC::getGroupId, groupId)
                        .orderByDesc(CharacterCoC::getCount)
                        .last(" limit 1")
        );


        if (characterCoC != null) {
            count = characterCoC.getCount();
        }
        count++;
        String characterName = "";
        int def = 1;
        if (msgs.length > 2) {
            characterName = msgs[1];
            map = getCardMap(msgs[2]);
        } else {
            map = getCardMap(msgs[1]);
        }
        if (characterCocService.count(
                Wrappers.<CharacterCoC>lambdaQuery()
                        .eq(CharacterCoC::getQqId, userId)
                        .eq(CharacterCoC::getGroupId, groupId)
                        .eq(CharacterCoC::getDef, 0)) == 0) {
            def = 0;
        }
        CharacterCoC newCard = new CharacterCoC(
                null, userId, groupId, characterName, map.get("str"), map.get("dex"),
                map.get("pow"), map.get("app"), map.get("edu"), map.get("siz"),
                map.get("int"), map.get("san"), map.get("hp"), map.get("mp"), map.get("幸运"), def, count);
        map.remove("str");
        map.remove("dex");
        map.remove("pow");
        map.remove("app");
        map.remove("edu");
        map.remove("siz");
        map.remove("int");
        map.remove("san值");
        map.remove("理智值");
        map.remove("体力");
        map.remove("魔法");
        map.remove("cm");
        boolean saveCharacter = characterCocService.save(newCard);
        List<SkillCoC> skillCocList = new ArrayList<>();
        for (String key : map.keySet()) {
            SkillCoC skillCoC = new SkillCoC(null, newCard.getId(), key, map.get(key));
            skillCocList.add(skillCoC);
        }
        boolean skill = skillCocService.saveBatch(skillCocList);
        if (saveCharacter && skill) {

            return true;
        }
        return false;
    }

    /**
     * 判断一个字符串是否是数字。
     *
     * @param string
     * @return
     */
    public static boolean isNumber(String string) {
        if (string == null) {
            return false;
        }
        Pattern pattern = compile("^-?\\d+(\\.\\d+)?$");
        return pattern.matcher(string).matches();
    }

    private Map<String, Integer> getCardMap(String str) {
        char[] strs = str.toCharArray();
        Map<String, Integer> map = new HashMap<>();
        int change = 0;
        String chinese = "";
        String number = "";
        for (char s : strs) {
            if (!isNumber(String.valueOf(s))) {
                if (change != 0) {
                    map.put(chinese, Integer.valueOf(number));
                    change = 0;
                    chinese = "";
                    number = "";
                }
                chinese += s;
            } else {
                change++;
                number += s;
            }

        }
        return map;
    }

}

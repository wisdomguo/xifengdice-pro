package com.wisdomguo.xifeng.plugin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wisdomguo.xifeng.assist.AssemblyCache;
import com.wisdomguo.xifeng.assist.BlackMap;
import com.wisdomguo.xifeng.assist.Disaster;
import com.wisdomguo.xifeng.modules.botset.qqgroup.entity.QQGroup;
import com.wisdomguo.xifeng.modules.botset.qqgroup.service.QQGroupSerivce;
import com.wisdomguo.xifeng.modules.game.explorepocket.entity.ExplorePocket;
import com.wisdomguo.xifeng.modules.game.explorepocket.service.ExplorePocketService;
import com.wisdomguo.xifeng.modules.game.farm.fruit.entity.Fruit;
import com.wisdomguo.xifeng.modules.game.farm.fruit.service.FruitService;
import com.wisdomguo.xifeng.modules.game.farm.plantedfield.entity.PlantedField;
import com.wisdomguo.xifeng.modules.game.farm.plantedfield.service.PlantedFieldService;
import com.wisdomguo.xifeng.modules.game.farm.seedbag.entity.SeedBag;
import com.wisdomguo.xifeng.modules.game.farm.seedbag.service.SeedBagService;
import com.wisdomguo.xifeng.modules.game.farm.seedspecies.entity.SeedSpecies;
import com.wisdomguo.xifeng.modules.game.farm.seedspecies.service.SeedSpeciesService;
import com.wisdomguo.xifeng.modules.game.farm.userinfo.entity.FarmUserInfo;
import com.wisdomguo.xifeng.modules.game.farm.userinfo.service.FarmUserInfoService;
import com.wisdomguo.xifeng.util.DateTimeUtil;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotBase;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


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

    @Resource
    private QQGroupSerivce qqGroupSerivce;

    @Resource
    private ExplorePocketService explorePocketService;

    @Resource
    private SeedBagService seedBagService;

    @Resource
    private SeedSpeciesService seedSpeciesService;

    @Resource
    private FruitService fruitService;

    @Resource
    private PlantedFieldService plantedFieldService;

    @Resource
    private FarmUserInfoService farmUserInfoService;

    @Resource
    private GamePlugin thisGame;

    private Map<Long, Date> expMap = new HashMap<>();

    private Map<Long, LocalDateTime> transfer = new HashMap<>();

    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    /**
     * 收到群消息时会调用这个方法
     *
     * @param cq    机器人对象，用于调用API，例如发送群消息 sendGroupMsg
     * @param event 事件对象，用于获取消息内容、群号、发送者QQ等
     * @return 是否继续调用下一个插件，IGNORE表示继续，BLOCK表示不继续
     */

    @Override
    public int onGroupMessage(@NotNull Bot cq, @NotNull OnebotEvent.GroupMessageEvent event) {
        // 获取 消息内容 群号 发送者QQ
        //获取消息内容
        String msg = event.getRawMessage().replaceAll("。", ".");
        //获取群号
        long groupId = event.getGroupId();
        //获取发送者QQ
        long userId = event.getUserId();

        //判断发送者是否在黑名单
        if (BlackMap.returnBlackList(userId)) {
            return MESSAGE_BLOCK;
        }

        //判断qq群功能是否开启
        QQGroup qqGroup = qqGroupSerivce.selectAllByID(String.valueOf(groupId));
        if (qqGroup.getXfOpen() == 1 || qqGroup.getGameOpen() == 1) {
            return MESSAGE_IGNORE;
        }

        //进行权限判断
        boolean judgment = (("owner".equals(event.getSender().getRole())
                || "admin".equals(event.getSender().getRole()))
                || userId == 1969077760L);

        //获取被@人QQ号备注
        String atUserId = "";
        if (event.getMessageList().size() > 1) {
            for (OnebotBase.Message message : event.getMessageList()) {
                if ("at".equals(message.getType())) {
                    atUserId = message.getDataMap().get("qq");
                }
            }
        }

        String nickname = cq.getGroupMemberInfo(groupId, userId, false).getCard();
        if (nickname.equals("")) {
            nickname = cq.getGroupMemberInfo(groupId, userId, false).getNickname();
        }

        try {
            //星空探索
            if (thisGame.starrySkyExploration(cq, event, msg, groupId, userId, nickname)) {
                return MESSAGE_IGNORE;
            }
        } catch (Exception e) {
            AssemblyCache.explorePockets.put(userId, explorePocketService.getById(userId));
        }

        //查看背包
        if (thisGame.selectBackpack(cq, event, msg, groupId, userId)) {
            return MESSAGE_IGNORE;
        }

        try {
            //兑换星碎
            if (thisGame.exchangeStarFragment(cq, event, msg, groupId, userId, nickname)) {
                return MESSAGE_IGNORE;
            }
        } catch (Exception e) {
            AssemblyCache.explorePockets.put(userId, explorePocketService.getById(userId));
        }

        //查看排行
        if (thisGame.selectRanking(cq, msg, groupId)) {
            return MESSAGE_IGNORE;
        }

        try {
            //星空抽奖
            if (thisGame.luckLottery(cq, event, msg, groupId, userId)) {
                return MESSAGE_IGNORE;
            }
        } catch (Exception e) {
            AssemblyCache.explorePockets.put(userId, explorePocketService.getById(userId));
            AssemblyCache.seedBags.put(userId, seedBagService.list(Wrappers.<SeedBag>lambdaQuery().eq(SeedBag::getQqId, userId)));
        }

        //查看种子袋
        if (thisGame.selectSeedBag(cq, msg, groupId, userId)) {
            return MESSAGE_IGNORE;
        }

        //查看仓库
        if (thisGame.selectWarehouse(cq, msg, groupId, userId)) {
            return MESSAGE_IGNORE;
        }

        //查看田地
        if (thisGame.selectField(cq, msg, groupId, userId)) {
            return MESSAGE_IGNORE;
        }

        //农业商店
        if (thisGame.agricultureStore(cq, msg, groupId)) {
            return MESSAGE_IGNORE;
        }

        try {
            //购买种子
            if (thisGame.buySeeds(cq, event, msg, groupId, userId)) {
                return MESSAGE_IGNORE;
            }
        } catch (Exception e) {
            AssemblyCache.explorePockets.put(userId, explorePocketService.getById(userId));
            AssemblyCache.seedBags.put(userId, seedBagService.list(Wrappers.<SeedBag>lambdaQuery().eq(SeedBag::getQqId, userId)));
        }

        try {
            //种植果实
            if (thisGame.plantingSeeds(cq, msg, groupId, userId)) {
                return MESSAGE_IGNORE;
            }
        } catch (Exception e) {
            AssemblyCache.plantedFields.put(userId, plantedFieldService.list(Wrappers.<PlantedField>lambdaQuery().eq(PlantedField::getQqId, userId)));
            AssemblyCache.seedBags.put(userId, seedBagService.list(Wrappers.<SeedBag>lambdaQuery().eq(SeedBag::getQqId, userId)));
        }

        try {
            //收获果实
            if (thisGame.rewardSeeds(cq, msg, groupId, userId)) {
                return MESSAGE_IGNORE;
            }
        } catch (Exception e) {
            AssemblyCache.plantedFields.put(userId, plantedFieldService.list(Wrappers.<PlantedField>lambdaQuery().eq(PlantedField::getQqId, userId)));
            AssemblyCache.userInfos.put(userId, farmUserInfoService.getById(userId));
            AssemblyCache.fruits.put(userId, fruitService.list(Wrappers.<Fruit>lambdaQuery().eq(Fruit::getQqId, userId)));
        }

        try {
            //出售果实
            if (thisGame.sellSeeds(cq, event, msg, groupId, userId)) {
                return MESSAGE_IGNORE;
            }
        } catch (Exception e) {
            AssemblyCache.explorePockets.put(userId, explorePocketService.getById(userId));
            AssemblyCache.fruits.put(userId, fruitService.list(Wrappers.<Fruit>lambdaQuery().eq(Fruit::getQqId, userId)));
        }

        try {
            //恢复田地
            if (thisGame.restoreFarm(cq, event, msg, groupId, userId)) {
                return MESSAGE_IGNORE;
            }
        } catch (Exception e) {
            AssemblyCache.explorePockets.put(userId, explorePocketService.getById(userId));
            AssemblyCache.userInfos.put(userId, farmUserInfoService.getById(userId));
        }

        try {
            //购买田地
            if (thisGame.buyFarm(cq, event, msg, groupId, userId)) {
                return MESSAGE_IGNORE;
            }
        } catch (Exception e) {
            AssemblyCache.explorePockets.put(userId, explorePocketService.getById(userId));
            AssemblyCache.userInfos.put(userId, farmUserInfoService.getById(userId));
        }

        try {
            //使用加速卡(随机
            if (thisGame.accelerate(cq, msg, groupId, userId)) {
                return MESSAGE_IGNORE;
            }
        } catch (Exception e) {
            AssemblyCache.plantedFields.put(userId, plantedFieldService.list(Wrappers.<PlantedField>lambdaQuery().eq(PlantedField::getQqId, userId)));
            AssemblyCache.userInfos.put(userId, farmUserInfoService.getById(userId));
        }

        try {
            //随机偷菜
            if (thisGame.stealFruit(cq, event, msg, groupId, userId)) {
                return MESSAGE_IGNORE;
            }
        } catch (Exception e) {
            AssemblyCache.explorePockets.put(userId, explorePocketService.getById(userId));
        }

        try {
            //转账
            if (transfer(cq, event, msg, groupId, userId)) {
                return MESSAGE_IGNORE;
            }
        } catch (Exception e) {
            //加载星空寻宝背包
            explorePocketService.list().stream().forEach(item -> {
                AssemblyCache.explorePockets.put(item.getQqId(), item);
            });
            cq.sendGroupMsg(groupId, "请输入正确的转账数量", false);
        }

        // 继续执行下一个插件
        return MESSAGE_IGNORE;
    }

    @Transactional
    public boolean transfer(@NotNull Bot cq, @NotNull OnebotEvent.GroupMessageEvent event, String msg, long groupId, long userId) {
        if (msg.startsWith("转账") || msg.startsWith("转账@")) {
            ExplorePocket from = explorePocketService.findByQqId(userId, event.getSender().getNickname());
            String atQQID = "";
            String count = "0";
            for (OnebotBase.Message message : event.getMessageList()) {
                if ("at".equals(message.getType())) {
                    atQQID = message.getDataMap().get("qq");
                }
                if (!"at".equals(message.getType()) && !message.getDataMap().get("text").equals("转账")) {
                    count = message.getDataMap().get("text").split("，")[1].replaceAll(" ", "");
                }
            }
            if (from.getStardust() >= Integer.valueOf(count)) {
                //判断是否有转账账号
                if (atQQID.equals("")) {
                    from.setStardust(from.getStardust() - Integer.valueOf(count));
                    explorePocketService.changeStars(from);
                    cq.sendGroupMsg(groupId, "格式错误，那这些星屑惜风就用来给艾露买好吃的了~", false);
                } else {
                    //判断8小时内有没有转账
                    if (transfer.get(userId) == null || transfer.get(userId).plusHours(8).isBefore(LocalDateTime.now())) {
                        //判断转账是不是超过500
                        if (Integer.valueOf(count) > 500) {
                            cq.sendGroupMsg(groupId, "单次转账不能超过500哦，记得多余的星屑用来送给有需要的人吧！", false);
                        } else {
                            from.setStardust(from.getStardust() - Integer.valueOf(count));
                            ExplorePocket to = explorePocketService.findByQqId(Long.valueOf(atQQID), "");
                            to.setStardust(to.getStardust() + Integer.valueOf(count));
                            explorePocketService.changeStars(from);
                            explorePocketService.changeStars(to);
                            transfer.put(userId, LocalDateTime.now());
                            cq.sendGroupMsg(groupId, "您已转账" + count + "给" + to.getNickName() + "（" + to.getQqId() + "）", false);
                        }
                    } else {
                        cq.sendGroupMsg(groupId, "请不要频繁转账哦，上次转账时间为" + transfer.get(userId).toString().replaceAll("T", " ") + "，请至少间隔8小时！", false);
                    }
                }
            } else {
                cq.sendGroupMsg(groupId, "您的星屑不够这么多啦~", false);
            }
            return true;
        }
        return false;
    }

    @Transactional
    public boolean accelerate(@NotNull Bot cq, String msg, long groupId, long userId) {
        if (msg.equals("随机加速")) {
            FarmUserInfo farmUserInfo = farmUserInfoService.findByQqId(userId);
            if (farmUserInfo.getQuickenCount() > 0) {
                //寻找所有该编号下程序
                List<PlantedField> plantedFields = plantedFieldService.findByQqId(userId);
                List<PlantedField> newPlante = new ArrayList<>();
                plantedFields.stream().forEach(item -> {
                    //判断是否成熟
                    if (DateTimeUtil.getTimeSecondDifference(item.getPlantingTime()) < (AssemblyCache.seedSpeciesMap.get(item.getSerial()).getDuration() * 3600)) {
                        newPlante.add(item);
                    }
                });
                Random r = new Random();
                int result = r.nextInt(newPlante.size());
                PlantedField field = newPlante.get(result);
                field.setPlantingTime(Date.from(LocalDateTime.now().minusHours(AssemblyCache.seedSpeciesMap.get(field.getSerial()).getDuration()).atZone(ZoneId.systemDefault()).toInstant()));
                plantedFieldService.changeField(field);
                farmUserInfo.setQuickenCount(farmUserInfo.getQuickenCount() - 1);
                farmUserInfoService.changeUserInfo(farmUserInfo);
                cq.sendGroupMsg(groupId, "您的" + AssemblyCache.seedSpeciesMap.get(field.getSerial()).getName() + "已经可以收获了，快去看看吧！", false);
            } else {
                cq.sendGroupMsg(groupId, "您的加速卡不足，请等待管理员发放吧！", false);
            }
            return true;
        }
        return false;
    }

    @Transactional
    public boolean stealFruit(@NotNull Bot cq, @NotNull OnebotEvent.GroupMessageEvent event, String msg, long groupId, long userId) {
        if (msg.startsWith("随机偷菜")) {
            List<FarmUserInfo> farmUserInfos = farmUserInfoService.list();
            FarmUserInfo farmUserInfo = farmUserInfoService.findByQqId(userId);
            StringBuilder builder = new StringBuilder();
            if (farmUserInfo.getStealCount() > 0) {
                boolean attempt = farmUserInfos.stream().anyMatch(item -> {
                    if (item.getProtectionTime().isBefore(LocalDateTime.now().minusHours(8)) && item.getQqId() != userId) {
                        List<PlantedField> fields = plantedFieldService.findByQqId(item.getQqId());
                        AtomicReference<PlantedField> plantedField = new AtomicReference<>(new PlantedField());
                        boolean have = fields.stream().anyMatch(field -> {
                            if (DateTimeUtil.getTimeSecondDifference(field.getPlantingTime()) >= (AssemblyCache.seedSpeciesMap.get(field.getSerial()).getDuration() * 3600)) {
                                plantedField.set(field);
                                return true;
                            }
                            return false;
                        });
                        if (have) {
                            Random random = new Random();
                            int result = random.nextInt(100) + 1;
                            farmUserInfo.setStealCount(farmUserInfo.getStealCount() - 1);
                            farmUserInfoService.changeUserInfo(farmUserInfo);
                            if (result < 15) {
                                int stardust = AssemblyCache.seedSpeciesMap.get(plantedField.get().getSerial()).getMaxSell() / 5;
                                ExplorePocket explorePocket = explorePocketService.findByQqId(userId, event.getSender().getNickname());
                                explorePocket.setStardust(explorePocket.getStardust() + stardust);
                                builder.append("您成功偷走了（" + item.getQqId() + "）的" + AssemblyCache.seedSpeciesMap.get(plantedField.get().getSerial()).getName() + "，偷偷卖掉后获得了" + stardust + "星屑！");
                            } else if (result < 90) {
                                builder.append("偷菜失败~下次再试吧！");
                            } else {
                                Random random1 = new Random();
                                int result1 = random1.nextInt(1000) + 1;
                                ExplorePocket explorePocket = explorePocketService.findByQqId(userId, event.getSender().getNickname());
                                explorePocket.setStardust(explorePocket.getStardust() - (result1));
                                builder.append("您偷菜的时候被惜风的小艾露发现了~小艾露偷偷的从你的口袋里拿出了" + result1 + "星屑！");
                            }
                            return true;
                        }
                    }
                    return false;
                });
                if (!attempt) {
                    cq.sendGroupMsg(groupId, Msg.builder().at(userId).text("暂时没有成熟的蔬菜可以偷，请稍后再来吧！"), false);
                } else {
                    cq.sendGroupMsg(groupId, Msg.builder().at(userId).text(builder.toString()), false);
                }
            } else {
                cq.sendGroupMsg(groupId, "您今天已经没有偷菜次数了！", false);
            }
            return true;
        }
        return false;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean buyFarm(@NotNull Bot cq, @NotNull OnebotEvent.GroupMessageEvent event, String msg, long groupId, long userId) {
        if (msg.startsWith("购买田地")) {
            //查询个人背包
            ExplorePocket explorePocket = explorePocketService.findByQqId(userId, event.getSender().getNickname());
            //查询田地
            FarmUserInfo farmUserInfo = farmUserInfoService.findByQqId(userId);
            //判断能否购买
            if (farmUserInfo.getFieldCount() < 6) {
                //判断星币是否够
                if (explorePocket.getStars() >= farmUserInfo.getFieldCount()) {
                    explorePocket.setStars(explorePocket.getStars() - farmUserInfo.getFieldCount());
                    explorePocketService.changeStars(explorePocket);
                    farmUserInfo.setFieldCount(farmUserInfo.getFieldCount() + 1);
                    farmUserInfoService.changeUserInfo(farmUserInfo);
                    cq.sendGroupMsg(groupId, "惜风已为您开辟一块新的田地，快去种点什么吧！", true);
                } else {
                    cq.sendGroupMsg(groupId, "您的星币不足购买新的田地了哦！攒" + farmUserInfo.getFieldCount() + "星币再来吧。", true);
                }
            } else {
                cq.sendGroupMsg(groupId, "您已经拥有了6块田地...不要贪心不足了~", true);
            }
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean restoreFarm(@NotNull Bot cq, @NotNull OnebotEvent.GroupMessageEvent event, String msg, long groupId, long userId) {
        if (msg.startsWith("修复田地")) {
            int count = 0;
            String newMsg = msg.replaceAll("修复田地", "");
            ExplorePocket explorePocket = explorePocketService.findByQqId(userId, event.getSender().getNickname());
            FarmUserInfo farmUserInfo = farmUserInfoService.findByQqId(userId);
            if ("".equals(newMsg)) {
                count = 1;
            } else {
                count = Integer.valueOf(newMsg);
            }
            if (farmUserInfo.getDisasterCount() >= count) {
                if (explorePocket.getStardust() < 1000 * count) {
                    explorePocket.setStardust(explorePocket.getStardust() - (1000 * count));
                    explorePocketService.changeStars(explorePocket);
                    farmUserInfo.setDisasterCount(farmUserInfo.getDisasterCount() - count);
                    farmUserInfoService.changeUserInfo(farmUserInfo);
                    cq.sendGroupMsg(groupId, "惜风已经对您的土地使用了恢复术，已帮您恢复" + count + "块田地。", true);
                } else {
                    cq.sendGroupMsg(groupId, "您的星屑不足" + (1000 * count) + "，无法修复！", true);
                }
            } else {
                cq.sendGroupMsg(groupId, "您没有那么多田地需要修复哦", true);
            }
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean starrySkyExploration(@NotNull Bot cq, @NotNull OnebotEvent.GroupMessageEvent event, String msg, long groupId, long userId, String nickname) {
        if (msg.equals("星空探索")) {
            //探索结果
            int skillResult = 0;
            StringBuilder builder = new StringBuilder();
            Random random = new Random();
            skillResult = random.nextInt(100) + 1;
            //查看背包
            ExplorePocket explorePocket = explorePocketService.findByQqId(userId, event.getSender().getNickname());
            int determination = 50;
            if (explorePocket.getStarFragment() * 100 + explorePocket.getStardust() < 500 && explorePocket.getStars() == 0) {
                determination = 80;
            } else if ((explorePocket.getStarFragment() * 100 + explorePocket.getStardust()) < 1000) {
                determination = 70;
            }
            builder.append(nickname + "穿过时空门来到了无尽星空开始探索...\n—————————\n" + nickname + "进行探索检定：1D100=" + skillResult + "/" + determination);
            if (expMap.get(userId) != null) {
                long time = DateTimeUtil.getTimeMinDifference(expMap.get(userId));
                if (time < 30) {
                    cq.sendGroupMsg(groupId, "再多休息一下，请" + (30 - time) + "分钟后再来吧owo", false);
                    return true;
                }
            }
            expMap.put(userId, new Date());

            //判断结果
            if (skillResult == 1) {
                explorePocket.setStarFragment(explorePocket.getStarFragment() + 1);
                builder.append("极限大成功！\n你找到了前来者留下来的寻宝图，获得了星碎*1");
            } else if (skillResult <= 5) {
                explorePocket.setStardust(explorePocket.getStardust() + 50);
                builder.append("大成功！\n你在一颗不知名的星球上挖出了宝箱，获得了星屑+50");
            } else if (skillResult <= determination / 5) {
                explorePocket.setStardust(explorePocket.getStardust() + 20);
                builder.append("极难成功！\n你找到一块奇特的宝石，卖掉它换来了星屑+20");
            } else if (skillResult <= determination / 2) {
                explorePocket.setStardust(explorePocket.getStardust() + 10);
                builder.append("困难成功！\n你找到一张古老的轴卷，卖掉它换来了星屑+10");
            } else if (skillResult <= determination) {
                explorePocket.setStardust(explorePocket.getStardust() + 5);
                builder.append("成功\n你找到一块破旧的怀表，卖掉它换来了星屑+5");
            } else if (skillResult <= 95) {
                builder.append("失败...\n你没发现什么有价值的东西，是时候回家好好休息休息了。");
            } else {
                builder.append("大失败！\n你在星空中探索的时候遇到了黑洞！");
                if (skillResult == 100) {
                    explorePocket.setStardust(0);
                    builder.append("\n你企图逃脱它...但是还是失败了！你醒来后发现身上的星屑全都消失了！");
                } else {
                    if (explorePocket.getStardust() >= 50) {
                        explorePocket.setStardust(explorePocket.getStardust() - 50);
                        builder.append("\n你奋力逃脱了黑洞...但还是失去了50星屑");
                    } else {
                        builder.append("\n在惜风的帮助下,你逃离了这个黑洞...");
                    }
                }
            }
            cq.sendGroupMsg(groupId, builder.toString(), false);
            explorePocket.setNickName(event.getSender().getNickname());
            explorePocketService.changeStars(explorePocket);
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean selectBackpack(@NotNull Bot cq, @NotNull OnebotEvent.GroupMessageEvent event, String msg, long groupId, long userId) {
        if (msg.equals("查看背包")) {
            //查看背包
            ExplorePocket explorePocket = explorePocketService.findByQqId(userId, event.getSender().getNickname());
            StringBuilder builder = new StringBuilder();
            if (explorePocket.getStars() > 0) {
                builder.append("耀眼的星光洋溢了出来，您目前拥有：\n" + explorePocket.getStars() + "星币 " + explorePocket.getStarFragment() + "星碎 " + explorePocket.getStardust() + "星屑");
            } else if (explorePocket.getStarFragment() > 0) {
                builder.append("飘扬的星光了漫出来，您目前拥有：\n" + explorePocket.getStars() + "星币 " + explorePocket.getStarFragment() + "星碎 " + explorePocket.getStardust() + "星屑");
            } else if (explorePocket.getStardust() > 0) {
                builder.append("零散的星光从背包中散出，您目前拥有：\n" + explorePocket.getStars() + "星币 " + explorePocket.getStarFragment() + "星碎 " + explorePocket.getStardust() + "星屑");
            } else {
                builder.append("您的背包空空如也...");
            }
            cq.sendGroupMsg(groupId, Msg.builder().at(userId).text(builder.toString()), false);
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean exchangeStarFragment(@NotNull Bot cq, @NotNull OnebotEvent.GroupMessageEvent event, String msg, long groupId, long userId, String nickname) {
        if (msg.startsWith("星碎兑换")) {
            String countString = msg.replaceAll("星碎兑换", "");
            if (countString.equals("")) {
                countString = "1";
            }
            int count = Integer.valueOf(countString);
            //查看背包
            ExplorePocket explorePocket = explorePocketService.findByQqId(userId, event.getSender().getNickname());

            if (explorePocket.getStardust() < count * 100) {
                cq.sendGroupMsg(groupId, nickname + "您的星屑不够哦！", false);
            } else {
                explorePocket.setStardust(explorePocket.getStardust() - (count * 100));
                explorePocket.setStarFragment(explorePocket.getStarFragment() + count);
                explorePocket.setNickName(event.getSender().getNickname());
                explorePocketService.changeStars(explorePocket);
                cq.sendGroupMsg(groupId, Msg.builder().at(userId).text("已为您兑换" + count + "枚星碎"), false);
            }
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean selectRanking(@NotNull Bot cq, String msg, long groupId) {
        if (msg.indexOf("排行") != -1) {
            if (msg.equals("星空排行")) {
                StringBuilder builder = new StringBuilder();
                builder.append("无尽星空排行榜：");
                AtomicInteger rankList = new AtomicInteger(1);
                explorePocketService.selectAllRanking().stream().forEach(item -> {
                    builder.append("\n" + (rankList.getAndIncrement()) + "、" + item.getNickName() + "（" + item.getQqId() + "）：\n" + item.getStarFragment() + "星碎" + item.getStardust() + "星屑");
                });
                cq.sendGroupMsg(groupId, builder.toString(), false);
            }

            if (msg.equals("星屑排行")) {
                StringBuilder builder = new StringBuilder();
                builder.append("星屑排行榜：");
                AtomicInteger rankList = new AtomicInteger(1);
                explorePocketService.list(Wrappers.<ExplorePocket>lambdaQuery().orderByDesc(ExplorePocket::getStardust).last(" limit 0, 10")).stream().forEach(item -> {
                    builder.append("\n" + (rankList.getAndIncrement()) + "、" + item.getNickName() + "（" + item.getQqId() + "）：\n" + item.getStardust() + "星屑");
                });
                cq.sendGroupMsg(groupId, builder.toString(), false);
            }

            if (msg.equals("星币排行")) {
                StringBuilder builder = new StringBuilder();
                builder.append("星币排行榜：");
                AtomicInteger rankList = new AtomicInteger(1);
                explorePocketService.list(Wrappers.<ExplorePocket>lambdaQuery().orderByDesc(ExplorePocket::getStars).last(" limit 0, 10")).stream().forEach(item -> {
                    builder.append("\n" + (rankList.getAndIncrement()) + "、" + item.getNickName() + "（" + item.getQqId() + "）：\n" + item.getStars() + "星币");
                });
                cq.sendGroupMsg(groupId, builder.toString(), false);
            }

            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean luckLottery(@NotNull Bot cq, @NotNull OnebotEvent.GroupMessageEvent event, String msg, long groupId, long userId) {
        if (msg.equals("星空转轮")) {
            ExplorePocket explorePocket = explorePocketService.findByQqId(userId, event.getSender().getNickname());
            if (explorePocket.getStarFragment() >= 10) {
                StringBuilder builder = new StringBuilder();
                builder.append("已扣除10星碎！开始星空转轮！\n[ ");
                //扣除10星碎
                explorePocket.setStarFragment(explorePocket.getStarFragment() - 10);
                explorePocket.setNickName(event.getSender().getNickname());
                Random random = new Random();
                List<Integer> results = new ArrayList<>();

                //获取五次抽奖计数
                for (int i = 0; i < 5; i++) {
                    int result = random.nextInt(100) + 1;
                    results.add(result);
                    builder.append(result + " ");
                }
                AtomicInteger rankList = new AtomicInteger(1);
                AtomicInteger exchange = new AtomicInteger();
                results.stream().anyMatch(item -> {
                    //是否是第一次
                    if (rankList.getAndIncrement() == 1) {
                        //第一次是否极限大成功
                        if (item == 1) {
                            exchange.addAndGet(100);
                            return true;
                        } else {
                            exchange.addAndGet(20);
                        }
                    } else {
                        if (item < 20) {
                            exchange.addAndGet(20);
                        } else if (item < 30) {
                            exchange.addAndGet(20);
                        } else if (item < 40) {
                            exchange.addAndGet(15);
                        } else if (item < 60) {
                            exchange.addAndGet(10);
                        } else {
                            exchange.addAndGet(5);
                        }
                    }
                    return false;
                });
                //判断抽奖结果
                if (exchange.intValue() == 100) {
                    explorePocket.setStars(explorePocket.getStars() + 10);
                    builder.append("]\n首针落中！恭喜您获得10星币大奖！");
                } else if (exchange.intValue() > 80) {
                    explorePocket.setStars(explorePocket.getStars() + 1);
                    builder.append("]\n总分80！恭喜获得1星币！");
                } else if (exchange.intValue() > 60) {
                    List<SeedSpecies> seedSpeciesList = seedSpeciesService.list(Wrappers.<SeedSpecies>lambdaQuery().eq(SeedSpecies::getType, 2));
                    Random random1 = new Random();
                    int seedNum = random1.nextInt(seedSpeciesList.size());
                    SeedBag seedBag = new SeedBag(userId, seedSpeciesList.get(seedNum).getId(), 2, 1);
                    seedBagService.changeSeed(seedBag);
                    builder.append("]\n总分60！恭喜获得随机种子：" + seedSpeciesList.get(seedNum).getName() + "！");
                } else if (exchange.intValue() > 40) {
                    List<SeedSpecies> seedSpeciesList = seedSpeciesService.list(Wrappers.<SeedSpecies>lambdaQuery().eq(SeedSpecies::getType, 1));
                    Random random1 = new Random();
                    int seedNum = random1.nextInt(seedSpeciesList.size());
                    SeedBag seedBag = new SeedBag(userId, seedSpeciesList.get(seedNum).getId(), 1, 1);
                    seedBag.setSpeciesId(seedSpeciesList.get(seedNum).getId());
                    seedBagService.changeSeed(seedBag);
                    builder.append("]\n总分40！恭喜获得随机种子：" + seedSpeciesList.get(seedNum).getName() + "！");
                } else {
                    builder.append("]\n全针落空！谢谢您本次参与！");
                }
                //修改星碎变化
                explorePocketService.changeStars(explorePocket);
                cq.sendGroupMsg(groupId, Msg.builder().at(userId).text(builder.toString()), false);
            } else {
                cq.sendGroupMsg(groupId, Msg.builder().at(userId).text("您的星碎还不够哦！"), false);
            }
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean selectSeedBag(@NotNull Bot cq, String msg, long groupId, long userId) {
        if (msg.equals("种子口袋")) {
            List<SeedBag> list = seedBagService.findByQqId(userId);
            StringBuilder builder = new StringBuilder("");
            if (list != null) {
                if (list.size() > 0) {
                    AtomicInteger count = new AtomicInteger();
                    builder.append("您的种子口袋中有：");
                    list.stream().forEach(item -> {
                        if (item.getCount() != 0) {
                            builder.append("\n" + AssemblyCache.seedSpeciesMap.get(item.getSpeciesId()).getName() + "种子：" + item.getCount() + "颗");
                            count.getAndIncrement();
                        }
                    });
                    if (count.intValue() == 0) {
                        builder.delete(0, builder.length());
                        builder.append("只是一只空空的口袋，什么都没有...");
                    }
                } else {
                    builder.append("只是一只空空的口袋，什么都没有...");
                }
            } else {
                builder.append("只是一只空空的口袋，什么都没有...");
            }
            cq.sendGroupMsg(groupId, Msg.builder().at(userId).text(builder.toString()), false);
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean selectWarehouse(@NotNull Bot cq, String msg, long groupId, long userId) {
        if (msg.equals("作物仓库")) {
            List<Fruit> list = fruitService.findByQqId(userId);
            StringBuilder builder = new StringBuilder("");
            if (list != null) {
                if (list.size() > 0) {
                    AtomicInteger count = new AtomicInteger();
                    builder.append("您的作物仓库中有：");
                    list.stream().forEach(item -> {
                        if (item.getCount() != 0) {
                            builder.append("\n" + AssemblyCache.seedSpeciesMap.get(item.getSpeciesId()).getName() + "：" + item.getCount() + AssemblyCache.seedSpeciesMap.get(item.getSpeciesId()).getUnit());
                            count.getAndIncrement();
                        }
                    });
                    if (count.intValue() == 0) {
                        builder.delete(0, builder.length());
                        builder.append("仓库空空如也，还什么都没有...");
                    }
                } else {
                    builder.append("仓库空空如也，还什么都没有...");
                }
            } else {
                builder.append("仓库空空如也，还什么都没有...");
            }
            cq.sendGroupMsg(groupId, Msg.builder().at(userId).text(builder.toString()), false);
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean selectField(@NotNull Bot cq, String msg, long groupId, long userId) {
        if (msg.equals("我的田地")) {
            List<PlantedField> list = plantedFieldService.findByQqId(userId);
            StringBuilder builder = new StringBuilder("");
            if (list != null) {
                if (list.size() > 0) {
                    builder.append("种植的作物有：");
                    list.stream().forEach(item -> {
                        builder.append("\n" + AssemblyCache.seedSpeciesMap.get(item.getSerial()).getName() + " 种植时间：" + DateTimeUtil.getTimeSecondDifference(item.getPlantingTime()));
                        builder.append("\n收获时间：" + AssemblyCache.seedSpeciesMap.get(item.getSerial()).getDuration() * 60 * 60 + "");
                        builder.append("\n已收获次数：" + item.getTimes() + "\n");
                    });
                } else {
                    builder.append("您的田地里一片荒凉，快去种点什么吧！");
                }
            } else {
                builder.append("您的田地里一片荒凉，快去种点什么吧！");
            }
            if (farmUserInfoService.findByQqId(userId).getDisasterCount() > 0) {
                builder.append("\n请注意！您有" + farmUserInfoService.findByQqId(userId).getDisasterCount() + "块土地已遭受灾害无法使用！\n请尽快缴纳修复费用！");
            }
            cq.sendGroupMsg(groupId, Msg.builder().at(userId).text(builder.toString()), false);
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean agricultureStore(@NotNull Bot cq, String msg, long groupId) {
        if (msg.equals("农业商店")) {
            List<SeedSpecies> list = seedSpeciesService.list();
            StringBuilder builder = new StringBuilder("");
            if (list != null) {
                if (list.size() > 0) {
                    builder.append("欢迎光临惜风的农业小店：\n");
                    list.stream().forEach(item -> {
                        builder.append("\n" + item.getName() + "：\n");
                        builder.append(item.getPrice());
                        if (item.getType() == 1) {
                            builder.append("星屑");
                        } else if (item.getType() == 2) {
                            builder.append("星碎");
                        } else {
                            builder.append("星币");
                        }
                        builder.append("，可收获 " + item.getTimes() + "次\n种植时间" + item.getDuration() + "h\n预计收益" + item.getMinSell() + "~" + item.getMaxSell() + "星屑\n");
                    });
                } else {
                    builder.append("您的田地里一片荒凉，快去种点什么吧！");
                }
            } else {
                builder.append("您的田地里一片荒凉，快去种点什么吧！");
            }
            cq.sendGroupMsg(groupId, builder.toString(), false);
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean buySeeds(@NotNull Bot cq, @NotNull OnebotEvent.GroupMessageEvent event, String msg, long groupId, long userId) {
        //判断是否是购买
        if (msg.startsWith("购买")) {
            //获取种子种类
            List<SeedSpecies> list = seedSpeciesService.list();
            //获取背包
            ExplorePocket explorePocket = AssemblyCache.explorePockets.get(userId);
            StringBuilder builder = new StringBuilder();
            //获取购买类型
            String finalMsg = msg.replaceAll("购买", "");
            //设置种子实体
            SeedBag seedBag = new SeedBag();
            seedBag.setCount(1);
            seedBag.setQqId(userId);
            AtomicInteger count = new AtomicInteger();
            list.stream().forEach(item -> {
                if (item.getName().equals(finalMsg)) {
                    explorePocket.setNickName(event.getSender().getNickname());
                    if (item.getType() == 1) {
                        //判断星屑是否够用
                        if (explorePocket.getStardust() >= item.getPrice()) {
                            explorePocket.setStardust(explorePocket.getStardust() - item.getPrice());
                            seedBag.setSpeciesId(item.getId());
                            seedBag.setType(1);
                            seedBagService.changeSeed(seedBag);
                            explorePocketService.changeStars(explorePocket);
                            builder.append("您已购买" + "1颗" + item.getName() + "种子");
                        } else {
                            builder.append("星屑不够，请下次再来吧！");
                        }
                    } else if (item.getType() == 2) {
                        //判断星碎是否够用
                        if (explorePocket.getStarFragment() >= item.getPrice()) {
                            explorePocket.setStarFragment(explorePocket.getStarFragment() - item.getPrice());
                            seedBag.setSpeciesId(item.getId());
                            seedBag.setType(2);
                            seedBagService.changeSeed(seedBag);
                            explorePocketService.changeStars(explorePocket);
                            builder.append("您已购买" + "1颗" + item.getName() + "种子");
                        } else {
                            builder.append("星碎不够，请下次再来吧！");
                        }
                    } else {
                        //判断星币是否够用
                        if (explorePocket.getStars() >= item.getPrice()) {
                            explorePocket.setStars(explorePocket.getStars() - item.getPrice());
                            seedBag.setSpeciesId(item.getId());
                            seedBag.setType(3);
                            seedBagService.changeSeed(seedBag);
                            explorePocketService.changeStars(explorePocket);
                            builder.append("您已购买" + "1颗" + item.getName() + "种子");
                        } else {
                            builder.append("星币不够，请下次再来吧！");
                        }
                    }
                    count.getAndIncrement();
                }
            });
            if (count.intValue() == 0) {
                builder.append("请正确输入购买指令");
            }
            cq.sendGroupMsg(groupId, Msg.builder().at(userId).text(builder.toString()), false);
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean plantingSeeds(@NotNull Bot cq, String msg, long groupId, long userId) {
        //判断是否是种植
        if (msg.startsWith("种植")) {
            StringBuilder builder = new StringBuilder();
            //获取种植种类
            String finalMsg = msg.replaceAll("种植", "");
            //获取种子类型
            List<SeedSpecies> seedSpecies = seedSpeciesService.list();
            //获取种子包
            List<SeedBag> seedBags = seedBagService.findByQqId(userId);
            AtomicInteger correct = new AtomicInteger();
            seedSpecies.stream().forEach(item -> {
                //寻找种植类型
                if (item.getName().equals(finalMsg)) {
                    AtomicInteger count = new AtomicInteger();
                    AtomicBoolean fieldBool = new AtomicBoolean(true);
                    seedBags.stream().forEach(seedBag -> {
                        //判断是否有该类型种子,或者数量足够
                        if (seedBag.getSpeciesId().equals(item.getId()) && seedBag.getCount() > 0) {
                            //判断是否有空闲田地
                            if (plantedFieldService.findByQqId(userId).size() < (farmUserInfoService.findByQqId(userId).getFieldCount() - farmUserInfoService.findByQqId(userId).getDisasterCount())) {
                                seedBag.setCount(-1);
                                seedBagService.changeSeed(seedBag);
                                PlantedField field = new PlantedField(userId, item.getId(), item.getType(), 0, new Date(), 0, 0);
                                plantedFieldService.changeField(field);
                                count.getAndIncrement();
                                fieldBool.set(true);
                            } else {
                                fieldBool.set(false);
                            }
                        }
                    });
                    //判断是否有足够种子
                    if (count.intValue() == 0) {
                        if (fieldBool.get()) {
                            builder.append("您还未有该类型种子哦！请先购买吧！");
                        } else {
                            builder.append("您的田地不足！");
                        }
                    } else {
                        builder.append("您已成功种植");
                    }
                    correct.getAndIncrement();
                }
            });
            if (correct.intValue() == 0) {
                builder.append("请正确输入种植指令");
            }
            cq.sendGroupMsg(groupId, Msg.builder().at(userId).text(builder.toString()), false);
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean rewardSeeds(@NotNull Bot cq, String msg, long groupId, long userId) {
        //判断是否是收获
        if (msg.startsWith("收获")) {
            StringBuilder builder = new StringBuilder();
            StringBuilder builder2 = new StringBuilder();
            //获取收获种类
            String finalMsg = msg.replaceAll("收获", "");
            List<SeedSpecies> seedSpecies = seedSpeciesService.list();
            AtomicInteger speciesId = new AtomicInteger();
            //获取该果实编号
            seedSpecies.stream().forEach(item -> {
                if (item.getName().equals(finalMsg)) {
                    speciesId.set(item.getId());
                }
            });
            //寻找所有该编号下程序
            List<PlantedField> plantedFields = plantedFieldService.list(Wrappers.<PlantedField>lambdaQuery().eq(PlantedField::getSerial, speciesId.intValue()).eq(PlantedField::getQqId, userId));
            //收获列表
            List<Fruit> fruits = new ArrayList<>();
            //灾害列表
            StringBuilder failCount = new StringBuilder();
            //大成功列表
            StringBuilder successCount = new StringBuilder();

            plantedFields.stream().forEach(item -> {
                //判断是否成熟
                if (DateTimeUtil.getTimeSecondDifference(item.getPlantingTime()) >= (AssemblyCache.seedSpeciesMap.get(item.getSerial()).getDuration() * 3600)) {
                    //是否是受灾种类
                    if (AssemblyCache.seedSpeciesMap.get(item.getSerial()).getType() > 1) {
                        Random random = new Random();
                        int success = random.nextInt(100) + 1;
                        //判断是否受灾,是否大成功
                        if (success > 85) {
                            failCount.append(" " + AssemblyCache.seedSpeciesMap.get(item.getSerial()).getName());
                        } else if (success <= 5) {
                            successCount.append(" " + AssemblyCache.seedSpeciesMap.get(item.getSerial()).getName());
                            Fruit fruit = new Fruit(userId, item.getSerial(), 2);
                            fruits.add(fruit);
                        } else {
                            Fruit fruit = new Fruit(userId, item.getSerial(), 1);
                            fruits.add(fruit);
                        }
                    } else {
                        Random random = new Random();
                        int success = random.nextInt(100) + 1;
                        if (success <= 5) {
                            successCount.append(" " + AssemblyCache.seedSpeciesMap.get(item.getSerial()).getName());
                            Fruit fruit = new Fruit(userId, item.getSerial(), 2);
                            fruits.add(fruit);
                        } else {
                            Fruit fruit = new Fruit(userId, item.getSerial(), 1);
                            fruits.add(fruit);
                        }
                    }
                    if (AssemblyCache.seedSpeciesMap.get(item.getSerial()).getTimes() > (item.getTimes() + 1)) {
                        plantedFieldService.changeField(item);
                    } else {
                        Random random = new Random();
                        if (random.nextInt(100) + 1 > 95) {
                            FarmUserInfo userInfo = farmUserInfoService.findByQqId(userId);
                            userInfo.setDisasterCount(userInfo.getDisasterCount() + 1);
                            farmUserInfoService.changeUserInfo(userInfo);
                            builder2.append("警告：此田地因为遭受灾害受到了损毁，请您及时修复！");
                        }
                        plantedFieldService.deleteField(item);
                    }
                }
            });
            //判断收获结果
            if (fruits.size() > 0) {
                builder.append("惜风已帮您收获作物，其中：");
                if (failCount.length() > 0) {
                    builder.append("\n您的[" + failCount.toString() + " ]" + getDisasterJson().getName());
                }
                if (successCount.length() > 0) {
                    builder.append("\n您的[" + successCount.toString() + " ]成长的非常优秀！获得了额外的作物！");
                }
                fruits.stream().forEach(ItemEvent -> {
                    builder.append("\n" + AssemblyCache.seedSpeciesMap.get(ItemEvent.getSpeciesId()).getName() + "：" + ItemEvent.getCount() + AssemblyCache.seedSpeciesMap.get(ItemEvent.getSpeciesId()).getUnit());
                });
                //修改果实仓库
                fruitService.changeFruitList(fruits);
            } else {
                if (failCount.length() > 0) {
                    builder.append("您的[" + failCount.toString() + " ]" + getDisasterJson().getName());
                } else {
                    builder.append("还没有成熟的" + finalMsg + "作物哦！");
                }
            }
            cq.sendGroupMsg(groupId, Msg.builder().at(userId).text(builder.toString()).text(builder2.toString()), false);
            return true;
        }
        return false;
    }

    //出售种子
    @Transactional(rollbackFor = Exception.class)
    public boolean sellSeeds(@NotNull Bot cq, @NotNull OnebotEvent.GroupMessageEvent event, String msg, long groupId, long userId) {
        if (msg.startsWith("出售")) {
            //获取背包
            ExplorePocket explorePocket = explorePocketService.findByQqId(userId, event.getSender().getNickname());
            //获取果实仓库
            List<Fruit> list = fruitService.findByQqId(userId);
            //获取出售类型
            String fianlMsg = msg.replaceAll("出售", "");
            StringBuilder builder = new StringBuilder("");
            AtomicInteger count = new AtomicInteger();
            if (list != null) {
                if (list.size() > 0) {
                    list.stream().forEach(item -> {
                        //判断是否是出售品
                        if (fianlMsg.startsWith(AssemblyCache.seedSpeciesMap.get(item.getSpeciesId()).getName())) {
                            String sell = fianlMsg.replaceAll(AssemblyCache.seedSpeciesMap.get(item.getSpeciesId()).getName(), "");
                            int sellCount = 0;
                            if ("".equals(sell)) {
                                sellCount = 1;
                            } else {
                                sellCount = Integer.valueOf(sell);
                            }
                            if (sellCount > item.getCount()) {
                                builder.append("无法出售！您的仓库没有足够的该种作物哦！");
                            } else {
                                Fruit fruit = new Fruit(userId, item.getSpeciesId(), -sellCount);
                                fruitService.changeFruit(fruit);
                                //判断最终出售价格
                                Random random = new Random();
                                int minPrice = (AssemblyCache.seedSpeciesMap.get(item.getSpeciesId()).getMinSell() * sellCount);
                                int maxPrice = (AssemblyCache.seedSpeciesMap.get(item.getSpeciesId()).getMaxSell() * sellCount);
                                int priceCount = minPrice + random.nextInt(maxPrice - minPrice);
                                explorePocket.setStardust(explorePocket.getStardust() + priceCount);
                                //修改过货币数
                                explorePocketService.changeStars(explorePocket);
                                builder.append("您成功出售了：\n" + sellCount + AssemblyCache.seedSpeciesMap.get(item.getSpeciesId()).getUnit() + "\n收益：" + priceCount + "星屑");
                            }
                            count.getAndIncrement();
                        }
                    });
                } else {
                    builder.append("无法出售！您的仓库里没有作物哦...");
                }
            } else {
                builder.append("无法出售！您的仓库里没有作物哦...");
            }
            if (count.intValue() == 0) {
                builder.append("无法出售！您的仓库里没有该作物...");
            }
            cq.sendGroupMsg(groupId, Msg.builder().at(userId).text(builder.toString()), false);
            return true;
        }
        return false;
    }

    /**
     * 读取灾害列表
     *
     * @return Disaster
     * @throws IOException
     */
    public Disaster getDisasterJson() {
        String path = "/disaster.json";
        InputStream config = getClass().getResourceAsStream(path);
        if (config == null) {
            throw new RuntimeException("读取文件失败");
        } else {
            JSONObject json = null;
            try {
                json = JSON.parseObject(config, JSONObject.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONArray array = json.getJSONArray("disaster");
            List<Disaster> disasters = array.toJavaList(Disaster.class);
            Random random = new Random();
            int num = random.nextInt(disasters.size());
            for (Disaster disaster : disasters) {
                if (disaster.getNum() == num) {
                    return disaster;
                }
            }
        }
        return null;
    }
//测试方法
//    public static void main(String[] args) {
//        int c1=0;
//        int c2=0;
//        int c3=0;
//        int c4=0;
//        int c5=0;
//        for(int j=0;j<100000000;j++){
//            Random random = new Random();
//            List<Integer> results = new ArrayList<>();
//            //获取五次抽奖计数
//            for (int i = 0; i < 5; i++) {
//                int result = random.nextInt(100) + 1;
//                results.add(result);
//            }
//            AtomicInteger rankList = new AtomicInteger(1);
//            AtomicInteger exchange = new AtomicInteger();
//            results.stream().anyMatch(item -> {
//                //是否是第一次
//                if (rankList.getAndIncrement() == 1) {
//                    //第一次是否极限大成功
//                    if (item == 1) {
//                        exchange.addAndGet(100);
//                        return true;
//                    } else {
//                        exchange.addAndGet(20);
//                    }
//                } else {
//                    if (item < 20) {
//                        exchange.addAndGet(20);
//                    } else if (item < 30) {
//                        exchange.addAndGet(20);
//                    } else if (item < 40) {
//                        exchange.addAndGet(15);
//                    } else if (item < 60) {
//                        exchange.addAndGet(10);
//                    } else {
//                        exchange.addAndGet(5);
//                    }
//                }
//                return false;
//            });
//            if (exchange.intValue() == 100) {
//                c1++;
//               } else if (exchange.intValue() > 80) {
//                c2++;
//                } else if (exchange.intValue() > 60) {
//                c3++;
//                } else if (exchange.intValue() > 40) {
//                c4++;
//                } else {
//                c5++;
//            }
//        }
//        System.out.println(c1/100000000.0+" "+c2/100000000.0+" "+c3/100000000.0+" "+c4/100000000.0+" "+c5/100000000.0+" ");
//    }
}

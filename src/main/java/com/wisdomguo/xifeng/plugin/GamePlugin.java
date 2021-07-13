package com.wisdomguo.xifeng.plugin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wisdomguo.xifeng.assist.AssemblyCache;
import com.wisdomguo.xifeng.assist.BlackMap;
import com.wisdomguo.xifeng.assist.Disaster;
import com.wisdomguo.xifeng.modules.game.explorepocket.entity.ExplorePocket;
import com.wisdomguo.xifeng.modules.game.explorepocket.service.ExplorePocketService;
import com.wisdomguo.xifeng.modules.game.fruit.entity.Fruit;
import com.wisdomguo.xifeng.modules.game.fruit.service.FruitService;
import com.wisdomguo.xifeng.modules.game.plantedfield.entity.PlantedField;
import com.wisdomguo.xifeng.modules.game.plantedfield.service.PlantedFieldService;
import com.wisdomguo.xifeng.modules.game.seedbag.entity.SeedBag;
import com.wisdomguo.xifeng.modules.game.seedbag.service.SeedBagService;
import com.wisdomguo.xifeng.modules.game.seedspecies.entity.SeedSpecies;
import com.wisdomguo.xifeng.modules.game.seedspecies.service.SeedSpeciesService;
import com.wisdomguo.xifeng.modules.qqgroup.entity.QQGroup;
import com.wisdomguo.xifeng.modules.qqgroup.service.QQGroupSerivce;
import com.wisdomguo.xifeng.util.DateTimeUtil;
import lombok.SneakyThrows;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


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

    private Map<Long, Date> expMap = new HashMap<>();

    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    /**
     * 收到群消息时会调用这个方法
     *
     * @param cq    机器人对象，用于调用API，例如发送群消息 sendGroupMsg
     * @param event 事件对象，用于获取消息内容、群号、发送者QQ等
     * @return 是否继续调用下一个插件，IGNORE表示继续，BLOCK表示不继续
     */
    @SneakyThrows
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int onGroupMessage(@NotNull Bot cq, @NotNull OnebotEvent.GroupMessageEvent event) {
        // 获取 消息内容 群号 发送者QQ
        //获取消息内容
        String msg = event.getRawMessage().replaceAll("。", ".");
        //获取群号
        long groupId = event.getGroupId();
        //获取发送者QQ
        long userId = event.getUserId();
        if (BlackMap.returnBlackList(userId)) {
            return MESSAGE_BLOCK;
        }

        QQGroup qqGroup = qqGroupSerivce.selectAllByID(String.valueOf(groupId));
        if (qqGroup.getXfOpen() == 1 || qqGroup.getGameOpen() == 1) {
            return MESSAGE_IGNORE;
        }

        boolean judgment = (("owner".equals(event.getSender().getRole())
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
        String nickname = cq.getGroupMemberInfo(groupId, userId, false).getCard();
        if (nickname.equals("")) {
            nickname = cq.getGroupMemberInfo(groupId, userId, false).getNickname();
        }


        if (msg.equals("星空探索")) {

            int skillResult = 0;

            StringBuilder builder = new StringBuilder();

            Random random = new Random();
            skillResult = random.nextInt(100) + 1;
            builder.append(nickname + "穿过时空门来到了无尽星空开始探索...\n—————————\n" + nickname + "进行探索检定：1D100=" + skillResult + "/50");

            if (expMap.get(userId) != null) {
                long time = DateTimeUtil.getTimeMinDifference(expMap.get(userId));
                if (time < 30) {
                    cq.sendGroupMsg(groupId, "再多休息一下，请" + (30 - time) + "分钟后再来吧owo", false);
                    return MESSAGE_IGNORE;
                }
            }
            expMap.put(userId, new Date());

            //查看背包
            ExplorePocket explorePocket = explorePocketService.findByQqId(userId, event.getSender().getNickname());
            //判断结果
            if (skillResult == 1) {
                explorePocket.setStarFragment(explorePocket.getStarFragment() + 1);
                builder.append("极限大成功！\n你找到了前来者留下来的寻宝图，获得了星碎*1");
            } else if (skillResult <= 5) {
                explorePocket.setStardust(explorePocket.getStardust() + 50);
                builder.append("大成功！\n你在一颗不知名的星球上挖出了宝箱，获得了星屑+50");
            } else if (skillResult <= 10) {
                explorePocket.setStardust(explorePocket.getStardust() + 20);
                builder.append("极难成功！\n你找到一块奇特的宝石，卖掉它换来了星屑+20");
            } else if (skillResult <= 25) {
                explorePocket.setStardust(explorePocket.getStardust() + 10);
                builder.append("困难成功！\n你找到一张古老的轴卷，卖掉它换来了星屑+10");
            } else if (skillResult <= 50) {
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
            return MESSAGE_IGNORE;
        }

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
            return MESSAGE_IGNORE;
        }


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

            return MESSAGE_IGNORE;
        }

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
                    SeedBag seedBag = new SeedBag();
                    seedBag.setQqId(userId);
                    seedBag.setCount(1);
                    seedBag.setType(2);
                    seedBag.setSpeciesId(seedSpeciesList.get(seedNum).getId());
                    seedBagService.changeSeed(seedBag);
                    builder.append("]\n总分60！恭喜获得随机种子：" + seedSpeciesList.get(seedNum).getName() + "！");
                } else if (exchange.intValue() > 40) {
                    List<SeedSpecies> seedSpeciesList = seedSpeciesService.list(Wrappers.<SeedSpecies>lambdaQuery().eq(SeedSpecies::getType, 1));
                    Random random1 = new Random();
                    int seedNum = random1.nextInt(seedSpeciesList.size());
                    SeedBag seedBag = new SeedBag();
                    seedBag.setQqId(userId);
                    seedBag.setCount(1);
                    seedBag.setType(1);
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
            return MESSAGE_IGNORE;
        }

        if (msg.equals("种子口袋")) {
            List<SeedBag> list = seedBagService.findByQqId(userId);
            StringBuilder builder = new StringBuilder("");
            if (list != null) {
                if (list.size() > 0) {
                    AtomicInteger count = new AtomicInteger();
                    builder.append("您的种子口袋中有：");
                    list.stream().forEach(item -> {
                        if (item.getCount() != 0) {
                            builder.append("\n" + AssemblyCache.seedSpeciesMap.get(item.getSpeciesId()).getName() + "种子：" + item.getCount() + AssemblyCache.seedSpeciesMap.get(item.getSpeciesId()).getUnit());
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
            return MESSAGE_IGNORE;
        }

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
            return MESSAGE_IGNORE;
        }

        if (msg.equals("我的田地")) {
            List<PlantedField> list = plantedFieldService.findByQqId(userId);
            StringBuilder builder = new StringBuilder("");
            if (list != null) {
                if (list.size() > 0) {
                    builder.append("种植的作物有：");
                    list.stream().forEach(item -> {
                        try {
                            builder.append("\n" + AssemblyCache.seedSpeciesMap.get(item.getSerial()).getName() + " 种植时间：" + DateTimeUtil.getTimeSecondDifference(item.getPlantingTime()));
                            builder.append("\n收获时间：" + AssemblyCache.seedSpeciesMap.get(item.getSerial()).getDuration() * 60 * 60 + "");
                            builder.append("\n已收获次数：" + item.getTimes() + "\n");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    builder.append("您的田地里一片荒凉，快去种点什么吧！");
                }
            } else {
                builder.append("您的田地里一片荒凉，快去种点什么吧！");
            }
            cq.sendGroupMsg(groupId, Msg.builder().at(userId).text(builder.toString()), false);
            return MESSAGE_IGNORE;
        }

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
            return MESSAGE_IGNORE;
        }

        if (msg.startsWith("购买")) {
            List<SeedSpecies> list = seedSpeciesService.list();
            ExplorePocket explorePocket = AssemblyCache.explorePockets.get(userId);
            StringBuilder builder = new StringBuilder();
            String finalMsg = msg.replaceAll("购买", "");
            SeedBag seedBag = new SeedBag();
            seedBag.setCount(1);
            seedBag.setQqId(userId);
            AtomicInteger count= new AtomicInteger();
            list.stream().forEach(item -> {
                if (item.getName().equals(finalMsg)) {
                    explorePocket.setNickName(event.getSender().getNickname());
                    if (item.getType() == 1) {
                        if (explorePocket.getStardust() >= item.getPrice()) {
                            explorePocket.setStardust(explorePocket.getStardust() - item.getPrice());
                            seedBag.setSpeciesId(item.getId());
                            seedBag.setType(1);
                            seedBagService.changeSeed(seedBag);
                            explorePocketService.changeStars(explorePocket);
                            builder.append("您已购买" + item.getName() + "1" + item.getUnit());
                        } else {
                            builder.append("星屑不够，请下次再来吧！");
                        }
                    } else if (item.getType() == 2) {
                        if (explorePocket.getStarFragment() >= item.getPrice()) {
                            explorePocket.setStarFragment(explorePocket.getStarFragment() - item.getPrice());
                            seedBag.setSpeciesId(item.getId());
                            seedBag.setType(2);
                            seedBagService.changeSeed(seedBag);
                            explorePocketService.changeStars(explorePocket);
                            builder.append("您已购买" + item.getName() + "1" + item.getUnit());
                        } else {
                            builder.append("星碎不够，请下次再来吧！");
                        }
                    } else {
                        if (explorePocket.getStars() >= item.getPrice()) {
                            explorePocket.setStars(explorePocket.getStars() - item.getPrice());
                            seedBag.setSpeciesId(item.getId());
                            seedBag.setType(3);
                            seedBagService.changeSeed(seedBag);
                            explorePocketService.changeStars(explorePocket);
                            builder.append("您已购买" + item.getName() + "1" + item.getUnit());
                        } else {
                            builder.append("星币不够，请下次再来吧！");
                        }
                    }
                    count.getAndIncrement();
                }
            });
            if(count.intValue()==0){
                builder.append("请正确输入购买指令");
            }
            cq.sendGroupMsg(groupId, Msg.builder().at(userId).text(builder.toString()), false);
            return MESSAGE_IGNORE;
        }

        if (msg.startsWith("种植")) {
            StringBuilder builder = new StringBuilder();
            String finalMsg = msg.replaceAll("种植", "");
            List<SeedSpecies> seedSpecies = seedSpeciesService.list();
            List<SeedBag> seedBags = seedBagService.findByQqId(userId);
            AtomicInteger correct = new AtomicInteger();
            seedSpecies.stream().forEach(item -> {
                if (item.getName().equals(finalMsg)) {
                    AtomicInteger count = new AtomicInteger();
                    AtomicBoolean fieldBool = new AtomicBoolean(true);
                    seedBags.stream().forEach(seedBag -> {
                        //判断是否有该类型种子,或者数量足够
                        if (seedBag.getSpeciesId().equals(item.getId()) && seedBag.getCount() > 0) {
                            //判断是否有空闲田地
                            if (plantedFieldService.findByQqId(userId).size() < 1) {
                                seedBag.setCount(-1);
                                seedBagService.changeSeed(seedBag);
                                count.getAndIncrement();
                                PlantedField field = new PlantedField();
                                field.setStage(0);
                                field.setQqId(userId);
                                field.setDelFlag(0);
                                field.setTimes(0);
                                field.setType(item.getType());
                                field.setPlantingTime(new Date());
                                field.setSerial(item.getId());
                                plantedFieldService.changeField(field);
                                fieldBool.set(true);
                            } else {
                                fieldBool.set(false);
                            }
                        }
                    });
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
            if(correct.intValue()==0){
                builder.append("请正确输入种植指令");
            }
            cq.sendGroupMsg(groupId, Msg.builder().at(userId).text(builder.toString()), false);
            return MESSAGE_IGNORE;
        }

        if (msg.startsWith("收获")) {
            StringBuilder builder = new StringBuilder();
            String finalMsg = msg.replaceAll("收获", "");
            List<SeedSpecies> seedSpecies = seedSpeciesService.list();
            AtomicInteger speciesId = new AtomicInteger();
            seedSpecies.stream().forEach(item -> {
                if (item.getName().equals(finalMsg)) {
                    speciesId.set(item.getId());
                }
            });
            List<PlantedField> plantedFields = plantedFieldService.list(Wrappers.<PlantedField>lambdaQuery().eq(PlantedField::getSerial, speciesId.intValue()).eq(PlantedField::getQqId, userId));
            List<Fruit> fruits = new ArrayList<>();
            StringBuilder failCount = new StringBuilder();
            StringBuilder successCount = new StringBuilder();
            plantedFields.stream().forEach(item -> {
                try {
                    if (DateTimeUtil.getTimeSecondDifference(item.getPlantingTime()) >= (AssemblyCache.seedSpeciesMap.get(item.getSerial()).getDuration() * 3600)) {
                        if (AssemblyCache.seedSpeciesMap.get(item.getSerial()).getType() > 1) {
                            Random random = new Random();
                            int success = random.nextInt(100) + 1;
                            if (success > 85) {
                                failCount.append(" " + AssemblyCache.seedSpeciesMap.get(item.getSerial()).getName());
                            } else if (success <= 5) {
                                successCount.append(" " + AssemblyCache.seedSpeciesMap.get(item.getSerial()).getName());
                                Fruit fruit = new Fruit();
                                fruit.setCount(2);
                                fruit.setQqId(userId);
                                fruit.setSpeciesId(item.getSerial());
                                fruits.add(fruit);
                            } else {
                                Fruit fruit = new Fruit();
                                fruit.setCount(1);
                                fruit.setQqId(userId);
                                fruit.setSpeciesId(item.getSerial());
                                fruits.add(fruit);
                            }
                        } else {
                            Random random = new Random();
                            int success = random.nextInt(100) + 1;
                            if (success <= 5) {
                                successCount.append(" " + AssemblyCache.seedSpeciesMap.get(item.getSerial()).getName());
                                Fruit fruit = new Fruit();
                                fruit.setCount(2);
                                fruit.setQqId(userId);
                                fruit.setSpeciesId(item.getSerial());
                                fruits.add(fruit);
                            } else {
                                Fruit fruit = new Fruit();
                                fruit.setCount(1);
                                fruit.setQqId(userId);
                                fruit.setSpeciesId(item.getSerial());
                                fruits.add(fruit);
                            }
                        }
                        if (AssemblyCache.seedSpeciesMap.get(item.getSerial()).getTimes() > (item.getTimes() + 1)) {
                            plantedFieldService.changeField(item);
                        } else {
                            plantedFieldService.deleteField(item);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });

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
                fruitService.changeFruitList(fruits);
            } else {
                if (failCount.length() > 0) {
                    builder.append("您的[" + failCount.toString() + " ]" + getDisasterJson().getName());
                } else {
                    builder.append("还没有成熟的" + finalMsg + "作物哦！");
                }
            }
            cq.sendGroupMsg(groupId, Msg.builder().at(userId).text(builder.toString()), false);
            return MESSAGE_IGNORE;
        }

        if (msg.startsWith("出售")) {
            ExplorePocket explorePocket = explorePocketService.findByQqId(userId,event.getSender().getNickname());
            List<Fruit> list = fruitService.findByQqId(userId);
            String fianlMsg = msg.replaceAll("出售", "");
            StringBuilder builder = new StringBuilder("");
            AtomicInteger count = new AtomicInteger();
            if (list != null) {
                if (list.size() > 0) {
                    list.stream().forEach(item -> {
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
                                Fruit fruit = new Fruit();
                                fruit.setCount(-sellCount);
                                fruit.setQqId(userId);
                                fruit.setSpeciesId(item.getSpeciesId());
                                fruitService.changeFruit(fruit);
                                Random random=new Random();
                                int minPrice=(AssemblyCache.seedSpeciesMap.get(item.getSpeciesId()).getMinSell()*sellCount);
                                int maxPrice=(AssemblyCache.seedSpeciesMap.get(item.getSpeciesId()).getMaxSell()*sellCount);
                                int priceCount=minPrice+random.nextInt(maxPrice-minPrice);
                                explorePocket.setStardust(explorePocket.getStardust()+priceCount);
                                explorePocketService.changeStars(explorePocket);
                                builder.append("您成功出售了：\n"+fianlMsg+AssemblyCache.seedSpeciesMap.get(item.getSpeciesId()).getUnit()+"\n收益："+priceCount+"星屑");
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
            if(count.intValue()==0){
                builder.append("无法出售！您的仓库里没有该作物...");
            }
            cq.sendGroupMsg(groupId, Msg.builder().at(userId).text(builder.toString()), false);
            return MESSAGE_IGNORE;
        }
        // 继续执行下一个插件
        return MESSAGE_IGNORE;
    }

    public Disaster getDisasterJson() throws IOException {
        String path = "/disaster.json";
        InputStream config = getClass().getResourceAsStream(path);
        if (config == null) {
            throw new RuntimeException("读取文件失败");
        } else {
            JSONObject json = JSON.parseObject(config, JSONObject.class);
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

}

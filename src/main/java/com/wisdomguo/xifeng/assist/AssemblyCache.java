package com.wisdomguo.xifeng.assist;

import com.wisdomguo.xifeng.modules.botset.blacklist.entity.BlackList;
import com.wisdomguo.xifeng.modules.botset.blacklist.service.BlackListService;
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
import com.wisdomguo.xifeng.modules.botset.qqgroup.entity.QQGroup;
import com.wisdomguo.xifeng.modules.botset.qqgroup.service.QQGroupSerivce;
import com.wisdomguo.xifeng.modules.game.farm.userinfo.entity.FarmUserInfo;
import com.wisdomguo.xifeng.modules.game.farm.userinfo.service.FarmUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.*;

/**
 * AssemblyCache
 *
 * @author wisdom-guo
 * @since 2021/7/8
 */
@Component
public class AssemblyCache implements ServletContextAware {

    @Autowired
    private BlackListService blackListService;

    @Autowired
    private QQGroupSerivce qqGroupSerivce;

    @Autowired
    private ExplorePocketService explorePocketService;

    @Autowired
    private SeedBagService seedBagService;

    @Autowired
    private PlantedFieldService plantedFieldService;

    @Autowired
    private SeedSpeciesService seedSpeciesService;

    @Autowired
    private FruitService fruitService;

    @Autowired
    private FarmUserInfoService userInfoService;


    //???????????????
    public static Map<Long,BlackList> blackLists=new HashMap<>();
    //???????????????
    public static Map<String,QQGroup> qqGroups=new HashMap<>();
    //????????????
    public static Map<Long, ExplorePocket> explorePockets=new HashMap<>();
    //?????????
    public static Map<Long, List<SeedBag>> seedBags=new HashMap<>();
    //????????????
    public static Map<Long, List<PlantedField>> plantedFields=new HashMap<>();
    //???????????????
    public static Map<Long, List<Fruit>> fruits=new HashMap<>();
    //????????????
    public static Map<Integer, SeedSpecies> seedSpeciesMap=new HashMap<>();
    //????????????
    public static Map<Long, FarmUserInfo> userInfos=new HashMap<>();

    @Override
    public void setServletContext(ServletContext servletContext) {

//        //???????????????
//        blackListService.list().stream().forEach(item->{
//            blackLists.put(item.getId(),item);
//        });
//
//        //???????????????
//        qqGroupSerivce.list().stream().forEach(item->{
//            qqGroups.put(item.getGroupId(),item);
//        });
//
//        //????????????????????????
//        explorePocketService.list().stream().forEach(item->{
//            explorePockets.put(item.getQqId(),item);
//        });
//
//        //???????????????
//        seedBagService.list().stream().forEach(item->{
//            if(seedBags.get(item.getQqId())!=null){
//                seedBags.get(item.getQqId()).add(item);
//            }else{
//                List<SeedBag> list=new ArrayList<>();
//                list.add(item);
//                seedBags.put(item.getQqId(),list);
//            }
//        });
//
//        //??????????????????
//        plantedFieldService.list().stream().forEach(item->{
//            if(plantedFields.get(item.getQqId())!=null){
//                plantedFields.get(item.getQqId()).add(item);
//            }else{
//                List<PlantedField> list=new ArrayList<>();
//                list.add(item);
//                plantedFields.put(item.getQqId(),list);
//            }
//        });
//
//        //??????????????????
//        fruitService.list().stream().forEach(item->{
//            if(fruits.get(item.getQqId())!=null){
//                fruits.get(item.getQqId()).add(item);
//            }else{
//                List<Fruit> list=new ArrayList<>();
//                list.add(item);
//                fruits.put(item.getQqId(),list);
//            }
//        });
//
//        //??????????????????
//        seedSpeciesService.list().stream().forEach(item->{
//            seedSpeciesMap.put(item.getId(),item);
//        });
//
//        //??????????????????
//        userInfoService.list().stream().forEach(item->{
//            userInfos.put(item.getQqId(),item);
//        });
    }

}

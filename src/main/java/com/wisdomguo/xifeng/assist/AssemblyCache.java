package com.wisdomguo.xifeng.assist;

import com.wisdomguo.xifeng.modules.blacklist.entity.BlackList;
import com.wisdomguo.xifeng.modules.blacklist.service.BlackListService;
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


    //黑名单列表
    public static Map<Long,BlackList> blackLists=new HashMap<>();
    //群设置列表
    public static Map<String,QQGroup> qqGroups=new HashMap<>();
    //探索口袋
    public static Map<Long, ExplorePocket> explorePockets=new HashMap<>();
    //种子袋
    public static Map<Long, List<SeedBag>> seedBags=new HashMap<>();
    //用户田地
    public static Map<Long, List<PlantedField>> plantedFields=new HashMap<>();
    //用户作物库
    public static Map<Long, List<Fruit>> fruits=new HashMap<>();
    //种子类型
    public static Map<Integer, SeedSpecies> seedSpeciesMap=new HashMap<>();

    @Override
    public void setServletContext(ServletContext servletContext) {

        //加载黑名单
        blackListService.list().stream().forEach(item->{
            blackLists.put(item.getId(),item);
        });

        //加载群设置
        qqGroupSerivce.list().stream().forEach(item->{
            qqGroups.put(item.getGroupId(),item);
        });

        //加载星空寻宝背包
        explorePocketService.list().stream().forEach(item->{
            explorePockets.put(item.getQqId(),item);
        });

        //加载种子袋
        seedBagService.list().stream().forEach(item->{
            if(seedBags.get(item.getQqId())!=null){
                seedBags.get(item.getQqId()).add(item);
            }else{
                List<SeedBag> list=new ArrayList<>();
                list.add(item);
                seedBags.put(item.getQqId(),list);
            }
        });

        //加载个人田地
        plantedFieldService.list().stream().forEach(item->{
            if(plantedFields.get(item.getQqId())!=null){
                plantedFields.get(item.getQqId()).add(item);
            }else{
                List<PlantedField> list=new ArrayList<>();
                list.add(item);
                plantedFields.put(item.getQqId(),list);
            }
        });

        //加载作物仓库
        fruitService.list().stream().forEach(item->{
            if(fruits.get(item.getQqId())!=null){
                fruits.get(item.getQqId()).add(item);
            }else{
                List<Fruit> list=new ArrayList<>();
                list.add(item);
                fruits.put(item.getQqId(),list);
            }
        });

        //加载作物类型
        seedSpeciesService.list().stream().forEach(item->{
            seedSpeciesMap.put(item.getId(),item);
        });
    }

}

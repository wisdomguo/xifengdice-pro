package com.wisdomguo.xifeng.assist;

import com.wisdomguo.xifeng.modules.blacklist.entity.BlackList;
import com.wisdomguo.xifeng.modules.blacklist.service.BlackListService;
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


    //黑名单列表
    public static Map<Long,BlackList> blackLists=new HashMap<>();
    //群设置列表
    public static Map<String,QQGroup> qqGroups=new HashMap<>();

    @Override
    public void setServletContext(ServletContext servletContext) {
        blackListService.list().stream().forEach(item->{
            blackLists.put(item.getId(),item);
        });
        qqGroupSerivce.list().stream().forEach(item->{
            qqGroups.put(item.getGroupId(),item);
        });
    }

}

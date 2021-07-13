package com.wisdomguo.xifeng.modules.dice.charactercoc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wisdomguo.xifeng.modules.dice.charactercoc.entity.CharacterCoC;
import com.wisdomguo.xifeng.modules.dice.skillcoc.entity.SkillCoC;

import java.util.List;

/**
 * CharacterCocService
 *
 * @author wisdom-guo
 * @since 2021/4/3
 */
public interface CharacterCocService extends IService<CharacterCoC> {

    boolean insertCharacterCocAndSkill(CharacterCoC characterCoC, List<SkillCoC> skillCoCs);
}

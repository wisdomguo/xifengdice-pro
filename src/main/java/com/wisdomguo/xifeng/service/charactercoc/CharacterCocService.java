package com.wisdomguo.xifeng.service.charactercoc;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wisdomguo.xifeng.entity.CharacterCoC;
import com.wisdomguo.xifeng.entity.SkillCoC;

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

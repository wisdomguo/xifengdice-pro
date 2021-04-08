package com.wisdom.xifeng.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BonusDice
 * 奖励惩罚骰
 * @author wisdom-guo
 * @since 2021/4/6
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BonusDice {

    private String resultFirst;
    private String resultTen;
    private Integer result;
}

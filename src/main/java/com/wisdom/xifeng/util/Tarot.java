package com.wisdom.xifeng.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Tarot
 *
 * @author wisdom-guo
 * @since 2021/4/6
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Tarot implements Serializable {

    private String name;
    private String positive;
    private String negative;
    private int num;
}

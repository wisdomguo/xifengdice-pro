package com.wisdomguo.xifeng.assist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ReportRead
 *
 * @author wisdom-guo
 * @since 2021/4/13
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReportRead {
    private Long userId;
    private String message;
}

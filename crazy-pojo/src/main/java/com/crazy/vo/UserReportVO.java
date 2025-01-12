package com.crazy.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReportVO implements Serializable {

    // 日期，以逗号分隔，如 2025-01-01,2025-01-02,2025-01-03
    private String dateList;

    // 新增用户，以逗号分隔，例如：20,21,10
    private String newUserList;

    // 用户总量，以逗号分隔，例如：200,210,220
    private String totalUserList;
}

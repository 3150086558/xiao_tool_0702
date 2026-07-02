package com.xiao.sys.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class DataScopeDTO {

    private Integer positionId;

    @NotBlank(message = "数据范围类型不能为空")
    private String scopeType;

    private List<Integer> customOrgIds;

    public static String fromDataScopeNumber(Integer dataScope) {
        if (dataScope == null) return "self";
        switch (dataScope) {
            case 1: return "self";
            case 2: return "dept";
            case 3: return "dept_and_sub";
            case 4: return "custom";
            case 5: return "all";
            default: return "self";
        }
    }
}

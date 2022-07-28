package com.thtf.office.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TblOrganizationDTO {
    private static final long serialVersionUID = 7909416269024680419L;

    /**
     * 机构负责人名字
     */
    private String leaderName;

    /**
     * 子组织机构
     */
    private List<TblOrganizationDTO> children = new ArrayList();

    /**
     * 组织机构编码 - 前端组件使用
     */
    private String value;

    /**
     * 组织机构名称 - 前端组件使用
     */
    private String label;

    /**
     * 父机构名
     */
    private String parentName;

    /**
     * 前端展示需要
     */
    private Boolean _loading = false;

    /**
     * 前端展示需要
     */
    private Boolean loading = false;

    /**
     * 前端展示需要
     */
    private Boolean hasChildren;
}

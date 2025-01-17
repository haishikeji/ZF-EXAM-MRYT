package com.px.system.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysUserTestLog implements Serializable {
    /**
     * ID
     */
    @ApiModelProperty("ID")
    private Integer id;

    /**
     * 用户手机号
     */
    @ApiModelProperty("用户手机号")
    private String phone;

    /**
     * 题目ID
     */
    @ApiModelProperty("题目ID")
    private Integer qId;

    /**
     * 提交选项
     */
    @ApiModelProperty("提交选项")
    private String qAnswer;

    /**
     * 得分
     */
    @ApiModelProperty("得分")
    private Integer score;

    /**
     * 答案
     */
    @ApiModelProperty("答案")
    private String answer;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 答题日期
     */
    @ApiModelProperty("答题日期")
    private Date createDate;

    /**
     * 答题状态  1 有效 0 无效
     */
    @ApiModelProperty("答题状态  1 有效 0 无效")
    private Integer status;

    private Integer testNum;

    private String month;


}
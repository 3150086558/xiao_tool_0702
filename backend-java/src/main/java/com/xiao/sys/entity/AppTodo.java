package com.xiao.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 待办事项实体类
 */
@TableName("todos")
public class AppTodo {

    @TableId(type = IdType.AUTO)
    private Integer id;

    // 用户ID
    @JsonProperty("user_id")
    private Integer userId;

    // 组织ID（数据库中可能不存在）
    @TableField(exist = false)
    private Integer orgId;

    // 待办标题
    private String title;

    // 完成状态：0-未完成，1-已完成
    private Integer completed;

    // 优先级：low-低，normal-普通，high-高
    private String priority;

    // 截止日期
    @JsonProperty("due_date")
    private String dueDate;

    // 备注
    private String remark;

    // 创建人
    @JsonProperty("creator")
    private String creator;

    // 负责人
    @JsonProperty("assignee")
    private String assignee;

    // 完成时间
    @JsonProperty("completed_at")
    private String completedAt;

    // 创建时间
    @JsonProperty("created_at")
    private String createdAt;

    // 更新时间
    @JsonProperty("updated_at")
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCompleted() {
        return completed;
    }

    public void setCompleted(Integer completed) {
        this.completed = completed;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
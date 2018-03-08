package com.jyall.core.unregistrationhandler;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;
import java.util.Map;
/**
 * 用于记录微服务结点信息的
 * 实体类
 * @autor: Faizel
 * @date: 2017/7/7
 * @version:1.0.0
 */

public class Status {

    private String id;

    private String name;

    private String appid;

    private String from;

    private String to;

    private String hostIp;
    /**
     * 状态变更时间
     **/
    @JSONField(format = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
    private Date createDate;

    private Map<String, String> status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public Map<String, String> getStatus() {
        return status;
    }

    public void setStatus(Map<String, String> status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}

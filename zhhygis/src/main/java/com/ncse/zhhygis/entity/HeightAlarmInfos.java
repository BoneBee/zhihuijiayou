package com.ncse.zhhygis.entity;

import java.util.Date;

public class HeightAlarmInfos {
    private Integer id;

    private String aircode;

    private String carnum;

    private String regid;

    private String regname;

    private String updatetime;

    private String x;

    private String y;

    private String driver;

    private Double limitheight;

    private Double vehiheight;

    private String isalarm;

    private Date writetime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAircode() {
        return aircode;
    }

    public void setAircode(String aircode) {
        this.aircode = aircode == null ? null : aircode.trim();
    }

    public String getCarnum() {
        return carnum;
    }

    public void setCarnum(String carnum) {
        this.carnum = carnum == null ? null : carnum.trim();
    }

    public String getRegid() {
        return regid;
    }

    public void setRegid(String regid) {
        this.regid = regid == null ? null : regid.trim();
    }

    public String getRegname() {
        return regname;
    }

    public void setRegname(String regname) {
        this.regname = regname == null ? null : regname.trim();
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime == null ? null : updatetime.trim();
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x == null ? null : x.trim();
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y == null ? null : y.trim();
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver == null ? null : driver.trim();
    }

    public Double getLimitheight() {
        return limitheight;
    }

    public void setLimitheight(Double limitheight) {
        this.limitheight = limitheight;
    }

    public Double getVehiheight() {
        return vehiheight;
    }

    public void setVehiheight(Double vehiheight) {
        this.vehiheight = vehiheight;
    }

    public String getIsalarm() {
        return isalarm;
    }

    public void setIsalarm(String isalarm) {
        this.isalarm = isalarm == null ? null : isalarm.trim();
    }

    public Date getWritetime() {
        return writetime;
    }

    public void setWritetime(Date writetime) {
        this.writetime = writetime;
    }
}
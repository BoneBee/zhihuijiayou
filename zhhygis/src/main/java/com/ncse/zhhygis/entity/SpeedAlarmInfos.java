package com.ncse.zhhygis.entity;

import java.util.Date;

public class SpeedAlarmInfos {
    private String id;

    private Double sjspeed;

    private Double limitspeed;

    private String updatetime;

    private String isalarm;

    private String driverid;

    private String drivername;

    private String carnum;

    private String x;

    private String y;

    private String regname;

    private String aircode;

    private Date writetime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public Double getSjspeed() {
        return sjspeed;
    }

    public void setSjspeed(Double sjspeed) {
        this.sjspeed = sjspeed;
    }

    public Double getLimitspeed() {
        return limitspeed;
    }

    public void setLimitspeed(Double limitspeed) {
        this.limitspeed = limitspeed;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime == null ? null : updatetime.trim();
    }

    public String getIsalarm() {
        return isalarm;
    }

    public void setIsalarm(String isalarm) {
        this.isalarm = isalarm == null ? null : isalarm.trim();
    }

    public String getDriverid() {
        return driverid;
    }

    public void setDriverid(String driverid) {
        this.driverid = driverid == null ? null : driverid.trim();
    }

    public String getDrivername() {
        return drivername;
    }

    public void setDrivername(String drivername) {
        this.drivername = drivername == null ? null : drivername.trim();
    }

    public String getCarnum() {
        return carnum;
    }

    public void setCarnum(String carnum) {
        this.carnum = carnum == null ? null : carnum.trim();
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

    public String getRegname() {
        return regname;
    }

    public void setRegname(String regname) {
        this.regname = regname == null ? null : regname.trim();
    }

    public String getAircode() {
        return aircode;
    }

    public void setAircode(String aircode) {
        this.aircode = aircode == null ? null : aircode.trim();
    }

    public Date getWritetime() {
        return writetime;
    }

    public void setWritetime(Date writetime) {
        this.writetime = writetime;
    }
}
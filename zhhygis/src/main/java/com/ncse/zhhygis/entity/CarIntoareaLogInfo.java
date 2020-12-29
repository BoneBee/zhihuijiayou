package com.ncse.zhhygis.entity;

import java.util.Date;

public class CarIntoareaLogInfo {
    private Integer id;

    private String aircode;

    private String carnum;

    private String istrue;

    private String regid;

    private String regname;

    private String updatetime;

    private String x;

    private String y;

    private String driver;

    private String isalarm;

    private String isonline;

    private Date writetime;

    private String timecon;

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

    public String getIstrue() {
        return istrue;
    }

    public void setIstrue(String istrue) {
        this.istrue = istrue == null ? null : istrue.trim();
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

    public String getIsalarm() {
        return isalarm;
    }

    public void setIsalarm(String isalarm) {
        this.isalarm = isalarm == null ? null : isalarm.trim();
    }

    public String getIsonline() {
        return isonline;
    }

    public void setIsonline(String isonline) {
        this.isonline = isonline == null ? null : isonline.trim();
    }

    public Date getWritetime() {
        return writetime;
    }

    public void setWritetime(Date writetime) {
        this.writetime = writetime;
    }

    public String getTimecon() {
        return timecon;
    }

    public void setTimecon(String timecon) {
        this.timecon = timecon == null ? null : timecon.trim();
    }
}
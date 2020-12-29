package com.ncse.zhhygis.entity;

import java.util.Date;

public class CarIntoAreaalarmInfos {
    private Integer id;

    private String aircode;

    private String carnum;

    private String intotime;

    private String outtime;

    private String regname;

    private String timecon;

    private String isonline;

    private Date writetime;

    private String maxtime;

    private Integer intoarealogid;

    private String driver;

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

    public String getIntotime() {
        return intotime;
    }

    public void setIntotime(String intotime) {
        this.intotime = intotime == null ? null : intotime.trim();
    }

    public String getOuttime() {
        return outtime;
    }

    public void setOuttime(String outtime) {
        this.outtime = outtime == null ? null : outtime.trim();
    }

    public String getRegname() {
        return regname;
    }

    public void setRegname(String regname) {
        this.regname = regname == null ? null : regname.trim();
    }

    public String getTimecon() {
        return timecon;
    }

    public void setTimecon(String timecon) {
        this.timecon = timecon == null ? null : timecon.trim();
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

    public String getMaxtime() {
        return maxtime;
    }

    public void setMaxtime(String maxtime) {
        this.maxtime = maxtime == null ? null : maxtime.trim();
    }

    public Integer getIntoarealogid() {
        return intoarealogid;
    }

    public void setIntoarealogid(Integer intoarealogid) {
        this.intoarealogid = intoarealogid;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver == null ? null : driver.trim();
    }
}
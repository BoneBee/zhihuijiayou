package com.ncse.zhhygis.entity;

import java.util.Date;

public class CarTrajectoryInfos {
    private Integer id;

    private String carnum;

    private String airportid;

    private String updatetime;

    private Double longitude;

    private Double latitude;

    private Double angle;

    private String alarmsign;

    private String driverid;

    private String drivername;

    private String isonline;

    private Date writetime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCarnum() {
        return carnum;
    }

    public void setCarnum(String carnum) {
        this.carnum = carnum == null ? null : carnum.trim();
    }

    public String getAirportid() {
        return airportid;
    }

    public void setAirportid(String airportid) {
        this.airportid = airportid == null ? null : airportid.trim();
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime == null ? null : updatetime.trim();
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getAngle() {
        return angle;
    }

    public void setAngle(Double angle) {
        this.angle = angle;
    }

    public String getAlarmsign() {
        return alarmsign;
    }

    public void setAlarmsign(String alarmsign) {
        this.alarmsign = alarmsign == null ? null : alarmsign.trim();
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
}
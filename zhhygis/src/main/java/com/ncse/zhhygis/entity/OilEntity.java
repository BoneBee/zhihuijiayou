package com.ncse.zhhygis.entity;

import com.zh.bean.login.MyStaff;

public class OilEntity extends MyStaff{
	private String aircraft;
	private MyStaff loginUserIn;
	
	private String placecode;
	
	private String flightIdA;
	
	private String flightIdD;
	
	public String getPlacecode() {
		return placecode;
	}
	public void setPlacecode(String placecode) {
		this.placecode = placecode;
	}
	
	
	
	public String getFlightIdA() {
		return flightIdA;
	}
	public void setFlightIdA(String flightIdA) {
		this.flightIdA = flightIdA;
	}
	public String getFlightIdD() {
		return flightIdD;
	}
	public void setFlightIdD(String flightIdD) {
		this.flightIdD = flightIdD;
	}
	public String getAircraft() {
		return aircraft;
	}
	public void setAircraft(String aircraft) {
		this.aircraft = aircraft;
	}
	public MyStaff getLoginUserIn() {
		return loginUserIn;
	}
	public void setLoginUserIn(MyStaff loginUserIn) {
		this.loginUserIn = loginUserIn;
	}
	
	
}

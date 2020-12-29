package com.ncse.zhhygis.entity;

import java.io.Serializable;

public class ApiReturnObject implements Serializable {

	private static final long serialVersionUID = 1L;

	private String errorCode = "00";
	private Object errorMessage;
	private Object returnObject;

	public ApiReturnObject(String errorCode, Object errorMessage, Object returnObject) {
		super();
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.returnObject = returnObject;
	}
}

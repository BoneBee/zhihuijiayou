package com.ncse.zhhygis.utils.projectUtils;

import java.io.Serializable;

import com.ncse.zhhygis.entity.ApiReturnObject;

public class ApiReturnUtil implements Serializable {

	private static final long serialVersionUID = 1L;

	public static ApiReturnObject error(Object errorMessage) {
		return new ApiReturnObject("01", errorMessage, "");
	}

	public static ApiReturnObject error(Object errorMessage, Object returnObject) {
		return new ApiReturnObject("01", errorMessage, returnObject);
	}

	public static ApiReturnObject success(Object returnObject) {
		return new ApiReturnObject("00", "success", returnObject);
	}

	public static ApiReturnObject success(Object errorMessage, Object returnObject) {
		return new ApiReturnObject("00", errorMessage, returnObject);
	}

}

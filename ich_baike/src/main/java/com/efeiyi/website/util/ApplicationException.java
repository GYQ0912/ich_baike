package com.efeiyi.website.util;

import net.sf.json.JSONObject;

public class ApplicationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static int INNER_ERROR = 0x01;
	public static int DATABASE_ERROR = 0x02;

	private static final String[] ERROR_DESC_LIST = new String[] { "", "内部错误.", "数据库错误"};

	private int code;
	private String description;
	private String innerDescription;

	public ApplicationException(int code) {
		this.code = code;
		this.description = ERROR_DESC_LIST[code];
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setInnerDescription(String innerDescription) {
		this.innerDescription = innerDescription;
	}

	public String toJsonString() {
		JSONObject result = new JSONObject();
		result.put("code", this.code);
		result.put("description", this.description);
		result.put("innerDescription", innerDescription);
		return result.toString();
	}

}

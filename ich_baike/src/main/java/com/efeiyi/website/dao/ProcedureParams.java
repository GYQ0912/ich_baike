package com.efeiyi.website.dao;

import java.lang.reflect.Field;

import com.efeiyi.website.util.Util;

public  class ProcedureParams {
	
	public  String[]  get(String procedureName) {
		Field field = null;
		try {
			field = ProcedureParams.class.getDeclaredField(procedureName);
		} catch (SecurityException e) {
			Util.getLogger(this.getClass()).debug(e.getMessage());
		} catch (NoSuchFieldException e) {
			Util.getLogger(this.getClass()).debug(e.getMessage());
		}
		
		String[] value = {};
		try {
			value = (String[])field.get(this);
		} catch (IllegalArgumentException e) {
			Util.getLogger(this.getClass()).debug(e.getMessage());
		} catch (IllegalAccessException e) {
			Util.getLogger(this.getClass()).debug(e.getMessage());
		}
		
		return  value;
		
	}
	
	
}

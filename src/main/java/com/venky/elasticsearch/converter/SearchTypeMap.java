package com.venky.elasticsearch.converter;

import java.util.HashMap;
import java.util.Map;


public final class SearchTypeMap {
	
	@SuppressWarnings("rawtypes")
	private static final Map<String, Class> searchTypeMap = new HashMap<String, Class>();
	
	static {
		searchTypeMap.put("mo", SearchTypeMap.class);
    }
	
	@SuppressWarnings("rawtypes")
	public static Class getClass(String key) {
		return searchTypeMap.get(key);
	}
}

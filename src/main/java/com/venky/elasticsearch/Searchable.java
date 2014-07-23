package com.venky.elasticsearch;



public interface Searchable {
	
	public long getId();
	
	public String searchIndex();
	
	public String searchType();
	
}

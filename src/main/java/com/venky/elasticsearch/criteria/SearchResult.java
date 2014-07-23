package com.venky.elasticsearch.criteria;

import java.util.ArrayList;
import java.util.List;

import com.venky.elasticsearch.AbstractObject;
import com.venky.elasticsearch.Searchable;


@SuppressWarnings("serial")
public class SearchResult extends AbstractObject {
	
	private List<Searchable> results = new ArrayList<Searchable>();

	private int pageSize = 20;

	private int page = 1;

	private int totalResults = -1;
	
	public SearchResult(List<Searchable> results , int page, int pageSize, int totalResults){
		this.results=results;
		this.page=page;
		this.pageSize=pageSize;
		this.totalResults=totalResults;
		
	}
	
	public List<Searchable> getList() {
		return hasNextPage() ? results.subList(0, pageSize) : results;
	}

	public int getTotalResults() {
		return totalResults;
	}

	public int getFirstResultNumber() {
		return (page - 1) * pageSize + 1;
	}

	public int getLastResultNumber() {
		int fullPage = getFirstResultNumber() + pageSize - 1;
		return getTotalResults() < fullPage ? getTotalResults() : fullPage;
	}

	public int getLastPageNumber() {
		double totalResults = new Integer(getTotalResults()).doubleValue();
		int mod = (int) (totalResults % pageSize);
		return mod == 0 ? new Double(Math.floor(totalResults / pageSize)).intValue() : new Double(Math.floor(totalResults / pageSize) + 1).intValue();
	}

	public int getNextPageNumber() {
		return page + 1;
	}

	public int getPreviousPageNumber() {
		return page - 1;
	}

	public int getTotalPages() {
		return (totalResults + (pageSize - 1)) / pageSize;
	}

	public int getPageNo() {
		return page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public boolean isNextPage() {
		return results != null && results.size() > pageSize;
	}

	public boolean hasNextPage() {
		return results != null && results.size() > pageSize;
	}

}

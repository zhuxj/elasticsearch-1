package com.venky.elasticsearch.criteria;

import java.util.HashSet;
import java.util.Set;

import org.elasticsearch.index.query.QueryBuilder;

import com.venky.elasticsearch.AbstractObject;



@SuppressWarnings("serial")
public class SearchQuery extends AbstractObject {

	public static final int	DEFAULT_START_PAGE		= 1;
	public static final int	DEFAULT_ITEMS_PER_PAGE	= 20;

	private int				startPage				= DEFAULT_START_PAGE;
	private int				itemsPerPage			= DEFAULT_ITEMS_PER_PAGE;
	private Set<OrderBy>	orderByList;
	private GeoFilter		geoFilter;
	private QueryBuilder queryBuilder;

	/**
	 * @param itemsPerPage
	 * @param orderByList
	 * @param queryString
	 * @param startPage
	 */
	public SearchQuery() {
		super();
		this.itemsPerPage = DEFAULT_ITEMS_PER_PAGE;
		this.startPage = DEFAULT_START_PAGE;
	}
	
	public SearchQuery(final QueryBuilder queryBuilder) {
		this();
		this.orderByList = new HashSet<OrderBy>();
		this.orderByList.add(OrderBy.DEFAULT);
		this.queryBuilder = queryBuilder;
	}

	/**
	 * @param queryString
	 * @param orderByList
	 */
	public SearchQuery(final QueryBuilder queryBuilder, final Set<OrderBy> orderByList) {
		this();
		this.orderByList = orderByList;
		this.queryBuilder = queryBuilder;
	}

	/**
	 * @param itemsPerPage
	 * @param orderByList
	 * @param queryString
	 * @param startPage
	 */
	public SearchQuery(final QueryBuilder queryBuilder, final Set<OrderBy> orderByList, final int startPage,
			final int itemsPerPage) {
		this();
		this.queryBuilder = queryBuilder;
		this.orderByList = orderByList;
		this.startPage = startPage;
		this.itemsPerPage = itemsPerPage;
	}
	
	public int getItemsPerPage() {
		return this.itemsPerPage;
	}

	public Set<OrderBy> getOrderByList() {
		return this.orderByList;
	}

	public int getStartPage() {
		return this.startPage;
	}

	public void setItemsPerPage(final int itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}

	public void setOrderByList(final Set<OrderBy> orderByList) {
		this.orderByList = orderByList;
	}

	public void setStartPage(final int startPage) {
		this.startPage = startPage;
	}

	public GeoFilter getGeoFilter() {
		return geoFilter;
	}

	public void setGeoFilter(GeoFilter geoFilter) {
		this.geoFilter = geoFilter;
	}

	/**
	 * @return the queryBuilder
	 */
	public QueryBuilder getQueryBuilder() {
		return queryBuilder;
	}

	/**
	 * @param queryBuilder the queryBuilder to set
	 */
	public void setQueryBuilder(QueryBuilder queryBuilder) {
		this.queryBuilder = queryBuilder;
	}
}

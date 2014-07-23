package com.venky.elasticsearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.status.IndicesStatusRequestBuilder;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.base.Preconditions;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.GeoDistanceFilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.venky.elasticsearch.converter.EntityToJsonByteArrayConverter;
import com.venky.elasticsearch.converter.SearchResponseToSearchResultConverter;
import com.venky.elasticsearch.criteria.OrderBy;
import com.venky.elasticsearch.criteria.SearchQuery;
import com.venky.elasticsearch.criteria.SearchResult;
import com.venky.elasticsearch.criteria.SortDirection;


public class IndexTemplate {
	
	public static String CLAUSES_SEPARATOR = "#";

	private static IndexTemplate indexTemplate = null;
	
	private IndexTemplate() {
		
	}
	
	public static IndexTemplate getInstance() {
		if(indexTemplate == null) {
			indexTemplate = new IndexTemplate();
		}
		return indexTemplate;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void createGeoPoint(String index, String type) {
		try {
			
			ClusterState cs = this.getElasticSearchClient()
					.admin().cluster().prepareState().execute().actionGet().getState();
			if(!cs.getMetaData().hasIndex(index)) {
				createIndex(index);
				createGeoPointDataType(index, type);
				return;
			}
			
			IndexMetaData imd = cs.getMetaData().index(index);
			MappingMetaData mdd = imd.mapping(type);
			if(mdd ==  null) {
				createGeoPointDataType(index, type);
				return;
			}
			
			Map<String, Object> userDataMap = (Map) mdd.getSourceAsMap().get("properties");
			
			if(!userDataMap.containsKey("geoPoint")) {
				createGeoPointDataType(index, type);
				return;
			}
			
			Map<String, Object> properties = (Map) userDataMap.get("geoPoint");
			
			if(!properties.containsKey("type")) {
				createGeoPointDataType(index, type);
				return;
			}
			
			if(!properties.get("type").equals("geo_point")) {
				createGeoPointDataType(index, type);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createGeoPointDataType(String index, String type) {
		PutMappingResponse response = this.getElasticSearchClient().admin().indices()
			.preparePutMapping(index)
			.setType(type)
			.setSource("{ \""+ type +"\" : {\"properties\" : { \"geoPoint\" : {\"type\" : \"geo_point\"} } } }")
			.execute().actionGet();	
		System.out.println("PutMapping for Geo point: " + response.isAcknowledged());
	}
	
	public void createIndex(String index) throws Exception {

		CreateIndexRequestBuilder cirb = this.getElasticSearchClient().admin().indices().prepareCreate(index);
		
		cirb.execute().actionGet();
	}

	
	public void index(Searchable searchable) {
		try {
			Preconditions.checkArgument(searchable != null, "document is required");

			Preconditions.checkArgument(searchable instanceof Searchable, "Document is not the Searchable entity");

			final byte[] source = this.getEntityToJsonByteArrayConverter().convert(searchable);
			
			String indexName = searchable.searchIndex();
			
			String type = searchable.searchType();
			
			Preconditions.checkArgument(indexName != null, "Index name is required");
			
			this.getElasticSearchClient().prepareIndex(indexName, type, Long.toString(searchable.getId()))
					.setRefresh(true)
					.setSource(source)
					.execute().actionGet();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Searchable findById(final Searchable searchable, final Long id) throws Exception {
		try {
			
			String indexName = searchable.searchIndex();
			
			String type = searchable.searchType();
			
			Preconditions.checkArgument(indexName != null, "indexName is required");
			
			Preconditions.checkArgument(type != null, "type is required");

			Preconditions.checkArgument(id != null, "id is required");

			final TermQueryBuilder idClause = QueryBuilders.termQuery("_id", id);

			final QueryBuilder builder = QueryBuilders.boolQuery().must(idClause);

			SearchResponse searchResponse = this.getElasticSearchClient().prepareSearch(indexName).setTypes(type)
					.setQuery(builder).execute().actionGet();
			
			final List<Searchable> result = this.getSearchResponseToSearchResultConverter().convert(searchResponse);

			return (result != null && result.iterator().hasNext()) ? result.iterator().next() : null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public void removeFromIndex(final String indexName, final String type, final Long id) throws Exception {
		try {
			Preconditions.checkArgument(type != null, "type is required");

			Preconditions.checkArgument(id != null, "Id is required");

			this.getElasticSearchClient().prepareDelete(indexName, type, id.toString())
					.setRefresh(true).execute().actionGet();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private void applySort(final SearchRequestBuilder searchRequestBuilder, final Set<OrderBy> orderByList) {
		if (CollectionUtils.isNotEmpty(orderByList)) {
			for ( final OrderBy orderBy : orderByList ) {
				if(orderBy.getGeoDistanceSortBuilder() != null) {
					searchRequestBuilder.addSort(orderBy.getGeoDistanceSortBuilder());
				} else {
					searchRequestBuilder.addSort(orderBy.getField(),
							orderBy.getSortDirection() == SortDirection.ASC ? SortOrder.ASC : SortOrder.DESC);
				}
			}
		}

		/*if (orderSpecificationDoesntContainDefaultSortField(orderByList)) {
			applyDefaultSort(searchRequestBuilder);
		}*/
	}
	
	@SuppressWarnings("deprecation")
	public List<String> getAllIndices() throws Exception {
		try {
			IndicesStatusRequestBuilder isrb = this.getElasticSearchClient()
					.admin().indices().prepareStatus();
			IndicesStatusResponse rsp = isrb.execute().actionGet();

			List<String> indices = new ArrayList<String>();
			for (String indexName : rsp.getIndices().keySet()) {
				indices.add(indexName);
			}
			Collections.sort(indices);
			return indices;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private SearchRequestBuilder convertSearchQueryToRequestBuilder(final String index, final String type, final SearchQuery query) throws Exception {
		String[] indices = {index};
		if(StringUtils.isEmpty(index) || "_all".equalsIgnoreCase(index)) {
			indices = convertListToStringArray(getAllIndices());
		}
		String[] types = {type};
		if(StringUtils.isEmpty(type) || "_all".equalsIgnoreCase(type)) {
			types = null;
		}
		return convertSearchQueryToRequestBuilder(indices, types, query);
	}
	
	/**
	 * @param query
	 * @return
	 * @throws Exception 
	 */
	private SearchRequestBuilder convertSearchQueryToRequestBuilder(final String[] indices, final String[] types, final SearchQuery query) {
		final SearchRequestBuilder searchRequestBuilder = this.getElasticSearchClient().prepareSearch(indices);
		
		if(types != null) {
			searchRequestBuilder.setTypes(types);
		}

		if (query == null) 
			return searchRequestBuilder;

		searchRequestBuilder.setFrom(query.getStartPage() - 1);

		if (query.getItemsPerPage() > 0) {
			searchRequestBuilder.setFrom((query.getStartPage() - 1) * query.getItemsPerPage());
			searchRequestBuilder.setSize(query.getItemsPerPage());
		}

		QueryBuilder queryBuilder = query.getQueryBuilder();
		
		if (searchRequestBuilder != null) {
			searchRequestBuilder.setQuery(queryBuilder);
		}
		
		if(query.getGeoFilter() != null) {
			 GeoDistanceFilterBuilder gfb = FilterBuilders.geoDistanceFilter("geoPoint").distance(query.getGeoFilter().getDistance(), query.getGeoFilter().getDistanceUnit())
					.point(query.getGeoFilter().getPinLocation().getLatitude(), query.getGeoFilter().getPinLocation().getLongitude())
					.geoDistance(GeoDistance.ARC);
			 searchRequestBuilder.setPostFilter(gfb);
		} 
		applySort(searchRequestBuilder, query.getOrderByList());
		return searchRequestBuilder;
	}
	
	/**
	 * @see org.diveintojee.poc.jbehave.persistence.SearchEngine#search(org.diveintojee.poc.jbehave.domain.SearchQuery)q
	 */
	public SearchResult search(final String indexName, final String searchTypes, final SearchQuery searchQuery) throws Exception {
		try {
			final SearchRequestBuilder searchRequestBuilder = convertSearchQueryToRequestBuilder(indexName, searchTypes, searchQuery);
			
			System.out.println("INFO:SearchRequestBuilder:" + searchRequestBuilder.toString());

			SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
			
			List<Searchable> items = this.getSearchResponseToSearchResultConverter().convert(searchResponse);
			
			int totalResults = Long.valueOf(searchResponse.getHits().getTotalHits()).intValue();
			
			final SearchResult result = new SearchResult(items, searchQuery.getStartPage(), searchQuery.getItemsPerPage(), totalResults);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * @see org.diveintojee.poc.jbehave.persistence.SearchEngine#search(org.diveintojee.poc.jbehave.domain.SearchQuery)
	 */
	public SearchResponse search(final SearchQuery searchQuery, final String indexName, final String searchTypes) throws Exception {
		try {
			final SearchRequestBuilder searchRequestBuilder = convertSearchQueryToRequestBuilder(indexName, searchTypes, searchQuery);

			SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
			
			return searchResponse;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public SearchResult search(List<String> indices, List<String> types, SearchQuery searchQuery) throws Exception {
		try {
			final SearchRequestBuilder searchRequestBuilder = convertSearchQueryToRequestBuilder(
					convertListToStringArray(indices), convertListToStringArray(types), searchQuery);

			SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
			
			List<Searchable> items = this.getSearchResponseToSearchResultConverter().convert(searchResponse);
			
			int totalResults = Long.valueOf(searchResponse.getHits().getTotalHits()).intValue();
			
			final SearchResult result = new SearchResult(items, searchQuery.getStartPage(), searchQuery.getItemsPerPage(), totalResults);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static String[] convertListToStringArray(List<String> ids) {
    	if(ids == null) {
    		return null;
    	}
    	String[] array = new String[ids.size()];
		for(int i=0 ; i <ids.size() ; i++) {
			array[i] = ids.get(i);
		}
		return array;
	}

	/**
	 * @return the elasticSearchClient
	 */
	public Client getElasticSearchClient() {
		return ElasticSearch.getInstance().getEsClient();
	}
	
	public EntityToJsonByteArrayConverter getEntityToJsonByteArrayConverter() {
		return EntityToJsonByteArrayConverter.getInstance();
	}

	public SearchResponseToSearchResultConverter getSearchResponseToSearchResultConverter() {
		return SearchResponseToSearchResultConverter.getInstance();
	}
}

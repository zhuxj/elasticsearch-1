package com.venky.elasticsearch.converter;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import com.venky.elasticsearch.Searchable;




public class SearchResponseToSearchResultConverter implements Converter<SearchResponse, List<Searchable>> {

	private static SearchResponseToSearchResultConverter searchResponseToSearchResultConverter;
	
	private static ObjectMapper objectMapper;
	
	private SearchResponseToSearchResultConverter() {
		
	}
	
	public static SearchResponseToSearchResultConverter getInstance() {
		if(searchResponseToSearchResultConverter == null) {
			searchResponseToSearchResultConverter = new SearchResponseToSearchResultConverter();
			objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		}
		return searchResponseToSearchResultConverter;
	}
	
    /**
     * @see 
     */
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Searchable> convert(final SearchResponse source) {
    	if (source == null) {
    		return null;
    	}

    	List<Searchable> items = new ArrayList<Searchable>();
        final SearchHits hits = source.getHits();
        for (final SearchHit searchHit : hits.getHits()) {
    		try {
				Class clazz = SearchTypeMap.getClass(searchHit.getType());
    			final Searchable entity = (Searchable) objectMapper.readValue(searchHit.source(), clazz);
    			items.add(entity);
    		} catch (final IOException e) {
    			e.printStackTrace();
    		} catch (Exception e1) {
    			e1.printStackTrace();
			}
        }
        return items;
    }

}
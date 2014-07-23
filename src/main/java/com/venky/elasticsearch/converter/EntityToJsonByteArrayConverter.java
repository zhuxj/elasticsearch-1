package com.venky.elasticsearch.converter;


import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.venky.elasticsearch.Searchable;



public class EntityToJsonByteArrayConverter implements Converter<Searchable, byte[]> {
	
	private static EntityToJsonByteArrayConverter entityToJsonByteArrayConverter;
	
	private static ObjectMapper objectMapper;
	
	private EntityToJsonByteArrayConverter() {
		
	}
	
	public static EntityToJsonByteArrayConverter getInstance() {
		if(entityToJsonByteArrayConverter == null) {
			entityToJsonByteArrayConverter = new EntityToJsonByteArrayConverter();
			objectMapper = new ObjectMapper();
			objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
			objectMapper.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, false);
			objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
		}
		return entityToJsonByteArrayConverter;
	}

    /**
     * @see 
     */
    public byte[] convert(final Searchable source) {

        if (source == null) {
            return null;
        }
        
        String string;
        try {
            string = objectMapper.writeValueAsString(source);
            return string.getBytes("utf-8");
        } catch (final Throwable th) {
            throw new IllegalArgumentException(th);
        }
    } 
    
}

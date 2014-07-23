package com.venky.elasticsearch.converter;

public interface Converter<S, T> {

	public T convert(S source);
}

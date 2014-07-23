package com.venky.elasticsearch;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.tivamo.util.StringUtil;


public final class ElasticSearch {
	
	private static ElasticSearch es;
	
	private static Client esClient;
	
	private static String settingsFile = "es.properties";
	
	private ElasticSearch() {

	}
	
	public static ElasticSearch getInstance() {
		if(es == null) {
			es = new ElasticSearch();
			esClient = es.buildEsClient();
		}
		return es;
	}
	
	private Client buildEsClient() {
		Settings settings = ImmutableSettings.settingsBuilder()
			.loadFromClasspath(settingsFile)
			.build();
		TransportClient client = new TransportClient(settings);
		String esNodeStr = System.getProperty("es.nodes");
		if(StringUtil.isEmpty(esNodeStr)) {
			esNodeStr = "localhost:9300";
		}
		String[] esNodes = esNodeStr.split(",");
		for (int i = 0; i < esNodes.length; i++) {
			client.addTransportAddress(toAddress(esNodes[i].trim()));
		}
		return client;
	}
	
	private InetSocketTransportAddress toAddress(String address) {
 		if (address == null) return null;
 		
 		String[] splitted = address.split(":");
 		int port = 9300;
 		if (splitted.length > 1) {
 			port = Integer.parseInt(splitted[1]);
 		}
 		
		return new InetSocketTransportAddress(splitted[0], port);
	}

	public Client getEsClient() {
		return esClient;
	}
}

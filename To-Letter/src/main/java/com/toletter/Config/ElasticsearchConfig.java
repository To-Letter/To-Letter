package com.toletter.Config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.toletter.Repository.ElasticSearch")
public class ElasticsearchConfig {
    @Value("${spring.data.elasticsearch.host}")
    private String host;

    @Value("${spring.data.elasticsearch.port}")
    private int port;

    @Bean(destroyMethod = "close")
    public RestHighLevelClient client(){
        RestClientBuilder builder = RestClient.builder(new HttpHost(host, port));
        RestHighLevelClient client = new RestHighLevelClient(builder);

        return client;
    }
}

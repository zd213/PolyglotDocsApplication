package dev.arusha.PolyglotDocs.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfiguration {
    @Value("${elasticsearch.url}")
    String elastcSearchUrl;
    @Value("${elasticsearch.port}")
    int elastcSearchPort;
    /*@Value("${elasticsearch.username}")
    String elastcSearchUsername;
    @Value("${elasticsearch.password}")
    String elastcSearchPassword;
    @Value("${elasticsearch.index}")
    String elastcSearchIndex;*/

    /*@Bean
     public RestClient getRestClient() {
         BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
         credentialsProvider.setCredentials(AuthScope.ANY,
                 new UsernamePasswordCredentials(elastcSearchUsername, elastcSearchPassword));

         return RestClient.builder(new HttpHost(elastcSearchUrl, elastcSearchPort))
                 .setHttpClientConfigCallback(httpClientBuilder ->
                         httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                 ).build();
     }*/

    @Bean
    public RestClient getRestClient() {
        HttpHost httpHost = new HttpHost(elastcSearchUrl, elastcSearchPort, "http");
        return RestClient.builder(httpHost).build();
    }

    @Bean
    public ElasticsearchTransport getElasticsearchTransport(RestClient restClient) {
        return new RestClientTransport(restClient, new JacksonJsonpMapper());
    }

    @Bean
    public ElasticsearchClient getElasticsearchClient(ElasticsearchTransport elasticsearchTransport) {
        return new ElasticsearchClient(elasticsearchTransport);
    }
}
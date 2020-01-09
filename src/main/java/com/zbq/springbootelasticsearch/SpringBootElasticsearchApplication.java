package com.zbq.springbootelasticsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.jest.JestAutoConfiguration;

@SpringBootApplication
public class SpringBootElasticsearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootElasticsearchApplication.class, args);
    }

}

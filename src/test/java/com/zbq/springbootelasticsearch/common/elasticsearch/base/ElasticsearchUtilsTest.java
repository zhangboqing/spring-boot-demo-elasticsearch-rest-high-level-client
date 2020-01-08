package com.zbq.springbootelasticsearch.common.elasticsearch.base;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author zhangboqing
 * @Date 2020-01-07
 */

@SpringBootTest
class ElasticsearchUtilsTest {

    @Autowired
    ElasticsearchUtils elasticsearchUtils;

    @Test
    void existIndex() {
        elasticsearchUtils.existIndex("goods");
    }
}
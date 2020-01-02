package com.zbq.springbootelasticsearch;

import com.alibaba.fastjson.JSON;
import com.zbq.springbootelasticsearch.common.enums.ElasticsearchIndexEnum;
import com.zbq.springbootelasticsearch.model.GoodsESEntity;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;

/**
 * @author zhangboqing
 * @date 2018/10/23
 */
@SpringBootTest
@Slf4j
public class ElasticSearchTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 测试新增
     */
    @Test
    public void insertTest() throws IOException {
        GoodsESEntity goodsESEntity = GoodsESEntity.builder().goodsId(1L).goodsName("1").goodBrand("1").goodsSpec("1").goodsAccessoriesCode("1").goodsOriginalFactoryCode("1").build();
        BulkRequest request = new BulkRequest();
        request.add(new IndexRequest(ElasticsearchIndexEnum.GOODS.getIndexName()).id("1").source(JSON.toJSONString(goodsESEntity),XContentType.JSON));
        BulkResponse bulk = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        log.info("==========================> {}",JSON.toJSONString(bulk));
    }


}

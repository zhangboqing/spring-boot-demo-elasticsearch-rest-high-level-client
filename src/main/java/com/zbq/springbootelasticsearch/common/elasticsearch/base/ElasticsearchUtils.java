package com.zbq.springbootelasticsearch.common.elasticsearch.base;

import com.alibaba.fastjson.JSON;
import com.zbq.springbootelasticsearch.common.config.ElasticsearchProperties;
import com.zbq.springbootelasticsearch.common.elasticsearch.annotation.ESField;
import com.zbq.springbootelasticsearch.common.elasticsearch.annotation.ESId;
import com.zbq.springbootelasticsearch.common.elasticsearch.enums.ESFieldType;
import com.zbq.springbootelasticsearch.common.exception.ElasticsearchException;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.IndicesExists;
import io.searchbox.indices.mapping.PutMapping;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * @author zhangboqing
 * @date 2019/12/10
 */
@Slf4j
@Component
public class ElasticsearchUtils {

    @Autowired
    public JestClient client;

    @Autowired
    private ElasticsearchProperties elasticsearchProperties;



    public boolean existIndex(String indexName) {


        boolean exists = false;
        try {
            JestResult result = client.execute(new IndicesExists.Builder(indexName).build());
            int responseCode = result.getResponseCode();
            log.info(" existIndex responseCcode: {},errorMessage: {}", responseCode,result.getErrorMessage());

            if (responseCode != 200 && responseCode != 404) {
                throw new ElasticsearchException("判断索引 {" + indexName + "} 是否存在失败");
            }

            if (responseCode == 200) {
                exists = true;
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new ElasticsearchException("判断索引 {" + indexName + "} 是否存在失败");
        }
        return exists;
    }

    public void createIndexRequest(String indexName) {
        createIndexRequest(indexName, elasticsearchProperties.getIndex().getNumberOfShards(), elasticsearchProperties.getIndex().getNumberOfReplicas());
    }

    public void createIndexRequest(String indexName, int shards, int replicas) {
        if (existIndex(indexName)) {
            return;
        }

        try {
            Settings.Builder settingsBuilder = Settings.builder();
            settingsBuilder.put("number_of_shards",shards);
            settingsBuilder.put("number_of_replicas",replicas);

            JestResult result = client.execute(new CreateIndex.Builder(indexName).settings(settingsBuilder.build().getAsMap()).build());
            int responseCode = result.getResponseCode();
            log.info(" createIndexRequest responseCcode: {},jsonString: {},errorMessage: {}", result.getResponseCode(),result.getJsonString(),result.getErrorMessage());
            if (responseCode != 200) {
                throw new ElasticsearchException("创建索引 {" + indexName + "} 失败");
            }
        } catch (IOException e) {
            throw new ElasticsearchException("创建索引 {" + indexName + "} 失败");
        }
    }

    public void putMappingRequest(String indexName, Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        if (fields == null || fields.length == 0) {
            return;
        }


        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            {
                builder.startObject("properties");
                {
                    for (int i = 0; i < fields.length; i++) {
                        Field field = fields[i];
                        ESId esId = field.getAnnotation(ESId.class);
                        if (esId != null) {
                            continue;
                        } else {
                            AnnotationAttributes esField = AnnotatedElementUtils.getMergedAnnotationAttributes(field, ESField.class);
                            if (esField == null) {
                                continue;
                            }
                            String name = esField.getString("name");
                            if (StringUtils.isEmpty(name)) {
                                throw new ElasticsearchException("注解ESField的name属性未指定");
                            }
                            ESFieldType esFieldType = (ESFieldType) esField.get("type");
                            if (esFieldType == null) {
                                throw new ElasticsearchException("注解ESField的type属性未指定");
                            }
                            builder.startObject(name);
                            {
                                builder.field("type", esFieldType.typeName);
                                // 分词器
                                String analyzer = esField.getString("analyzer");
                                if (StringUtils.hasText(analyzer)) {
                                    builder.field("analyzer", analyzer);
                                }
                            }
                            builder.endObject();
                        }
                    }
                }
                builder.endObject();
            }
            builder.endObject();

            PutMapping putMapping = new PutMapping.Builder(
                    indexName,
                    indexName,
                    builder.string()
            ).build();
            JestResult result = client.execute(putMapping);
            log.info(" putMappingRequest responseCcode: {},jsonString: {},errorMessage: {}", result.getResponseCode(),result.getJsonString(),result.getErrorMessage());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void deleteIndexRequest(String indexName) {
        try {
            JestResult result = client.execute(new DeleteIndex.Builder(indexName).build());
            log.info(" deleteIndexRequest responseCcode: {},errorMessage: {}", result.getResponseCode(),result.getErrorMessage());
        } catch (IOException e) {
            throw new ElasticsearchException("删除索引 {" + indexName + "} 失败");
        }
    }

    public void updateRequest(String indexName, String id, Object object) {
        try {
            JestResult result = client.execute(new Update.Builder(JSON.toJSONString(object)).index(indexName).type(indexName).id(id).build());
            log.info(" updateRequest responseCcode: {},errorMessage: {}", result.getResponseCode(),result.getErrorMessage());
        } catch (IOException e) {
            throw new ElasticsearchException("更新索引 {" + indexName + "} 数据 {" + object + "} 失败");
        }
    }

    public void deleteRequest(String indexName, String id) {
        try {
            JestResult result = client.execute(new Delete.Builder(id).index(indexName).type(indexName).build());
            log.info(" updateRequest responseCcode: {},errorMessage: {}", result.getResponseCode(),result.getErrorMessage());
        } catch (IOException e) {
            throw new ElasticsearchException("删除索引 {" + indexName + "} 数据id {" + id + "} 失败");
        }
    }

    public SearchResult search(String indexName) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        Search search = new Search.Builder(searchSourceBuilder.toString())
                // multiple index or types can be added.
                .addIndex(indexName)
                .addType(indexName)
                .build();

        SearchResult result = null;
        try {
            result = client.execute(search);
            log.info(" updateRequest responseCcode: {},errorMessage: {}", result.getResponseCode(),result.getErrorMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}

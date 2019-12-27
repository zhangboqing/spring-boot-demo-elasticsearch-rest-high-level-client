package com.zbq.springbootelasticsearch.dao.base;

import com.alibaba.fastjson.JSON;
import com.zbq.springbootelasticsearch.common.elasticsearch.annotation.ESDocument;
import com.zbq.springbootelasticsearch.common.elasticsearch.annotation.ESId;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhangboqing
 * @date 2019/12/10
 */
@Slf4j
public abstract class BaseElasticsearchDao<T> {

    @Autowired
    protected ElasticsearchUtils elasticsearchUtils;
    @Autowired
    protected RestHighLevelClient client;

    /**
     * 索引名称
     */
    private String indexName;
    /**
     * ID字段
     */
    private Field idField;
    /**
     * T对应的类型Class
     */
    private Class<T> genericClass;

    public BaseElasticsearchDao() {
        Class<T> beanClass = (Class<T>) GenericTypeResolver.resolveTypeArgument(this.getClass(), BaseElasticsearchDao.class);
        this.genericClass = beanClass;

        ESDocument esDocument = AnnotationUtils.findAnnotation(beanClass, ESDocument.class);
        this.indexName = esDocument.indexName();

        Field[] declaredFields = beanClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            ESId esId = declaredField.getAnnotation(ESId.class);
            if (esId != null) {
                this.idField = declaredField;
                idField.setAccessible(true);
                break;
            }
        }
    }


    public void insert(List<T> list) {

        list.forEach(genericInstance -> {
            IndexRequest request = ElasticsearchUtils.buildIndexRequest(indexName, getIdValue(genericInstance), genericInstance);
            try {
                client.index(request, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
                log.error("elasticsearch insert error", e);
            }
        });

    }

    public void update(List<T> list) {
        list.forEach(genericInstance -> {
            Object idValue = ReflectionUtils.getField(idField, genericInstance);
            elasticsearchUtils.updateRequest(indexName, String.valueOf(idValue), genericInstance);
        });
    }

    public void delete(T genericInstance) {
        if (ObjectUtils.isEmpty(genericInstance)) {
            // 如果对象为空，则删除全量
            searchList().forEach(result -> {

                elasticsearchUtils.deleteRequest(indexName, getIdValue(genericInstance));
            });
        }
        elasticsearchUtils.deleteRequest(indexName, getIdValue(genericInstance));
    }

    public List<T> searchList() {
        SearchResponse searchResponse = elasticsearchUtils.search(indexName);
        SearchHit[] hits = searchResponse.getHits().getHits();
        List<T> genericInstanceList = new ArrayList<>();
        Arrays.stream(hits).forEach(hit -> {
            String sourceAsString = hit.getSourceAsString();
            genericInstanceList.add(JSON.parseObject(sourceAsString, genericClass));
        });
        return genericInstanceList;
    }


    private String getIdValue(T genericInstance) {
        try {
            Object idValue = idField.get(genericInstance);
            return idValue.toString();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

}

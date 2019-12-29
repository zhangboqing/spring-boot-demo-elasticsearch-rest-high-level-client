package com.zbq.springbootelasticsearch.common.elasticsearch.base;

import com.alibaba.fastjson.JSON;
import com.zbq.springbootelasticsearch.common.elasticsearch.annotation.ESDocument;
import com.zbq.springbootelasticsearch.common.elasticsearch.annotation.ESId;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

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
    protected String indexName;
    /**
     * ID字段
     */
    protected Field idField;
    /**
     * T对应的类型Class
     */
    protected Class<T> genericClass;

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


    /**
     * 保存或更新文档数据
     * @param list 文档数据集合
     */
    public void saveOrUpdate(List<T> list) {

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

    /**
     * 删除操作
     * 当genericInstance在es中不存在时，调用该方法也不会报错
     * @param genericInstance 被删除的实例对象
     */
    public void delete(T genericInstance) {
        if (ObjectUtils.isEmpty(genericInstance)) {
            // 如果对象为空，则删除全量
            searchList().forEach(result -> {

                elasticsearchUtils.deleteRequest(indexName, getIdValue(genericInstance));
            });
        }
        elasticsearchUtils.deleteRequest(indexName, getIdValue(genericInstance));
    }

    /**
     * 搜索文档，根据指定的搜索条件
     * @param searchSourceBuilder
     * @return
     */
    public List<T> search(SearchSourceBuilder searchSourceBuilder) {
        Assert.notNull(searchSourceBuilder,"searchSourceBuilder is null");
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (searchResponse == null) {
            return null;
        }

        SearchHit[] hits = searchResponse.getHits().getHits();
        List<T> genericInstanceList = new ArrayList<>();
        Arrays.stream(hits).forEach(hit -> {
            String sourceAsString = hit.getSourceAsString();
            genericInstanceList.add(JSON.parseObject(sourceAsString, genericClass));
        });
        return genericInstanceList;

    }

    /**
     * 分页搜索文档，根据指定的搜索条件
     * @param searchSourceBuilder
     * @param pageNo    页码
     * @param pageSize  每页大小
     * @return
     */
    public List<T> search(SearchSourceBuilder searchSourceBuilder,int pageNo,int pageSize) {
        Assert.notNull(searchSourceBuilder,"searchSourceBuilder is null");
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);

        if (pageNo > 0 && pageSize > 0) {
            searchSourceBuilder.from(pageNo);
            searchSourceBuilder.size(pageSize);
        }

        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (searchResponse == null) {
            return null;
        }

        SearchHit[] hits = searchResponse.getHits().getHits();
        List<T> genericInstanceList = new ArrayList<>();
        Arrays.stream(hits).forEach(hit -> {
            String sourceAsString = hit.getSourceAsString();
            genericInstanceList.add(JSON.parseObject(sourceAsString, genericClass));
        });
        return genericInstanceList;

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

    /**
     * ============================================================================================================
     *                                                  私有方法
     * ============================================================================================================
     * */

    /**
     * 获取当前操作的genericInstance的主键ID
     * @param genericInstance 实例对象
     * @return 返回主键ID值
     */
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

package com.zbq.springbootelasticsearch.common.elasticsearch.base;

import com.zbq.springbootelasticsearch.common.elasticsearch.annotation.ESDocument;
import com.zbq.springbootelasticsearch.common.elasticsearch.annotation.ESId;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.lang.reflect.Field;
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
    protected JestClient client;

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
     *
     * @param list 文档数据集合
     */
    public void saveOrUpdate(List<T> list) {

        list.forEach(genericInstance -> {
            Index index = new Index.Builder(genericInstance).index(indexName).type(indexName).id(getIdValue(genericInstance)).build();
            try {
                JestResult result = client.execute(index);
                log.info(" saveOrUpdate responseCcode: {},errorMessage: {}", result.getResponseCode(),result.getErrorMessage());
            } catch (IOException e) {
                e.printStackTrace();
                log.error("elasticsearch insert error", e);
            }
        });

    }

    /**
     * 删除操作
     * 当genericInstance在es中不存在时，调用该方法也不会报错
     *
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
     *
     * @param searchSourceBuilder
     * @return
     */
    public List<T> search(SearchSourceBuilder searchSourceBuilder) {
        ESPageResult search = search(searchSourceBuilder, null, null);
        return search != null ? search.getResults() : null;

    }

    /**
     * 分页排序搜索文档，根据指定的搜索条件
     *
     * @param searchSourceBuilder
     * @param esPageRequest       分页
     * @param esSort              排序
     * @return
     */
    public ESPageResult<T> search(SearchSourceBuilder searchSourceBuilder, ESPageRequest esPageRequest, ESSort esSort) {

        // 搜索
        Assert.notNull(searchSourceBuilder, "searchSourceBuilder is null");
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);

        // 分页
        if (esPageRequest != null) {
            searchSourceBuilder.from(esPageRequest.getPageNo());
            searchSourceBuilder.size(esPageRequest.getSize());
        }

        // 排序
        if (esSort != null) {
            List<ESSort.ESOrder> orders = esSort.orders;
            if (!CollectionUtils.isEmpty(orders)) {
                orders.forEach(esOrder -> searchSourceBuilder.sort(esOrder.getProperty(), esOrder.getDirection()));
            }
        }

        Search search = new Search.Builder(searchSourceBuilder.toString())
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
        if (result == null) {
            return null;
        }

        List<T> genericInstanceList = result.getSourceAsObjectList(genericClass, false);


        ESPageResult<T> pageResult = new ESPageResult<>(
                result.getTotal(),
                esPageRequest != null ? esPageRequest.getPageNo() : -1,
                esPageRequest != null ? esPageRequest.getSize() : -1,
                genericInstanceList);
        return pageResult;

    }




    /**
     * ============================================================================================================
     *                                                  私有方法
     * ============================================================================================================
     * */

    private List<T> searchList() {
        SearchResult searchResult = elasticsearchUtils.search(indexName);
        List<T> genericInstanceList = searchResult.getSourceAsObjectList(genericClass, false);
        return genericInstanceList;
    }

    /**
     * 获取当前操作的genericInstance的主键ID
     *
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

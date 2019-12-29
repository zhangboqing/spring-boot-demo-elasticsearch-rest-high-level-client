package com.zbq.springbootelasticsearch.dao;

import com.zbq.springbootelasticsearch.common.elasticsearch.base.BaseElasticsearchDao;
import com.zbq.springbootelasticsearch.model.GoodsESEntity;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhangboqing
 * @date 2019/12/10
 */
@Component
public class GoodsESDao<T extends GoodsESEntity> extends BaseElasticsearchDao<GoodsESEntity> {


    /**
     * 全文搜索查询
     * @param queryName
     * @return
     */
    public List<GoodsESEntity> findListByGroupData(String queryName) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("groupData",queryName));
        List<GoodsESEntity> search = search(searchSourceBuilder);
        return search;
    }
}

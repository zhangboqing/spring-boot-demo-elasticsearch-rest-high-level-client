package com.zbq.springbootelasticsearch.dao;

import com.alibaba.fastjson.JSON;
import com.zbq.springbootelasticsearch.dao.base.BaseElasticsearchDao;
import com.zbq.springbootelasticsearch.model.GoodsESEntity;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhangboqing
 * @date 2019/12/10
 */
@Component
public class GoodsESDao<T extends GoodsESEntity> extends BaseElasticsearchDao<GoodsESEntity> {


    public List<GoodsESEntity> findListByGroupData(String queryName) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("groupData",queryName));
        List<GoodsESEntity> search = search(searchSourceBuilder);
        return search;
    }
}

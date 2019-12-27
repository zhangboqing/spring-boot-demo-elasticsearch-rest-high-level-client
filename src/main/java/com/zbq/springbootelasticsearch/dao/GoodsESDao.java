package com.zbq.springbootelasticsearch.dao;

import com.zbq.springbootelasticsearch.dao.base.BaseElasticsearchDao;
import com.zbq.springbootelasticsearch.model.GoodsESEntity;
import org.springframework.stereotype.Component;

/**
 * @author zhangboqing
 * @date 2019/12/10
 */
@Component
public class GoodsESDao<T extends GoodsESEntity> extends BaseElasticsearchDao<GoodsESEntity> {


}

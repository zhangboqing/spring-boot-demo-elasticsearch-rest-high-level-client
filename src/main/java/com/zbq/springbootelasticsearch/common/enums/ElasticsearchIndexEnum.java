package com.zbq.springbootelasticsearch.common.enums;

/**
 * @author zhangboqing
 * @date 2019/12/10
 */
public enum  ElasticsearchIndexEnum {
    /** 商品 */
    GOODS("goods");

    ElasticsearchIndexEnum(String indexName) {
        this.indexName = indexName;
    }

    private String indexName;

    public String getIndexName() {
        return indexName;
    }

}

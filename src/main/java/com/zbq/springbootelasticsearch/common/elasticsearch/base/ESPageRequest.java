package com.zbq.springbootelasticsearch.common.elasticsearch.base;

import lombok.Data;

/**
 * 分页
 * @author zhangboqing
 * @date 2019/12/29
 */
@Data
public class ESPageRequest {

    private final int page;
    private final int size;

    public ESPageRequest(int page, int size) {

        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero!");
        }

        if (size < 1) {
            throw new IllegalArgumentException("Page size must not be less than one!");
        }

        this.page = page;
        this.size = size;
    }


}


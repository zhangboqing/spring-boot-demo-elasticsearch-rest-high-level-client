package com.zbq.springbootelasticsearch.common.elasticsearch.base;

import lombok.Builder;
import lombok.Data;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author zhangboqing
 * @Date 2019/12/29
 * 封装排序参数
 */
public class ESSort {

    public final List<ESOrder> orders;
    public ESSort() {
        orders = new ArrayList<>();
    }

    public ESSort(SortOrder direction, String property) {
        orders = new ArrayList<>();
        add(direction,property);
    }



    /**
     * 追加排序字段
     * @param direction  排序方向
     * @param property  排序字段
     * @return
     */
    public ESSort add(SortOrder direction, String property) {

        Assert.notNull(direction, "direction must not be null!");
        Assert.hasText(property, "fieldName must not be empty!");

        orders.add(ESOrder.builder().direction(direction).property(property).build());
        return this;
    }


    @Builder
    @Data
    public static class ESOrder implements Serializable {

        private final SortOrder direction;
        private final String property;

    }

}

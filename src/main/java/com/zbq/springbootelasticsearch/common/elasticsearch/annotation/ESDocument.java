package com.zbq.springbootelasticsearch.common.elasticsearch.annotation;


import java.lang.annotation.*;

/**
 * @author zhangboqing
 * @date 2019/12/12
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ESDocument {

    /**
     * Name of the Elasticsearch index.
     * <ul>
     * <li>Lowercase only</li>
     * <li><Cannot include \, /, *, ?, ", <, >, |, ` ` (space character), ,, #/li>
     * <li>Cannot start with -, _, +</li>
     * <li>Cannot be . or ..</li>
     * <li>Cannot be longer than 255 bytes (note it is bytes, so multi-byte characters will count towards the 255 limit
     * faster)</li>
     * </ul>
     */
    String indexName();

    /**
     * Number of shards for the index {@link #indexName()}. Used for index creation.
     */
    int shards() default 0;

    /**
     * Number of replicas for the index {@link #indexName()}. Used for index creation.
     */
    int replicas() default 0;
}

package com.zbq.springbootelasticsearch.common.elasticsearch.annotation;

import com.zbq.springbootelasticsearch.common.elasticsearch.enums.ESFieldType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author zhangboqing
 * @date 2019/12/12
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface ESField {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    ESFieldType type();

    String analyzer() default "";
}

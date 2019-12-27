package com.zbq.springbootelasticsearch.common.exception;


/**
 * @author zhangboqing
 * @date 2019/12/10
 */
public class ElasticsearchException extends ApiException {

    public ElasticsearchException(String message) {
        super(ApiResponseCode.FAILED,message);
    }
}

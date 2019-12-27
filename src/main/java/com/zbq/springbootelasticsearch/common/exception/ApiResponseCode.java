package com.zbq.springbootelasticsearch.common.exception;

public enum ApiResponseCode implements IApiResponseCode {
    SUCCESS(0, "成功"),
    FAILED(1, "失败");

    public Integer code;
    public String message;

    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    private ApiResponseCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public String toString() {
        return "ApiResponseCode(code=" + this.getCode() + ", message=" + this.getMessage() + ")";
    }
}
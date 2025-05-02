package org.ishareReading.bankai.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 基础响应
 */
@Data
public class BaseResponse<T> implements Serializable {

    /**
     * 业务数据
     */
    protected T data;
    /**
     * 错误码
     */
    private int code;
    /**
     * 错误信息
     */
    private String msg;
    /**
     * 是否成功
     */
    private boolean success;

    public BaseResponse() {
    }

    public BaseResponse(int code, String msg) {
        this(code, msg, null);
    }

    public BaseResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.success = ResponseCode.SUCCESS.getCode() == code;
    }

    public BaseResponse(T data) {
        this(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), data);
    }

}

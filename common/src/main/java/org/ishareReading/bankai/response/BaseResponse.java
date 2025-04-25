package org.ishareReading.bankai.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.ishareReading.bankai.exception.BusinessException;

import java.io.Serializable;

/**
 * 基础响应
 *
 */
@Data
public class BaseResponse<T> implements Serializable {

    /**
     * 错误码
     */
    private int code;

    /**
     * 错误信息
     */
    private String msg;

    /**
     * 业务数据
     */
    protected T data;

    /**
     * 是否成功
     */
    private boolean success;

    public BaseResponse() {
    }

    public BaseResponse(int code, String msg) {
        this(code, msg, null);
    }

    public BaseResponse(T data) {
        this(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), data);
    }

    public BaseResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.success = ResponseCode.SUCCESS.getCode() == code;
    }

    /**
     * 获取响应业务数据,在响应失败的情况下抛出正确的异常。
     *
     * @return 响应业务数据
     */
    @JsonIgnore
    public T getDataExceptionThrowCheck() {
        this.checkUnsuccessExceptionThrown();
        return this.getData();
    }

    /**
     * 检查响应失败的情况下抛出正确的异常。
     */
    @JsonIgnore
    public void checkUnsuccessExceptionThrown() {
        if (!isSuccess()) {
            throw new BusinessException("错误码:" + this.getCode() + ",错误信息：" + this.msg);
        }
    }
}

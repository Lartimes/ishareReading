package org.ishareReading.bankai.response;

import lombok.Getter;

/**
 * Result Error Code.
 */
@Getter
public enum ResponseCode implements ErrorCode {

    /**
     *  成功.
     */
    SUCCESS(200, "操作成功"),

    /**
     *  业务异常.
     */
    FAILURE(400, "业务异常"),

    /**
     *  server error.
     */
    SERVER_ERROR(500, "服务内部异常");

    private final int code;

    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}

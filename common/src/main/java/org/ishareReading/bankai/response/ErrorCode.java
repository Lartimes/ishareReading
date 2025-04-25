package org.ishareReading.bankai.response;

import java.io.Serializable;

public interface ErrorCode extends Serializable {

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    int getCode();

    /**
     * 获取错误消息
     *
     * @return 错误消息
     */
    String getMessage();

}

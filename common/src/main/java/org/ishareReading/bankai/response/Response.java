package org.ishareReading.bankai.response;

/**
 * 通用响应
 *
 */
public class Response<T> extends BaseResponse<T> {

    public Response() {
        super();
    }

    public Response(int code, String msg) {
        super(code, msg);
    }

    public Response(T data) {
        super(data);
    }

    public Response(int code, String msg, T data) {
        super(code, msg, data);
    }

    /**
     * 成功
     *
     * @param <T> 数据泛型
     * @return 响应对象
     */
    public static <T> Response<T> success() {
        return success(ResponseCode.SUCCESS.getMessage(), null);
    }

    /**
     * 成功
     *
     * @param data 响应数据
     * @param <T>  数据泛型
     * @return 响应对象
     */
    public static <T> Response<T> success(T data) {
        return success(ResponseCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功
     *
     * @param msg  消息
     * @param data 响应数据
     * @param <T>  数据泛型
     * @return 响应对象
     */
    public static <T> Response<T> success(String msg, T data) {
        return new Response<>(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    /**
     * 失败
     *
     * @param <T> 响应数据
     * @return 响应对象
     */
    public static <T> Response<T> fail() {
        return fail(ResponseCode.FAILURE.getMessage());
    }

    /**
     * 失败
     *
     * @param msg 消息
     * @param <T> 数据泛型
     * @return 响应对象
     */
    public static <T> Response<T> fail(String msg) {
        return fail(ResponseCode.FAILURE.getCode(), msg);
    }

    /**
     * 失败
     *
     * @param code 错误码
     * @param msg  错误消息
     * @param <T>  数据泛型
     * @return 响应对象
     */
    public static <T> Response<T> fail(int code, String msg) {
        return fail(code, msg, null);
    }

    /**
     * 失败
     *
     * @param code 错误码
     * @param msg  错误消息
     * @param data 响应数据
     * @param <T>  数据泛型
     * @return 响应对象
     */
    public static <T> Response<T> fail(int code, String msg, T data) {
        return new Response<>(code, msg, data);
    }

    /**
     * 失败
     *
     * @param errorCode 错误码
     * @param <T>       数据泛型
     * @return 响应对象
     */
    public static <T> Response<T> fail(ErrorCode errorCode) {
        return fail(errorCode.getCode(), errorCode.getMessage());
    }


    /**
     * 失败
     *
     * @param errorCode 错误码
     * @param data      响应数据
     * @param <T>       数据泛型
     * @return 响应对象
     */
    public static <T> Response<T> fail(ErrorCode errorCode, T data) {
        return new Response<>(errorCode.getCode(), errorCode.getMessage(), data);
    }

    /**
     * 状态
     *
     * @param flag 是否成功
     * @param <T>  数据泛型
     * @return 响应对象
     */
    public static <T> Response<T> status(boolean flag) {
        return flag ? success() : fail();
    }
}

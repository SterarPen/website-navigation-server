package com.starer.website_navigation_server.util;

/**
 * 用于保存Service层返回业务处理结果，返回给Controller层
 * 失败时，只会包含code、message字段以及为false值的success字段
 * 成功时，会包含所有字段，并success为true
 * @param <T> 当成功时，携带的数据类型
 */
public class ServiceResult<T> {

    // 状态码
    private final int code;
    // Service层业务处理是否成功
    private final boolean success;
    // 业务处理结果信息，例如，失败时具体失败原因
    private final String message;
    // 一般为业务处理成功后，返回到Controller层的数据
    private final T data;


    private ServiceResult(int code, boolean success, String message, T data) {
        this.code = code;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * Service层业务失败时的结果对象
     * @param code 状态码
     * @param message 失败信息
     * @return 失败时ServiceResult对象
     * @param <T>
     */
    public static <T> ServiceResult<T> createFactory(int code, String message) {
        return new ServiceResult<>(code, false, message, null);
    }

    /**
     * Service层业务成功时结果对象
     * @param code 状态码
     * @param message 成功信息
     * @param data 返回Controller层所携带的数据，一般是查询操作获取的数据
     * @return 成功时ServiceResult对象
     * @param <T> 携带的数据的类型
     */
    public static <T> ServiceResult<T> createFactory(int code, String message, T data) {
        return new ServiceResult<>(code, true, message, data);
    }

    public int getCode() {
        return code;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ServiceResult{" +
                "code=" + code +
                ", success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}

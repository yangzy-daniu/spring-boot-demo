package com.example.demo.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class Result<T> implements Serializable {
    private boolean success;
    private String message;
    private Object data;
    private Integer code;
    private Long timestamp;

//    public static Result success() {
//        Result result = new Result();
//        result.setSuccess(true);
//        return result;
//    }
//
//    public static Result success(Object data) {
//        Result result = new Result();
//        result.setSuccess(true);
//        result.setData(data);
//        return result;
//    }
//
//    public static Result error(String message) {
//        Result result = new Result();
//        result.setSuccess(false);
//        result.setMessage(message);
//        return result;
//    }

    public Result data(String key, Object value) {
        if (this.data == null) {
            this.data = new HashMap<String, Object>();
        }
        if (this.data instanceof Map) {
            ((Map<String, Object>) this.data).put(key, value);
        }
        return this;
    }

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
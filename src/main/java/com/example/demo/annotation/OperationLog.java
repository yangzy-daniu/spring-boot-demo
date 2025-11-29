package com.example.demo.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 用于标记需要记录操作日志的方法
 */
@Target(ElementType.METHOD)  // 注解可以用在方法上
@Retention(RetentionPolicy.RUNTIME)  // 注解在运行时有效
@Documented  // 包含在JavaDoc中
public @interface OperationLog {
    /**
     * 模块名称
     */
    String module() default "";

    /**
     * 操作类型
     */
    String type() default "";

    /**
     * 操作描述
     */
    String value() default "";
}
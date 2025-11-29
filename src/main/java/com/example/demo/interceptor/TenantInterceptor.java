package com.example.demo.interceptor;

import com.example.demo.common.context.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// 拦截器：在每个请求开始时设置租户信息
@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头、JWT、子域名等获取租户ID
        Long tenantId = extractTenantIdFromRequest(request);
        TenantContext.setTenantId(tenantId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        TenantContext.clear();
    }

    private Long extractTenantIdFromRequest(HttpServletRequest request) {
        // 实现租户ID提取逻辑
        String tenantId = request.getHeader("X-Tenant-ID");
        return tenantId != null ? Long.valueOf(tenantId) : 1L; // 默认租户
    }
}
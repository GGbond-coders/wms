package com.wms.service;

public interface AuditLogService {
    void log(String operation, String content);
}


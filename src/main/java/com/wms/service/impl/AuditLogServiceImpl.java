package com.wms.service.impl;

import com.wms.mapper.OperationLogMapper;
import com.wms.pojo.OperationLog;
import com.wms.pojo.User;
import com.wms.security.UserContext;
import com.wms.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public void log(String operation, String content) {
        User user = UserContext.get();
        String username = user != null ? user.getUsername() : null;

        OperationLog log = new OperationLog();
        log.setUsername(username);
        log.setOperation(operation);
        log.setContent(content);
        operationLogMapper.insert(log);
    }
}


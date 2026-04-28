package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.mapper.OperationLogMapper;
import com.wms.pojo.OperationLog;
import com.wms.service.OperationLogService;
import com.wms.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public PageVO<OperationLog> getLogList(String username, String type, Integer page, Integer pageSize) {
        QueryWrapper<OperationLog> wrapper = new QueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            wrapper.eq("username", username);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq("operation", type);
        }
        wrapper.orderByDesc("create_time");
        Page<OperationLog> pageResult = operationLogMapper.selectPage(new Page<>(page, pageSize), wrapper);
        PageVO<OperationLog> pageVO = new PageVO<>();
        pageVO.setTotal(pageResult.getTotal());
        pageVO.setRows(pageResult.getRecords());
        return pageVO;
    }

    @Override
    public boolean addLog(String username, String operation, String content) {
        OperationLog log = new OperationLog();
        log.setUsername(username);
        log.setOperation(operation);
        log.setContent(content);
        return operationLogMapper.insert(log) > 0;
    }
}
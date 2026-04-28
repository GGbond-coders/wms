package com.wms.service;

import com.wms.pojo.OperationLog;
import com.wms.vo.PageVO;

public interface OperationLogService {
    PageVO<OperationLog> getLogList(String username, String type, Integer page, Integer pageSize);

    boolean addLog(String username, String operation, String content);
}
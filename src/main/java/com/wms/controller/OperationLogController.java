package com.wms.controller;

import com.wms.pojo.OperationLog;
import com.wms.service.OperationLogService;
import com.wms.vo.PageVO;
import com.wms.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logs")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping
    public ResultVO<PageVO<OperationLog>> getLogList(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<OperationLog> pageVO = operationLogService.getLogList(username, type, page, pageSize);
        return ResultVO.success(pageVO);
    }

    @PostMapping
    public ResultVO<Void> addLog(@RequestParam String username, @RequestParam String operation, @RequestParam String content) {
        boolean result = operationLogService.addLog(username, operation, content);
        return result ? ResultVO.success() : ResultVO.error("记录日志失败");
    }
}
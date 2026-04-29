package com.wms.controller;

import com.wms.pojo.Inbound;
import com.wms.service.InboundService;
import com.wms.vo.PageVO;
import com.wms.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inbound")
public class InboundController {

    @Autowired
    private InboundService inboundService;

    @GetMapping
    public ResultVO<PageVO<Inbound>> getInboundList(
            @RequestParam(required = false) String goodsName,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<Inbound> pageVO = inboundService.getInboundList(goodsName, page, pageSize);
        return ResultVO.success(pageVO);
    }

    @PostMapping
    public ResultVO<Void> addInbound(
            @RequestParam Long goodsId,
            @RequestParam Integer quantity,
            @RequestParam Long operatorId) {
        boolean result = inboundService.addInbound(goodsId, quantity, operatorId);
        return result ? ResultVO.success() : ResultVO.error("入库失败");
    }
}


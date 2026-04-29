package com.wms.controller;

import com.wms.pojo.Outbound;
import com.wms.service.OutboundService;
import com.wms.vo.PageVO;
import com.wms.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/outbound")
public class OutboundController {

    @Autowired
    private OutboundService outboundService;

    @GetMapping
    public ResultVO<PageVO<Outbound>> getOutboundList(
            @RequestParam(required = false) String goodsName,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<Outbound> pageVO = outboundService.getOutboundList(goodsName, page, pageSize);
        return ResultVO.success(pageVO);
    }

    @PostMapping
    public ResultVO<Void> addOutbound(
            @RequestParam Long goodsId,
            @RequestParam Integer quantity,
            @RequestParam Long operatorId,
            @RequestParam String receiver) {
        boolean result = outboundService.addOutbound(goodsId, quantity, operatorId, receiver);
        return result ? ResultVO.success() : ResultVO.error("outbound failed: insufficient stock");
    }
}

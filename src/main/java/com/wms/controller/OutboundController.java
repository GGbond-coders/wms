package com.wms.controller;

import com.wms.pojo.Outbound;
import com.wms.service.OutboundService;
import com.wms.vo.PageVO;
import com.wms.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public ResultVO<Void> addOutbound(@RequestParam Long goodsId, @RequestParam Integer quantity, @RequestParam String receiver) {
        boolean result = outboundService.addOutbound(goodsId, quantity, receiver);
        return result ? ResultVO.success() : ResultVO.error("出库失败，库存不足");
    }
}
package com.wms.controller;

import com.wms.pojo.Inbound;
import com.wms.service.InboundService;
import com.wms.vo.PageVO;
import com.wms.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public ResultVO<Void> addInbound(@RequestParam Long goodsId, @RequestParam Integer quantity, @RequestParam String operator) {
        boolean result = inboundService.addInbound(goodsId, quantity, operator);
        return result ? ResultVO.success() : ResultVO.error("入库失败");
    }
}
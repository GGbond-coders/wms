package com.wms.service;

import com.wms.pojo.Outbound;
import com.wms.vo.PageVO;

public interface OutboundService {
    PageVO<Outbound> getOutboundList(String goodsName, Integer page, Integer pageSize);

    boolean addOutbound(Long goodsId, Integer quantity, String receiver);
}
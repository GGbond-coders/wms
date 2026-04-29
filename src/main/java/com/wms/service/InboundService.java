package com.wms.service;

import com.wms.pojo.Inbound;
import com.wms.vo.PageVO;

public interface InboundService {
    PageVO<Inbound> getInboundList(String goodsName, Integer page, Integer pageSize);

    boolean addInbound(Long goodsId, Integer quantity, Long operatorId);
}

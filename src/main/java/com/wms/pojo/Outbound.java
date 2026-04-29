package com.wms.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("outbound")
public class Outbound {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long goodsId;
    private Integer quantity;
    private Long operatorId;
    private String receiver;
    private LocalDateTime createTime;
}


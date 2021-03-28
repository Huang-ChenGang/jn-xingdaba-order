package com.jn.xingdaba.order.api;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public final class QuoteBusTypeRequestData {
    /** 车型ID */
    @NotEmpty(message = "车型ID不能为空")
    private String busTypeId;

    /** 车型名称 **/
    @NotEmpty(message = "车型名称不能为空")
    private String busTypeName;

    /** 车辆数 */
    @NotNull(message = "车辆数不能为空")
    private Integer busQuantity;
}

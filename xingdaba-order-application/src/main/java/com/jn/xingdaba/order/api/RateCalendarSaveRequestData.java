package com.jn.xingdaba.order.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;

@Data
public final class RateCalendarSaveRequestData {
    @NotEmpty(message = "请选择车型")
    private List<RateCalendarBusType> busTypes;

    @NotNull(message = "请选择开始用车日期")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate beginUseDate;

    @NotNull(message = "请选择结束用车日期")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate endUseDate;

    @NotEmpty(message = "请选择星期")
    private List<Integer> weeks;

    @NotNull(message = "请填写库存")
    private Integer stock;

    @NotBlank(message = "请填写市率")
    @Pattern(regexp = "[+|-]?[0-9]*", message = "市率只能输入正整数、负整数或零")
    private String cityRatioDisplay;

    @NotBlank(message = "请填写省率")
    @Pattern(regexp = "[+|-]?[0-9]*", message = "省率只能输入正整数、负整数或零")
    private String provinceRatioDisplay;

    @NotBlank(message = "请选择是否紧缺")
    private String isLack;

    @Data
    public static class RateCalendarBusType {
        @NotBlank(message = "车型ID不能为空")
        private String id;
        @NotBlank(message = "车型ID不能为空")
        private String busTypeName;
    }

}

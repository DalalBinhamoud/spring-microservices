package com.productproject.orderservice.dto;


import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemsDto {
    private long id;
    private String skuCode;
    private BigDecimal price;
    private Integer quantity;
}

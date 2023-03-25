package com.productproject.orderservice.service;


import com.productproject.orderservice.dto.InventoryResponse;
import com.productproject.orderservice.dto.OrderLineItemsDto;
import com.productproject.orderservice.dto.OrderRequest;
import com.productproject.orderservice.model.Order;
import com.productproject.orderservice.model.OrderLineItems;
import com.productproject.orderservice.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class OrderService {

    private  final OrderRepository orderRepository;
    private  final WebClient webClient;

    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItemsList =  orderRequest.getOrderLineItemsDtoList().stream().map(this::mapToOrderLineItems).toList();
        order.setOrderLineItems(orderLineItemsList);

        List<String> skuCodes = order.getOrderLineItems().stream()
                .map(orderLineItems -> orderLineItems.getSkuCode())
                .toList();

        InventoryResponse[] inventoryResponses = webClient.get().uri("http://localhost:8082/api/inventory",uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                        .retrieve()
                                .bodyToMono(InventoryResponse[].class)
                                        .block();

        Boolean isInStock = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);

        if(isInStock){
            orderRepository.save(order);
        }else{
            throw  new IllegalArgumentException("out of stock");
        }

    }

    private OrderLineItems mapToOrderLineItems(OrderLineItemsDto orderLineItemsDto){
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        return orderLineItems;
    }


}

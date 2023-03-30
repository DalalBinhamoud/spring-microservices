package com.productproject.inventoryservice.service;

import com.productproject.inventoryservice.dto.InventoryResponse;
import com.productproject.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j

public class InventoryService {

    private  final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public  List<InventoryResponse> isInStock(List<String> skuCodes) throws InterruptedException {
//        log.info("wait started");
//        Thread.sleep(10000);
//        log.info("wait ended");
        return inventoryRepository.findBySkuCodeIn(skuCodes).stream().map(
                inventory-> InventoryResponse.builder().skuCode(inventory.getSkuCode())
                        .inStock(inventory.getQuantity() > 0)
                        .build()
        ).toList();
    }
}

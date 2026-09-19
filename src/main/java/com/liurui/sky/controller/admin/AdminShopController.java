package com.liurui.sky.controller.admin;

import com.liurui.sky.common.ApiResponse;
import com.liurui.sky.model.ShopStatus;
import com.liurui.sky.store.InMemoryStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/shop")
public class AdminShopController {

    private final InMemoryStore store;

    public AdminShopController(InMemoryStore store) {
        this.store = store;
    }

    @GetMapping("/status")
    public ApiResponse<ShopStatus> status() {
        return ApiResponse.success(new ShopStatus(store.shopOpen()));
    }

    @PutMapping("/status")
    public ApiResponse<Void> update(@RequestBody ShopStatus status) {
        store.updateShopOpen(Boolean.TRUE.equals(status.open()));
        return ApiResponse.success();
    }
}

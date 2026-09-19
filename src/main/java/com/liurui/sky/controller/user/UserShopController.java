package com.liurui.sky.controller.user;

import com.liurui.sky.common.ApiResponse;
import com.liurui.sky.model.ShopStatus;
import com.liurui.sky.store.InMemoryStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/shop")
public class UserShopController {

    private final InMemoryStore store;

    public UserShopController(InMemoryStore store) {
        this.store = store;
    }

    @GetMapping("/status")
    public ApiResponse<ShopStatus> status() {
        return ApiResponse.success(new ShopStatus(store.shopOpen()));
    }
}

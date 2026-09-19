package com.liurui.sky.controller.user;

import com.liurui.sky.common.ApiResponse;
import com.liurui.sky.model.Order;
import com.liurui.sky.model.OrderSubmitRequest;
import com.liurui.sky.store.InMemoryStore;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/order")
public class UserOrderController {

    private final InMemoryStore store;

    public UserOrderController(InMemoryStore store) {
        this.store = store;
    }

    @PostMapping("/submit")
    public ApiResponse<Order> submit(@Valid @RequestBody OrderSubmitRequest request) {
        return ApiResponse.success(store.submitOrder(request));
    }

    @GetMapping("/historyOrders")
    public ApiResponse<List<Order>> historyOrders() {
        return ApiResponse.success(store.listOrders());
    }
}

package com.liurui.sky.controller.admin;

import com.liurui.sky.common.ApiResponse;
import com.liurui.sky.model.Order;
import com.liurui.sky.store.InMemoryStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    private final InMemoryStore store;

    public AdminOrderController(InMemoryStore store) {
        this.store = store;
    }

    @GetMapping("/list")
    public ApiResponse<List<Order>> list() {
        return ApiResponse.success(store.listOrders());
    }

    @PutMapping("/{id}/status/{status}")
    public ApiResponse<Order> status(@PathVariable Long id, @PathVariable Integer status) {
        return ApiResponse.success(store.updateOrderStatus(id, status));
    }
}

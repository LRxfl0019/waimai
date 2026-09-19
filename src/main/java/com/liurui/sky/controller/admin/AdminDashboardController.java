package com.liurui.sky.controller.admin;

import com.liurui.sky.common.ApiResponse;
import com.liurui.sky.store.InMemoryStore;
import com.liurui.sky.store.InMemoryStore.DashboardStats;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    private final InMemoryStore store;

    public AdminDashboardController(InMemoryStore store) {
        this.store = store;
    }

    @GetMapping
    public ApiResponse<DashboardStats> stats() {
        return ApiResponse.success(store.dashboardStats());
    }
}

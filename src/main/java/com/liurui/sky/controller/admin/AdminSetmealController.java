package com.liurui.sky.controller.admin;

import com.liurui.sky.common.ApiResponse;
import com.liurui.sky.model.Setmeal;
import com.liurui.sky.store.InMemoryStore;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
public class AdminSetmealController {

    private final InMemoryStore store;

    public AdminSetmealController(InMemoryStore store) {
        this.store = store;
    }

    @GetMapping("/list")
    public ApiResponse<List<Setmeal>> list(@RequestParam(required = false) Long categoryId) {
        return ApiResponse.success(store.listSetmeals(categoryId, false));
    }

    @PostMapping
    public ApiResponse<Setmeal> save(@Valid @RequestBody Setmeal setmeal) {
        return ApiResponse.success(store.saveSetmeal(setmeal));
    }

    @PutMapping("/{id}/status/{enabled}")
    public ApiResponse<Void> status(@PathVariable Long id, @PathVariable Boolean enabled) {
        store.enableSetmeal(id, enabled);
        return ApiResponse.success();
    }
}

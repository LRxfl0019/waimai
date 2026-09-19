package com.liurui.sky.controller.admin;

import com.liurui.sky.common.ApiResponse;
import com.liurui.sky.model.Dish;
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
@RequestMapping("/admin/dish")
public class AdminDishController {

    private final InMemoryStore store;

    public AdminDishController(InMemoryStore store) {
        this.store = store;
    }

    @GetMapping("/list")
    public ApiResponse<List<Dish>> list(@RequestParam(required = false) Long categoryId) {
        return ApiResponse.success(store.listDishes(categoryId, false));
    }

    @PostMapping
    public ApiResponse<Dish> save(@Valid @RequestBody Dish dish) {
        return ApiResponse.success(store.saveDish(dish));
    }

    @PutMapping("/{id}/status/{enabled}")
    public ApiResponse<Void> status(@PathVariable Long id, @PathVariable Boolean enabled) {
        store.enableDish(id, enabled);
        return ApiResponse.success();
    }
}

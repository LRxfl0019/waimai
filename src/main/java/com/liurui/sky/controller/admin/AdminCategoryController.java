package com.liurui.sky.controller.admin;

import com.liurui.sky.common.ApiResponse;
import com.liurui.sky.model.Category;
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
@RequestMapping("/admin/category")
public class AdminCategoryController {

    private final InMemoryStore store;

    public AdminCategoryController(InMemoryStore store) {
        this.store = store;
    }

    @GetMapping("/list")
    public ApiResponse<List<Category>> list(@RequestParam(required = false) Integer type) {
        return ApiResponse.success(store.listCategories(type));
    }

    @PostMapping
    public ApiResponse<Category> save(@Valid @RequestBody Category category) {
        return ApiResponse.success(store.saveCategory(category));
    }

    @PutMapping("/{id}/status/{enabled}")
    public ApiResponse<Void> status(@PathVariable Long id, @PathVariable Boolean enabled) {
        store.enableCategory(id, enabled);
        return ApiResponse.success();
    }
}

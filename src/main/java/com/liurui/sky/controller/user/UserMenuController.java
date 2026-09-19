package com.liurui.sky.controller.user;

import com.liurui.sky.common.ApiResponse;
import com.liurui.sky.model.Category;
import com.liurui.sky.model.Dish;
import com.liurui.sky.model.Setmeal;
import com.liurui.sky.store.InMemoryStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserMenuController {

    private final InMemoryStore store;

    public UserMenuController(InMemoryStore store) {
        this.store = store;
    }

    @GetMapping("/user/category/list")
    public ApiResponse<List<Category>> categories(@RequestParam(required = false) Integer type) {
        return ApiResponse.success(store.listCategories(type).stream()
                .filter(category -> Boolean.TRUE.equals(category.getEnabled()))
                .toList());
    }

    @GetMapping("/user/dish/list")
    public ApiResponse<List<Dish>> dishes(@RequestParam(required = false) Long categoryId) {
        return ApiResponse.success(store.listDishes(categoryId, true));
    }

    @GetMapping("/user/setmeal/list")
    public ApiResponse<List<Setmeal>> setmeals(@RequestParam(required = false) Long categoryId) {
        return ApiResponse.success(store.listSetmeals(categoryId, true));
    }
}

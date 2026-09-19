package com.liurui.sky.controller.user;

import com.liurui.sky.common.ApiResponse;
import com.liurui.sky.model.CartItem;
import com.liurui.sky.store.InMemoryStore;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
public class UserCartController {

    private final InMemoryStore store;

    public UserCartController(InMemoryStore store) {
        this.store = store;
    }

    @GetMapping("/list")
    public ApiResponse<List<CartItem>> list() {
        return ApiResponse.success(store.listCart());
    }

    @PostMapping("/add")
    public ApiResponse<CartItem> add(@Valid @RequestBody CartItem item) {
        return ApiResponse.success(store.addCartItem(item));
    }

    @DeleteMapping("/clean")
    public ApiResponse<Void> clean() {
        store.clearCart();
        return ApiResponse.success();
    }
}

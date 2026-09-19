package com.liurui.sky.controller.user;

import com.liurui.sky.common.ApiResponse;
import com.liurui.sky.model.LoginRequest;
import com.liurui.sky.model.LoginResponse;
import com.liurui.sky.store.InMemoryStore;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/user")
public class UserAuthController {

    private final InMemoryStore store;

    public UserAuthController(InMemoryStore store) {
        this.store = store;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(store.loginCustomer(request.getUsername(), request.getPassword()));
    }
}

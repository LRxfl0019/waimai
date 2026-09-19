package com.liurui.sky.controller.user;

import com.liurui.sky.common.ApiResponse;
import com.liurui.sky.model.AddressBook;
import com.liurui.sky.store.InMemoryStore;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
public class UserAddressBookController {

    private final InMemoryStore store;

    public UserAddressBookController(InMemoryStore store) {
        this.store = store;
    }

    @GetMapping("/list")
    public ApiResponse<List<AddressBook>> list() {
        return ApiResponse.success(store.listAddresses());
    }

    @PostMapping
    public ApiResponse<AddressBook> save(@Valid @RequestBody AddressBook address) {
        return ApiResponse.success(store.saveAddress(address));
    }

    @PutMapping("/{id}/default")
    public ApiResponse<AddressBook> defaultAddress(@PathVariable Long id) {
        return ApiResponse.success(store.defaultAddress(id));
    }
}

package com.sareehub.controller;

import com.sareehub.dto.AddressRequest;
import com.sareehub.entity.Address;
import com.sareehub.entity.User;
import com.sareehub.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public List<Address> getAddresses(@AuthenticationPrincipal User user) {
        return addressService.getAddresses(user);
    }

    @PostMapping
    public Address addAddress(@AuthenticationPrincipal User user, @RequestBody AddressRequest request) {
        return addressService.addAddress(user, request);
    }

    @DeleteMapping("/{id}")
    public void deleteAddress(@AuthenticationPrincipal User user, @PathVariable Long id) {
        addressService.deleteAddress(user, id);
    }
}

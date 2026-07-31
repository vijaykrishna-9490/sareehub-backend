package com.sareehub.service;

import com.sareehub.dto.AddressRequest;
import com.sareehub.entity.Address;
import com.sareehub.entity.User;
import com.sareehub.exception.ApiException;
import com.sareehub.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public List<Address> getAddresses(User user) {
        return addressRepository.findByUser(user);
    }

    public Address addAddress(User user, AddressRequest request) {
        Address address = Address.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .line1(request.getLine1())
                .line2(request.getLine2())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .country(request.getCountry())
                .defaultAddress(request.isDefaultAddress())
                .build();
        return addressRepository.save(address);
    }

    public void deleteAddress(User user, Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ApiException("Address not found", HttpStatus.NOT_FOUND));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new ApiException("Not authorized", HttpStatus.FORBIDDEN);
        }
        addressRepository.delete(address);
    }
}

package com.sareehub.repository;

import com.sareehub.entity.Address;
import com.sareehub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);
}

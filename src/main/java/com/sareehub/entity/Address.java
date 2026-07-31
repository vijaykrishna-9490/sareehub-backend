package com.sareehub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String fullName;
    private String phone;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String pincode;
    private String country;

    @Builder.Default
    private boolean defaultAddress = false;
}

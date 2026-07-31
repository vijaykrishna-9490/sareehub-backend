package com.sareehub.service;

import com.sareehub.dto.CheckoutRequest;
import com.sareehub.entity.*;
import com.sareehub.exception.ApiException;
import com.sareehub.repository.AddressRepository;
import com.sareehub.repository.CartItemRepository;
import com.sareehub.repository.OrderRepository;
import com.sareehub.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CouponService couponService;

    private static final BigDecimal FREE_SHIPPING_THRESHOLD = BigDecimal.valueOf(999);
    private static final BigDecimal SHIPPING_CHARGE = BigDecimal.valueOf(79);

    @Transactional
    public Order checkout(User user, CheckoutRequest request) {
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new ApiException("Your cart is empty", HttpStatus.BAD_REQUEST);
        }

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ApiException("Address not found", HttpStatus.NOT_FOUND));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new ApiException("Not authorized", HttpStatus.FORBIDDEN);
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem ci : cartItems) {
            Product product = ci.getProduct();
            if (ci.getQuantity() > product.getStock()) {
                throw new ApiException("Insufficient stock for: " + product.getName(), HttpStatus.BAD_REQUEST);
            }
            BigDecimal effectivePrice = product.getPrice()
                    .subtract(product.getPrice().multiply(BigDecimal.valueOf(product.getDiscountPercent() / 100.0)));
            BigDecimal lineTotal = effectivePrice.multiply(BigDecimal.valueOf(ci.getQuantity()));
            subtotal = subtotal.add(lineTotal);

            orderItems.add(OrderItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .productImage(product.getImages().isEmpty() ? null : product.getImages().get(0).getUrl())
                    .price(effectivePrice)
                    .quantity(ci.getQuantity())
                    .build());
        }

        BigDecimal discount = BigDecimal.ZERO;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            Coupon coupon = couponService.validate(request.getCouponCode(), subtotal.doubleValue());
            discount = BigDecimal.valueOf(couponService.calculateDiscount(coupon, subtotal.doubleValue()));
        }

        BigDecimal afterDiscount = subtotal.subtract(discount);
        BigDecimal shipping = afterDiscount.compareTo(FREE_SHIPPING_THRESHOLD) >= 0 ? BigDecimal.ZERO : SHIPPING_CHARGE;
        BigDecimal total = afterDiscount.add(shipping);

        Order order = Order.builder()
                .orderNumber("SH" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(user)
                .shippingAddress(ShippingAddressSnapshot.builder()
                        .fullName(address.getFullName())
                        .phone(address.getPhone())
                        .line1(address.getLine1())
                        .line2(address.getLine2())
                        .city(address.getCity())
                        .state(address.getState())
                        .pincode(address.getPincode())
                        .country(address.getCountry())
                        .build())
                .subtotal(subtotal)
                .discount(discount)
                .shippingCharge(shipping)
                .totalAmount(total)
                .couponCode(request.getCouponCode())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(request.getPaymentMethod() == PaymentMethod.COD ? PaymentStatus.PENDING : PaymentStatus.PENDING)
                .status(OrderStatus.PLACED)
                .build();

        orderItems.forEach(oi -> oi.setOrder(order));
        order.setItems(orderItems);

        // reduce stock
        for (CartItem ci : cartItems) {
            Product p = ci.getProduct();
            p.setStock(p.getStock() - ci.getQuantity());
            productRepository.save(p);
        }

        Order saved = orderRepository.save(order);
        cartItemRepository.deleteByUser(user);
        return saved;
    }

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ApiException("Order not found", HttpStatus.NOT_FOUND));
    }

    public Order cancelOrder(User user, Long id) {
        Order order = getOrder(id);
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ApiException("Not authorized", HttpStatus.FORBIDDEN);
        }
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.SHIPPED) {
            throw new ApiException("Order cannot be cancelled at this stage", HttpStatus.BAD_REQUEST);
        }
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateStatus(Long id, OrderStatus status) {
        Order order = getOrder(id);
        order.setStatus(status);
        if (status == OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
            order.setPaymentStatus(PaymentStatus.PAID);
        }
        return orderRepository.save(order);
    }
}

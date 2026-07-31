package com.sareehub.service;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.sareehub.entity.Order;
import com.sareehub.entity.OrderStatus;
import com.sareehub.entity.PaymentStatus;
import com.sareehub.entity.User;
import com.sareehub.exception.ApiException;
import com.sareehub.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RazorpayService {

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    private final OrderRepository orderRepository;

    /**
     * Creates a Razorpay order for an existing internal order and returns
     * everything the frontend needs to open the Razorpay checkout popup.
     * Only the order's OWNER may initiate payment for it.
     */
    @Transactional
    public Map<String, Object> createRazorpayOrder(User user, Long internalOrderId) {
        Order order = getOwnedOrder(user, internalOrderId);

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new ApiException("This order has already been paid for", HttpStatus.BAD_REQUEST);
        }

        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);

            // Razorpay expects the amount in the smallest currency unit (paise for INR)
            int amountInPaise = order.getTotalAmount()
                    .multiply(BigDecimal.valueOf(100))
                    .intValue();

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", order.getOrderNumber());
            orderRequest.put("payment_capture", 1);

            com.razorpay.Order rzpOrder = client.orders.create(orderRequest);

            order.setRazorpayOrderId(rzpOrder.get("id"));
            orderRepository.save(order);

            Map<String, Object> response = new HashMap<>();
            response.put("razorpayOrderId", rzpOrder.get("id"));
            response.put("amount", amountInPaise);
            response.put("currency", "INR");
            response.put("keyId", keyId); // safe to expose — this is the PUBLIC key, never the secret
            response.put("internalOrderId", order.getId());
            response.put("orderNumber", order.getOrderNumber());
            return response;

        } catch (RazorpayException e) {
            throw new ApiException("Could not initiate payment: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Verifies the signature Razorpay returns after a successful checkout.
     * This is the step that actually proves the payment is genuine —
     * never trust a "payment successful" message from the frontend alone.
     */
    @Transactional
    public Order verifyPayment(User user, Long internalOrderId, String razorpayOrderId,
                                String razorpayPaymentId, String razorpaySignature) {
        Order order = getOwnedOrder(user, internalOrderId);

        if (order.getRazorpayOrderId() == null || !order.getRazorpayOrderId().equals(razorpayOrderId)) {
            throw new ApiException("Order mismatch — payment does not belong to this order", HttpStatus.BAD_REQUEST);
        }

        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", razorpayOrderId);
            options.put("razorpay_payment_id", razorpayPaymentId);
            options.put("razorpay_signature", razorpaySignature);

            boolean isValid = Utils.verifyPaymentSignature(options, keySecret);

            if (!isValid) {
                throw new ApiException("Payment signature verification failed — payment rejected", HttpStatus.BAD_REQUEST);
            }

            order.setRazorpayPaymentId(razorpayPaymentId);
            order.setPaymentStatus(PaymentStatus.PAID);
            if (order.getStatus() == OrderStatus.PLACED) {
                order.setStatus(OrderStatus.CONFIRMED);
            }
            return orderRepository.save(order);

        } catch (RazorpayException e) {
            throw new ApiException("Signature verification error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private Order getOwnedOrder(User user, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException("Order not found", HttpStatus.NOT_FOUND));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ApiException("Not authorized to act on this order", HttpStatus.FORBIDDEN);
        }
        return order;
    }
}

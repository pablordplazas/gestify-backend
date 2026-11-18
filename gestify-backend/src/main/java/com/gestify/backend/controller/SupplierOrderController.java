package com.gestify.backend.controller;

import com.gestify.backend.dto.request.SupplierOrderRequest;
import com.gestify.backend.dto.response.SupplierOrderResponse;
import com.gestify.backend.security.AuthUser;
import com.gestify.backend.service.SupplierOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/supplier-orders")
@RequiredArgsConstructor
public class SupplierOrderController {

    private final SupplierOrderService orderService;

    @GetMapping
    public ResponseEntity<?> getOrders(@AuthenticationPrincipal AuthUser user) {
        return ResponseEntity.ok(orderService.getOrdersByBusiness(user.businessId()));
    }

    @PostMapping
    public ResponseEntity<SupplierOrderResponse> createOrder(
            @AuthenticationPrincipal AuthUser user,
            @Valid @RequestBody SupplierOrderRequest request
    ) {
        return ResponseEntity.ok(orderService.createOrder(user.businessId(), request));
    }

    @PatchMapping("/{id}/receive")
    public ResponseEntity<SupplierOrderResponse> receiveOrder(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(orderService.markAsReceived(user.businessId(), id));
    }
}
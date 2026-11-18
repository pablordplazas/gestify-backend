package com.gestify.backend.controller;

import com.gestify.backend.dto.request.SupplierRequest;
import com.gestify.backend.dto.response.SupplierResponse;
import com.gestify.backend.security.AuthUser;
import com.gestify.backend.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public ResponseEntity<?> getSuppliers(@AuthenticationPrincipal AuthUser user) {
        return ResponseEntity.ok(supplierService.getSuppliersByBusiness(user.businessId()));
    }

    @PostMapping
    public ResponseEntity<SupplierResponse> createSupplier(
            @AuthenticationPrincipal AuthUser user,
            @Valid @RequestBody SupplierRequest request
    ) {
        return ResponseEntity.ok(supplierService.createSupplier(user.businessId(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponse> updateSupplier(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequest request
    ) {
        return ResponseEntity.ok(supplierService.updateSupplier(user.businessId(), id, request));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<SupplierResponse> toggleSupplier(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(supplierService.toggleSupplierStatus(user.businessId(), id));
    }
}
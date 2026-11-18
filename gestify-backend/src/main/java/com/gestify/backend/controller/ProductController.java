package com.gestify.backend.controller;

import com.gestify.backend.dto.request.ProductRequest;
import com.gestify.backend.dto.response.ProductResponse;
import com.gestify.backend.security.AuthUser;
import com.gestify.backend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts(@AuthenticationPrincipal AuthUser user) {
        return ResponseEntity.ok(productService.getAllByBusiness(user.businessId()));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @AuthenticationPrincipal AuthUser user,
            @Valid @RequestBody ProductRequest request
    ) {
        return ResponseEntity.ok(productService.createProduct(user.businessId(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {
        return ResponseEntity.ok(productService.updateProduct(user.businessId(), id, request));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ProductResponse> toggleProduct(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(productService.toggleProductStatus(user.businessId(), id));
    }
}
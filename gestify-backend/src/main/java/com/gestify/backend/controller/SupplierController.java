package com.gestify.backend.controller;

import com.gestify.backend.dto.request.SupplierRequest;
import com.gestify.backend.dto.response.SupplierResponse;
import com.gestify.backend.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public ResponseEntity<List<SupplierResponse>> getSuppliers(@RequestParam("id_negocio") Long idNegocio) {
        return ResponseEntity.ok(supplierService.getSuppliersByBusiness(idNegocio));
    }

    @PostMapping
    public ResponseEntity<SupplierResponse> createSupplier(@Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.ok(supplierService.createSupplier(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponse> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequest request
    ) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, request));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<SupplierResponse> toggleSupplier(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.toggleSupplierStatus(id));
    }
}
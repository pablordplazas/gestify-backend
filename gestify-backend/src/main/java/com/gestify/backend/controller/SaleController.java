package com.gestify.backend.controller;

import com.gestify.backend.dto.request.SaleRequest;
import com.gestify.backend.dto.response.SaleResponse;
import com.gestify.backend.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @GetMapping
    public ResponseEntity<List<SaleResponse>> getSales(@RequestParam("id_negocio") Long idNegocio) {
        return ResponseEntity.ok(saleService.getSalesByBusiness(idNegocio));
    }

    @PostMapping
    public ResponseEntity<SaleResponse> createSale(@Valid @RequestBody SaleRequest request) {
        return ResponseEntity.ok(saleService.registerSale(request));
    }
}
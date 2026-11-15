package com.gestify.backend.service;

import com.gestify.backend.dto.request.SaleRequest;
import com.gestify.backend.dto.response.SaleResponse;
import com.gestify.backend.model.Business;
import com.gestify.backend.model.Product;
import com.gestify.backend.model.Sale;
import com.gestify.backend.repository.BusinessRepository;
import com.gestify.backend.repository.ProductRepository;
import com.gestify.backend.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final BusinessRepository businessRepository;

    private static final ZoneId BOGOTA_ZONE = ZoneId.of("America/Bogota");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);

    @Transactional(readOnly = true)
    public List<SaleResponse> getSalesByBusiness(Long businessId) {
        return saleRepository.findByBusinessIdOrderByIdDesc(businessId)
                .stream()
                .map(SaleResponse::fromEntity)
                .toList();
    }

    @Transactional
    public SaleResponse registerSale(SaleRequest request) {
        Business business = businessRepository.findById(request.getIdNegocio())
                .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado."));

        Product product = productRepository.findByIdWithLock(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

        if (!product.getBusiness().getId().equals(business.getId())) {
            throw new IllegalArgumentException("El producto no pertenece a este negocio.");
        }
        if (!product.getIsActive()) {
            throw new IllegalArgumentException("No se puede vender un producto desactivado.");
        }
        if (product.getStock() < request.getQuantity()) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + product.getStock() + " unidades.");
        }

        // Descuento atómico de stock
        product.setStock(product.getStock() - request.getQuantity());
        productRepository.save(product);

        ZonedDateTime now = ZonedDateTime.now(BOGOTA_ZONE);
        double total = product.getPrice() * request.getQuantity();

        Sale sale = Sale.builder()
                .business(business)
                .product(product)
                .productName(product.getName())
                .quantity(request.getQuantity())
                .total(total)
                .date(now.format(DATE_FMT))
                .time(now.format(TIME_FMT))
                .build();

        return SaleResponse.fromEntity(saleRepository.save(sale));
    }
}
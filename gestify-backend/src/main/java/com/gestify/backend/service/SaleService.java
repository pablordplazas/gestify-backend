package com.gestify.backend.service;

import com.gestify.backend.dto.request.SaleRequest;
import com.gestify.backend.dto.response.SaleResponse;
import com.gestify.backend.exception.ResourceNotFoundException;
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

    private static final ZoneId BOGOTA_ZONE = ZoneId.of("America/Bogota");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final BusinessRepository businessRepository;

    @Transactional(readOnly = true)
    public List<SaleResponse> getSalesByBusiness(Long businessId) {
        return saleRepository.findByBusinessIdOrderByIdDesc(businessId)
                .stream()
                .map(SaleResponse::fromEntity)
                .toList();
    }

    @Transactional
    public SaleResponse registerSale(Long businessId, SaleRequest request) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Negocio no encontrado."));

        Product product = productRepository.findByIdAndBusinessIdWithLock(request.getProductId(), businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado."));

        if (!Boolean.TRUE.equals(product.getIsActive())) {
            throw new IllegalArgumentException("No puedes vender un producto inactivo.");
        }
        if (product.getStock() < request.getQuantity()) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + product.getStock() + " un.");
        }

        product.setStock(product.getStock() - request.getQuantity());
        productRepository.save(product);

        double unitPrice = product.getPrice();
        double total = unitPrice * request.getQuantity();
        ZonedDateTime now = ZonedDateTime.now(BOGOTA_ZONE);

        Sale sale = Sale.builder()
                .business(business)
                .product(product)
                .productName(product.getName())
                .quantity(request.getQuantity())
                .unitPrice(unitPrice)
                .total(total)
                .date(now.format(DATE_FORMATTER))
                .time(now.format(TIME_FORMATTER))
                .build();

        return SaleResponse.fromEntity(saleRepository.save(sale));
    }
}
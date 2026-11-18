package com.gestify.backend.service;

import com.gestify.backend.dto.request.SaleRequest;
import com.gestify.backend.dto.response.SaleResponse;
import com.gestify.backend.exception.ResourceNotFoundException;
import com.gestify.backend.model.Business;
import com.gestify.backend.model.Product;
import com.gestify.backend.model.Sale;
import com.gestify.backend.model.SaleLineItem;
import com.gestify.backend.repository.BusinessRepository;
import com.gestify.backend.repository.ProductRepository;
import com.gestify.backend.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

        // Agrupar cantidades por productId para evitar duplicados en el carrito
        Map<Long, Integer> requestedQuantities = new LinkedHashMap<>();
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (SaleRequest.Item item : request.getItems()) {
                if (item.getProductId() == null || item.getQuantity() == null || item.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Cada producto del carrito debe tener una cantidad válida.");
                }
                requestedQuantities.merge(item.getProductId(), item.getQuantity(), Integer::sum);
            }
        } else if (request.getProductId() != null && request.getQuantity() != null && request.getQuantity() > 0) {
            requestedQuantities.put(request.getProductId(), request.getQuantity());
        } else {
            throw new IllegalArgumentException("Debes agregar al menos un producto al carrito.");
        }

        ZonedDateTime now = ZonedDateTime.now(BOGOTA_ZONE);
        Sale sale = Sale.builder()
                .business(business)
                .date(now.format(DATE_FORMATTER))
                .time(now.format(TIME_FORMATTER))
                .customerName(cleanOptional(request.getCustomerName()))
                .customerDocument(cleanOptional(request.getCustomerDocument()))
                .customerPhone(cleanOptional(request.getCustomerPhone()))
                .customerEmail(cleanOptional(request.getCustomerEmail()))
                .build();

        List<SaleLineItem> lineItems = new ArrayList<>();
        double grandTotal = 0.0;
        int totalUnits = 0;
        Product firstProduct = null;

        for (Map.Entry<Long, Integer> entry : requestedQuantities.entrySet()) {
            Long productId = entry.getKey();
            int qty = entry.getValue();

            Product product = productRepository.findByIdAndBusinessIdWithLock(productId, businessId)
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado (ID " + productId + ")."));

            if (!Boolean.TRUE.equals(product.getIsActive())) {
                throw new IllegalArgumentException("El producto '" + product.getName() + "' está inactivo.");
            }
            if (product.getStock() < qty) {
                throw new IllegalArgumentException(
                        "Stock insuficiente para '" + product.getName() + "'. Disponible: " + product.getStock() + " un."
                );
            }

            product.setStock(product.getStock() - qty);
            productRepository.save(product);

            if (firstProduct == null) {
                firstProduct = product;
            }

            double unitPrice = product.getPrice();
            double subtotal = unitPrice * qty;
            grandTotal += subtotal;
            totalUnits += qty;

            lineItems.add(SaleLineItem.builder()
                    .sale(sale)
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(qty)
                    .unitPrice(unitPrice)
                    .subtotal(subtotal)
                    .build());
        }

        String summaryName = lineItems.size() == 1
                ? lineItems.get(0).getProductName()
                : lineItems.get(0).getProductName() + " + " + (lineItems.size() - 1) + " más";

        sale.setProduct(firstProduct);
        sale.setProductName(summaryName);
        sale.setQuantity(totalUnits);
        sale.setUnitPrice(lineItems.size() == 1 ? lineItems.get(0).getUnitPrice() : null);
        sale.setTotal(grandTotal);
        sale.setItems(lineItems);

        return SaleResponse.fromEntity(saleRepository.save(sale));
    }

    private String cleanOptional(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
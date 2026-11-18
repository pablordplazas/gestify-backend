package com.gestify.backend.service;

import com.gestify.backend.dto.request.SupplierOrderRequest;
import com.gestify.backend.dto.response.SupplierOrderResponse;
import com.gestify.backend.exception.ResourceNotFoundException;
import com.gestify.backend.model.*;
import com.gestify.backend.repository.BusinessRepository;
import com.gestify.backend.repository.ProductRepository;
import com.gestify.backend.repository.SupplierOrderRepository;
import com.gestify.backend.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierOrderService {

    private static final ZoneId BOGOTA_ZONE = ZoneId.of("America/Bogota");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

    private final SupplierOrderRepository orderRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final BusinessRepository businessRepository;

    @Transactional(readOnly = true)
    public List<SupplierOrderResponse> getOrdersByBusiness(Long businessId) {
        return orderRepository.findByBusinessIdOrderByIdDesc(businessId)
                .stream()
                .map(SupplierOrderResponse::fromEntity)
                .toList();
    }

    @Transactional
    public SupplierOrderResponse createOrder(Long businessId, SupplierOrderRequest request) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Negocio no encontrado."));

        Supplier supplier = supplierRepository.findByIdAndBusinessId(request.getSupplierId(), businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado."));

        double shipping = request.getShippingCost() != null ? request.getShippingCost() : 0.0;
        if (shipping < 0) {
            throw new IllegalArgumentException("El costo de envío no puede ser negativo.");
        }

        String nowStr = ZonedDateTime.now(BOGOTA_ZONE).format(DATE_FORMATTER);
        boolean receiveNow = Boolean.TRUE.equals(request.getReceiveImmediately());

        SupplierOrder order = SupplierOrder.builder()
                .business(business)
                .supplier(supplier)
                .supplierName(supplier.getName())
                .shippingCost(shipping)
                .status(receiveNow ? "RECIBIDO" : "PENDIENTE")
                .orderDate(nowStr)
                .receivedDate(receiveNow ? nowStr : null)
                .notes(request.getNotes() != null ? request.getNotes().trim() : null)
                .build();

        // Primero calculamos el total de unidades del pedido para prorratear el envío
        int grandTotalUnits = 0;
        for (SupplierOrderRequest.OrderItemRequest reqItem : request.getItems()) {
            int multiplier = tierMultiplier(reqItem.getPricingTier());
            grandTotalUnits += reqItem.getPackQuantity() * multiplier;
        }
        double shippingPerUnit = grandTotalUnits > 0 ? (shipping / grandTotalUnits) : 0.0;

        double subtotalProducts = 0.0;
        List<SupplierOrderItem> items = new ArrayList<>();

        for (SupplierOrderRequest.OrderItemRequest reqItem : request.getItems()) {
            Product product = productRepository.findByIdAndBusinessIdWithLock(reqItem.getProductId(), businessId)
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado (ID " + reqItem.getProductId() + ")."));

            String normalizedTier = normalizeTier(reqItem.getPricingTier());
            int multiplier = tierMultiplier(normalizedTier);
            int totalUnits = reqItem.getPackQuantity() * multiplier;
            double itemSubtotal = reqItem.getPackQuantity() * reqItem.getTierPrice();
            double baseUnitCost = reqItem.getTierPrice() / multiplier;
            double realUnitCost = baseUnitCost + shippingPerUnit;

            subtotalProducts += itemSubtotal;

            if (receiveNow) {
                product.setStock(product.getStock() + totalUnits);
                productRepository.save(product);
            }

            items.add(SupplierOrderItem.builder()
                    .order(order)
                    .productId(product.getId())
                    .productName(product.getName())
                    .pricingTier(normalizedTier)
                    .packQuantity(reqItem.getPackQuantity())
                    .totalUnits(totalUnits)
                    .tierPrice(reqItem.getTierPrice())
                    .realUnitCost(realUnitCost)
                    .subtotal(itemSubtotal)
                    .build());
        }

        order.setSubtotalProducts(subtotalProducts);
        order.setTotalCost(subtotalProducts + shipping);
        order.setItems(items);

        return SupplierOrderResponse.fromEntity(orderRepository.save(order));
    }

    @Transactional
    public SupplierOrderResponse markAsReceived(Long businessId, Long orderId) {
        SupplierOrder order = orderRepository.findByIdAndBusinessId(orderId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado."));

        if ("RECIBIDO".equals(order.getStatus())) {
            throw new IllegalArgumentException("Este pedido ya fue marcado como recibido.");
        }
        if ("CANCELADO".equals(order.getStatus())) {
            throw new IllegalArgumentException("No puedes recibir un pedido cancelado.");
        }

        for (SupplierOrderItem item : order.getItems()) {
            Product product = productRepository.findByIdAndBusinessIdWithLock(item.getProductId(), businessId)
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + item.getProductName()));
            product.setStock(product.getStock() + item.getTotalUnits());
            productRepository.save(product);
        }

        order.setStatus("RECIBIDO");
        order.setReceivedDate(ZonedDateTime.now(BOGOTA_ZONE).format(DATE_FORMATTER));
        return SupplierOrderResponse.fromEntity(orderRepository.save(order));
    }

    private String normalizeTier(String tier) {
        if (tier == null) return "UNIDAD";
        return switch (tier.trim().toUpperCase()) {
            case "DECENA" -> "DECENA";
            case "DOCENA" -> "DOCENA";
            default -> "UNIDAD";
        };
    }

    private int tierMultiplier(String tier) {
        return switch (normalizeTier(tier)) {
            case "DECENA" -> 10;
            case "DOCENA" -> 12;
            default -> 1;
        };
    }
}

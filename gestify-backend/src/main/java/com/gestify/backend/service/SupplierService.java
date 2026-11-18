package com.gestify.backend.service;

import com.gestify.backend.dto.request.SupplierRequest;
import com.gestify.backend.dto.response.SupplierResponse;
import com.gestify.backend.exception.ResourceNotFoundException;
import com.gestify.backend.model.Business;
import com.gestify.backend.model.Product;
import com.gestify.backend.model.Supplier;
import com.gestify.backend.repository.BusinessRepository;
import com.gestify.backend.repository.ProductRepository;
import com.gestify.backend.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private static final int MAX_EXTRA_ITEMS = 50;
    private static final int MAX_ITEM_LENGTH = 80;

    private final SupplierRepository supplierRepository;
    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<SupplierResponse> getSuppliersByBusiness(Long businessId) {
        return supplierRepository.findByBusinessIdWithProducts(businessId)
                .stream()
                .map(SupplierResponse::fromEntity)
                .toList();
    }

    @Transactional
    public SupplierResponse createSupplier(Long businessId, SupplierRequest request) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Negocio no encontrado."));

        Supplier supplier = Supplier.builder()
                .business(business)
                .name(request.getName().trim())
                .contactName(trimOrNull(request.getContactName()))
                .phone(request.getPhone().trim())
                .category(trimOrNull(request.getCategory()))
                .products(resolveProducts(request.getProductIds(), businessId))
                .isActive(true)
                .build();
        supplier.getExtraItems().addAll(cleanExtraItems(request.getExtraItems()));

        return SupplierResponse.fromEntity(supplierRepository.save(supplier));
    }

    @Transactional
    public SupplierResponse updateSupplier(Long businessId, Long id, SupplierRequest request) {
        Supplier supplier = supplierRepository.findByIdAndBusinessId(id, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado."));

        supplier.setName(request.getName().trim());
        supplier.setContactName(trimOrNull(request.getContactName()));
        supplier.setPhone(request.getPhone().trim());
        supplier.setCategory(trimOrNull(request.getCategory()));
        supplier.setProducts(resolveProducts(request.getProductIds(), businessId));

        supplier.getExtraItems().clear();
        supplier.getExtraItems().addAll(cleanExtraItems(request.getExtraItems()));

        return SupplierResponse.fromEntity(supplierRepository.save(supplier));
    }

    @Transactional
    public SupplierResponse toggleSupplierStatus(Long businessId, Long id) {
        Supplier supplier = supplierRepository.findByIdAndBusinessId(id, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado."));

        supplier.setIsActive(!supplier.getIsActive());
        return SupplierResponse.fromEntity(supplierRepository.save(supplier));
    }

    private Set<Product> resolveProducts(List<Long> productIds, Long businessId) {
        if (productIds == null || productIds.isEmpty()) {
            return new HashSet<>();
        }
        List<Product> found = productRepository.findAllById(productIds);
        for (Product p : found) {
            if (!p.getBusiness().getId().equals(businessId)) {
                throw new IllegalArgumentException("El producto ID " + p.getId() + " no pertenece al negocio.");
            }
        }
        return new HashSet<>(found);
    }

    private List<String> cleanExtraItems(List<String> items) {
        if (items == null) return List.of();
        Map<String, String> unique = new LinkedHashMap<>();
        for (String raw : items) {
            if (raw == null) continue;
            String item = raw.trim();
            if (item.isEmpty()) continue;
            if (item.length() > MAX_ITEM_LENGTH) item = item.substring(0, MAX_ITEM_LENGTH);
            unique.putIfAbsent(item.toLowerCase(), item);
            if (unique.size() >= MAX_EXTRA_ITEMS) break;
        }
        return List.copyOf(unique.values());
    }

    private String trimOrNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
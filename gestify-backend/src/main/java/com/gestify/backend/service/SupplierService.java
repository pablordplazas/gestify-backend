package com.gestify.backend.service;

import com.gestify.backend.dto.request.SupplierRequest;
import com.gestify.backend.dto.response.SupplierResponse;
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
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SupplierService {

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
    public SupplierResponse createSupplier(SupplierRequest request) {
        Business business = businessRepository.findById(request.getIdNegocio())
                .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado."));

        Set<Product> linkedProducts = resolveProducts(request.getProductIds(), business.getId());

        Supplier supplier = Supplier.builder()
                .business(business)
                .name(request.getName().trim())
                .contactName(request.getContactName() != null ? request.getContactName().trim() : null)
                .phone(request.getPhone().trim())
                .category(request.getCategory() != null ? request.getCategory().trim() : null)
                .products(linkedProducts)
                .isActive(true)
                .build();

        return SupplierResponse.fromEntity(supplierRepository.save(supplier));
    }

    @Transactional
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado."));

        Set<Product> linkedProducts = resolveProducts(request.getProductIds(), supplier.getBusiness().getId());

        supplier.setName(request.getName().trim());
        supplier.setContactName(request.getContactName() != null ? request.getContactName().trim() : null);
        supplier.setPhone(request.getPhone().trim());
        supplier.setCategory(request.getCategory() != null ? request.getCategory().trim() : null);
        supplier.setProducts(linkedProducts);

        return SupplierResponse.fromEntity(supplierRepository.save(supplier));
    }

    @Transactional
    public SupplierResponse toggleSupplierStatus(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado."));

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
}
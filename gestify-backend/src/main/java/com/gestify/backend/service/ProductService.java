package com.gestify.backend.service;

import com.gestify.backend.dto.request.ProductRequest;
import com.gestify.backend.dto.response.ProductResponse;
import com.gestify.backend.exception.ResourceNotFoundException;
import com.gestify.backend.model.Business;
import com.gestify.backend.model.Product;
import com.gestify.backend.repository.BusinessRepository;
import com.gestify.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final BusinessRepository businessRepository;

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllByBusiness(Long businessId) {
        return productRepository.findByBusinessIdOrderByIdDesc(businessId)
                .stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }

    @Transactional
    public ProductResponse createProduct(Long businessId, ProductRequest request) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Negocio no encontrado."));

        Product product = Product.builder()
                .business(business)
                .name(request.getName().trim())
                .category(request.getCategory().trim())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUri(trimOrNull(request.getImageUri()))
                .isActive(true)
                .build();

        return ProductResponse.fromEntity(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long businessId, Long id, ProductRequest request) {
        Product product = productRepository.findByIdAndBusinessId(id, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado."));

        product.setName(request.getName().trim());
        product.setCategory(request.getCategory().trim());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUri(trimOrNull(request.getImageUri()));

        return ProductResponse.fromEntity(productRepository.save(product));
    }

    @Transactional
    public ProductResponse toggleProductStatus(Long businessId, Long id) {
        Product product = productRepository.findByIdAndBusinessId(id, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado."));

        product.setIsActive(!product.getIsActive());
        return ProductResponse.fromEntity(productRepository.save(product));
    }

    private String trimOrNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
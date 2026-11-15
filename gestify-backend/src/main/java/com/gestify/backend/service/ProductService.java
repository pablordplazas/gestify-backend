package com.gestify.backend.service;

import com.gestify.backend.dto.request.ProductRequest;
import com.gestify.backend.dto.response.ProductResponse;
import com.gestify.backend.model.Business;
import com.gestify.backend.model.Product;
import com.gestify.backend.repository.BusinessRepository;
import com.gestify.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final BusinessRepository businessRepository;

    public List<ProductResponse> getAllByBusiness(Long idNegocio) {
        return productRepository.findByBusinessIdOrderByIdDesc(idNegocio)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse createProduct(ProductRequest request) {
        Business business = businessRepository.findById(request.getIdNegocio())
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado con id: " + request.getIdNegocio()));

        Product product = new Product();
        product.setBusiness(business);
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setIsActive(true);
        product.setImageUri(request.getImageUri());

        return mapToResponse(productRepository.save(product));
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));

        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        if (request.getImageUri() != null && !request.getImageUri().isBlank()) {
            product.setImageUri(request.getImageUri());
        }

        return mapToResponse(productRepository.save(product));
    }

    public ProductResponse toggleProductStatus(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));

        product.setIsActive(!product.getIsActive());
        return mapToResponse(productRepository.save(product));
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .idNegocio(product.getBusiness().getId())
                .name(product.getName())
                .category(product.getCategory())
                .price(product.getPrice())
                .stock(product.getStock())
                .isActive(product.getIsActive())
                .imageUri(product.getImageUri())
                .build();
    }
}
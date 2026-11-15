package com.gestify.backend.service;

import com.gestify.backend.dto.response.ProductResponse;
import com.gestify.backend.model.Business;
import com.gestify.backend.model.Product;
import com.gestify.backend.repository.BusinessRepository;
import com.gestify.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final BusinessRepository businessRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllByBusiness(Long businessId) {
        return productRepository.findByBusinessIdOrderByIdDesc(businessId)
                .stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }

    @Transactional
    public ProductResponse createProduct(Long businessId, String name, String category, Double price, Integer stock, MultipartFile image) {
        validateProductFields(name, category, price, stock);

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado con ID: " + businessId));

        String imageUrl = cloudinaryService.uploadImage(image);

        Product product = Product.builder()
                .business(business)
                .name(name.trim())
                .category(category.trim())
                .price(price)
                .stock(stock)
                .isActive(true)
                .imageUri(imageUrl)
                .build();

        return ProductResponse.fromEntity(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, String name, String category, Double price, Integer stock, MultipartFile image) {
        validateProductFields(name, category, price, stock);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));

        product.setName(name.trim());
        product.setCategory(category.trim());
        product.setPrice(price);
        product.setStock(stock);

        if (image != null && !image.isEmpty()) {
            String newImageUrl = cloudinaryService.uploadImage(image);
            product.setImageUri(newImageUrl);
        }

        return ProductResponse.fromEntity(productRepository.save(product));
    }

    @Transactional
    public ProductResponse toggleProductStatus(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));

        product.setIsActive(!product.getIsActive());
        return ProductResponse.fromEntity(productRepository.save(product));
    }

    private void validateProductFields(String name, String category, Double price, Integer stock) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        if (category == null || category.isBlank()) throw new IllegalArgumentException("La categoría es obligatoria.");
        if (price == null || price <= 0) throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        if (stock == null || stock < 0) throw new IllegalArgumentException("El stock no puede ser negativo.");
    }
}
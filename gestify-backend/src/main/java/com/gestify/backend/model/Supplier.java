package com.gestify.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "suppliers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_negocio", nullable = false)
    private Business business;

    @Column(nullable = false)
    private String name;

    @Column(name = "contact_name")
    private String contactName;

    @Column(nullable = false, length = 30)
    private String phone;

    private String category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "supplier_products",
            joinColumns = @JoinColumn(name = "supplier_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @Builder.Default
    private Set<Product> products = new HashSet<>();

    /** Productos escritos a mano (que no están en el inventario). */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "supplier_extra_items", joinColumns = @JoinColumn(name = "supplier_id"))
    @Column(name = "item_name", length = 80)
    @OrderColumn(name = "item_order")
    @BatchSize(size = 50)
    @Builder.Default
    private List<String> extraItems = new ArrayList<>();

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
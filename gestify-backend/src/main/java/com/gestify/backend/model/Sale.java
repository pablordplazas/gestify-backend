package com.gestify.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_negocio", nullable = false)
    private Business business;

    /** Nullable para ventas multi-producto nuevas; conservado por compatibilidad con registros previos. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price")
    private Double unitPrice;

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false, length = 20)
    private String time;

    @Column(nullable = false, length = 20)
    private String date;

    @Column(name = "customer_name", length = 120)
    private String customerName;

    @Column(name = "customer_document", length = 40)
    private String customerDocument;

    @Column(name = "customer_phone", length = 40)
    private String customerPhone;

    @Column(name = "customer_email", length = 120)
    private String customerEmail;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SaleLineItem> items = new ArrayList<>();
}
package com.gestify.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "supplier_orders")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SupplierOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_negocio", nullable = false)
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "supplier_name", nullable = false)
    private String supplierName;

    @Column(name = "shipping_cost", nullable = false)
    private Double shippingCost;

    @Column(name = "subtotal_products", nullable = false)
    private Double subtotalProducts;

    @Column(name = "total_cost", nullable = false)
    private Double totalCost;

    /** PENDIENTE, RECIBIDO, CANCELADO */
    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "order_date", nullable = false, length = 30)
    private String orderDate;

    @Column(name = "received_date", length = 30)
    private String receivedDate;

    @Column(length = 250)
    private String notes;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SupplierOrderItem> items = new ArrayList<>();
}
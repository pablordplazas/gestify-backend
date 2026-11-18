package com.gestify.backend.model;

import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    /** Precio unitario al momento de la venta. Nullable para no romper las ventas ya guardadas. */
    @Column(name = "unit_price")
    private Double unitPrice;

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false, length = 20)
    private String time;

    @Column(nullable = false, length = 20)
    private String date;
}
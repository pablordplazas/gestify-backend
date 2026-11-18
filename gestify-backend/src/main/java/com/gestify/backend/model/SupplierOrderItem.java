package com.gestify.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "supplier_order_items")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SupplierOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private SupplierOrder order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    /** UNIDAD (1), DECENA (10), DOCENA (12) */
    @Column(name = "pricing_tier", nullable = false, length = 20)
    private String pricingTier;

    /** Cantidad de paquetes/unidades según la escala elegida (ej: 2 docenas) */
    @Column(name = "pack_quantity", nullable = false)
    private Integer packQuantity;

    /** Total de unidades individuales que entran al stock (ej: 2 * 12 = 24) */
    @Column(name = "total_units", nullable = false)
    private Integer totalUnits;

    /** Precio cobrado por el proveedor según la escala (por unidad, por decena o por docena) */
    @Column(name = "tier_price", nullable = false)
    private Double tierPrice;

    /** Costo real unitario incluyendo proporción de envío */
    @Column(name = "real_unit_cost", nullable = false)
    private Double realUnitCost;

    @Column(nullable = false)
    private Double subtotal;
}
package com.gestify.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "businesses")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_negocio", nullable = false)
    private String businessName;

    @Column(name = "correo_usuario", nullable = false, unique = true)
    private String email;

    @Column(name = "contrasena_hash", nullable = false)
    private String passwordHash;

    @Column(name = "tema_color", nullable = false)
    @Builder.Default
    private String themeColor = "BLUE";

    @Column(name = "modo_oscuro", nullable = false)
    @Builder.Default
    private Boolean isDarkMode = false;
}
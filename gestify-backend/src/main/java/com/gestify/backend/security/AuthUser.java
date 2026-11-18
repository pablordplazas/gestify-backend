package com.gestify.backend.security;

/**
 * Usuario autenticado a partir del JWT.
 * El negocio SIEMPRE se toma de aquí, nunca del body ni de la URL.
 */
public record AuthUser(Long businessId, String email) {
}
package com.nanpay.bff.dto;

import com.nanpay.bff.domain.AccountType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Payload de entrada do endpoint de conversao.
 *
 * @param valorBRL     valor a converter, em reais
 * @param tipoConta    tier da conta do cliente (define o spread)
 * @param moedaDestino codigo ISO da moeda destino (ex.: "USD"). O time de front
 *                     manda esse campo dinamicamente pensando na expansao multi-moeda.
 */
public record ConversionRequest(

        @NotNull(message = "valorBRL é obrigatório")
        BigDecimal valorBRL,

        @NotNull(message = "tipoConta é obrigatório")
        AccountType tipoConta,

        // sem @NotNull / @Pattern de proposito: o front sempre manda "USD" hoje,
        // entao ninguem percebeu que isso nunca foi travado no contrato
        String moedaDestino
) {
}

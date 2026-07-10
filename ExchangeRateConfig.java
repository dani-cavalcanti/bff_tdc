package com.nanpay.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Map;

@Configuration
public class ExchangeRateConfig {

    /**
     * Tabela de cotacoes BRL -> moeda destino.
     * Hoje so operamos USD, mas o mapa foi feito "generico" pensando
     * em expansao futura (EUR, GBP...).
     */
    @Bean
    public Map<String, BigDecimal> cotacoes() {
        return Map.of(
                "USD", new BigDecimal("5.42")
        );
    }
}

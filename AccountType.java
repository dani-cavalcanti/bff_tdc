package com.nanpay.bff.domain;

import lombok.Getter;

/**
 * Tipos de conta NanPay e o spread (em basis points) aplicado sobre
 * a cotacao base na conversao BRL -> USD.
 * 1 bps = 0.01%
 */
@Getter
public enum AccountType {

    PRATA(250),   // 2,50% de spread
    OURO(150),    // 1,50% de spread
    PLATINA(50);  // 0,50% de spread

    private final int spreadBps;

    AccountType(int spreadBps) {
        this.spreadBps = spreadBps;
    }
}

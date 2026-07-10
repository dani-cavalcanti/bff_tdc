package com.nanpay.bff.service;

import com.nanpay.bff.domain.AccountType;
import com.nanpay.bff.dto.ConversionRequest;
import com.nanpay.bff.dto.ConversionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyExchangeService {

    private final Map<String, BigDecimal> cotacoes;
    private final AuditService auditService;

    public ConversionResponse converter(ConversionRequest request) {
        BigDecimal valorBRL = request.valorBRL();
        AccountType tipoConta = request.tipoConta();

        // BUG-02 (falta de validação de borda):
        // não há checagem de valorBRL <= 0 aqui. Um valor negativo (estorno
        // digitado errado, payload manipulado, etc.) passa direto e devolve
        // um "valor convertido" negativo como se fosse normal.
        //
        // Além disso, cotacoes.getOrDefault(..., ZERO) parece uma defesa contra
        // NPE quando moedaDestino não está na tabela (ex.: "usd" minúsculo, "EUR"
        // ainda não configurado) - mas na prática abre a porta pra divisão por
        // zero mais abaixo, que em double não estoura exceção: vira Infinity/NaN
        // silenciosamente.
        String moeda = request.moedaDestino() == null ? "USD" : request.moedaDestino();
        BigDecimal cotacaoBase = cotacoes.getOrDefault(moeda, BigDecimal.ZERO);

        double spreadPercentual = tipoConta.getSpreadBps() / 10000.0;

        // BUG-01 (precisão monetária):
        // a conta crítica é feita em double em vez de BigDecimal. Para valores
        // "redondos" usados nos testes felizes (ex.: 1000.00, 500.00) o erro de
        // arredondamento binário não aparece nos primeiros dígitos e passa
        // despercebido. Para valores com muitas casas decimais / grandes somas
        // (ex.: R$ 1.234.567,891234), o resultado diverge do BigDecimal "correto",
        // e no fintech isso é dinheiro sumindo ou aparecendo do nada.
        double valorLiquidoDouble = valorBRL.doubleValue() * (1 - spreadPercentual);
        double valorConvertidoDouble = valorLiquidoDouble / cotacaoBase.doubleValue();

        BigDecimal valorConvertidoUSD = BigDecimal.valueOf(valorConvertidoDouble)
                .setScale(2, java.math.RoundingMode.HALF_UP);

        String auditoriaId = auditService.registrar(tipoConta.name(), valorBRL);

        return new ConversionResponse(
                valorBRL,
                valorConvertidoUSD,
                cotacaoBase,
                tipoConta.name(),
                auditoriaId,
                LocalDateTime.now()
        );
    }
}

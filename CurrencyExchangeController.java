package com.nanpay.bff.controller;

import com.nanpay.bff.dto.ConversionRequest;
import com.nanpay.bff.dto.ConversionResponse;
import com.nanpay.bff.service.CurrencyExchangeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * BUG-03 (inconsistência de contrato):
 * este controller devolve ConversionResponse como está, e o Jackson do
 * Spring Boot serializa records em camelCase por padrão
 * (ex.: "valorConvertidoUSD"). O pact do consumer mobile (time de front)
 * foi escrito esperando snake_case ("valor_convertido_usd").
 *
 * Nenhum teste de unidade do Controller pega isso, porque eles normalmente
 * verificam o objeto Java retornado (ou usam o mesmo Jackson config nos dois
 * lados). Só um contract test real contra o pact broker - ou o app mobile
 * em produção recebendo "valorConvertidoUSD": undefined - expõe o problema.
 */
@RestController
@RequestMapping("/api/v1/conversoes")
@RequiredArgsConstructor
public class CurrencyExchangeController {

    private final CurrencyExchangeService currencyExchangeService;

    @PostMapping
    public ResponseEntity<ConversionResponse> converter(@Valid @RequestBody ConversionRequest request) {
        ConversionResponse response = currencyExchangeService.converter(request);
        return ResponseEntity.ok(response);
    }
}

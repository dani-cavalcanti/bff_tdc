package com.nanpay.bff.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payload de saida do endpoint de conversao.
 *
 * ATENCAO (contrato consumido pelo app mobile via Pact):
 * o consumer.pact.json foi escrito esperando snake_case
 * ("valor_convertido_usd", "cotacao_aplicada", "auditoria_id"),
 * mas o Jackson default do Spring serializa records em camelCase.
 * Isso nunca gera erro de compilacao nem quebra teste unitario do Service
 * (que trabalha com o objeto Java, nao com o JSON serializado) - so aparece
 * no contract test contra o pact real, ou em producao no client mobile.
 *
 * @param valorOriginalBRL  valor de entrada, para conferencia do consumer
 * @param valorConvertidoUSD valor final em USD
 * @param cotacaoAplicada  cotacao BRL/USD usada no calculo
 * @param tipoConta        tier da conta usado no calculo
 * @param auditoriaId      id do registro de auditoria - contrato marca como obrigatorio,
 *                         mas pode chegar null (ver AuditService)
 * @param dataConversao    timestamp do calculo
 */
public record ConversionResponse(
        BigDecimal valorOriginalBRL,
        BigDecimal valorConvertidoUSD,
        BigDecimal cotacaoAplicada,
        String tipoConta,
        String auditoriaId,
        LocalDateTime dataConversao
) {
}

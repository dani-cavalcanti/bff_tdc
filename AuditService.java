package com.nanpay.bff.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Registra a conversao para fins de auditoria/compliance.
 * Em ambientes reais isso chamaria um servico externo (fila, banco, etc.).
 */
@Slf4j
@Service
public class AuditService {

    /**
     * Gera o id de auditoria da conversao.
     * Se o registro falhar (ex.: fila fora do ar), a chamada não deveria
     * derrubar a conversao para o cliente - entao logamos e seguimos.
     *
     * O problema: o retorno null aqui vira o "auditoriaId" da resposta,
     * campo que o contrato Pact marca como obrigatorio (nao nulo) para o
     * time de compliance. Como isso so falha esporadicamente (timeout,
     * indisponibilidade), o teste feliz do Controller nunca pega.
     */
    public String registrar(String tipoConta, java.math.BigDecimal valor) {
        try {
            return simularChamadaExterna(tipoConta, valor);
        } catch (Exception e) {
            log.warn("Falha ao registrar auditoria, seguindo sem bloquear a conversao", e);
            return null;
        }
    }

    private String simularChamadaExterna(String tipoConta, java.math.BigDecimal valor) {
        // simulacao: em cenarios reais aqui poderia estourar timeout,
        // erro de serializacao, servico fora do ar, etc.
        return UUID.randomUUID().toString();
    }
}

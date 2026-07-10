<div align="center">

# 🎙️ O Teste que Passa e Mente

### Como detectar o que o Copilot não consegue testar

*Repositório de demonstração da palestra prática com live coding*

<br/>

> 🏷️ **Título oficial:** Implementação de Barreiras de Qualidade Avançadas e Engenharia de Resiliência em Ambientes de CI/CD acelerados por IA
>
> 📍 **Trilha:** DevTest e IA &nbsp;·&nbsp; ⏱️ **Formato:** Palestra 35 min

<br/>

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit_5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

</div>

---

## 🧠 Sobre a palestra

Com a adopção massiva de ferramentas de IA generativa (como o GitHub Copilot) no fluxo de desenvolvimento, observamos um aumento na velocidade de escrita de código e na métrica de cobertura de testes tradicional (Line/Branch Coverage). No entanto, a cobertura quantitativa não se traduz necessariamente em eficácia qualitativa, gerando cenários de **"falso positivo"** onde o pipeline permanece verde, mas bugs lógicos e quebras de contrato chegam ao ambiente produtivo, elevando o MTTR e impactando a estabilidade.

Esta aplicação demonstra ao vivo como três técnicas avançadas expõem o que a IA não consegue testar — usando como pano de fundo um cenário de **fintech de câmbio digital**, onde erro de precisão e quebra de contrato não são só bug, são dinheiro do cliente:

<br/>

| 🔬 Técnica | 🛠️ Ferramenta | 💡 O que revela |
|:---|:---|:---|
| **Testes de Mutação** | PITest | Lógica não verificada — mutantes que sobrevivem mesmo com JUnit 100% verde |
| **Property-Based Testing** | jqwik | Inputs extremos (valores negativos, `Infinity`, casas decimais longas) que causam bugs reais |
| **Contract Testing** | Pact JVM | Quebras silenciosas de contrato entre BFF e app mobile antes do deploy |

<br/>

> **💬 A grande revelação:** 100% de cobertura de linhas significa que cada linha foi *executada* — não que foi *verificada*. O PITest prova isso gerando versões quebradas do seu código e medindo quantas delas seus testes detectam.

---

## 💱 A aplicação — NanPay BFF

Um BFF (Backend For Frontend) de conversão de câmbio: recebe um valor em BRL, o tier da conta do cliente, e devolve o valor convertido para USD já com o spread do tier aplicado. Simples o suficiente para entender em segundos, complexo o suficiente para esconder bugs que vão para produção.

```http
POST /api/v1/conversoes
Content-Type: application/json

{
  "valorBRL": 1000.00,
  "tipoConta": "OURO",
  "moedaDestino": "USD"
}

```

**Resposta esperada:**

```json
{
  "valorOriginalBRL": 1000.00,
  "valorConvertidoUSD": 181.68,
  "cotacaoAplicada": 5.42,
  "tipoConta": "OURO",
  "auditoriaId": "a1b2c3d4-...",
  "dataConversao": "2026-07-10T14:32:00"
}

```

**Tabela de spreads por tier:**

| Tier | Spread |
| --- | --- |
| `PRATA` | 2,50% |
| `OURO` | 1,50% |
| `PLATINA` | 0,50% |

A implementação carrega **3 bugs intencionais** que os testes felizes não detectam — revelados progressivamente durante a palestra.

---

## 🐛 Os 3 bugs intencionais

| # | Bug no código | Por que passa despercebido | Detectado por |
| --- | --- | --- | --- |
| 1 | Conta crítica de conversão feita em `double` em vez de `BigDecimal` (`CurrencyExchangeService`) | Valores redondos (1000.00, 500.00) não expõem o erro de arredondamento binário | 🔬 jqwik |
| 2 | `valorBRL` sem validação de negativo + `cotacoes.getOrDefault(moeda, ZERO)` mascarando divisão por zero | Testes felizes sempre mandam `"USD"` e valores positivos | 💀 PITest · 🔬 jqwik |
| 3 | `ConversionResponse` serializado em camelCase pelo Jackson, enquanto o pact do app mobile espera snake_case; `auditoriaId` pode chegar `null` (exceção engolida em `AuditService`) | Teste de unidade do Controller valida o objeto Java, não o JSON real contra o contrato | 📜 Pact JVM |

---

## 🏗️ Estrutura do projeto

```
src/
├── main/java/com/nanpay/bff/
│   ├── NanpayBffApplication.java
│   ├── domain/
│   │   └── AccountType.java              ← enum PRATA | OURO | PLATINA
│   ├── dto/
│   │   ├── ConversionRequest.java        ← record de entrada
│   │   └── ConversionResponse.java       ← record de saída (contrato do Pact)
│   ├── service/
│   │   ├── CurrencyExchangeService.java  ← ⚠️ bugs 1 e 2
│   │   └── AuditService.java             ← ⚠️ origem do bug 3 (null silencioso)
│   ├── controller/
│   │   └── CurrencyExchangeController.java  ← ⚠️ bug 3 (camelCase x snake_case)
│   └── config/
│       └── ExchangeRateConfig.java       ← tabela de cotações
│
└── test/java/com/nanpay/bff/
    ├── unit/
    │   └── CurrencyExchangeServiceCopilotTest.java   ← 🤖 Ato 2: 100% JaCoCo, mutation score baixo
    ├── property/
    │   └── CurrencyExchangeServicePropertyTest.java  ← 🔬 Ato 3: jqwik explode com valores extremos
    ├── contract/
    │   └── ConversionPactConsumerTest.java           ← 📜 Ato 4: gera contrato JSON
    └── provider/
        └── ConversionProviderPactTest.java           ← 🔒 Ato 4: verifica contrato contra o app real

```
---

## 🚀 Pré-requisitos

```bash
java -version   # deve mostrar 21+
mvn -version    # deve mostrar 3.9+

```

## 📦 Clonando o Repositório

Para baixar o projeto, você pode acessar diretamente o repositório [dani-cavalcanti/bff_tdc](https://github.com/dani-cavalcanti/bff_tdc.git) ou clonar executando o comando abaixo no seu terminal:

```bash
git clone https://github.com/dani-cavalcanti/bff_tdc.git
cd bff_tdc

```

---

## ▶️ Como rodar

### Subir a API

```bash
mvn spring-boot:run
# → http://localhost:8080/api/v1/conversoes

```

### Exemplo de chamada

```bash
curl -X POST http://localhost:8080/api/v1/conversoes \
  -H "Content-Type: application/json" \
  -d '{"valorBRL": 1000.00, "tipoConta": "OURO", "moedaDestino": "USD"}'

```

---

### 🤖 Ato 2 — A ilusão do Copilot

```bash
# Rodar os testes "felizes" — 100% de cobertura de linhas ✅
mvn test -Dtest=CurrencyExchangeServiceCopilotTest

# Ligar o PITest — mutantes sobrevivem apesar do JUnit verde 💀
mvn test-compile org.pitest:pitest-maven:mutationCoverage
# → Abrir: target/pit-reports/index.html

```

---

### 🔬 Ato 3 — jqwik revela os bugs

```bash
# Propriedade vai FALHAR com counterexample: valorBRL negativo ou moedaDestino inválido 💥
mvn test -Dtest=CurrencyExchangeServicePropertyTest

```

Output esperado:

```
PropertyFailureException: Property falsified
  Shrunk Sample:
    valorBRL      = -1000.00
    tipoConta     = PLATINA
    moedaDestino  = "eur"

  cotacoes.getOrDefault("eur", ZERO) → divisão por zero em double → NaN/Infinity!

```

---

### 📜 Ato 4 — Pact contract testing

```bash
# 1. Gerar o contrato (Consumer define o que espera)
mvn test -Dtest=ConversionPactConsumerTest
# → target/pacts/MobileApp-NanpayBFF.json

# 2. Mover para resources (Provider lê daqui)
cp target/pacts/MobileApp-NanpayBFF.json src/test/resources/pacts/

# 3. Verificar o contrato contra o app real (Provider)
mvn test -Dtest=ConversionProviderPactTest

```

---

## 🏔️ A pirâmide de testes

```
                 ┌──────────────────────┐
                 │  📜 Contrato · Pact   │  ← BFF x App Mobile · CI
                 └──────────────────────┘
            ┌──────────────────────────────────┐
            │  🔬 Propriedade · jqwik           │  ← valores extremos, negativos, moedas inválidas
            └──────────────────────────────────┘
       ┌──────────────────────────────────────────────┐
       │  💀 Mutação · PITest                          │  ← mutation threshold 0, gap exposto
       └──────────────────────────────────────────────┘
  ┌──────────────────────────────────────────────────────────┐
  │  🤖 Unitário · JUnit 5 (estilo Copilot)                  │  ← base: rápidos, isolados
  └──────────────────────────────────────────────────────────┘

```

<div align="center">

<br/>

---

**"A IA gera código médio para inputs médios. Você é responsável pelos extremos."**



👩🏽‍💻 **Dani Cavalcanti** · Quality Engineer & SDET

[![Instagram](https://img.shields.io/badge/@qadanicavalcanti-E4405F?style=flat-square&logo=instagram&logoColor=white)](https://instagram.com/qadanicavalcanti)
[![LinkedIn](https://img.shields.io/badge/dani--cavalcanti--qa-0A66C2?style=flat-square&logo=linkedin&logoColor=white)](https://linkedin.com/in/dani-cavalcanti-qa)



<sub>Feito com 💜 para a comunidade de Quality Engineering · Trilha DevTest e IA</sub>

</div>


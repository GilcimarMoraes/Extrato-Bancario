# 💰 Sistema de Processamento de Transações Bancárias

Sistema em Java para leitura, validação e processamento de transações bancárias a partir de arquivos CSV, com cálculo automático de saldos e geração de extratos.

---

## 📋 Funcionalidades

- ✅ Leitura de arquivos CSV com milhões de registros
- ✅ Validação completa de campos (operação, data/hora, campos obrigatórios)
- ✅ Remoção de transações duplicadas via `HashSet`
- ✅ Cálculo de saldos por conta com ordenação cronológica
- ✅ Rejeição automática de saques com saldo insuficiente
- ✅ Extrato completo com histórico e saldos parciais
- ✅ Modo resumido para arquivos grandes (+10.000 transações)
- ✅ Relatório de erros e estatísticas de processamento

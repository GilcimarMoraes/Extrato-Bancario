💰 Sistema de Processamento de Transações Bancárias
Sistema em Java para leitura, validação e processamento de transações bancárias a partir de arquivos CSV, com cálculo automático de saldos e geração de extratos.

📋 Funcionalidades
✅ Leitura de arquivos CSV com milhões de registros
✅ Validação completa de campos (operação, data/hora, campos obrigatórios)
✅ Remoção de transações duplicadas via HashSet
✅ Cálculo de saldos por conta com ordenação cronológica
✅ Rejeição automática de saques com saldo insuficiente
✅ Extrato completo com histórico e saldos parciais
✅ Modo resumido para arquivos grandes (+10.000 transações)
✅ Relatório de erros e estatísticas de processamento
🏗️ Arquitetura
O projeto segue o princípio de Responsabilidade Única (SRP), onde cada classe possui uma única razão para mudar.

src/
├── model/
│   ├── Transacao.java              → Modelo imutável de uma transação
│   ├── SaldoConta.java             → Estado da conta (saldo + histórico)
│   └── ResultadoValidacao.java     → Encapsula válidas + erros da validação
│
├── service/
│   ├── LeitorCsv.java              → Leitura do arquivo CSV (I/O puro)
│   ├── ValidadorTransacao.java     → Validação e conversão dos campos
│   ├── DeduplicadorService.java    → Remoção de duplicatas
│   └── SaldoService.java           → Ordenação e cálculo de saldos
│
├── report/
│   ├── RelatorioProcessamento.java → Estatísticas da leitura/validação
│   └── ExtratoFormatter.java       → Formatação de extratos
│
├── Main.java                       → Ponto de entrada da aplicação
└── LeitorCSVTest.java              → Classe de teste
Fluxo de Processamento
CSV → Leitura → Validação → Deduplicação → Cálculo de Saldos → Apresentação
(1)        (2)          (3)              (4)                (5)
Etapa	Classe	Entrada	Saída
1. Leitura	LeitorCsv	Arquivo CSV	List<String[]>
2. Validação	ValidadorTransacao	List<String[]>	ResultadoValidacao
3. Deduplicação	DeduplicadorService	List<Transacao>	List<Transacao> (sem duplicatas)
4. Cálculo	SaldoService	List<Transacao>	Map<String, SaldoConta>
5. Apresentação	ExtratoFormatter	Map<String, SaldoConta>	Saída no console
   📄 Formato do CSV
   O arquivo CSV deve seguir o formato abaixo:

csv
Copy
AGENCIA,CONTA,BANCO,TITULAR,OPERACAO,DATAHORA
1520,0001,SANTANDER,JOAO,DEPOSITO,2022-02-07T11:08:10
3320,0004,SANTANDER,MARIA,SAQUE,2022-02-15T14:20:10
Campos
Campo	Tipo	Obrigatório	Descrição
AGENCIA	String	✅	Código da agência
CONTA	String	✅	Número da conta
BANCO	String	✅	Nome do banco
TITULAR	String	✅	Nome do titular
OPERACAO	String	✅	DEPOSITO ou SAQUE
DATAHORA	String	✅	Formato yyyy-MM-ddTHH:mm:ss
VALOR	Decimal	❌	Valor da operação (opcional)
Nota: Se a coluna VALOR não estiver presente no CSV, o sistema assume o valor 1.00 para cada transação.

🚀 Como Executar
Pré-requisitos
Java 11 ou superior
Compilação
bash
Copy
javac -d out src/**/*.java
Execução
bash
Copy
java -cp out Main data/operacoes.csv
IDE (IntelliJ / Eclipse)
Importe o projeto
Configure Program Arguments: data/operacoes.csv
Execute a classe Main
VS Code
Adicione ao launch.json:

json
Copy
{
"type": "java",
"name": "Main",
"request": "launch",
"mainClass": "Main",
"args": ["data/operacoes.csv"]
}
🧪 Testes
O projeto inclui um arquivo operacoes_teste.csv com cenários de erro para validação:

Cenário	Descrição
Transações válidas	Fluxo normal + fora de ordem cronológica
Duplicatas exatas	Mesma transação repetida 2x e 3x
Linhas vazias	Linha vazia e apenas espaços
Campos insuficientes	Linhas com 1, 2 e 3 campos
Campos obrigatórios vazios	Cada campo vazio individualmente
Operação inválida	TRANSFERENCIA, PIX, PAGAMENTO
Data com tamanho errado	Só data, sem segundos, formato BR
Data com formato inválido	Mês 13, dia 30/fev, hora 25
Saque sem saldo	Saldo zerado e antes de depósito
Caractere especial	Acento no nome do titular
Para rodar os testes:

bash
Copy
java -cp out LeitorCSVTest data/operacoes_teste.csv
📊 Exemplo de Saída
Relatório de Processamento
=== RELATÓRIO DE PROCESSAMENTO ===
Linhas processadas   : 56
Linhas com erro      : 17
Duplicatas removidas : 9
Operações válidas    : 22
Extrato (modo completo)
================================================================================
Titular: JOAO
Agência: 1520 | Conta: 0001 | Banco: SANTANDER
--------------------------------------------------------------------------------
Histórico de Operações:
15/01/2022 08:00:00 | DEPOSITO | R$ 1,00       | Saldo: R$ 1,00       | (+)
07/02/2022 11:08:10 | DEPOSITO | R$ 1,00       | Saldo: R$ 2,00       | (+)
08/02/2022 09:15:00 | DEPOSITO | R$ 1,00       | Saldo: R$ 3,00       | (+)
10/02/2022 10:13:39 | SAQUE    | R$ 1,00       | Saldo: R$ 2,00       | (-)

SALDO FINAL: R$ 2,00
Extrato (modo resumido — arquivos grandes)
================================================================================
SALDOS FINAIS POR CONTA
================================================================================
ANA        | Ag: 5500 | Conta: 0006 | Banco: ITAU      | Saldo: R$       1,00
CARLOS     | Ag: 4410 | Conta: 0005 | Banco: BRADESCO   | Saldo: R$       2,00
JOAO       | Ag: 1520 | Conta: 0001 | Banco: SANTANDER  | Saldo: R$       2,00
================================================================================
SALDO TOTAL GERAL: R$ 5,00
🔧 Decisões Técnicas
Decisão	Justificativa
HashSet para deduplicação	Performance O(1) por operação, ideal para milhões de registros
BigDecimal para valores	Precisão monetária sem erros de ponto flutuante
LocalDateTime para datas	API moderna do Java, imutável e thread-safe
LinkedHashMap nos saldos	Mantém ordem de inserção para exibição consistente
BufferedReader com buffer 1MB	Otimiza leitura de arquivos grandes
Modelo imutável (Transacao)	Segurança contra alterações acidentais
👤 Autor
Gilcimar Matias

📝 Licença
Este projeto é de uso acadêmico/pessoal.
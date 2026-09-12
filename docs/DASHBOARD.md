# FrmPrincipal, menu e Dashboard inicial

## Análise e estratégia

O ponto de entrada definido no POM é com.chocoflow.ChocoFlow. Antes desta etapa, ele chamava Database.criarBanco e abria FrmInsumo. FrmInsumo e FrmCompra são JFrame gerados pelo NetBeans, ambos com EXIT_ON_CLOSE. Não havia tela principal.

A solução mantém a principal disponível e abre os formulários existentes sem convertê-los em painéis. Cada módulo tem uma única referência: se já estiver aberto, é trazido à frente e restaurado caso minimizado; se foi fechado, uma nova instância é criada. A principal configura DISPOSE_ON_CLOSE nas instâncias abertas pelo menu. Os módulos e seus arquivos .form não foram editados; sua execução individual continua com o comportamento anterior.

A nova FrmPrincipal é escrita em Swing padrão, sem GUI Builder e sem .form. Os formulários existentes continuam editáveis no Designer. A principal tem cabeçalho, menu lateral, quatro cards em duas linhas e duas tabelas separadas por divisor ajustável. Tamanho inicial 1080×740, mínimo 800×680, centralizada. A aparência em notebooks e escalas de tela diferentes ainda precisa de conferência manual.

## Arquivos

Criados:
- src/main/java/view/principal/FrmPrincipal.java
- src/main/java/model/dashboard/ResumoDashboard.java
- src/main/java/service/DashboardService.java
- src/main/java/repository/DashboardRepository.java
- src/test/java/DashboardIntegrationTest.java
- docs/DASHBOARD.md

Alterado nesta etapa:
- src/main/java/com/chocoflow/ChocoFlow.java

O main inicializa o banco uma vez e cria FrmPrincipal na fila de eventos Swing. Foi removida a conexão redundante de diagnóstico que ficava aberta no main anterior. Abrir módulos pelo menu não chama seus métodos main e, portanto, não reinicializa o banco.

Nenhuma mudança de schema, POM ou biblioteca. Os hashes de FrmInsumo.java/.form, FrmCompra.java/.form, dos Services/Repositories desses módulos e do trufas.db real permaneceram iguais antes/depois desta etapa. Não foram executados testes contra o banco real.

## Menu e atualização

- Dashboard: consulta novamente e apresenta os dados atuais.
- Insumos e Compras: abrem as telas existentes, com controle de instância única por módulo.
- Trufas, Remessas, Vendas e Financeiro: visíveis, desabilitados, com indicação Em breve ao passar o mouse.
- Sair e fechar a principal pelo X: pedem confirmação. Não ou fechar a confirmação mantém a aplicação. Sim fecha as janelas e encerra o processo. A mensagem informa sobre dados não salvos.

atualizarDashboard é chamado na criação da principal, ao clicar Dashboard, quando a principal volta a ficar ativa e quando um módulo é fechado. Não há timer. A navegação é entre janelas; o Dashboard permanece no painel central da principal.

## Dados e consultas

View → DashboardService → DashboardRepository → SQLite. ResumoDashboard apenas transporta os resultados; não cria tabela nem persistência adicional.

1. Total de insumos ativos: COUNT(*) WHERE ativo = 1.
2. Estoque baixo: insumos ativos com quantidade_estoque <= estoque_minimo. A contagem do card vem da mesma lista mostrada na tabela, ordenada por descrição e ID. Campos: descrição, estoque atual, mínimo e unidade.
3. Compras ativas no mês: COUNT(*) por status ATIVA, entre o primeiro e o último dia do mês atual, inclusive.
4. Valor comprado no mês: COALESCE(SUM(valor_total), 0) com o mesmo filtro. Canceladas não entram em nenhum dos dois indicadores mensais.
5. Últimas compras: ORDER BY date_compra DESC, id DESC LIMIT 5, incluindo ATIVA e CANCELADA. Data, ID, valor e status, sem edição.

A coluna real é date_compra e contém datas yyyy-MM-dd. DashboardService usa YearMonth.now(), conforme a data local do computador; o Repository recebe o mês e vincula seus limites por PreparedStatement. Ano, mês e fevereiro bissexto são respeitados. Valores monetários são formatados somente na View com NumberFormat pt-BR. Banco vazio resulta em zeros e mensagens de ausência de registros. Falhas de consulta mostram valores indisponíveis, sem fingir saldo zero nem abrir mensagens repetitivas; clicar Dashboard tenta novamente.

## Validação realizada

Compilação completa aprovada com JDK 25.0.2, --release 22, SQLite JDBC 3.50.3.0 e JDatePicker 1.3.4 existentes. Não foi necessário adicionar dependências. Compilação executada com javac, não Maven.

DashboardIntegrationTest executado em banco isolado:
- fresh: 23 verificações aprovadas.
- reopen, em outro processo Java: 3 verificações aprovadas.

Cobertura: banco vazio, zeros de SUM, mês local, cadastro/inativação de insumo, ordenação, igualdade ao mínimo, exclusão acima do mínimo, registro real de compra, valor e quantidade de compras mensais, cancelamento real e estorno, histórico com canceladas, início/fim de mês, fevereiro bissexto, virada do ano, limite de cinco, desempate por ID, mês inválido, propagação de falha SQL e recuperação. Reabertura confirmou indicadores, estoque e histórico persistidos.

Para repetir: compile src/main/java e src/test/java com as dependências do POM; use classpath absoluto. Execute DashboardIntegrationTest fresh em um diretório vazio, depois DashboardIntegrationTest reopen no mesmo diretório em outro processo. O teste fresh recusa sobrescrever trufas.db existente. Nunca execute os testes no diretório do banco real. Fixtures históricas são apenas dados de teste, não dados da interface.

## Roteiro manual pendente

1. Executar o projeto pelo main ChocoFlow: conferir principal e Dashboard inicial, inclusive em banco vazio.
2. Conferir layout em notebook, tamanho mínimo, redimensionamento, divisor das tabelas e formato monetário.
3. Abrir Insumos várias vezes pelo menu: deve reutilizar a mesma janela. Minimizar e abrir novamente deve restaurá-la. Fechar Insumos deve manter a principal. Reabrir deve funcionar.
4. Repetir o mesmo fluxo com Compras.
5. Cadastrar/inativar Insumo, voltar à principal e conferir total de ativos e estoque baixo.
6. Registrar compra no mês atual, voltar e conferir redução dos alertas, contagem e soma mensal e últimas compras.
7. Cancelar essa compra, voltar e conferir estorno dos alertas, redução dos indicadores mensais e status CANCELADA no histórico.
8. Clicar Dashboard para atualizar explicitamente. Fechar/reabrir o projeto e conferir persistência.
9. Conferir que opções futuras não abrem módulos.
10. Testar Sair e o X da principal: Não mantém janelas e dados digitados; Sim encerra tudo. Testar também fechar a confirmação sem escolher Sim.

## Limitações e TODOs

- Os testes automatizados cobrem dados e integração, não cliques nem aparência. Navegação, confirmação e redimensionamento aguardam a conferência manual acima.
- As consultas são curtas e síncronas na fila de eventos Swing. Se o volume crescer ou o banco ficar lento, considerar SwingWorker numa etapa posterior.
- Sem nova transação: consultas diferentes podem observar momentos ligeiramente diferentes se outra instância do programa alterar o banco simultaneamente. Não há suporte multiusuário novo.
- O tratamento de erros de Database.criarBanco existente continua registrando falhas no console; o Dashboard exibe erro quando não consegue consultar. Uma melhoria futura pode centralizar a falha de inicialização.
- Conexões persistentes dos módulos existentes mantêm seu ciclo de vida anterior. As conexões do Dashboard são fechadas a cada atualização.
- Nenhum módulo futuro foi iniciado, nem foram adicionados gráficos, autenticação ou conversão de unidades.

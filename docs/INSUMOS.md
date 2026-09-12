# Insumos — conclusão da consulta

## Alterações

A consulta lista somente insumos ativos, ordenados por descrição. Consultar ou Enter pesquisa parte da descrição; espaços nas extremidades são ignorados. Texto vazio mostra todos os ativos. O botão Limpar pesquisa apaga o filtro e consulta novamente o banco. %, _ e ! são pesquisados literalmente, sem virar curingas. O termo é enviado por PreparedStatement.

A tabela mantém as seis colunas existentes e os valores numéricos. O último preço recebe apresentação monetária em reais, sem modificar o banco. Uma mensagem na aba informa o número de resultados ou a ausência deles, sem diálogos repetitivos. Erros de consulta limpam a tabela e mostram uma mensagem com orientação para tentar novamente.

A carga ocorre na abertura, entrada na aba Consulta, reativação da janela quando essa aba está visível, depois de cadastrar/alterar/inativar, ao consultar e ao limpar pesquisa. Cada carga faz uma nova consulta através do Service; não há timer nem lista de estoque mantida em memória entre consultas. O filtro atual é preservado após alterações; um registro que não corresponda ao filtro só reaparecerá ao limpar a pesquisa.

O duplo clique busca o registro ativo novamente pelo ID, converte o índice visual para o índice do modelo e carrega o enum correto no combo. Cliques fora das linhas são ignorados. ID, estoque atual e último valor continuam somente leitura. O Cadastro é aberto somente após recuperar o registro.

Corrigidas inconsistências preexistentes: editarInsumo não recebia ID, então tentava atualizar id=0; o botão Salvar rotulado Alterar continuava criando um novo registro. Agora a edição usa o ID e ambos os caminhos de alteração atualizam o registro selecionado. Novo/Cancelar restabelecem o modo de cadastro. O botão Novo da Consulta abre um cadastro limpo.

A edição atualiza somente descrição, unidade e estoque mínimo. Inativar mantém soft delete e verifica o registro afetado. O Service valida descrição, unidade, ID e mínimo finito/não negativo. Insumos inativos não são carregados para edição.

## Arquivos e métodos

Alterados somente nesta etapa:
- `src/main/java/repository/InsumoRepository.java`: sobrecarga `ListarInsumos(String)`, `buscarPorId(int)` e mapeamento `lerInsumo(ResultSet)`; recursos SQL fechados corretamente nos métodos alterados; UPDATE condicionado a ativo e verificação das linhas afetadas.
- `src/main/java/service/InsumoService.java`: sobrecarga `ListarInsumos(String)`, implementação de `buscarPorId(int)`, validações `validarId`/`validarDados`; `editarInsumo` passa a receber o ID necessário.
- `src/main/java/view/insumo/FrmInsumo.java`: reutilizado `CarregarInsumos`; adicionados `configurarConsulta`, `atualizarConsulta`, `limparPesquisa`, `limparCadastro` e `mostrarErro`; eventos existentes corrigidos. O main da própria tela inicializa o banco para Run File.

Criados:
- `src/test/java/InsumosIntegrationTest.java`.
- `docs/INSUMOS.md` (este relatório).

Compras, entidade Insumo, enum, banco/schema, POM e ConnectionFactory não foram modificados nesta etapa. Os hashes de CompraRepository, CompraService, FrmCompra, FrmInsumo.form e do banco real trufas.db foram comparados antes/depois e permaneceram iguais. Os blocos `initComponents` e `variables` também permaneceram iguais. Os componentes auxiliares da consulta são configurados após initComponents, mantendo o arquivo do GUI Builder intacto.

## Verificação executada

Compilação de todos os fontes principais e de teste aprovada com JDK 25.0.2, `--release 22`, SQLite JDBC 3.50.3.0 e JDatePicker 1.3.4 já usados no projeto. Nenhuma dependência foi adicionada. Maven não foi usado; a compilação foi realizada diretamente com javac.

34 verificações passaram em banco isolado:
- 31 verificações na primeira execução: cadastro, listagem inicial, nome completo, busca parcial, maiúsculas/minúsculas ASCII, espaços, resultado vazio, filtro vazio, caracteres especiais, parâmetro SQL seguro, busca por ID, alteração sem duplicação, manutenção do estoque/preço, inativação, registro preservado, validações e recuperação após erro SQL simulado.
- Registro e cancelamento de compra reais via CompraService existente, mantendo a mesma instância de InsumoService aberta: a consulta refletiu entrada de 1000 e estorno para 0, além do preço atualizado. Nenhum código de Compras foi alterado.
- 3 verificações em outro processo Java reabrindo o mesmo banco: estoque/preço, dados editados e inativação persistidos.

Para repetir: compile src/main/java e src/test/java com as dependências do POM e classpath absoluto. Em diretório vazio, execute `InsumosIntegrationTest fresh`. Em seguida, execute `InsumosIntegrationTest reopen` no mesmo diretório em outro processo. O cenário fresh recusa um diretório que já contenha trufas.db. Nunca executar no diretório do banco real.

## Testes manuais pendentes

1. Abrir FrmInsumo no NetBeans e confirmar o modo Design e o layout da aba Consulta.
2. Cadastrar, pesquisar nome completo/parcial, variar maiúsculas, testar pesquisa vazia e inexistente, usar Enter e Limpar pesquisa. Conferir a mensagem e a tabela vazia sem alerta repetitivo.
3. Dar duplo clique em uma linha e conferir todos os seis campos, unidade selecionada e campos controlados somente leitura. Clicar fora das linhas não deve abrir outro registro.
4. Alterar por Salvar/Alterar e pelo botão Alterar existente; confirmar ausência de duplicação, limpeza do cadastro e recarga. Usar Novo da Consulta e Cancelar, e confirmar que um novo cadastro não altera o registro anterior.
5. Inativar e limpar o filtro: o registro não pode aparecer. Testar Alterar/Excluir sem selecionar registro.
6. Manter Insumos aberto, registrar compra, voltar à Consulta e conferir saldo/preço. Cancelar compra e conferir estorno ao retornar/consultar novamente.
7. Fechar/reabrir a aplicação e conferir persistência. Conferir a apresentação monetária e a disposição dos botões em sua resolução.

Os testes executados cobrem Service/Repository/SQLite; cliques e aparência da janela não foram automatizados.

## Limitações

O LIKE nativo do SQLite ignora diferenças de maiúsculas/minúsculas para ASCII, mas não faz equivalência geral entre letras Unicode acentuadas nem remove acentos. Não foi adicionada extensão ou biblioteca para isso. O Context7 foi consultado, mas não encontrou o tópico; o comportamento foi confirmado na documentação oficial: https://www.sqlite.org/lang_expr.html#the_like_glob_regexp_match_and_extract_operators.

A conexão persistente do Repository foi mantida conforme arquitetura existente; uma política geral de fechamento de conexões permanece melhoria futura. A atualização é orientada a eventos, não contínua. Estoque baixo não recebe cores nesta etapa. Não foram implementados reativação, conversão de unidades, menu ou outro módulo.

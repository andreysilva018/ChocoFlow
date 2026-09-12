# Padronização visual — Insumos e Compras

## Referência e escopo

A FrmPrincipal aprovada foi lida e usada como referência; ela não possui .form. Foram comparadas imagens da principal e das duas telas antigas com as telas estilizadas, renderizadas pelo Swing em banco isolado.

Paleta exata reutilizada:
- fundo RGB(247,245,242);
- marrom RGB(66,44,36);
- branco Color.WHITE para cabeçalho e área de conteúdo;
- demais cores/fontes dos controles vêm do Look and Feel do sistema, como na principal.

Escala: marca 24 px em negrito, título do módulo 22 px, títulos de seção 15 px, total da compra 26 px. Campos 14 px, tabelas com a fonte do tema e linhas de 28 px. Espaçamentos de cabeçalho 18/24 px e margens laterais 22 px acompanham a referência. Não foi criada outra paleta nem alterada FrmPrincipal.

## Arquivos desta etapa

Alterados:
- src/main/java/view/insumo/FrmInsumo.java: uma chamada no construtor e o método aplicarEstiloVisual.
- src/main/java/view/compra/FrmCompra.java: uma chamada no construtor e o método aplicarEstiloVisual.

Criados:
- src/main/java/util/EstiloUI.java: pequenos métodos de apresentação reutilizados pelas duas telas.
- docs/VISUAL.md: este relatório.

O programa auxiliar de renderização e verificação Swing e suas imagens ficam em target/visual-verificacao. Não fazem parte da aplicação.

## Insumos

Cabeçalho ChocoFlow / Insumos, fundo claro, abas preservadas e card branco de cadastro. Código, descrição/unidade, estoque mínimo/atual e último preço foram alinhados. Campos continuam diretamente em jPanel2: LimparCampos mantém seu comportamento anterior. Campos desabilitados/somente leitura têm fundo claro e texto marrom legível; os editáveis passam a branco quando habilitados, sem alteração dos estados.

As ações foram agrupadas sob os campos: Salvar marrom; Novo, Alterar e Cancelar secundários; Excluir com contorno discreto. Na Consulta, título, instrução, pesquisa e botão ficam acima da tabela; Novo, Limpar pesquisa e contador permanecem abaixo. A JTable e seu renderer monetário foram preservados.

## Compras

Cabeçalho ChocoFlow / Compras e as mesmas abas/cores. Cadastro dividido visualmente em Dados da compra, Adicionar item, Itens da compra e faixa de total. Data e pagamento agrupados; insumo, quantidade e Valor Pago alinhados; tabela usa o espaço vertical flexível. Total ganha fonte maior e indicação R$, sem formatar/reinterpretar seu conteúdo ou alterar cálculo.

Os botões de cadastro foram alinhados ao rodapé; estados desabilitados continuam iguais. Os botões já existentes Ver itens da compra e Cancelar compra selecionada foram movidos para baixo da tabela de Consulta, com seus próprios listeners preservados. O cancelamento recebe contorno marrom discreto. Calendário, obtenção de data e componentes internos do JDatePicker não foram alterados.

## Preservação do comportamento e GUI Builder

Os dois .form, initComponents e variables declaration estão idênticos ao início desta etapa. A comparação textual, retirando somente o método visual novo e sua chamada, confirmou que todo o código anterior das duas Views permanece igual. Nenhum listener de ação/mouse anterior foi substituído. O helper acrescenta somente observação visual dos estados enabled/editable para atualizar o fundo dos campos; não altera esses estados.

Hashes de Services, Repositories, models, Database, FrmPrincipal, arquivos .form e trufas.db real permaneceram iguais. Não houve mudança de SQL, regras, navegação, persistência, estoque ou cancelamento.

O Designer do NetBeans continua mostrando a estrutura base original. A nova disposição é aplicada em tempo de execução, após a criação dos componentes. Não foi gravada a nova disposição nos .form, evitando mexer nas regiões geradas. Futuras renomeações/exclusões de componentes no Designer precisam considerar as referências no método visual, como já acontece com outros métodos auxiliares.

## Compilação e verificações

Compilação aprovada após Insumos e novamente após Compras, com JDK 25.0.2 e --release 22, usando as bibliotecas existentes. Não foram adicionadas dependências. Foi utilizado javac, sem modificar POM.

Execução final: 116 verificações aprovadas, todas em bancos separados:
- ComprasIntegrationTest: 38 (incluindo migrações e reabertura).
- InsumosIntegrationTest: 34.
- DashboardIntegrationTest: 26.
- Verificação Swing: 18.

A verificação Swing acionou os próprios botões e eventos: Novo, cadastro, pesquisa, limpeza, duplo clique, alteração, cancelamento do cadastro, inativação; adicionar/remover item, salvar compra, consulta, filtro por período, abrir itens, cancelar compra, conferir estoque e status. Verificou também limpeza dos campos, estados desabilitados e somente leitura. Os diálogos de confirmação desse teste foram respondidos automaticamente em seu próprio banco de teste.

Imagens das duas abas foram inspecionadas em 1080×740, além dos cadastros no tamanho mínimo de 850×680. Foram comparadas com imagens das telas anteriores e da principal. Renderização Swing não equivale a uma revisão manual completa da experiência de teclado, foco e escala do monitor.

## Conferência manual restante

1. Abrir Insumos e Compras pelo menu e conferir consistência visual com o Dashboard no seu monitor e escala do Windows.
2. Repetir os fluxos de cadastro, consulta, limpar, duplo clique, alteração e inativação em Insumos, conferindo foco e navegação por Tab.
3. Conferir o calendário ao abrir, escolher data e salvar; o teste usou a data padrão, sem automatizar o popup do calendário.
4. Em Compras, conferir adição/remoção de vários itens, total, consulta, detalhes e cancelamento. Testar responder Não às confirmações e validar mensagens de erro.
5. Voltar ao Dashboard e à consulta de Insumos após compra/estorno, conferindo atualização e navegação entre janelas.
6. Redimensionar as telas, conferir colunas com descrições longas, seleção, valores monetários, estados desabilitados e legibilidade.

Nenhum outro módulo ou funcionalidade foi iniciado.

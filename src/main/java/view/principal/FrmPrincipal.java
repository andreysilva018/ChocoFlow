package view.principal;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.compra.Compra;
import model.dashboard.ResumoDashboard;
import model.producao.Insumo;
import service.DashboardService;
import view.compra.FrmCompra;
import view.insumo.FrmInsumo;

/** Tela montada em Swing padrão, sem regiões geradas pelo GUI Builder. */
public class FrmPrincipal extends JFrame {
    private static final Color FUNDO = new Color(247, 245, 242);
    private static final Color CACAU = new Color(66, 44, 36);
    private final DashboardService service = new DashboardService();
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
    private final JLabel totalInsumos = valorCard();
    private final JLabel estoqueBaixo = valorCard();
    private final JLabel comprasMes = valorCard();
    private final JLabel valorMes = valorCard();
    private final JLabel periodo = new JLabel(" ");
    private final JLabel mensagem = new JLabel(" ");
    private final JLabel mensagemEstoque = new JLabel(" ");
    private final JLabel mensagemCompras = new JLabel(" ");
    private final DefaultTableModel modeloEstoque = modelo("Descrição", "Estoque atual", "Mínimo", "Unidade");
    private final DefaultTableModel modeloCompras = modelo("ID", "Data", "Valor total", "Status");
    private FrmInsumo frmInsumo;
    private FrmCompra frmCompra;

    public FrmPrincipal() {
        super("ChocoFlow");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(800, 680));
        setSize(1080, 740);
        setLocationRelativeTo(null);
        criarInterface();
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { confirmarSaida(); }
            @Override public void windowActivated(WindowEvent e) { atualizarDashboard(); }
        });
        atualizarDashboard();
    }

    private void criarInterface() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(FUNDO);
        setContentPane(raiz);
        JPanel cabecalho = new JPanel(new BorderLayout(20, 0));
        cabecalho.setBackground(Color.WHITE);
        cabecalho.setBorder(new EmptyBorder(18, 24, 18, 24));
        JLabel marca = new JLabel("ChocoFlow");
        marca.setFont(marca.getFont().deriveFont(Font.BOLD, 24f));
        marca.setForeground(CACAU);
        JPanel identidade = new JPanel(new GridLayout(2, 1, 0, 4));
        identidade.setOpaque(false);
        identidade.add(marca);
        identidade.add(new JLabel("Gestão de produção de trufas"));
        cabecalho.add(identidade, BorderLayout.CENTER);
        cabecalho.add(new JLabel("Versão Alpha"), BorderLayout.EAST);
        raiz.add(cabecalho, BorderLayout.NORTH);

        JPanel menu = new JPanel(new BorderLayout());
        menu.setBackground(CACAU);
        menu.setBorder(new EmptyBorder(20, 12, 20, 12));
        menu.setPreferredSize(new Dimension(170, 0));
        JPanel opcoes = new JPanel(new GridLayout(7, 1, 0, 10));
        opcoes.setOpaque(false);
        JButton dashboard = botaoMenu("Dashboard");
        dashboard.addActionListener(e -> atualizarDashboard());
        opcoes.add(dashboard);
        JButton insumos = botaoMenu("Insumos");
        insumos.addActionListener(e -> abrirInsumos());
        opcoes.add(insumos);
        JButton compras = botaoMenu("Compras");
        compras.addActionListener(e -> abrirCompras());
        opcoes.add(compras);
        for (String nome : new String[]{"Trufas", "Remessas", "Vendas", "Financeiro"}) {
            JButton futuro = botaoMenu(nome);
            futuro.setEnabled(false);
            futuro.setToolTipText("Em breve");
            opcoes.add(futuro);
        }
        menu.add(opcoes, BorderLayout.NORTH);
        JButton sair = botaoMenu("Sair");
        sair.addActionListener(e -> confirmarSaida());
        menu.add(sair, BorderLayout.SOUTH);
        raiz.add(menu, BorderLayout.WEST);

        JPanel conteudo = new JPanel(new BorderLayout(0, 16));
        conteudo.setOpaque(false);
        conteudo.setBorder(new EmptyBorder(20, 22, 16, 22));
        JPanel topo = new JPanel(new BorderLayout(0, 12));
        topo.setOpaque(false);
        JPanel titulo = new JPanel(new BorderLayout());
        titulo.setOpaque(false);
        JLabel nomeDashboard = new JLabel("Dashboard");
        nomeDashboard.setFont(nomeDashboard.getFont().deriveFont(Font.BOLD, 22f));
        titulo.add(nomeDashboard, BorderLayout.WEST);
        titulo.add(periodo, BorderLayout.EAST);
        topo.add(titulo, BorderLayout.NORTH);
        JPanel cards = new JPanel(new GridLayout(2, 2, 12, 12));
        cards.setOpaque(false);
        cards.add(card("Insumos ativos", totalInsumos));
        cards.add(card("Estoque baixo", estoqueBaixo));
        cards.add(card("Compras ativas no mês", comprasMes));
        cards.add(card("Valor comprado no mês", valorMes));
        topo.add(cards, BorderLayout.CENTER);
        conteudo.add(topo, BorderLayout.NORTH);

        JTable tabelaEstoque = tabela(modeloEstoque);
        JTable tabelaCompras = tabela(modeloCompras);
        tabelaCompras.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override protected void setValue(Object valor) {
                setHorizontalAlignment(SwingConstants.RIGHT);
                setText(valor instanceof Number ? moeda.format(valor) : "");
            }
        });
        JSplitPane secoes = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                secao("Estoque baixo", tabelaEstoque, mensagemEstoque),
                secao("Últimas compras", tabelaCompras, mensagemCompras));
        secoes.setBorder(null);
        secoes.setResizeWeight(0.5);
        secoes.setDividerSize(8);
        conteudo.add(secoes, BorderLayout.CENTER);
        conteudo.add(mensagem, BorderLayout.SOUTH);
        raiz.add(conteudo, BorderLayout.CENTER);
    }

    private JButton botaoMenu(String titulo) {
        JButton botao = new JButton(titulo);
        botao.setHorizontalAlignment(SwingConstants.LEFT);
        botao.setPreferredSize(new Dimension(145, 38));
        botao.setMargin(new Insets(8, 12, 8, 12));
        return botao;
    }
    private JLabel valorCard() {
        JLabel valor = new JLabel("—");
        valor.setFont(valor.getFont().deriveFont(Font.BOLD, 26f));
        valor.setForeground(CACAU);
        return valor;
    }
    private JPanel card(String titulo, JLabel valor) {
        JPanel painel = new JPanel(new BorderLayout(0, 6));
        painel.setBackground(Color.WHITE);
        painel.setBorder(new EmptyBorder(12, 16, 12, 16));
        painel.add(new JLabel(titulo), BorderLayout.NORTH);
        painel.add(valor, BorderLayout.CENTER);
        return painel;
    }
    private DefaultTableModel modelo(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int linha, int coluna) { return false; }
        };
    }
    private JTable tabela(DefaultTableModel modelo) {
        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(28);
        tabela.setFillsViewportHeight(true);
        tabela.setShowVerticalLines(false);
        tabela.getTableHeader().setReorderingAllowed(false);
        return tabela;
    }
    private JPanel secao(String titulo, JTable tabela, JLabel vazio) {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(Color.WHITE);
        painel.setBorder(new EmptyBorder(10, 12, 10, 12));
        JLabel label = new JLabel(titulo);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 15f));
        painel.add(label, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setPreferredSize(new Dimension(400, 110));
        painel.add(scroll, BorderLayout.CENTER);
        painel.add(vazio, BorderLayout.SOUTH);
        painel.setMinimumSize(new Dimension(0, 120));
        return painel;
    }

    public void atualizarDashboard() {
        try {
            ResumoDashboard resumo = service.consultarDashboard();
            totalInsumos.setText(String.valueOf(resumo.getInsumosAtivos()));
            estoqueBaixo.setText(String.valueOf(resumo.getEstoqueBaixo().size()));
            comprasMes.setText(String.valueOf(resumo.getComprasMes()));
            valorMes.setText(moeda.format(resumo.getValorCompradoMes()));
            periodo.setText(resumo.getMes().format(DateTimeFormatter.ofPattern("MM/yyyy")));
            modeloEstoque.setRowCount(0);
            for (Insumo insumo : resumo.getEstoqueBaixo())
                modeloEstoque.addRow(new Object[]{insumo.getDescricao(), insumo.getQtdEstoque(), insumo.getEstoqueMin(), insumo.getUnidademedida()});
            modeloCompras.setRowCount(0);
            for (Compra compra : resumo.getUltimasCompras())
                modeloCompras.addRow(new Object[]{compra.getId(), compra.getDataCompra().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), compra.getValorTotal(), compra.getStatus()});
            mensagemEstoque.setText(resumo.getEstoqueBaixo().isEmpty() ? "Nenhum insumo com estoque no mínimo ou abaixo dele." : "Estoque atual menor ou igual ao mínimo; somente ativos.");
            mensagemCompras.setText(resumo.getUltimasCompras().isEmpty() ? "Nenhuma compra registrada." : "Histórico das últimas 5 compras, incluindo canceladas.");
            mensagem.setText("Dados atualizados. Clique em Dashboard para consultar novamente.");
        } catch (Exception erro) {
            erro.printStackTrace();
            for (JLabel valor : new JLabel[]{totalInsumos, estoqueBaixo, comprasMes, valorMes}) valor.setText("—");
            modeloEstoque.setRowCount(0);
            modeloCompras.setRowCount(0);
            mensagemEstoque.setText("Dados indisponíveis.");
            mensagemCompras.setText("Dados indisponíveis.");
            mensagem.setText("Não foi possível atualizar. Verifique o banco e clique em Dashboard.");
        }
    }

    private void abrirInsumos() {
        try {
            if (frmInsumo == null || !frmInsumo.isDisplayable()) {
                frmInsumo = new FrmInsumo();
                prepararModulo(frmInsumo);
            }
            mostrarModulo(frmInsumo);
        } catch (Exception erro) { erroModulo(erro); }
    }
    private void abrirCompras() {
        try {
            if (frmCompra == null || !frmCompra.isDisplayable()) {
                frmCompra = new FrmCompra();
                prepararModulo(frmCompra);
            }
            mostrarModulo(frmCompra);
        } catch (Exception erro) { erroModulo(erro); }
    }
    private void prepararModulo(JFrame modulo) {
        modulo.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        modulo.setLocationRelativeTo(this);
        modulo.addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) { atualizarDashboard(); }
        });
    }
    private void mostrarModulo(JFrame modulo) {
        modulo.setExtendedState(modulo.getExtendedState() & ~JFrame.ICONIFIED);
        modulo.setVisible(true);
        modulo.toFront();
        modulo.requestFocus();
    }
    private void erroModulo(Exception erro) {
        erro.printStackTrace();
        JOptionPane.showMessageDialog(this, "Não foi possível abrir o módulo. Verifique o banco de dados.", "ChocoFlow", JOptionPane.ERROR_MESSAGE);
    }
    private void confirmarSaida() {
        if (JOptionPane.showConfirmDialog(this, "Deseja realmente sair do ChocoFlow? Dados não salvos serão perdidos.",
                "Sair", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (frmInsumo != null) frmInsumo.dispose();
            if (frmCompra != null) frmCompra.dispose();
            dispose();
            System.exit(0);
        }
    }
}


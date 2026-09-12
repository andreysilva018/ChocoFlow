package util;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Somente apresentação; não altera modelos, eventos nem estados enabled/editable. */
public final class EstiloUI {
    // Valores exatos da FrmPrincipal aprovada.
    public static final Color FUNDO = new Color(247, 245, 242);
    public static final Color CACAU = new Color(66, 44, 36);
    private EstiloUI() { }

    public static void janela(JFrame janela, JTabbedPane abas, String modulo) {
        JPanel raiz = new JPanel(new BorderLayout(0, 16));
        raiz.setBackground(FUNDO);
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(Color.WHITE);
        cabecalho.setBorder(new EmptyBorder(18, 24, 18, 24));
        JLabel marca = new JLabel("ChocoFlow");
        marca.setFont(marca.getFont().deriveFont(Font.BOLD, 24f));
        marca.setForeground(CACAU);
        cabecalho.add(marca, BorderLayout.WEST);
        JLabel titulo = new JLabel(modulo);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 22f));
        titulo.setForeground(CACAU);
        cabecalho.add(titulo, BorderLayout.EAST);
        raiz.add(cabecalho, BorderLayout.NORTH);
        JPanel conteudo = new JPanel(new BorderLayout());
        conteudo.setOpaque(false);
        conteudo.setBorder(new EmptyBorder(0, 22, 20, 22));
        abas.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 14f));
        abas.setBackground(FUNDO);
        abas.setForeground(CACAU);
        conteudo.add(abas);
        raiz.add(conteudo, BorderLayout.CENTER);
        janela.setContentPane(raiz);
        janela.setTitle("ChocoFlow — " + modulo);
        janela.setMinimumSize(new Dimension(850, 680));
        janela.setSize(1080, 740);
        janela.setLocationRelativeTo(null);
    }

    public static void painel(JPanel painel) {
        painel.removeAll();
        painel.setLayout(new GridBagLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(new EmptyBorder(16, 20, 16, 20));
    }

    public static void colocar(JPanel painel, Component componente, int x, int y,
            int largura, double pesoX, double pesoY) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=x; c.gridy=y; c.gridwidth=largura;
        c.weightx=pesoX; c.weighty=pesoY;
        c.fill=pesoY > 0 ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;
        c.anchor=GridBagConstraints.NORTHWEST;
        c.insets=new Insets(4, 6, 6, 6);
        painel.add(componente,c);
    }

    public static JLabel titulo(String texto) {
        JLabel label=new JLabel(texto);
        label.setFont(label.getFont().deriveFont(Font.BOLD,15f));
        label.setForeground(CACAU);
        return label;
    }

    public static JPanel acoes(Component... componentes) {
        JPanel painel=new JPanel(new FlowLayout(FlowLayout.LEFT,10,4));
        painel.setOpaque(false);
        for(Component componente:componentes) painel.add(componente);
        return painel;
    }

    public static void componentes(Container raiz) {
        for(Component componente:raiz.getComponents()) {
            if(componente.getClass().getPackageName().startsWith("org.jdatepicker")) continue;
            if(componente instanceof JButton botao) {
                botao.setFont(UIManager.getFont("Button.font"));
                botao.setMargin(new Insets(8,12,8,12));
            } else if(componente instanceof JTextField campo) {
                campo.setFont(UIManager.getFont("TextField.font").deriveFont(14f));
                campo.setPreferredSize(new Dimension(120,34));
                campo.setMinimumSize(new Dimension(60,34));
                campo.setBackground(!campo.isEnabled() || !campo.isEditable() ? FUNDO : Color.WHITE);
                campo.setDisabledTextColor(CACAU);
                campo.addPropertyChangeListener("enabled", e -> campo.setBackground(!campo.isEnabled() || !campo.isEditable() ? FUNDO : Color.WHITE));
                campo.addPropertyChangeListener("editable", e -> campo.setBackground(!campo.isEnabled() || !campo.isEditable() ? FUNDO : Color.WHITE));
            } else if(componente instanceof JComboBox<?> combo) {
                combo.setFont(UIManager.getFont("ComboBox.font").deriveFont(14f));
                combo.setPreferredSize(new Dimension(160,34));
                combo.setMinimumSize(new Dimension(80,34));
            } else if(componente instanceof JTable tabela) {
                tabela.setFont(UIManager.getFont("Table.font"));
                tabela.setRowHeight(28);
                tabela.setFillsViewportHeight(true);
                tabela.setShowVerticalLines(false);
                tabela.setSelectionBackground(CACAU);
                tabela.setSelectionForeground(Color.WHITE);
                tabela.getTableHeader().setFont(UIManager.getFont("TableHeader.font").deriveFont(Font.BOLD));
                tabela.getTableHeader().setBackground(FUNDO);
                tabela.getTableHeader().setForeground(CACAU);
            }
            // Não percorrer internos do calendário, combos, campos ou tabelas.
            if(componente instanceof JPanel || componente instanceof JTabbedPane || componente instanceof JScrollPane || componente instanceof JViewport)
                componentes((Container)componente);
        }
    }

    public static void principal(JButton botao) {
        botao.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        botao.setBackground(CACAU);
        botao.setForeground(Color.WHITE);
        botao.setOpaque(true);
        botao.setBorderPainted(false);
    }

    public static void destrutivo(JButton botao) {
        botao.setForeground(CACAU);
        botao.setToolTipText(botao.getToolTipText());
        botao.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(CACAU),new EmptyBorder(8,12,8,12)));
    }
}



import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import javax.imageio.ImageIO;
import javax.swing.*;
import database.Database;
import enums.UnidadeMedida;
import service.InsumoService;
import view.insumo.FrmInsumo;
import view.compra.FrmCompra;
import view.principal.FrmPrincipal;

public class VisualCheck {
    static int checks;
    static Object get(Object obj,String nome) throws Exception { Field f=obj.getClass().getDeclaredField(nome);f.setAccessible(true);return f.get(obj); }
    static void text(Object obj,String nome,String valor) throws Exception { ((JTextField)get(obj,nome)).setText(valor); }
    static void click(Object obj,String nome) throws Exception { ((JButton)get(obj,nome)).doClick(); }
    static void check(boolean ok,String msg) { if(!ok)throw new AssertionError(msg);checks++; }
    static JButton find(Container raiz,String texto) {
        for(Component c:raiz.getComponents()) { if(c instanceof JButton b && texto.equals(b.getText()))return b; if(c instanceof Container p){JButton b=find(p,texto);if(b!=null)return b;} }return null;
    }
    static void responder(Container raiz) {
        if(raiz instanceof JOptionPane p) { p.setValue(JOptionPane.YES_OPTION);return; }
        for(Component c:raiz.getComponents()) if(c instanceof Container p)responder(p);
    }
    static void render(JFrame frame,String nome) throws Exception {
        frame.addNotify(); frame.setSize(1080,740); frame.validate();
        Container root=frame.getContentPane();
        BufferedImage imagem=new BufferedImage(root.getWidth(),root.getHeight(),BufferedImage.TYPE_INT_RGB);
        Graphics2D g=imagem.createGraphics();root.printAll(g);g.dispose();ImageIO.write(imagem,"png",new File(nome+".png"));
    }
    public static void main(String[] args) throws Exception {
        if(new File("trufas.db").exists())throw new IllegalStateException("Diretório de teste deve estar vazio");
        Database.criarBanco();
        InsumoService insumos=new InsumoService();insumos.cadastrarInsumo("Chocolate de teste",UnidadeMedida.KG,5);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        try { SwingUtilities.invokeAndWait(()->{
            try {
                FrmPrincipal principal=new FrmPrincipal(); render(principal,"principal");principal.dispose();
                FrmInsumo i=new FrmInsumo();
                render(i,"insumos-cadastro");
                JTabbedPane ti=(JTabbedPane)get(i,"jTabbedPane1");ti.setSelectedIndex(1);render(i,"insumos-consulta");
                FrmCompra c=new FrmCompra();render(c,"compras-cadastro");
                JTabbedPane tc=(JTabbedPane)get(c,"jTabbedPane1");tc.setSelectedIndex(1);render(c,"compras-consulta");
                if(args[0].equals("after")) {
                    javax.swing.Timer dialogos=new javax.swing.Timer(50,e->{for(Window w:Window.getWindows())if(w instanceof JDialog && w.isVisible()){responder(w);w.dispose();}});
                    dialogos.start();
                    try {
                        click(i,"btnNovoConsulta");text(i,"txtDescricao","Insumo visual");text(i,"txtEstoqueMin","2");
                        ((JComboBox<?>)get(i,"cbxUnidade")).setSelectedItem(UnidadeMedida.UN);click(i,"btnSalvar");
                        check(insumos.ListarInsumos("Insumo visual").size()==1,"Salvar insumo");
                        check(((JTextField)get(i,"txtDescricao")).getText().isEmpty(),"Limpeza pós cadastro");
                        ti.setSelectedIndex(1);text(i,"txtConsultarDescricao","visual");click(i,"btnConsultar");
                        JTable tabela=(JTable)get(i,"jTableInsumos");check(tabela.getRowCount()==1,"Pesquisa");
                        tabela.dispatchEvent(new MouseEvent(tabela,MouseEvent.MOUSE_CLICKED,System.currentTimeMillis(),0,10,10,2,false,MouseEvent.BUTTON1));
                        check(ti.getSelectedIndex()==0 && !((JTextField)get(i,"txtCodigo")).getText().isEmpty(),"Duplo clique");
                        text(i,"txtDescricao","Insumo editado");click(i,"btnSalvar");check(insumos.ListarInsumos("editado").size()==1,"Alterar");
                        click(i,"btnNovo");text(i,"txtDescricao","Rascunho");click(i,"btnCancelar");check(((JTextField)get(i,"txtDescricao")).getText().isEmpty(),"Cancelar limpa");
                        ti.setSelectedIndex(1);find(i.getContentPane(),"Limpar pesquisa").doClick();check(tabela.getRowCount()==2,"Limpar pesquisa");
                        text(i,"txtConsultarDescricao","editado");click(i,"btnConsultar");
                        tabela.dispatchEvent(new MouseEvent(tabela,MouseEvent.MOUSE_CLICKED,System.currentTimeMillis(),0,10,10,2,false,MouseEvent.BUTTON1));click(i,"btnExcluir");check(insumos.ListarInsumos("editado").isEmpty(),"Inativar");
                        tc.setSelectedIndex(0);click(c,"btnNovoCompra");
                        JComboBox<?> combo=(JComboBox<?>)get(c,"jcbxInsumo");combo.setSelectedIndex(0);
                        text(c,"txtQtd","10");text(c,"txtValorPago","25");click(c,"btnAdicionarItem");
                        JTable itens=(JTable)get(c,"jTableItems");check(itens.getRowCount()==1,"Adicionar item");
                        itens.setRowSelectionInterval(0,0);click(c,"btnRemoverItem");check(itens.getRowCount()==0,"Remover item");
                        click(c,"btnAdicionarItem");click(c,"btnSalvarCompra");check(insumos.ListarInsumos("Chocolate").get(0).getQtdEstoque()==10,"Compra soma estoque");
                        check(((JTextField)get(c,"txtQtd")).getText().isEmpty(),"Compra limpa campos");
                        tc.setSelectedIndex(1);click(c,"btnConsultar");JTable compras=(JTable)get(c,"jTable1");check(compras.getRowCount()==1,"Consultar compra");
                        text(c,"txtDataInicial","01/01/2099");click(c,"btnConsultar");check(compras.getRowCount()==0,"Filtro período");text(c,"txtDataInicial","");click(c,"btnConsultar");compras.setRowSelectionInterval(0,0);
                        find(c.getContentPane(),"Ver itens da compra").doClick();find(c.getContentPane(),"Cancelar compra selecionada").doClick();
                        check(insumos.ListarInsumos("Chocolate").get(0).getQtdEstoque()==0,"Cancelamento estorna");
                        check(compras.getValueAt(0,3).toString().equals("CANCELADA"),"Status atualizado");
                        check(!((JButton)get(c,"btnAlterarCompra")).isEnabled() && !((JButton)get(c,"btnExcluir")).isEnabled(),"Ações desabilitadas preservadas");
                        check(!((JTextField)get(i,"txtQtdEstoque")).isEditable() && !((JTextField)get(i,"txtCodigo")).isEditable(),"Somente leitura");
                    } finally { dialogos.stop(); }
                    ti.setSelectedIndex(0);i.setSize(850,680);i.validate();Container root=i.getContentPane();BufferedImage img=new BufferedImage(root.getWidth(),root.getHeight(),1);Graphics2D g=img.createGraphics();root.printAll(g);g.dispose();ImageIO.write(img,"png",new File("insumos-minimo.png"));
                    tc.setSelectedIndex(0);c.setSize(850,680);c.validate();root=c.getContentPane();img=new BufferedImage(root.getWidth(),root.getHeight(),1);g=img.createGraphics();root.printAll(g);g.dispose();ImageIO.write(img,"png",new File("compras-minimo.png"));
                }
                i.dispose();c.dispose();System.out.println("PASS visual "+args[0]+": "+checks+" verificacoes");
            } catch(Exception e) { throw new RuntimeException(e); }
        });
        } catch (Throwable erro) { erro.printStackTrace(); System.exit(1); }
        System.exit(0);
    }
}


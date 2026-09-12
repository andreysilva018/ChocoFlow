import database.Database;
import enums.StatusCompra;
import enums.UnidadeMedida;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import model.compra.*;
import model.producao.Insumo;
import service.CompraService;
import util.ConnectionFactory;

/** Executar somente em diretório de teste vazio: usa trufas.db relativo ao processo. */
public class ComprasIntegrationTest {
    static CompraService service;
    static int checks;
    interface Operacao { void executar() throws Exception; }
    static void verificar(boolean ok, String mensagem) {
        if (!ok) throw new AssertionError(mensagem);
        checks++;
    }
    static void bloquear(Operacao operacao) throws Exception {
        try { operacao.executar(); }
        catch (IllegalArgumentException | IllegalStateException esperado) { checks++; return; }
        throw new AssertionError("Operação inválida foi aceita");
    }
    static void sql(String sql) throws Exception {
        try (Connection c = ConnectionFactory.getConnection(); Statement s = c.createStatement()) { s.executeUpdate(sql); }
    }
    static double numero(String sql) throws Exception {
        try (Connection c = ConnectionFactory.getConnection(); Statement s = c.createStatement(); ResultSet r = s.executeQuery(sql)) {
            r.next(); return r.getDouble(1);
        }
    }
    static double estoque(int id) throws Exception { return numero("SELECT quantidade_estoque FROM insumo WHERE id=" + id); }
    static ItemCompra item(int id, double qtd) {
        Insumo insumo = new Insumo(); insumo.setId(id); insumo.setDescricao("Insumo " + id); insumo.setUnidademedida(UnidadeMedida.KG);
        ItemCompra item = new ItemCompra(); item.setInsumo(insumo); item.setQtd(qtd); item.setValorUnitario(2); return item;
    }
    static Compra compra(ItemCompra... itens) throws Exception {
        Compra c = new Compra(LocalDate.of(2026, 9, 11), 0); c.setItens(List.of(itens)); service.registrarCompra(c); return c;
    }
    public static void main(String[] args) throws Exception {
        if (args.length == 0) throw new IllegalArgumentException("Informe fresh, reopen, boolean ou missing.");
        if (args[0].equals("reopen")) {
            Database.criarBanco(); service = new CompraService();
            verificar(service.consultarCompras(null, null).stream().anyMatch(c -> c.getStatus() == StatusCompra.CANCELADA), "Histórico após reinício");
            verificar(estoque(1) == 29, "Estoque persistido");
        } else {
            if (new java.io.File("trufas.db").exists()) throw new IllegalStateException("Use diretório vazio para proteger dados existentes.");
            if (!args[0].equals("fresh")) {
                String coluna = args[0].equals("boolean") ? ", status BOOLEAN NOT NULL DEFAULT 1" : "";
                sql("CREATE TABLE compra(id INTEGER PRIMARY KEY AUTOINCREMENT, date_compra TEXT NOT NULL, valor_total REAL NOT NULL" + coluna + ")");
                sql("INSERT INTO compra(date_compra,valor_total) VALUES('2026-01-01',12)");
                if (args[0].equals("boolean")) sql("INSERT INTO compra(date_compra,valor_total,status) VALUES('2026-01-02',5,0)");
                Database.criarBanco(); Database.criarBanco(); service = new CompraService();
                verificar(service.consultarCompras(null,null).stream().anyMatch(c -> c.getId()==1 && c.getStatus()==StatusCompra.ATIVA), "Migração da compra ativa");
                if (args[0].equals("boolean")) verificar(service.consultarCompras(null,null).stream().anyMatch(c -> c.getId()==2 && c.getStatus()==StatusCompra.CANCELADA), "Migração da compra cancelada");
                verificar(numero("SELECT valor_total FROM compra WHERE id=1")==12, "Preservação dos dados");
            } else { Database.criarBanco(); Database.criarBanco(); service = new CompraService(); }
            sql("INSERT INTO insumo(descricao,unidade_medida,quantidade_estoque) VALUES('Chocolate','KG',20),('Leite','KG',30)");
            if (args[0].equals("fresh")) testarFluxo();
            else { Compra c=compra(item(1,2)); service.cancelarCompra(c.getId()); verificar(estoque(1)==20, "Compra e cancelamento no schema migrado"); }
        }
        System.out.println("PASS " + args[0] + ": " + checks + " verificações");
    }
    static void testarFluxo() throws Exception {
        Compra unica=compra(item(1,10)); verificar(estoque(1)==30, "Entrada soma ao estoque anterior");
        verificar(unica.getValorTotal()==20 && unica.getStatus()==StatusCompra.ATIVA, "Total e status");
        Compra varios=compra(item(1,3),item(2,4)); verificar(estoque(1)==33 && estoque(2)==34,"Múltiplas entradas");
        List<ItemCompra> itens=service.buscarItensPorCompra(varios.getId());
        verificar(itens.size()==2 && itens.get(0).getValorTotalItem()==6,"Detalhes completos");
        service.cancelarCompra(varios.getId()); verificar(estoque(1)==30 && estoque(2)==30,"Estorno de todos");
        bloquear(() -> service.cancelarCompra(varios.getId())); verificar(estoque(1)==30 && estoque(2)==30,"Sem estorno duplicado");
        sql("UPDATE insumo SET quantidade_estoque=2 WHERE id=1");
        bloquear(() -> service.cancelarCompra(unica.getId())); verificar(estoque(1)==2,"Estoque insuficiente");
        Compra parcial=compra(item(2,8),item(1,10)); sql("UPDATE insumo SET quantidade_estoque=1 WHERE id=1");
        bloquear(() -> service.cancelarCompra(parcial.getId())); verificar(estoque(2)==38 && estoque(1)==1,"Nenhum item estornado se outro falha");
        Compra repetida=compra(item(1,6),item(1,6)); sql("UPDATE insumo SET quantidade_estoque=7 WHERE id=1");
        bloquear(() -> service.cancelarCompra(repetida.getId())); verificar(estoque(1)==7,"Soma de insumos repetidos validada");
        sql("UPDATE insumo SET quantidade_estoque=20 WHERE id=1"); service.cancelarCompra(repetida.getId()); verificar(estoque(1)==8,"Estorno de linhas repetidas");
        bloquear(() -> service.cancelarCompra(0)); bloquear(() -> service.cancelarCompra(9999));
        sql("INSERT INTO compra(date_compra,valor_total,status) VALUES('2026-09-11',0,'ATIVA')");
        int vazia=(int)numero("SELECT max(id) FROM compra"); bloquear(() -> service.cancelarCompra(vazia));
        bloquear(() -> compra(item(1,0))); bloquear(() -> compra(item(1,-2))); bloquear(() -> compra(item(1,Double.NaN)));
        bloquear(() -> compra(item(999,2))); bloquear(() -> compra());
        bloquear(() -> service.consultarCompras(LocalDate.of(2026,9,12),LocalDate.of(2026,9,11)));
        verificar(service.consultarCompras(LocalDate.of(2026,9,12),null).isEmpty(),"Filtro de data");
        verificar(service.consultarCompras(null,null).stream().filter(c -> c.getStatus()==StatusCompra.CANCELADA).count()==2,"Canceladas no histórico");
        verificar(numero("SELECT count(*) FROM item_compra WHERE compra_id="+varios.getId())==2,"Itens preservados");
        // Falha no segundo INSERT deve ser propagada; preservar transação de registro existente.
        double antes=estoque(1); double comprasAntes=numero("SELECT count(*) FROM compra");
        sql("CREATE TRIGGER falhar_item BEFORE INSERT ON item_compra WHEN NEW.insumo_id=2 BEGIN SELECT RAISE(ABORT,'falha teste'); END");
        try { compra(item(1,3),item(2,3)); throw new AssertionError("Falha de banco escondida"); }
        catch (SQLException esperado) { checks++; }
        verificar(estoque(1)==antes && numero("SELECT count(*) FROM compra")==comprasAntes,"Rollback já existente preservado");
        sql("DROP TRIGGER falhar_item");
        compra(item(1,21)); // Também verifica reutilização após falha e persistência em outro processo.
        verificar(estoque(1)==29,"Registro após falha");
    }
}

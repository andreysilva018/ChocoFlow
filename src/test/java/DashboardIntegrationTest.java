import database.Database;
import enums.StatusCompra;
import enums.UnidadeMedida;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import model.compra.*;
import model.dashboard.ResumoDashboard;
import model.producao.Insumo;
import service.*;
import util.ConnectionFactory;

/** Execute fresh em diretório vazio e reopen no mesmo diretório em outro processo. */
public class DashboardIntegrationTest {
    static int checks;
    static void verificar(boolean ok, String descricao) {
        if (!ok) throw new AssertionError(descricao);
        checks++;
    }
    static void inserirCompra(String data, double valor, String status) throws Exception {
        try (Connection c=ConnectionFactory.getConnection(); PreparedStatement s=c.prepareStatement(
                "INSERT INTO compra(date_compra,valor_total,status) VALUES(?,?,?)")) {
            s.setString(1,data); s.setDouble(2,valor); s.setString(3,status); s.executeUpdate();
        }
    }
    public static void main(String[] args) throws Exception {
        if (args.length!=1) throw new IllegalArgumentException("Informe fresh ou reopen");
        DashboardService dashboard = new DashboardService();
        YearMonth fevereiro = YearMonth.of(2024,2);
        if (args[0].equals("reopen")) {
            ResumoDashboard r=dashboard.consultarDashboard(fevereiro);
            verificar(r.getInsumosAtivos()==2 && r.getEstoqueBaixo().size()==1,"Insumos persistidos");
            verificar(r.getComprasMes()==2 && r.getValorCompradoMes()==50,"Valores mensais persistidos");
            verificar(r.getUltimasCompras().size()==5 && r.getUltimasCompras().stream().anyMatch(c->c.getStatus()==StatusCompra.CANCELADA),"Histórico persistido");
        } else {
            if (!args[0].equals("fresh") || new java.io.File("trufas.db").exists()) throw new IllegalStateException("Use diretório vazio para proteger o banco real");
            Database.criarBanco();
            ResumoDashboard r=dashboard.consultarDashboard();
            verificar(r.getInsumosAtivos()==0 && r.getEstoqueBaixo().isEmpty(),"Banco vazio insumos");
            verificar(r.getComprasMes()==0 && r.getValorCompradoMes()==0 && r.getUltimasCompras().isEmpty(),"Banco vazio compras e COALESCE");
            verificar(r.getMes().equals(YearMonth.now()),"Mês local atual");
            InsumoService insumos=new InsumoService();
            insumos.cadastrarInsumo("Chocolate",UnidadeMedida.KG,5);
            insumos.cadastrarInsumo("Açúcar",UnidadeMedida.KG,5);
            insumos.cadastrarInsumo("Inativo",UnidadeMedida.KG,0);
            int inativo=insumos.ListarInsumos("Inativo").get(0).getId();
            insumos.excluirInsumo(inativo);
            r=dashboard.consultarDashboard();
            verificar(r.getInsumosAtivos()==2 && r.getEstoqueBaixo().size()==2,"Cadastro e inativação atualizam resumo");
            verificar(r.getEstoqueBaixo().get(0).getDescricao().equals("Açúcar"),"Ordenação por descrição");
            int acucar=insumos.ListarInsumos("Açúcar").get(0).getId();
            try(Connection c=ConnectionFactory.getConnection(); PreparedStatement s=c.prepareStatement("UPDATE insumo SET quantidade_estoque=? WHERE id=?")) {
                s.setDouble(1,5); s.setInt(2,acucar); s.executeUpdate();
                verificar(dashboard.consultarDashboard().getEstoqueBaixo().size()==2,"Igual ao mínimo incluído");
                s.setDouble(1,6); s.executeUpdate();
            }
            verificar(dashboard.consultarDashboard().getEstoqueBaixo().size()==1,"Acima do mínimo excluído");
            Insumo chocolate=insumos.ListarInsumos("Chocolate").get(0);
            CompraService compras=new CompraService();
            ItemCompra item=new ItemCompra(); item.setInsumo(chocolate); item.setQtd(10); item.setValorUnitario(3.5);
            Compra compra=new Compra(LocalDate.now(),0); compra.setItens(List.of(item)); compras.registrarCompra(compra);
            r=dashboard.consultarDashboard();
            verificar(r.getEstoqueBaixo().isEmpty(),"Compra remove alerta de estoque");
            verificar(r.getComprasMes()==1 && r.getValorCompradoMes()==35,"Compra atual entra nos indicadores");
            compras.cancelarCompra(compra.getId());
            r=dashboard.consultarDashboard();
            verificar(r.getEstoqueBaixo().size()==1,"Estorno restaura alerta");
            verificar(r.getComprasMes()==0 && r.getValorCompradoMes()==0,"Cancelada excluída dos indicadores");
            verificar(r.getUltimasCompras().get(0).getStatus()==StatusCompra.CANCELADA,"Cancelada preservada no histórico");
            inserirCompra("2024-01-31",9,"ATIVA");
            inserirCompra("2024-02-01",20,"ATIVA");
            inserirCompra("2024-02-29",30,"ATIVA");
            inserirCompra("2024-02-15",500,"CANCELADA");
            inserirCompra("2024-03-01",11,"ATIVA");
            inserirCompra("2024-12-31",7,"ATIVA");
            inserirCompra("2025-01-01",8,"ATIVA");
            inserirCompra("2025-01-01",4,"CANCELADA");
            r=dashboard.consultarDashboard(fevereiro);
            verificar(r.getComprasMes()==2 && r.getValorCompradoMes()==50,"Fevereiro bissexto, limites inclusivos e cancelada excluída");
            verificar(dashboard.consultarDashboard(YearMonth.of(2024,12)).getValorCompradoMes()==7,"Limite de dezembro");
            verificar(dashboard.consultarDashboard(YearMonth.of(2025,1)).getValorCompradoMes()==8,"Janeiro não mistura anos");
            verificar(r.getUltimasCompras().size()==5,"Limite de cinco compras");
            List<Compra> recentes=r.getUltimasCompras();
            for(int i=1;i<recentes.size();i++) {
                Compra a=recentes.get(i-1), b=recentes.get(i);
                verificar(a.getDataCompra().isAfter(b.getDataCompra()) || (a.getDataCompra().equals(b.getDataCompra()) && a.getId()>b.getId()),"Ordem data e ID desc");
            }
            try { dashboard.consultarDashboard(null); throw new AssertionError("Mês nulo aceito"); }
            catch(IllegalArgumentException esperado) { checks++; }
            try(Connection c=ConnectionFactory.getConnection(); Statement s=c.createStatement()) {
                s.executeUpdate("ALTER TABLE compra RENAME TO compra_teste");
                try { dashboard.consultarDashboard(); throw new AssertionError("Falha SQL ocultada"); }
                catch(SQLException esperado) { checks++; }
                finally { s.executeUpdate("ALTER TABLE compra_teste RENAME TO compra"); }
            }
            verificar(dashboard.consultarDashboard(fevereiro).getValorCompradoMes()==50,"Recuperação após falha SQL");
        }
        System.out.println("PASS dashboard " + args[0] + ": " + checks + " verificacoes");
    }
}

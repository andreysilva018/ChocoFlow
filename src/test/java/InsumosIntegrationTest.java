import database.Database;
import enums.UnidadeMedida;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import model.compra.*;
import model.producao.Insumo;
import service.*;
import util.ConnectionFactory;

/** Executar somente em diretório de teste vazio; nunca no diretório do banco real. */
public class InsumosIntegrationTest {
    static int checks;
    interface Operacao { void executar() throws Exception; }
    static void verificar(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
        checks++;
    }
    static void bloquear(Operacao acao) throws Exception {
        try { acao.executar(); }
        catch (IllegalArgumentException | IllegalStateException esperado) { checks++; return; }
        throw new AssertionError("Operação inválida aceita");
    }
    public static void main(String[] args) throws Exception {
        if (args.length != 1) throw new IllegalArgumentException("Informe fresh ou reopen");
        if (args[0].equals("reopen")) {
            InsumoService service = new InsumoService();
            Insumo chocolate = service.ListarInsumos("Chocolate Branco").get(0);
            verificar(chocolate.getQtdEstoque() == 0 && chocolate.getValorUltimaCompra() == 2.5, "Estoque e valor persistidos");
            verificar(chocolate.getUnidademedida() == UnidadeMedida.G && chocolate.getEstoqueMin() == 15, "Alterações persistidas");
            verificar(service.ListarInsumos("Leite").isEmpty(), "Inativação persistida");
        } else {
            if (!args[0].equals("fresh") || new java.io.File("trufas.db").exists()) throw new IllegalStateException("Use fresh em diretório vazio");
            Database.criarBanco();
            InsumoService service = new InsumoService();
            verificar(service.ListarInsumos().isEmpty(), "Listagem vazia");
            service.cadastrarInsumo("Chocolate ao Leite", UnidadeMedida.KG, 5);
            service.cadastrarInsumo("Leite", UnidadeMedida.LT, 2);
            service.cadastrarInsumo("Cacau 100%_!", UnidadeMedida.G, 0);
            verificar(service.ListarInsumos().size() == 3, "Novo cadastro aparece");
            Insumo chocolate = service.ListarInsumos("Chocolate ao Leite").get(0);
            verificar(chocolate.isAtivo() && chocolate.getQtdEstoque() == 0 && chocolate.getValorUltimaCompra() == 0, "Campos controlados pelo banco");
            verificar(service.ListarInsumos("choco").size() == 1, "Pesquisa parcial");
            verificar(service.ListarInsumos("CHOCOLATE").get(0).getId() == chocolate.getId(), "Pesquisa maiúscula");
            verificar(service.ListarInsumos("   ChOcO   ").size() == 1, "Pesquisa normalizada");
            verificar(service.ListarInsumos("inexistente").isEmpty(), "Sem resultados");
            verificar(service.ListarInsumos("").size() == 3 && service.ListarInsumos("   ").size() == 3, "Pesquisa vazia e limpar");
            verificar(service.ListarInsumos("%").size() == 1 && service.ListarInsumos("_").size() == 1 && service.ListarInsumos("!").size() == 1, "Caracteres literais");
            verificar(service.ListarInsumos("' OR 1=1 --").isEmpty(), "Parâmetro SQL seguro");
            verificar(service.buscarPorId(chocolate.getId()).getDescricao().equals("Chocolate ao Leite"), "Busca por ID");
            // O Service de Insumos permanece aberto antes, durante e depois da compra.
            CompraService compras = new CompraService();
            ItemCompra item = new ItemCompra(); item.setInsumo(chocolate); item.setQtd(1000); item.setValorUnitario(2.5);
            Compra compra = new Compra(LocalDate.of(2026,9,11), 0); compra.setItens(List.of(item)); compras.registrarCompra(compra);
            verificar(service.ListarInsumos("choco").get(0).getQtdEstoque() == 1000, "Consulta atualizada após compra");
            verificar(service.buscarPorId(chocolate.getId()).getValorUltimaCompra() == 2.5, "Preço atual via ID");
            service.editarInsumo(chocolate.getId(), "Chocolate Branco", UnidadeMedida.G, 15);
            Insumo editado = service.buscarPorId(chocolate.getId());
            verificar(editado.getDescricao().equals("Chocolate Branco") && editado.getUnidademedida() == UnidadeMedida.G && editado.getEstoqueMin() == 15, "Edição correta pelo ID");
            verificar(editado.getQtdEstoque() == 1000 && editado.getValorUltimaCompra() == 2.5, "Edição preserva estoque e preço");
            verificar(service.ListarInsumos("ao Leite").isEmpty() && service.ListarInsumos().size() == 3, "Edição não duplica");
            compras.cancelarCompra(compra.getId());
            verificar(service.ListarInsumos("choco").get(0).getQtdEstoque() == 0, "Consulta após estorno");
            int leite = service.ListarInsumos("Leite").get(0).getId();
            service.excluirInsumo(leite);
            verificar(service.ListarInsumos("Leite").isEmpty() && service.ListarInsumos().size() == 2, "Inativos fora da consulta");
            bloquear(() -> service.buscarPorId(leite));
            bloquear(() -> service.editarInsumo(leite, "Leite", UnidadeMedida.LT, 0));
            bloquear(() -> service.excluirInsumo(leite));
            bloquear(() -> service.buscarPorId(0));
            bloquear(() -> service.buscarPorId(99999));
            bloquear(() -> service.editarInsumo(99999,"Teste",UnidadeMedida.G,0));
            bloquear(() -> service.cadastrarInsumo(" ",UnidadeMedida.G,0));
            bloquear(() -> service.cadastrarInsumo("Teste",null,0));
            bloquear(() -> service.cadastrarInsumo("Teste",UnidadeMedida.G,-1));
            bloquear(() -> service.cadastrarInsumo("Teste",UnidadeMedida.G,Double.NaN));
            try (Connection c=ConnectionFactory.getConnection(); Statement s=c.createStatement(); ResultSet r=s.executeQuery("SELECT ativo FROM insumo WHERE id="+leite)) {
                verificar(r.next() && !r.getBoolean(1), "Soft delete preserva registro");
            }
            // Falha de consulta propagada; a View apresenta mensagem sem encerrar a aplicação.
            try (Connection c=ConnectionFactory.getConnection(); Statement s=c.createStatement()) {
                s.executeUpdate("ALTER TABLE insumo RENAME TO insumo_teste");
                try { service.ListarInsumos("choco"); throw new AssertionError("Erro de consulta oculto"); }
                catch (SQLException esperado) { checks++; }
                finally { s.executeUpdate("ALTER TABLE insumo_teste RENAME TO insumo"); }
            }
            verificar(service.ListarInsumos().size()==2, "Consulta recuperada após erro");
        }
        System.out.println("PASS insumos " + args[0] + ": " + checks + " verificacoes");
    }
}

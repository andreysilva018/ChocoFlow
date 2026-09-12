/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import enums.StatusCompra;
import enums.UnidadeMedida;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.compra.Compra;
import model.compra.ItemCompra;
import model.producao.Insumo;
import java.time.LocalDate;
import util.ConnectionFactory;

/**
 *
 * @author Windows
 */
public class CompraRepository {
    public Compra buscarCompraPorId(int compraId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM compra WHERE id = ?")) {
            stmt.setInt(1, compraId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? lerCompra(rs) : null;
            }
        }
    }

    public List<Compra> consultarCompras(LocalDate inicio, LocalDate fim) throws SQLException {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT * FROM compra WHERE (? IS NULL OR date_compra >= ?) "
                + "AND (? IS NULL OR date_compra <= ?) ORDER BY date_compra DESC, id DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, inicio == null ? null : inicio.toString());
            stmt.setString(2, inicio == null ? null : inicio.toString());
            stmt.setString(3, fim == null ? null : fim.toString());
            stmt.setString(4, fim == null ? null : fim.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) compras.add(lerCompra(rs));
            }
        }
        return compras;
    }

    private Compra lerCompra(ResultSet rs) throws SQLException {
        Compra compra = new Compra();
        compra.setId(rs.getInt("id"));
        compra.setDataCompra(LocalDate.parse(rs.getString("date_compra")));
        compra.setValorTotal(rs.getDouble("valor_total"));
        compra.setStatus(StatusCompra.valueOf(rs.getString("status")));
        return compra;
    }

    public boolean insumoEstaAtivo(int insumoId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT ativo FROM insumo WHERE id = ?")) {
            stmt.setInt(1, insumoId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean("ativo");
            }
        }
    }
    private Connection conn;

    
    public CompraRepository(){
        try {
            this.conn = new ConnectionFactory().getConnection();
        } catch (Exception erro) {
            erro.printStackTrace();
            throw new RuntimeException("Erro ao conectar no banco de dados", erro);
        }
    }
    
    public List<Insumo> MostrarInsumosComboBx() throws Exception{
        List<Insumo> lista = new ArrayList<>();
        String sql = """
                     SELECT * FROM insumo WHERE ativo=1 ORDER BY descricao
                     """;
        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Insumo insumo = new Insumo();
                insumo.setId(rs.getInt("id"));
                insumo.setDescricao(rs.getString("descricao"));
                insumo.setUnidademedida(UnidadeMedida.valueOf(rs.getString("unidade_medida")));
                lista.add(insumo);
            }
        }
        return lista;
    }
    
    public void registrarCompra(Compra compra) throws SQLException {
        // Transação de registro já existente no projeto: preservar e propagar falhas.
        conn.setAutoCommit(false);
        try {
            int compraId;
            String sql = "INSERT INTO compra(date_compra, valor_total, status) VALUES(?, ?, 'ATIVA')";
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, compra.getDataCompra().toString());
                stmt.setDouble(2, compra.getValorTotal());
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("ID da compra não foi gerado.");
                    compraId = rs.getInt(1);
                }
            }
            for (ItemCompra item : compra.getItens()) {
                SalvarItensdaCompra(compraId, item);
                atualizarInsumo(item);
            }
            conn.commit();
            compra.setId(compraId);
            compra.setStatus(StatusCompra.ATIVA);
        } catch (Exception erro) {
            conn.rollback();
            throw new SQLException("Não foi possível registrar a compra.", erro);
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public void SalvarItensdaCompra(int compraId, ItemCompra item) throws SQLException {
        String sql = "INSERT INTO item_compra(compra_id, insumo_id, quantidade, valor_unitario, valor_total_item) "
                + "VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, compraId);
            stmt.setInt(2, item.getInsumo().getId());
            stmt.setDouble(3, item.getQtd());
            stmt.setDouble(4, item.getValorUnitario());
            stmt.setDouble(5, item.getValorTotalItem());
            stmt.executeUpdate();
        }
    }

    public void atualizarInsumo(ItemCompra item) throws SQLException {
        String sql = "UPDATE insumo SET valor_ultima_compra=?, quantidade_estoque = quantidade_estoque + ? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, item.getValorUnitario());
            stmt.setDouble(2, item.getQtd());
            stmt.setInt(3, item.getInsumo().getId());
            if (stmt.executeUpdate() != 1) throw new SQLException("Insumo não encontrado ao atualizar estoque.");
        }
    }
    public List<ItemCompra> buscarItensPorCompra(int compraId) {
        List<ItemCompra> itens = new ArrayList<>();

        String sql = """
            SELECT
                ic.id,
                ic.quantidade,
                ic.valor_unitario,
                ic.valor_total_item,
                i.id AS insumo_id,
                i.descricao,
                i.quantidade_estoque,
                i.unidade_medida
            FROM item_compra ic
            LEFT JOIN insumo i
                ON i.id = ic.insumo_id
            WHERE ic.compra_id = ?
            """;

        try (
            Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, compraId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if (rs.getObject("insumo_id") == null) {
                        throw new IllegalStateException("Insumo da compra não encontrado.");
                    }
                    Insumo insumo = new Insumo();

                    insumo.setId(rs.getInt("insumo_id"));
                    insumo.setDescricao(rs.getString("descricao"));
                    insumo.setQtdEstoque(
                        rs.getDouble("quantidade_estoque")
                    );

                    ItemCompra item = new ItemCompra();

                    insumo.setUnidademedida(UnidadeMedida.valueOf(rs.getString("unidade_medida")));
                    item.setCompra(new Compra(compraId, (StatusCompra) null));
                    item.setValorTotalItem(rs.getDouble("valor_total_item"));
                    item.setId(rs.getInt("id"));
                    item.setInsumo(insumo);
                    item.setQtd(rs.getDouble("quantidade"));
                    item.setValorUnitario(rs.getDouble("valor_unitario"));

                    itens.add(item);
                }
            }

            return itens;

        } catch (SQLException erro) {
            throw new RuntimeException(
                "Erro ao buscar os itens da compra.",
                erro
            );
        }
    }
    
    public void estornarEstoque(ItemCompra item){
        String sql = """
            UPDATE insumo
            SET quantidade_estoque = quantidade_estoque - ?
            WHERE id = ?
            """;
        try (
            Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            stmt.setDouble(1, item.getQtd());
            stmt.setInt(2, item.getInsumo().getId());
            
            int linhasAlteradas = stmt.executeUpdate();
            
            if(linhasAlteradas == 0){
                throw new IllegalStateException(
                        "O insumo não foi encontrado para o estorno"
                );
            }
        } catch (Exception erro) {
            throw new RuntimeException(
                    "Erro ao estornar o estoque do insumo"
                    + item.getInsumo().getDescricao(),
                    erro
            );
        }
    }
    
    public void marcarCompraCancelada(int compraId){
        String sql = """
            UPDATE compra
            SET status = ?
            WHERE id = ?
                AND status = ?
            """;
        
        try (
            Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            stmt.setString(1, StatusCompra.CANCELADA.name());
            stmt.setInt(2, compraId);
            stmt.setString(3, StatusCompra.ATIVA.name());
            
            int linhasAlteradas = stmt.executeUpdate();
            
            if(linhasAlteradas == 0){
                throw new IllegalStateException(
                        "A compra não existe ou já foi cancelada"
                );
            }
            
        } catch (Exception erro) {
            throw new RuntimeException(
                "Erro ao cancelar a compra",
                erro
            );
        }
    }
}

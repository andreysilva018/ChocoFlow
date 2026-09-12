/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import enums.UnidadeMedida;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.producao.Insumo;
import util.ConnectionFactory;

/**
 *
 * @author Windows
 */
public class InsumoRepository {
    private Connection conn;
    
    public InsumoRepository(){
        try {
            this.conn = new ConnectionFactory().getConnection();            
        } catch (SQLException erro) {
            throw new RuntimeException("Erro ao conectar com o banco de dados", erro);
        }
        
    }
    
    public void CadastrarInsumo(Insumo insumo) throws SQLException{
        String sql = "INSERT INTO insumo (descricao, unidade_medida, estoque_minimo, ativo)"
                + "values(?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, insumo.getDescricao());
            stmt.setString(2, insumo.getUnidademedida().name());
            stmt.setDouble(3, insumo.getEstoqueMin());
            stmt.setBoolean(4, insumo.isAtivo());
        
            stmt.execute();
        }
    }
    
    public void AtualizarInsumo(Insumo insumo) throws SQLException{
        String sql = "UPDATE insumo SET descricao=?, unidade_medida=?, estoque_minimo=? WHERE id=? AND ativo = 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, insumo.getDescricao());
            stmt.setString(2, insumo.getUnidademedida().name());
            stmt.setDouble(3, insumo.getEstoqueMin());
            stmt.setInt(4, insumo.getId());
        
            if (stmt.executeUpdate() != 1) throw new IllegalStateException("Insumo não encontrado ou já inativo.");
        }
    }
    
    public void ExcluirInsumo(Insumo insumo) throws SQLException{
        String sql = "UPDATE insumo SET ativo=? WHERE id=? AND ativo = 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, insumo.isAtivo());
            stmt.setInt(2, insumo.getId());
        
            if (stmt.executeUpdate() != 1) throw new IllegalStateException("Insumo não encontrado ou já inativo.");
        }
    }
    
    public List<Insumo> ListarInsumos() throws SQLException {
        return ListarInsumos("");
    }

    public List<Insumo> ListarInsumos(String descricao) throws SQLException {
        List<Insumo> lista = new ArrayList<>();
        String sql = "SELECT * FROM insumo WHERE ativo = 1 AND descricao LIKE ? ESCAPE '!' ORDER BY descricao COLLATE NOCASE, id";
        // Pesquisa literal: %, _ e ! digitados não viram curingas SQL.
        String filtro = descricao.replace("!", "!!").replace("%", "!%").replace("_", "!_");
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + filtro + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(lerInsumo(rs));
            }
        }
        return lista;
    }

    public Insumo buscarPorId(int id) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM insumo WHERE id = ? AND ativo = 1")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? lerInsumo(rs) : null;
            }
        }
    }

    private Insumo lerInsumo(ResultSet rs) throws SQLException {
        Insumo insumo = new Insumo();
        insumo.setId(rs.getInt("id"));
        insumo.setDescricao(rs.getString("descricao"));
        insumo.setUnidademedida(UnidadeMedida.valueOf(rs.getString("unidade_medida")));
        insumo.setQtdEstoque(rs.getDouble("quantidade_estoque"));
        insumo.setEstoqueMin(rs.getDouble("estoque_minimo"));
        insumo.setValorUltimaCompra(rs.getDouble("valor_ultima_compra"));
        insumo.setAtivo(rs.getBoolean("ativo"));
        return insumo;
    }
}

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
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, insumo.getDescricao());
        stmt.setString(2, insumo.getUnidademedida().name());
        stmt.setDouble(3, insumo.getEstoqueMin());
        stmt.setBoolean(4, insumo.isAtivo());
        
        stmt.execute();
        stmt.close();
    }
    
    public void AtualizarInsumo(Insumo insumo) throws SQLException{
        String sql = "UPDATE insumo SET descricao=?, unidade_medida=?, estoque_minimo=? WHERE id=?";
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, insumo.getDescricao());
        stmt.setString(2, insumo.getUnidademedida().name());
        stmt.setDouble(3, insumo.getEstoqueMin());
        stmt.setInt(4, insumo.getId());
        
        stmt.executeUpdate();
        stmt.close();
    }
    
    public void ExcluirInsumo(Insumo insumo) throws SQLException{
        String sql = "UPDATE insumo SET ativo=? WHERE id=? ";
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setBoolean(1, insumo.isAtivo());
        stmt.setInt(2, insumo.getId());
        
        stmt.executeUpdate();
        stmt.close();
    }
    
    public List<Insumo> ListarInsumos() throws Exception{
        List<Insumo> lista = new ArrayList<>();
        String sql = "SELECT * FROM insumo WHERE ativo =1";
        PreparedStatement stmt = conn.prepareStatement(sql);
        
        ResultSet rs = stmt.executeQuery();
        
        while(rs.next()){
            Insumo insumo = new Insumo();
            insumo.setId(rs.getInt("id"));
            insumo.setDescricao(rs.getString("descricao"));
            insumo.setUnidademedida(UnidadeMedida.valueOf(rs.getString("unidade_medida")));
            insumo.setQtdEstoque(Double.parseDouble(rs.getString("quantidade_estoque")));
            insumo.setEstoqueMin(Double.parseDouble(rs.getString("estoque_minimo")));
            insumo.setValorUltimaCompra(Double.parseDouble(rs.getString("valor_ultima_compra")));
            
            lista.add(insumo);
        }
        rs.close();
        stmt.close();        
        return lista;
    }    
    
}

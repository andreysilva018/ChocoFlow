/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import enums.UnidadeMedida;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.compra.Compra;
import model.compra.ItemCompra;
import model.producao.Insumo;
import util.ConnectionFactory;

/**
 *
 * @author Windows
 */
public class CompraRepository {
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
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        
        while(rs.next()){
            Insumo insumo = new Insumo();
            
            insumo.setId(rs.getInt("id"));
            insumo.setDescricao(rs.getString("descricao"));
            insumo.setUnidademedida(UnidadeMedida.valueOf(rs.getString("unidade_medida")));
            
            lista.add(insumo);
        }
        rs.close();
        stmt.close();
        return lista;
    }
    
    public void registrarCompra(Compra compra) throws Exception{
        String sql = "INSERT INTO compra(date_compra, valor_total)"
                + "VALUES(?, ?)";
        
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, compra.getDataCompra().toString());
        stmt.setDouble(2, compra.getValorTotal());
        
        stmt.executeUpdate();
        
        ResultSet rs = stmt.getGeneratedKeys();
        
        int compraId = 0;
        
        if(rs.next()){
            compraId = rs.getInt(1);
        }
        rs.close();        
        stmt.close();
        for(ItemCompra item : compra.getItens()){
            SalvarItensdaCompra(compraId, item);
            atualizarInsumo(item);
        }
    }
    
    public void SalvarItensdaCompra(int compraId, ItemCompra item) throws Exception{ 
        String sql = "INSERT INTO item_compra(compra_id, insumo_id, quantidade, valor_unitario, valor_total_item)"
                + "values(?, ?, ?, ?, ?)" ;
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, compraId);
        stmt.setInt(2, item.getInsumo().getId());
        stmt.setDouble(3, item.getQtd());
        stmt.setDouble(4, item.getValorUnitario());
        stmt.setDouble(5, item.getValorTotalItem());
        
        stmt.executeUpdate();
        stmt.close();
    }
    
    public void atualizarInsumo(ItemCompra item) throws SQLException{
        String sql = "UPDATE insumo SET valor_ultima_compra=?, quantidade_estoque=? WHERE id=?";
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setDouble(1, item.getValorUnitario());
        stmt.setDouble(2, item.getQtd());
        stmt.setInt(3, item.getInsumo().getId());
        
        stmt.executeUpdate();
        stmt.close();
    }
}

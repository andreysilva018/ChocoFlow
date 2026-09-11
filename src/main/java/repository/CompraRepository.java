/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import enums.StatusCompra;
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
import service.CompraService;
import util.ConnectionFactory;

/**
 *
 * @author Windows
 */
public class CompraRepository {
    private Connection conn;
    private CompraService service;
    
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
    
    public void registrarCompra(Compra compra) throws SQLException {
        conn.setAutoCommit(false);
        try {
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
            conn.commit();
        } catch (Exception erro) {
            conn.rollback();
        } finally {
            conn.setAutoCommit(true);
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
        String sql = "UPDATE insumo SET valor_ultima_compra=?, quantidade_estoque = quantidade_estoque + ? WHERE id=?";
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setDouble(1, item.getValorUnitario());
        stmt.setDouble(2, item.getQtd());
        stmt.setInt(3, item.getInsumo().getId());
        
        stmt.executeUpdate();
        stmt.close();
    }
    
    public void CancelarCompra(int compraId) throws SQLException{
        if(!compraEstaAtiva(compraId)){
            throw new IllegalStateException(
                    "Essa compra já está cancelada"
            );
        }
        
        List<ItemCompra> itens = buscarItensPorCompra(compraId);
        
        if(itens.isEmpty()){
            throw new IllegalStateException(
                    "A compra não possui itens registrados"
            );
        }
        
        service.validarEstoqueparaCancelamento(itens);
        
        for(ItemCompra item : itens){
            estornarEstoque(item);
        }
        
        marcarCompraCancelada(compraId);
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
                i.quantidade_estoque
            FROM item_compra ic
            INNER JOIN insumo i
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
                    Insumo insumo = new Insumo();

                    insumo.setId(rs.getInt("insumo_id"));
                    insumo.setDescricao(rs.getString("descricao"));
                    insumo.setQtdEstoque(
                        rs.getDouble("quantidade_estoque")
                    );

                    ItemCompra item = new ItemCompra();

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
    
    public boolean compraEstaAtiva(int compraId){
        String sql = """
                     SELECT status
                     FROM compra
                     WHERE id =?
                    """;
        try (            
            Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            
            try(ResultSet rs = stmt.executeQuery()){
                if(!rs.next()){
                    throw new IllegalArgumentException(
                            "Compra não encontrada"
                    );
                }
                
                return StatusCompra.ATIVA.name().equals(rs.getString("Status"));
            }
            
        } catch (Exception erro) {
            
            throw new RuntimeException("Erro ao verifica o status da compra" , erro);
        }
    }
    
    private void estornarEstoque(ItemCompra item){
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
    
    private void marcarCompraCancelada(int compraId){
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
                "Erro ao cancelar a comrpa",
                erro
            );
        }
    }
}

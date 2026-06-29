/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import enums.UnidadeMedida;
import java.sql.SQLException;
import java.util.List;
import model.producao.Insumo;
import repository.InsumoRepository;

/**
 *
 * @author Windows
 */
public class InsumoService {
    
    InsumoRepository repository = new InsumoRepository();
    
    public void cadastrarInsumo(String descricao, UnidadeMedida unidade, double estoqueMin) throws SQLException{
        boolean ativo = true;
        Insumo insumo = new Insumo(descricao, unidade, estoqueMin, ativo);
        repository.CadastrarInsumo(insumo);
    }
    
    public void editarInsumo(String descricao, UnidadeMedida unidade, double estoqueMin) throws SQLException{
        Insumo insumo = new Insumo(descricao, unidade, estoqueMin);
        repository.AtualizarInsumo(insumo);
    }
    
    public void excluirInsumo(int id) throws SQLException{
        boolean ativo = false;
        Insumo insumo = new Insumo(id, ativo);
        repository.ExcluirInsumo(insumo);
    }
    
    public void buscarPorId(){}
    
    public List<Insumo> ListarInsumos() throws Exception{
        return repository.ListarInsumos();
    }
    
    public void atualizarEstoque(){}
    
    public void verificarEstqMinimo(){}
}

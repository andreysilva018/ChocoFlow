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
        validarDados(descricao, unidade, estoqueMin);
        descricao = descricao.trim();
        boolean ativo = true;
        Insumo insumo = new Insumo(descricao, unidade, estoqueMin, ativo);
        repository.CadastrarInsumo(insumo);
    }
    
    public void editarInsumo(int id, String descricao, UnidadeMedida unidade, double estoqueMin) throws SQLException{
        validarId(id);
        validarDados(descricao, unidade, estoqueMin);
        Insumo insumo = new Insumo(descricao.trim(), unidade, estoqueMin);
        insumo.setId(id);
        repository.AtualizarInsumo(insumo);
    }
    
    public void excluirInsumo(int id) throws SQLException{
        validarId(id);
        boolean ativo = false;
        Insumo insumo = new Insumo(id, ativo);
        repository.ExcluirInsumo(insumo);
    }
    
    public Insumo buscarPorId(int id) throws SQLException {
        validarId(id);
        Insumo insumo = repository.buscarPorId(id);
        if (insumo == null) throw new IllegalArgumentException("Insumo não encontrado ou inativo. Atualize a consulta.");
        return insumo;
    }

    public List<Insumo> ListarInsumos(String descricao) throws SQLException {
        return repository.ListarInsumos(descricao == null ? "" : descricao.trim());
    }

    private void validarId(int id) {
        if (id <= 0) throw new IllegalArgumentException("Selecione um insumo na aba Consulta.");
    }

    private void validarDados(String descricao, UnidadeMedida unidade, double estoqueMin) {
        if (descricao == null || descricao.isBlank()) throw new IllegalArgumentException("Informe a descrição do insumo.");
        if (unidade == null) throw new IllegalArgumentException("Selecione a unidade de medida.");
        if (!Double.isFinite(estoqueMin) || estoqueMin < 0) throw new IllegalArgumentException("O estoque mínimo deve ser um número maior ou igual a zero.");
    }
    
    public List<Insumo> ListarInsumos() throws Exception{
        return repository.ListarInsumos();
    }
    
    public void atualizarEstoque(){}
    
    public void verificarEstqMinimo(){}
}

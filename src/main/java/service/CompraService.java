/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import model.compra.Compra;
import model.compra.ItemCompra;
import model.producao.Insumo;
import repository.CompraRepository;

/**
 *
 * @author Windows
 */
public class CompraService {
    CompraRepository repository = new CompraRepository();
    
    public void registrarCompra(Compra compra) throws Exception{
        
        repository.registrarCompra(compra);
    }
    
    public void SalvarItemCompra(){}
    
    public void calcularValorTotal(){}
    
    public void atualizarEstoque(){}
    
    public void gerarMovimentacaoCaixa(){}
    
    public List<Insumo> MostrarInsumoComboBx() throws Exception{
        return repository.MostrarInsumosComboBx();
    }
}

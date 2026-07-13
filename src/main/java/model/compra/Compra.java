/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.compra;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Windows
 */
public class Compra {
    private int id;
    private LocalDate dataCompra;
    private double valorTotal;
    private List<ItemCompra> itens;

    public Compra() {
    }
    
    public Compra(int id, LocalDate dataCompra, double valorTotal, List<ItemCompra> itens) {
        this.id = id;
        this.dataCompra = dataCompra;
        this.valorTotal = valorTotal;
        this.itens = itens;
    }

    public Compra(LocalDate dataCompra, double valorTotal) {
        this.dataCompra = dataCompra;
        this.valorTotal = valorTotal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(LocalDate dataCompra) {
        this.dataCompra = dataCompra;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public List<ItemCompra> getItens() {
        return itens;
    }

    public void setItens(List<ItemCompra> itens) {
        this.itens = itens;
    }
    
    
}

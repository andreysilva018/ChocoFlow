/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.compra;

import model.producao.Insumo;

/**
 *
 * @author Windows
 */
public class ItemCompra {
    private int id;
    private Compra compra;
    private Insumo insumo;
    private double qtd;
    private double valorUnitario;

    public ItemCompra() {
    }

    public ItemCompra(int id, Compra compra, Insumo insumo, double qtd, double valorUnitario) {
        this.id = id;
        this.compra = compra;
        this.insumo = insumo;
        this.qtd = qtd;
        this.valorUnitario = valorUnitario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

    public Insumo getInsumo() {
        return insumo;
    }

    public void setInsumo(Insumo insumo) {
        this.insumo = insumo;
    }

    public double getQtd() {
        return qtd;
    }

    public void setQtd(double qtd) {
        this.qtd = qtd;
    }

    public double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }
    
    
}

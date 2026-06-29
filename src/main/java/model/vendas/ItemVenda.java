/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.vendas;

import model.producao.Trufa;

/**
 *
 * @author Windows
 */
public class ItemVenda {
    private int id;
    private Venda venda;
    private Trufa trufa;
    private int qtd;
    private double valorUnitario;

    public ItemVenda() {
    }

    public ItemVenda(int id, Venda venda, Trufa trufa, int qtd, double valorUnitario) {
        this.id = id;
        this.venda = venda;
        this.trufa = trufa;
        this.qtd = qtd;
        this.valorUnitario = valorUnitario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Trufa getTrufa() {
        return trufa;
    }

    public void setTrufa(Trufa trufa) {
        this.trufa = trufa;
    }

    public int getQtd() {
        return qtd;
    }

    public void setQtd(int qtd) {
        this.qtd = qtd;
    }

    public double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }
    
    
}

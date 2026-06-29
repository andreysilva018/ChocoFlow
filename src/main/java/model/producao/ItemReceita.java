/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.producao;

import model.producao.Trufa;

/**
 *
 * @author Windows
 */
public class ItemReceita {
    private int id;
    private Trufa trufa;
    private Insumo insumo;
    private double qtd;

    public ItemReceita() {
    }
    
    public ItemReceita(int id, Trufa trufa, Insumo insumo, double qtd) {
        this.id = id;
        this.trufa = trufa;
        this.insumo = insumo;
        this.qtd = qtd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Trufa getTrufa() {
        return trufa;
    }

    public void setTrufa(Trufa trufa) {
        this.trufa = trufa;
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
    
    
}

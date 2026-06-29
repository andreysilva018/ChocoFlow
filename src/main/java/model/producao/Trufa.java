/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.producao;

import java.util.List;

/**
 *
 * @author Windows
 */
public class Trufa {
    private int id;
    private String descricao;
    private int redimentoBase;
    private List<ItemReceita> receita;

    public Trufa() {
    }

    public Trufa(int id, String descricao, int redimentoBase, List<ItemReceita> receita) {
        this.id = id;
        this.descricao = descricao;
        this.redimentoBase = redimentoBase;
        this.receita = receita;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getRedimentoBase() {
        return redimentoBase;
    }

    public void setRedimentoBase(int redimentoBase) {
        this.redimentoBase = redimentoBase;
    }

    public List<ItemReceita> getReceita() {
        return receita;
    }

    public void setReceita(List<ItemReceita> receita) {
        this.receita = receita;
    }
    
    @Override
    public String toString(){
        return descricao;
    }
}

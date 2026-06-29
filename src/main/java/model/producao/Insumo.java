/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.producao;

import enums.UnidadeMedida;

/**
 *
 * @author Windows
 */
public class Insumo {
    private int id;
    private String descricao;
    private UnidadeMedida unidademedida;
    private double qtdEstoque;
    private double estoqueMin;
    private double valorUltimaCompra;
    private boolean ativo;
    
    public Insumo(){
    }

    public Insumo(int id, boolean ativo) {
        this.id = id;
        this.ativo = ativo;
    }

    public Insumo(int id, String descricao, UnidadeMedida unidademedida, double qtdEstoque, double estoqueMin, double valorUltimaCompra) {
        this.id = id;
        this.descricao = descricao;
        this.unidademedida = unidademedida;
        this.qtdEstoque = qtdEstoque;
        this.estoqueMin = estoqueMin;
        this.valorUltimaCompra = valorUltimaCompra;
    }

    public Insumo(String descricao, UnidadeMedida unidademedida, double estoqueMin, boolean ativo) {
        this.descricao = descricao;
        this.unidademedida = unidademedida;
        this.estoqueMin = estoqueMin;
        this.ativo = ativo;
    }

    public Insumo(String descricao, UnidadeMedida unidademedida, double estoqueMin) {
        this.descricao = descricao;
        this.unidademedida = unidademedida;
        this.estoqueMin = estoqueMin;
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

    public UnidadeMedida getUnidademedida() {
        return unidademedida;
    }

    public void setUnidademedida(UnidadeMedida unidademedida) {
        this.unidademedida = unidademedida;
    }

    public double getQtdEstoque() {
        return qtdEstoque;
    }

    public void setQtdEstoque(double qtdEstoque) {
        this.qtdEstoque = qtdEstoque;
    }

    public double getEstoqueMin() {
        return estoqueMin;
    }

    public void setEstoqueMin(double estoqueMin) {
        this.estoqueMin = estoqueMin;
    }

    public double getValorUltimaCompra() {
        return valorUltimaCompra;
    }

    public void setValorUltimaCompra(double valorUltimaCompra) {
        this.valorUltimaCompra = valorUltimaCompra;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    } 
    
    @Override
    public String toString(){
        return descricao;
    }
}

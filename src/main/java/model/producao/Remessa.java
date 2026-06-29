/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.producao;

import java.time.LocalDate;

/**
 *
 * @author Windows
 */
public class Remessa {
    private int id;
    private Trufa trufa;
    private LocalDate dataProducao;
    private int qtd;
    private double percentualLucro;
    private double custoTotal;
    private double custoUnitario;
    private double precoVendaSugerida;

    public Remessa() {
    }

    public Remessa(int id, Trufa trufa, LocalDate dataProducao, int qtd, double percentualLucro, double custoTotal, double custoUnitario, double precoVendaSugerida) {
        this.id = id;
        this.trufa = trufa;
        this.dataProducao = dataProducao;
        this.qtd = qtd;
        this.percentualLucro = percentualLucro;
        this.custoTotal = custoTotal;
        this.custoUnitario = custoUnitario;
        this.precoVendaSugerida = precoVendaSugerida;
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

    public LocalDate getDataProducao() {
        return dataProducao;
    }

    public void setDataProducao(LocalDate dataProducao) {
        this.dataProducao = dataProducao;
    }

    public int getQtd() {
        return qtd;
    }

    public void setQtd(int qtd) {
        this.qtd = qtd;
    }

    public double getPercentualLucro() {
        return percentualLucro;
    }

    public void setPercentualLucro(double percentualLucro) {
        this.percentualLucro = percentualLucro;
    }

    public double getCustoTotal() {
        return custoTotal;
    }

    public void setCustoTotal(double custoTotal) {
        this.custoTotal = custoTotal;
    }

    public double getCustoUnitario() {
        return custoUnitario;
    }

    public void setCustoUnitario(double custoUnitario) {
        this.custoUnitario = custoUnitario;
    }

    public double getPrecoVendaSugerida() {
        return precoVendaSugerida;
    }

    public void setPrecoVendaSugerida(double precoVendaSugerida) {
        this.precoVendaSugerida = precoVendaSugerida;
    }
    
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.financeiro;

import enums.TipoMovimentacao;
import java.time.LocalDate;

/**
 *
 * @author Windows
 */
public class MovimentacaoCaixa {
    private int id;
    private LocalDate data;
    private TipoMovimentacao tipoMovi;
    private String descricao;
    private double valor;

    public MovimentacaoCaixa() {
    }

    public MovimentacaoCaixa(int id, LocalDate data, TipoMovimentacao tipoMovi, String descricao, double valor) {
        this.id = id;
        this.data = data;
        this.tipoMovi = tipoMovi;
        this.descricao = descricao;
        this.valor = valor;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public TipoMovimentacao getTipoMovi() {
        return tipoMovi;
    }

    public void setTipoMovi(TipoMovimentacao tipoMovi) {
        this.tipoMovi = tipoMovi;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}

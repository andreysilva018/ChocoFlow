package model.dashboard;

import java.time.YearMonth;
import java.util.List;
import model.compra.Compra;
import model.producao.Insumo;

/** Resultado das consultas do Dashboard; não representa uma nova tabela. */
public class ResumoDashboard {
    private final YearMonth mes;
    private final int insumosAtivos;
    private final int comprasMes;
    private final double valorCompradoMes;
    private final List<Insumo> estoqueBaixo;
    private final List<Compra> ultimasCompras;

    public ResumoDashboard(YearMonth mes, int insumosAtivos, int comprasMes,
            double valorCompradoMes, List<Insumo> estoqueBaixo, List<Compra> ultimasCompras) {
        this.mes = mes;
        this.insumosAtivos = insumosAtivos;
        this.comprasMes = comprasMes;
        this.valorCompradoMes = valorCompradoMes;
        this.estoqueBaixo = estoqueBaixo;
        this.ultimasCompras = ultimasCompras;
    }
    public YearMonth getMes() { return mes; }
    public int getInsumosAtivos() { return insumosAtivos; }
    public int getComprasMes() { return comprasMes; }
    public double getValorCompradoMes() { return valorCompradoMes; }
    public List<Insumo> getEstoqueBaixo() { return estoqueBaixo; }
    public List<Compra> getUltimasCompras() { return ultimasCompras; }
}

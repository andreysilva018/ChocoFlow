package service;

import enums.StatusCompra;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.compra.Compra;
import model.compra.ItemCompra;
import model.producao.Insumo;
import repository.CompraRepository;

public class CompraService {
    private final CompraRepository repository = new CompraRepository();

    public void validarItemCompra(ItemCompra item) {
        if (item == null || item.getInsumo() == null || item.getInsumo().getId() <= 0)
            throw new IllegalArgumentException("Selecione um insumo válido.");
        if (!Double.isFinite(item.getQtd()) || item.getQtd() <= 0)
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        if (!Double.isFinite(item.getValorUnitario()) || item.getValorUnitario() <= 0
                || !Double.isFinite(item.getQtd() * item.getValorUnitario()))
            throw new IllegalArgumentException("O valor deve ser maior que zero e válido.");
    }

    public void registrarCompra(Compra compra) throws Exception {
        if (compra == null || compra.getDataCompra() == null)
            throw new IllegalArgumentException("Informe a data da compra.");
        if (compra.getItens() == null || compra.getItens().isEmpty())
            throw new IllegalArgumentException("Adicione pelo menos um item à compra.");
        double total = 0;
        for (ItemCompra item : compra.getItens()) {
            validarItemCompra(item);
            if (!repository.insumoEstaAtivo(item.getInsumo().getId()))
                throw new IllegalArgumentException("O insumo não existe ou está inativo: " + item.getInsumo());
            item.setValorTotalItem(item.getQtd() * item.getValorUnitario());
            total += item.getValorTotalItem();
        }
        if (!Double.isFinite(total)) throw new IllegalArgumentException("Total da compra inválido.");
        compra.setValorTotal(total);
        repository.registrarCompra(compra);
    }

    public void validarEstoqueparaCancelamento(List<ItemCompra> itens) {
        if (itens == null || itens.isEmpty())
            throw new IllegalStateException("A compra não possui itens registrados.");
        // Um mesmo insumo pode aparecer em várias linhas da compra.
        Map<Integer, Double> quantidades = new LinkedHashMap<>();
        for (ItemCompra item : itens) {
            validarItemCompra(item);
            quantidades.merge(item.getInsumo().getId(), item.getQtd(), Double::sum);
        }
        for (ItemCompra item : itens) {
            double quantidade = quantidades.get(item.getInsumo().getId());
            double estoque = item.getInsumo().getQtdEstoque();
            if (!Double.isFinite(quantidade) || !Double.isFinite(estoque) || estoque < quantidade)
                throw new IllegalStateException("Não é possível cancelar a compra. O estoque do insumo "
                        + item.getInsumo().getDescricao() + " é menor que a quantidade comprada.");
        }
    }

    public void cancelarCompra(int compraId) throws Exception {
        validarId(compraId);
        Compra compra = repository.buscarCompraPorId(compraId);
        if (compra == null) throw new IllegalArgumentException("Compra não encontrada.");
        if (compra.getStatus() == StatusCompra.CANCELADA)
            throw new IllegalStateException("Essa compra já está cancelada.");
        List<ItemCompra> itens = repository.buscarItensPorCompra(compraId);
        validarEstoqueparaCancelamento(itens);
        // TODO: envolver cancelamento em transação JDBC (commit/rollback).
        // Todos os itens são validados antes da primeira alteração.
        for (ItemCompra item : itens) repository.estornarEstoque(item);
        repository.marcarCompraCancelada(compraId);
        // TODO: recalcular valor_ultima_compra pela última compra ATIVA.
    }

    private void validarId(int compraId) {
        if (compraId <= 0) throw new IllegalArgumentException("Selecione uma compra válida.");
    }

    public List<Compra> consultarCompras(LocalDate inicio, LocalDate fim) throws Exception {
        if (inicio != null && fim != null && inicio.isAfter(fim))
            throw new IllegalArgumentException("A data inicial deve ser anterior ou igual à data final.");
        return repository.consultarCompras(inicio, fim);
    }

    public List<ItemCompra> buscarItensPorCompra(int compraId) {
        validarId(compraId);
        return repository.buscarItensPorCompra(compraId);
    }

    public List<Insumo> MostrarInsumoComboBx() throws Exception {
        return repository.MostrarInsumosComboBx();
    }
}

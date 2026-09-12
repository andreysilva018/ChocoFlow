package repository;

import enums.StatusCompra;
import enums.UnidadeMedida;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import model.compra.Compra;
import model.dashboard.ResumoDashboard;
import model.producao.Insumo;
import util.ConnectionFactory;

/** Consultas somente de leitura específicas da tela principal. */
public class DashboardRepository {
    public ResumoDashboard consultar(YearMonth mes) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection()) {
            int ativos;
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM insumo WHERE ativo = 1");
                    ResultSet rs = stmt.executeQuery()) {
                rs.next();
                ativos = rs.getInt(1);
            }
            int comprasMes;
            double valorMes;
            String sql = "SELECT COUNT(*), COALESCE(SUM(valor_total), 0) FROM compra "
                    + "WHERE status = ? AND date_compra >= ? AND date_compra <= ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, StatusCompra.ATIVA.name());
                stmt.setString(2, mes.atDay(1).toString());
                stmt.setString(3, mes.atEndOfMonth().toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    rs.next();
                    comprasMes = rs.getInt(1);
                    valorMes = rs.getDouble(2);
                }
            }
            return new ResumoDashboard(mes, ativos, comprasMes, valorMes,
                    listarEstoqueBaixo(conn), listarUltimasCompras(conn));
        }
    }

    private List<Insumo> listarEstoqueBaixo(Connection conn) throws SQLException {
        List<Insumo> lista = new ArrayList<>();
        String sql = "SELECT id, descricao, unidade_medida, quantidade_estoque, estoque_minimo "
                + "FROM insumo WHERE ativo = 1 AND quantidade_estoque <= estoque_minimo "
                + "ORDER BY descricao COLLATE NOCASE, id";
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Insumo insumo = new Insumo();
                insumo.setId(rs.getInt("id"));
                insumo.setDescricao(rs.getString("descricao"));
                insumo.setUnidademedida(UnidadeMedida.valueOf(rs.getString("unidade_medida")));
                insumo.setQtdEstoque(rs.getDouble("quantidade_estoque"));
                insumo.setEstoqueMin(rs.getDouble("estoque_minimo"));
                insumo.setAtivo(true);
                lista.add(insumo);
            }
        }
        return lista;
    }

    private List<Compra> listarUltimasCompras(Connection conn) throws SQLException {
        List<Compra> lista = new ArrayList<>();
        String sql = "SELECT id, date_compra, valor_total, status FROM compra "
                + "ORDER BY date_compra DESC, id DESC LIMIT 5";
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Compra compra = new Compra();
                compra.setId(rs.getInt("id"));
                compra.setDataCompra(LocalDate.parse(rs.getString("date_compra")));
                compra.setValorTotal(rs.getDouble("valor_total"));
                compra.setStatus(StatusCompra.valueOf(rs.getString("status")));
                lista.add(compra);
            }
        }
        return lista;
    }
}

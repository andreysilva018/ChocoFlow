package service;

import java.sql.SQLException;
import java.time.YearMonth;
import model.dashboard.ResumoDashboard;
import repository.DashboardRepository;

public class DashboardService {
    private final DashboardRepository repository = new DashboardRepository();

    public ResumoDashboard consultarDashboard() throws SQLException {
        return consultarDashboard(YearMonth.now());
    }

    public ResumoDashboard consultarDashboard(YearMonth mes) throws SQLException {
        if (mes == null) throw new IllegalArgumentException("Informe o mês da consulta.");
        return repository.consultar(mes);
    }
}

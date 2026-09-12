package com.chocoflow;

import database.Database;
import view.principal.FrmPrincipal;

public class ChocoFlow {
    public static void main(String[] args) {
        Database.criarBanco();
        java.awt.EventQueue.invokeLater(() -> {
            try {
                javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (Exception erro) {
                erro.printStackTrace();
            }
            new FrmPrincipal().setVisible(true);
        });
    }
}

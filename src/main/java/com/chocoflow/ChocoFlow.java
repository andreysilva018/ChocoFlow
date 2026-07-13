/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.chocoflow;

import database.Database;
import java.sql.Connection;
import javax.swing.JOptionPane;
import util.ConnectionFactory;
import view.compra.FrmCompra;
import view.insumo.FrmInsumo;

/**
 *
 * @author Windows
 */
public class ChocoFlow {

    public static void main(String[] args) {
        System.out.println("BEM VINDO AO SISTEMA DE GERENCIMAENTO DE PRODUCAO DE TRUFAS!");
        
        Database.criarBanco();
        try {
            for(javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()){
                if("Windows".equals(info.getName())){
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                }
            }
        } catch (Exception erro) {
            erro.printStackTrace();
        }
        
        java.awt.EventQueue.invokeLater(()-> {
            new FrmInsumo().setVisible(true);
            //new FrmCompra().seVisible(true);
        });
        
        
        try {
            Connection conn = ConnectionFactory.getConnection();
            
            if(conn != null){
                System.out.println("Conexao com o banco de dados ocorrida com sucesso!");
            }
        } catch (Exception erro) {
            erro.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro na conexao com o banco de dados - Erro:" + erro);
        }
    }
}

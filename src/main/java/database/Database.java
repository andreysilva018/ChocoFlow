/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.sql.Connection;
import java.sql.Statement;
import util.ConnectionFactory;

/**
 *
 * @author Windows
 */
public class Database {
    public static void criarBanco(){
        try (Connection conn = ConnectionFactory.getConnection(); Statement stmt = conn.createStatement()){
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS insumo (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    descricao TEXT NOT NULL,
                    unidade_medida TEXT NOT NULL,
                    quantidade_estoque REAL NOT NULL DEFAULT 0,
                    estoque_minimo REAL NOT NULL DEFAULT 0,
                    valor_ultima_compra REAL NOT NULL DEFAULT 0,
                    ativo BOOLEAN NOT NULL DEFAULT 1
                );
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS compra(
                         id INTEGER PRIMARY KEY AUTOINCREMENT,
                         date_compra TEXT NOT NULL,
                         valor_total REAL NOT NULL
                );
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS item_compra(
                         id INTEGER PRIMARY KEY AUTOINCREMENT,
                         compra_id INTEGER NOT NULL,
                         insumo_id INTEGER NOT NULL,
                         quantidade REAL NOT NULL,
                         valor_unitario REAL NOT NULL,
                         valor_total_item REAQL NOT NULL,
                         
                         FOREIGN KEY (compra_id) REFERENCES compra(id),
                         FOREIGN KEY (insumo_id) REFERENCES insumo(id)                         
                );
            """);
        } catch (Exception erro) {
            System.out.println("Erro ao criar bacno de dados");
            erro.printStackTrace();
        }
    }
}

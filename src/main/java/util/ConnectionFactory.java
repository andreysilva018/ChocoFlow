/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Windows
 */
public class ConnectionFactory {
    
    private static final String URL = "jdbc:sqlite:trufas.db";
    
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL);
    }
    
}

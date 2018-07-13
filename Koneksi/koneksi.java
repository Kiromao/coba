/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Koneksi;

import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author forsca
 */
public class koneksi {
    private Connection con;//tempat penyimpanan koneksi
    private final String db = "db_recogfil";//nama database
    private final String url = "jdbc:mysql://localhost:3306/" + db;//server link+database
    private final String user = "root";//mysql username
    private final String pass = "";//mysql password

    public Connection getCon() {
        return con;
    }

    public koneksi() {
        try {
            //registrasi driver koneksi 
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            //login mysql/koneksi dengan syarat yg ada
            con = DriverManager.getConnection(url, user, pass);
            //test message
            //JOptionPane.showMessageDialog(null, "Connected !");
        } catch (SQLException | HeadlessException  e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        koneksi con = new koneksi();
    }
}
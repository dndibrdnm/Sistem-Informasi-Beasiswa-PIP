package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;


//Database: db_beasiswa
public class config {

    private static Connection mysqlconfig;

    public static Connection getConnection() throws SQLException {
        try {
            String url  = "jdbc:mysql://localhost:3308/db_beasiswa";
            String user = "root";
            String pass = "";

            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            mysqlconfig = DriverManager.getConnection(url, user, pass);

        } catch (Exception e) {
            System.err.println("Koneksi DB Gagal: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                "Koneksi Database Gagal!\nPastikan Koneksi sudah nyala.",
                "Error Koneksi", JOptionPane.ERROR_MESSAGE);
        }
        return mysqlconfig;
    }
}

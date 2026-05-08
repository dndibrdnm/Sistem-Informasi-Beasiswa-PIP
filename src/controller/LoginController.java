package controller;

import config.config;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controller untuk FrmLogin
 *
 * Tabel yang dipakai: siswa (nisn sebagai username, nama sebagai identitas)
 * Akses: Siswa  → cek dari tabel siswa (nisn = username)
 *        TU     → hardcode atau bisa ditambah tabel user nanti
 *        KEPSEK → hardcode atau bisa ditambah tabel user nanti
 *
 * Karena db_beasiswa hanya punya tabel `siswa`, login TU & KEPSEK
 * menggunakan kredensial statis yang bisa diubah di sini.
 */
public class LoginController {

    // Kredensial statis untuk TU dan KEPSEK
    // (ganti sesuai kebutuhan, atau migrasikan ke tabel user)
    private static final String TU_USERNAME      = "tu";
    private static final String TU_PASSWORD      = "tu123";
    private static final String KEPSEK_USERNAME  = "kepsek";
    private static final String KEPSEK_PASSWORD  = "kepsek123";

    /**
     * Validasi login.
     *
     * @param username  input username dari form
     * @param password  input password dari form
     * @param akses     pilihan comboBox: "Siswa", "TU", "KEPSEK"
     * @return String[] berisi {nisn, nama} jika berhasil, null jika gagal
     */
    public String[] login(String username, String password, String akses) {

        if (username.isEmpty() || password.isEmpty() || akses.equals("Pilih:")) {
            return null;
        }

        switch (akses) {
            case "Siswa":
                return loginSiswa(username, password);

            case "TU":
                if (username.equals(TU_USERNAME) && password.equals(TU_PASSWORD)) {
                    return new String[]{"", "TU"};
                }
                return null;

            case "KEPSEK":
                if (username.equals(KEPSEK_USERNAME) && password.equals(KEPSEK_PASSWORD)) {
                    return new String[]{"", "KEPSEK"};
                }
                return null;

            default:
                return null;
        }
    }

    /**
     * Login khusus Siswa — cek NISN di tabel siswa.
     * username = NISN siswa, password = nama siswa (bisa diubah sesuai skema)
     */
    private String[] loginSiswa(String username, String password) {
        // Di tabel siswa tidak ada kolom password.
        // Konvensi sederhana: password = nama siswa (case-insensitive)
        // Ubah logika ini jika sudah ada tabel user terpisah.
        String sql = "SELECT nisn, nama FROM siswa WHERE nisn = ? AND LOWER(nama) = LOWER(?)";
        try {
            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String nisn = rs.getString("nisn");
                String nama = rs.getString("nama");
                rs.close();
                ps.close();
                return new String[]{nisn, nama};
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("LoginController.loginSiswa error: " + e.getMessage());
        }
        return null;
    }
}

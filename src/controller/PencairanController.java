package controller;

import config.config;
import model.Transaksi;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Controller untuk FrmPencairan (TU)
 *
 * Fitur:
 * - Cari siswa by NISN untuk cek status_acc
 * - Eksekusi pencairan (insert ke tabel transaksi)
 * - Tampil riwayat transaksi
 *
 * PENTING: Jalankan SQL berikut di phpMyAdmin untuk membuat tabel transaksi:
 *
 *   CREATE TABLE IF NOT EXISTS transaksi (
 *     id_transaksi INT AUTO_INCREMENT PRIMARY KEY,
 *     nisn         VARCHAR(20),
 *     nama_siswa   VARCHAR(100),
 *     tgl_acc      DATE,
 *     tgl_cair     DATE,
 *     keterangan   TEXT
 *   );
 */
public class PencairanController {

    // ----------------------------------------------------------------
    // CARI SISWA BY NISN → isi form pencairan
    // ----------------------------------------------------------------
    public String[] cariSiswaByNisn(String nisn) {
        // return: [nama, status_acc] atau null jika tidak ditemukan
        String sql = "SELECT nama, status_acc FROM siswa WHERE nisn=?";
        try {
            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nisn.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String[] hasil = {rs.getString("nama"), rs.getString("status_acc")};
                rs.close(); ps.close();
                return hasil;
            }
            rs.close(); ps.close();
            JOptionPane.showMessageDialog(null, "NISN tidak ditemukan!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error cari NISN:\n" + e.getMessage());
        }
        return null;
    }

    // ----------------------------------------------------------------
    // EKSEKUSI PENCAIRAN
    // ----------------------------------------------------------------
    public boolean cairkan(String nisn, String namaSiswa, String tglCair) {
        if (nisn == null || nisn.isEmpty()) {
            JOptionPane.showMessageDialog(null, "NISN tidak boleh kosong!");
            return false;
        }
        if (tglCair == null || tglCair.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Tanggal cair tidak boleh kosong!\nFormat: YYYY-MM-DD");
            return false;
        }

        // Validasi format tanggal
        try {
            new SimpleDateFormat("yyyy-MM-dd").parse(tglCair);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Format tanggal salah! Gunakan: YYYY-MM-DD");
            return false;
        }

        // Pastikan siswa sudah disetujui
        String cekSql = "SELECT status_acc FROM siswa WHERE nisn=?";
        try {
            Connection con = config.getConnection();
            PreparedStatement cekPs = con.prepareStatement(cekSql);
            cekPs.setString(1, nisn);
            ResultSet cekRs = cekPs.executeQuery();
            if (cekRs.next()) {
                String statusAcc = cekRs.getString("status_acc");
                cekRs.close(); cekPs.close();
                if (!"Disetujui".equalsIgnoreCase(statusAcc)) {
                    JOptionPane.showMessageDialog(null,
                        "Siswa belum disetujui oleh Kepala Sekolah!\nStatus saat ini: " + statusAcc);
                    return false;
                }
            } else {
                cekRs.close(); cekPs.close();
                JOptionPane.showMessageDialog(null, "NISN tidak ditemukan!");
                return false;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error validasi:\n" + e.getMessage());
            return false;
        }

        // Insert ke tabel transaksi
        String tanggalHariIni = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String sql = "INSERT INTO transaksi (nisn, nama_siswa, tgl_acc, tgl_cair, keterangan) "
                   + "VALUES (?, ?, ?, ?, 'Cair')";
        try {
            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nisn);
            ps.setString(2, namaSiswa);
            ps.setString(3, tanggalHariIni);
            ps.setString(4, tglCair);
            int rows = ps.executeUpdate();
            ps.close();

            if (rows > 0) {
                // Update keterangan di tabel siswa
                PreparedStatement upd = con.prepareStatement(
                    "UPDATE siswa SET keterangan='Sudah Cair' WHERE nisn=?");
                upd.setString(1, nisn);
                upd.executeUpdate();
                upd.close();

                JOptionPane.showMessageDialog(null, "Pencairan berhasil dicatat!");
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mencatat pencairan:\n" + e.getMessage());
        }
        return false;
    }

    // ----------------------------------------------------------------
    // RIWAYAT TRANSAKSI → isi JTable di jPanel2
    // ----------------------------------------------------------------
    public DefaultTableModel getRiwayatTransaksi() {
        String[] kolom = {"No Transaksi", "NISN", "Nama Siswa", "Tanggal ACC", "Tanggal Cair", "Keterangan"};
        DefaultTableModel model = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        String sql = "SELECT id_transaksi, nisn, nama_siswa, tgl_acc, tgl_cair, keterangan "
                   + "FROM transaksi ORDER BY id_transaksi DESC";
        try {
            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_transaksi"),
                    rs.getString("nisn"),
                    rs.getString("nama_siswa"),
                    rs.getString("tgl_acc"),
                    rs.getString("tgl_cair"),
                    rs.getString("keterangan")
                });
            }
            rs.close(); ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal memuat riwayat:\n" + e.getMessage());
        }
        return model;
    }

    // ----------------------------------------------------------------
    // CARI RIWAYAT (Nama / NISN)
    // ----------------------------------------------------------------
    public DefaultTableModel cariRiwayat(String keyword) {
        String[] kolom = {"No Transaksi", "NISN", "Nama Siswa", "Tanggal ACC", "Tanggal Cair", "Keterangan"};
        DefaultTableModel model = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        String sql = "SELECT id_transaksi, nisn, nama_siswa, tgl_acc, tgl_cair, keterangan "
                   + "FROM transaksi WHERE nama_siswa LIKE ? OR nisn LIKE ? "
                   + "ORDER BY id_transaksi DESC";
        try {
            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_transaksi"),
                    rs.getString("nisn"),
                    rs.getString("nama_siswa"),
                    rs.getString("tgl_acc"),
                    rs.getString("tgl_cair"),
                    rs.getString("keterangan")
                });
            }
            rs.close(); ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mencari riwayat:\n" + e.getMessage());
        }
        return model;
    }

    // ----------------------------------------------------------------
    // HITUNG TOTAL SUDAH CAIR (untuk Dashboard)
    // ----------------------------------------------------------------
    public int getTotalSudahCair() {
        String sql = "SELECT COUNT(*) FROM transaksi";
        try {
            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int n = rs.getInt(1);
                rs.close(); ps.close();
                return n;
            }
        } catch (SQLException e) {
            System.err.println("getTotalSudahCair error: " + e.getMessage());
        }
        return 0;
    }
    
    public boolean simpanTransaksi(String nisn, String nama, String tglAcc, String keterangan, String nominal) {
    // Tanggal cair otomatis hari ini
    String tglCair = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
    
    // Tambahkan kolom nominal di query INSERT
    String sql = "INSERT INTO transaksi (nisn, nama_siswa, tgl_acc, tgl_cair, nominal, keterangan) VALUES (?, ?, ?, ?, ?, ?)";
    
    try {
        Connection con = config.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, nisn);
        ps.setString(2, nama);
        ps.setString(3, tglAcc);
        ps.setString(4, tglCair);
        ps.setString(5, nominal); // Ini data nominalnya
        ps.setString(6, keterangan);
        
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    } catch (SQLException e) {
        javax.swing.JOptionPane.showMessageDialog(null, "Gagal Simpan Transaksi: " + e.getMessage());
        return false;
    }
}
}

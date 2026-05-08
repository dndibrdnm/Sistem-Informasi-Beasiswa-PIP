package controller;

import config.config;
import model.Siswa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Controller untuk FrmVerifikasi (TU)
 *
 * Fitur:
 * - Tampil daftar siswa + status berkas
 * - Cari siswa
 * - Update status berkas berdasarkan checkbox (chkKK, chkKTP, chkAkta, chkKIP)
 */
public class VerifikasiController {

    // ----------------------------------------------------------------
    // TAMPIL DATA SISWA UNTUK VERIFIKASI
    // ----------------------------------------------------------------
    public DefaultTableModel getAllUntukVerifikasi() {
        String[] kolom = {"NISN", "Nama", "Status Berkas"};
        DefaultTableModel model = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        String sql = "SELECT nisn, nama, status_berkas FROM siswa ORDER BY nama";
        try {
            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("nisn"),
                    rs.getString("nama"),
                    rs.getString("status_berkas")
                });
            }
            rs.close(); ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal memuat data verifikasi:\n" + e.getMessage());
        }
        return model;
    }

    // ----------------------------------------------------------------
    // CARI SISWA
    // ----------------------------------------------------------------
    public DefaultTableModel cariSiswa(String keyword) {
        String[] kolom = {"NISN", "Nama", "Status Berkas"};
        DefaultTableModel model = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        String sql = "SELECT nisn, nama, status_berkas FROM siswa WHERE nama LIKE ? ORDER BY nama";
        try {
            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("nisn"),
                    rs.getString("nama"),
                    rs.getString("status_berkas")
                });
            }
            rs.close(); ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mencari data:\n" + e.getMessage());
        }
        return model;
    }

    // ----------------------------------------------------------------
    // UPDATE STATUS BERKAS
    // Dipanggil saat klik btnUpdateStatus di FrmVerifikasi
    //
    // chkKK   = Kartu Keluarga
    // chkKTP  = KTP Orang Tua
    // chkAkta = Akta Kelahiran
    // chkKIP  = Kartu KIP (opsional)
    // ----------------------------------------------------------------
    
    public boolean updateStatusBerkas(String nisn, boolean kk, boolean ktp, boolean akta, boolean kip) {
        
        // 1. Logika Penentuan Status (Anggap KIP opsional, sisanya wajib)
        String statusBerkas = "Belum Lengkap";
        if (kk && ktp && akta) {
            statusBerkas = "Lengkap";
        }

        // 2. Merangkai Teks Detail Kekurangan
        String ketKK = kk ? "Sudah" : "Belum";
        String ketKTP = ktp ? "Sudah" : "Belum";
        String ketAkta = akta ? "Sudah" : "Belum";
        String ketKIP = kip ? "Sudah" : "Belum (Opsional)";

        // Menggabungkan semuanya menjadi satu kalimat rapi
        String keterangan = "Detail Berkas -- KK: " + ketKK + " | KTP: " + ketKTP + " | Akta: " + ketAkta + " | KIP: " + ketKIP;

        // 3. Simpan ke Database
        String sql = "UPDATE siswa SET status_berkas=?, keterangan=? WHERE nisn=?";
        try {
            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, statusBerkas);
            ps.setString(2, keterangan);
            ps.setString(3, nisn);
            
            int rows = ps.executeUpdate();
            ps.close();
            
            if (rows > 0) {
                javax.swing.JOptionPane.showMessageDialog(null, "Status berkas berhasil diupdate!\nStatus: " + statusBerkas);
                return true;
            }
        } catch (SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Gagal update status:\n" + e.getMessage());
        }
        return false;
    }

    private boolean doUpdate(String nisn, String statusBerkas, String keterangan) {
        String sql = "UPDATE siswa SET status_berkas=?, keterangan=? WHERE nisn=?";
        try {
            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, statusBerkas);
            ps.setString(2, keterangan);
            ps.setString(3, nisn);
            int rows = ps.executeUpdate();
            ps.close();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null,
                    "Status berkas berhasil diupdate!\nStatus: " + statusBerkas);
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal update status:\n" + e.getMessage());
        }
        return false;
    }

    // ----------------------------------------------------------------
    // HITUNG TOTAL BERKAS BELUM LENGKAP (untuk Dashboard)
    // ----------------------------------------------------------------
    public int getTotalBelumLengkap() {
        String sql = "SELECT COUNT(*) FROM siswa WHERE status_berkas != 'Lengkap'";
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
            System.err.println("getTotalBelumLengkap error: " + e.getMessage());
        }
        return 0;
    }
}

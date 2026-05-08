package controller;

import config.config;

import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Controller untuk FrmCekStatus (Portal Siswa)
 *
 * Siswa yang login bisa melihat:
 * - Status pengajuan saat ini (prgStatus + lblStatusInfo + lblKeterangan)
 * - Riwayat pencairan beasiswa (tblRiwayatSiswa)
 */
public class CekStatusController {

    /**
     * Ambil status berkas & ACC siswa berdasarkan NISN yang sedang login.
     *
     * @return String[] {status_berkas, status_acc, keterangan} atau null
     */
    public String[] getStatusSiswa(String nisn) {
        String sql = "SELECT status_berkas, status_acc, keterangan FROM siswa WHERE nisn=?";
        try {
            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nisn);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String[] hasil = {
                    rs.getString("status_berkas"),
                    rs.getString("status_acc"),
                    rs.getString("keterangan")
                };
                rs.close(); ps.close();
                return hasil;
            }
            rs.close(); ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal memuat status:\n" + e.getMessage());
        }
        return null;
    }

    /**
     * Konversi status ke nilai progress bar (0–100).
     *
     * Tahapan:
     *   Belum Lengkap / Revisi → 25
     *   Lengkap (Pending)      → 50
     *   Disetujui              → 75
     *   Sudah Cair             → 100
     *   Ditolak                → 0
     */
    public int getProgressValue(String statusBerkas, String statusAcc) {
        if ("Ditolak".equalsIgnoreCase(statusAcc))     return 0;
        if ("Sudah Cair".equalsIgnoreCase(statusAcc))  return 100;
        if ("Disetujui".equalsIgnoreCase(statusAcc))   return 75;
        if ("Lengkap".equalsIgnoreCase(statusBerkas)
                && "Pending".equalsIgnoreCase(statusAcc)) return 50;
        return 25; // Belum Lengkap atau Revisi
    }

    
    //Label status yang ditampilkan ke siswa.
     
    public String getLabelStatus(String statusBerkas, String statusAcc) {
        if ("Ditolak".equalsIgnoreCase(statusAcc))    return "Ditolak";
        if ("Sudah Cair".equalsIgnoreCase(statusAcc)) return "Dana Sudah Cair";
        if ("Revisi".equalsIgnoreCase(statusBerkas))  return "Berkas Perlu Revisi";
        if (statusBerkas.equalsIgnoreCase("Lengkap") && statusAcc.equalsIgnoreCase("Disetujui")) {
        return "Selamat! Beasiswa Disetujui (Menunggu Pencairan Dana)";
    }
        return "Berkas Belum Lengkap";
    }

    /**
     * Riwayat pencairan siswa dari tabel transaksi → tblRiwayatSiswa
     * Kolom: Tahun Ajaran, Tanggal Cair, Nominal, Keterangan
     *
     * Kolom "Nominal" belum ada di tabel transaksi — ditampilkan sebagai "-".
     * Tambahkan kolom nominal di tabel transaksi jika diperlukan.
     */
    public DefaultTableModel getRiwayatSiswa(String nisn) {
        String[] kolom = {"Tahun Ajaran", "Tanggal Cair", "Nominal", "Keterangan"};
        DefaultTableModel model = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // Cek apakah tabel transaksi sudah ada
       String sql = "SELECT YEAR(tgl_cair) AS tahun, tgl_cair, nominal, keterangan "
           + "FROM transaksi WHERE nisn=? ORDER BY tgl_cair DESC";
        try {
            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nisn);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("tahun"),
                    rs.getString("tgl_cair"),
                    rs.getString("nominal"),
                    rs.getString("keterangan")
                });
            }
            rs.close(); ps.close();
        } catch (SQLException e) {
            // Jika tabel transaksi belum dibuat, tampilkan kosong saja
            System.err.println("getRiwayatSiswa: " + e.getMessage());
        }
        return model;
    }
}

package controller;

import config.config;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Controller untuk FrmApproval (KEPSEK)
 *
 * Hanya siswa yang status_berkas = 'Lengkap' yang muncul di sini
 * untuk disetujui atau ditolak oleh Kepala Sekolah.
 */
public class ApprovalController {

    // TAMPIL SISWA SIAP DIAPPROVE (status_berkas = Lengkap)
    public DefaultTableModel getSiapApproval() {
        String[] kolom = {"NISN", "Nama Siswa", "Kelas", "Berkas", "Tanggal Verifikasi TU"};
        DefaultTableModel model = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // Kolom rata_nilai & tgl_verifikasi belum ada di tabel siswa saat ini.
        // Tambahkan ALTER TABLE jika diperlukan, atau biarkan kosong dulu.
        String sql = "SELECT nisn, nama, kelas, "
                   + "status_berkas, "
                   + "NOW() AS tgl_verifikasi "
                   + "FROM siswa "
                   + "WHERE status_berkas = 'Lengkap' AND status_acc = 'Pending' "
                   + "ORDER BY nama";
        try {
            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("nisn"),
                    rs.getString("nama"),
                    rs.getString("kelas"),
                    rs.getString("status_berkas"),
                    rs.getString("tgl_verifikasi")
                });
            }
            rs.close(); ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal memuat data approval:\n" + e.getMessage());
        }
        return model;
    }

    // CARI SISWA DI DAFTAR APPROVAL
    public DefaultTableModel cariApproval(String keyword) {
        String[] kolom = {"NISN", "Nama Siswa", "Kelas", "Berkas", "Tanggal Verifikasi TU"};
        DefaultTableModel model = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        String sql = "SELECT nisn, nama, kelas, status_berkas, NOW() AS tgl_verifikasi "
                   + "FROM siswa WHERE status_berkas='Lengkap' AND status_acc='Pending' "
                   + "AND nama LIKE ? ORDER BY nama";
        try {
            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("nisn"),
                    rs.getString("nama"),
                    rs.getString("kelas"),
                    rs.getString("status_berkas"),
                    rs.getString("tgl_verifikasi")
                });
            }
            rs.close(); ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mencari data:\n" + e.getMessage());
        }
        return model;
    }

    // SETUJUI (APPROVE)
    public boolean approve(String nisn) {
        if (nisn == null || nisn.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Pilih siswa dari tabel terlebih dahulu!");
            return false;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(null,
            "Setujui beasiswa untuk NISN: " + nisn + "?",
            "Konfirmasi Persetujuan", JOptionPane.YES_NO_OPTION);
        if (konfirmasi != JOptionPane.YES_OPTION) return false;

        String sql = "UPDATE siswa SET status_acc='Disetujui', keterangan='Berkas Komplit' WHERE nisn=?";
        try {
            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nisn);
            int rows = ps.executeUpdate();
            ps.close();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Beasiswa berhasil DISETUJUI!");
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menyetujui:\n" + e.getMessage());
        }
        return false;
    }

    // TOLAK (REJECT)
    public boolean reject(String nisn) {
        if (nisn == null || nisn.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Pilih siswa dari tabel terlebih dahulu!");
            return false;
        }

        String alasan = javax.swing.JOptionPane.showInputDialog(null,
            "Masukkan alasan penolakan:", "Alasan Tolak",
            javax.swing.JOptionPane.QUESTION_MESSAGE);
        if (alasan == null || alasan.trim().isEmpty()) return false;

        String sql = "UPDATE siswa SET status_acc='Ditolak', keterangan=? WHERE nisn=?";
        try {
            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, alasan.trim());
            ps.setString(2, nisn);
            int rows = ps.executeUpdate();
            ps.close();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Beasiswa DITOLAK dengan alasan: " + alasan);
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menolak:\n" + e.getMessage());
        }
        return false;
    }

    // HITUNG YANG SIAP DI-ACC (untuk Dashboard)
    public int getTotalSiapAcc() {
        String sql = "SELECT COUNT(*) FROM siswa WHERE status_berkas='Lengkap' AND status_acc='Pending'";
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
            System.err.println("getTotalSiapAcc error: " + e.getMessage());
        }
        return 0;
    }
}

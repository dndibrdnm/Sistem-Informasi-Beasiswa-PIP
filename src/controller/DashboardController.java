package controller;

import config.config;

import java.sql.*;
import javax.swing.JOptionPane;
import org.jfree.data.general.DefaultPieDataset;

/**
 * Controller untuk FrmDashboard
 *
 * Menyediakan data angka untuk 4 panel statistik:
 *  - Total Siswa          → jPanel1 (lblTotalSiswa)
 *  - Berkas Belum Lengkap → jPanel2 (lblTotalBelumLengkap)
 *  - Siap Di-ACC          → jPanel3 (lblTotalPending)
 *  - Sudah Cair           → jPanel4 (lblTotalCair)
 */
public class DashboardController {

    private final SiswaController       siswaCtrl       = new SiswaController();
    private final VerifikasiController  verifikasiCtrl  = new VerifikasiController();
    private final ApprovalController    approvalCtrl    = new ApprovalController();
    private final PencairanController   pencairanCtrl   = new PencairanController();

    /** Total seluruh siswa terdaftar */
    public int getTotalSiswa() {
        return siswaCtrl.getTotalSiswa();
    }

    /** Siswa yang status_berkas != 'Lengkap' */
    public int getTotalBelumLengkap() {
        return verifikasiCtrl.getTotalBelumLengkap();
    }

    /** Siswa status_berkas='Lengkap' dan status_acc='Pending' → siap disetujui Kepsek */
    public int getTotalSiapAcc() {
        return approvalCtrl.getTotalSiapAcc();
    }

    /** Jumlah transaksi pencairan yang sudah dilakukan */
    public int getTotalSudahCair() {
        return pencairanCtrl.getTotalSudahCair();
    }

    /**
     * Persentase beasiswa yang sudah cair dibanding total siswa.
     * Digunakan untuk pnlChart (ProgressBar atau pie chart sederhana).
     */
    public int getPersentaseCair() {
        int total = getTotalSiswa();
        if (total == 0) return 0;
        int cair = getTotalSudahCair();
        return (int) Math.round((double) cair / total * 100);
    }

    /**
     * Refresh semua data sekaligus → return int[] {totalSiswa, belumLengkap, siapAcc, sudahCair}
     */
    public int[] refreshSemua() {
        return new int[]{
            getTotalSiswa(),
            getTotalBelumLengkap(),
            getTotalSiapAcc(),
            getTotalSudahCair()
        };
    }
    
    public DefaultPieDataset getStatusDataset() {
    DefaultPieDataset dataset = new DefaultPieDataset();
    try {
        Connection con = config.getConnection();
        // Menghitung jumlah per status
        String sql = "SELECT status_acc, COUNT(*) as jumlah FROM siswa GROUP BY status_acc";
        ResultSet rs = con.createStatement().executeQuery(sql);
        
        while (rs.next()) {
            dataset.setValue(rs.getString("status_acc"), rs.getInt("jumlah"));
        }
    } catch (Exception e) {
        System.err.println("Gagal ambil data chart: " + e.getMessage());
    }
    return dataset;
}
}

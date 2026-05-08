package controller;

import config.config;
import model.Siswa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.FileInputStream;

/**
 * Controller untuk FrmSiswa
 *
 * Operasi: tampilSemua, cariNama, simpan, ubah, hapus
 */
public class SiswaController {

    // ----------------------------------------------------------------
    // AMBIL SEMUA DATA → isi JTable
    // ----------------------------------------------------------------
    public DefaultTableModel getAllSiswa() {
        String[] kolom = {"NISN", "Nama Siswa", "Kelas", "Jurusan"};
        DefaultTableModel model = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        String sql = "SELECT nisn, nama, kelas, jurusan FROM siswa ORDER BY nama";
        try {
            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("nisn"),
                    rs.getString("nama"),
                    rs.getString("kelas"),
                    rs.getString("jurusan")
                });
            }
            rs.close(); ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal memuat data siswa:\n" + e.getMessage());
        }
        return model;
    }

    // ----------------------------------------------------------------
    // CARI BERDASARKAN NAMA
    // ----------------------------------------------------------------
    public DefaultTableModel cariSiswa(String keyword) {
        String[] kolom = {"NISN", "Nama Siswa", "Kelas", "Jurusan"};
        DefaultTableModel model = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        String sql = "SELECT nisn, nama, kelas, jurusan FROM siswa WHERE nama LIKE ? ORDER BY nama";
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
                    rs.getString("jurusan")
                });
            }
            rs.close(); ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mencari data:\n" + e.getMessage());
        }
        return model;
    }

    // ----------------------------------------------------------------
    // SIMPAN (INSERT)
    // ----------------------------------------------------------------
    public boolean simpanSiswa(Siswa s) {
        // Validasi input dasar
        if (s.getNisn().isEmpty() || s.getNama().isEmpty()) {
            JOptionPane.showMessageDialog(null, "NISN dan Nama tidak boleh kosong!");
            return false;
        }

        String sql = "INSERT INTO siswa (nisn, nama, kelas, jurusan, status_berkas, status_acc, keterangan) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, s.getNisn());
            ps.setString(2, s.getNama());
            ps.setString(3, s.getKelas());
            ps.setString(4, s.getJurusan());
            ps.setString(5, s.getStatusBerkas() != null ? s.getStatusBerkas() : "Belum Lengkap");
            ps.setString(6, s.getStatusAcc()    != null ? s.getStatusAcc()    : "Pending");
            ps.setString(7, s.getKeterangan()   != null ? s.getKeterangan()   : "-");
            int rows = ps.executeUpdate();
            ps.close();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Data siswa berhasil disimpan!");
                return true;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(null, "NISN sudah terdaftar! Gunakan NISN yang berbeda.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data:\n" + e.getMessage());
        }
        return false;
    }

    // ----------------------------------------------------------------
    // UBAH (UPDATE)
    // ----------------------------------------------------------------
    public boolean ubahSiswa(Siswa s) {
        if (s.getNisn().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Pilih data siswa dari tabel terlebih dahulu!");
            return false;
        }

        String sql = "UPDATE siswa SET nama=?, kelas=?, jurusan=? WHERE nisn=?";
        try {
            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, s.getNama());
            ps.setString(2, s.getKelas());
            ps.setString(3, s.getJurusan());
            ps.setString(4, s.getNisn());
            int rows = ps.executeUpdate();
            ps.close();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Data siswa berhasil diubah!");
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengubah data:\n" + e.getMessage());
        }
        return false;
    }

    // ----------------------------------------------------------------
    // HAPUS (DELETE)
    // ----------------------------------------------------------------
    public boolean hapusSiswa(String nisn) {
        if (nisn == null || nisn.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Pilih data siswa dari tabel terlebih dahulu!");
            return false;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(null,
            "Yakin ingin menghapus siswa dengan NISN: " + nisn + "?",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (konfirmasi != JOptionPane.YES_OPTION) return false;

        String sql = "DELETE FROM siswa WHERE nisn=?";
        try {
            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nisn);
            int rows = ps.executeUpdate();
            ps.close();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Data siswa berhasil dihapus!");
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menghapus data:\n" + e.getMessage());
        }
        return false;
    }

    // ----------------------------------------------------------------
    // GET SATU SISWA BERDASARKAN NISN
    // ----------------------------------------------------------------
    public Siswa getSiswaByNisn(String nisn) {
        String sql = "SELECT * FROM siswa WHERE nisn=?";
        try {
            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nisn);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Siswa s = new Siswa(
                    rs.getString("nisn"),
                    rs.getString("nama"),
                    rs.getString("kelas"),
                    rs.getString("jurusan"),
                    rs.getString("status_berkas"),
                    rs.getString("status_acc"),
                    rs.getString("keterangan")
                );
                rs.close(); ps.close();
                return s;
            }
            rs.close(); ps.close();
        } catch (SQLException e) {
            System.err.println("getSiswaByNisn error: " + e.getMessage());
        }
        return null;
    }

    // ----------------------------------------------------------------
    // HITUNG TOTAL SISWA
    // ----------------------------------------------------------------
    public int getTotalSiswa() {
        String sql = "SELECT COUNT(*) FROM siswa";
        try {
            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int total = rs.getInt(1);
                rs.close(); ps.close();
                return total;
            }
        } catch (SQLException e) {
            System.err.println("getTotalSiswa error: " + e.getMessage());
        }
        return 0;
    }
    
    // Fungsi untuk Import Excel
    public boolean importDataExcel(String filePath) {
        String sql = "INSERT INTO siswa (nisn, nama, kelas, jurusan, status_berkas, status_acc) VALUES (?,?,?,?,'Belum Lengkap','Pending')";
        int sukses = 0;
        int gagal = 0;

        try {
            java.io.FileInputStream file = new java.io.FileInputStream(new java.io.File(filePath));
            
            // Membaca file Excel (.xlsx)
            org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook(file);
            org.apache.poi.xssf.usermodel.XSSFSheet sheet = workbook.getSheetAt(0); // Ambil sheet pertama

            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            // Looping baris dari atas ke bawah (dimulai dari baris 1, karena baris 0 biasanya Header/Judul)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                org.apache.poi.ss.usermodel.Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    // Mengambil data per kolom (Kolom 0: NISN, 1: Nama, 2: Kelas, 3: Jurusan)
                    String nisn = getCellValue(row.getCell(0));
                    String nama = getCellValue(row.getCell(1));
                    String kelas = getCellValue(row.getCell(2));
                    String jurusan = getCellValue(row.getCell(3));

                    // Jika NISN dan Nama tidak kosong, masukkan ke database
                    if (!nisn.isEmpty() && !nama.isEmpty()) {
                        ps.setString(1, nisn);
                        ps.setString(2, nama);
                        ps.setString(3, kelas);
                        ps.setString(4, jurusan);
                        ps.executeUpdate();
                        sukses++;
                    }
                } catch (SQLException ex) {
                    gagal++; // Jika gagal (misal NISN duplikat), abaikan dan lanjut ke siswa berikutnya
                }
            }
            
            workbook.close();
            file.close();
            
            javax.swing.JOptionPane.showMessageDialog(null, "Import Selesai!\nBerhasil: " + sukses + " data\nGagal/Duplikat: " + gagal + " data");
            return true;

        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Gagal membaca file Excel: " + e.getMessage());
            return false;
        }
    }

    // Fungsi bantuan untuk mengubah tipe data cell Excel menjadi String
    private String getCellValue(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf((long) cell.getNumericCellValue());
            default: return "";
        }
    }
}

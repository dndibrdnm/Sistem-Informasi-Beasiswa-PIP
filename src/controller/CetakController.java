/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import config.config;
import java.io.FileOutputStream;
import java.sql.*;
import javax.swing.JOptionPane;
/**
 *
 * @author Advan Comp
 */
public class CetakController {
    // 1. CETAK LAPORAN SEMUA PENERIMA (Untuk TU/Laporan Bulanan)
    public void cetakLaporanPenerima() {
        Document document = new Document(PageSize.A4);
        try {
            String path = "Laporan_Penerima_Beasiswa.pdf";
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            // Font Style
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

            // Judul
            Paragraph title = new Paragraph("DAFTAR PENERIMA BEASISWA\n\n", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Tabel
            PdfPTable table = new PdfPTable(4); // 4 Kolom
            table.setWidthPercentage(100);
            
            // Header Tabel
            table.addCell(new PdfPCell(new Phrase("NISN", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Nama Siswa", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Jurusan", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Status", headerFont)));

            // Ambil Data dari Database
            Connection con = config.getConnection();
            String sql = "SELECT nisn, nama, jurusan, status_acc FROM siswa WHERE status_acc='Disetujui'";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                table.addCell(rs.getString("nisn"));
                table.addCell(rs.getString("nama"));
                table.addCell(rs.getString("jurusan"));
                table.addCell(rs.getString("status_acc"));
            }

            document.add(table);
            document.close();
            JOptionPane.showMessageDialog(null, "Laporan PDF Berhasil Dibuat: " + path);
            java.awt.Desktop.getDesktop().open(new java.io.File(path));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal Cetak: " + e.getMessage());
        }
    }

    // 2. CETAK SURAT PENGANTAR INDIVIDU (Untuk Kepsek)
    public void cetakSuratPengantar(String nisn) {
        Document document = new Document(PageSize.A4);
        try {
            String path = "Surat_Pengantar_" + nisn + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            // Header Surat
            Paragraph kop = new Paragraph("SMK NEGERI 3 KUNINGAN\nSurat Keterangan Penerima Beasiswa\n----------------------------------\n\n", 
                    new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD));
            kop.setAlignment(Element.ALIGN_CENTER);
            document.add(kop);

            // Ambil detail siswa
            Connection con = config.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM siswa WHERE nisn=?");
            ps.setString(1, nisn);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                    String isi = "Yang bertanda tangan di bawah ini, Kepala Sekolah SMK Negeri 3 KUNINGAN, "
                           + "menerangkan bahwa siswa tersebut di bawah ini:\n\n"
                           + "Nama : " + rs.getString("nama") + "\n"
                           + "NISN : " + rs.getString("nisn") + "\n"
                           + "Kelas/Jurusan : " + rs.getString("kelas") + " / " + rs.getString("jurusan") + "\n\n"
                           + "Telah dinyatakan LAYAK dan DISETUJUI sebagai penerima beasiswa tahun ajaran 2025.\n\n"
                           + "Demikian surat ini dibuat untuk dipergunakan sebagaimana mestinya.";
                
                document.add(new Paragraph(isi));
                
                // Tanda Tangan (Simple)
                Paragraph ttd = new Paragraph("\n\n\nMengetahui,\nKepala Sekolah\n\n\n( ........................... )");
                ttd.setAlignment(Element.ALIGN_RIGHT);
                document.add(ttd);
            }

            document.close();
            JOptionPane.showMessageDialog(null, "Surat Pengantar Berhasil Dibuat!");
            java.awt.Desktop.getDesktop().open(new java.io.File(path));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal Cetak Surat: " + e.getMessage());
        }
    }
}

package model;

public class Siswa {

    private String nisn;
    private String nama;
    private String kelas;
    private String jurusan;
    private String statusBerkas; // "Lengkap", "Belum Lengkap", "Revisi"
    private String statusAcc;    // "Pending", "Disetujui", "Ditolak"
    private String keterangan;

    public Siswa() {}

    public Siswa(String nisn, String nama, String kelas, String jurusan,
                 String statusBerkas, String statusAcc, String keterangan) {
        this.nisn         = nisn;
        this.nama         = nama;
        this.kelas        = kelas;
        this.jurusan      = jurusan;
        this.statusBerkas = statusBerkas;
        this.statusAcc    = statusAcc;
        this.keterangan   = keterangan;
    }

    // ---- Getter & Setter ----

    public String getNisn()              { return nisn; }
    public void   setNisn(String nisn)   { this.nisn = nisn; }

    public String getNama()              { return nama; }
    public void   setNama(String nama)   { this.nama = nama; }

    public String getKelas()             { return kelas; }
    public void   setKelas(String kelas) { this.kelas = kelas; }

    public String getJurusan()                 { return jurusan; }
    public void   setJurusan(String jurusan)   { this.jurusan = jurusan; }

    public String getStatusBerkas()                    { return statusBerkas; }
    public void   setStatusBerkas(String statusBerkas) { this.statusBerkas = statusBerkas; }

    public String getStatusAcc()                 { return statusAcc; }
    public void   setStatusAcc(String statusAcc) { this.statusAcc = statusAcc; }

    public String getKeterangan()                  { return keterangan; }
    public void   setKeterangan(String keterangan) { this.keterangan = keterangan; }

    @Override
    public String toString() {
        return "Siswa{nisn='" + nisn + "', nama='" + nama + "', kelas='" + kelas +
               "', jurusan='" + jurusan + "', statusBerkas='" + statusBerkas +
               "', statusAcc='" + statusAcc + "'}";
    }
}

package model;

public class Transaksi {

    private int    idTransaksi;
    private String nisn;
    private String namaSiswa;
    private String tglAcc;
    private String tglCair;
    private String keterangan;

    public Transaksi() {}

    public Transaksi(int idTransaksi, String nisn, String namaSiswa,
                     String tglAcc, String tglCair, String keterangan) {
        this.idTransaksi = idTransaksi;
        this.nisn        = nisn;
        this.namaSiswa   = namaSiswa;
        this.tglAcc      = tglAcc;
        this.tglCair     = tglCair;
        this.keterangan  = keterangan;
    }

    // ---- Getter & Setter ----

    public int  getIdTransaksi()               { return idTransaksi; }
    public void setIdTransaksi(int id)         { this.idTransaksi = id; }

    public String getNisn()              { return nisn; }
    public void   setNisn(String nisn)   { this.nisn = nisn; }

    public String getNamaSiswa()                   { return namaSiswa; }
    public void   setNamaSiswa(String namaSiswa)   { this.namaSiswa = namaSiswa; }

    public String getTglAcc()                { return tglAcc; }
    public void   setTglAcc(String tglAcc)   { this.tglAcc = tglAcc; }

    public String getTglCair()                 { return tglCair; }
    public void   setTglCair(String tglCair)   { this.tglCair = tglCair; }

    public String getKeterangan()                  { return keterangan; }
    public void   setKeterangan(String keterangan) { this.keterangan = keterangan; }
}

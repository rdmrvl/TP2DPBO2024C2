public class Mahasiswa {
    private String nim;
    private String nama;
    private String jenisKelamin;
    private boolean bekerja;

    public Mahasiswa(String nim, String nama, String jenisKelamin, boolean bekerja) {
        this.nim = nim;
        this.nama = nama;
        this.jenisKelamin = jenisKelamin;
        this.bekerja = bekerja;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public boolean isBekerja() {
        return bekerja;
    }

    public void setBekerja(boolean bekerja) {
        this.bekerja = bekerja;
    }
}

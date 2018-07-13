
package Absensi;
 
import inputan.mhs;
import java.sql.Timestamp;

/**
 *
 * @author forsca
 */
public class absensi {
    
    private int id;
    Timestamp timestamp;
    private String keterangan;
    private mhs mahasiswa;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public mhs getMahasiswa() {
        return mahasiswa;
    }

    public void setMahasiswa(mhs mahasiswa) {
        this.mahasiswa = mahasiswa;
    }
}

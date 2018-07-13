package Image;

import inputan.mhs;

/**
 *
 * @author forsca
 */
public class trainingImage {
    private int id;
    private String path;
    private String eigvector;
    private Double eigvalue;
    private mhs mahasiswa;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public mhs getMahasiswa() {
        return mahasiswa;
    }

    public void setMahasiswa(mhs mahasiswa) {
        this.mahasiswa = mahasiswa;
    }
    
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getEigvector() {
        return eigvector;
    }

    public void setEigvector(String eigvector) {
        this.eigvector = eigvector;
    }

    public Double getEigvalue() {
        return eigvalue;
    }

    public void setEigvalue(Double eigvalue) {
        this.eigvalue = eigvalue;
    }
}

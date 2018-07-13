package inputan;

/**
 *
 * @author forsca
 */
public class mhs {
   private int id;
   private String nim;
   private String nama;
   private String imagePath;
   
//   khusus untuk recog
   private int countResult;

    public int getCountResult() {
        return countResult;
    }

    public void setCountResult(int countResult) {
        this.countResult = countResult;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}

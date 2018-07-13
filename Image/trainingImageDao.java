package Image;


import javax.swing.JPanel;


/**
 *
 * @author forsca
 */
public interface trainingImageDao {
    public void read(JPanel jPanel, int mahasiswa_id);

    public void trainingImage(trainingImage image);

    public void saveEigenface(trainingImage image);

    public void delete(int id);

    public void search(String key);
}

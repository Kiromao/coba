package Image;

import Koneksi.koneksi;
import java.awt.Dimension;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author forsca
 */
public class trainingImageImpl implements trainingImageDao {

    private koneksi con = new koneksi();
    private Statement s;
    private PreparedStatement ps;
    private ResultSet rs;
    private DefaultTableModel dtm;
    private String[] column = {"Id", "path", "eigenface", "eigenvalue"};
    private String read = "select * from wajah where mahasiswa_id=?";
    private String readForBundle = "select * from wajah order by pathTi";
    private String create = "insert into wajah (pathTi, mahasiswa_id)values(?,?)";
    private String update = "insert into wajah set eigenface=?,eigenvalue=? where id=?";
    private String delete = "delete from wajah where id=?";
    private String search = "select * from wajah where nama like %?%";

    private int image_num = 0;

//    ambil gambar dari DB untuk dimasukan ke dalam FACEBUNDLE
    public ArrayList FileNamesAndID() {
        ArrayList<String> PathImage = new ArrayList();
        ArrayList<String> IdMahasiswa = new ArrayList();
        ArrayList result = new ArrayList();

        try {
            ps = con.getCon().prepareStatement(readForBundle);
            rs = ps.executeQuery();
            while (rs.next()) {
                PathImage.add(rs.getString("pathTi"));
                IdMahasiswa.add(rs.getString("mahasiswa_id"));
            }
            
            result.add(PathImage);
            result.add(IdMahasiswa);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

        return result;
    }

    @Override
    public void read(JPanel jPanel, int mahasiswa_id) {
        try {
            ps = con.getCon().prepareStatement(read);
            ps.setInt(1, mahasiswa_id);
            rs = ps.executeQuery();
            while (rs.next()) {
                JLabel image = new JLabel();
                image.setIcon(new ImageIcon("trainingImages/" + rs.getString("pathTi")));
                image.setSize(new Dimension(150, 150));
                jPanel.add(image);
                jPanel.updateUI();
//                System.out.println(rs.getString("pathTi"));
                image_num += 1;
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    public int get_image_num() {
        return image_num;
    }

    @Override
    public void trainingImage(trainingImage image) {
        try {
            ps = con.getCon().prepareStatement(create);
            ps.setString(1, image.getPath());
            ps.setInt(2, image.getMahasiswa().getId());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Tambah data berhasil !");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    @Override
    public void saveEigenface(trainingImage image) {
        try {
            ps = con.getCon().prepareStatement(update);
            ps.setString(2, image.getEigvector());
            ps.setString(3, String.valueOf(image.getEigvalue()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, " data berhasil !");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try {
            ps = con.getCon().prepareStatement(delete);
            ps.setString(1, String.valueOf(id));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Hapus data berhasil !");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    @Override
    public void search(String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

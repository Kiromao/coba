package inputan;

import Koneksi.koneksi;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author forsca
 */
public class mhsImpl implements mhsDao {

    private koneksi con = new koneksi();
    private Statement s;
    private PreparedStatement ps;
    private ResultSet rs;
    private DefaultTableModel dtm;
    private String[] column = {"Id", "Nim", "Nama",};
    private String read = "select * from mahasiswa order by id";
    private String create = "insert into mahasiswa (nim,nama)values(?,?)";
    private String update = "update mahasiswa set nim=?,nama=? where id=?";
    private String delete = "delete from mahasiswa where id=?";
    private String search = "select * from mahasiswa where nama like %?%";
    private String apaya = "select * from mahasiswa where nim = ?";

    @Override
    public void read(JTable table) {
        try {
            dtm = new DefaultTableModel(null, column);
            s = con.getCon().createStatement();
            rs = s.executeQuery(read);
            while (rs.next()) {
                Object[] col = new Object[3];
                col[0] = rs.getInt("id");
                col[1] = rs.getString("nim");
                col[2] = rs.getString("nama");
                dtm.addRow(col);
            }
            table.setModel(dtm);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    public mhs get_mahasiswa(String nim) {
        try {
            ps = con.getCon().prepareStatement(apaya);
            ps.setString(1, nim);
            ps.executeQuery();
            rs = ps.getResultSet();

            mhs mahasiswa = new mhs();
            while (rs.next()) {
                mahasiswa.setId(rs.getInt("id"));
                mahasiswa.setNim(rs.getString("nim"));
                mahasiswa.setNama(rs.getString("nama"));
            }
            return mahasiswa;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return null;
    }

    @Override
    public void create(mhs mahasiswa) {
        try {
            ps = con.getCon().prepareStatement(create);
            ps.setString(1, String.valueOf(mahasiswa.getNim()));
            ps.setString(2, mahasiswa.getNama());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Tambah data berhasil !");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    @Override
    public void update(mhs mahasiswa) {
        try {
            ps = con.getCon().prepareStatement(update);
            ps.setString(1, mahasiswa.getNim());
            ps.setString(2, mahasiswa.getNama());
            ps.setInt(3, mahasiswa.getId());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Ubah data berhasil !");
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

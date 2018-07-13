package User;

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
public class UserImp implements UserDao{
    private String view = "select * from user order by id";
    private String insert = "insert into user(nama,username,password,status,"
            + "no_telp)value(?,?,?,?,?)";
    private String update = "update user set nama=?,username=?,password=?,"
            + "status=?,no_telp=? where id=?";
    private String delete = "delete from user where id=?";
    private String search = "select * from user where nama like %?%";

    private Statement s;
    private PreparedStatement ps;
    private ResultSet rs;
    private koneksi con = new koneksi();
    private DefaultTableModel dtm;
    private final String[] column = {"ID", "NAMA", "USERNAME", "PASSWORD", "STATUS",
        "NO.TELP/HP"};
    @Override
    public void read(JTable table) {
     dtm = new DefaultTableModel(null,column);
        try {
            s = con.getCon().createStatement();
            rs = s.executeQuery(view);
            while(rs.next()){
                Object[]col = new Object[6];
                col[0] = rs.getInt("id");
                col[1] = rs.getString("nama");
                col[2] = rs.getString("username");
                col[3] = rs.getString("password");
                col[4] = rs.getString("status");
                col[5] = rs.getString("no_telp");
                dtm.addRow(col);
            }
            table.setModel(dtm);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,"Gagal mengambil data !");
        }
    }

    @Override
    public void create(User user) {
         try {
            ps = con.getCon().prepareStatement(insert);
            ps.setString(1, user.getNama());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getStatus());
            ps.setString(5, user.getNoTelp());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Tambah data berhasil !");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    @Override
    public void update(User user) {
         try {
            ps = con.getCon().prepareStatement(update);
            ps.setString(1, user.getNama());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getStatus());
            ps.setString(5, user.getNoTelp());
            ps.setInt(6, user.getId());
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

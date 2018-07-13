package Absensi;

import Koneksi.koneksi;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author forsca
 */
public class absensiImp implements absensiDao{
    private koneksi con = new koneksi();
    private Statement s;
    private PreparedStatement ps;
    private ResultSet rs;
    private DefaultTableModel dtm;
    private String[] column = {"Id", "Nim", "Nama","Keterangan","Time"};
    private String read_absen = "select * from view_absensi order by id";
    private String insert = "insert into absensi (keterangan,mahasiswa_id)values(?,?)";
    private String update = "update absensi set keterangan=?,mahasiswa_id=? where id=?";
    private String delete = "delete from absensi where id=?";
    private String search = "select * from absensi where nama like %?%";
    @Override
    public void read() {
     
    }

    @Override
    public void read_absen(JTable table) {
    try {
            dtm= new DefaultTableModel(null,column);
            s=con.getCon().createStatement();
            rs = s.executeQuery(read_absen);
            while(rs.next()){
                Object[] col = new Object[5];
                col[0] = rs.getInt("id");
                col[1] = rs.getString("nim");
                col[2] = rs.getString("nama");
                col[3] = rs.getString("keterangan");
                Date date = new Date();
                String s =  new SimpleDateFormat("dd/MM/yyyy").format(date); 
                col[4] = rs.getString("timestamp");
                dtm.addRow(col);
            }
            table.setModel(dtm);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    
    @Override
    public void create(absensi abs) {
         try {
            ps = con.getCon().prepareStatement(insert);
            ps.setString(1,abs.getKeterangan());
            ps.setInt(2, abs.getMahasiswa().getId());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Tambah data berhasil !");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    @Override
    public void update(absensi abs) {
        try {
            ps = con.getCon().prepareStatement(update);
            ps.setString(1, abs.getKeterangan());
            ps.setInt(2, abs.getMahasiswa().getId());
            ps.setInt(3, abs.getId());
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
    



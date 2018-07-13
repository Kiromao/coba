package Auth;

import Koneksi.koneksi;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author forsca
 */
public class AuthImp implements AuthDao{
    private final String authQuery = "select * from user where username = ? and password = ?";
    private PreparedStatement ps;
    private ResultSet rs;
    private final koneksi koneksi = new koneksi();
    @Override
    public void Auth(String username, String password) {
        try {
            ps = koneksi.getCon().prepareStatement(authQuery);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();
            while (rs.next()) {
                Auth.AUTH = true;
                Auth.NAMA = rs.getString("nama");
                Auth.USER_ID = rs.getInt("id");
                Auth.STATUS = rs.getString("status");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    @Override
    public void logout() {
        Auth.AUTH = false;
        Auth.NAMA = "";
        Auth.USER_ID = 0;
        Auth.STATUS = "";
    }
    
}

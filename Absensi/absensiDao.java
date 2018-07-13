package Absensi;

import javax.swing.JTable;

/**
 *
 * @author forsca
 */
public interface absensiDao {
    
    public void read();
    
    public void read_absen(JTable table);
    
    public void create(absensi abs);

    public void update(absensi abs);

    public void delete(int id);

    public void search(String key);
}

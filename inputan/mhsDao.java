/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inputan;


import java.util.List;
import javax.swing.JTable;

/**
 *
 * @author forsca
 */
public interface mhsDao {
    
    
    
    public void read(JTable table);

    public void create(mhs mahasiswa);

    public void update(mhs mahasiswa);

    public void delete(int id);

    public void search(String key);
}

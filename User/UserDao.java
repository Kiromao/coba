/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package User;

import javax.swing.JTable;

/**
 *
 * @author forsca
 */
public interface UserDao {
    public void read(JTable table);

    public void create(User user);

    public void update(User user);

    public void delete(int id);

    public void search(String key);
}

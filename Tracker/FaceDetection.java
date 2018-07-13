/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tracker;

import Absensi.absensi;
import Absensi.absensiDao;
import Absensi.absensiImp;
import BuildEigenface.Build;
import Image.trainingImageImpl;
import Koneksi.koneksi;
import inputan.mhs;
import inputan.mhsDao;
import inputan.mhsImpl;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import org.opencv.core.Core;

/**
 *
 * @author Arpian Fahmi
 */
public class FaceDetection extends javax.swing.JFrame {

    /**
     * Creates new form FaceDetection
     */
    FaceDetectionController controller;
    private mhsDao dao;
    private absensiDao dao1 = new absensiImp();
    trainingImageImpl ti = new trainingImageImpl();
    koneksi con = new koneksi();
    private int tag;
    private int id;
    private int image_num = 0;

    mhs m = null;

    public FaceDetection() {
        initComponents();
        dao = new mhsImpl();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Build build = new Build(4);
        controller = new FaceDetectionController(btnStart,
                lImage,
                btnSnap,
                btnSave,
                btnRecognize
        );
        reset();

    }

    public void AddTINow() {
        int conf = JOptionPane.NO_OPTION;
        conf = JOptionPane.showConfirmDialog(rootPane, "Tambah Traning Image Sekarang ?",
                "Confirm !", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            int baris = tabelinputan.getRowCount() - 1;
            btnUpdate.setText("Cancel");
            tag = 1;
            id = Integer.valueOf(tabelinputan.getValueAt(baris, 0).toString());
            tfnim.setText(tabelinputan.getValueAt(baris, 1).toString());
            tfnama.setText(tabelinputan.getValueAt(baris, 2).toString());

            ti.read(TIPanel, id);

        } else {
            JOptionPane.showMessageDialog(rootPane, "Tidak ada data yang dipilih !");
        }
    }

    public void reset() {
        tag = 0;
        tfnim.setText("");
        tfnama.setText("");

        btnUpdate.setText("Update");
        read();
        TIPanel.removeAll();
        TIPanel.updateUI();

    }

    //form validation
    public boolean validasi() {
        boolean hasil = false;
        if (tfnim.getText().isEmpty() || tfnama.getText().isEmpty()) {
            //tidak valid
            hasil = false;
        } else {
            //valid
            hasil = true;
        }
        return hasil;
    }

    public void read() {
        dao.read(tabelinputan);
        dao1.read_absen(tabelAbsen);
    }

    public void create() {
        mhs mhs = new mhs();
        mhs.setNim(tfnim.getText());
        mhs.setNama(tfnama.getText());
        dao.create(mhs);
    }

    public void update() {
        mhs mhs = new mhs();
        mhs.setNim(tfnim.getText());
        mhs.setNama(tfnama.getText());
        mhs.setId(id);
        dao.update(mhs);
    }

    public void save() {
        if (validasi() == true) {
            if (tag == 0) {
                //create
                create();
                reset();
                AddTINow();
            } else if (tag == 1) {
                //update
                update();
                image_num += 1;
                controller.SaveImage(tfnim.getText(), image_num, id);
                preview_hasil_snap.setIcon(new ImageIcon());

                reset();
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Inputan belum lengkap !");
        }
    }

    public void getRecord() {
        int selected = tabelinputan.getSelectedRowCount();
        if (selected > 0) {
            int conf = JOptionPane.NO_OPTION;
            conf = JOptionPane.showConfirmDialog(rootPane, "Yakin untuk ubah ?",
                    "Confirm !", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                btnUpdate.setText("Cancel");
                tag = 1;
                int baris = tabelinputan.getSelectedRow();
                id = Integer.valueOf(tabelinputan.getValueAt(baris, 0).toString());
                tfnim.setText(tabelinputan.getValueAt(baris, 1).toString());
                tfnama.setText(tabelinputan.getValueAt(baris, 2).toString());
                ti.read(TIPanel, id);
                image_num = ti.get_image_num();
                System.out.println(image_num);
            }

        } else {
            JOptionPane.showMessageDialog(rootPane, "Tidak ada data yang dipilih !");
        }
    }

    public void delete() {
        int selected = tabelinputan.getSelectedRowCount();
        if (selected > 0) {
            int conf = JOptionPane.NO_OPTION;
            conf = JOptionPane.showConfirmDialog(rootPane, "Yakin untuk hapus ?",
                    "Confirm !", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                int baris = tabelinputan.getSelectedRow();
                id = Integer.valueOf(tabelinputan.getValueAt(baris, 0).toString());
                dao.delete(id);
                reset();
            }

        } else {
            JOptionPane.showMessageDialog(rootPane, "Tidak ada data yang dipilih !");
        }
    }

    public boolean validasiA() {
        boolean hasil = false;
        if (txtNim.getText().isEmpty() || txtNama.getText().isEmpty()
                || cbketerangan.getSelectedItem().toString() == "Pilih . . ") {
            //tidak valid
            hasil = false;
        } else {
            //valid
            hasil = true;
        }
        return hasil;
    }

    public void absen() {
        if (validasiA() == true) {
            if (tag == 0) {
                //create
                createA();
                reset();

            } else if (tag == 1) {
                //update
                updateA();
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Inputan belum lengkap !");
        }
    }

    public void createA() {
        if (this.m != null) {
            absensi abs = new absensi();
            mhs m = this.m;
            String ket = null;
            if (cbketerangan.getSelectedItem().toString() == "Hadir") {
                ket = "H";
            } else if (cbketerangan.getSelectedItem().toString() == "Alpha") {
                ket = "A";
            } else if (cbketerangan.getSelectedItem().toString() == "Sakit") {
                ket = "S";
            } else if (cbketerangan.getSelectedItem().toString() == "Izin") {
                ket = "I";
            }

            abs.setKeterangan(ket);
            abs.setMahasiswa(m);
            dao1.create(abs);
        }

    }

    public void updateA() {
        if (this.m != null) {
            absensi abs = new absensi();
            mhs m = this.m;

            abs.setKeterangan(null);
            abs.setMahasiswa(m);
            dao1.update(abs);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tfnim = new javax.swing.JTextField();
        tfnama = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        preview_hasil_snap = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelinputan = new javax.swing.JTable();
        btnDelete = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        lImage = new javax.swing.JPanel();
        btnStart = new javax.swing.JButton();
        btnBuildE = new javax.swing.JButton();
        btnRecognize = new javax.swing.JButton();
        btnSnap = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        TIPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        resultImage = new javax.swing.JPanel();
        result_image = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtNim = new javax.swing.JTextField();
        txtNama = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabelAbsen = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        btnAbsen = new javax.swing.JButton();
        btnUPdate = new javax.swing.JButton();
        btnDElete = new javax.swing.JButton();
        cbketerangan = new javax.swing.JComboBox();
        btnReport = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        lUser = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 51, 51));

        jLabel1.setText("inputan");

        jLabel2.setText("Nim    :");

        jLabel3.setText("Nama    :");

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.setPreferredSize(new java.awt.Dimension(150, 150));

        preview_hasil_snap.setText(" ");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(preview_hasil_snap, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(preview_hasil_snap, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
        );

        jLabel4.setText("photo   :");

        tabelinputan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tabelinputan);

        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(24, 24, 24))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel3))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel4)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfnim)
                            .addComponent(tfnama)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(12, 12, 12))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tfnim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfnama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel4)))
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(btnUpdate)
                    .addComponent(btnDelete))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        lImage.setBackground(new java.awt.Color(51, 51, 255));

        javax.swing.GroupLayout lImageLayout = new javax.swing.GroupLayout(lImage);
        lImage.setLayout(lImageLayout);
        lImageLayout.setHorizontalGroup(
            lImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        lImageLayout.setVerticalGroup(
            lImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 488, Short.MAX_VALUE)
        );

        btnStart.setText("Start camera");
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });

        btnBuildE.setText("Build Eigenface");
        btnBuildE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuildEActionPerformed(evt);
            }
        });

        btnRecognize.setText("Recognizing Face");
        btnRecognize.setEnabled(false);
        btnRecognize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRecognizeActionPerformed(evt);
            }
        });

        btnSnap.setText("Snap");
        btnSnap.setEnabled(false);
        btnSnap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSnapActionPerformed(evt);
            }
        });

        jScrollPane3.setViewportView(TIPanel);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lImage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnStart)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSnap, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBuildE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                .addComponent(btnRecognize)
                .addContainerGap())
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(lImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBuildE)
                    .addComponent(btnStart)
                    .addComponent(btnSnap)
                    .addComponent(btnRecognize))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3))
        );

        resultImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        result_image.setText(" ");

        javax.swing.GroupLayout resultImageLayout = new javax.swing.GroupLayout(resultImage);
        resultImage.setLayout(resultImageLayout);
        resultImageLayout.setHorizontalGroup(
            resultImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(result_image, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
        );
        resultImageLayout.setVerticalGroup(
            resultImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(result_image, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
        );

        jLabel5.setText("Nim    :");

        jLabel6.setText("Nama  :");

        jLabel7.setText("Keterangan");

        tabelAbsen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tabelAbsen);

        jLabel8.setText("Absensi");

        btnAbsen.setText("Absen");
        btnAbsen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbsenActionPerformed(evt);
            }
        });

        btnUPdate.setText("Update");
        btnUPdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUPdateActionPerformed(evt);
            }
        });

        btnDElete.setText("Delete");

        cbketerangan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Pilih . . .", "Hadir", "Alpha", "Sakit", "Izin" }));

        btnReport.setText("Report");
        btnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportActionPerformed(evt);
            }
        });

        lUser.setText("################");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(89, 89, 89)
                .addComponent(lUser, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(lUser, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel5))
                                .addGap(30, 30, 30))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel7)
                                .addGap(18, 18, 18)))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNim, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(cbketerangan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(87, 87, 87))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(133, 133, 133)
                        .addComponent(jLabel8))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(btnAbsen)
                        .addGap(18, 18, 18)
                        .addComponent(btnUPdate)
                        .addGap(19, 19, 19)
                        .addComponent(btnDElete)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(resultImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(cbketerangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnAbsen)
                            .addComponent(btnUPdate)
                            .addComponent(btnDElete)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(resultImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnReport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        // TODO add your handling code here:
        controller.startCamera();
    }//GEN-LAST:event_btnStartActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        controller.setClosed();
    }//GEN-LAST:event_formWindowClosing

    private void btnSnapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSnapActionPerformed
        // TODO add your handling code here:
        controller.SnapImage(preview_hasil_snap);
    }//GEN-LAST:event_btnSnapActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        save(); //CRUD Mahasiswa

    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnBuildEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuildEActionPerformed
        // TODO add your handling code here:
        Build build = new Build(4);
    }//GEN-LAST:event_btnBuildEActionPerformed

    private void btnRecognizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRecognizeActionPerformed
        // TODO add your handling code here:
        m = controller.RecognizeImage();
        txtNim.setText(m.getNim());
        txtNama.setText(m.getNama());
        BufferedImage prosesGambar = null;
        try {
            prosesGambar = ImageIO.read(new File(m.getImagePath()));
        } catch (IOException ex) {
            Logger.getLogger(FaceDetection.class.getName()).log(Level.SEVERE, null, ex);
        }
        result_image.setIcon(new ImageIcon(prosesGambar));
    }//GEN-LAST:event_btnRecognizeActionPerformed

    private void btnUPdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUPdateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnUPdateActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        if (btnUpdate.getText().equals("Update")) {
            getRecord();
        } else {
            reset();
        }
        preview_hasil_snap.setIcon(new ImageIcon());
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        delete();
        preview_hasil_snap.setIcon(new ImageIcon());

    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnAbsenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbsenActionPerformed
        absen();
    }//GEN-LAST:event_btnAbsenActionPerformed

    private void btnReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportActionPerformed
        String Repost = "src/report/absenR.jrxml";

        JasperReport jr;
        try {
            jr = JasperCompileManager.compileReport(Repost);

            JasperPrint jp = JasperFillManager.fillReport(jr, null, con.getCon());

            JasperViewer.viewReport(jp, false);

        } catch (JRException ex) {
            Logger.getLogger(FaceDetection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnReportActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FaceDetection.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FaceDetection.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FaceDetection.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FaceDetection.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FaceDetection().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel TIPanel;
    private javax.swing.JButton btnAbsen;
    private javax.swing.JButton btnBuildE;
    private javax.swing.JButton btnDElete;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnRecognize;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSnap;
    private javax.swing.JButton btnStart;
    private javax.swing.JButton btnUPdate;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox cbketerangan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel lImage;
    public javax.swing.JLabel lUser;
    private javax.swing.JLabel preview_hasil_snap;
    private javax.swing.JPanel resultImage;
    private javax.swing.JLabel result_image;
    private javax.swing.JTable tabelAbsen;
    private javax.swing.JTable tabelinputan;
    private javax.swing.JTextField tfnama;
    private javax.swing.JTextField tfnim;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtNim;
    // End of variables declaration//GEN-END:variables
}

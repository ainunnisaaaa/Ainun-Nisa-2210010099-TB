/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import config.DatabaseConnection;
import util.PDFGenerator;

import javax.swing.JOptionPane;
import java.text.SimpleDateFormat;

public class MainMenu extends javax.swing.JFrame {
    private Connection conn;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private boolean isEditing = false;

    /**
     * Creates new form MainMenu
     */
    public MainMenu() {
        initComponents();
        conn = DatabaseConnection.getConnection();
        setupComboBoxes();
        refreshTables();
    }

    private void setupComboBoxes() {
        // Setup Status Absensi ComboBox
        cbKategori2.removeAllItems();
        cbKategori2.addItem("Hadir");
        cbKategori2.addItem("Sakit");
        cbKategori2.addItem("Izin");
        cbKategori2.addItem("Alpa");

        // Setup Jabatan ComboBox
        refreshJabatanComboBox();

        // Setup Karyawan ComboBox for Absensi
        refreshKaryawanComboBox();
    }

    private void refreshJabatanComboBox() {
        cbKategori.removeAllItems();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT NamaJabatan FROM Jabatan");
            while(rs.next()) {
                cbKategori.addItem(rs.getString("NamaJabatan"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading jabatan: " + e.getMessage());
        }
    }

    private void refreshKaryawanComboBox() {
        cbKategori1.removeAllItems();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Nama FROM Karyawan");
            while(rs.next()) {
                cbKategori1.addItem(rs.getString("Nama"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading karyawan: " + e.getMessage());
        }
    }

    private void refreshTables() {
        refreshKaryawanTable();
        refreshJabatanTable();
        refreshAbsensiTable();
    }

    private void refreshKaryawanTable() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT k.KaryawanID, k.Nama, k.Alamat, k.TanggalLahir, j.NamaJabatan " +
                "FROM Karyawan k JOIN Jabatan j ON k.JabatanID = j.JabatanID"
            );

            DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Nama", "Alamat", "Tanggal Lahir", "Jabatan"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("Nama"),
                    rs.getString("Alamat"),
                    rs.getDate("TanggalLahir"),
                    rs.getString("NamaJabatan")
                });
            }

            jTable1.setModel(model);
        } catch (SQLException e) {
            System.err.println("Error refreshing karyawan table: " + e.getMessage());
        }
    }

    private void refreshJabatanTable() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Jabatan");

            DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Nama Jabatan", "Gaji Pokok"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("NamaJabatan"),
                    rs.getDouble("GajiPokok")
                });
            }

            jTable3.setModel(model);
        } catch (SQLException e) {
            System.err.println("Error refreshing jabatan table: " + e.getMessage());
        }
    }

    private void refreshAbsensiTable() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT a.AbsensiID, k.Nama, a.Tanggal, a.Status " +
                "FROM Absensi a JOIN Karyawan k ON a.KaryawanID = k.KaryawanID"
            );

            DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Nama Karyawan", "Tanggal", "Status"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("Nama"),
                    rs.getDate("Tanggal"),
                    rs.getString("Status")
                });
            }

            jTable2.setModel(model);
        } catch (SQLException e) {
            System.err.println("Error refreshing absensi table: " + e.getMessage());
        }
    }

    private void validateInput() throws Exception {
        if (txtNama.getText().trim().isEmpty()) {
            throw new Exception("Nama tidak boleh kosong");
        }
        if (jDateChooser1.getDate() == null) {
            throw new Exception("Tanggal lahir harus diisi");
        }
        if (jTextArea1.getText().trim().isEmpty()) {
            throw new Exception("Alamat tidak boleh kosong");
        }
        if (cbKategori.getSelectedIndex() == -1) {
            throw new Exception("Jabatan harus dipilih");
        }
    }
    
    private void validateJabatan() throws Exception {
        if (txtNama3.getText().trim().isEmpty()) {
            throw new Exception("Nama jabatan tidak boleh kosong");
        }
        try {
            double gajiPokok = Double.parseDouble(txtNama2.getText().trim());
            if (gajiPokok <= 0) {
                throw new Exception("Gaji pokok harus lebih dari 0");
            }
        } catch (NumberFormatException e) {
            throw new Exception("Gaji pokok harus berupa angka");
        }
    }
    
    private void validateAbsensi() throws Exception {
        if (cbKategori1.getSelectedIndex() == -1) {
            throw new Exception("Karyawan harus dipilih");
        }
        if (jDateChooser2.getDate() == null) {
            throw new Exception("Tanggal absensi harus diisi");
        }
        if (cbKategori2.getSelectedIndex() == -1) {
            throw new Exception("Status harus dipilih");
        }
    }
    
    private void toggleEditMode(boolean editing) {
        isEditing = editing;
        
        // Karyawan buttons
        btnTambah.setText(editing ? "Simpan" : "Tambahkan");
        btnUbah.setEnabled(!editing);
        btnHapus.setEnabled(!editing);
        
        // Jabatan buttons
        btnTambah2.setText(editing ? "Simpan" : "Tambahkan");
        btnUbah2.setEnabled(!editing);
        btnHapus2.setEnabled(!editing);
        
        // Absensi buttons
        btnTambah3.setText(editing ? "Simpan" : "Tambahkan");
        btnUbah1.setEnabled(!editing);
        btnHapus1.setEnabled(!editing);
    }

    private void searchKaryawan(String keyword) {
        try {
            String sql = "SELECT k.Nama, k.Alamat, k.TanggalLahir, j.NamaJabatan " +
                        "FROM Karyawan k JOIN Jabatan j ON k.JabatanID = j.JabatanID " +
                        "WHERE k.Nama LIKE ? OR k.Alamat LIKE ? OR j.NamaJabatan LIKE ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);
            
            boolean found = false;
            while(rs.next()) {
                found = true;
                model.addRow(new Object[]{
                    rs.getString("Nama"),
                    rs.getString("Alamat"),
                    rs.getDate("TanggalLahir"),
                    rs.getString("NamaJabatan")
                });
            }
            
            if (!found) {
                JOptionPane.showMessageDialog(this, 
                    "Data tidak ditemukan\nKata kunci pencarian dapat berupa:\n- Nama Karyawan\n- Alamat\n- Jabatan", 
                    "Informasi", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshKaryawanTable();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error pencarian: " + e.getMessage());
            refreshKaryawanTable();
        }
    }
    
    private void searchJabatan(String keyword) {
        try {
            String sql = "SELECT NamaJabatan, GajiPokok FROM Jabatan WHERE NamaJabatan LIKE ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%");
            
            ResultSet rs = pstmt.executeQuery();
            DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
            model.setRowCount(0);
            
            boolean found = false;
            while(rs.next()) {
                found = true;
                model.addRow(new Object[]{
                    rs.getString("NamaJabatan"),
                    rs.getDouble("GajiPokok")
                });
            }
            
            if (!found) {
                JOptionPane.showMessageDialog(this, 
                    "Data tidak ditemukan\nKata kunci pencarian dapat berupa:\n- Nama Jabatan", 
                    "Informasi", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshJabatanTable();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error pencarian: " + e.getMessage());
            refreshJabatanTable();
        }
    }
    
    private void searchAbsensi(String keyword) {
        try {
            String sql = "SELECT k.Nama, a.Tanggal, a.Status " +
                        "FROM Absensi a JOIN Karyawan k ON a.KaryawanID = k.KaryawanID " +
                        "WHERE k.Nama LIKE ? OR a.Status LIKE ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
            model.setRowCount(0);
            
            boolean found = false;
            while(rs.next()) {
                found = true;
                model.addRow(new Object[]{
                    rs.getString("Nama"),
                    rs.getDate("Tanggal"),
                    rs.getString("Status")
                });
            }
            
            if (!found) {
                JOptionPane.showMessageDialog(this, 
                    "Data tidak ditemukan\nKata kunci pencarian dapat berupa:\n- Nama Karyawan\n- Status (Hadir/Sakit/Izin/Alpa)", 
                    "Informasi", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshAbsensiTable();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error pencarian: " + e.getMessage());
            refreshAbsensiTable();
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
        java.awt.GridBagConstraints gridBagConstraints;

        jTabbedPane1 = new javax.swing.JTabbedPane();
        karyawan = new javax.swing.JPanel();
        inputKaryawan = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        cbKategori = new javax.swing.JComboBox<>();
        btnTambah = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        tabelKaryawan = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        btnCari2 = new javax.swing.JButton();
        btnCetak = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jabatan = new javax.swing.JPanel();
        inputJabatan = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtNama2 = new javax.swing.JTextField();
        btnTambah2 = new javax.swing.JButton();
        btnUbah2 = new javax.swing.JButton();
        btnHapus2 = new javax.swing.JButton();
        txtNama3 = new javax.swing.JTextField();
        tabelJabatan = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();
        btnCetak2 = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        absensi = new javax.swing.JPanel();
        inputAbsensi = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        cbKategori1 = new javax.swing.JComboBox<>();
        btnSekarang = new javax.swing.JButton();
        btnUbah1 = new javax.swing.JButton();
        btnHapus1 = new javax.swing.JButton();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        btnTambah3 = new javax.swing.JButton();
        cbKategori2 = new javax.swing.JComboBox<>();
        tabelAbsensi = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        btnCari1 = new javax.swing.JButton();
        btnCetak1 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridLayout(1, 0));

        jTabbedPane1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTabbedPane1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        karyawan.setLayout(new java.awt.GridLayout(0, 1));

        inputKaryawan.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        java.awt.GridBagLayout inputKaryawanLayout = new java.awt.GridBagLayout();
        inputKaryawanLayout.columnWidths = new int[] {0, 15, 0, 15, 0, 15, 0, 15, 0, 15, 0, 15, 0, 15, 0, 15, 0};
        inputKaryawanLayout.rowHeights = new int[] {0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0};
        inputKaryawan.setLayout(inputKaryawanLayout);

        jLabel2.setText("Nama");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        inputKaryawan.add(jLabel2, gridBagConstraints);

        jLabel3.setText("Tanggal Lahir");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        inputKaryawan.add(jLabel3, gridBagConstraints);

        jLabel4.setText("Jabatan");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        inputKaryawan.add(jLabel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        inputKaryawan.add(txtNama, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        inputKaryawan.add(cbKategori, gridBagConstraints);

        btnTambah.setText("Tambahkan");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        inputKaryawan.add(btnTambah, gridBagConstraints);

        btnUbah.setText("Ubah");
        btnUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbahActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        inputKaryawan.add(btnUbah, gridBagConstraints);

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        inputKaryawan.add(btnHapus, gridBagConstraints);

        jLabel6.setText("Alamat");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        inputKaryawan.add(jLabel6, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 60);
        inputKaryawan.add(jDateChooser1, gridBagConstraints);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 13;
        inputKaryawan.add(jScrollPane2, gridBagConstraints);

        karyawan.add(inputKaryawan);

        tabelKaryawan.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        tabelKaryawan.setLayout(new java.awt.GridBagLayout());

        jLabel5.setText("Cari Karyawan");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        tabelKaryawan.add(jLabel5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        tabelKaryawan.add(jTextField3, gridBagConstraints);

        btnCari2.setText("Cari");
        btnCari2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCari2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        tabelKaryawan.add(btnCari2, gridBagConstraints);

        btnCetak.setText("Cetak");
        btnCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        tabelKaryawan.add(btnCetak, gridBagConstraints);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(200, 300));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable1.setPreferredSize(new java.awt.Dimension(60, 80));
        jTable1.setShowGrid(true);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        tabelKaryawan.add(jScrollPane1, gridBagConstraints);

        karyawan.add(tabelKaryawan);

        jTabbedPane1.addTab("Karyawan", karyawan);

        inputJabatan.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        java.awt.GridBagLayout inputJabatanLayout1 = new java.awt.GridBagLayout();
        inputJabatanLayout1.columnWidths = new int[] {0, 15, 0, 15, 0, 15, 0, 15, 0};
        inputJabatanLayout1.rowHeights = new int[] {0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0};
        inputJabatan.setLayout(inputJabatanLayout1);

        jLabel12.setText("Nama Jabatan");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        inputJabatan.add(jLabel12, gridBagConstraints);

        jLabel13.setText("Gaji Pokok");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        inputJabatan.add(jLabel13, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        inputJabatan.add(txtNama2, gridBagConstraints);

        btnTambah2.setText("Tambahkan");
        btnTambah2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambah2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        inputJabatan.add(btnTambah2, gridBagConstraints);

        btnUbah2.setText("Ubah");
        btnUbah2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbah2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 80);
        inputJabatan.add(btnUbah2, gridBagConstraints);

        btnHapus2.setText("Hapus");
        btnHapus2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapus2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        inputJabatan.add(btnHapus2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        inputJabatan.add(txtNama3, gridBagConstraints);

        tabelJabatan.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        tabelJabatan.setLayout(new java.awt.GridBagLayout());

        jLabel16.setText("Cari Jabatan");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        tabelJabatan.add(jLabel16, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        tabelJabatan.add(jTextField5, gridBagConstraints);

        btnCari.setText("Cari");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        tabelJabatan.add(btnCari, gridBagConstraints);

        btnCetak2.setText("Cetak");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        tabelJabatan.add(btnCetak2, gridBagConstraints);

        jScrollPane6.setPreferredSize(new java.awt.Dimension(200, 300));

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ));
        jTable3.setPreferredSize(new java.awt.Dimension(60, 80));
        jTable3.setShowGrid(true);
        jTable3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable3MouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(jTable3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        tabelJabatan.add(jScrollPane6, gridBagConstraints);

        javax.swing.GroupLayout jabatanLayout = new javax.swing.GroupLayout(jabatan);
        jabatan.setLayout(jabatanLayout);
        jabatanLayout.setHorizontalGroup(
            jabatanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(inputJabatan, javax.swing.GroupLayout.PREFERRED_SIZE, 832, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(tabelJabatan, javax.swing.GroupLayout.PREFERRED_SIZE, 832, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jabatanLayout.setVerticalGroup(
            jabatanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jabatanLayout.createSequentialGroup()
                .addComponent(inputJabatan, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(tabelJabatan, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("Jabatan", jabatan);

        absensi.setLayout(new java.awt.GridLayout(0, 1));

        inputAbsensi.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        java.awt.GridBagLayout inputAbsensiLayout = new java.awt.GridBagLayout();
        inputAbsensiLayout.columnWidths = new int[] {0, 15, 0, 15, 0, 15, 0, 15, 0, 15, 0, 15, 0, 15, 0, 15, 0, 15, 0, 15, 0};
        inputAbsensiLayout.rowHeights = new int[] {0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0};
        inputAbsensi.setLayout(inputAbsensiLayout);

        jLabel7.setText("Karyawan");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        inputAbsensi.add(jLabel7, gridBagConstraints);

        jLabel8.setText("Tanggal Absensi");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        inputAbsensi.add(jLabel8, gridBagConstraints);

        jLabel9.setText("Status");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        inputAbsensi.add(jLabel9, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        inputAbsensi.add(cbKategori1, gridBagConstraints);

        btnSekarang.setText("Sekarang");
        btnSekarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSekarangActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 16;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        inputAbsensi.add(btnSekarang, gridBagConstraints);

        btnUbah1.setText("Ubah");
        btnUbah1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbah1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        inputAbsensi.add(btnUbah1, gridBagConstraints);

        btnHapus1.setText("Hapus");
        btnHapus1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapus1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        inputAbsensi.add(btnHapus1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        inputAbsensi.add(jDateChooser2, gridBagConstraints);

        btnTambah3.setText("Tambahkan");
        btnTambah3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambah3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        inputAbsensi.add(btnTambah3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        inputAbsensi.add(cbKategori2, gridBagConstraints);

        absensi.add(inputAbsensi);

        tabelAbsensi.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        tabelAbsensi.setLayout(new java.awt.GridBagLayout());

        jLabel11.setText("Cari Riwayat Absen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        tabelAbsensi.add(jLabel11, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        tabelAbsensi.add(jTextField4, gridBagConstraints);

        btnCari1.setText("Cari");
        btnCari1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCari1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        tabelAbsensi.add(btnCari1, gridBagConstraints);

        btnCetak1.setText("Cetak");
        btnCetak1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetak1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        tabelAbsensi.add(btnCetak1, gridBagConstraints);

        jScrollPane4.setPreferredSize(new java.awt.Dimension(200, 300));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ));
        jTable2.setPreferredSize(new java.awt.Dimension(60, 80));
        jTable2.setShowGrid(true);
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(jTable2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        tabelAbsensi.add(jScrollPane4, gridBagConstraints);

        absensi.add(tabelAbsensi);

        jTabbedPane1.addTab("Absensi", absensi);

        getContentPane().add(jTabbedPane1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        try {
            validateInput();
            
            String sql = "INSERT INTO Karyawan (Nama, Alamat, TanggalLahir, JabatanID) VALUES (?, ?, ?, (SELECT JabatanID FROM Jabatan WHERE NamaJabatan = ?))";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, txtNama.getText().trim());
            pstmt.setString(2, jTextArea1.getText().trim());
            pstmt.setDate(3, new java.sql.Date(jDateChooser1.getDate().getTime()));
            pstmt.setString(4, cbKategori.getSelectedItem().toString());
            
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data karyawan berhasil ditambahkan");
            refreshTables();
            refreshKaryawanComboBox();
            clearKaryawanFields();
            toggleEditMode(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Validasi", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (jTable1.getSelectedRow() == -1) {
                throw new Exception("Pilih data yang akan diubah");
            }
            validateInput();
            
            String sql = "UPDATE Karyawan SET Nama=?, Alamat=?, TanggalLahir=?, JabatanID=(SELECT JabatanID FROM Jabatan WHERE NamaJabatan=?) WHERE Nama=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, txtNama.getText().trim());
            pstmt.setString(2, jTextArea1.getText().trim());
            pstmt.setDate(3, new java.sql.Date(jDateChooser1.getDate().getTime()));
            pstmt.setString(4, cbKategori.getSelectedItem().toString());
            pstmt.setString(5, jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString());
            
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data karyawan berhasil diubah");
            refreshTables();
            refreshKaryawanComboBox();
            clearKaryawanFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Validasi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (jTable1.getSelectedRow() == -1) {
                throw new Exception("Pilih data yang akan dihapus");
            }
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus data ini?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                String sql = "DELETE FROM Karyawan WHERE Nama=?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString());
                
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data karyawan berhasil dihapus");
                refreshTables();
                refreshKaryawanComboBox();
                clearKaryawanFields();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnCari2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCari2ActionPerformed
        String keyword = jTextField3.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Masukkan kata kunci pencarian\nAnda dapat mencari berdasarkan:\n- Nama Karyawan\n- Alamat\n- Jabatan",
                "Informasi", 
                JOptionPane.INFORMATION_MESSAGE);
            refreshKaryawanTable();
            return;
        }
        searchKaryawan(keyword);
    }//GEN-LAST:event_btnCari2ActionPerformed

    private void btnCetakActionPerformed(java.awt.event.ActionEvent evt) {
        if (jTable1.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, 
                "Tidak ada data untuk dicetak",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin mencetak laporan data karyawan?",
            "Konfirmasi Cetak",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String path = System.getProperty("user.home") + "/Desktop/Laporan_Karyawan.pdf";
                PDFGenerator.generateKaryawanReport(jTable1, path);
                
                int openFile = JOptionPane.showConfirmDialog(this,
                    "Laporan berhasil dibuat!\nFile tersimpan di: " + path + "\n\nApakah Anda ingin membuka file tersebut?",
                    "Sukses",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
                    
                if (openFile == JOptionPane.YES_OPTION) {
                    java.awt.Desktop.getDesktop().open(new java.io.File(path));
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error membuat laporan: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void btnCetak2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCetak2ActionPerformed
        try {
            String path = System.getProperty("user.home") + "/Desktop/Laporan_Jabatan.pdf";
            PDFGenerator.generateJabatanReport(jTable3, path);
            JOptionPane.showMessageDialog(this, 
                "Laporan berhasil dibuat!\nFile tersimpan di: " + path,
                "Sukses",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error membuat laporan: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnCetak2ActionPerformed


    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {
        try {
            int row = jTable1.getSelectedRow();
            if(row >= 0) {
                txtNama.setText(jTable1.getValueAt(row, 0).toString());
                jTextArea1.setText(jTable1.getValueAt(row, 1).toString());
                
                // Handle date conversion
                Object dateValue = jTable1.getValueAt(row, 2);
                if (dateValue instanceof java.util.Date) {
                    jDateChooser1.setDate((java.util.Date) dateValue);
                } else if (dateValue instanceof java.sql.Date) {
                    jDateChooser1.setDate(new java.util.Date(((java.sql.Date) dateValue).getTime()));
                } else if (dateValue instanceof String) {
                    try {
                        java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue.toString());
                        jDateChooser1.setDate(date);
                    } catch (Exception e) {
                        System.err.println("Error parsing date: " + e.getMessage());
                    }
                }
                
                cbKategori.setSelectedItem(jTable1.getValueAt(row, 3).toString());
            }
        } catch (Exception e) {
            System.err.println("Error selecting row: " + e.getMessage());
        }
    }

    private void btnTambah2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambah2ActionPerformed
        try {
            validateJabatan();
            
            String sql = "INSERT INTO Jabatan (NamaJabatan, GajiPokok) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, txtNama3.getText().trim());
            pstmt.setDouble(2, Double.parseDouble(txtNama2.getText().trim()));
            
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data jabatan berhasil ditambahkan");
            refreshTables();
            refreshJabatanComboBox();
            clearJabatanFields();
            toggleEditMode(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Validasi", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnTambah2ActionPerformed

    private void btnUbah2ActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (jTable3.getSelectedRow() == -1) {
                throw new Exception("Pilih data yang akan diubah");
            }
            validateJabatan();
            
            String sql = "UPDATE Jabatan SET NamaJabatan=?, GajiPokok=? WHERE NamaJabatan=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, txtNama3.getText().trim());
            pstmt.setDouble(2, Double.parseDouble(txtNama2.getText().trim()));
            pstmt.setString(3, jTable3.getValueAt(jTable3.getSelectedRow(), 0).toString());
            
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data jabatan berhasil diubah");
            refreshTables();
            refreshJabatanComboBox();
            clearJabatanFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Validasi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnHapus2ActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (jTable3.getSelectedRow() == -1) {
                throw new Exception("Pilih data yang akan dihapus");
            }
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus data ini?\nMenghapus jabatan akan menghapus data karyawan terkait!",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                String sql = "DELETE FROM Jabatan WHERE NamaJabatan=?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, jTable3.getValueAt(jTable3.getSelectedRow(), 0).toString());
                
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data jabatan berhasil dihapus");
                refreshTables();
                refreshJabatanComboBox();
                clearJabatanFields();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jTable3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MouseClicked
        int row = jTable3.getSelectedRow();
        if(row >= 0) {
            txtNama3.setText(jTable3.getValueAt(row, 0).toString());
            txtNama2.setText(jTable3.getValueAt(row, 1).toString());
        }
    }//GEN-LAST:event_jTable3MouseClicked

    private void btnSekarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSekarangActionPerformed
        jDateChooser2.setDate(new java.util.Date());
    }//GEN-LAST:event_btnSekarangActionPerformed

    private void btnUbah1ActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (jTable2.getSelectedRow() == -1) {
                throw new Exception("Pilih data yang akan diubah");
            }
            validateAbsensi();
            
            String sql = "UPDATE Absensi SET KaryawanID=(SELECT KaryawanID FROM Karyawan WHERE Nama=?), Tanggal=?, Status=? " +
                        "WHERE KaryawanID=(SELECT KaryawanID FROM Karyawan WHERE Nama=?) AND Tanggal=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cbKategori1.getSelectedItem().toString());
            pstmt.setDate(2, new java.sql.Date(jDateChooser2.getDate().getTime()));
            pstmt.setString(3, cbKategori2.getSelectedItem().toString());
            pstmt.setString(4, jTable2.getValueAt(jTable2.getSelectedRow(), 0).toString());
            pstmt.setDate(5, new java.sql.Date(((java.util.Date)jTable2.getValueAt(jTable2.getSelectedRow(), 1)).getTime()));
            
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data absensi berhasil diubah");
            refreshTables();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Validasi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnHapus1ActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (jTable2.getSelectedRow() == -1) {
                throw new Exception("Pilih data yang akan dihapus");
            }
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus data ini?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                String sql = "DELETE FROM Absensi WHERE KaryawanID=(SELECT KaryawanID FROM Karyawan WHERE Nama=?) AND Tanggal=?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, jTable2.getValueAt(jTable2.getSelectedRow(), 0).toString());
                pstmt.setDate(2, new java.sql.Date(((java.util.Date)jTable2.getValueAt(jTable2.getSelectedRow(), 1)).getTime()));
                
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data absensi berhasil dihapus");
                refreshTables();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnTambah3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambah3ActionPerformed
        try {
            validateAbsensi();
            
            String sql = "INSERT INTO Absensi (KaryawanID, Tanggal, Status) VALUES ((SELECT KaryawanID FROM Karyawan WHERE Nama = ?), ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cbKategori1.getSelectedItem().toString());
            pstmt.setDate(2, new java.sql.Date(jDateChooser2.getDate().getTime()));
            pstmt.setString(3, cbKategori2.getSelectedItem().toString());
            
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data absensi berhasil ditambahkan");
            refreshTables();
            toggleEditMode(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Validasi", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnTambah3ActionPerformed

    private void btnCari1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCari1ActionPerformed
        String keyword = jTextField4.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Masukkan kata kunci pencarian\nAnda dapat mencari berdasarkan:\n- Nama Karyawan\n- Status (Hadir/Sakit/Izin/Alpa)",
                "Informasi", 
                JOptionPane.INFORMATION_MESSAGE);
            refreshAbsensiTable();
            return;
        }
        searchAbsensi(keyword);
    }//GEN-LAST:event_btnCari1ActionPerformed

    private void btnCetak1ActionPerformed(java.awt.event.ActionEvent evt) {
        if (jTable2.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, 
                "Tidak ada data untuk dicetak",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin mencetak laporan data absensi?",
            "Konfirmasi Cetak",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String path = System.getProperty("user.home") + "/Desktop/Laporan_Absensi.pdf";
                PDFGenerator.generateAbsensiReport(jTable2, path);
                
                int openFile = JOptionPane.showConfirmDialog(this,
                    "Laporan berhasil dibuat!\nFile tersimpan di: " + path + "\n\nApakah Anda ingin membuka file tersebut?",
                    "Sukses",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
                    
                if (openFile == JOptionPane.YES_OPTION) {
                    java.awt.Desktop.getDesktop().open(new java.io.File(path));
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error membuat laporan: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {
        try {
            int row = jTable2.getSelectedRow();
            if(row >= 0) {
                cbKategori1.setSelectedItem(jTable2.getValueAt(row, 0).toString());
                
                // Handle date conversion
                Object dateValue = jTable2.getValueAt(row, 1);
                if (dateValue instanceof java.util.Date) {
                    jDateChooser2.setDate((java.util.Date) dateValue);
                } else if (dateValue instanceof java.sql.Date) {
                    jDateChooser2.setDate(new java.util.Date(((java.sql.Date) dateValue).getTime()));
                } else if (dateValue instanceof String) {
                    try {
                        java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue.toString());
                        jDateChooser2.setDate(date);
                    } catch (Exception e) {
                        System.err.println("Error parsing date: " + e.getMessage());
                    }
                }
                
                cbKategori2.setSelectedItem(jTable2.getValueAt(row, 2).toString());
            }
        } catch (Exception e) {
            System.err.println("Error selecting row: " + e.getMessage());
        }
    }//GEN-LAST:event_jTable2MouseClicked

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
        String keyword = jTextField5.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Masukkan kata kunci pencarian\nAnda dapat mencari berdasarkan:\n- Nama Jabatan",
                "Informasi", 
                JOptionPane.INFORMATION_MESSAGE);
            refreshJabatanTable();
            return;
        }
        searchJabatan(keyword);
    }//GEN-LAST:event_btnCariActionPerformed

    private void clearKaryawanFields() {
        txtNama.setText("");
        jTextArea1.setText("");
        jDateChooser1.setDate(null);
        cbKategori.setSelectedIndex(0);
    }

    private void clearJabatanFields() {
        txtNama3.setText("");
        txtNama2.setText("");
    }

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
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainMenu().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel absensi;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnCari1;
    private javax.swing.JButton btnCari2;
    private javax.swing.JButton btnCetak;
    private javax.swing.JButton btnCetak1;
    private javax.swing.JButton btnCetak2;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnHapus1;
    private javax.swing.JButton btnHapus2;
    private javax.swing.JButton btnSekarang;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton btnTambah2;
    private javax.swing.JButton btnTambah3;
    private javax.swing.JButton btnUbah;
    private javax.swing.JButton btnUbah1;
    private javax.swing.JButton btnUbah2;
    private javax.swing.JComboBox<String> cbKategori;
    private javax.swing.JComboBox<String> cbKategori1;
    private javax.swing.JComboBox<String> cbKategori2;
    private javax.swing.JPanel inputAbsensi;
    private javax.swing.JPanel inputJabatan;
    private javax.swing.JPanel inputKaryawan;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JPanel jabatan;
    private javax.swing.JPanel karyawan;
    private javax.swing.JPanel tabelAbsensi;
    private javax.swing.JPanel tabelJabatan;
    private javax.swing.JPanel tabelKaryawan;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtNama2;
    private javax.swing.JTextField txtNama3;
    // End of variables declaration//GEN-END:variables
}

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Menu extends JFrame {
    private Database database;
    private int selectedIndex = -1;
    private ArrayList<Mahasiswa> listMahasiswa;

    private JPanel mainPanel;
    private JTextField nimField;
    private JTextField namaField;
    private JTable mahasiswaTable;
    private JButton addUpdateButton;
    private JButton cancelButton;
    private JComboBox jenisKelaminComboBox;
    private JButton deleteButton;
    private JLabel titleLabel;
    private JLabel nimLabel;
    private JLabel namaLabel;
    private JLabel jenisKelaminLabel;
    private JRadioButton bekerjaRadioButton;
    private JRadioButton tidakBekerjaRadioButton;
    private DefaultTableModel tableModel;
    private ButtonGroup buttonGroup = new ButtonGroup();

    public static void main(String[] args) {
        Menu window = new Menu();
        window.setSize(480, 560);
        window.setLocationRelativeTo(null);
        window.setContentPane(window.mainPanel);
        window.getContentPane().setBackground(Color.white);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public Menu() {
        database = new Database();
        buttonGroup.add(bekerjaRadioButton);
        buttonGroup.add(tidakBekerjaRadioButton);
        createTableModel();
        fetchDataFromDatabase();
        mahasiswaTable.setModel(tableModel);
        jenisKelaminComboBox.addItem("Laki-laki");
        jenisKelaminComboBox.addItem("Perempuan");
        addUpdateButton.addActionListener(e -> {
            if (nimField.getText().isEmpty() || namaField.getText().isEmpty() || jenisKelaminComboBox.getSelectedItem().toString().isEmpty() || (!bekerjaRadioButton.isSelected() && !tidakBekerjaRadioButton.isSelected())) {
                JOptionPane.showMessageDialog(null, "Mohon lengkapi data!");
            } else {
                if (selectedIndex == -1) {
                    insertData();
                } else {
                    updateData();
                }
            }
        });

        deleteButton.addActionListener(e -> {
            if (selectedIndex >= 0) {
                deleteData();
            }
        });

        cancelButton.addActionListener(e -> clearForm());

        mahasiswaTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectedIndex = mahasiswaTable.getSelectedRow();
                if (selectedIndex >= 0) {
                    String nim = tableModel.getValueAt(selectedIndex, 1).toString();
                    String nama = tableModel.getValueAt(selectedIndex, 2).toString();
                    String jenisKelamin = tableModel.getValueAt(selectedIndex, 3).toString();
                    boolean bekerja = Boolean.parseBoolean(tableModel.getValueAt(selectedIndex, 4).toString());

                    nimField.setText(nim);
                    namaField.setText(nama);
                    jenisKelaminComboBox.setSelectedItem(jenisKelamin);
                    bekerjaRadioButton.setSelected(bekerja);
                    tidakBekerjaRadioButton.setSelected(!bekerja);

                    addUpdateButton.setText("Update");
                    deleteButton.setVisible(true);
                }
            }
        });
    }

    private void createTableModel() {
        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("NIM");
        tableModel.addColumn("Nama");
        tableModel.addColumn("Jenis Kelamin");
        tableModel.addColumn("Status Bekerja");
    }

    private void fetchDataFromDatabase() {
        try {
            ResultSet resultSet = database.selectQuery("SELECT * FROM mahasiswa");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nim = resultSet.getString("nim");
                String nama = resultSet.getString("nama");
                String jenisKelamin = resultSet.getString("jenis_kelamin");
                Boolean bekerja = resultSet.getBoolean("status_pekerjaan");

                Object[] rowData = {id, nim, nama, jenisKelamin, bekerja};
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void insertData() {
        String nim = nimField.getText();
        String nama = namaField.getText();
        String jenisKelamin = (String) jenisKelaminComboBox.getSelectedItem();
        boolean bekerja = bekerjaRadioButton.isSelected();

        // Cek apakah NIM sudah ada
        if (isNIMExists(nim)) {
            JOptionPane.showMessageDialog(null, "NIM sudah ada, tidak dapat menambahkan data!");
        } else {
            String sql = "INSERT INTO mahasiswa (nim, nama, jenis_kelamin, status_pekerjaan) VALUES ('" + nim + "', '" + nama + "', '" + jenisKelamin + "', " + (bekerja ? 1 : 0) + ")";
            database.insertUpdateDeleteQuery(sql);

            Object[] rowData = {database.getLastInsertedId(), nim, nama, jenisKelamin, bekerja};
            tableModel.addRow(rowData);
            clearForm();

            JOptionPane.showMessageDialog(null, "Data Berhasil Ditambahkan!");
        }
    }

    private boolean isNIMExists(String nim) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String existingNIM = (String) tableModel.getValueAt(i, 1);
            if (existingNIM.equals(nim)) {
                return true;
            }
        }
        return false;
    }


    private void updateData() {
        String nim = nimField.getText();
        // Cek apakah NIM sudah ada, kecuali untuk baris yang sedang diperbarui
        if (isNIMExists(nim, selectedIndex)) {
            JOptionPane.showMessageDialog(null, "NIM sudah ada, tidak dapat memperbarui data!");
        } else {
            int id = (int) tableModel.getValueAt(selectedIndex, 0);
            String nama = namaField.getText();
            String jenisKelamin = jenisKelaminComboBox.getSelectedItem().toString();
            boolean bekerja = bekerjaRadioButton.isSelected();

            String sql = "UPDATE mahasiswa SET nim='" + nim + "', nama='" + nama + "', jenis_kelamin='" + jenisKelamin + "', status_pekerjaan=" + (bekerja ? 1 : 0) + " WHERE id=" + id;
            database.insertUpdateDeleteQuery(sql);
            tableModel.setValueAt(nim, selectedIndex, 1);
            tableModel.setValueAt(nama, selectedIndex, 2);
            tableModel.setValueAt(jenisKelamin, selectedIndex, 3);
            tableModel.setValueAt(bekerja, selectedIndex, 4);
            clearForm();

            JOptionPane.showMessageDialog(null, "Data Berhasil Diubah!");
        }
    }

    private boolean isNIMExists(String nim, int excludeIndex) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (i != excludeIndex) {
                String existingNIM = (String) tableModel.getValueAt(i, 1);
                if (existingNIM.equals(nim)) {
                    return true;
                }
            }
        }
        return false;
    }


    private void deleteData() {
        int dialogResult = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(selectedIndex, 0);
            String sql = "DELETE FROM mahasiswa WHERE id=" + id;
            database.insertUpdateDeleteQuery(sql);

            tableModel.removeRow(selectedIndex);
            clearForm();

            JOptionPane.showMessageDialog(null, "Data Berhasil Dihapus!");
        }
    }

    private void clearForm() {
        nimField.setText("");
        namaField.setText("");
        jenisKelaminComboBox.setSelectedIndex(0);
        bekerjaRadioButton.setSelected(false);
        tidakBekerjaRadioButton.setSelected(false);

        addUpdateButton.setText("Add");
        deleteButton.setVisible(false);
        selectedIndex = -1;
    }
}

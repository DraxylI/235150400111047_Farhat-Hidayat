package gui;

import java.sql.PreparedStatement;
import java.time.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.stream.IntStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class FormMahasiswa extends JFrame {
    private JTextField nama, nim, no_tel, alamat, alamat_email;
    private JComboBox<String> tanggal, bulan, tahun;

    private String[] tgl = IntStream.rangeClosed(1, 31).mapToObj(Integer::toString).toArray(String[]::new);
    private String[] bln = { "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember" };
    private String[] thn = IntStream.rangeClosed(1945, 2024).mapToObj(Integer::toString).toArray(String[]::new);

    public FormMahasiswa() {
        setTitle("Form Mahasiswa Baru");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel pan = new JPanel(new GridBagLayout());
        pan.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        pan.add(new JLabel("NIM "), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        pan.add(new JLabel(":"), gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        nim = new JTextField(20);
        pan.add(nim, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        pan.add(new JLabel("Nama Lengkap"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        pan.add(new JLabel(":"), gbc);
        gbc.gridx = 2;
        gbc.gridy = 1;
        nama = new JTextField(20);
        pan.add(nama, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        pan.add(new JLabel("Tanggal Lahir"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        pan.add(new JLabel(":"), gbc);
        gbc.gridx = 2;
        gbc.gridy = 2;
        JPanel panel = new JPanel();
        tanggal = new JComboBox<>(tgl);
        bulan = new JComboBox<>(bln);
        tahun = new JComboBox<>(thn);
        panel.add(tanggal);
        panel.add(bulan);
        panel.add(tahun);
        pan.add(panel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        pan.add(new JLabel("No Telepon"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        pan.add(new JLabel(":"), gbc);
        gbc.gridx = 2;
        gbc.gridy = 3;
        no_tel = new JTextField(20);
        pan.add(no_tel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        pan.add(new JLabel("Alamat"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        pan.add(new JLabel(":"), gbc);
        gbc.gridx = 2;
        gbc.gridy = 4;
        alamat = new JTextField(20);
        pan.add(alamat, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        pan.add(new JLabel("Alamat Email"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        pan.add(new JLabel(":"), gbc);
        gbc.gridx = 2;
        gbc.gridy = 5;
        alamat_email = new JTextField(20);
        pan.add(alamat_email, gbc);

        JButton submitButton = new JButton("Submit");
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        submitButton.addActionListener(this::handleSubmit);
        pan.add(submitButton, gbc);

        add(pan);
        setVisible(true);
    }

    private Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/mydb";
        String user = "root";
        String password = "@Fa15110500";
        return DriverManager.getConnection(url, user, password);
    }

    private void handleSubmit(ActionEvent e) {
        String blnn = String.valueOf(bulan.getSelectedIndex() + 1);
        String ttl_lengkap = (String) tahun.getSelectedItem() + "-" + blnn + "-" + (String) tanggal.getSelectedItem();

        UIManager.put("OptionPane.okButtonText", "Kembali");
        UIManager.put("OptionPane.noButtonText", "Cancel");
        UIManager.put("OptionPane.yesButtonText", "Ya");

        if (nama.getText().isEmpty() || nim.getText().isEmpty() || no_tel.getText().isEmpty() || alamat.getText().isEmpty() || alamat_email.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua kolom harus terisi.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "Apakah data yang diisi sudah benar?", "Konfirmasi", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection conn = connect()) {
            String sql = "INSERT INTO mahasiswa (nim, nama_lengkap, tanggal_lahir, no_telepon, alamat, alamat_email) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nim.getText());
                pstmt.setString(2, nama.getText());
                pstmt.setString(3, ttl_lengkap);
                pstmt.setString(4, no_tel.getText());
                pstmt.setString(5, alamat.getText());
                pstmt.setString(6, alamat_email.getText());
                pstmt.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data ke database.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String data = String.format(
                "<html><table border='1'>" +
                        "<tr><th>NIM</th><th>Nama Lengkap</th><th>Tanggal Lahir</th><th>No Telepon</th><th>Alamat</th><th>Alamat Email</th></tr>" +
                        "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>" +
                        "</table></html>",
                nim.getText(), nama.getText(), ttl_lengkap, no_tel.getText(), alamat.getText(), alamat_email.getText()
        );
        
        JLabel label = new JLabel(data);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        JOptionPane.showMessageDialog(this, label, "Data Mahasiswa", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FormMahasiswa::new);
    }
}

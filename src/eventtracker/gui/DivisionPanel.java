package eventtracker.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
// import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import eventtracker.exception.DataNotFoundException;
import eventtracker.exception.InvalidDataException;
import eventtracker.model.Division;
import eventtracker.model.Event;
import eventtracker.model.Member;
import eventtracker.model.Position;

public class DivisionPanel extends RefreshablePanel {
    private JTable divisionTable;
    private DefaultTableModel divisionModel;
    private JButton addDivButton;
    private JButton editDivButton;
    private JButton deleteDivButton;

    private JTable memberTable;
    private DefaultTableModel memberModel;
    private JButton addMemberButton;
    private JButton deleteMemberButton;

    public DivisionPanel(MainFrame frame) {
        super(frame);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Panel Divisi (Kiri)
        JPanel divPanel = new JPanel(new BorderLayout(5, 5));
        divPanel.setBorder(BorderFactory.createTitledBorder("Daftar Divisi"));

        String[] divCols = new String[] { "No", "Nama Divisi", "Ketua Divisi", "Jumlah Tugas", "Progres" };
        divisionModel = new DefaultTableModel(divCols, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        divisionTable = new JTable(divisionModel);
        divisionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        divisionTable.getColumnModel().getColumn(0).setPreferredWidth(35);
        divisionTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        divisionTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        divisionTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        divisionTable.getColumnModel().getColumn(4).setPreferredWidth(70);

        JPanel divButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        addDivButton = new JButton("Tambah Divisi");
        editDivButton = new JButton("Ubah Divisi");
        deleteDivButton = new JButton("Hapus Divisi");

        addDivButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onAddDivision();
            }
        });

        editDivButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onEditDivision();
            }
        });

        deleteDivButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onDeleteDivision();
            }
        });

        divButtonPanel.add(addDivButton);
        divButtonPanel.add(editDivButton);
        divButtonPanel.add(deleteDivButton);

        divPanel.add(new JScrollPane(divisionTable), BorderLayout.CENTER);
        divPanel.add(divButtonPanel, BorderLayout.SOUTH);

        // 2. Panel Anggota (Kanan)
        JPanel memberPanel = new JPanel(new BorderLayout(5, 5));
        memberPanel.setBorder(BorderFactory.createTitledBorder("Daftar Anggota"));

        String[] memCols = new String[] { "No", "Nama", "Jabatan", "Divisi" };
        memberModel = new DefaultTableModel(memCols, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        memberTable = new JTable(memberModel);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberTable.getColumnModel().getColumn(0).setPreferredWidth(35);
        memberTable.getColumnModel().getColumn(1).setPreferredWidth(130);
        memberTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        memberTable.getColumnModel().getColumn(3).setPreferredWidth(110);

        JPanel memberButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        addMemberButton = new JButton("Tambah Anggota");
        deleteMemberButton = new JButton("Hapus Anggota");

        addMemberButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onAddMember();
            }
        });

        deleteMemberButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onDeleteMember();
            }
        });

        memberButtonPanel.add(addMemberButton);
        memberButtonPanel.add(deleteMemberButton);

        memberPanel.add(new JScrollPane(memberTable), BorderLayout.CENTER);
        memberPanel.add(memberButtonPanel, BorderLayout.SOUTH);

        // Gabungkan keduanya dengan SplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, divPanel, memberPanel);
        splitPane.setResizeWeight(0.5);

        add(splitPane, BorderLayout.CENTER);
    }

    public void refresh() {
        Event event = frame.getEvent();

        // Isi ulang tabel divisi
        divisionModel.setRowCount(0);
        if (event != null) {
            for (int i = 0; i < event.getDivisionCount(); i++) {
                Division d = event.getDivision(i);
                Object[] row = new Object[] {
                    (i + 1),
                    d.getName(),
                    d.getHeadName().isEmpty() ? "-" : d.getHeadName(),
                    d.getTaskCount(),
                    d.getProgress() + "%"
                };
                divisionModel.addRow(row);
            }
        }

        // Isi ulang tabel anggota
        memberModel.setRowCount(0);
        if (event != null) {
            for (int i = 0; i < event.getMemberCount(); i++) {
                Member m = event.getMember(i);
                Object[] row = new Object[] {
                    (i + 1),
                    m.getName(),
                    m.getPosition().getLabel(),
                    m.getDivisionName().isEmpty() ? "-" : m.getDivisionName()
                };
                memberModel.addRow(row);
            }
        }

        // Hak akses kelola event
        boolean canManage = frame.canManage();
        addDivButton.setEnabled(canManage);
        editDivButton.setEnabled(canManage);
        deleteDivButton.setEnabled(canManage);
        addMemberButton.setEnabled(canManage);
        deleteMemberButton.setEnabled(canManage);
    }

    private void onAddDivision() {
        if (!frame.canManage()) {
            frame.showError("Anda tidak memiliki hak akses untuk mengelola divisi.");
            return;
        }

        JTextField nameField = new JTextField(15);
        JTextField headField = new JTextField(15);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nama Divisi:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Ketua Divisi (opsional):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(headField, gbc);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Tambah Divisi Baru", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String head = headField.getText().trim();
            try {
                Division d = new Division(name, head);
                frame.getEvent().addDivision(d);
                frame.dataChanged();
            } catch (InvalidDataException ex) {
                frame.showError(ex.getMessage());
            }
        }
    }

    private void onEditDivision() {
        if (!frame.canManage()) {
            frame.showError("Anda tidak memiliki hak akses untuk mengelola divisi.");
            return;
        }

        int row = divisionTable.getSelectedRow();
        if (row < 0) {
            frame.showError("Pilih baris dulu.");
            return;
        }

        Division div = frame.getEvent().getDivision(row);
        String oldName = div.getName();

        JTextField nameField = new JTextField(div.getName(), 15);
        JTextField headField = new JTextField(div.getHeadName(), 15);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nama Divisi:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Ketua Divisi:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(headField, gbc);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Ubah Divisi", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String newName = nameField.getText().trim();
            String newHead = headField.getText().trim();

            if (newName.isEmpty()) {
                frame.showError("Nama divisi tidak boleh kosong.");
                return;
            }

            // Periksa nama kembar jika nama diubah
            if (!newName.equalsIgnoreCase(oldName)) {
                for (int i = 0; i < frame.getEvent().getDivisionCount(); i++) {
                    if (frame.getEvent().getDivision(i).getName().equalsIgnoreCase(newName)) {
                        frame.showError("Divisi dengan nama \"" + newName + "\" sudah ada.");
                        return;
                    }
                }
            }

            try {
                div.setName(newName);
                div.setHeadName(newHead);

                // Perbarui juga divisi pada anggota yang berada di divisi lama
                for (int i = 0; i < frame.getEvent().getMemberCount(); i++) {
                    Member m = frame.getEvent().getMember(i);
                    if (m.getDivisionName().equalsIgnoreCase(oldName)) {
                        m.setDivisionName(newName);
                    }
                }

                frame.dataChanged();
            } catch (InvalidDataException ex) {
                frame.showError(ex.getMessage());
            }
        }
    }

    private void onDeleteDivision() {
        if (!frame.canManage()) {
            frame.showError("Anda tidak memiliki hak akses untuk mengelola divisi.");
            return;
        }

        int row = divisionTable.getSelectedRow();
        if (row < 0) {
            frame.showError("Pilih baris dulu.");
            return;
        }

        Division div = frame.getEvent().getDivision(row);
        int choice = JOptionPane.showConfirmDialog(frame,
                "Menghapus divisi \"" + div.getName() + "\" akan menghapus seluruh tugas di dalamnya ("
                + div.getTaskCount() + " tugas).\nAnggota pada divisi ini akan dikosongkan divisinya.\nApakah Anda yakin ingin melanjutkan?",
                "Konfirmasi Hapus Divisi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                frame.getEvent().removeDivision(div);
                frame.dataChanged();
            } catch (DataNotFoundException ex) {
                frame.showError(ex.getMessage());
            }
        }
    }

    private void onAddMember() {
        if (!frame.canManage()) {
            frame.showError("Anda tidak memiliki hak akses untuk mengelola anggota.");
            return;
        }

        Event event = frame.getEvent();
        JTextField nameField = new JTextField(15);

        Position[] positions = new Position[] {
            Position.KETUA,
            Position.WAKIL,
            Position.SEKRETARIS,
            Position.BENDAHARA,
            Position.KETUA_DIVISI,
            Position.ANGGOTA
        };
        String[] posLabels = new String[positions.length];
        for (int i = 0; i < positions.length; i++) {
            posLabels[i] = positions[i].getLabel();
        }
        JComboBox<String> posCombo = new JComboBox<String>(posLabels);

        // Pilihan divisi: "-" untuk umum / koordinator, dan daftar divisi
        String[] divChoices = new String[event.getDivisionCount() + 1];
        divChoices[0] = "-";
        for (int i = 0; i < event.getDivisionCount(); i++) {
            divChoices[i + 1] = event.getDivision(i).getName();
        }
        JComboBox<String> divCombo = new JComboBox<String>(divChoices);

        // Jika jabatan koordinator dipilih, default divisi "-"
        posCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int idx = posCombo.getSelectedIndex();
                if (idx >= 0 && idx <= 3) {
                    divCombo.setSelectedIndex(0);
                    divCombo.setEnabled(false);
                } else {
                    divCombo.setEnabled(true);
                }
            }
        });
        posCombo.setSelectedIndex(5); // Default ANGGOTA

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nama Anggota:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Jabatan:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(posCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Divisi:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(divCombo, gbc);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Tambah Anggota", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            Position pos = positions[posCombo.getSelectedIndex()];
            String div = (String) divCombo.getSelectedItem();
            if ("-".equals(div)) {
                div = "";
            }

            try {
                Member m = Member.create(name, pos, div);
                event.addMember(m);
                frame.dataChanged();
            } catch (InvalidDataException ex) {
                frame.showError(ex.getMessage());
            }
        }
    }

    private void onDeleteMember() {
        if (!frame.canManage()) {
            frame.showError("Anda tidak memiliki hak akses untuk mengelola anggota.");
            return;
        }

        int row = memberTable.getSelectedRow();
        if (row < 0) {
            frame.showError("Pilih baris dulu.");
            return;
        }

        Member m = frame.getEvent().getMember(row);
        int choice = JOptionPane.showConfirmDialog(frame,
                "Apakah Anda yakin ingin menghapus anggota \"" + m.getName() + "\"?",
                "Konfirmasi Hapus Anggota",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                frame.getEvent().removeMember(m);
                frame.dataChanged();
            } catch (DataNotFoundException ex) {
                frame.showError(ex.getMessage());
            }
        }
    }
}

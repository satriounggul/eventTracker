package eventtracker.gui;

import java.awt.BorderLayout;
//import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import eventtracker.exception.DataNotFoundException;
import eventtracker.exception.InvalidDataException;
import eventtracker.io.TextUtil;
import eventtracker.model.Event;
import eventtracker.model.Meeting;
import eventtracker.model.Member;

public class MeetingPanel extends RefreshablePanel {
    private JTable meetingTable;
    private DefaultTableModel tableModel;

    private JTextField dateField;
    private JComboBox<String> divisionCombo;
    private JTextArea contentArea;
    private JTextArea decisionArea;

    private JButton addButton;
    private JButton deleteButton;
    private JButton clearButton;

    public MeetingPanel(MainFrame frame) {
        super(frame);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Tabel Notulensi Rapat
        String[] columns = new String[] { "No", "Tanggal", "Divisi", "Cuplikan Isi" };
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        meetingTable = new JTable(tableModel);
        meetingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        meetingTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        meetingTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        meetingTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        meetingTable.getColumnModel().getColumn(3).setPreferredWidth(450);

        meetingTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                onTableRowSelected();
            }
        });

        JScrollPane tableScroll = new JScrollPane(meetingTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Daftar Notulensi Rapat"));

        // 2. Form Rapat di Bawah
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Detail / Tambah Notulensi"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Tanggal
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        formPanel.add(new JLabel("Tanggal (dd-MM-yyyy):"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        dateField = new JTextField(TextUtil.formatDate(LocalDate.now()), 15);
        formPanel.add(dateField, gbc);
        row++;

        // Divisi
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        formPanel.add(new JLabel("Divisi:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        divisionCombo = new JComboBox<String>(new String[] { "Semua" });
        formPanel.add(divisionCombo, gbc);
        row++;

        // Isi Rapat
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        formPanel.add(new JLabel("Isi Rapat:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        contentArea = new JTextArea(3, 30);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(contentArea), gbc);
        row++;

        // Keputusan
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        formPanel.add(new JLabel("Keputusan:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        decisionArea = new JTextArea(2, 30);
        decisionArea.setLineWrap(true);
        decisionArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(decisionArea), gbc);
        row++;

        // Tombol Aksi Form
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        clearButton = new JButton("Form Baru");
        addButton = new JButton("Tambah Notulensi");
        deleteButton = new JButton("Hapus Notulensi");

        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearForm();
                meetingTable.clearSelection();
            }
        });

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onAddMeeting();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onDeleteMeeting();
            }
        });

        buttonPanel.add(clearButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Gabungkan tabel dan form
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, formPanel);
        splitPane.setResizeWeight(0.55);

        add(splitPane, BorderLayout.CENTER);
    }

    public void refresh() {
        Event event = frame.getEvent();

        // Perbarui daftar divisi di form dropdown
        String currentSelection = (String) divisionCombo.getSelectedItem();
        divisionCombo.removeAllItems();
        divisionCombo.addItem("Semua");
        if (event != null) {
            for (int i = 0; i < event.getDivisionCount(); i++) {
                divisionCombo.addItem(event.getDivision(i).getName());
            }
        }
        if (currentSelection != null) {
            divisionCombo.setSelectedItem(currentSelection);
            if (divisionCombo.getSelectedIndex() < 0) {
                divisionCombo.setSelectedIndex(0);
            }
        }

        // Isi ulang tabel
        tableModel.setRowCount(0);
        if (event != null) {
            for (int i = 0; i < event.getMeetingCount(); i++) {
                Meeting m = event.getMeeting(i);
                String snippet = m.getContent();
                if (snippet.length() > 60) {
                    snippet = snippet.substring(0, 57) + "...";
                }
                Object[] row = new Object[] {
                    (i + 1),
                    TextUtil.formatDate(m.getDate()),
                    m.getDivisionName(),
                    snippet
                };
                tableModel.addRow(row);
            }
        }

        updateActionButtons();
    }

    private void onTableRowSelected() {
        int selectedRow = meetingTable.getSelectedRow();
        Event event = frame.getEvent();
        if (event != null && selectedRow >= 0 && selectedRow < event.getMeetingCount()) {
            Meeting m = event.getMeeting(selectedRow);
            dateField.setText(TextUtil.formatDate(m.getDate()));
            divisionCombo.setSelectedItem(m.getDivisionName());
            contentArea.setText(m.getContent());
            decisionArea.setText(m.getDecision());
        }
        updateActionButtons();
    }

    private void clearForm() {
        dateField.setText(TextUtil.formatDate(LocalDate.now()));
        divisionCombo.setSelectedIndex(0);
        contentArea.setText("");
        decisionArea.setText("");
        updateActionButtons();
    }

    private void updateActionButtons() {
        Member user = frame.getCurrentUser();
        Event event = frame.getEvent();

        if (event == null) {
            addButton.setEnabled(false);
            deleteButton.setEnabled(false);
            return;
        }

        // Pengguna tanpa login / koordinator bisa tambah
        if (user == null || user.canManageEvent()) {
            addButton.setEnabled(true);
        } else if (user instanceof eventtracker.model.DivisionHead) {
            addButton.setEnabled(true);
        } else {
            // Staff
            addButton.setEnabled(false);
        }

        int selectedRow = meetingTable.getSelectedRow();
        if (selectedRow < 0) {
            deleteButton.setEnabled(false);
        } else {
            Meeting m = event.getMeeting(selectedRow);
            if (user == null || user.canManageEvent()) {
                deleteButton.setEnabled(true);
            } else if (m.getDivisionName().equalsIgnoreCase(user.getDivisionName())) {
                deleteButton.setEnabled(true);
            } else {
                deleteButton.setEnabled(false);
            }
        }
    }

    private void onAddMeeting() {
        Event event = frame.getEvent();
        if (event == null) {
            return;
        }

        String divName = (String) divisionCombo.getSelectedItem();
        if (divName == null) {
            divName = "Semua";
        }

        Member user = frame.getCurrentUser();
        if (user != null && !user.canManageEvent()) {
            // Jika bukan koordinator, hanya boleh tambah notulensi untuk divisinya sendiri
            if (!divName.equalsIgnoreCase(user.getDivisionName())) {
                frame.showError("Anda hanya dapat menambahkan notulensi untuk divisi Anda (" + user.getDivisionName() + ").");
                return;
            }
        }

        String dateStr = dateField.getText().trim();
        LocalDate mDate;
        try {
            mDate = TextUtil.parseDate(dateStr);
        } catch (InvalidDataException ex) {
            frame.showError(ex.getMessage());
            return;
        }

        String content = contentArea.getText().trim();
        if (content.isEmpty()) {
            frame.showError("Isi notulensi rapat tidak boleh kosong.");
            return;
        }

        String decision = decisionArea.getText().trim();

        try {
            Meeting meeting = new Meeting(mDate, divName, content, decision);
            event.addMeeting(meeting);
            clearForm();
            frame.dataChanged();
        } catch (InvalidDataException ex) {
            frame.showError(ex.getMessage());
        }
    }

    private void onDeleteMeeting() {
        Event event = frame.getEvent();
        if (event == null) {
            return;
        }

        int row = meetingTable.getSelectedRow();
        if (row < 0) {
            frame.showError("Pilih baris dulu.");
            return;
        }

        Meeting m = event.getMeeting(row);
        Member user = frame.getCurrentUser();
        if (user != null && !user.canManageEvent()) {
            if (!m.getDivisionName().equalsIgnoreCase(user.getDivisionName())) {
                frame.showError("Anda tidak memiliki hak akses untuk menghapus notulensi divisi ini.");
                return;
            }
        }

        int choice = JOptionPane.showConfirmDialog(frame,
                "Apakah Anda yakin ingin menghapus notulensi rapat tanggal " + TextUtil.formatDate(m.getDate()) + "?",
                "Konfirmasi Hapus Notulensi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                event.removeMeeting(m);
                clearForm();
                frame.dataChanged();
            } catch (DataNotFoundException ex) {
                frame.showError(ex.getMessage());
            }
        }
    }
}

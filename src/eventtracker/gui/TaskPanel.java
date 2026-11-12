package eventtracker.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import eventtracker.exception.DataNotFoundException;
import eventtracker.io.TextUtil;
import eventtracker.model.Division;
import eventtracker.model.Event;
import eventtracker.model.Member;
import eventtracker.model.Status;
import eventtracker.model.Task;

public class TaskPanel extends RefreshablePanel {
    private JComboBox<String> divisionFilterCombo;
    private JComboBox<String> statusFilterCombo;
    private JTextField searchField;
    private JTable taskTable;
    private DefaultTableModel tableModel;

    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton markDoneButton;

    private Task[] displayedTasks;
    private int displayedCount;

    public TaskPanel(MainFrame frame) {
        super(frame);
        displayedTasks = new Task[20];
        displayedCount = 0;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel Filter Bagian Atas
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter dan Pencarian"));

        filterPanel.add(new JLabel("Divisi:"));
        divisionFilterCombo = new JComboBox<String>(new String[] { "Semua Divisi" });
        divisionFilterCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyFilter();
            }
        });
        filterPanel.add(divisionFilterCombo);

        filterPanel.add(new JLabel("Status:"));
        statusFilterCombo = new JComboBox<String>(new String[] { "Semua Status", "Belum", "Proses", "Selesai", "Terlambat" });
        statusFilterCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyFilter();
            }
        });
        filterPanel.add(statusFilterCombo);

        filterPanel.add(new JLabel("Cari:"));
        searchField = new JTextField(15);
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                applyFilter();
            }
        });
        filterPanel.add(searchField);

        JButton clearFilterButton = new JButton("Reset");
        clearFilterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                divisionFilterCombo.setSelectedIndex(0);
                statusFilterCombo.setSelectedIndex(0);
                searchField.setText("");
                applyFilter();
            }
        });
        filterPanel.add(clearFilterButton);

        // Tabel Tugas
        String[] columnNames = new String[] {
            "No", "Judul", "Divisi", "PIC", "Prioritas", "Deadline", "Status", "QC"
        };
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        taskTable = new JTable(tableModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateButtonStates();
            }
        });

        // Ukuran kolom
        taskTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        taskTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        taskTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        taskTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        taskTable.getColumnModel().getColumn(7).setPreferredWidth(50);

        JScrollPane scrollPane = new JScrollPane(taskTable);

        // Panel Tombol Aksi Bagian Bawah
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        addButton = new JButton("Tambah Tugas");
        editButton = new JButton("Ubah Tugas");
        deleteButton = new JButton("Hapus Tugas");
        markDoneButton = new JButton("Tandai Selesai");

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onAdd();
            }
        });

        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onEdit();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onDelete();
            }
        });

        markDoneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onMarkDone();
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(markDoneButton);

        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void refresh() {
        Event event = frame.getEvent();

        // Perbarui daftar divisi pada dropdown filter
        String currentSelectedDiv = (String) divisionFilterCombo.getSelectedItem();
        divisionFilterCombo.removeAllItems();
        divisionFilterCombo.addItem("Semua Divisi");

        if (event != null) {
            for (int i = 0; i < event.getDivisionCount(); i++) {
                divisionFilterCombo.addItem(event.getDivision(i).getName());
            }
        }

        if (currentSelectedDiv != null) {
            divisionFilterCombo.setSelectedItem(currentSelectedDiv);
            if (divisionFilterCombo.getSelectedIndex() < 0) {
                divisionFilterCombo.setSelectedIndex(0);
            }
        }

        applyFilter();
    }

    private void applyFilter() {
        Event event = frame.getEvent();
        tableModel.setRowCount(0);
        displayedCount = 0;

        if (event == null) {
            updateButtonStates();
            return;
        }

        String selectedDiv = (String) divisionFilterCombo.getSelectedItem();
        String selectedStatus = (String) statusFilterCombo.getSelectedItem();
        String keyword = searchField.getText().trim().toLowerCase();

        // Kumpulkan tugas yang cocok
        for (int i = 0; i < event.getDivisionCount(); i++) {
            Division div = event.getDivision(i);

            // Filter divisi
            if (selectedDiv != null && !selectedDiv.equals("Semua Divisi") && !div.getName().equalsIgnoreCase(selectedDiv)) {
                continue;
            }

            for (int j = 0; j < div.getTaskCount(); j++) {
                Task t = div.getTask(j);

                // Filter status
                if (selectedStatus != null && !selectedStatus.equals("Semua Status")) {
                    if (selectedStatus.equals("Terlambat")) {
                        if (!t.isLate()) {
                            continue;
                        }
                    } else if (selectedStatus.equals("Belum")) {
                        if (t.getStatus() != Status.BELUM || t.isLate()) {
                            continue;
                        }
                    } else if (selectedStatus.equals("Proses")) {
                        if (t.getStatus() != Status.PROSES || t.isLate()) {
                            continue;
                        }
                    } else if (selectedStatus.equals("Selesai")) {
                        if (t.getStatus() != Status.SELESAI) {
                            continue;
                        }
                    }
                }

                // Filter kata kunci pencarian (judul atau PIC)
                if (!keyword.isEmpty()) {
                    boolean matchTitle = t.getTitle().toLowerCase().contains(keyword);
                    boolean matchPic = t.getPicText().toLowerCase().contains(keyword);
                    if (!matchTitle && !matchPic) {
                        continue;
                    }
                }

                // Tambahkan ke array displayedTasks
                if (displayedCount == displayedTasks.length) {
                    Task[] newArr = new Task[displayedTasks.length * 2];
                    for (int k = 0; k < displayedTasks.length; k++) {
                        newArr[k] = displayedTasks[k];
                    }
                    displayedTasks = newArr;
                }
                displayedTasks[displayedCount++] = t;

                Object[] row = new Object[] {
                    displayedCount,
                    t.getTitle(),
                    t.getDivisionName(),
                    t.getPicText(),
                    t.getPriority().getLabel(),
                    TextUtil.formatDate(t.getDeadline()),
                    t.getStatusLabel(),
                    t.isQualityChecked() ? "Ya" : "Tidak"
                };
                tableModel.addRow(row);
            }
        }

        updateButtonStates();
    }

    private Task getSelectedTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < displayedCount) {
            return displayedTasks[selectedRow];
        }
        return null;
    }

    private void updateButtonStates() {
        Event event = frame.getEvent();
        Member user = frame.getCurrentUser();

        // Tombol Tambah
        boolean canAddAny = false;
        if (event != null && event.getDivisionCount() > 0) {
            if (user == null || user.canManageEvent()) {
                canAddAny = true;
            } else {
                for (int i = 0; i < event.getDivisionCount(); i++) {
                    if (user.canEdit(event.getDivision(i).getName())) {
                        canAddAny = true;
                        break;
                    }
                }
            }
        }
        addButton.setEnabled(canAddAny);

        // Tombol baris terpilih
        Task selected = getSelectedTask();
        if (selected == null) {
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
            markDoneButton.setEnabled(false);
        } else {
            boolean canEditTask = frame.canEdit(selected.getDivisionName());
            editButton.setEnabled(canEditTask);
            deleteButton.setEnabled(canEditTask);
            markDoneButton.setEnabled(canEditTask);
        }
    }

    private void onAdd() {
        Event event = frame.getEvent();
        if (event == null || event.getDivisionCount() == 0) {
            frame.showError("Belum ada divisi. Buat divisi terlebih dahulu di tab Divisi dan Anggota.");
            return;
        }

        String defaultDiv = "";
        Member user = frame.getCurrentUser();
        if (user != null && !user.canManageEvent()) {
            defaultDiv = user.getDivisionName();
        }

        TaskDialog dialog = new TaskDialog(frame, event, null, defaultDiv);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            frame.dataChanged();
        }
    }

    private void onEdit() {
        Task selected = getSelectedTask();
        if (selected == null) {
            frame.showError("Pilih baris dulu.");
            return;
        }
        if (!frame.canEdit(selected.getDivisionName())) {
            frame.showError("Anda tidak memiliki hak akses untuk mengubah tugas di divisi ini.");
            return;
        }

        TaskDialog dialog = new TaskDialog(frame, frame.getEvent(), selected, selected.getDivisionName());
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            frame.dataChanged();
        }
    }

    private void onDelete() {
        Task selected = getSelectedTask();
        if (selected == null) {
            frame.showError("Pilih baris dulu.");
            return;
        }
        if (!frame.canEdit(selected.getDivisionName())) {
            frame.showError("Anda tidak memiliki hak akses untuk menghapus tugas di divisi ini.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(frame,
                "Apakah Anda yakin ingin menghapus tugas \"" + selected.getTitle() + "\"?",
                "Konfirmasi Hapus Tugas",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                Division div = frame.getEvent().findDivision(selected.getDivisionName());
                div.removeTask(selected);
                frame.dataChanged();
            } catch (DataNotFoundException ex) {
                frame.showError(ex.getMessage());
            }
        }
    }

    private void onMarkDone() {
        Task selected = getSelectedTask();
        if (selected == null) {
            frame.showError("Pilih baris dulu.");
            return;
        }
        if (!frame.canEdit(selected.getDivisionName())) {
            frame.showError("Anda tidak memiliki hak akses untuk mengubah status tugas ini.");
            return;
        }

        selected.setStatus(Status.SELESAI);
        frame.dataChanged();
    }
}

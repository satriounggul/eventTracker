package eventtracker.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import eventtracker.exception.DataNotFoundException;
import eventtracker.exception.InvalidDataException;
import eventtracker.io.TextUtil;
import eventtracker.model.Division;
import eventtracker.model.Event;
import eventtracker.model.Priority;
import eventtracker.model.Status;
import eventtracker.model.Task;

public class TaskDialog extends JDialog {
    private Event event;
    private Task taskToEdit;
    private boolean confirmed;

    private JTextField titleField;
    private JComboBox<String> divisionCombo;
    private JTextArea descArea;
    private JTextField picField;
    private JCheckBox allPicsCheckBox;
    private JComboBox<String> priorityCombo;
    private JTextField deadlineField;
    private JComboBox<String> statusCombo;
    private JTextArea notesArea;
    private JTextField linkField;
    private JCheckBox qcCheckBox;

    public TaskDialog(Frame parent, Event event, Task taskToEdit, String defaultDivision) {
        super(parent, (taskToEdit == null ? "Tambah Tugas" : "Ubah Tugas"), true);
        this.event = event;
        this.taskToEdit = taskToEdit;
        this.confirmed = false;

        initComponents(defaultDivision);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents(String defaultDivision) {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // 1. Judul
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        formPanel.add(new JLabel("Judul Tugas:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        titleField = new JTextField(25);
        formPanel.add(titleField, gbc);
        row++;

        // 2. Divisi
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        formPanel.add(new JLabel("Divisi:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        String[] divNames = new String[event.getDivisionCount()];
        for (int i = 0; i < event.getDivisionCount(); i++) {
            divNames[i] = event.getDivision(i).getName();
        }
        divisionCombo = new JComboBox<String>(divNames);
        if (defaultDivision != null && !defaultDivision.isEmpty()) {
            divisionCombo.setSelectedItem(defaultDivision);
        }
        formPanel.add(divisionCombo, gbc);
        row++;

        // 3. Deskripsi
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        formPanel.add(new JLabel("Deskripsi:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        descArea = new JTextArea(3, 25);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(descArea), gbc);
        row++;

        // 4. PIC
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        formPanel.add(new JLabel("PIC (pisahkan koma):"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        picField = new JTextField(25);
        formPanel.add(picField, gbc);
        row++;

        // 5. Opsi Semua Anggota
        gbc.gridx = 1; gbc.gridy = row;
        allPicsCheckBox = new JCheckBox("Semua anggota divisi");
        allPicsCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                picField.setEnabled(!allPicsCheckBox.isSelected());
            }
        });
        formPanel.add(allPicsCheckBox, gbc);
        row++;

        // 6. Prioritas
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        formPanel.add(new JLabel("Prioritas:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        priorityCombo = new JComboBox<String>(new String[] {
            Priority.RENDAH.getLabel(),
            Priority.SEDANG.getLabel(),
            Priority.TINGGI.getLabel()
        });
        priorityCombo.setSelectedIndex(1); // Default SEDANG
        formPanel.add(priorityCombo, gbc);
        row++;

        // 7. Deadline
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        formPanel.add(new JLabel("Deadline (dd-MM-yyyy):"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        deadlineField = new JTextField(TextUtil.formatDate(LocalDate.now().plusDays(7)), 15);
        formPanel.add(deadlineField, gbc);
        row++;

        // 8. Status
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        statusCombo = new JComboBox<String>(new String[] {
            Status.BELUM.getLabel(),
            Status.PROSES.getLabel(),
            Status.SELESAI.getLabel()
        });
        formPanel.add(statusCombo, gbc);
        row++;

        // 9. Catatan
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        formPanel.add(new JLabel("Catatan:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        notesArea = new JTextArea(2, 25);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(notesArea), gbc);
        row++;

        // 10. Link Hasil
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        formPanel.add(new JLabel("Link Hasil:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        linkField = new JTextField(25);
        formPanel.add(linkField, gbc);
        row++;

        // 11. QC Checkbox
        gbc.gridx = 1; gbc.gridy = row;
        qcCheckBox = new JCheckBox("Quality Control (QC)");
        formPanel.add(qcCheckBox, gbc);
        row++;

        // Jika mode edit, isi nilai yang sudah ada
        if (taskToEdit != null) {
            titleField.setText(taskToEdit.getTitle());
            divisionCombo.setSelectedItem(taskToEdit.getDivisionName());
            descArea.setText(taskToEdit.getDescription());
            picField.setText(TextUtil.joinPicsWithComma(taskToEdit.getPics()));
            allPicsCheckBox.setSelected(taskToEdit.isAllPics());
            if (taskToEdit.isAllPics()) {
                picField.setEnabled(false);
            }
            if (taskToEdit.getPriority() == Priority.RENDAH) {
                priorityCombo.setSelectedIndex(0);
            } else if (taskToEdit.getPriority() == Priority.SEDANG) {
                priorityCombo.setSelectedIndex(1);
            } else {
                priorityCombo.setSelectedIndex(2);
            }
            deadlineField.setText(TextUtil.formatDate(taskToEdit.getDeadline()));
            if (taskToEdit.getStatus() == Status.BELUM) {
                statusCombo.setSelectedIndex(0);
            } else if (taskToEdit.getStatus() == Status.PROSES) {
                statusCombo.setSelectedIndex(1);
            } else {
                statusCombo.setSelectedIndex(2);
            }
            notesArea.setText(taskToEdit.getNotes());
            linkField.setText(taskToEdit.getResultLink());
            qcCheckBox.setSelected(taskToEdit.isQualityChecked());
        }

        // Panel Tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Simpan");
        JButton cancelButton = new JButton("Batal");

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSave();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                confirmed = false;
                dispose();
            }
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void onSave() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Judul tugas tidak boleh kosong.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String divName = (String) divisionCombo.getSelectedItem();
        if (divName == null || divName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih divisi untuk tugas ini.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean allPics = allPicsCheckBox.isSelected();
        String[] pics = TextUtil.splitPics(picField.getText());
        if (!allPics && pics.length == 0) {
            JOptionPane.showMessageDialog(this, "PIC tidak boleh kosong jika opsi Semua tidak dicentang.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate deadline;
        try {
            deadline = TextUtil.parseDate(deadlineField.getText());
        } catch (InvalidDataException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Priority priority;
        int pIdx = priorityCombo.getSelectedIndex();
        if (pIdx == 0) {
            priority = Priority.RENDAH;
        } else if (pIdx == 1) {
            priority = Priority.SEDANG;
        } else {
            priority = Priority.TINGGI;
        }

        Status status;
        int sIdx = statusCombo.getSelectedIndex();
        if (sIdx == 0) {
            status = Status.BELUM;
        } else if (sIdx == 1) {
            status = Status.PROSES;
        } else {
            status = Status.SELESAI;
        }

        try {
            if (taskToEdit == null) {
                // Tambah tugas baru
                Division targetDiv = event.findDivision(divName);
                Task newTask = new Task(title, targetDiv.getName(), priority, deadline);
                newTask.setDescription(descArea.getText().trim());
                newTask.setPics(pics);
                newTask.setAllPics(allPics);
                newTask.setStatus(status);
                newTask.setNotes(notesArea.getText().trim());
                newTask.setResultLink(linkField.getText().trim());
                newTask.setQualityChecked(qcCheckBox.isSelected());
                targetDiv.addTask(newTask);
            } else {
                // Ubah tugas yang sudah ada
                String oldDivName = taskToEdit.getDivisionName();
                taskToEdit.setTitle(title);
                taskToEdit.setDescription(descArea.getText().trim());
                taskToEdit.setPics(pics);
                taskToEdit.setAllPics(allPics);
                taskToEdit.setPriority(priority);
                taskToEdit.setDeadline(deadline);
                taskToEdit.setStatus(status);
                taskToEdit.setNotes(notesArea.getText().trim());
                taskToEdit.setResultLink(linkField.getText().trim());
                taskToEdit.setQualityChecked(qcCheckBox.isSelected());

                // Jika divisi berubah, pindahkan tugas ke divisi baru
                if (!oldDivName.equalsIgnoreCase(divName)) {
                    Division oldDiv = event.findDivision(oldDivName);
                    Division newDiv = event.findDivision(divName);
                    oldDiv.removeTask(taskToEdit);
                    newDiv.addTask(taskToEdit);
                }
            }
            confirmed = true;
            dispose();
        } catch (InvalidDataException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        } catch (DataNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}

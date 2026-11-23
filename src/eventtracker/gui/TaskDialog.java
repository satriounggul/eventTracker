package eventtracker.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
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

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 6, 12));

        // 1. Judul
        titleField = new JTextField(25);
        formPanel.add(createRow("Judul Tugas:", titleField));
        formPanel.add(Box.createVerticalStrut(6));

        // 2. Divisi
        String[] divNames = new String[event.getDivisionCount()];
        for (int i = 0; i < event.getDivisionCount(); i++) {
            divNames[i] = event.getDivision(i).getName();
        }
        divisionCombo = new JComboBox<>(divNames);
        if (defaultDivision != null && !defaultDivision.isEmpty()) {
            divisionCombo.setSelectedItem(defaultDivision);
        }
        formPanel.add(createRow("Divisi:", divisionCombo));
        formPanel.add(Box.createVerticalStrut(6));

        // 3. Deskripsi
        descArea = new JTextArea(3, 25);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        formPanel.add(createRow("Deskripsi:", new JScrollPane(descArea)));
        formPanel.add(Box.createVerticalStrut(6));

        // 4. PIC
        picField = new JTextField(25);
        formPanel.add(createRow("PIC (pisahkan koma):", picField));
        formPanel.add(Box.createVerticalStrut(4));

        // 5. Opsi Semua Anggota
        allPicsCheckBox = new JCheckBox("Semua anggota divisi");
        allPicsCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                picField.setEnabled(!allPicsCheckBox.isSelected());
            }
        });
        formPanel.add(createRow("", allPicsCheckBox));
        formPanel.add(Box.createVerticalStrut(6));

        // 6. Prioritas
        priorityCombo = new JComboBox<>(new String[] {
            Priority.RENDAH.getLabel(),
            Priority.SEDANG.getLabel(),
            Priority.TINGGI.getLabel()
        });
        priorityCombo.setSelectedIndex(1); // Default SEDANG
        formPanel.add(createRow("Prioritas:", priorityCombo));
        formPanel.add(Box.createVerticalStrut(6));

        // 7. Deadline
        deadlineField = new JTextField(TextUtil.formatDate(LocalDate.now().plusDays(7)), 15);
        formPanel.add(createRow("Deadline (dd-MM-yyyy):", deadlineField));
        formPanel.add(Box.createVerticalStrut(6));

        // 8. Status
        statusCombo = new JComboBox<>(new String[] {
            Status.BELUM.getLabel(),
            Status.PROSES.getLabel(),
            Status.SELESAI.getLabel()
        });
        formPanel.add(createRow("Status:", statusCombo));
        formPanel.add(Box.createVerticalStrut(6));

        // 9. Catatan
        notesArea = new JTextArea(2, 25);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        formPanel.add(createRow("Catatan:", new JScrollPane(notesArea)));
        formPanel.add(Box.createVerticalStrut(6));

        // 10. Link Hasil
        linkField = new JTextField(25);
        formPanel.add(createRow("Link Hasil:", linkField));
        formPanel.add(Box.createVerticalStrut(6));

        // 11. QC Checkbox
        qcCheckBox = new JCheckBox("Quality Control (QC)");
        formPanel.add(createRow("", qcCheckBox));

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
            @Override
            public void actionPerformed(ActionEvent e) {
                onSave();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
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

    private JPanel createRow(String labelText, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(150, 24));
        row.add(label, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
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
        Set<String> pics = TextUtil.splitPicsToSet(picField.getText());
        if (!allPics && pics.isEmpty()) {
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
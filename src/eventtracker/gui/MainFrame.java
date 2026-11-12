package eventtracker.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import eventtracker.exception.InvalidDataException;
import eventtracker.io.FileManager;
import eventtracker.io.ReportExporter;
import eventtracker.io.SampleData;
import eventtracker.io.TextUtil;
import eventtracker.model.Division;
import eventtracker.model.Event;
import eventtracker.model.Member;

public class MainFrame extends JFrame {
    private Event currentEvent;
    private File currentFile;
    private boolean dirty;
    private Member currentUser;

    private JComboBox<String> userCombo;
    private boolean updatingUserCombo;
    private JLabel eventInfoLabel;

    private DashboardPanel dashboardPanel;
    private TaskPanel taskPanel;
    private DivisionPanel divisionPanel;
    private MeetingPanel meetingPanel;

    public MainFrame() {
        super("Event Progress Tracker");
        dirty = false;
        currentFile = null;
        currentUser = null;
        updatingUserCombo = false;

        try {
            currentEvent = new Event("Belum Ada Event", LocalDate.now());
        } catch (InvalidDataException e) {
            // Default aman
        }

        initComponents();
        refreshAll();
    }

    private void initComponents() {
        setSize(1000, 680);
        setMinimumSize(new Dimension(850, 550));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (promptSaveIfDirty()) {
                    dispose();
                    System.exit(0);
                }
            }
        });

        // 1. Menu Bar
        buildMenuBar();

        // 2. Toolbar
        buildToolBar();

        // 3. Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        dashboardPanel = new DashboardPanel(this);
        taskPanel = new TaskPanel(this);
        divisionPanel = new DivisionPanel(this);
        meetingPanel = new MeetingPanel(this);

        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("Tugas", taskPanel);
        tabbedPane.addTab("Divisi dan Anggota", divisionPanel);
        tabbedPane.addTab("Notulensi", meetingPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu File
        JMenu fileMenu = new JMenu("File");

        JMenuItem newEventItem = new JMenuItem("Event Baru");
        newEventItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onNewEvent();
            }
        });

        JMenuItem openEventItem = new JMenuItem("Buka Event...");
        openEventItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOpenEvent();
            }
        });

        JMenuItem saveItem = new JMenuItem("Simpan");
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSave();
            }
        });

        JMenuItem saveAsItem = new JMenuItem("Simpan Sebagai...");
        saveAsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSaveAs();
            }
        });

        JMenuItem loadSampleItem = new JMenuItem("Muat Data Contoh");
        loadSampleItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onLoadSampleData();
            }
        });

        JMenuItem exportTxtItem = new JMenuItem("Export Laporan TXT...");
        exportTxtItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExportTxt();
            }
        });

        JMenuItem exportCsvItem = new JMenuItem("Export Tugas CSV...");
        exportCsvItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExportCsv();
            }
        });

        JMenuItem exitItem = new JMenuItem("Keluar");
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (promptSaveIfDirty()) {
                    dispose();
                    System.exit(0);
                }
            }
        });

        fileMenu.add(newEventItem);
        fileMenu.add(openEventItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.add(loadSampleItem);
        fileMenu.addSeparator();
        fileMenu.add(exportTxtItem);
        fileMenu.add(exportCsvItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Menu Tampilan
        JMenu viewMenu = new JMenu("Tampilan");
        UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
        ButtonGroup lafGroup = new ButtonGroup();
        String currentLafClass = UIManager.getLookAndFeel().getClass().getName();

        for (int i = 0; i < lafs.length; i++) {
            UIManager.LookAndFeelInfo laf = lafs[i];
            JRadioButtonMenuItem lafItem = new JRadioButtonMenuItem(laf.getName());
            if (laf.getClassName().equals(currentLafClass)) {
                lafItem.setSelected(true);
            }
            lafGroup.add(lafItem);
            lafItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        UIManager.setLookAndFeel(laf.getClassName());
                        SwingUtilities.updateComponentTreeUI(MainFrame.this);
                    } catch (Exception ex) {
                        showError("Gagal mengganti tema: " + ex.getMessage());
                    }
                }
            });
            viewMenu.add(lafItem);
        }

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);
    }

    private void buildToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        toolBar.add(new JLabel("Masuk sebagai:"));
        userCombo = new JComboBox<String>();
        userCombo.setPreferredSize(new Dimension(220, 26));
        userCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (updatingUserCombo) {
                    return;
                }
                int idx = userCombo.getSelectedIndex();
                if (idx <= 0 || currentEvent == null || idx > currentEvent.getMemberCount()) {
                    currentUser = null;
                } else {
                    currentUser = currentEvent.getMember(idx - 1);
                }
                refreshAll();
            }
        });
        toolBar.add(userCombo);

        toolBar.add(new JSeparator(JSeparator.VERTICAL));

        eventInfoLabel = new JLabel("Event: -");
        eventInfoLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        toolBar.add(eventInfoLabel);

        add(toolBar, BorderLayout.NORTH);
    }

    public Event getEvent() {
        return currentEvent;
    }

    public void setEvent(Event e) {
        this.currentEvent = e;
        this.dirty = false;
        this.currentFile = null;
        this.currentUser = null;
        refreshAll();
    }

    public Member getCurrentUser() {
        return currentUser;
    }

    public boolean canEdit(String divisionName) {
        if (currentUser == null) {
            return true;
        }
        return currentUser.canEdit(divisionName);
    }

    public boolean canManage() {
        if (currentUser == null) {
            return true;
        }
        return currentUser.canManageEvent();
    }

    public void dataChanged() {
        dirty = true;
        refreshAll();
    }

    public void refreshAll() {
        // Perbarui judul window
        String title = "Event Progress Tracker";
        if (currentEvent != null) {
            title += " - " + currentEvent.getName();
        }
        if (dirty) {
            title += " *";
        }
        setTitle(title);

        // Perbarui label event di toolbar
        if (currentEvent != null) {
            eventInfoLabel.setText("Event: " + currentEvent.getName() + " (" + TextUtil.formatDate(currentEvent.getDate()) + ")");
        } else {
            eventInfoLabel.setText("Event: -");
        }

        // Perbarui dropdown user
        updatingUserCombo = true;
        // int previousSelectedIndex = userCombo.getSelectedIndex();
        userCombo.removeAllItems();
        userCombo.addItem("(Akses Penuh / Tamu)");
        if (currentEvent != null) {
            for (int i = 0; i < currentEvent.getMemberCount(); i++) {
                Member m = currentEvent.getMember(i);
                userCombo.addItem(m.toString());
            }
        }

        if (currentUser == null) {
            userCombo.setSelectedIndex(0);
        } else {
            int foundIdx = -1;
            if (currentEvent != null) {
                for (int i = 0; i < currentEvent.getMemberCount(); i++) {
                    if (currentEvent.getMember(i) == currentUser) {
                        foundIdx = i + 1;
                        break;
                    }
                }
            }
            if (foundIdx != -1) {
                userCombo.setSelectedIndex(foundIdx);
            } else {
                currentUser = null;
                userCombo.setSelectedIndex(0);
            }
        }
        updatingUserCombo = false;

        // Refresh panel anak
        if (dashboardPanel != null) {
            dashboardPanel.refresh();
        }
        if (taskPanel != null) {
            taskPanel.refresh();
        }
        if (divisionPanel != null) {
            divisionPanel.refresh();
        }
        if (meetingPanel != null) {
            meetingPanel.refresh();
        }
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Kesalahan", JOptionPane.ERROR_MESSAGE);
    }

    public void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean promptSaveIfDirty() {
        if (!dirty) {
            return true;
        }
        int option = JOptionPane.showConfirmDialog(
                this,
                "Ada perubahan yang belum disimpan. Simpan sekarang?",
                "Konfirmasi Simpan",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            return onSave();
        } else if (option == JOptionPane.NO_OPTION) {
            return true;
        } else {
            return false;
        }
    }

    private void onNewEvent() {
        if (!promptSaveIfDirty()) {
            return;
        }

        JTextField nameField = new JTextField(20);
        JTextField dateField = new JTextField(TextUtil.formatDate(LocalDate.now().plusDays(30)), 12);
        JTextField divField = new JTextField(20);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nama Event:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Tanggal Event (dd-MM-yyyy):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Divisi Awal (pisahkan koma):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(divField, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Buat Event Baru", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                showError("Nama event tidak boleh kosong.");
                return;
            }
            LocalDate date;
            try {
                date = TextUtil.parseDate(dateField.getText());
            } catch (InvalidDataException ex) {
                showError(ex.getMessage());
                return;
            }

            try {
                Event newEvent = new Event(name, date);
                String[] initDivs = TextUtil.splitPics(divField.getText());
                for (int i = 0; i < initDivs.length; i++) {
                    if (!initDivs[i].isEmpty()) {
                        newEvent.addDivision(new Division(initDivs[i], ""));
                    }
                }
                currentEvent = newEvent;
                currentFile = null;
                dirty = false;
                currentUser = null;
                refreshAll();
            } catch (InvalidDataException ex) {
                showError(ex.getMessage());
            }
        }
    }

    private void onOpenEvent() {
        if (!promptSaveIfDirty()) {
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Buka File Event");
        chooser.setFileFilter(new FileNameExtensionFilter("Event File (*.evt)", "evt"));

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            try {
                Event loaded = FileManager.load(selectedFile);
                currentEvent = loaded;
                currentFile = selectedFile;
                dirty = false;
                currentUser = null;
                refreshAll();
                showInfo("Event berhasil dibuka dari: " + selectedFile.getName());
            } catch (InvalidDataException ex) {
                showError(ex.getMessage());
            } catch (IOException ex) {
                showError("Gagal membaca file: " + ex.getMessage());
            }
        }
    }

    private boolean onSave() {
        if (currentFile == null) {
            return onSaveAs();
        }
        try {
            FileManager.save(currentEvent, currentFile);
            dirty = false;
            refreshAll();
            showInfo("Data berhasil disimpan.");
            return true;
        } catch (IOException ex) {
            showError("Gagal menyimpan file: " + ex.getMessage());
            return false;
        }
    }

    private boolean onSaveAs() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Simpan Event Sebagai");
        chooser.setFileFilter(new FileNameExtensionFilter("Event File (*.evt)", "evt"));
        if (currentEvent != null) {
            String defaultFileName = currentEvent.getName().replaceAll("[^a-zA-Z0-9.-]", "_") + ".evt";
            chooser.setSelectedFile(new File(defaultFileName));
        }

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            if (!selectedFile.getName().toLowerCase().endsWith(".evt")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".evt");
            }
            try {
                FileManager.save(currentEvent, selectedFile);
                currentFile = selectedFile;
                dirty = false;
                refreshAll();
                showInfo("Data berhasil disimpan ke: " + selectedFile.getName());
                return true;
            } catch (IOException ex) {
                showError("Gagal menyimpan file: " + ex.getMessage());
                return false;
            }
        }
        return false;
    }

    private void onLoadSampleData() {
        if (!promptSaveIfDirty()) {
            return;
        }

        Event sample = SampleData.create();
        if (sample != null) {
            currentEvent = sample;
            currentFile = null;
            dirty = false;
            currentUser = null;
            refreshAll();
            showInfo("Data contoh berhasil dimuat.");
        } else {
            showError("Gagal memuat data contoh.");
        }
    }

    private void onExportTxt() {
        if (currentEvent == null) {
            showError("Tidak ada event yang aktif.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Laporan TXT");
        chooser.setFileFilter(new FileNameExtensionFilter("Text File (*.txt)", "txt"));
        String defaultFileName = "Laporan_" + currentEvent.getName().replaceAll("[^a-zA-Z0-9.-]", "_") + ".txt";
        chooser.setSelectedFile(new File(defaultFileName));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
            }
            try {
                ReportExporter.exportTxt(currentEvent, selectedFile);
                showInfo("Laporan berhasil diexport ke: " + selectedFile.getName());
            } catch (IOException ex) {
                showError("Gagal menulis laporan: " + ex.getMessage());
            }
        }
    }

    private void onExportCsv() {
        if (currentEvent == null) {
            showError("Tidak ada event yang aktif.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Tugas CSV");
        chooser.setFileFilter(new FileNameExtensionFilter("CSV File (*.csv)", "csv"));
        String defaultFileName = "Tugas_" + currentEvent.getName().replaceAll("[^a-zA-Z0-9.-]", "_") + ".csv";
        chooser.setSelectedFile(new File(defaultFileName));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            if (!selectedFile.getName().toLowerCase().endsWith(".csv")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".csv");
            }
            try {
                ReportExporter.exportCsv(currentEvent, selectedFile);
                showInfo("Daftar tugas berhasil diexport ke: " + selectedFile.getName());
            } catch (IOException ex) {
                showError("Gagal menulis CSV: " + ex.getMessage());
            }
        }
    }
}

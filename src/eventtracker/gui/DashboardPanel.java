package eventtracker.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
// import java.awt.FlowLayout;
import java.awt.Font;
// import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import eventtracker.io.TextUtil;
import eventtracker.model.Division;
import eventtracker.model.Event;
import eventtracker.model.Task;

public class DashboardPanel extends RefreshablePanel {
    private JProgressBar totalProgressBar;
    private JLabel totalSummaryLabel;
    private JPanel divisionListPanel;
    private JTable lateTable;
    private DefaultTableModel lateTableModel;
    private JTable upcomingTable;
    private DefaultTableModel upcomingTableModel;

    public DashboardPanel(MainFrame frame) {
        super(frame);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel Atas: Progres Keseluruhan Event
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Progres Keseluruhan Event"),
                BorderFactory.createEmptyBorder(8, 10, 10, 10)));

        totalProgressBar = new JProgressBar(0, 100);
        totalProgressBar.setStringPainted(true);
        totalProgressBar.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalProgressBar.setPreferredSize(new Dimension(800, 30));

        totalSummaryLabel = new JLabel("0 dari 0 tugas selesai (0%)");
        totalSummaryLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        topPanel.add(totalProgressBar);
        topPanel.add(Box.createVerticalStrut(6));
        topPanel.add(totalSummaryLabel);

        // Panel Tengah: Progres per Divisi (Scrollable)
        divisionListPanel = new JPanel();
        divisionListPanel.setLayout(new BoxLayout(divisionListPanel, BoxLayout.Y_AXIS));
        JScrollPane divScrollPane = new JScrollPane(divisionListPanel);
        divScrollPane.setBorder(BorderFactory.createTitledBorder("Progres Divisi"));
        divScrollPane.setPreferredSize(new Dimension(800, 160));

        // Panel Gabungan Atas + Divisi
        JPanel progressSection = new JPanel(new BorderLayout(5, 5));
        progressSection.add(topPanel, BorderLayout.NORTH);
        progressSection.add(divScrollPane, BorderLayout.CENTER);

        // Panel Bawah: 2 Tabel (Tugas Terlambat & 5 Deadline Terdekat)
        String[] columns = new String[] { "Judul Tugas", "Divisi", "PIC", "Deadline" };

        lateTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        lateTable = new JTable(lateTableModel);
        JScrollPane lateScrollPane = new JScrollPane(lateTable);
        lateScrollPane.setBorder(BorderFactory.createTitledBorder("Tugas Terlambat"));

        upcomingTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        upcomingTable = new JTable(upcomingTableModel);
        JScrollPane upcomingScrollPane = new JScrollPane(upcomingTable);
        upcomingScrollPane.setBorder(BorderFactory.createTitledBorder("5 Deadline Terdekat"));

        JSplitPane tablesSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, lateScrollPane, upcomingScrollPane);
        tablesSplitPane.setResizeWeight(0.5);
        tablesSplitPane.setPreferredSize(new Dimension(800, 220));

        add(progressSection, BorderLayout.NORTH);
        add(tablesSplitPane, BorderLayout.CENTER);
    }

    public void refresh() {
        Event event = frame.getEvent();
        if (event == null) {
            totalProgressBar.setValue(0);
            totalProgressBar.setString("0%");
            totalSummaryLabel.setText("Belum ada event.");
            divisionListPanel.removeAll();
            divisionListPanel.revalidate();
            divisionListPanel.repaint();
            lateTableModel.setRowCount(0);
            upcomingTableModel.setRowCount(0);
            return;
        }

        // 1. Perbarui progres total
        int totalTasks = event.getTotalTasks();
        int totalDone = event.getTotalDone();
        int totalLate = event.getTotalLate();
        int progress = event.getProgress();

        totalProgressBar.setValue(progress);
        totalProgressBar.setString(progress + "%");
        totalSummaryLabel.setText(totalDone + " dari " + totalTasks + " tugas selesai (" + progress + "%), " + totalLate + " tugas terlambat");

        // 2. Perbarui daftar divisi
        divisionListPanel.removeAll();
        if (event.getDivisionCount() == 0) {
            JLabel emptyLabel = new JLabel("Belum ada divisi yang terdaftar.");
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            divisionListPanel.add(emptyLabel);
        } else {
            for (int i = 0; i < event.getDivisionCount(); i++) {
                Division div = event.getDivision(i);
                JPanel rowPanel = new JPanel(new BorderLayout(10, 5));
                rowPanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

                JLabel nameLabel = new JLabel(div.getName());
                nameLabel.setPreferredSize(new Dimension(160, 22));
                nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

                JProgressBar divBar = new JProgressBar(0, 100);
                divBar.setValue(div.getProgress());
                divBar.setStringPainted(true);
                divBar.setString(div.getProgress() + "%");
                divBar.setPreferredSize(new Dimension(300, 20));

                JLabel countLabel = new JLabel(div.countDone() + "/" + div.getTaskCount() + " selesai (" + div.countLate() + " terlambat)");
                countLabel.setPreferredSize(new Dimension(160, 22));

                rowPanel.add(nameLabel, BorderLayout.WEST);
                rowPanel.add(divBar, BorderLayout.CENTER);
                rowPanel.add(countLabel, BorderLayout.EAST);

                divisionListPanel.add(rowPanel);
            }
        }
        divisionListPanel.revalidate();
        divisionListPanel.repaint();

        // 3. Perbarui tabel tugas terlambat
        lateTableModel.setRowCount(0);
        Task[] lateTasks = event.getLateTasks();
        for (int i = 0; i < lateTasks.length; i++) {
            Task t = lateTasks[i];
            Object[] row = new Object[] {
                t.getTitle(),
                t.getDivisionName(),
                t.getPicText(),
                TextUtil.formatDate(t.getDeadline())
            };
            lateTableModel.addRow(row);
        }

        // 4. Perbarui tabel 5 deadline terdekat
        upcomingTableModel.setRowCount(0);
        Task[] upcomingTasks = event.getUpcomingTasks(5);
        for (int i = 0; i < upcomingTasks.length; i++) {
            Task t = upcomingTasks[i];
            Object[] row = new Object[] {
                t.getTitle(),
                t.getDivisionName(),
                t.getPicText(),
                TextUtil.formatDate(t.getDeadline())
            };
            upcomingTableModel.addRow(row);
        }
    }
}

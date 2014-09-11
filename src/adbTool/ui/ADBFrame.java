package adbTool.ui;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import adbTool.ADBLogcat;
import adbTool.core.AdbWrapper;
import adbTool.core.ShellOutputReceiver;
import adbTool.core.Util;
import adbTool.models.AndroidPackage;
import adbTool.models.Device;
import adbTool.models.LogcatItem;
import adbTool.models.LogcatLevel;

public class ADBFrame extends javax.swing.JFrame
{
    //SortedListModel<String> _sortedPackageListModel = new SortedListModel<String>();
    SortedComboBoxModel<String> _sortedPackageCombobox = new SortedComboBoxModel<>();
    private AndroidPackage _activePackage;
    private String _packagePid;

    /**
     * Creates new form ADBFrame
     */
    public ADBFrame()
    {
        _logcatLevelComboBox = new JComboBox<LogcatLevel>(LogcatLevel.getValues());
//        ImageIcon img = new ImageIcon()
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("Resource/paw.png")));
        initComponents();
        setActions();
        //refreshDeviceList();

        //        setActivePackage();
        _packagesCombobox.setModel(_sortedPackageCombobox);

        AdbWrapper.getInstance().connect(" ", new AdbWrapper.DeviceConnectionListener()
        {
            @Override
            public void deviceConnected(Device deviceModel)
            {
                runInEventThread(() -> _devices.addItem(deviceModel), true);
            }

            @Override
            public void deviceDisconnected(Device device)
            {
                runInEventThread(() -> {
                    try
                    {
                        if (AdbWrapper.getInstance().getActiveDevice() == _devices.getSelectedItem())
                        {
                            AdbWrapper.getInstance().setDevice(null);
                        }
                        _devices.removeItem(device);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    onDeviceChanged();
                }, true);
            }

            @Override
            public void deviceChanged(Device deviceModel)
            {
                onDeviceChanged();
           //     _devices.setRequestFocusEnabled(true);
                //_devices.contentsChanged(new ListDataEvent(_devices.getSelectedItem(), 0,0, _devices.getItemCount()));
            }
        });

    //    ADBLogcat.init();
    }

    private void getPackagePid()
    {
        AdbWrapper.getInstance().getPidForPackage((String) _packagesCombobox.getSelectedItem(), results -> {
            if (results.length != 0)
            {
                _packagePid = null;
                String[] split = results[0].split(" ");
                if (split.length > 3)
                {
                    _packagePid = split[3];
                    Util.DbgLog("pid set to " + _packagePid);
                    ADBLogcat.getInstance().setLogcatPidFilter(_packagePid);
                }
            }
        });
    }

    private void runInEventThread(Runnable r, boolean isSynchronous)
    {
        if (isSynchronous)
        {
            try
            {
                SwingUtilities.invokeAndWait(r);
            }
            catch (InterruptedException e)
            {
            }
            catch (InvocationTargetException e)
            {
            }
        }
        else
        {
            SwingUtilities.invokeLater(r);
        }
    }

    private void refreshPackages()
    {
        _sortedPackageCombobox.clear();
        AdbWrapper.getInstance().getPackages(new ShellOutputReceiver(null, results -> {
            addPackagesFromShellResult(results);
            setActivePackage();

        }));

    }

    private void addPackagesFromShellResult(String[] results)
    {
        runInEventThread(() -> {
            for (String s : results)
            {
                if (s.contains(":"))
                {
                    s = s.split(":")[1];
                }
                _sortedPackageCombobox.addElement(s);
            }
        }, false);
    }

    private void setActivePackage()
    {
        _activePackageName.setText("");
        AdbWrapper.getInstance().getActivePackage(new AdbWrapper.ShellCommandResult<AndroidPackage>()
        {
            @Override
            public <T> void onCommandResult(T result)
            {
                _activePackage = (AndroidPackage) result;
                runInEventThread(() ->
                {
                    _packagesCombobox.setSelectedItem(_activePackage.getName());
                    _activePackageName.setText(_activePackage.getName());
                }, false);
            }
        });
    }

    private void onDeviceChanged()
    {
        refreshPackages();
        ADBLogcat.getInstance().start();
     //   setActivePackage();
    }

    private void setActions()
    {
        _packagesCombobox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                   getPackagePid();
            }
        });
        _save.addActionListener(event -> {
            String filename = openSaveLogToFileDialog();
            if (filename == null) return;

            PrintWriter out = null;
            try
            {
                out = new PrintWriter(filename);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            if (out == null) return;
            DefaultListModel<LogcatItem> itemList = ((LogcatTableModel) _logcatTable.getModel()).getItemList();
            for (Object item : itemList.toArray())
            {
                String logcatString = ((LogcatItem) item).getLogcatString();
                out.println(logcatString);
            }
        });
        _devices.removeAllItems();
        //_startLogcat.addActionListener(arg0 -> ADBLogcat.getInstance().startLogcat());

        _logcatLevelComboBox.addActionListener(arg0 -> {
            LogcatLevel x = (LogcatLevel) _logcatLevelComboBox.getSelectedItem();
            ADBLogcat.getInstance().setLogcatLevel(x);
        });
        _logcatLevelComboBox.setSelectedIndex(1);

        _devices.addActionListener(event -> {
            Device selectedItem = (Device) _devices.getSelectedItem();
            //   Util.DbgLog("Selected device changed: " + selectedItem == null ? "no devices" : selectedItem.toString());
            AdbWrapper.getInstance().setDevice(selectedItem);
            onDeviceChanged();
        });
        _installAPKButoon.addActionListener(arg0 -> {
            ChooseAndInstallAPKDialog dialog = new ChooseAndInstallAPKDialog(ADBFrame.this, true);
            dialog.run();
        });

        _clearLogcatButton.addActionListener(arg0 -> ADBLogcat.getInstance().clearLogcat());

        _clearAppData.addActionListener(arg -> {
            String selectedValue = (String) _packagesCombobox.getSelectedItem();
            if (selectedValue == null) return;
            AdbWrapper.getInstance().clearAppData(selectedValue);
        });


        _filterTextBox.addActionListener(evt -> filterTextBoxActionPerformed(evt));

        _filterTextBox.getDocument().addDocumentListener(new DocumentListener()
        {
            public void changedUpdate(DocumentEvent e)
            {
                warn();
            }

            public void removeUpdate(DocumentEvent e)
            {
                warn();
            }

            public void insertUpdate(DocumentEvent e)
            {
                warn();
            }

            public void warn()
            {
                //   _logcatTable.setFilter(_filterTextBox.getText().toString());
            }
        });

        _sendTextButton.addActionListener(arg0 -> new SendTextDialog(ADBFrame.this, true).run());
    }

    private String openSaveLogToFileDialog()
    {
        JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        if (fc.showOpenDialog(ADBFrame.this) == JFileChooser.APPROVE_OPTION)
        {
            File file = fc.getSelectedFile();
            return file.getPath();
        }
        return null;
    }

    private void filterTextBoxActionPerformed(java.awt.event.ActionEvent evt)
    {

    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents()
    {

        _rigthPanel = new javax.swing.JPanel();
        _installAPKButoon = new javax.swing.JButton();
        _restatADBServer = new javax.swing.JButton();
        _clearAppData = new javax.swing.JButton();
        _sendTextButton = new javax.swing.JButton();
        _centerPanel = new javax.swing.JPanel();
        scrollPanel = new javax.swing.JScrollPane();
        _logcatTable = new LogcatTable();
        _topPanle = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        _filterTextBox = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        _activePackageName = new javax.swing.JLabel();
        _startLogcat = new javax.swing.JButton();
        _clearLogcatButton = new javax.swing.JButton();
        _save = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        _packagesCombobox = new javax.swing.JComboBox();
        _devices = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        _bottomPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        _rigthPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Shortcuts"));

        _installAPKButoon.setText("Install APK");

        _restatADBServer.setText("Restart ADB");
        _restatADBServer.setToolTipText("");

        _clearAppData.setText("Clear app data");

        _sendTextButton.setText("Send Text");

        javax.swing.GroupLayout _rigthPanelLayout = new javax.swing.GroupLayout(_rigthPanel);
        _rigthPanel.setLayout(_rigthPanelLayout);
        _rigthPanelLayout.setHorizontalGroup(_rigthPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(_installAPKButoon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(_restatADBServer, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE).addComponent(_clearAppData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(_sendTextButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        _rigthPanelLayout.setVerticalGroup(_rigthPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(_rigthPanelLayout.createSequentialGroup().addComponent(_restatADBServer).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(_installAPKButoon).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(_clearAppData).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(_sendTextButton).addContainerGap(490, Short.MAX_VALUE)));

        _centerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Logcat"));

        scrollPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        _logcatTable.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{{null, null, null, null}, {null, null, null, null}, {null, null, null, null}, {null, null, null, null}}, new String[]{"Title 1", "Title 2", "Title 3", "Title 4"}));
        scrollPanel.setViewportView(_logcatTable);

        javax.swing.GroupLayout _topPanleLayout = new javax.swing.GroupLayout(_topPanle);
        _topPanle.setLayout(_topPanleLayout);
        _topPanleLayout.setHorizontalGroup(_topPanleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 576, Short.MAX_VALUE));
        _topPanleLayout.setVerticalGroup(_topPanleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 77, Short.MAX_VALUE));

        jLabel1.setText("Log level:");

        jLabel3.setText("Filter:");

        jLabel4.setText("Active Package:");

        _activePackageName.setText("{package name like: com.zemingo.ozvision}");

        _startLogcat.setText("Start");
        _startLogcat.setMaximumSize(new java.awt.Dimension(30, 30));
        _startLogcat.setMinimumSize(new java.awt.Dimension(20, 20));
        _startLogcat.setPreferredSize(new java.awt.Dimension(30, 30));

        _clearLogcatButton.setText("Clear");
        _clearLogcatButton.setMaximumSize(new java.awt.Dimension(30, 30));
        _clearLogcatButton.setMinimumSize(new java.awt.Dimension(20, 20));
        _clearLogcatButton.setPreferredSize(new java.awt.Dimension(30, 30));

        _save.setText("Save");
        _save.setMaximumSize(new java.awt.Dimension(30, 30));
        _save.setMinimumSize(new java.awt.Dimension(20, 20));
        _save.setPreferredSize(new java.awt.Dimension(30, 30));

        jLabel2.setText("Select package:");

        _packagesCombobox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));

        _devices.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));

        jLabel5.setText("Devices:");

        javax.swing.GroupLayout _centerPanelLayout = new javax.swing.GroupLayout(_centerPanel);
        _centerPanel.setLayout(_centerPanelLayout);
        _centerPanelLayout.setHorizontalGroup(_centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(scrollPanel).addGroup(_centerPanelLayout.createSequentialGroup().addGap(4, 4, 4).addGroup(_centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(_centerPanelLayout.createSequentialGroup().addGroup(_centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel1).addComponent(jLabel3)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(_centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(_filterTextBox).addComponent(_logcatLevelComboBox, 0, 264, Short.MAX_VALUE))).addGroup(_centerPanelLayout.createSequentialGroup().addComponent(jLabel4).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(_activePackageName))).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(_centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel2).addComponent(jLabel5)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(_centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(_devices, 0, 299, Short.MAX_VALUE).addComponent(_packagesCombobox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(_topPanle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(_startLogcat, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(_clearLogcatButton, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(_save, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)));
        _centerPanelLayout.setVerticalGroup(_centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, _centerPanelLayout.createSequentialGroup().addGroup(_centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(_topPanle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGroup(_centerPanelLayout.createSequentialGroup().addGroup(_centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel4).addComponent(_activePackageName)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(_centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel1).addComponent(_logcatLevelComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(_devices, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel5)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(_centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel3).addComponent(_filterTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel2).addComponent(_packagesCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))).addGroup(_centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(_clearLogcatButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(_startLogcat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(_save, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(scrollPanel)));

        javax.swing.GroupLayout _bottomPanelLayout = new javax.swing.GroupLayout(_bottomPanel);
        _bottomPanel.setLayout(_bottomPanelLayout);
        _bottomPanelLayout.setHorizontalGroup(_bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        _bottomPanelLayout.setVerticalGroup(_bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 22, Short.MAX_VALUE));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(_bottomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(layout.createSequentialGroup().addComponent(_centerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(_rigthPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(_rigthPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(_centerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(_bottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)));

        pack();
    }// </editor-fold>


    public static void showAdbFrame()
    {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException ex)
        {
            java.util.logging.Logger.getLogger(ADBFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (InstantiationException ex)
        {
            java.util.logging.Logger.getLogger(ADBFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (IllegalAccessException ex)
        {
            java.util.logging.Logger.getLogger(ADBFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(ADBFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new ADBFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify
    private javax.swing.JLabel _activePackageName;
    private javax.swing.JPanel _bottomPanel;
    private javax.swing.JPanel _centerPanel;
    private javax.swing.JButton _clearAppData;
    private javax.swing.JButton _clearLogcatButton;
    private javax.swing.JComboBox _devices;
    private javax.swing.JTextField _filterTextBox;
    private javax.swing.JButton _installAPKButoon;
    private javax.swing.JComboBox<LogcatLevel> _logcatLevelComboBox;
    private LogcatTable _logcatTable;
    private javax.swing.JComboBox _packagesCombobox;
    private javax.swing.JButton _restatADBServer;
    private javax.swing.JPanel _rigthPanel;
    private javax.swing.JButton _save;
    private javax.swing.JButton _sendTextButton;
    private javax.swing.JButton _startLogcat;
    private javax.swing.JPanel _topPanle;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane scrollPanel;


    // End of variables declaration


}

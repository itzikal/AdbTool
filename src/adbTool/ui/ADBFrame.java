package adbTool.ui;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import adbTool.ADBLogcat;
import adbTool.core.AdbWrapper;
import adbTool.core.ShellOutputReceiver;
import adbTool.models.AndroidPackage;
import adbTool.models.Device;
import adbTool.models.LogcatLevel;

public class ADBFrame extends javax.swing.JFrame
{
    SortedListModel<String> _sortedPackageListModel = new SortedListModel<String>();
    private AndroidPackage _activePackage;

    /**
     * Creates new form ADBFrame
     */
    public ADBFrame()
    {
        _logcatLevelComboBox = new JComboBox<LogcatLevel>(LogcatLevel.getValues());

        initComponents();
        setActions();
        //refreshDeviceList();

        //        setActivePackage();
        _packages.setModel(_sortedPackageListModel);

        AdbWrapper.getInstance().connect(" ", new AdbWrapper.DeviceConnectionListener()
        {
            @Override
            public void deviceConnected(Device device)
            {
                runInEventThread(() -> _devices.addItem(device), true);
            }

            @Override
            public void deviceDisconnected(Device device)
            {
                runInEventThread(() ->
                {
                    try
                    {
                        if(AdbWrapper.getInstance().getActiveDevice() == _devices.getSelectedItem())
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
        _sortedPackageListModel.clear();
        AdbWrapper.getInstance().getPackages(new ShellOutputReceiver(null, results -> addPackagesFromShellResult(results)));
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
                _sortedPackageListModel.add(s);
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
                runInEventThread(() -> _activePackageName.setText(_activePackage.getName()), false);
            }
        });
    }

    private void onDeviceChanged()
    {
        refreshPackages();
        setActivePackage();
    }

    private void setActions()
    {
        _devices.removeAllItems();
        _startLogcat.addActionListener(arg0 -> ADBLogcat.getInstance().startLogcat());

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
            String selectedValue = (String) _packages.getSelectedValue();
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
        jLabel2 = new javax.swing.JLabel();
        _packagesCombobox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();

        jLabel3 = new javax.swing.JLabel();
        _filterTextBox = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        _activePackageName = new javax.swing.JLabel();
        _startLogcat = new javax.swing.JButton();
        _clearLogcatButton = new javax.swing.JButton();
        _save = new javax.swing.JButton();
        _bottomPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        _devices = new javax.swing.JComboBox<Device>();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        _packages = new javax.swing.JList();

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

        jLabel2.setText("Select package:");

        _packagesCombobox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));

        javax.swing.GroupLayout _topPanleLayout = new javax.swing.GroupLayout(_topPanle);
        _topPanle.setLayout(_topPanleLayout);
        _topPanleLayout.setHorizontalGroup(_topPanleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(_topPanleLayout.createSequentialGroup().addComponent(jLabel2).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(_packagesCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(217, Short.MAX_VALUE)));
        _topPanleLayout.setVerticalGroup(_topPanleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(_topPanleLayout.createSequentialGroup().addContainerGap(29, Short.MAX_VALUE).addGroup(_topPanleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel2).addComponent(_packagesCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(28, 28, 28)));

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

        javax.swing.GroupLayout _centerPanelLayout = new javax.swing.GroupLayout(_centerPanel);
        _centerPanel.setLayout(_centerPanelLayout);
        _centerPanelLayout.setHorizontalGroup(_centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(scrollPanel).addGroup(_centerPanelLayout.createSequentialGroup().addGap(4, 4, 4).addGroup(_centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(_centerPanelLayout.createSequentialGroup().addGroup(_centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel1).addComponent(jLabel3)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(_centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(_filterTextBox).addComponent(_logcatLevelComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))).addGroup(_centerPanelLayout.createSequentialGroup().addComponent(jLabel4).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(_activePackageName))).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(_topPanle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(_startLogcat, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(_clearLogcatButton, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(_save, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)));
        _centerPanelLayout.setVerticalGroup(_centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, _centerPanelLayout.createSequentialGroup().addGroup(_centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(_topPanle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGroup(_centerPanelLayout.createSequentialGroup().addGroup(_centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel4).addComponent(_activePackageName)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(_centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel1).addComponent(_logcatLevelComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(_centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel3).addComponent(_filterTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))).addGroup(_centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(_clearLogcatButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(_startLogcat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(_save, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(scrollPanel)));

        javax.swing.GroupLayout _bottomPanelLayout = new javax.swing.GroupLayout(_bottomPanel);
        _bottomPanel.setLayout(_bottomPanelLayout);
        _bottomPanelLayout.setHorizontalGroup(_bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        _bottomPanelLayout.setVerticalGroup(_bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 22, Short.MAX_VALUE));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Devices"));

        _devices.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Packages"));

        //        _packages.setModel(new javax.swing.AbstractListModel()
        //        {
        //            String[] strings = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};
        //
        //            public int getSize()
        //            {
        //                return strings.length;
        //            }
        //
        //            public Object getElementAt(int i)
        //            {
        //                return strings[i];
        //            }
        //        });
        jScrollPane2.setViewportView(_packages);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jScrollPane2));
        jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jScrollPane2));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(_devices, 0, 165, Short.MAX_VALUE).addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(_devices, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18, 18, 18).addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(_bottomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(layout.createSequentialGroup().addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(_centerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(_rigthPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(_rigthPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(_centerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(_bottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)));

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
    private javax.swing.JList _packages;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane scrollPanel;
    // End of variables declaration


}

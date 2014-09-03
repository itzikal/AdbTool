package adbTool.ui;

import java.awt.Frame;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import adbTool.ADBWrapper;

public class ChooseAndInstallAPKDialog extends javax.swing.JDialog
{
    private JButton _browse;
    private JButton _cancel;
    private JTextField _filename;
    private JButton _install;
    private JLabel _title;

    public ChooseAndInstallAPKDialog(Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();
    }

    private void initComponents()
    {
        initTitle();
        initWindowParams();
        initFileNameEditBox();
        initBrowseButton();
        initCancelButton();
        initInstallButton();
        initMainPanelAndLayout();

        pack();
    }

    private void initMainPanelAndLayout()
    {
        JPanel _mainPanel = new JPanel();
        _mainPanel.setDropTarget(new DropTarget()
        {
            public synchronized void drop(DropTargetDropEvent evt)
            {
                try
                {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File file : droppedFiles)
                    {
                        String name = file.getName();
                        String ext = name.substring(name.lastIndexOf('.') + 1);
                        if (ext.toLowerCase().contains("apk"))
                        {
                            _filename.setText(file.getAbsolutePath());
                        }
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });


        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(_mainPanel);
        _mainPanel.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addGap(210, 210, 210).addComponent(_title).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addComponent(_install).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(_cancel)).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addComponent(_filename).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(_browse))).addContainerGap()));
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addComponent(_title).addGap(34, 34, 34).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(_browse).addComponent(_filename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(_cancel).addComponent(_install)).addContainerGap()));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(_mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addComponent(_mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
    }

    private void initInstallButton()
    {
        _install = new JButton();
        _install.setText("Install");
        _install.addActionListener(actionEvent ->
        {
            Process installProcess = ADBWrapper.getInstance().installApk(_filename.getText());
        });
    }

    private void initBrowseButton()
    {
        _browse = new JButton();
        _browse.setText("browse");
        _browse.addActionListener(actionEvent -> {
            JFileChooser fc = new JFileChooser();
            FileFilter apkfilter = new FileNameExtensionFilter("Apk Files", "apk");
            fc.setFileFilter(apkfilter);
            fc.setAcceptAllFileFilterUsed(false);
            if (fc.showOpenDialog(ChooseAndInstallAPKDialog.this) == JFileChooser.APPROVE_OPTION)
            {
                File file = fc.getSelectedFile();
                _filename.setText(file.getPath());
                System.out.println("getAbsolutePath " + file.getAbsolutePath());
                System.out.println("path: " + file.getPath());

                System.out.println("getName: " + file.getName());
            }
        });
    }

    private void initCancelButton()
    {
        _cancel = new JButton();
        _cancel.setText("Cancel");
        _cancel.addActionListener(arg0 -> ChooseAndInstallAPKDialog.this.setVisible(false));
    }

    private void initFileNameEditBox()
    {
        _filename = new JTextField();
        _filename.setToolTipText("Drag and drop APK file, or use file browser");
        _filename.setDropTarget(new DropTarget()
        {
            public synchronized void drop(DropTargetDropEvent evt)
            {
                try
                {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File file : droppedFiles)
                    {
                        String name = file.getName();
                        String ext = name.substring(name.lastIndexOf('.') + 1);
                        if (ext.toLowerCase().contains("apk"))
                        {
                            _filename.setText(file.getAbsolutePath());
                        }
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void initWindowParams()
    {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationByPlatform(true);
        setMaximumSize(new java.awt.Dimension(600, 200));
        setMinimumSize(new java.awt.Dimension(600, 200));
        setPreferredSize(new java.awt.Dimension(600, 200));
        setResizable(false);
    }

    private void initTitle()
    {
        _title = new javax.swing.JLabel();
        _title.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        _title.setText("Select APK File ");
    }


    public void run()
    {
        setVisible(true);
    }
}

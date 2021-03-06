package adbTool.ui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import adbTool.core.AdbWrapper;

public class SendTextDialog extends JDialog
{
    private JButton _sendTextButton;
    private JButton _cancel;
    private JTextField _sendTextField;
    private JLabel _title;
    private JPanel jPanel1;

    ActionListener escListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
        }
    };
    public SendTextDialog(Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();
        getRootPane().setDefaultButton(_sendTextButton);
        getRootPane().registerKeyboardAction(escListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        setActions();
    }

    private void setActions()
    {
        _cancel.addActionListener(arg0 -> SendTextDialog.this.setVisible(false));
        _sendTextButton.addActionListener(arg0 -> new Thread(() -> sendText()).start());
    }

    private void sendText()
    {
            String text = _sendTextField.getText();
            if (text == null || text.isEmpty()) return;
            AdbWrapper.getInstance().sendText(text);
    }

    public void run()
    {
        setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents()
    {
        initTitle();
        initWindowParams();
        jPanel1 = new JPanel();

        _sendTextField = new JTextField();
        _sendTextField.setToolTipText("");

        _sendTextButton = new JButton();
        _sendTextButton.setText("Send Text");

        _cancel = new JButton();
        _cancel.setText("Cancel");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addGap(210, 210, 210).addComponent(_title).addContainerGap(67, Short.MAX_VALUE)).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE).addComponent(_cancel)).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addComponent(_sendTextField).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(_sendTextButton))).addContainerGap()));
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addComponent(_title).addGap(34, 34, 34).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(_sendTextButton).addComponent(_sendTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE).addComponent(_cancel).addContainerGap()));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        pack();
    }// </editor-fold>

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
        _title.setText("Enter Text");
    }

}

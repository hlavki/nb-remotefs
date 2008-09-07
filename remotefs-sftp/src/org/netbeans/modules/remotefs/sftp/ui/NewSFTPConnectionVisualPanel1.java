/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NewSFTPConnectionPanel.java
 *
 * Created on 1.9.2008, 22:59:23
 */
package org.netbeans.modules.remotefs.sftp.ui;

import org.netbeans.modules.remotefs.sftp.client.SFTPClient;
import org.netbeans.modules.remotefs.sftp.resources.Bundle;

/**
 *
 * @author hlavki
 */
public class NewSFTPConnectionVisualPanel1 extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    /** Creates new form NewSFTPConnectionPanel */
    public NewSFTPConnectionVisualPanel1() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        connNameLabel = new javax.swing.JLabel();
        connNameTextField = new javax.swing.JTextField();
        hostLabel = new javax.swing.JLabel();
        hostTextField = new javax.swing.JTextField();
        portLabel = new javax.swing.JLabel();
        portSpinner = new javax.swing.JSpinner();
        userNameLabel = new javax.swing.JLabel();
        userNameTextField = new javax.swing.JTextField();
        keyFileLabel = new javax.swing.JLabel();
        keyFileTextField = new javax.swing.JTextField();
        browseKeyFileButton = new javax.swing.JButton();

        connNameLabel.setText(org.openide.util.NbBundle.getMessage(Bundle.class, "NewSFTPConnectionVisualPanel1.connNameLabel.text")); // NOI18N

        hostLabel.setText(org.openide.util.NbBundle.getMessage(Bundle.class, "NewSFTPConnectionVisualPanel1.hostLabel.text")); // NOI18N

        portLabel.setText(org.openide.util.NbBundle.getMessage(Bundle.class, "NewSFTPConnectionVisualPanel1.portLabel.text")); // NOI18N

        portSpinner.setValue(SFTPClient.DEFAULT_PORT);

        userNameLabel.setText(org.openide.util.NbBundle.getMessage(Bundle.class, "NewSFTPConnectionVisualPanel1.userNameLabel.text")); // NOI18N

        keyFileLabel.setText(org.openide.util.NbBundle.getMessage(Bundle.class, "NewSFTPConnectionVisualPanel1.keyFileLabel.text")); // NOI18N

        browseKeyFileButton.setText("...");
        browseKeyFileButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        browseKeyFileButton.setPreferredSize(new java.awt.Dimension(19, 19));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, keyFileLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, userNameLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, portLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, hostLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, connNameLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, connNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, hostTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, portSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 61, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(userNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(keyFileTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(browseKeyFileButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(connNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(connNameLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(hostLabel)
                    .add(hostTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(portLabel)
                    .add(portSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(userNameLabel)
                    .add(userNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(keyFileLabel)
                    .add(browseKeyFileButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(keyFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseKeyFileButton;
    private javax.swing.JLabel connNameLabel;
    private javax.swing.JTextField connNameTextField;
    private javax.swing.JLabel hostLabel;
    private javax.swing.JTextField hostTextField;
    private javax.swing.JLabel keyFileLabel;
    private javax.swing.JTextField keyFileTextField;
    private javax.swing.JLabel portLabel;
    private javax.swing.JSpinner portSpinner;
    private javax.swing.JLabel userNameLabel;
    private javax.swing.JTextField userNameTextField;
    // End of variables declaration//GEN-END:variables
}
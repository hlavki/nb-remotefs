/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.sftp.ui;

import java.awt.Component;
import java.awt.Dialog;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import javax.swing.JComponent;
import org.netbeans.modules.remotefs.api.LogInfoList;
import org.netbeans.modules.remotefs.sftp.client.SFTPLogInfo;
import org.netbeans.modules.remotefs.sftp.resources.Bundle;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

// An example action demonstrating how the wizard could be called from within
// your code. You can copy-paste the code below wherever you need.
public final class NewSFTPSiteWizardAction extends CallableSystemAction {

    private static final long serialVersionUID = 1L;
    private static final String PROP_TITLE = "NewSFTPSiteWizardAction.title";
    private static final String PROP_NAME = "NewSFTPSiteWizardAction.name";
    private static NewSFTPSiteWizardAction instance;
    private WizardDescriptor.Panel[] panels;

    public static CallableSystemAction getInstance() {
        if (instance == null) {
            instance = new NewSFTPSiteWizardAction();
        }
        return instance;
    }

    public void performAction() {
        @SuppressWarnings("unchecked")
        WizardDescriptor wizardDescriptor = new WizardDescriptor(getPanels());
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wizardDescriptor.setTitleFormat(new MessageFormat("{0}"));
        wizardDescriptor.setTitle(NbBundle.getMessage(Bundle.class, PROP_TITLE));
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
        dialog.setVisible(true);
        dialog.toFront();
        boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            Map<String, Object> props = wizardDescriptor.getProperties();
            SFTPLogInfo info = (SFTPLogInfo) props.get(NewSFTPSiteVisualPanel1.PROP_LOG_INFO);
            try {
                LogInfoList.getDefault().add(info);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            panels = new WizardDescriptor.Panel[]{
                        new NewSFTPSiteWizardPanel1()
                    };
            String[] steps = new String[panels.length];
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                // Default step name to component name of panel. Mainly useful
                // for getting the name of the target chooser to appear in the
                // list of steps.
                steps[i] = c.getName();
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    // TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
                    jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                    // Sets steps names for a panel
                    jc.putClientProperty("WizardPanel_contentData", steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
                }
            }
        }
        return panels;
    }

    public String getName() {
        return NbBundle.getMessage(Bundle.class, PROP_NAME);
    }

    @Override
    public String iconResource() {
        return null;
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}

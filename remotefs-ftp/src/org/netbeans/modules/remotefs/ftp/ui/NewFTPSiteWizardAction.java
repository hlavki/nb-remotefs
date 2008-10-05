package org.netbeans.modules.remotefs.ftp.ui;

import java.awt.Component;
import java.awt.Dialog;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import org.netbeans.modules.remotefs.api.LogInfoList;
import org.netbeans.modules.remotefs.ftp.client.FTPClient;
import org.netbeans.modules.remotefs.ftp.client.FTPLogInfo;
import org.netbeans.modules.remotefs.ftp.resources.Bundle;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 * Action that launches a wizard to register a new FTP site in the Explorer window.
 * 
 */
public final class NewFTPSiteWizardAction extends CallableSystemAction {

    private static final long serialVersionUID = 5592358462626059881L;
    private static final String PROP_TITLE = "NewFTPSiteWizardAction.title";
    private static final String PROP_NAME = "NewFTPSiteWizardAction.name";
    private WizardDescriptor.Panel[] panels;
    private static NewFTPSiteWizardAction instance;

    private NewFTPSiteWizardAction() {
        putValue(SMALL_ICON, new ImageIcon(
                ImageUtilities.loadImage("org/netbeans/modules/remotefs/ftp/resources/globe-sextant-16x16.png")));
    }

    public static CallableSystemAction getInstance() {
        if (instance == null) {
            instance = new NewFTPSiteWizardAction();
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
            //Validate
            Map<String, Object> props = wizardDescriptor.getProperties();
//            RemoteFSNode ftpx = Utilities.actionsGlobalContext().lookup(RemoteFSNode.class);
//            if (ftpx != null) {
            FTPLogInfo info = new FTPLogInfo();
            info.setHost(props.get(NewFTPSiteVisualPanel1.SITE_SERVER).toString());
            info.setPort(FTPClient.DEFAULT_PORT);
            info.setUser(props.get(NewFTPSiteVisualPanel1.SITE_USER).toString());
            info.setPassword(props.get(NewFTPSiteVisualPanel1.SITE_PWD).toString());
            //info.setName(props.get(NewFTPSiteVisualPanel1.SITE_NAME).toString());
            info.setRootFolder(props.get(NewFTPSiteVisualPanel1.SITE_INIT_FOLDER).toString());
            info.setPassiveMode((Boolean) props.get(NewFTPSiteVisualPanel1.SITE_PASSIVE_MODE));
            try {
                LogInfoList.getDefault().add(info);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
//              //  Repository.getDefault().addFileSystem(fs);
//                ((RootNode.RootNodeChildren)ftpx.getChildren()).add(fs);
//                try {
//                    DataObject find = DataObject.find(Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("FTPSites"));
//                    FileObject fld = find.getPrimaryFile();
//                    String baseName = info.getUser() + "@" + info.getHost();
//                    FileObject writeTo = fld.createData(baseName, "ser");
//                    FileLock lock = writeTo.lock();
//                    try {
//                        ObjectOutputStream str = new ObjectOutputStream(writeTo.getOutputStream(lock));
//                        try {
//                            str.writeObject(info);
//                        } finally {
//                            str.close();
//                        }
//                    } finally {
//                        lock.releaseLock();
//                    }
//                } catch (IOException ioe) {
//                    Exceptions.printStackTrace(ioe);
//                }
//            }
        }
    }

    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            panels = new WizardDescriptor.Panel[]{new NewFTPSiteWizardPanel1()};
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

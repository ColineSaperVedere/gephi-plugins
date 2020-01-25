/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.plugins.seadragon;

import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.gephi.desktop.io.export.spi.ExporterClassUI;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.utils.longtask.api.LongTaskErrorHandler;
import org.gephi.utils.longtask.api.LongTaskExecutor;
import org.gephi.utils.longtask.api.LongTaskListener;
import org.gephi.utils.longtask.spi.LongTask;
import org.netbeans.validation.api.ui.ValidationPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;
/**
 *
 * @author pcdev
 */
@ServiceProvider(service = ExporterClassUI.class)
public class SeadragonUI implements ExporterClassUI {

    private final LongTaskErrorHandler errorHandler;
    private boolean cancelled = true;
    private final ExporterSettings settings = new ExporterSettings();
    private final LongTaskListener longTaskListener;
    private String filePath;

    public SeadragonUI() {
        //Create a generic error handler called if the task raises an exception
        errorHandler = new LongTaskErrorHandler() {

            @Override
            public void fatalError(Throwable t) {
                cancelled = true;
                String message = t.getCause().getMessage();
                if (message == null || message.isEmpty()) {
                    message = t.getMessage();
                }
                NotifyDescriptor.Message msg = new NotifyDescriptor.Message(message, NotifyDescriptor.WARNING_MESSAGE);
                DialogDisplayer.getDefault().notify(msg);
            }
        };
        
        longTaskListener = new LongTaskListener() {

            @Override
            public void taskFinished(LongTask task) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        if (!cancelled) {
                            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

                            if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                                Object[] options = {"Open in browser", "OK"};
                                int n = JOptionPane.showOptionDialog(WindowManager.getDefault().getMainWindow(),
                                        "Seadragon export finished.",
                                        "Export finished",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.INFORMATION_MESSAGE,
                                        null, //do not use a custom Icon
                                        options, //the titles of buttons
                                        options[0]); //default button title
                                if (n == 0) {
                                    try {
                                        File f = new File(filePath + File.separator + "seadragon.html");
                                        if (f.exists()) {
                                            desktop.browse(f.toURI());
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            } else {
                                JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), "Seadragon export finished.", "Export finished", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                });
            }
        };
    }

    @Override
    public String getName() {
        return "Seadragon Web...";
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public void action() {
        final Seadragon exporter = new Seadragon();
        settings.load(exporter);

        SeadragonPanel panel = new SeadragonPanel();
        panel.setup(exporter);
        ValidationPanel validationPanel = SeadragonPanel.createValidationPanel(panel);
        final DialogDescriptor dd = new DialogDescriptor(validationPanel, "Seadragon Web Export");
        validationPanel.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                dd.setValid(!((ValidationPanel) e.getSource()).isProblem());
            }
        });
        Object result = DialogDisplayer.getDefault().notify(dd);
        if (result == NotifyDescriptor.OK_OPTION) {
            panel.unsetup(true);
            settings.save(exporter);
            filePath = exporter.getPath().getAbsolutePath();

            LongTaskExecutor executor = new LongTaskExecutor(true, "Seadragon");
            executor.setLongTaskListener(longTaskListener);
            executor.setDefaultErrorHandler(errorHandler);
            executor.execute(exporter, new Runnable() {

                @Override
                public void run() {
                    Workspace currentWorkspace = Lookup.getDefault().lookup(ProjectController.class).getCurrentWorkspace();
                    exporter.setWorkspace(currentWorkspace);
                    cancelled = !exporter.execute();
                }
            });
        } else {
            panel.unsetup(false);
        }
    }
    
    /*
    @Override
    public void action() {
        //Create exporter
        final Seadragon exporter = new Seadragon();

        //Create the settings panel
        SeadragonPanel settingPanel = new SeadragonPanel();
        
        settingPanel.setup(exporter);
        final DialogDescriptor dd = new DialogDescriptor(settingPanel, "Seadragon Web Export");
        Object result = DialogDisplayer.getDefault().notify(dd);
        if (result == NotifyDescriptor.OK_OPTION) {
            //This line will write the file path from the panel to the exporter's <code>setPath()<code> method.
            settingPanel.unsetup(true);

            //Create a new executor and execute
            LongTaskExecutor executor = new LongTaskExecutor(true, "Seadragon Web Export");
            executor.setDefaultErrorHandler(errorHandler);
            executor.execute(exporter, new Runnable() {

                @Override
                public void run() {
                    //Get the current workspace and set it to the exporter
                    Workspace currentWorkspace = Lookup.getDefault().lookup(ProjectController.class).getCurrentWorkspace();
                    exporter.setWorkspace(currentWorkspace);
                    
                    //Execute export
                    cancelled = !exporter.execute();
                    
                    //If not cancelled, write a status line message
                    if (!cancelled) {
                        StatusDisplayer.getDefault().setStatusText("Export to SQLite database completed");
                    }
                }
            });
        } else {
            settingPanel.unsetup(false);
        }
    }
*/
    
    

    private static class ExporterSettings {

        final String LAST_WIDTH = "SeadragonExporterUI_Last_Width";
        final String LAST_HEIGHT = "SeadragonExporterUI_Last_Height";
        final String LAST_MARGIN = "SeadragonExporterUI_Last_Margin";
        final String LAST_TILESIZE = "SeadragonExporterUI_Last_TileSize";
        private int overlap = 1;
        private int width = 8192;
        private int height = 6144;
        private int tileSize = 256;
        private int margin = 4;
          
        public void save(Seadragon exporter) {
            this.overlap = exporter.getOverlap();
            this.width = (int) exporter.getWidth();
            this.height = (int) exporter.getHeight();
            this.tileSize = exporter.getTileSize();
            this.margin = exporter.getMargin();

            NbPreferences.forModule(SeadragonUI.class).putInt(LAST_WIDTH, width);
            NbPreferences.forModule(SeadragonUI.class).putInt(LAST_HEIGHT, height);
            NbPreferences.forModule(SeadragonUI.class).putFloat(LAST_MARGIN, margin);
            NbPreferences.forModule(SeadragonUI.class).putInt(LAST_TILESIZE, tileSize);
        }

        public void load(Seadragon exporter) {
            width = NbPreferences.forModule(SeadragonUI.class).getInt(LAST_WIDTH, width);
            height = NbPreferences.forModule(SeadragonUI.class).getInt(LAST_HEIGHT, height);
            margin = NbPreferences.forModule(SeadragonUI.class).getInt(LAST_MARGIN, margin);
            tileSize = NbPreferences.forModule(SeadragonUI.class).getInt(LAST_TILESIZE, tileSize);

            exporter.setOverlap(overlap);
            exporter.setWidth(width);
            exporter.setHeight(height);
            exporter.setTileSize(tileSize);
            exporter.setMargin(margin);
        }
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.plugins.seadragon;

import org.gephi.desktop.io.export.spi.ExporterClassUI;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.utils.longtask.api.LongTaskErrorHandler;
import org.gephi.utils.longtask.api.LongTaskExecutor;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
/**
 *
 * @author pcdev
 */
@ServiceProvider(service = ExporterClassUI.class)
public class SeadragonUI implements ExporterClassUI {

    private final LongTaskErrorHandler errorHandler;
    private boolean cancelled = true;

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
    }

    @Override
    public String getName() {
        return "Seadragon Web Export...";
    }

    @Override
    public boolean isEnable() {
        return true;
    }

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
}
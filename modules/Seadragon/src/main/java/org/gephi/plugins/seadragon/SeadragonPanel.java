package org.gephi.plugins.seadragon;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import org.gephi.lib.validation.Multiple4NumberValidator;
import org.gephi.lib.validation.PositiveNumberValidator;
import org.netbeans.validation.api.builtin.Validators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationPanel;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

/**
 *
 * @author ch
 */
public class SeadragonPanel extends javax.swing.JPanel {

    final String LAST_PATH = "SeadragonExporterUI_Last_Path";
    private File path;
    private Seadragon exporter;

    public SeadragonPanel() {
        initComponents();

        browseButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(pathTextField.getText());
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(WindowManager.getDefault().getMainWindow());
                if (result == JFileChooser.APPROVE_OPTION) {
                    path = fileChooser.getSelectedFile();
                    pathTextField.setText(path.getAbsolutePath());
                }
            }
        });
    }
    
    
    public void setup(Seadragon exporter) {
        this.exporter = exporter;
        tileSizeTextField.setText(String.valueOf(exporter.getTileSize()));
        path = new File(NbPreferences.forModule(SeadragonUI.class).get(LAST_PATH, System.getProperty("user.home")));
        pathTextField.setText(path.getAbsolutePath());

        //PDF
        widthTextField.setText(String.valueOf((int) exporter.getWidth()));
        heightTextField.setText(String.valueOf((int) exporter.getHeight()));
        marginTextField.setText(String.valueOf((int) exporter.getMargin()));
    }

    public void unsetup(boolean update) {
        if (update) {
            try {
                path = new File(pathTextField.getText());
            } catch (Exception e) {
            }
            NbPreferences.forModule(SeadragonUI.class).put(LAST_PATH, path.getAbsolutePath());
            
            exporter.setPath(path);
            exporter.setWidth(Integer.parseInt(widthTextField.getText()));
            exporter.setHeight(Integer.parseInt(heightTextField.getText()));
            exporter.setTileSize(Integer.parseInt(tileSizeTextField.getText()));
            exporter.setMargin(Integer.parseInt(marginTextField.getText()));
        }
    }

    public static ValidationPanel createValidationPanel(SeadragonPanel innerPanel) {
        ValidationPanel validationPanel = new ValidationPanel();
        validationPanel.setInnerComponent(innerPanel);

        ValidationGroup group = validationPanel.getValidationGroup();
        group.add(innerPanel.tileSizeTextField, Validators.REQUIRE_NON_EMPTY_STRING,
                new Multiple4NumberValidator());

        group.add(innerPanel.widthTextField, Validators.REQUIRE_NON_EMPTY_STRING,
                new PositiveNumberValidator());
        group.add(innerPanel.heightTextField, Validators.REQUIRE_NON_EMPTY_STRING,
                new PositiveNumberValidator());

        group.add(innerPanel.pathTextField, Validators.FILE_MUST_BE_DIRECTORY);

        //Margins
        group.add(innerPanel.marginTextField, Validators.REQUIRE_NON_EMPTY_STRING,
                Validators.REQUIRE_VALID_NUMBER);

        return validationPanel;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelDirectory = new javax.swing.JLabel();
        browseButton = new javax.swing.JButton();
        labelTileSize = new javax.swing.JLabel();
        tileSizeTextField = new javax.swing.JTextField();
        labelPx3 = new javax.swing.JLabel();
        pathTextField = new javax.swing.JTextField();
        labelWidth = new javax.swing.JLabel();
        widthTextField = new javax.swing.JTextField();
        labelHeight = new javax.swing.JLabel();
        heightTextField = new javax.swing.JTextField();
        labelPx = new javax.swing.JLabel();
        labelPx2 = new javax.swing.JLabel();
        labelMargins = new javax.swing.JLabel();
        marginTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        labelDirectory.setText(org.openide.util.NbBundle.getMessage(SeadragonPanel.class, "SeadragonSettingsPanel.labelDirectory.text")); // NOI18N

        browseButton.setText(org.openide.util.NbBundle.getMessage(SeadragonPanel.class, "SeadragonSettingsPanel.browseButton.text")); // NOI18N

        labelTileSize.setText(org.openide.util.NbBundle.getMessage(SeadragonPanel.class, "SeadragonSettingsPanel.labelTileSize.text")); // NOI18N

        tileSizeTextField.setText(org.openide.util.NbBundle.getMessage(SeadragonPanel.class, "SeadragonSettingsPanel.tile size.text")); // NOI18N
        tileSizeTextField.setName("tile size"); // NOI18N

        labelPx3.setText(org.openide.util.NbBundle.getMessage(SeadragonPanel.class, "SeadragonSettingsPanel.labelPx3.text")); // NOI18N

        pathTextField.setText(org.openide.util.NbBundle.getMessage(SeadragonPanel.class, "SeadragonSettingsPanel.path.text")); // NOI18N
        pathTextField.setName("path"); // NOI18N

        labelWidth.setText(org.openide.util.NbBundle.getMessage(SeadragonPanel.class, "SeadragonSettingsPanel.labelWidth.text")); // NOI18N

        widthTextField.setText(org.openide.util.NbBundle.getMessage(SeadragonPanel.class, "SeadragonSettingsPanel.width.text")); // NOI18N
        widthTextField.setName("width"); // NOI18N
        widthTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                widthTextFieldActionPerformed(evt);
            }
        });

        labelHeight.setText(org.openide.util.NbBundle.getMessage(SeadragonPanel.class, "SeadragonSettingsPanel.labelHeight.text")); // NOI18N

        heightTextField.setText(org.openide.util.NbBundle.getMessage(SeadragonPanel.class, "SeadragonSettingsPanel.height.text")); // NOI18N
        heightTextField.setName("height"); // NOI18N

        labelPx.setText(org.openide.util.NbBundle.getMessage(SeadragonPanel.class, "SeadragonSettingsPanel.labelPx.text")); // NOI18N

        labelPx2.setText(org.openide.util.NbBundle.getMessage(SeadragonPanel.class, "SeadragonSettingsPanel.labelPx2.text")); // NOI18N

        labelMargins.setText(org.openide.util.NbBundle.getMessage(SeadragonPanel.class, "SeadragonSettingsPanel.labelMargins.text")); // NOI18N

        marginTextField.setText(org.openide.util.NbBundle.getMessage(SeadragonPanel.class, "SeadragonSettingsPanel.margin.text")); // NOI18N
        marginTextField.setName("margin"); // NOI18N

        jLabel1.setText(org.openide.util.NbBundle.getMessage(SeadragonPanel.class, "SeadragonSettingsPanel.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(pathTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelWidth)
                                    .addComponent(labelHeight))
                                .addGap(29, 29, 29)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(widthTextField)
                                    .addComponent(heightTextField))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(labelPx)
                                        .addGap(47, 47, 47)
                                        .addComponent(labelTileSize)
                                        .addGap(18, 18, 18)
                                        .addComponent(tileSizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(4, 4, 4)
                                        .addComponent(labelPx3))
                                    .addComponent(labelPx2)
                                    .addComponent(jLabel1)))
                            .addComponent(labelDirectory)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(labelMargins)
                                .addGap(18, 18, 18)
                                .addComponent(marginTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(113, 113, 113)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(86, 86, 86)
                .addComponent(labelDirectory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelWidth)
                    .addComponent(widthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelPx)
                    .addComponent(labelTileSize)
                    .addComponent(tileSizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelPx3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelHeight)
                    .addComponent(heightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelPx2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelMargins)
                    .addComponent(marginTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void widthTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_widthTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_widthTextFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JTextField heightTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel labelDirectory;
    private javax.swing.JLabel labelHeight;
    private javax.swing.JLabel labelMargins;
    private javax.swing.JLabel labelPx;
    private javax.swing.JLabel labelPx2;
    private javax.swing.JLabel labelPx3;
    private javax.swing.JLabel labelTileSize;
    private javax.swing.JLabel labelWidth;
    private javax.swing.JTextField marginTextField;
    private javax.swing.JTextField pathTextField;
    private javax.swing.JTextField tileSizeTextField;
    private javax.swing.JTextField widthTextField;
    // End of variables declaration//GEN-END:variables
}
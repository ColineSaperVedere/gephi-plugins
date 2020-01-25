/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.plugins.seadragon;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import org.gephi.io.exporter.spi.ByteExporter;
import org.gephi.io.exporter.spi.VectorExporter;
import org.gephi.preview.api.G2DTarget;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewProperties;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.api.RenderTarget;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

/**
 *
 * @author pcdev
 */
public class Seadragon implements VectorExporter, ByteExporter {

    private Workspace workspace;
    private OutputStream stream;
    private int width = 1024;
    private int height = 1024;
    private G2DTarget target;

    @Override
    public boolean execute() {

        PreviewController controller = Lookup.getDefault().lookup(PreviewController.class);
        controller.getModel(workspace).getProperties().putValue(PreviewProperty.VISIBILITY_RATIO, 1.0);
        controller.refreshPreview(workspace);

        PreviewProperties props = controller.getModel(workspace).getProperties();
        props.putValue("width", width);
        props.putValue("height", height);
        target = (G2DTarget) controller.getRenderTarget(RenderTarget.G2D_TARGET, workspace);

        try {
            target.refresh();

            Graphics2D pg2 = (Graphics2D) target.getGraphics();

            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            pg2.drawImage(img, null, 0, 0);
            ImageIO.write(img, "jpg", stream);
            stream.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public void setOutputStream(OutputStream stream) {
        this.stream = stream;
    }
}
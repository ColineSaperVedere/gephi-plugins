/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.plugins.seadragon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.gephi.io.exporter.spi.ByteExporter;
import org.gephi.io.exporter.spi.VectorExporter;
import org.gephi.preview.api.G2DTarget;
import org.gephi.io.exporter.preview.PNGExporter;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewProperties;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.api.RenderTarget;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;
import org.gephi.io.exporter.spi.Exporter;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.w3c.dom.Element;

/**
 *
 * @author pcdev
 */
public class Seadragon implements Exporter, LongTask {

    //Const
    private static final String XML_FILE = "map.xml";
    private static final String PATH_MAP = "map";
    private static final String PATH_FILES = "_files";
    //Architecture
    private Workspace workspace;
    private ProgressTicket progress;
    private boolean cancel = false;
    private PNGExporter pngExporter = new PNGExporter();
    private TileRenderer tileRenderer;
    //Settings
    private int width;
    private int height;
    private int margin;
    private File path;
    private int overlap = 1;
    private int tileSize = 256;

    @Override
    public boolean execute() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        
        // Start progress monitoring
        Progress.start(progress);
        Progress.setDisplayName(progress, "Export Seadragon");
        
        // Render workspace using PreviewController
        PreviewController controller = Lookup.getDefault().lookup(PreviewController.class);
        controller.getModel(workspace).getProperties().putValue(PreviewProperty.VISIBILITY_RATIO, 1.0);
        controller.refreshPreview(workspace);
        
        PreviewProperties props = controller.getModel(workspace).getProperties();
        props.putValue("width", width);
        props.putValue("height", height);
        props.putValue(PreviewProperty.BACKGROUND_COLOR, Color.BLACK);
        props.putValue(PreviewProperty.MARGIN, new Float((float) margin));
        G2DTarget target = (G2DTarget) controller.getRenderTarget(RenderTarget.G2D_TARGET, workspace);
        
        target.refresh();
        
        Progress.switchToIndeterminate(progress);
        
        // Set image for export
        // TODO check for alpha
        /*
        Graphics2D pg2 = (Graphics2D) target.getGraphics();
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D imgG2D = img.createGraphics();
        imgG2D.
        img.setRGB(0, 0, width, height, pg2.pixels, 0, width);
        */
        
        //PGraphics2D pg2 = (PGraphics2D) target.getGraphics();
        
        BufferedImage img = (BufferedImage) target.getImage();
        
        try {
            export(img);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        
        createXML();
        exportOtherFiles();
        
        Progress.finish(progress);
        
        return !cancel;
    }
    
    public void export(BufferedImage img) throws Exception {
        delete(new File(path, PATH_MAP));
        File folder = new File(path, PATH_MAP);
        folder.mkdir();
        folder = new File(folder, PATH_MAP + PATH_FILES);
        folder.mkdir();
        
        int numLevels = (int) Math.ceil(Math.log(Math.max(img.getWidth(), img.getHeight())) / Math.log(2.));
        int w = img.getWidth();
        int h = img.getHeight();
        
        //Calculate tasks count
        int tasks = 0;
        for (int level = numLevels; level >= 0; level--) {
            float levelScale = 1f / (1 << (numLevels - level));
            tasks += (int) Math.ceil(levelScale * w / tileSize) * (int) Math.ceil(levelScale * h / tileSize);
        }

        Progress.switchToDeterminate(progress, tasks);
        
        //Tile renderer
        tileRenderer = new TileRenderer(folder, tileSize, overlap);
        tileRenderer.setProgressTicket(progress);
        for (int level = numLevels; level >= 0 && !cancel; level--) {
            File levelFolder = new File(folder, "" + (level));
            levelFolder.mkdir();
            float levelScale = 1f / (1 << (numLevels - level));
            tileRenderer.writeLevel(img, levelScale, level);
        }
        
        tileRenderer = null;
    }
    
    
    public void createXML() {
        File file = new File(path + File.separator + PATH_MAP + File.separator + XML_FILE);
        org.w3c.dom.Document document = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            document = documentBuilder.newDocument();
            document.setXmlVersion("1.0");
            document.setXmlStandalone(true);
        } catch (Exception ex) {
            throw new RuntimeException("Can't create XML file", ex);
        }
        
        Element imageElement = document.createElement("Image");
        imageElement.setAttribute("TileSize", String.valueOf(tileSize));
        imageElement.setAttribute("Overlap", String.valueOf(overlap));
        imageElement.setAttribute("Format", "png");
        imageElement.setAttribute("ServerFormat", "Default");
        imageElement.setAttribute("xmlns", "http://schemas.microsoft.com/deepzoom/2009");
        
        Element sizeElement = document.createElement("Size");
        sizeElement.setAttribute("Width", String.valueOf(width));
        sizeElement.setAttribute("Height", String.valueOf(height));
        imageElement.appendChild(sizeElement);
        document.appendChild(imageElement);
        
        try {
            Source source = new DOMSource(document);
            Result result = new StreamResult(file);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(source, result);
        } catch (Exception ex) {
            throw new RuntimeException("Can't write XML file", ex);
        }
    }
    
    private void exportOtherFiles() {
        try {
            copyFromJar("seadragon.html", path);
            copyFromJar("img/fullpage_grouphover.png", path);
            copyFromJar("img/fullpage_hover.png", path);
            copyFromJar("img/fullpage_pressed.png", path);
            copyFromJar("img/fullpage_rest.png", path);
            copyFromJar("img/home_grouphover.png", path);
            copyFromJar("img/home_hover.png", path);
            copyFromJar("img/home_pressed.png", path);
            copyFromJar("img/home_rest.png", path);
            copyFromJar("img/zoomin_grouphover.png", path);
            copyFromJar("img/zoomin_hover.png", path);
            copyFromJar("img/zoomin_pressed.png", path);
            copyFromJar("img/zoomin_rest.png", path);
            copyFromJar("img/zoomout_grouphover.png", path);
            copyFromJar("img/zoomout_hover.png", path);
            copyFromJar("img/zoomout_pressed.png", path);
            copyFromJar("img/zoomout_rest.png", path);
            copyFromJar("js/seadragon-min.js", path);
        } catch (Exception ex) {
            Logger.getLogger(Seadragon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void copyFromJar(String source, File folder) throws Exception {
        InputStream is = getClass().getResourceAsStream("/org/gephi/plugins/seadragon/resources/" + source);
        File file = new File(folder + (folder.getPath().endsWith(File.separator) ? "" : File.separator) + source);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        OutputStream os = new FileOutputStream(file);
        byte[] buffer = new byte[4096];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        os.close();
        is.close();
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
    
    public int getTileSize() {
        return tileSize;
    }
    
    public void setTileSize(int tileSize) {
        this.tileSize = tileSize;
    }
    
    
    public int getMargin() {
        return margin;
    }
    
    public void setMargin(int margin) {
        this.margin = margin;
    }
    
    public int getOverlap() {
        return overlap;
    }
    
    public void setOverlap(int overlap) {
        this.overlap = overlap;
    }
    

    @Override
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }
    
    public File getPath() {
        return path;
    }

    public void setPath(File path) {
        this.path = path;
    }

    @Override
    public boolean cancel() {
        cancel = true;
        return true;
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progress = progressTicket;
    }
    
    private void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                delete(c);
            }
        }
        if (f.exists() && !f.delete()) {
            throw new IOException("Failed to delete file: " + f);
        }
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.plugins.seadragon;


import org.gephi.io.exporter.api.FileType;
import org.gephi.io.exporter.spi.VectorExporter;
import org.gephi.io.exporter.spi.VectorFileExporterBuilder;
import org.openide.util.lookup.ServiceProvider;
/**
 *
 * @author pcdev
 */
@ServiceProvider(service = VectorFileExporterBuilder.class)
public class SeadragonBuilder implements VectorFileExporterBuilder {
    
    @Override
    public VectorExporter buildExporter() {
        return new Seadragon();
    }

    @Override
    public FileType[] getFileTypes() {
        return new FileType[]{new FileType(".jpg", "JPG Files")};
    }

    @Override
    public String getName() {
        return "jpg";
    }
}
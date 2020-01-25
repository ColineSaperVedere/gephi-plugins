/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.plugins.seadragon;


import org.gephi.io.exporter.api.FileType;
import org.gephi.io.exporter.spi.Exporter;
import org.gephi.io.exporter.spi.FileExporterBuilder;
import org.openide.util.lookup.ServiceProvider;
/**
 *
 * @author pcdev
 */
@ServiceProvider(service = FileExporterBuilder.class)
public class SeadragonBuilder implements FileExporterBuilder {
    
    @Override
    public Exporter buildExporter() {
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
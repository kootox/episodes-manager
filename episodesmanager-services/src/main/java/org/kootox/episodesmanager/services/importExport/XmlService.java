package org.kootox.episodesmanager.services.importExport;

import java.io.File;
import org.kootox.episodesmanager.services.EpisodesManagerService;
import org.kootox.episodesmanager.services.ServiceContext;

/**
 * @author jcouteau <couteau@codelutin.com>
 */
public class XmlService implements EpisodesManagerService {

    protected ServiceContext serviceContext;

    public void setServiceContext(ServiceContext serviceContext) {
        this.serviceContext = serviceContext;
    }

    /**
     * Method to import lists of episodes from a XML file.
     * @param file The file to import from.
     */
    public void importFromXML(File file) {
        XMLReader reader = new XMLReader(serviceContext);

        reader.readFromXML(file);
    }

    /**
     * Method to export database to XML
     * @param file The file in which to export.
     */
    public void exportToXML(File file) {
        XMLWriter writer = new XMLWriter(serviceContext);

        writer.exportToXML(file);
    }
}

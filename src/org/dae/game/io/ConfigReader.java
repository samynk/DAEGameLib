package org.dae.game.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.dae.game.GameConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Reads the configuration of the project.
 *
 * @author Koen Samyn
 */
public class ConfigReader {

    public static GameConfig readConfigFile(File configFile) {
        GameConfig config = new GameConfig();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;

            db = dbf.newDocumentBuilder();
            InputStream is = new BufferedInputStream(new FileInputStream(configFile));
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            String name = root.getAttribute("name");

            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); ++i) {
                Node n = children.item(i);
                if ("assetfolder".equals(n.getNodeName())) {
                    // get the cdata section.
                    //n.get
                    String file = readCData(n);
                    config.addAssetFolder(file);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(ConfigReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return config;
    }

    public static String readCData(org.w3c.dom.Node node) {
        NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            org.w3c.dom.Node child = nl.item(i);
            if (child.getNodeType() == org.w3c.dom.Node.CDATA_SECTION_NODE) {
                return child.getNodeValue();
            }
        }
        return null;
    }
}

package com.netaporter.test.utils.cucumber.formatters;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

/**
 * Created by a.makarenko on 04/12/2014.
 * **
 * Created by a.makarenko on 02/12/2014.
 * Merges cucumber Junit report files (XML)
 */

public class JunitReportMerger{
    /**
     * Overwrites the results of the test from origFile with
     * result of the same test from overrideFile
     * The report files' extensions renamed to 'bak' and the result file gets
     * the name of the original file
     * This is used for rerun formatter to combine the results from rerun with the initial
     * run report.
     * */
    public static void replace(String origFile, String overrideFile) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document orig = documentBuilder.parse(origFile);
            Document override = documentBuilder.parse(overrideFile);
            Node origStats = orig.getElementsByTagName("testsuite").item(0);
            Node overrideStats = override.getElementsByTagName("testsuite").item(0);
            //change the number of failed tests in the original file to those from rerun file
            origStats.getAttributes().getNamedItem("failures").setNodeValue(overrideStats.getAttributes().getNamedItem("failures").getNodeValue());
            NodeList origTests = orig.getElementsByTagName("testcase");
            NodeList overrideTests = override.getElementsByTagName("testcase");

            for (int i = 0; i <overrideTests.getLength(); i++) {
                replaceDuplicateTests(origTests, overrideTests.item(i));
            }
            rename(origFile);
            rename(overrideFile);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(orig);
            StreamResult result = new StreamResult(new File(origFile));
            transformer.transform(source, result);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (SAXException sae) {
            sae.printStackTrace();
        }
    }
    /**
     * Replace the test result in the original report to the one from rerun report
     * */
    private static void replaceDuplicateTests(NodeList origTests, Node n) {
        for (int i=0; i<origTests.getLength(); i++) {
            if (isSameTest(origTests.item(i), n)) {
                Node rn = origTests.item(i);
                Node imp = rn.getOwnerDocument().importNode(n, true);
                rn.getParentNode().replaceChild(imp, rn);
                break;
            }
        }
    }
    /**
     * Check if the test results are for the same test
     * */
    private static boolean isSameTest(Node node, Node n) {
          return node.getAttributes().getNamedItem("classname").getNodeValue().equals(n.getAttributes().getNamedItem("classname").getNodeValue())&&
                  node.getAttributes().getNamedItem("name").getNodeValue().equals(n.getAttributes().getNamedItem("name").getNodeValue());
        }
    /**
     * Rename the original file to backup (.bak)
     * */
    static boolean rename(String fileName){
        File file = new File(fileName);
        // File (or directory) with new name
        File file2 = new File(fileName.replace(".xml", ".bak"));

        // Rename file (or directory)
        boolean success = file.renameTo(file2);
        if (!success) {
            return false;
        }
        else return true;
    }
}


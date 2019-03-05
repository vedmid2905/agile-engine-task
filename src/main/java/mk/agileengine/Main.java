package mk.agileengine;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.SAXException;

public class Main {


    public static void main(String argv[]) throws Exception {

        argv = setDefaultValueIfNeed(argv);
        String pathToBaseFile = argv[0];
        String pathToChangedFile = argv[1];
        String elementId = argv[2];
        List<String[]> comparePropertyList = setCompareProperty();
        // System.out.println("_________________" + "base element" + "____________________________");
        Map<String, String> basePropertyMap = getInformationFromBaseFileByIdElement(pathToBaseFile, elementId);

        // System.out.println("_________________" + "end base element" + "____________________________");
        List<Map<String, String>> searchElementsResultlist =
                getInformationFromChangedFileByTag(pathToChangedFile, basePropertyMap.get("nodeName"));

        for (String[] comparePropertyArray : comparePropertyList) {
            //   System.out.println("_________________"+String.join(",",comparePropertyArray)+"____________________________");
            for (Map<String, String> mapFromSecondFile : searchElementsResultlist) {
                if (compareElementForProperty(comparePropertyArray, basePropertyMap, mapFromSecondFile)) return;

            }
        }

        // fixToCase4;
        List<Map<String, String>> searchElementsResultlistfIX =
                getInformationFromChangedFileByTag(pathToChangedFile, "button");


        for (String[] comparePropertyArray : comparePropertyList) {
            //    System.out.println("_________________" + String.join(",", comparePropertyArray) + "____________________________");
            for (Map<String, String> mapFromSecondFile : searchElementsResultlistfIX) {
                if (compareElementForProperty(comparePropertyArray, basePropertyMap, mapFromSecondFile)) return;

            }
        }


    }

    private static String[] setDefaultValueIfNeed(String[] argv) {

        if (argv.length == 3 && argv[0] != null && argv[1] != null && argv[2] != null) return argv;
        else if (argv.length == 2) {
            String[] result = new String[3];
            result[0] = argv[0];
            result[1] = argv[1];
            result[2] = "make-everything-ok-button";
            return result;
        } else {
            //for my test
            String[] result = new String[3];
            result[0] = "src/main/resources/sample-0-origin.html";
            //sample-1-evil-gemini.html
            //sample-2-container-and-clone.html
            //sample-3-the-escape.html
            //sample-4-the-mash.html
            result[1] = "src/main/resources/sample-4-the-mash.html";
            result[2] = "make-everything-ok-button";
            return result;
        }
    }

    private static List<String[]> setCompareProperty() {
        List<String[]> comparePropertyList = new ArrayList<>();
        String[] idCompare = {"id"};
        comparePropertyList.add(idCompare);
        String[] roadTextCompare = {"road", "text1"};
        comparePropertyList.add(roadTextCompare);
        String[] roadOnclickCompare = {"road", "onclick"};
        comparePropertyList.add(roadOnclickCompare);
        String[] textClassCompare = {"road", "text", "class"};
        comparePropertyList.add(textClassCompare);
        return comparePropertyList;
    }

    private static boolean compareElementForProperty
            (String[] comparePropertyArray, Map basePropertyMap,
             Map<String, String> mapFromSecondFile) {

        // System.out.println("_________________check____________________________");

        //  checkMap(mapFromSecondFile);
        for (String compareField : comparePropertyArray) {

            if (!(basePropertyMap.get(compareField).equals(mapFromSecondFile.get(compareField)))) {
                //         System.out.println("_________________false____________________________");
                return false;
            }
        }

        //   System.out.println("_________________true___________________________");
        //   System.out.println("_________________" + String.join(",", comparePropertyArray) + "____________________________");
        showDiff(basePropertyMap, mapFromSecondFile);

        return true;
    }

    private static void showDiff(Map<String, String> basePropertyMap, Map<String, String> mapForDff) {

        System.out.println("_________________show diff___________________________");
        System.out.println("element path - " + mapForDff.get("road") + mapForDff.get("nodeName"));

        for (Map.Entry<String, String> entry : basePropertyMap.entrySet()) {

            if (entry.getValue().equals(mapForDff.get(entry.getKey()))) {
                //nothing
            } else {
                System.out.println("-- " + entry);
                System.out.println("++ " + entry.getKey() + "=" + mapForDff.get(entry.getKey()));
            }
        }
        Set<String> newProperties = mapForDff.keySet();
        newProperties.removeAll(basePropertyMap.keySet());
        if (!newProperties.isEmpty()) {
            for (String property : newProperties) {
                System.out.println("++ " + property + "=" + mapForDff.get(property));

            }
        }

    }

    private static List<Map<String, String>> getInformationFromChangedFileByTag
            (String pathToChangedFile, String nodeName) throws ParserConfigurationException, IOException, SAXException {

        List<Map<String, String>> result = new ArrayList<>();
        File xmlFile = new File(pathToChangedFile);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);

        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName(nodeName);
        for (int i = 0; i < nList.getLength(); i++) {
            Node tempNode = nList.item(i);
            result.add(getElementAttributesAndValueMap((Element) tempNode));
        }


        return result;
    }

    private static Map<String, String> getInformationFromBaseFileByIdElement(String pathToBaseFile,
                                                                             String elementId) throws Exception {
        String xPathExpressionBluePrint = "//*[@id='%s']";
        //"//*[@id='make-everything-ok-button']";
        String xPathExpressionToElement = String.format(xPathExpressionBluePrint, elementId);
        //  System.out.println(xPathExpressionToElement);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(pathToBaseFile));
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node foundNode = (Node) xpath.evaluate(xPathExpressionToElement, document, XPathConstants.NODE);
        Element element = (Element) foundNode;
        if (element == null) {
            throw new Exception(String.format("Cannot resolve element with ID %s ", elementId));
        }
        return getElementAttributesAndValueMap(element);

    }

    private static Map<String, String> getElementAttributesAndValueMap(Element element) {

        Map<String, String> map = new HashMap<>();
        map.put("nodeName", element.getNodeName());
        map.put("road", getPathToElement(element));
        map.put("text", element.getTextContent());
        map.put("text1", element.getTextContent().trim());
        NamedNodeMap namedMap = element.getAttributes();
        for (int j = 0; j < namedMap.getLength(); j++) {
            Node node = namedMap.item(j);
            map.put(node.getNodeName(), node.getTextContent());
        }

        // checkMap(map);
        return map;
    }

    private static void checkMap(Map<String, String> map) {

        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry);
        }
        System.out.println();
    }

    private static String getPathToElement(Node node) {
        List<String> list = new ArrayList<>();
        // list.add(node.getNodeName());
        while (node.getParentNode() != null) {
            list.add(node.getParentNode().getNodeName());
            node = node.getParentNode();
        }
        Collections.reverse(list);
        String result = list.stream().map(e -> e.toString() + "|").reduce("", String::concat);
        //  System.out.println(result);
        return result;
    }


}

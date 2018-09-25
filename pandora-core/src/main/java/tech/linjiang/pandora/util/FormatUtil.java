package tech.linjiang.pandora.util;

import android.text.TextUtils;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import okhttp3.Headers;

/**
 * Created by linjiang on 2018/6/21.
 */

public class FormatUtil {


    public static String formatHeaders(Headers headers) {
        JSONArray array = new JSONArray();
        for (int i = 0, size = headers.size(); i < size; i++) {
            array.put(new JSONArray().put(headers.name(i)).put(headers.value(i)));
        }
        if (array.length() > 0) {
            return array.toString();
        } else {
            return null;
        }
    }

    public static List<Pair<String, String>> parseHeaders(String headers) {
        List<Pair<String, String>> headerList = new ArrayList<>();
        if (!TextUtils.isEmpty(headers)) {
            try {
                JSONArray array = new JSONArray(headers);
                for (int i = 0; i < array.length(); i++) {
                    Pair<String, String> header = new Pair<>(
                            array.getJSONArray(i).getString(0), array.getJSONArray(i).getString(1));
                    headerList.add(header);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return headerList;
    }

    public static List<String> printJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        int tabNum = 0;
        List<String> result = new ArrayList<>();
        StringBuilder jsonFormat = new StringBuilder();
        int length = json.length();

        char last = 0;
        for (int i = 0; i < length; i++) {
            char c = json.charAt(i);
            if (c == '{') {
                tabNum++;
                jsonFormat.append(c);
                result.add(jsonFormat.toString());
                jsonFormat = new StringBuilder();
                jsonFormat.append(addTab(tabNum));
            } else if (c == '}') {
                tabNum--;
                result.add(jsonFormat.toString());
                jsonFormat = new StringBuilder();
                jsonFormat.append(addTab(tabNum));
                jsonFormat.append(c);
            } else if (c == ',') {
                jsonFormat.append(c);
                result.add(jsonFormat.toString());
                jsonFormat = new StringBuilder();
                jsonFormat.append(addTab(tabNum));
            } else if (c == ':') {
                jsonFormat.append(c).append(" ");
            } else if (c == '[') {
                tabNum++;
                char next = json.charAt(i + 1);
                if (next == ']') {
                    jsonFormat.append(c);
                } else {
                    jsonFormat.append(c);
                    result.add(jsonFormat.toString());
                    jsonFormat = new StringBuilder();
                    jsonFormat.append(addTab(tabNum));
                }
            } else if (c == ']') {
                tabNum--;
                if (last == '[') {
                    jsonFormat.append(c);
                } else {
                    result.add(jsonFormat.toString());
                    jsonFormat = new StringBuilder();
                    jsonFormat.append(addTab(tabNum)).append(c);
                }
            } else {
                jsonFormat.append(c);
            }
            last = c;
        }
        result.add(jsonFormat.toString());
        return result;
    }

    private static String addTab(int tabNum) {
        StringBuilder sbTab = new StringBuilder();
        for (int i = 0; i < tabNum; i++) {
            sbTab.append("        "/* 8 space */);
        }
        return sbTab.toString();
    }


    public static List<String> printXml(String xml) {
        try {
            xml = toPrettyString(xml);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        return Collections.singletonList(xml);
        String[] split = xml.split("\n");
        return Arrays.asList(split);
    }


    private static String toPrettyString(String xml) {
        try {
            // Turn xml string into a document
            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));

            // Remove whitespaces outside tags
            document.normalize();
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']",
                    document,
                    XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); ++i) {
                Node node = nodeList.item(i);
                node.getParentNode().removeChild(node);
            }

            // Setup pretty print options
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 8);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            // Return pretty print xml string
            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

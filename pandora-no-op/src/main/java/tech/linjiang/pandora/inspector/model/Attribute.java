package tech.linjiang.pandora.inspector.model;

/**
 * Created by linjiang on 15/06/2018.
 */

public class Attribute {

    // For classification
    public String category;
    public String attrName;
    public String attrValue;


    public Attribute(String attrName, String attrValue) {
    }

    public Attribute(String attrName, String attrValue, int attrType) {
    }

    public int attrType;

}

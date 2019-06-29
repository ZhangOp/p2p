package com.bjpowernode;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws DocumentException {
        //获取xml字符串
        String xmlString = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><bookstore><book><title lang=\"chinese\">哈利波特</title><price>29.99</price></book><book><title lang=\"eng\">Learning XML</title><price>39.95</price></book></bookstore>";
//        将字符串转成document对象
        Document document = DocumentHelper.parseText(xmlString);
        //获取title标签下lang属性为chinese的标签
        Node node = document.selectSingleNode("//title[@lang='chinese']");
        String text = node.getText();
        System.out.println("lang节点属性为chinese的值是------------>>"+text);

    }
}

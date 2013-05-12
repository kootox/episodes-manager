/*
 * #%L
 * episodesmanager-services
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2009 - 2010 Jean Couteau
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package org.kootox.episodesmanager.services.databases;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * User: couteau
 * Date: 21 mars 2010
 */
public class XMLParser {

    /** Logger */
    private final static Log log = LogFactory.getLog(XMLParser.class);

    /** Private default constructor to avoid instantiation **/
    private XMLParser(){}
    
    public static String getStringValue(Element element, String tagName){
        String value = null;
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (null != nodeList.item(0)){
            NodeList textNodeList = nodeList.item(0).getChildNodes();
            if (null != textNodeList.item(0) && null != textNodeList.item(0).getNodeValue()) {
                value = StringEscapeUtils.unescapeHtml(textNodeList.item(0).getNodeValue().trim());
            }
        }
        return value;
    }

    public static Date getDateValue(Element element, String tagName, DateFormat format) {
        Date date = null;
        NodeList nodeList = element.getElementsByTagName(tagName);

        if (null != nodeList && null != nodeList.item(0)) {
            NodeList textNodeList = nodeList.item(0).getChildNodes();
            if (null != textNodeList.item(0)) {
                String dateValue = textNodeList.item(0).getNodeValue().trim();
                try {
                    date = format.parse(dateValue);
                } catch (ParseException pe) {
                    log.debug("Could not parse date : " + dateValue);
                }
            }
        }

        return date;
    }

    public static Integer getIntegerValue(Element element, String tagName) {
        Integer returnInt = null;

        NodeList runtimeList = element.getElementsByTagName(tagName);
        if (null != runtimeList.item(0)) {
            NodeList textRuntimeList = runtimeList.item(0).getChildNodes();
            String showRuntime = textRuntimeList.item(0).getNodeValue().trim();

            if (!"null".equals(showRuntime)) {
                returnInt = Integer.parseInt(showRuntime);
            }
        }
        return returnInt;
    }

    public static Boolean getBooleanValue(Element element, String tagName) {
        return getBooleanValue(element, tagName, "true");
    }

    public static Boolean getBooleanValue(Element element, String tagName, String trueValue) {

        Boolean returnBoolean = null;

        NodeList statusList = element.getElementsByTagName(tagName);
        NodeList textStatusList = statusList.item(0).getChildNodes();
        String textStatus = textStatusList.item(0).getNodeValue().trim();

        if (!"null".equals(textStatus)) {
            returnBoolean = trueValue.equals(textStatus);
        }

        return returnBoolean;
    }

    public static List<String> getStringListValue(Element element, String tagName, String separator) {
        List<String> list = new ArrayList<String>();
        NodeList nodeList = element.getElementsByTagName(tagName);
        Element nodeElement = (Element) nodeList.item(0);
        if (null != nodeElement) {
            NodeList elementsList = nodeElement.getChildNodes();
            String values = elementsList.item(0).getNodeValue().trim();
            String[] valuesArray = values.split(separator);
            Collections.addAll(list, valuesArray);
            list.remove("");
        }
        return list;

    }
}

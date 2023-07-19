/*
 *  Copyright (C) 2022 github.com/REAndroid
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.reandroid.arsc.coder.xml;

import com.reandroid.arsc.coder.XmlSanitizer;
import com.reandroid.arsc.item.StringItem;
import com.reandroid.arsc.item.TableString;
import com.reandroid.xml.StyleDocument;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public class XmlDecodeUtil {

    public static String toXMLTagName(String typeName){
        // e.g ^attr-private
        if(typeName.length()>0 && typeName.charAt(0)=='^'){
            typeName = typeName.substring(1);
        }
        return typeName;
    }
    public static void text(XmlSerializer serializer, StringItem stringItem) throws IOException {
        if(stringItem.hasStyle() && stringItem instanceof TableString) {
            TableString tableString = (TableString) stringItem;
            StyleDocument document = tableString.getStyleDocument();
            if(document != null){
                document.serialize(serializer);
                return;
            }
        }
        serializer.text(XmlSanitizer.escapeSpecialCharacter(stringItem.get()));
    }
    public static void attribute(XmlSerializer serializer, String name, StringItem stringItem) throws IOException {
        String text = stringItem.getXml();
        text = XmlSanitizer.escapeSpecialCharacter(text);
        serializer.attribute(null, name, text);
    }
    public static void rootIndent(XmlSerializer serializer) throws IOException {
        writeTagIndent(serializer, INDENT_ROOT);
    }
    public static void bagIndent(XmlSerializer serializer) throws IOException {
        writeTagIndent(serializer, INDENT_BAG);
    }
    public static void entryIndent(XmlSerializer serializer) throws IOException {
        writeTagIndent(serializer, INDENT_ENTRY);
    }
    public static void writeTagIndent(XmlSerializer serializer, int level) throws IOException {
        if(level < 0){
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append('\n');
        for(int i = 0; i < level; i++){
            builder.append(' ');
        }
        serializer.text(builder.toString());
    }
    static final int INDENT_ROOT = 0;
    static final int INDENT_ENTRY = 2;
    static final int INDENT_BAG = 4;
}

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
package com.reandroid.apk.xmldecoder;

import com.reandroid.apk.XmlHelper;
import com.reandroid.arsc.array.CompoundItemArray;
import com.reandroid.arsc.value.*;
import com.reandroid.arsc.value.attribute.AttributeBag;

import java.io.IOException;

class BagDecoderAttr<OUTPUT> extends BagDecoder<OUTPUT>{
    public BagDecoderAttr(){
        super();
    }

    @Override
    public OUTPUT decode(ResTableMapEntry mapEntry, EntryWriter<OUTPUT> writer) throws IOException {
        Entry entry = mapEntry.getParentEntry();
        String tag = XmlHelper.toXMLTagName(entry.getTypeName());
        writer.writeTagIndent(INDENT_ENTRY);
        writer.startTag(tag);
        writer.attribute("name", entry.getName());
        writeParentAttributes(writer, mapEntry.getValue());
        ResValueMap formatsMap = mapEntry.getByType(AttributeType.FORMATS);

        AttributeDataFormat bagType = AttributeDataFormat.typeOfBag(formatsMap.getData());

        ResValueMap[] bagItems = mapEntry.listResValueMap();

        boolean hasBags = false;

        for(int i = 0; i < bagItems.length; i++){
            ResValueMap valueMap = bagItems[i];
            AttributeType attributeType = valueMap.getAttributeType();
            if(attributeType != null){
                continue;
            }
            writer.writeTagIndent(INDENT_BAG);
            writer.startTag(bagType.getName());
            writer.attribute("name", valueMap.decodeName());
            writer.text(valueMap.decodeValue());

            writer.endTag(bagType.getName());
            hasBags = true;
        }
        if(hasBags){
            writer.writeTagIndent(INDENT_ENTRY);
        }
        return writer.endTag(tag);
    }

    private void writeParentAttributes(EntryWriter<OUTPUT> writer, CompoundItemArray<? extends ResValueMap> itemArray) throws IOException {
        for(ResValueMap valueMap : itemArray.getChildes()){
            AttributeType type = valueMap.getAttributeType();
            if(type == null){
                continue;
            }
            String value;
            if(type == AttributeType.FORMATS){
                value = AttributeDataFormat.toString(
                        AttributeDataFormat.decodeValueTypes(valueMap.getData()));
            }else {
                value = Integer.toString(valueMap.getData());
            }
            if(value == null){
                continue;
            }
            writer.attribute(type.getName(), value);
        }
    }
    @Override
    public boolean canDecode(ResTableMapEntry mapEntry) {
        return AttributeBag.isAttribute(mapEntry);
    }
}

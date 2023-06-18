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
import com.reandroid.arsc.chunk.PackageBlock;
import com.reandroid.arsc.value.*;

import java.io.IOException;

class BagDecoderPlural<OUTPUT> extends BagDecoder<OUTPUT>{
    public BagDecoderPlural() {
        super();
    }

    @Override
    public OUTPUT decode(ResTableMapEntry mapEntry, EntryWriter<OUTPUT> writer) throws IOException {
        Entry entry = mapEntry.getParentEntry();
        String tag = XmlHelper.toXMLTagName(entry.getTypeName());
        writer.writeTagIndent(INDENT_ENTRY);
        writer.startTag(tag);
        writer.attribute("name", entry.getName());

        ResValueMap[] resValueMaps = mapEntry.listResValueMap();
        PackageBlock packageBlock = entry.getPackageBlock();

        boolean hasBags = false;

        for(int i=0; i < resValueMaps.length; i++){
            ResValueMap valueMap = resValueMaps[i];
            String childTag = "item";
            writer.writeTagIndent(INDENT_BAG);
            writer.startTag(childTag);

            AttributeType quantity = valueMap.getAttributeType();
            if(quantity == null || !quantity.isPlural()){
                throw new IOException("Unknown plural quantity: " + valueMap);
            }
            writer.attribute("quantity", quantity.getName());

            writeText(writer, packageBlock, valueMap);

            writer.endTag(childTag);
            hasBags = true;
        }
        if(hasBags){
            writer.writeTagIndent(INDENT_ENTRY);
        }
        return writer.endTag(tag);
    }

    @Override
    public boolean canDecode(ResTableMapEntry mapEntry) {
        return isResBagPluralsValue(mapEntry);
    }

    public static boolean isResBagPluralsValue(ResTableMapEntry valueItem){
        int parentId=valueItem.getParentId();
        if(parentId!=0){
            return false;
        }
        ResValueMap[] bagItems = valueItem.listResValueMap();
        if(bagItems==null||bagItems.length==0){
            return false;
        }
        int len=bagItems.length;
        for(int i=0;i<len;i++){
            ResValueMap item=bagItems[i];
            AttributeType attributeType = item.getAttributeType();
            if(attributeType == null || !attributeType.isPlural()){
                return false;
            }
        }
        return true;
    }
}

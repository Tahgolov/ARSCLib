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
package com.reandroid.lib.arsc.pool;

import com.reandroid.lib.arsc.array.StringArray;
import com.reandroid.lib.arsc.array.TypeStringArray;
import com.reandroid.lib.arsc.chunk.TypeBlock;
import com.reandroid.lib.arsc.group.StringGroup;
import com.reandroid.lib.arsc.item.IntegerArray;
import com.reandroid.lib.arsc.item.IntegerItem;
import com.reandroid.lib.arsc.item.TypeString;

public class TypeStringPool extends BaseStringPool<TypeString> {
    private final IntegerItem mTypeIdOffset;
    public TypeStringPool(boolean is_utf8, IntegerItem typeIdOffset) {
        super(is_utf8);
        this.mTypeIdOffset = typeIdOffset;
    }
    public byte idOf(String typeName){
        return idOf(getByName(typeName));
    }
    /**
     * Resolves id of {@link TypeBlock}
     * Not recommend to use unless unless you are sure of proper pool
     **/
    public byte idOf(TypeString typeString){
        if(typeString==null){
            return 0;
        }
        return (byte) (typeString.getIndex()+mTypeIdOffset.get()+1);
    }
    /**
     * Searches string entry {@link TypeBlock}
     * {@param name} is name of {@link TypeBlock}
     * This might not working if duplicate type names are present
     **/
    public TypeString getByName(String name){
        for(TypeString typeString:listStrings()){
            if(name.equals(typeString.get())){
                return typeString;
            }
        }
        return null;
    }
    public TypeString getById(int id){
        int index=id-mTypeIdOffset.get()-1;
        return super.get(index);
    }
    public TypeString getOrCreate(int typeId, String typeName){
        int size=typeId-mTypeIdOffset.get();
        getStringsArray().ensureSize(size);
        TypeString typeString=getById(typeId);
        typeString.set(typeName);
        return typeString;
    }
    /**
     * Use getOrCreate(typeId, typeName)}
     **/
    @Deprecated
    @Override
    public final TypeString getOrCreate(String str){
        StringGroup<TypeString> group = get(str);
        if(group==null||group.size()==0){
            throw new IllegalArgumentException("Can not create TypeString (" + str
                    +") without type id. use getOrCreate(typeId, typeName)");
        }
        return group.get(0);
    }
    @Override
    StringArray<TypeString> newInstance(IntegerArray offsets, IntegerItem itemCount, IntegerItem itemStart, boolean is_utf8) {
        return new TypeStringArray(offsets, itemCount, itemStart, is_utf8);
    }
}

/*
 *
 *  Copyright 2016 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.diffview.effigy.pairer;

import com.netflix.hollow.diffview.effigy.HollowEffigy;
import com.netflix.hollow.diffview.effigy.HollowEffigy.Field;
import java.util.ArrayList;
import java.util.List;

public class HollowEffigyObjectPairer extends HollowEffigyFieldPairer {

    public HollowEffigyObjectPairer(HollowEffigy fromObject, HollowEffigy toObject) {
        super(fromObject, toObject);
    }

    @Override
    public List<EffigyFieldPair> pair() {
        List<EffigyFieldPair> fieldPairs = new ArrayList<EffigyFieldPair>();

        for(Field fromField : from.getFields()) {
            fieldPairs.add(new EffigyFieldPair(fromField, getField(to, fromField.getFieldName()), -1, -1));
        }

        for(Field toField : to.getFields()) {
            Field fromField = getField(from, toField.getFieldName());
            if(fromField == null)
                fieldPairs.add(new EffigyFieldPair(null, toField, -1, -1));
        }

        return fieldPairs;
    }

    public Field getField(HollowEffigy effigy, String fieldName) {
        for(Field field : effigy.getFields()) {
            if(field.getFieldName().equals(fieldName))
                return field;
        }

        return null;
    }

}

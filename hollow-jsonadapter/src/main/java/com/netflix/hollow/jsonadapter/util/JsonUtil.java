/*
 *  Copyright 2016-2019 Netflix, Inc.
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
package com.netflix.hollow.jsonadapter.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.PrintStream;

public class JsonUtil {
    public static void print(JsonNode node) throws Exception {
        print(node.traverse());
    }

    public static void print(JsonParser parser) throws Exception {
        JsonToken token = parser.nextToken();
        print(parser, token, 0, System.out);
    }

    public static void print(JsonParser parser, PrintStream out) throws Exception {
        JsonToken token = parser.nextToken();
        print(parser, token, 0, out);
    }

    private static void print(int index, String value, PrintStream out) {
        for(int i = 0; i < index; i++) {
            out.print("\t");
        }
        out.println(value);
    }

    private static void print(JsonParser parser, JsonToken token, int index, PrintStream out) throws Exception {
        if(index == 0) System.out.println("\n\n -----");
        try {
            while(token != null && token != JsonToken.END_OBJECT) {
                switch(token) {
                    case START_ARRAY:
                        print(index, String.format("fieldname=%s, token=%s", parser.getCurrentName(), token), out);
                        print(parser, parser.nextToken(), index + 1, out);
                        break;
                    case START_OBJECT:
                        print(index, String.format("fieldname=%s, token=%s", parser.getCurrentName(), token), out);
                        print(parser, parser.nextToken(), index + 1, out);
                        break;
                    case VALUE_NUMBER_INT:
                        print(index, String.format("fieldname=%s, token=%s, value=%s", parser.getCurrentName(), token, parser.getLongValue()), out);
                        break;
                    case VALUE_NUMBER_FLOAT:
                        print(index, String.format("fieldname=%s, token=%s, value=%s", parser.getCurrentName(), token, parser.getDoubleValue()), out);
                        break;
                    case VALUE_NULL:
                        print(index, String.format("fieldname=%s, token=%s, value=NULL", parser.getCurrentName(), token), out);
                        break;
                    case VALUE_STRING:
                        print(index, String.format("fieldname=%s, token=%s, value=%s", parser.getCurrentName(), token, parser.getValueAsString()), out);
                        break;
                    case VALUE_FALSE:
                    case VALUE_TRUE:
                        print(index, String.format("fieldname=%s, token=%s, value=%s", parser.getCurrentName(), token, parser.getBooleanValue()), out);
                        break;
                    case FIELD_NAME:
                        //print(index, String.format("fieldname=%s, token=%s", parser.getCurrentName(), token));
                        break;
                    case END_ARRAY:
                    case END_OBJECT:
                        index--;
                        break;
                    default:
                }
                token = parser.nextToken();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

}

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
package com.netflix.hollow.core.schema;

import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Parses text representations of {@link HollowSchema}.
 * <p>
 * The text representations are the same format obtained via toString() on a HollowSchema.
 */
public class HollowSchemaParser {

    private static final Logger log = Logger.getLogger(HollowSchemaParser.class.getName());

    /**
     * Parse a collection of {@link HollowSchema}s from the provided Reader.
     *
     * @param reader the reader
     * @return the list of schema
     * @throws IOException if the schema cannot be parsed
     */
    public static List<HollowSchema> parseCollectionOfSchemas(Reader reader) throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(reader);
        configureTokenizer(tokenizer);
        List<HollowSchema> schemaList = new ArrayList<HollowSchema>();

        HollowSchema schema = parseSchema(tokenizer);
        while (schema != null) {
            schemaList.add(schema);
            schema = parseSchema(tokenizer);
        }

        return schemaList;
    }

    /**
     * Parse a collection of {@link HollowSchema}s from the provided String.
     *
     * @param schemas the schemas as a string
     * @return the list of schema
     * @throws IOException if the schema cannot be parsed
     */
    public static List<HollowSchema> parseCollectionOfSchemas(String schemas) throws IOException {
        return parseCollectionOfSchemas(new StringReader(schemas));
    }

    /**
     * Parse a single {@link HollowSchema} from the provided String.
     *
     * @param schema the schema as a string
     * @return the schema
     * @throws IOException if the schema cannot be parsed
     */
    public static HollowSchema parseSchema(String schema) throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(schema));
        configureTokenizer(tokenizer);
        return parseSchema(tokenizer);
    }

    private static HollowSchema parseSchema(StreamTokenizer tokenizer) throws IOException {
        int tok = tokenizer.nextToken();
        while(tok != StreamTokenizer.TT_WORD) {
            if(tok == StreamTokenizer.TT_EOF)
                return null;
            tok = tokenizer.nextToken();
        }

        String typeName = tokenizer.sval;

        tok = tokenizer.nextToken();
        if(tok == StreamTokenizer.TT_WORD) {
            if("List".equals(tokenizer.sval)) {
                return parseListSchema(typeName, tokenizer);
            } else if("Set".equals(tokenizer.sval)) {
                return parseSetSchema(typeName, tokenizer);
            } else if("Map".equals(tokenizer.sval)) {
                return parseMapSchema(typeName, tokenizer);
            } else {
                throw new IOException("Invalid syntax: expected one of '{', 'List', 'Set', or 'Map' after type declaration for '" + typeName + "'");
            }
        }

        return parseObjectSchema(typeName, tokenizer);
    }

    private static HollowObjectSchema parseObjectSchema(String typeName, StreamTokenizer tokenizer) throws IOException {
        String keyFieldPaths[] = parsePrimaryKey(tokenizer);
        if (tokenizer.ttype != '{') {
            throw new IOException("Invalid syntax: expecting '{' for '" + typeName + "'");
        }

        int tok = tokenizer.nextToken();
        List<String> tokens = new ArrayList<String>();
        while(tokenizer.ttype != '}') {
            if(tok != StreamTokenizer.TT_WORD)
                throw new IOException("Invalid syntax, expected field type: " + typeName);

            tokens.add(tokenizer.sval);
            tokenizer.nextToken();

            if(tok != StreamTokenizer.TT_WORD)
                throw new IOException("Invalid syntax, expected field name: " + typeName);

            String fieldName = tokenizer.sval;
            tokens.add(fieldName);
            tokenizer.nextToken();

            if(tokenizer.ttype != ';')
                throw new IOException("Invalid syntax, expected semicolon: " + typeName + "." + fieldName);

            tokenizer.nextToken();
        }

        HollowObjectSchema schema = new HollowObjectSchema(typeName, tokens.size() / 2, keyFieldPaths);
        for(int i=0;i<tokens.size();i+=2) {
            String fieldType = tokens.get(i);

            if("int".equals(fieldType)) {
                schema.addField(tokens.get(i+1), FieldType.INT);
            } else if("long".equals(fieldType)) {
                schema.addField(tokens.get(i+1), FieldType.LONG);
            } else if("float".equals(fieldType)) {
                schema.addField(tokens.get(i+1), FieldType.FLOAT);
            } else if("double".equals(fieldType)) {
                schema.addField(tokens.get(i+1), FieldType.DOUBLE);
            } else if("boolean".equals(fieldType)) {
                schema.addField(tokens.get(i+1), FieldType.BOOLEAN);
            } else if("string".equals(fieldType)) {
                schema.addField(tokens.get(i+1), FieldType.STRING);
            } else if("bytes".equals(fieldType)) {
                schema.addField(tokens.get(i+1), FieldType.BYTES);
            } else {
                schema.addField(tokens.get(i+1), FieldType.REFERENCE, fieldType);
            }
        }

        return schema;
    }

    private static HollowListSchema parseListSchema(String typeName, StreamTokenizer tokenizer) throws IOException {
        int tok = tokenizer.nextToken();

        if(tokenizer.ttype != '<')
            throw new IOException("Invalid Syntax: Expected '<' after 'List' for type " + typeName);

        tok = tokenizer.nextToken();
        if(tok != StreamTokenizer.TT_WORD) {
            log.warning("Invalid Syntax: Expected element type declaration: " + typeName);
        }

        String elementType = tokenizer.sval;

        tok = tokenizer.nextToken();
        if(tokenizer.ttype != '>')
            throw new IOException("Invalid Syntax: Expected '>' element type declaration: " + typeName);

        tok = tokenizer.nextToken();
        if(tokenizer.ttype != ';')
            throw new IOException("Invalid Syntax: Expected semicolon after List schema declaration: " + typeName);

        return new HollowListSchema(typeName, elementType);
    }

    private static HollowSetSchema parseSetSchema(String typeName, StreamTokenizer tokenizer) throws IOException {
        int tok = tokenizer.nextToken();

        if(tokenizer.ttype != '<')
            throw new IOException("Invalid Syntax: Expected '<' after 'Set' for type " + typeName);

        tok = tokenizer.nextToken();
        if(tok != StreamTokenizer.TT_WORD) {
            log.warning("Invalid Syntax: Expected element type declaration: " + typeName);
        }

        String elementType = tokenizer.sval;

        tok = tokenizer.nextToken();
        if(tokenizer.ttype != '>')
            throw new IOException("Invalid Syntax: Expected '>' element type declaration: " + typeName);

        tok = tokenizer.nextToken();
        String[] hashKeyPaths = parseHashKey(tokenizer);

        if(tokenizer.ttype != ';')
            throw new IOException("Invalid Syntax: Expected semicolon after Set schema declaration: " + typeName);


        return new HollowSetSchema(typeName, elementType, hashKeyPaths);
    }

    private static HollowMapSchema parseMapSchema(String typeName, StreamTokenizer tokenizer) throws IOException {
        int tok = tokenizer.nextToken();

        if(tokenizer.ttype != '<')
            throw new IOException("Invalid Syntax: Expected '<' after 'Map' for type " + typeName);

        tok = tokenizer.nextToken();
        if(tok != StreamTokenizer.TT_WORD) {
            log.warning("Invalid Syntax: Expected element type declaration: " + typeName);
        }

        String keyType = tokenizer.sval;

        tok = tokenizer.nextToken();
        if(tokenizer.ttype != ',')
            throw new IOException("Invalid Syntax: Expected ',' after key type declaration: " + typeName);

        tok = tokenizer.nextToken();
        if(tok != StreamTokenizer.TT_WORD) {
            log.warning("Invalid Syntax: Expected value type declaration: " + typeName);
        }

        String valueType = tokenizer.sval;

        tok = tokenizer.nextToken();
        if(tokenizer.ttype != '>')
            throw new IOException("Invalid Syntax: Expected '>' after value type declaration: " + typeName);

        tok = tokenizer.nextToken();
        String[] hashKeyPaths = parseHashKey(tokenizer);

        if(tokenizer.ttype != ';')
            throw new IOException("Invalid Syntax: Expected semicolon after Map schema declaration: " + typeName);

        return new HollowMapSchema(typeName, keyType, valueType, hashKeyPaths);
    }

    private static String[] parseHashKey(StreamTokenizer tokenizer) throws IOException {
        return parseKeyFieldPaths(tokenizer, "HashKey");
    }

    private static String[] parsePrimaryKey(StreamTokenizer tokenizer) throws IOException {
        return parseKeyFieldPaths(tokenizer, "PrimaryKey");
    }

    private static String[] parseKeyFieldPaths(StreamTokenizer tokenizer, String annotationName) throws IOException {
        if(tokenizer.ttype != '@')
            return new String[0];

        List<String> fieldPaths = new ArrayList<String>();

        int tok = tokenizer.nextToken();
        if(tok != StreamTokenizer.TT_WORD || !annotationName.equals(tokenizer.sval)) {
            throw new IOException("Invalid Syntax: Invalid  @" + tokenizer.sval + " annotation, expecting @" + annotationName + " declaraction");
        }

        tok = tokenizer.nextToken();
        if(tokenizer.ttype != '(')
            throw new IOException("Expected open parenthesis '(' after @" + annotationName + " declaration");

        tok = tokenizer.nextToken();
        while(tokenizer.ttype != ')') {
            if(tok != StreamTokenizer.TT_WORD)
                throw new IOException("Invalid field declaration inside @" + annotationName + "spec");

            fieldPaths.add(tokenizer.sval);

            tok = tokenizer.nextToken();

            if(tokenizer.ttype == ',') {
                tok = tokenizer.nextToken();
            } else if(tokenizer.ttype != ')') {
                throw new IOException("Invalid char inside @" + annotationName + " spec");
            }
        }

        tok = tokenizer.nextToken();

        return fieldPaths.toArray(new String[fieldPaths.size()]);
    }


    private static void configureTokenizer(StreamTokenizer tokenizer) {
        tokenizer.wordChars('_', '_');
        tokenizer.slashSlashComments(true);
        tokenizer.slashStarComments(true);
    }

}

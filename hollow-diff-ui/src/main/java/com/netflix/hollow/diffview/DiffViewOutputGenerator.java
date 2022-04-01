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
package com.netflix.hollow.diffview;

import com.netflix.hollow.diffview.effigy.HollowEffigy.Field;
import com.netflix.hollow.ui.HollowUISession;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DiffViewOutputGenerator {

    private final HollowObjectViewProvider viewProvider;

    public DiffViewOutputGenerator(HollowObjectViewProvider viewProvider) {
        this.viewProvider = viewProvider;
    }

    public void collapseRow(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HollowDiffViewRow row = findRow(req, resp);
        
        for(HollowDiffViewRow child : row.getChildren())
            child.setVisibility(false);
        
        resp.getWriter().write("ok");
    }

    public void uncollapseRow(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HollowDiffViewRow row = findRow(req, resp);
        
        for(HollowDiffViewRow child : row.getChildren())
            child.setVisibility(true);

        buildChildRowDisplayData(row, resp.getWriter());
    }
    
    private HollowDiffViewRow findRow(HttpServletRequest req, HttpServletResponse resp) {
        HollowUISession session = HollowUISession.getSession(req, resp);
        HollowObjectView objectView = viewProvider.getObjectView(req, session);

        int rowPath[] = getRowPath(req.getParameter("row"));
        HollowDiffViewRow row = objectView.getRootRow();
        for(int i=0;i<rowPath.length;i++)
            row = row.getChildren().get(rowPath[i]);
        return row;
    }
    
    private int[] getRowPath(String rowPathStr) {
        String rowPathElementStrings[] = rowPathStr.split("\\.");
        
        int rowPath[] = new int[rowPathElementStrings.length];
        
        for(int i=0;i<rowPathElementStrings.length;i++)
            rowPath[i] = Integer.parseInt(rowPathElementStrings[i]);

        return rowPath;
    }

    public static void buildChildRowDisplayData(HollowDiffViewRow parentRow, Writer writer) throws IOException {
        buildChildRowDisplayData(parentRow, writer, true);
    }
    
    private static void buildChildRowDisplayData(HollowDiffViewRow parentRow, Writer writer, boolean firstRow) throws IOException {
        
        for(HollowDiffViewRow row : parentRow.getChildren()) {
            if(row.isVisible()) {
                if(firstRow) {
                    firstRow = false;
                } else {
                    writer.write("|");
                }
                
                writeRowPathString(row, writer);                          writer.write("|");
                writer.write(row.getAvailableAction().toString());        writer.write("|");
                writer.write(marginIdx(row.getFieldPair().getFromIdx())); writer.write("|");
                writer.write(fromCellClassname(row));                     writer.write("|");
                writer.write(fromContent(row));                           writer.write("|");
                writer.write(marginIdx(row.getFieldPair().getToIdx()));   writer.write("|");
                writer.write(toCellClassname(row));                       writer.write("|");
                writer.write(toContent(row));
                
                buildChildRowDisplayData(row, writer, false);
            }
        }
    }

    public static void buildChildRowDisplayDataSimple(HollowDiffViewRow parentRow, Writer writer, boolean firstRow) throws IOException {

        for(HollowDiffViewRow row : parentRow.getChildren()) {
            if(row.isVisible()) {
                if(firstRow) {
                    firstRow = false;
                } else {
                    writer.write("|");
                }

                writer.write(fromContent(row));                           writer.write("|");
                writer.write(toContent(row));

                buildChildRowDisplayDataSimple(row, writer, false);
            }
        }
    }

    private static void writeRowPathString(HollowDiffViewRow row, Writer writer) throws IOException {
        for(int i=0;i<row.getRowPath().length;i++) {
            if(i > 0)
                writer.write('.');
            writer.write(String.valueOf(row.getRowPath()[i]));
        }
    }

    private static String marginIdx(int idx) {
        if(idx == -1)
            return "";
        return String.valueOf(idx);
    }

    private static String fromCellClassname(HollowDiffViewRow currentRow) {
        if(currentRow.getFieldPair().getTo() == null)
            return "delete";
        else if(currentRow.getFieldPair().getFrom() == null)
            return "empty";

        if(currentRow.getFieldPair().getFrom().getValue() == null && currentRow.getFieldPair().getTo().getValue() != null)
            return "replace";
        if(currentRow.getFieldPair().getFrom().getValue() != null && currentRow.getFieldPair().getTo().getValue() == null)
            return "replace";
        if(currentRow.getFieldPair().getFrom().getValue() == null && currentRow.getFieldPair().getTo().getValue() == null)
            return "equal";

        if(currentRow.getFieldPair().isLeafNode() && !currentRow.getFieldPair().getFrom().getValue().equals(currentRow.getFieldPair().getTo().getValue()))
            return "replace";


        return "equal";
    }

    private static String toCellClassname(HollowDiffViewRow currentRow) {
        if(currentRow.getFieldPair().getFrom() == null)
            return "insert";
        else if(currentRow.getFieldPair().getTo() == null)
            return "empty";

        if(currentRow.getFieldPair().getFrom().getValue() == null && currentRow.getFieldPair().getTo().getValue() != null)
            return "replace";
        if(currentRow.getFieldPair().getFrom().getValue() != null && currentRow.getFieldPair().getTo().getValue() == null)
            return "replace";
        if(currentRow.getFieldPair().getFrom().getValue() == null && currentRow.getFieldPair().getTo().getValue() == null)
            return "equal";

        if(currentRow.getFieldPair().isLeafNode() && !currentRow.getFieldPair().getFrom().getValue().equals(currentRow.getFieldPair().getTo().getValue()))
            return "replace";

        return "equal";
    }

    private static String fromContent(HollowDiffViewRow row) {
        boolean moreRows[] = new boolean[row.getIndentation() + 1];
        for(int i=0;i<=row.getIndentation();i++)
            moreRows[i] = row.hasMoreFromRows(i);

        if(row.getFieldPair().getFrom() == null)
            return unpopulatedContent(moreRows);

        String fieldName = row.getFieldPair().getFrom().getFieldName();
        return populatedContent(moreRows, row.getIndentation(),
            row.getFieldPair().isLeafNode(), fieldName, getFieldValue(row, true));
    }

    private static String toContent(HollowDiffViewRow row) {
        boolean moreRows[] = new boolean[row.getIndentation() + 1];
        for(int i=0;i<=row.getIndentation();i++)
            moreRows[i] = row.hasMoreToRows(i);

        if(row.getFieldPair().getTo() == null)
            return unpopulatedContent(moreRows);

        String fieldName = row.getFieldPair().getTo().getFieldName();
        return populatedContent(moreRows, row.getIndentation(), row.getFieldPair().isLeafNode(), fieldName,
            getFieldValue(row, false));
    }

    /**
     * Returns a String representation of the provided row's field value. If `useFrom` is
     * true, this will use the `from` value from the pair, otherwise this will use the
     * `to` value.
     */
    private static String getFieldValue(HollowDiffViewRow row, boolean useFrom) {
        Field field = useFrom ? row.getFieldPair().getFrom() : row.getFieldPair().getTo();
        if (row.getFieldPair().isLeafNode()) {
          return field.getValue() == null ? "null"
              : field.getValue().toString().replace("|", "&#x2502");
        } else {
            String suffix = field.getValue() == null ? " [null]" : "";
            return "(" + field.getTypeName() + ")" + suffix;
        }
    }

    private static String unpopulatedContent(boolean moreRows[]) {
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<moreRows.length;i++) {
            if(moreRows[i]) {
                builder.append(" &#x2502;");
            } else {
                builder.append("  ");
            }
        }
        return builder.toString();
    }

    private static String populatedContent(boolean moreRows[], int indentation, boolean leafNode, String fieldName, String value) {
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<indentation;i++) {
            if(moreRows[i]) {
                builder.append(".&#x2502;");
            } else {
                builder.append("..");
            }
        }

        if(!leafNode) {
            if(moreRows[indentation])
                builder.append(".&#x251D;&#x2501;&#x252F;&#x2501;&gt;");
            else
                builder.append(".&#x2515;&#x2501;&#x252F;&#x2501;&gt;");
        } else {
            if(moreRows[indentation])
                builder.append(".&#x251C;&#x2500;&#x2500;&#x2500;&gt;");
            else
                builder.append(".&#x2514;&#x2500;&#x2500;&#x2500;&gt;");
        }

        if(fieldName != null)
            builder.append(fieldName).append(": ");
        builder.append(value);

        return builder.toString();
    }


}

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
package com.netflix.hollow.diffview;

import com.netflix.hollow.diff.ui.HollowDiffSession;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DiffViewOutputGenerator {

    private final HollowObjectViewProvider viewProvider;

    public DiffViewOutputGenerator(HollowObjectViewProvider viewProvider) {
        this.viewProvider = viewProvider;
    }

    public void collapseRow(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HollowDiffSession session = HollowDiffSession.getSession(req, resp);
        HollowObjectView objectView = viewProvider.getObjectView(req, session);

        int row = Integer.parseInt(req.getParameter("row"));
        HollowDiffViewRow collapseRow = objectView.getRows().get(row);

        collapseRow.setUnrolled(false);

        resp.getWriter().write(String.valueOf(collapseRow.getRowId() + collapseRow.getNumDescendentRows() + 1));
        resp.getWriter().write("|");
        resp.getWriter().write(String.valueOf(showUncollapseAllButton(collapseRow, objectView.getRows())));
    }


    public void uncollapseRow(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HollowDiffSession session = HollowDiffSession.getSession(req, resp);
        HollowObjectView objectView = viewProvider.getObjectView(req, session);

        boolean uncollapseAll = "true".equals(req.getParameter("all"));

        int row = Integer.parseInt(req.getParameter("row"));
        objectView.getRows().get(row).setUnrolled(true);
        objectView.getRows().get(row).setPartiallyUnrolled(false);

        String data = getRowDisplayData(objectView, row, uncollapseAll);

        resp.getWriter().write(data);
    }

    public static String getRowDisplayData(HollowObjectView objectView, int row, boolean uncollapseAll) {

        List<HollowDiffViewRow> rows = objectView.getRows();

        StringBuilder data = new StringBuilder();
        int numDescendentRows = rows.get(row).getNumDescendentRows();

        int partialUnrollEnd = -1;

        for(int i=row+1;i<=row+numDescendentRows;i++) {
            HollowDiffViewRow currentRow = rows.get(i);

            if(uncollapseAll && !currentRow.getFieldPair().isLeafNode()) {
                currentRow.setUnrolled(true);
                currentRow.setPartiallyUnrolled(false);
            }

            if(currentRow.isPartiallyUnrolled()) {
                if(i + currentRow.getNumDescendentRows() > partialUnrollEnd)
                    partialUnrollEnd = i + currentRow.getNumDescendentRows() + 1;
            }

            if(i >= partialUnrollEnd || currentRow.isVisibleForPartialUnroll()) {
                if(i > row+1)
                    data.append("|");
                data.append(i).append("|");
                data.append(showUncollapseButton(currentRow)).append("|");
                data.append(showUncollapseAllButton(currentRow, rows)).append("|");
                data.append(showCollapseButton(currentRow)).append("|");
                data.append(showPartialUnrollButton(currentRow)).append("|");
                data.append(marginIdx(currentRow.getFieldPair().getFromIdx())).append("|");
                data.append(fromCellClassname(currentRow)).append("|");
                data.append(fromContent(currentRow)).append("|");
                data.append(marginIdx(currentRow.getFieldPair().getToIdx())).append("|");
                data.append(toCellClassname(currentRow)).append("|");
                data.append(toContent(currentRow));

                if(!currentRow.getFieldPair().isLeafNode() && !currentRow.isUnrolled() && !currentRow.isPartiallyUnrolled() && !uncollapseAll) {
                    i += currentRow.getNumDescendentRows();
                }
            }
        }
        return data.toString();
    }

    private static String marginIdx(int idx) {
        if(idx == -1)
            return "";
        return String.valueOf(idx);
    }

    private static boolean showPartialUnrollButton(HollowDiffViewRow currentRow) {
        return currentRow.isPartiallyUnrolled();
    }

    private static boolean showUncollapseButton(HollowDiffViewRow currentRow) {
        return !currentRow.isUnrolled() && !currentRow.isPartiallyUnrolled() && currentRow.getNumDescendentRows() > 0;
    }

    private static boolean showUncollapseAllButton(HollowDiffViewRow currentRow, List<HollowDiffViewRow> allRows) {
        if(showUncollapseButton(currentRow)) {
            int startRow = currentRow.getRowId() + 1;
            int endRow = startRow + currentRow.getNumDescendentRows();
            for(int i=startRow;i<endRow;i++) {
                currentRow = allRows.get(i);
                if(showUncollapseButton(currentRow) || showPartialUnrollButton(currentRow))
                    return true;
            }
        }
        return false;
    }

    private static boolean showCollapseButton(HollowDiffViewRow currentRow) {
        return (currentRow.isUnrolled() && currentRow.getNumDescendentRows() > 0);
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
        String fieldValue = row.getFieldPair().isLeafNode() ?
                row.getFieldPair().getFrom().getValue() == null ? "null" : row.getFieldPair().getFrom().getValue().toString().replace("|", "&#x2502")
                : "(" + row.getFieldPair().getFrom().getTypeName() + ")";
        return populatedContent(moreRows, row.getIndentation(), row.getFieldPair().isLeafNode(), fieldName, fieldValue);
    }

    private static String toContent(HollowDiffViewRow row) {
        boolean moreRows[] = new boolean[row.getIndentation() + 1];
        for(int i=0;i<=row.getIndentation();i++)
            moreRows[i] = row.hasMoreToRows(i);

        if(row.getFieldPair().getTo() == null)
            return unpopulatedContent(moreRows);

        String fieldName = row.getFieldPair().getTo().getFieldName();
        String fieldValue = row.getFieldPair().isLeafNode() ?
                row.getFieldPair().getTo().getValue() == null ? "null" : row.getFieldPair().getTo().getValue().toString().replace("|", "&#x2502")
                : "(" + row.getFieldPair().getTo().getTypeName() + ")";
        return populatedContent(moreRows, row.getIndentation(), row.getFieldPair().isLeafNode(), fieldName, fieldValue);
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

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

import com.netflix.hollow.diffview.HollowDiffViewRow.Action;
import java.io.IOException;
import java.io.StringWriter;


public class HollowDiffHtmlKickstarter {

    private final String baseURL;

    public HollowDiffHtmlKickstarter(String baseURL) {
        this.baseURL = baseURL;
    }

    public String initialHtmlRows(HollowObjectView objectView) {

        String diffViewOutput = null;
        try {
            StringWriter writer = new StringWriter();
            DiffViewOutputGenerator.buildChildRowDisplayData(objectView.getRootRow(), writer);
            diffViewOutput = writer.toString();
        } catch (IOException unexpected) {
            throw new RuntimeException(unexpected);
        }

        StringBuilder initialHtml = new StringBuilder();
        StringTokenizer tokenizer = new StringTokenizer(diffViewOutput);

        while(tokenizer.hasMoreTokens()) {
            String rowPath = tokenizer.nextToken();
            Action action = Action.valueOf(tokenizer.nextToken());
            String origFromIndexValue = tokenizer.nextToken();
            String fromCellClassname = tokenizer.nextToken();
            String fromCellContent = tokenizer.nextToken();
            String origToIndexValue = tokenizer.nextToken();
            String toCellClassname = tokenizer.nextToken();
            String toCellContent = tokenizer.nextToken();


            initialHtml.append("<tr id=\"r").append(rowPath).append("\"");

            if(action == Action.PARTIAL_UNCOLLAPSE || action == Action.UNCOLLAPSE) {
                initialHtml.append(" onclick=\"uncollapseRow('" + rowPath + "')\"");
            } else if(action == Action.COLLAPSE) {
                initialHtml.append(" onclick=\"collapseRow('" + rowPath + "')\"");
            }

            initialHtml.append(">");

            if(action == Action.PARTIAL_UNCOLLAPSE) {
                initialHtml.append("<td class=\"margin\">").append("<img src=\"" + baseURL + "/resource/partial_expand.png\"/>").append("</td>");
            } else if(action == Action.UNCOLLAPSE) {
                initialHtml.append("<td class=\"margin\">").append("<img src=\"" + baseURL + "/resource/expand.png\"/>").append("</td>");
            } else if(action == Action.COLLAPSE) {
                initialHtml.append("<td class=\"margin\">").append("<img src=\"" + baseURL + "/resource/collapse.png\"/>").append("</td>");
            } else {
                initialHtml.append("<td class=\"margin\"/>");
            }


            if(!"".equals(origFromIndexValue))
                initialHtml.append("<td class=\"margin\">").append(origFromIndexValue).append("</td>");
            else
                initialHtml.append("<td class=\"margin\"/>");

            initialHtml.append("<td class=\"").append(fromCellClassname).append("\">").append(fromCellContent).append("</td>");

            if(action == Action.PARTIAL_UNCOLLAPSE) {
                initialHtml.append("<td class=\"margin\">").append("<img src=\"" + baseURL + "/resource/partial_expand.png\"/>").append("</td>");
            } else if(action == Action.UNCOLLAPSE) {
                initialHtml.append("<td class=\"margin\">").append("<img src=\"" + baseURL + "/resource/expand.png\"/>").append("</td>");
            } else if(action == Action.COLLAPSE) {
                initialHtml.append("<td class=\"margin\">").append("<img src=\"" + baseURL + "/resource/collapse.png\"/>").append("</td>");
            } else {
                initialHtml.append("<td class=\"margin\"/>");
            }

            if(!"".equals(origToIndexValue))
                initialHtml.append("<td class=\"margin\">").append(origToIndexValue).append("</td>");
            else
                initialHtml.append("<td class=\"margin\"/>");

            initialHtml.append("<td class=\"").append(toCellClassname).append("\">").append(toCellContent).append("</td>");
        }

        return initialHtml.toString();
    }


    private static class StringTokenizer {
        private final String str;
        private int cursor;

        public StringTokenizer(String str) {
            this.str = str;
        }

        public String nextToken() {
            int endIdx = str.indexOf('|', cursor);
            String token;

            if(endIdx != -1) {
                token = str.substring(cursor, endIdx);
                cursor = endIdx + 1;
            } else {
                token = str.substring(cursor);
                cursor = -1;
            }


            return token;
        }

        public boolean hasMoreTokens() {
            return str.length() > 0 && cursor >= 0;
        }

    }

}

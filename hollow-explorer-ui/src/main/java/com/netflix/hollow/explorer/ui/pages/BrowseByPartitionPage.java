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
package com.netflix.hollow.explorer.ui.pages;

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;

import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.explorer.ui.HollowExplorerUI;
import com.netflix.hollow.ui.HollowUISession;
import java.io.Writer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.VelocityContext;

/**
 * Page for browsing records grouped by partition.
 * Shows partition index, ordinal ranges, and record counts per partition.
 */
public class BrowseByPartitionPage extends HollowExplorerPage {

    private static final int PARTITION_INDEX_BITS = 3;
    private static final int PARTITION_INDEX_MASK = (1 << PARTITION_INDEX_BITS) - 1;

    public BrowseByPartitionPage(HollowExplorerUI ui) {
        super(ui);
    }

    @Override
    protected void setUpContext(HttpServletRequest req, HollowUISession session, VelocityContext ctx) {
        String typeName = req.getParameter("type");
        HollowTypeReadState typeState = ui.getStateEngine().getTypeState(typeName);

        if (!(typeState instanceof HollowObjectTypeReadState)) {
            ctx.put("error", "Partition browsing is only available for OBJECT types");
            ctx.put("type", typeName);
            return;
        }

        HollowObjectTypeReadState objTypeState = (HollowObjectTypeReadState) typeState;
        int numPartitions = objTypeState.getNumPartitions();

        if (numPartitions <= 1) {
            ctx.put("error", "Type '" + typeName + "' is not partitioned (has " + numPartitions + " partition)");
            ctx.put("type", typeName);
            return;
        }

        // Collect ordinals per partition
        BitSet populatedOrdinals = typeState.getPopulatedOrdinals();
        List<PartitionInfo> partitions = new ArrayList<>();

        for (int p = 0; p < numPartitions; p++) {
            partitions.add(new PartitionInfo(p));
        }

        // Iterate through all populated ordinals and group by partition
        for (int encodedOrdinal = populatedOrdinals.nextSetBit(0);
             encodedOrdinal >= 0;
             encodedOrdinal = populatedOrdinals.nextSetBit(encodedOrdinal + 1)) {

            int partitionIndex = encodedOrdinal & PARTITION_INDEX_MASK;
            int partitionOrdinal = encodedOrdinal >> PARTITION_INDEX_BITS;

            if (partitionIndex < numPartitions) {
                PartitionInfo partition = partitions.get(partitionIndex);
                partition.addOrdinal(encodedOrdinal, partitionOrdinal);
            }
        }

        // Get expand parameters
        String partitionParam = req.getParameter("partition");
        String expandParam = req.getParameter("expand");

        Integer selectedPartition = null;
        if (partitionParam != null) {
            try {
                selectedPartition = Integer.parseInt(partitionParam);
            } catch (NumberFormatException e) {
                // Invalid partition parameter, ignore
            }
        }

        ctx.put("type", typeName);
        ctx.put("numPartitions", numPartitions);
        ctx.put("partitions", partitions);
        ctx.put("totalRecords", populatedOrdinals.cardinality());
        ctx.put("selectedPartition", selectedPartition);
        ctx.put("expand", expandParam);
    }

    @Override
    protected void renderPage(HttpServletRequest req, VelocityContext ctx, Writer writer) {
        ui.getVelocityEngine().getTemplate("browse-by-partition.vm").merge(ctx, writer);
    }

    /**
     * Helper class to track information about a single partition
     */
    public static class PartitionInfo {
        private final int partitionIndex;
        private final List<Integer> encodedOrdinals = new ArrayList<>();
        private final List<Integer> partitionOrdinals = new ArrayList<>();
        private int minPartitionOrdinal = Integer.MAX_VALUE;
        private int maxPartitionOrdinal = Integer.MIN_VALUE;

        public PartitionInfo(int partitionIndex) {
            this.partitionIndex = partitionIndex;
        }

        public void addOrdinal(int encodedOrdinal, int partitionOrdinal) {
            encodedOrdinals.add(encodedOrdinal);
            partitionOrdinals.add(partitionOrdinal);
            minPartitionOrdinal = Math.min(minPartitionOrdinal, partitionOrdinal);
            maxPartitionOrdinal = Math.max(maxPartitionOrdinal, partitionOrdinal);
        }

        public int getPartitionIndex() {
            return partitionIndex;
        }

        public int getRecordCount() {
            return encodedOrdinals.size();
        }

        public String getOrdinalRange() {
            if (encodedOrdinals.isEmpty()) {
                return "N/A";
            }
            if (minPartitionOrdinal == maxPartitionOrdinal) {
                return String.valueOf(minPartitionOrdinal);
            }
            return minPartitionOrdinal + " - " + maxPartitionOrdinal;
        }

        public int getMinPartitionOrdinal() {
            return minPartitionOrdinal == Integer.MAX_VALUE ? 0 : minPartitionOrdinal;
        }

        public int getMaxPartitionOrdinal() {
            return maxPartitionOrdinal == Integer.MIN_VALUE ? 0 : maxPartitionOrdinal;
        }

        public List<Integer> getEncodedOrdinals() {
            return encodedOrdinals;
        }

        public List<Integer> getPartitionOrdinals() {
            return partitionOrdinals;
        }

        public boolean isEmpty() {
            return encodedOrdinals.isEmpty();
        }
    }
}

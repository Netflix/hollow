package com.netflix.hollow.tools.combine;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;

/**
 * Specifies a set of inclusions for a {@link HollowCombiner}'s operation over one or more inputs.
 * <p>
 * Inclusions are specified based on record primary keys.
 * <p>
 * This is one of the most useful implementations of a {@link HollowCombinerCopyDirector}.
 * 
 */
public class HollowCombinerIncludePrimaryKeysCopyDirector implements HollowCombinerCopyDirector {

    private final HollowCombinerExcludePrimaryKeysCopyDirector inverseCopyDirector;

    public HollowCombinerIncludePrimaryKeysCopyDirector() {
        this.inverseCopyDirector = new HollowCombinerExcludePrimaryKeysCopyDirector();
    }

    public HollowCombinerIncludePrimaryKeysCopyDirector(HollowCombinerCopyDirector baseDirector) {
        this.inverseCopyDirector = new HollowCombinerExcludePrimaryKeysCopyDirector(baseDirector);
    }

    /**
     * Include the record which matches the specified key.
     * 
     * @param idx the index in which to query for the key 
     * @param key the key
     */
    public void includeKey(HollowPrimaryKeyIndex idx, Object... key) {
        inverseCopyDirector.excludeKey(idx, key);
    }

    @Override
    public boolean shouldCopy(HollowTypeReadState typeState, int ordinal) {
        if(typeState.getSchema().getName().equals("CompleteVideo") && ordinal == 2000896)
            System.out.println("asdf");
        return !inverseCopyDirector.shouldCopy(typeState, ordinal);
    }

}

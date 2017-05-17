package com.netflix.vms.transformer.modules.artwork;

import com.netflix.vms.transformer.hollowoutput.ArtworkCdn;
import com.netflix.vms.transformer.hollowoutput.ArtworkDerivative;
import java.util.Comparator;
import java.util.List;

public class ArtworkDerivativesListMerger {
    
    private final List<ArtworkDerivative> sortedList1;
    private final List<ArtworkDerivative> sortedList2;
    private final List<ArtworkCdn> cdnList1;
    private final List<ArtworkCdn> cdnList2;
    
    private int idx1 = -1;
    private int idx2;
    private int which = 0;
    
    public ArtworkDerivativesListMerger(List<ArtworkDerivative> sortedList1, List<ArtworkDerivative> sortedList2,
                                        List<ArtworkCdn> cdnList1,           List<ArtworkCdn> cdnList2) {
        
        this.sortedList1 = sortedList1;
        this.sortedList2 = sortedList2;
        this.cdnList1 = cdnList1;
        this.cdnList2 = cdnList2;
        
        //this.which = findWhich();
    }
    
    private int findWhich() {
        if(idx1 >= sortedList1.size())
            return 1;
        else if(idx2 >= sortedList2.size())
            return 0;
        
        int cmp = DERIVATIVE_COMPARATOR.compare(sortedList1.get(idx1), sortedList2.get(idx2));
        
        return cmp > 0 ? 1 : 0;
    }
    
    public int mergedSize() {
        return sortedList1.size() + sortedList2.size();
    }

    public boolean next() {
        switch(which) {
        case 0:
            idx1++;
            break;
        case 1:
            idx2++;
            break;
        }

        if(idx1 >= sortedList1.size() && idx2 >= sortedList2.size())
            return false;
        
        which = findWhich();
        
        return true;
    }
    
    public ArtworkDerivative getNextArtworkDerivative() {
        if(which == 0)
            return sortedList1.get(idx1);
        return sortedList2.get(idx2);
    }
    
    public ArtworkCdn getNextArtworkCdn() {
        if(which == 0)
            return cdnList1.get(idx1);
        return cdnList2.get(idx2);
    }
    
    public static final Comparator<ArtworkDerivative> DERIVATIVE_COMPARATOR = new Comparator<ArtworkDerivative>() {
        public int compare(ArtworkDerivative o1, ArtworkDerivative o2) {
            int cmp = compareArray(o1.type.nameStr, o2.type.nameStr);
            if(cmp != 0) return cmp;
            cmp = o1.format.width - o2.format.width;
            if(cmp != 0) return cmp;
            cmp = o1.format.height - o2.format.height;
            if(cmp != 0) return cmp;
            cmp = compareArray(o1.recipe.recipeNameStr, o2.recipe.recipeNameStr);
            if(cmp != 0) return cmp;
            cmp = compareArray(o1.recipeDesc.value, o2.recipeDesc.value);
            
            return cmp;
        }
        
        private int compareArray(char[] v1, char[] v2) {
            int len1 = v1.length;
            int len2 = v2.length;
            int lim = Math.min(len1, len2);

            int k = 0;
            while (k < lim) {
                char c1 = v1[k];
                char c2 = v2[k];
                if (c1 != c2) {
                    return c1 - c2;
                }
                k++;
            }
            return len1 - len2;
        }
    };
    
}

package com.netflix.vms.transformer;

import com.netflix.vms.transformer.hollowoutput.NFLocale;
import com.netflix.vms.transformer.util.VMSTransformerHashCodeFinder;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class VMSTransformerHashCodeFinderTest {

    @Test
    public void testHashCode() {

        List<String> locales = Arrays.asList("US", "BE", "IN", "NL", "CA", "MX", "SL");

        for (String locale : locales) {
            NFLocale nfLocale = new NFLocale(locale);
            Assert.assertEquals(nfLocale.hashCode(), VMSTransformerHashCodeFinder.stringHashCode(locale.toCharArray()));
        }
    }
}

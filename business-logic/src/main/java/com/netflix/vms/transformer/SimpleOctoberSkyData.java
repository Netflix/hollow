package com.netflix.vms.transformer;

import java.util.HashMap;

import java.util.Map;
import com.netflix.vms.transformer.common.config.OctoberSkyData;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SimpleOctoberSkyData implements OctoberSkyData {

    public static SimpleOctoberSkyData INSTANCE = new SimpleOctoberSkyData();

    private final Set<String> countrySet;
    private final Map<String, Set<String>> countryLocales;

    private SimpleOctoberSkyData() {
        String countryCodes = "AA AD AE AF AG AI AL AM AO AQ AR AS AT AU AW AZ BA BB BD BE BF BG BH "
                + "BI BJ BL BM BN BO BQ BR BS BT BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU "
                + "CV CW CX CY CZ DE DJ DK DM DO DZ EC EE EG EH ER ES ET FI FJ FK FM FO FR GA GB GD "
                + "GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IM IN "
                + "IO IQ IR IS IT JE JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS "
                + "LT LU LV LY MA MC MD ME MF MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ "
                + "NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY "
                + "QA RE RO RS RU RW SA SB SC SD SE SG SH SI SK SL SM SN SO SR SS ST SV SX SY SZ TC "
                + "TD TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU "
                + "WF WS YE YT ZA ZM ZW";

        this.countrySet = spaceDelimitedSet(countryCodes);
        this.countryLocales = new HashMap<>();
        
        this.countryLocales.put("BE", spaceDelimitedSet("fr nl"));
        this.countryLocales.put("CH", spaceDelimitedSet("fr de"));
        this.countryLocales.put("LU", spaceDelimitedSet("fr de"));
    }
    
    private Set<String> spaceDelimitedSet(String set) {
        return Collections.unmodifiableSet(
                new HashSet<>(Arrays.asList(set.split(" ")))
        );
    }

    @Override
    public Set<String> getSupportedCountries() {
        return countrySet;
    }
    
    @Override
    public Set<String> getCatalogLanguages(String country) {
        return countryLocales.get(country);
    }


    @Override
    public void refresh() { }


}

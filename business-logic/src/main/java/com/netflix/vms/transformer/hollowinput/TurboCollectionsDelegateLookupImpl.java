package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TurboCollectionsDelegateLookupImpl extends HollowObjectAbstractDelegate implements TurboCollectionsDelegate {

    private final TurboCollectionsTypeAPI typeAPI;

    public TurboCollectionsDelegateLookupImpl(TurboCollectionsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getId(int ordinal) {
        return typeAPI.getId(ordinal);
    }

    public Long getIdBoxed(int ordinal) {
        return typeAPI.getIdBoxed(ordinal);
    }

    public int getPrefixOrdinal(int ordinal) {
        return typeAPI.getPrefixOrdinal(ordinal);
    }

    public int getDesOrdinal(int ordinal) {
        return typeAPI.getDesOrdinal(ordinal);
    }

    public int getEvi_nOrdinal(int ordinal) {
        return typeAPI.getEvi_nOrdinal(ordinal);
    }

    public int getChar_nOrdinal(int ordinal) {
        return typeAPI.getChar_nOrdinal(ordinal);
    }

    public int getNav_snOrdinal(int ordinal) {
        return typeAPI.getNav_snOrdinal(ordinal);
    }

    public int getDnOrdinal(int ordinal) {
        return typeAPI.getDnOrdinal(ordinal);
    }

    public int getKc_cnOrdinal(int ordinal) {
        return typeAPI.getKc_cnOrdinal(ordinal);
    }

    public int getSt_2Ordinal(int ordinal) {
        return typeAPI.getSt_2Ordinal(ordinal);
    }

    public int getBmt_nOrdinal(int ordinal) {
        return typeAPI.getBmt_nOrdinal(ordinal);
    }

    public int getSt_1Ordinal(int ordinal) {
        return typeAPI.getSt_1Ordinal(ordinal);
    }

    public int getSt_4Ordinal(int ordinal) {
        return typeAPI.getSt_4Ordinal(ordinal);
    }

    public int getSt_3Ordinal(int ordinal) {
        return typeAPI.getSt_3Ordinal(ordinal);
    }

    public int getSt_0Ordinal(int ordinal) {
        return typeAPI.getSt_0Ordinal(ordinal);
    }

    public int getSt_9Ordinal(int ordinal) {
        return typeAPI.getSt_9Ordinal(ordinal);
    }

    public int getSnOrdinal(int ordinal) {
        return typeAPI.getSnOrdinal(ordinal);
    }

    public int getKag_knOrdinal(int ordinal) {
        return typeAPI.getKag_knOrdinal(ordinal);
    }

    public int getRoar_nOrdinal(int ordinal) {
        return typeAPI.getRoar_nOrdinal(ordinal);
    }

    public int getSt_6Ordinal(int ordinal) {
        return typeAPI.getSt_6Ordinal(ordinal);
    }

    public int getSt_5Ordinal(int ordinal) {
        return typeAPI.getSt_5Ordinal(ordinal);
    }

    public int getSt_8Ordinal(int ordinal) {
        return typeAPI.getSt_8Ordinal(ordinal);
    }

    public int getTdnOrdinal(int ordinal) {
        return typeAPI.getTdnOrdinal(ordinal);
    }

    public int getSt_7Ordinal(int ordinal) {
        return typeAPI.getSt_7Ordinal(ordinal);
    }

    public int getSt_10Ordinal(int ordinal) {
        return typeAPI.getSt_10Ordinal(ordinal);
    }

    public int getSt_11Ordinal(int ordinal) {
        return typeAPI.getSt_11Ordinal(ordinal);
    }

    public int getSt_12Ordinal(int ordinal) {
        return typeAPI.getSt_12Ordinal(ordinal);
    }

    public int getSt_13Ordinal(int ordinal) {
        return typeAPI.getSt_13Ordinal(ordinal);
    }

    public int getSt_14Ordinal(int ordinal) {
        return typeAPI.getSt_14Ordinal(ordinal);
    }

    public int getSt_15Ordinal(int ordinal) {
        return typeAPI.getSt_15Ordinal(ordinal);
    }

    public int getSt_16Ordinal(int ordinal) {
        return typeAPI.getSt_16Ordinal(ordinal);
    }

    public int getSt_17Ordinal(int ordinal) {
        return typeAPI.getSt_17Ordinal(ordinal);
    }

    public int getSt_18Ordinal(int ordinal) {
        return typeAPI.getSt_18Ordinal(ordinal);
    }

    public int getSt_19Ordinal(int ordinal) {
        return typeAPI.getSt_19Ordinal(ordinal);
    }

    public int getSt_20Ordinal(int ordinal) {
        return typeAPI.getSt_20Ordinal(ordinal);
    }

    public int getSt_21Ordinal(int ordinal) {
        return typeAPI.getSt_21Ordinal(ordinal);
    }

    public int getSt_22Ordinal(int ordinal) {
        return typeAPI.getSt_22Ordinal(ordinal);
    }

    public int getSt_23Ordinal(int ordinal) {
        return typeAPI.getSt_23Ordinal(ordinal);
    }

    public int getSt_24Ordinal(int ordinal) {
        return typeAPI.getSt_24Ordinal(ordinal);
    }

    public int getSt_25Ordinal(int ordinal) {
        return typeAPI.getSt_25Ordinal(ordinal);
    }

    public int getSt_26Ordinal(int ordinal) {
        return typeAPI.getSt_26Ordinal(ordinal);
    }

    public int getSt_27Ordinal(int ordinal) {
        return typeAPI.getSt_27Ordinal(ordinal);
    }

    public int getSt_28Ordinal(int ordinal) {
        return typeAPI.getSt_28Ordinal(ordinal);
    }

    public int getSt_29Ordinal(int ordinal) {
        return typeAPI.getSt_29Ordinal(ordinal);
    }

    public int getSt_30Ordinal(int ordinal) {
        return typeAPI.getSt_30Ordinal(ordinal);
    }

    public int getSt_31Ordinal(int ordinal) {
        return typeAPI.getSt_31Ordinal(ordinal);
    }

    public int getSt_32Ordinal(int ordinal) {
        return typeAPI.getSt_32Ordinal(ordinal);
    }

    public int getSt_33Ordinal(int ordinal) {
        return typeAPI.getSt_33Ordinal(ordinal);
    }

    public int getSt_34Ordinal(int ordinal) {
        return typeAPI.getSt_34Ordinal(ordinal);
    }

    public int getSt_35Ordinal(int ordinal) {
        return typeAPI.getSt_35Ordinal(ordinal);
    }

    public int getSt_36Ordinal(int ordinal) {
        return typeAPI.getSt_36Ordinal(ordinal);
    }

    public int getSt_37Ordinal(int ordinal) {
        return typeAPI.getSt_37Ordinal(ordinal);
    }

    public int getSt_38Ordinal(int ordinal) {
        return typeAPI.getSt_38Ordinal(ordinal);
    }

    public int getSt_39Ordinal(int ordinal) {
        return typeAPI.getSt_39Ordinal(ordinal);
    }

    public int getSt_40Ordinal(int ordinal) {
        return typeAPI.getSt_40Ordinal(ordinal);
    }

    public int getSt_41Ordinal(int ordinal) {
        return typeAPI.getSt_41Ordinal(ordinal);
    }

    public int getSt_42Ordinal(int ordinal) {
        return typeAPI.getSt_42Ordinal(ordinal);
    }

    public int getSt_43Ordinal(int ordinal) {
        return typeAPI.getSt_43Ordinal(ordinal);
    }

    public int getSt_44Ordinal(int ordinal) {
        return typeAPI.getSt_44Ordinal(ordinal);
    }

    public int getSt_45Ordinal(int ordinal) {
        return typeAPI.getSt_45Ordinal(ordinal);
    }

    public int getSt_46Ordinal(int ordinal) {
        return typeAPI.getSt_46Ordinal(ordinal);
    }

    public int getSt_47Ordinal(int ordinal) {
        return typeAPI.getSt_47Ordinal(ordinal);
    }

    public int getSt_48Ordinal(int ordinal) {
        return typeAPI.getSt_48Ordinal(ordinal);
    }

    public int getSt_49Ordinal(int ordinal) {
        return typeAPI.getSt_49Ordinal(ordinal);
    }

    public int getSt_50Ordinal(int ordinal) {
        return typeAPI.getSt_50Ordinal(ordinal);
    }

    public int getSt_51Ordinal(int ordinal) {
        return typeAPI.getSt_51Ordinal(ordinal);
    }

    public int getSt_52Ordinal(int ordinal) {
        return typeAPI.getSt_52Ordinal(ordinal);
    }

    public int getSt_53Ordinal(int ordinal) {
        return typeAPI.getSt_53Ordinal(ordinal);
    }

    public int getSt_54Ordinal(int ordinal) {
        return typeAPI.getSt_54Ordinal(ordinal);
    }

    public int getSt_55Ordinal(int ordinal) {
        return typeAPI.getSt_55Ordinal(ordinal);
    }

    public int getSt_56Ordinal(int ordinal) {
        return typeAPI.getSt_56Ordinal(ordinal);
    }

    public int getSt_57Ordinal(int ordinal) {
        return typeAPI.getSt_57Ordinal(ordinal);
    }

    public int getSt_58Ordinal(int ordinal) {
        return typeAPI.getSt_58Ordinal(ordinal);
    }

    public int getSt_59Ordinal(int ordinal) {
        return typeAPI.getSt_59Ordinal(ordinal);
    }

    public int getSt_60Ordinal(int ordinal) {
        return typeAPI.getSt_60Ordinal(ordinal);
    }

    public int getSt_61Ordinal(int ordinal) {
        return typeAPI.getSt_61Ordinal(ordinal);
    }

    public int getSt_62Ordinal(int ordinal) {
        return typeAPI.getSt_62Ordinal(ordinal);
    }

    public int getSt_63Ordinal(int ordinal) {
        return typeAPI.getSt_63Ordinal(ordinal);
    }

    public int getSt_64Ordinal(int ordinal) {
        return typeAPI.getSt_64Ordinal(ordinal);
    }

    public int getSt_65Ordinal(int ordinal) {
        return typeAPI.getSt_65Ordinal(ordinal);
    }

    public int getSt_66Ordinal(int ordinal) {
        return typeAPI.getSt_66Ordinal(ordinal);
    }

    public int getSt_67Ordinal(int ordinal) {
        return typeAPI.getSt_67Ordinal(ordinal);
    }

    public int getSt_68Ordinal(int ordinal) {
        return typeAPI.getSt_68Ordinal(ordinal);
    }

    public int getSt_69Ordinal(int ordinal) {
        return typeAPI.getSt_69Ordinal(ordinal);
    }

    public int getSt_70Ordinal(int ordinal) {
        return typeAPI.getSt_70Ordinal(ordinal);
    }

    public int getSt_71Ordinal(int ordinal) {
        return typeAPI.getSt_71Ordinal(ordinal);
    }

    public int getSt_72Ordinal(int ordinal) {
        return typeAPI.getSt_72Ordinal(ordinal);
    }

    public int getSt_73Ordinal(int ordinal) {
        return typeAPI.getSt_73Ordinal(ordinal);
    }

    public int getSt_74Ordinal(int ordinal) {
        return typeAPI.getSt_74Ordinal(ordinal);
    }

    public int getSt_75Ordinal(int ordinal) {
        return typeAPI.getSt_75Ordinal(ordinal);
    }

    public int getSt_76Ordinal(int ordinal) {
        return typeAPI.getSt_76Ordinal(ordinal);
    }

    public int getSt_77Ordinal(int ordinal) {
        return typeAPI.getSt_77Ordinal(ordinal);
    }

    public int getSt_78Ordinal(int ordinal) {
        return typeAPI.getSt_78Ordinal(ordinal);
    }

    public int getSt_79Ordinal(int ordinal) {
        return typeAPI.getSt_79Ordinal(ordinal);
    }

    public int getSt_80Ordinal(int ordinal) {
        return typeAPI.getSt_80Ordinal(ordinal);
    }

    public int getSt_81Ordinal(int ordinal) {
        return typeAPI.getSt_81Ordinal(ordinal);
    }

    public int getSt_82Ordinal(int ordinal) {
        return typeAPI.getSt_82Ordinal(ordinal);
    }

    public int getSt_83Ordinal(int ordinal) {
        return typeAPI.getSt_83Ordinal(ordinal);
    }

    public int getSt_84Ordinal(int ordinal) {
        return typeAPI.getSt_84Ordinal(ordinal);
    }

    public int getSt_85Ordinal(int ordinal) {
        return typeAPI.getSt_85Ordinal(ordinal);
    }

    public int getSt_86Ordinal(int ordinal) {
        return typeAPI.getSt_86Ordinal(ordinal);
    }

    public int getSt_87Ordinal(int ordinal) {
        return typeAPI.getSt_87Ordinal(ordinal);
    }

    public int getSt_88Ordinal(int ordinal) {
        return typeAPI.getSt_88Ordinal(ordinal);
    }

    public int getSt_89Ordinal(int ordinal) {
        return typeAPI.getSt_89Ordinal(ordinal);
    }

    public int getSt_90Ordinal(int ordinal) {
        return typeAPI.getSt_90Ordinal(ordinal);
    }

    public int getSt_91Ordinal(int ordinal) {
        return typeAPI.getSt_91Ordinal(ordinal);
    }

    public int getSt_92Ordinal(int ordinal) {
        return typeAPI.getSt_92Ordinal(ordinal);
    }

    public int getSt_93Ordinal(int ordinal) {
        return typeAPI.getSt_93Ordinal(ordinal);
    }

    public int getSt_94Ordinal(int ordinal) {
        return typeAPI.getSt_94Ordinal(ordinal);
    }

    public int getSt_95Ordinal(int ordinal) {
        return typeAPI.getSt_95Ordinal(ordinal);
    }

    public int getSt_96Ordinal(int ordinal) {
        return typeAPI.getSt_96Ordinal(ordinal);
    }

    public int getSt_97Ordinal(int ordinal) {
        return typeAPI.getSt_97Ordinal(ordinal);
    }

    public int getSt_98Ordinal(int ordinal) {
        return typeAPI.getSt_98Ordinal(ordinal);
    }

    public int getSt_99Ordinal(int ordinal) {
        return typeAPI.getSt_99Ordinal(ordinal);
    }

    public int getSt_100Ordinal(int ordinal) {
        return typeAPI.getSt_100Ordinal(ordinal);
    }

    public int getSt_101Ordinal(int ordinal) {
        return typeAPI.getSt_101Ordinal(ordinal);
    }

    public int getSt_102Ordinal(int ordinal) {
        return typeAPI.getSt_102Ordinal(ordinal);
    }

    public int getSt_103Ordinal(int ordinal) {
        return typeAPI.getSt_103Ordinal(ordinal);
    }

    public int getSt_104Ordinal(int ordinal) {
        return typeAPI.getSt_104Ordinal(ordinal);
    }

    public int getSt_105Ordinal(int ordinal) {
        return typeAPI.getSt_105Ordinal(ordinal);
    }

    public int getSt_106Ordinal(int ordinal) {
        return typeAPI.getSt_106Ordinal(ordinal);
    }

    public int getSt_107Ordinal(int ordinal) {
        return typeAPI.getSt_107Ordinal(ordinal);
    }

    public int getSt_108Ordinal(int ordinal) {
        return typeAPI.getSt_108Ordinal(ordinal);
    }

    public int getSt_109Ordinal(int ordinal) {
        return typeAPI.getSt_109Ordinal(ordinal);
    }

    public int getSt_110Ordinal(int ordinal) {
        return typeAPI.getSt_110Ordinal(ordinal);
    }

    public int getSt_111Ordinal(int ordinal) {
        return typeAPI.getSt_111Ordinal(ordinal);
    }

    public int getSt_112Ordinal(int ordinal) {
        return typeAPI.getSt_112Ordinal(ordinal);
    }

    public int getSt_113Ordinal(int ordinal) {
        return typeAPI.getSt_113Ordinal(ordinal);
    }

    public int getSt_114Ordinal(int ordinal) {
        return typeAPI.getSt_114Ordinal(ordinal);
    }

    public int getSt_115Ordinal(int ordinal) {
        return typeAPI.getSt_115Ordinal(ordinal);
    }

    public int getSt_116Ordinal(int ordinal) {
        return typeAPI.getSt_116Ordinal(ordinal);
    }

    public int getSt_117Ordinal(int ordinal) {
        return typeAPI.getSt_117Ordinal(ordinal);
    }

    public int getSt_118Ordinal(int ordinal) {
        return typeAPI.getSt_118Ordinal(ordinal);
    }

    public int getSt_119Ordinal(int ordinal) {
        return typeAPI.getSt_119Ordinal(ordinal);
    }

    public int getSt_120Ordinal(int ordinal) {
        return typeAPI.getSt_120Ordinal(ordinal);
    }

    public int getSt_121Ordinal(int ordinal) {
        return typeAPI.getSt_121Ordinal(ordinal);
    }

    public int getSt_122Ordinal(int ordinal) {
        return typeAPI.getSt_122Ordinal(ordinal);
    }

    public int getSt_123Ordinal(int ordinal) {
        return typeAPI.getSt_123Ordinal(ordinal);
    }

    public int getSt_124Ordinal(int ordinal) {
        return typeAPI.getSt_124Ordinal(ordinal);
    }

    public int getSt_125Ordinal(int ordinal) {
        return typeAPI.getSt_125Ordinal(ordinal);
    }

    public int getSt_126Ordinal(int ordinal) {
        return typeAPI.getSt_126Ordinal(ordinal);
    }

    public int getSt_127Ordinal(int ordinal) {
        return typeAPI.getSt_127Ordinal(ordinal);
    }

    public int getSt_128Ordinal(int ordinal) {
        return typeAPI.getSt_128Ordinal(ordinal);
    }

    public int getSt_129Ordinal(int ordinal) {
        return typeAPI.getSt_129Ordinal(ordinal);
    }

    public int getSt_130Ordinal(int ordinal) {
        return typeAPI.getSt_130Ordinal(ordinal);
    }

    public int getSt_131Ordinal(int ordinal) {
        return typeAPI.getSt_131Ordinal(ordinal);
    }

    public int getSt_132Ordinal(int ordinal) {
        return typeAPI.getSt_132Ordinal(ordinal);
    }

    public int getSt_133Ordinal(int ordinal) {
        return typeAPI.getSt_133Ordinal(ordinal);
    }

    public int getSt_134Ordinal(int ordinal) {
        return typeAPI.getSt_134Ordinal(ordinal);
    }

    public int getSt_135Ordinal(int ordinal) {
        return typeAPI.getSt_135Ordinal(ordinal);
    }

    public int getSt_136Ordinal(int ordinal) {
        return typeAPI.getSt_136Ordinal(ordinal);
    }

    public int getSt_137Ordinal(int ordinal) {
        return typeAPI.getSt_137Ordinal(ordinal);
    }

    public int getSt_138Ordinal(int ordinal) {
        return typeAPI.getSt_138Ordinal(ordinal);
    }

    public int getSt_139Ordinal(int ordinal) {
        return typeAPI.getSt_139Ordinal(ordinal);
    }

    public int getSt_140Ordinal(int ordinal) {
        return typeAPI.getSt_140Ordinal(ordinal);
    }

    public int getSt_141Ordinal(int ordinal) {
        return typeAPI.getSt_141Ordinal(ordinal);
    }

    public int getSt_142Ordinal(int ordinal) {
        return typeAPI.getSt_142Ordinal(ordinal);
    }

    public int getSt_143Ordinal(int ordinal) {
        return typeAPI.getSt_143Ordinal(ordinal);
    }

    public int getSt_144Ordinal(int ordinal) {
        return typeAPI.getSt_144Ordinal(ordinal);
    }

    public int getSt_145Ordinal(int ordinal) {
        return typeAPI.getSt_145Ordinal(ordinal);
    }

    public int getSt_146Ordinal(int ordinal) {
        return typeAPI.getSt_146Ordinal(ordinal);
    }

    public int getSt_147Ordinal(int ordinal) {
        return typeAPI.getSt_147Ordinal(ordinal);
    }

    public int getSt_148Ordinal(int ordinal) {
        return typeAPI.getSt_148Ordinal(ordinal);
    }

    public int getSt_149Ordinal(int ordinal) {
        return typeAPI.getSt_149Ordinal(ordinal);
    }

    public int getSt_150Ordinal(int ordinal) {
        return typeAPI.getSt_150Ordinal(ordinal);
    }

    public int getSt_151Ordinal(int ordinal) {
        return typeAPI.getSt_151Ordinal(ordinal);
    }

    public int getSt_152Ordinal(int ordinal) {
        return typeAPI.getSt_152Ordinal(ordinal);
    }

    public int getSt_153Ordinal(int ordinal) {
        return typeAPI.getSt_153Ordinal(ordinal);
    }

    public int getSt_154Ordinal(int ordinal) {
        return typeAPI.getSt_154Ordinal(ordinal);
    }

    public int getSt_155Ordinal(int ordinal) {
        return typeAPI.getSt_155Ordinal(ordinal);
    }

    public int getSt_156Ordinal(int ordinal) {
        return typeAPI.getSt_156Ordinal(ordinal);
    }

    public int getSt_157Ordinal(int ordinal) {
        return typeAPI.getSt_157Ordinal(ordinal);
    }

    public int getSt_158Ordinal(int ordinal) {
        return typeAPI.getSt_158Ordinal(ordinal);
    }

    public int getSt_159Ordinal(int ordinal) {
        return typeAPI.getSt_159Ordinal(ordinal);
    }

    public int getSt_160Ordinal(int ordinal) {
        return typeAPI.getSt_160Ordinal(ordinal);
    }

    public int getSt_161Ordinal(int ordinal) {
        return typeAPI.getSt_161Ordinal(ordinal);
    }

    public int getSt_162Ordinal(int ordinal) {
        return typeAPI.getSt_162Ordinal(ordinal);
    }

    public int getSt_163Ordinal(int ordinal) {
        return typeAPI.getSt_163Ordinal(ordinal);
    }

    public int getSt_164Ordinal(int ordinal) {
        return typeAPI.getSt_164Ordinal(ordinal);
    }

    public int getSt_165Ordinal(int ordinal) {
        return typeAPI.getSt_165Ordinal(ordinal);
    }

    public int getSt_166Ordinal(int ordinal) {
        return typeAPI.getSt_166Ordinal(ordinal);
    }

    public int getSt_167Ordinal(int ordinal) {
        return typeAPI.getSt_167Ordinal(ordinal);
    }

    public int getSt_168Ordinal(int ordinal) {
        return typeAPI.getSt_168Ordinal(ordinal);
    }

    public int getSt_169Ordinal(int ordinal) {
        return typeAPI.getSt_169Ordinal(ordinal);
    }

    public int getSt_170Ordinal(int ordinal) {
        return typeAPI.getSt_170Ordinal(ordinal);
    }

    public int getSt_171Ordinal(int ordinal) {
        return typeAPI.getSt_171Ordinal(ordinal);
    }

    public int getSt_172Ordinal(int ordinal) {
        return typeAPI.getSt_172Ordinal(ordinal);
    }

    public int getSt_173Ordinal(int ordinal) {
        return typeAPI.getSt_173Ordinal(ordinal);
    }

    public int getSt_174Ordinal(int ordinal) {
        return typeAPI.getSt_174Ordinal(ordinal);
    }

    public int getSt_175Ordinal(int ordinal) {
        return typeAPI.getSt_175Ordinal(ordinal);
    }

    public int getSt_176Ordinal(int ordinal) {
        return typeAPI.getSt_176Ordinal(ordinal);
    }

    public int getSt_177Ordinal(int ordinal) {
        return typeAPI.getSt_177Ordinal(ordinal);
    }

    public int getSt_178Ordinal(int ordinal) {
        return typeAPI.getSt_178Ordinal(ordinal);
    }

    public int getSt_179Ordinal(int ordinal) {
        return typeAPI.getSt_179Ordinal(ordinal);
    }

    public int getSt_180Ordinal(int ordinal) {
        return typeAPI.getSt_180Ordinal(ordinal);
    }

    public int getSt_181Ordinal(int ordinal) {
        return typeAPI.getSt_181Ordinal(ordinal);
    }

    public int getSt_182Ordinal(int ordinal) {
        return typeAPI.getSt_182Ordinal(ordinal);
    }

    public int getSt_183Ordinal(int ordinal) {
        return typeAPI.getSt_183Ordinal(ordinal);
    }

    public int getSt_184Ordinal(int ordinal) {
        return typeAPI.getSt_184Ordinal(ordinal);
    }

    public int getSt_185Ordinal(int ordinal) {
        return typeAPI.getSt_185Ordinal(ordinal);
    }

    public int getSt_186Ordinal(int ordinal) {
        return typeAPI.getSt_186Ordinal(ordinal);
    }

    public int getSt_187Ordinal(int ordinal) {
        return typeAPI.getSt_187Ordinal(ordinal);
    }

    public int getSt_188Ordinal(int ordinal) {
        return typeAPI.getSt_188Ordinal(ordinal);
    }

    public int getSt_189Ordinal(int ordinal) {
        return typeAPI.getSt_189Ordinal(ordinal);
    }

    public int getSt_190Ordinal(int ordinal) {
        return typeAPI.getSt_190Ordinal(ordinal);
    }

    public int getSt_191Ordinal(int ordinal) {
        return typeAPI.getSt_191Ordinal(ordinal);
    }

    public int getSt_192Ordinal(int ordinal) {
        return typeAPI.getSt_192Ordinal(ordinal);
    }

    public int getSt_193Ordinal(int ordinal) {
        return typeAPI.getSt_193Ordinal(ordinal);
    }

    public int getSt_194Ordinal(int ordinal) {
        return typeAPI.getSt_194Ordinal(ordinal);
    }

    public int getSt_195Ordinal(int ordinal) {
        return typeAPI.getSt_195Ordinal(ordinal);
    }

    public int getSt_196Ordinal(int ordinal) {
        return typeAPI.getSt_196Ordinal(ordinal);
    }

    public int getSt_197Ordinal(int ordinal) {
        return typeAPI.getSt_197Ordinal(ordinal);
    }

    public int getSt_198Ordinal(int ordinal) {
        return typeAPI.getSt_198Ordinal(ordinal);
    }

    public int getSt_199Ordinal(int ordinal) {
        return typeAPI.getSt_199Ordinal(ordinal);
    }

    public int getSt_200Ordinal(int ordinal) {
        return typeAPI.getSt_200Ordinal(ordinal);
    }

    public int getSt_201Ordinal(int ordinal) {
        return typeAPI.getSt_201Ordinal(ordinal);
    }

    public int getSt_202Ordinal(int ordinal) {
        return typeAPI.getSt_202Ordinal(ordinal);
    }

    public int getSt_203Ordinal(int ordinal) {
        return typeAPI.getSt_203Ordinal(ordinal);
    }

    public int getSt_204Ordinal(int ordinal) {
        return typeAPI.getSt_204Ordinal(ordinal);
    }

    public int getSt_205Ordinal(int ordinal) {
        return typeAPI.getSt_205Ordinal(ordinal);
    }

    public int getSt_206Ordinal(int ordinal) {
        return typeAPI.getSt_206Ordinal(ordinal);
    }

    public int getSt_207Ordinal(int ordinal) {
        return typeAPI.getSt_207Ordinal(ordinal);
    }

    public int getSt_208Ordinal(int ordinal) {
        return typeAPI.getSt_208Ordinal(ordinal);
    }

    public int getSt_209Ordinal(int ordinal) {
        return typeAPI.getSt_209Ordinal(ordinal);
    }

    public int getSt_210Ordinal(int ordinal) {
        return typeAPI.getSt_210Ordinal(ordinal);
    }

    public int getSt_211Ordinal(int ordinal) {
        return typeAPI.getSt_211Ordinal(ordinal);
    }

    public int getSt_212Ordinal(int ordinal) {
        return typeAPI.getSt_212Ordinal(ordinal);
    }

    public int getSt_213Ordinal(int ordinal) {
        return typeAPI.getSt_213Ordinal(ordinal);
    }

    public int getSt_214Ordinal(int ordinal) {
        return typeAPI.getSt_214Ordinal(ordinal);
    }

    public int getSt_215Ordinal(int ordinal) {
        return typeAPI.getSt_215Ordinal(ordinal);
    }

    public int getSt_216Ordinal(int ordinal) {
        return typeAPI.getSt_216Ordinal(ordinal);
    }

    public int getSt_217Ordinal(int ordinal) {
        return typeAPI.getSt_217Ordinal(ordinal);
    }

    public int getSt_218Ordinal(int ordinal) {
        return typeAPI.getSt_218Ordinal(ordinal);
    }

    public int getSt_219Ordinal(int ordinal) {
        return typeAPI.getSt_219Ordinal(ordinal);
    }

    public int getSt_220Ordinal(int ordinal) {
        return typeAPI.getSt_220Ordinal(ordinal);
    }

    public int getSt_221Ordinal(int ordinal) {
        return typeAPI.getSt_221Ordinal(ordinal);
    }

    public int getSt_222Ordinal(int ordinal) {
        return typeAPI.getSt_222Ordinal(ordinal);
    }

    public int getSt_223Ordinal(int ordinal) {
        return typeAPI.getSt_223Ordinal(ordinal);
    }

    public int getSt_224Ordinal(int ordinal) {
        return typeAPI.getSt_224Ordinal(ordinal);
    }

    public int getSt_225Ordinal(int ordinal) {
        return typeAPI.getSt_225Ordinal(ordinal);
    }

    public int getSt_226Ordinal(int ordinal) {
        return typeAPI.getSt_226Ordinal(ordinal);
    }

    public int getSt_227Ordinal(int ordinal) {
        return typeAPI.getSt_227Ordinal(ordinal);
    }

    public int getSt_228Ordinal(int ordinal) {
        return typeAPI.getSt_228Ordinal(ordinal);
    }

    public int getSt_229Ordinal(int ordinal) {
        return typeAPI.getSt_229Ordinal(ordinal);
    }

    public int getSt_230Ordinal(int ordinal) {
        return typeAPI.getSt_230Ordinal(ordinal);
    }

    public TurboCollectionsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}
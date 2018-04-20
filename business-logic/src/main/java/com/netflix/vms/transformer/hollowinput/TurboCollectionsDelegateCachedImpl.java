package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TurboCollectionsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, TurboCollectionsDelegate {

    private final Long id;
    private final int prefixOrdinal;
    private final int desOrdinal;
    private final int evi_nOrdinal;
    private final int char_nOrdinal;
    private final int nav_snOrdinal;
    private final int dnOrdinal;
    private final int kc_cnOrdinal;
    private final int st_2Ordinal;
    private final int bmt_nOrdinal;
    private final int st_1Ordinal;
    private final int st_4Ordinal;
    private final int st_3Ordinal;
    private final int st_0Ordinal;
    private final int st_9Ordinal;
    private final int snOrdinal;
    private final int kag_knOrdinal;
    private final int roar_nOrdinal;
    private final int st_6Ordinal;
    private final int st_5Ordinal;
    private final int st_8Ordinal;
    private final int tdnOrdinal;
    private final int st_7Ordinal;
    private final int st_10Ordinal;
    private final int st_11Ordinal;
    private final int st_12Ordinal;
    private final int st_13Ordinal;
    private final int st_14Ordinal;
    private final int st_15Ordinal;
    private final int st_16Ordinal;
    private final int st_17Ordinal;
    private final int st_18Ordinal;
    private final int st_19Ordinal;
    private final int st_20Ordinal;
    private final int st_21Ordinal;
    private final int st_22Ordinal;
    private final int st_23Ordinal;
    private final int st_24Ordinal;
    private final int st_25Ordinal;
    private final int st_26Ordinal;
    private final int st_27Ordinal;
    private final int st_28Ordinal;
    private final int st_29Ordinal;
    private final int st_30Ordinal;
    private final int st_31Ordinal;
    private final int st_32Ordinal;
    private final int st_33Ordinal;
    private final int st_34Ordinal;
    private final int st_35Ordinal;
    private final int st_36Ordinal;
    private final int st_37Ordinal;
    private final int st_38Ordinal;
    private final int st_39Ordinal;
    private final int st_40Ordinal;
    private final int st_41Ordinal;
    private final int st_42Ordinal;
    private final int st_43Ordinal;
    private final int st_44Ordinal;
    private final int st_45Ordinal;
    private final int st_46Ordinal;
    private final int st_47Ordinal;
    private final int st_48Ordinal;
    private final int st_49Ordinal;
    private final int st_50Ordinal;
    private final int st_51Ordinal;
    private final int st_52Ordinal;
    private final int st_53Ordinal;
    private final int st_54Ordinal;
    private final int st_55Ordinal;
    private final int st_56Ordinal;
    private final int st_57Ordinal;
    private final int st_58Ordinal;
    private final int st_59Ordinal;
    private final int st_60Ordinal;
    private final int st_61Ordinal;
    private final int st_62Ordinal;
    private final int st_63Ordinal;
    private final int st_64Ordinal;
    private final int st_65Ordinal;
    private final int st_66Ordinal;
    private final int st_67Ordinal;
    private final int st_68Ordinal;
    private final int st_69Ordinal;
    private final int st_70Ordinal;
    private final int st_71Ordinal;
    private final int st_72Ordinal;
    private final int st_73Ordinal;
    private final int st_74Ordinal;
    private final int st_75Ordinal;
    private final int st_76Ordinal;
    private final int st_77Ordinal;
    private final int st_78Ordinal;
    private final int st_79Ordinal;
    private final int st_80Ordinal;
    private final int st_81Ordinal;
    private final int st_82Ordinal;
    private final int st_83Ordinal;
    private final int st_84Ordinal;
    private final int st_85Ordinal;
    private final int st_86Ordinal;
    private final int st_87Ordinal;
    private final int st_88Ordinal;
    private final int st_89Ordinal;
    private final int st_90Ordinal;
    private final int st_91Ordinal;
    private final int st_92Ordinal;
    private final int st_93Ordinal;
    private final int st_94Ordinal;
    private final int st_95Ordinal;
    private final int st_96Ordinal;
    private final int st_97Ordinal;
    private final int st_98Ordinal;
    private final int st_99Ordinal;
    private final int st_100Ordinal;
    private final int st_101Ordinal;
    private final int st_102Ordinal;
    private final int st_103Ordinal;
    private final int st_104Ordinal;
    private final int st_105Ordinal;
    private final int st_106Ordinal;
    private final int st_107Ordinal;
    private final int st_108Ordinal;
    private final int st_109Ordinal;
    private final int st_110Ordinal;
    private final int st_111Ordinal;
    private final int st_112Ordinal;
    private final int st_113Ordinal;
    private final int st_114Ordinal;
    private final int st_115Ordinal;
    private final int st_116Ordinal;
    private final int st_117Ordinal;
    private final int st_118Ordinal;
    private final int st_119Ordinal;
    private final int st_120Ordinal;
    private final int st_121Ordinal;
    private final int st_122Ordinal;
    private final int st_123Ordinal;
    private final int st_124Ordinal;
    private final int st_125Ordinal;
    private final int st_126Ordinal;
    private final int st_127Ordinal;
    private final int st_128Ordinal;
    private final int st_129Ordinal;
    private final int st_130Ordinal;
    private final int st_131Ordinal;
    private final int st_132Ordinal;
    private final int st_133Ordinal;
    private final int st_134Ordinal;
    private final int st_135Ordinal;
    private final int st_136Ordinal;
    private final int st_137Ordinal;
    private final int st_138Ordinal;
    private final int st_139Ordinal;
    private final int st_140Ordinal;
    private final int st_141Ordinal;
    private final int st_142Ordinal;
    private final int st_143Ordinal;
    private final int st_144Ordinal;
    private final int st_145Ordinal;
    private final int st_146Ordinal;
    private final int st_147Ordinal;
    private final int st_148Ordinal;
    private final int st_149Ordinal;
    private final int st_150Ordinal;
    private final int st_151Ordinal;
    private final int st_152Ordinal;
    private final int st_153Ordinal;
    private final int st_154Ordinal;
    private final int st_155Ordinal;
    private final int st_156Ordinal;
    private final int st_157Ordinal;
    private final int st_158Ordinal;
    private final int st_159Ordinal;
    private final int st_160Ordinal;
    private final int st_161Ordinal;
    private final int st_162Ordinal;
    private final int st_163Ordinal;
    private final int st_164Ordinal;
    private final int st_165Ordinal;
    private final int st_166Ordinal;
    private final int st_167Ordinal;
    private final int st_168Ordinal;
    private final int st_169Ordinal;
    private final int st_170Ordinal;
    private final int st_171Ordinal;
    private final int st_172Ordinal;
    private final int st_173Ordinal;
    private final int st_174Ordinal;
    private final int st_175Ordinal;
    private final int st_176Ordinal;
    private final int st_177Ordinal;
    private final int st_178Ordinal;
    private final int st_179Ordinal;
    private final int st_180Ordinal;
    private final int st_181Ordinal;
    private final int st_182Ordinal;
    private final int st_183Ordinal;
    private final int st_184Ordinal;
    private final int st_185Ordinal;
    private final int st_186Ordinal;
    private final int st_187Ordinal;
    private final int st_188Ordinal;
    private final int st_189Ordinal;
    private final int st_190Ordinal;
    private final int st_191Ordinal;
    private final int st_192Ordinal;
    private final int st_193Ordinal;
    private final int st_194Ordinal;
    private final int st_195Ordinal;
    private final int st_196Ordinal;
    private final int st_197Ordinal;
    private final int st_198Ordinal;
    private final int st_199Ordinal;
    private final int st_200Ordinal;
    private final int st_201Ordinal;
    private final int st_202Ordinal;
    private final int st_203Ordinal;
    private final int st_204Ordinal;
    private final int st_205Ordinal;
    private final int st_206Ordinal;
    private final int st_207Ordinal;
    private final int st_208Ordinal;
    private final int st_209Ordinal;
    private final int st_210Ordinal;
    private final int st_211Ordinal;
    private final int st_212Ordinal;
    private final int st_213Ordinal;
    private final int st_214Ordinal;
    private final int st_215Ordinal;
    private final int st_216Ordinal;
    private final int st_217Ordinal;
    private final int st_218Ordinal;
    private final int st_219Ordinal;
    private final int st_220Ordinal;
    private final int st_221Ordinal;
    private final int st_222Ordinal;
    private final int st_223Ordinal;
    private final int st_224Ordinal;
    private final int st_225Ordinal;
    private final int st_226Ordinal;
    private final int st_227Ordinal;
    private final int st_228Ordinal;
    private final int st_229Ordinal;
    private final int st_230Ordinal;
    private TurboCollectionsTypeAPI typeAPI;

    public TurboCollectionsDelegateCachedImpl(TurboCollectionsTypeAPI typeAPI, int ordinal) {
        this.id = typeAPI.getIdBoxed(ordinal);
        this.prefixOrdinal = typeAPI.getPrefixOrdinal(ordinal);
        this.desOrdinal = typeAPI.getDesOrdinal(ordinal);
        this.evi_nOrdinal = typeAPI.getEvi_nOrdinal(ordinal);
        this.char_nOrdinal = typeAPI.getChar_nOrdinal(ordinal);
        this.nav_snOrdinal = typeAPI.getNav_snOrdinal(ordinal);
        this.dnOrdinal = typeAPI.getDnOrdinal(ordinal);
        this.kc_cnOrdinal = typeAPI.getKc_cnOrdinal(ordinal);
        this.st_2Ordinal = typeAPI.getSt_2Ordinal(ordinal);
        this.bmt_nOrdinal = typeAPI.getBmt_nOrdinal(ordinal);
        this.st_1Ordinal = typeAPI.getSt_1Ordinal(ordinal);
        this.st_4Ordinal = typeAPI.getSt_4Ordinal(ordinal);
        this.st_3Ordinal = typeAPI.getSt_3Ordinal(ordinal);
        this.st_0Ordinal = typeAPI.getSt_0Ordinal(ordinal);
        this.st_9Ordinal = typeAPI.getSt_9Ordinal(ordinal);
        this.snOrdinal = typeAPI.getSnOrdinal(ordinal);
        this.kag_knOrdinal = typeAPI.getKag_knOrdinal(ordinal);
        this.roar_nOrdinal = typeAPI.getRoar_nOrdinal(ordinal);
        this.st_6Ordinal = typeAPI.getSt_6Ordinal(ordinal);
        this.st_5Ordinal = typeAPI.getSt_5Ordinal(ordinal);
        this.st_8Ordinal = typeAPI.getSt_8Ordinal(ordinal);
        this.tdnOrdinal = typeAPI.getTdnOrdinal(ordinal);
        this.st_7Ordinal = typeAPI.getSt_7Ordinal(ordinal);
        this.st_10Ordinal = typeAPI.getSt_10Ordinal(ordinal);
        this.st_11Ordinal = typeAPI.getSt_11Ordinal(ordinal);
        this.st_12Ordinal = typeAPI.getSt_12Ordinal(ordinal);
        this.st_13Ordinal = typeAPI.getSt_13Ordinal(ordinal);
        this.st_14Ordinal = typeAPI.getSt_14Ordinal(ordinal);
        this.st_15Ordinal = typeAPI.getSt_15Ordinal(ordinal);
        this.st_16Ordinal = typeAPI.getSt_16Ordinal(ordinal);
        this.st_17Ordinal = typeAPI.getSt_17Ordinal(ordinal);
        this.st_18Ordinal = typeAPI.getSt_18Ordinal(ordinal);
        this.st_19Ordinal = typeAPI.getSt_19Ordinal(ordinal);
        this.st_20Ordinal = typeAPI.getSt_20Ordinal(ordinal);
        this.st_21Ordinal = typeAPI.getSt_21Ordinal(ordinal);
        this.st_22Ordinal = typeAPI.getSt_22Ordinal(ordinal);
        this.st_23Ordinal = typeAPI.getSt_23Ordinal(ordinal);
        this.st_24Ordinal = typeAPI.getSt_24Ordinal(ordinal);
        this.st_25Ordinal = typeAPI.getSt_25Ordinal(ordinal);
        this.st_26Ordinal = typeAPI.getSt_26Ordinal(ordinal);
        this.st_27Ordinal = typeAPI.getSt_27Ordinal(ordinal);
        this.st_28Ordinal = typeAPI.getSt_28Ordinal(ordinal);
        this.st_29Ordinal = typeAPI.getSt_29Ordinal(ordinal);
        this.st_30Ordinal = typeAPI.getSt_30Ordinal(ordinal);
        this.st_31Ordinal = typeAPI.getSt_31Ordinal(ordinal);
        this.st_32Ordinal = typeAPI.getSt_32Ordinal(ordinal);
        this.st_33Ordinal = typeAPI.getSt_33Ordinal(ordinal);
        this.st_34Ordinal = typeAPI.getSt_34Ordinal(ordinal);
        this.st_35Ordinal = typeAPI.getSt_35Ordinal(ordinal);
        this.st_36Ordinal = typeAPI.getSt_36Ordinal(ordinal);
        this.st_37Ordinal = typeAPI.getSt_37Ordinal(ordinal);
        this.st_38Ordinal = typeAPI.getSt_38Ordinal(ordinal);
        this.st_39Ordinal = typeAPI.getSt_39Ordinal(ordinal);
        this.st_40Ordinal = typeAPI.getSt_40Ordinal(ordinal);
        this.st_41Ordinal = typeAPI.getSt_41Ordinal(ordinal);
        this.st_42Ordinal = typeAPI.getSt_42Ordinal(ordinal);
        this.st_43Ordinal = typeAPI.getSt_43Ordinal(ordinal);
        this.st_44Ordinal = typeAPI.getSt_44Ordinal(ordinal);
        this.st_45Ordinal = typeAPI.getSt_45Ordinal(ordinal);
        this.st_46Ordinal = typeAPI.getSt_46Ordinal(ordinal);
        this.st_47Ordinal = typeAPI.getSt_47Ordinal(ordinal);
        this.st_48Ordinal = typeAPI.getSt_48Ordinal(ordinal);
        this.st_49Ordinal = typeAPI.getSt_49Ordinal(ordinal);
        this.st_50Ordinal = typeAPI.getSt_50Ordinal(ordinal);
        this.st_51Ordinal = typeAPI.getSt_51Ordinal(ordinal);
        this.st_52Ordinal = typeAPI.getSt_52Ordinal(ordinal);
        this.st_53Ordinal = typeAPI.getSt_53Ordinal(ordinal);
        this.st_54Ordinal = typeAPI.getSt_54Ordinal(ordinal);
        this.st_55Ordinal = typeAPI.getSt_55Ordinal(ordinal);
        this.st_56Ordinal = typeAPI.getSt_56Ordinal(ordinal);
        this.st_57Ordinal = typeAPI.getSt_57Ordinal(ordinal);
        this.st_58Ordinal = typeAPI.getSt_58Ordinal(ordinal);
        this.st_59Ordinal = typeAPI.getSt_59Ordinal(ordinal);
        this.st_60Ordinal = typeAPI.getSt_60Ordinal(ordinal);
        this.st_61Ordinal = typeAPI.getSt_61Ordinal(ordinal);
        this.st_62Ordinal = typeAPI.getSt_62Ordinal(ordinal);
        this.st_63Ordinal = typeAPI.getSt_63Ordinal(ordinal);
        this.st_64Ordinal = typeAPI.getSt_64Ordinal(ordinal);
        this.st_65Ordinal = typeAPI.getSt_65Ordinal(ordinal);
        this.st_66Ordinal = typeAPI.getSt_66Ordinal(ordinal);
        this.st_67Ordinal = typeAPI.getSt_67Ordinal(ordinal);
        this.st_68Ordinal = typeAPI.getSt_68Ordinal(ordinal);
        this.st_69Ordinal = typeAPI.getSt_69Ordinal(ordinal);
        this.st_70Ordinal = typeAPI.getSt_70Ordinal(ordinal);
        this.st_71Ordinal = typeAPI.getSt_71Ordinal(ordinal);
        this.st_72Ordinal = typeAPI.getSt_72Ordinal(ordinal);
        this.st_73Ordinal = typeAPI.getSt_73Ordinal(ordinal);
        this.st_74Ordinal = typeAPI.getSt_74Ordinal(ordinal);
        this.st_75Ordinal = typeAPI.getSt_75Ordinal(ordinal);
        this.st_76Ordinal = typeAPI.getSt_76Ordinal(ordinal);
        this.st_77Ordinal = typeAPI.getSt_77Ordinal(ordinal);
        this.st_78Ordinal = typeAPI.getSt_78Ordinal(ordinal);
        this.st_79Ordinal = typeAPI.getSt_79Ordinal(ordinal);
        this.st_80Ordinal = typeAPI.getSt_80Ordinal(ordinal);
        this.st_81Ordinal = typeAPI.getSt_81Ordinal(ordinal);
        this.st_82Ordinal = typeAPI.getSt_82Ordinal(ordinal);
        this.st_83Ordinal = typeAPI.getSt_83Ordinal(ordinal);
        this.st_84Ordinal = typeAPI.getSt_84Ordinal(ordinal);
        this.st_85Ordinal = typeAPI.getSt_85Ordinal(ordinal);
        this.st_86Ordinal = typeAPI.getSt_86Ordinal(ordinal);
        this.st_87Ordinal = typeAPI.getSt_87Ordinal(ordinal);
        this.st_88Ordinal = typeAPI.getSt_88Ordinal(ordinal);
        this.st_89Ordinal = typeAPI.getSt_89Ordinal(ordinal);
        this.st_90Ordinal = typeAPI.getSt_90Ordinal(ordinal);
        this.st_91Ordinal = typeAPI.getSt_91Ordinal(ordinal);
        this.st_92Ordinal = typeAPI.getSt_92Ordinal(ordinal);
        this.st_93Ordinal = typeAPI.getSt_93Ordinal(ordinal);
        this.st_94Ordinal = typeAPI.getSt_94Ordinal(ordinal);
        this.st_95Ordinal = typeAPI.getSt_95Ordinal(ordinal);
        this.st_96Ordinal = typeAPI.getSt_96Ordinal(ordinal);
        this.st_97Ordinal = typeAPI.getSt_97Ordinal(ordinal);
        this.st_98Ordinal = typeAPI.getSt_98Ordinal(ordinal);
        this.st_99Ordinal = typeAPI.getSt_99Ordinal(ordinal);
        this.st_100Ordinal = typeAPI.getSt_100Ordinal(ordinal);
        this.st_101Ordinal = typeAPI.getSt_101Ordinal(ordinal);
        this.st_102Ordinal = typeAPI.getSt_102Ordinal(ordinal);
        this.st_103Ordinal = typeAPI.getSt_103Ordinal(ordinal);
        this.st_104Ordinal = typeAPI.getSt_104Ordinal(ordinal);
        this.st_105Ordinal = typeAPI.getSt_105Ordinal(ordinal);
        this.st_106Ordinal = typeAPI.getSt_106Ordinal(ordinal);
        this.st_107Ordinal = typeAPI.getSt_107Ordinal(ordinal);
        this.st_108Ordinal = typeAPI.getSt_108Ordinal(ordinal);
        this.st_109Ordinal = typeAPI.getSt_109Ordinal(ordinal);
        this.st_110Ordinal = typeAPI.getSt_110Ordinal(ordinal);
        this.st_111Ordinal = typeAPI.getSt_111Ordinal(ordinal);
        this.st_112Ordinal = typeAPI.getSt_112Ordinal(ordinal);
        this.st_113Ordinal = typeAPI.getSt_113Ordinal(ordinal);
        this.st_114Ordinal = typeAPI.getSt_114Ordinal(ordinal);
        this.st_115Ordinal = typeAPI.getSt_115Ordinal(ordinal);
        this.st_116Ordinal = typeAPI.getSt_116Ordinal(ordinal);
        this.st_117Ordinal = typeAPI.getSt_117Ordinal(ordinal);
        this.st_118Ordinal = typeAPI.getSt_118Ordinal(ordinal);
        this.st_119Ordinal = typeAPI.getSt_119Ordinal(ordinal);
        this.st_120Ordinal = typeAPI.getSt_120Ordinal(ordinal);
        this.st_121Ordinal = typeAPI.getSt_121Ordinal(ordinal);
        this.st_122Ordinal = typeAPI.getSt_122Ordinal(ordinal);
        this.st_123Ordinal = typeAPI.getSt_123Ordinal(ordinal);
        this.st_124Ordinal = typeAPI.getSt_124Ordinal(ordinal);
        this.st_125Ordinal = typeAPI.getSt_125Ordinal(ordinal);
        this.st_126Ordinal = typeAPI.getSt_126Ordinal(ordinal);
        this.st_127Ordinal = typeAPI.getSt_127Ordinal(ordinal);
        this.st_128Ordinal = typeAPI.getSt_128Ordinal(ordinal);
        this.st_129Ordinal = typeAPI.getSt_129Ordinal(ordinal);
        this.st_130Ordinal = typeAPI.getSt_130Ordinal(ordinal);
        this.st_131Ordinal = typeAPI.getSt_131Ordinal(ordinal);
        this.st_132Ordinal = typeAPI.getSt_132Ordinal(ordinal);
        this.st_133Ordinal = typeAPI.getSt_133Ordinal(ordinal);
        this.st_134Ordinal = typeAPI.getSt_134Ordinal(ordinal);
        this.st_135Ordinal = typeAPI.getSt_135Ordinal(ordinal);
        this.st_136Ordinal = typeAPI.getSt_136Ordinal(ordinal);
        this.st_137Ordinal = typeAPI.getSt_137Ordinal(ordinal);
        this.st_138Ordinal = typeAPI.getSt_138Ordinal(ordinal);
        this.st_139Ordinal = typeAPI.getSt_139Ordinal(ordinal);
        this.st_140Ordinal = typeAPI.getSt_140Ordinal(ordinal);
        this.st_141Ordinal = typeAPI.getSt_141Ordinal(ordinal);
        this.st_142Ordinal = typeAPI.getSt_142Ordinal(ordinal);
        this.st_143Ordinal = typeAPI.getSt_143Ordinal(ordinal);
        this.st_144Ordinal = typeAPI.getSt_144Ordinal(ordinal);
        this.st_145Ordinal = typeAPI.getSt_145Ordinal(ordinal);
        this.st_146Ordinal = typeAPI.getSt_146Ordinal(ordinal);
        this.st_147Ordinal = typeAPI.getSt_147Ordinal(ordinal);
        this.st_148Ordinal = typeAPI.getSt_148Ordinal(ordinal);
        this.st_149Ordinal = typeAPI.getSt_149Ordinal(ordinal);
        this.st_150Ordinal = typeAPI.getSt_150Ordinal(ordinal);
        this.st_151Ordinal = typeAPI.getSt_151Ordinal(ordinal);
        this.st_152Ordinal = typeAPI.getSt_152Ordinal(ordinal);
        this.st_153Ordinal = typeAPI.getSt_153Ordinal(ordinal);
        this.st_154Ordinal = typeAPI.getSt_154Ordinal(ordinal);
        this.st_155Ordinal = typeAPI.getSt_155Ordinal(ordinal);
        this.st_156Ordinal = typeAPI.getSt_156Ordinal(ordinal);
        this.st_157Ordinal = typeAPI.getSt_157Ordinal(ordinal);
        this.st_158Ordinal = typeAPI.getSt_158Ordinal(ordinal);
        this.st_159Ordinal = typeAPI.getSt_159Ordinal(ordinal);
        this.st_160Ordinal = typeAPI.getSt_160Ordinal(ordinal);
        this.st_161Ordinal = typeAPI.getSt_161Ordinal(ordinal);
        this.st_162Ordinal = typeAPI.getSt_162Ordinal(ordinal);
        this.st_163Ordinal = typeAPI.getSt_163Ordinal(ordinal);
        this.st_164Ordinal = typeAPI.getSt_164Ordinal(ordinal);
        this.st_165Ordinal = typeAPI.getSt_165Ordinal(ordinal);
        this.st_166Ordinal = typeAPI.getSt_166Ordinal(ordinal);
        this.st_167Ordinal = typeAPI.getSt_167Ordinal(ordinal);
        this.st_168Ordinal = typeAPI.getSt_168Ordinal(ordinal);
        this.st_169Ordinal = typeAPI.getSt_169Ordinal(ordinal);
        this.st_170Ordinal = typeAPI.getSt_170Ordinal(ordinal);
        this.st_171Ordinal = typeAPI.getSt_171Ordinal(ordinal);
        this.st_172Ordinal = typeAPI.getSt_172Ordinal(ordinal);
        this.st_173Ordinal = typeAPI.getSt_173Ordinal(ordinal);
        this.st_174Ordinal = typeAPI.getSt_174Ordinal(ordinal);
        this.st_175Ordinal = typeAPI.getSt_175Ordinal(ordinal);
        this.st_176Ordinal = typeAPI.getSt_176Ordinal(ordinal);
        this.st_177Ordinal = typeAPI.getSt_177Ordinal(ordinal);
        this.st_178Ordinal = typeAPI.getSt_178Ordinal(ordinal);
        this.st_179Ordinal = typeAPI.getSt_179Ordinal(ordinal);
        this.st_180Ordinal = typeAPI.getSt_180Ordinal(ordinal);
        this.st_181Ordinal = typeAPI.getSt_181Ordinal(ordinal);
        this.st_182Ordinal = typeAPI.getSt_182Ordinal(ordinal);
        this.st_183Ordinal = typeAPI.getSt_183Ordinal(ordinal);
        this.st_184Ordinal = typeAPI.getSt_184Ordinal(ordinal);
        this.st_185Ordinal = typeAPI.getSt_185Ordinal(ordinal);
        this.st_186Ordinal = typeAPI.getSt_186Ordinal(ordinal);
        this.st_187Ordinal = typeAPI.getSt_187Ordinal(ordinal);
        this.st_188Ordinal = typeAPI.getSt_188Ordinal(ordinal);
        this.st_189Ordinal = typeAPI.getSt_189Ordinal(ordinal);
        this.st_190Ordinal = typeAPI.getSt_190Ordinal(ordinal);
        this.st_191Ordinal = typeAPI.getSt_191Ordinal(ordinal);
        this.st_192Ordinal = typeAPI.getSt_192Ordinal(ordinal);
        this.st_193Ordinal = typeAPI.getSt_193Ordinal(ordinal);
        this.st_194Ordinal = typeAPI.getSt_194Ordinal(ordinal);
        this.st_195Ordinal = typeAPI.getSt_195Ordinal(ordinal);
        this.st_196Ordinal = typeAPI.getSt_196Ordinal(ordinal);
        this.st_197Ordinal = typeAPI.getSt_197Ordinal(ordinal);
        this.st_198Ordinal = typeAPI.getSt_198Ordinal(ordinal);
        this.st_199Ordinal = typeAPI.getSt_199Ordinal(ordinal);
        this.st_200Ordinal = typeAPI.getSt_200Ordinal(ordinal);
        this.st_201Ordinal = typeAPI.getSt_201Ordinal(ordinal);
        this.st_202Ordinal = typeAPI.getSt_202Ordinal(ordinal);
        this.st_203Ordinal = typeAPI.getSt_203Ordinal(ordinal);
        this.st_204Ordinal = typeAPI.getSt_204Ordinal(ordinal);
        this.st_205Ordinal = typeAPI.getSt_205Ordinal(ordinal);
        this.st_206Ordinal = typeAPI.getSt_206Ordinal(ordinal);
        this.st_207Ordinal = typeAPI.getSt_207Ordinal(ordinal);
        this.st_208Ordinal = typeAPI.getSt_208Ordinal(ordinal);
        this.st_209Ordinal = typeAPI.getSt_209Ordinal(ordinal);
        this.st_210Ordinal = typeAPI.getSt_210Ordinal(ordinal);
        this.st_211Ordinal = typeAPI.getSt_211Ordinal(ordinal);
        this.st_212Ordinal = typeAPI.getSt_212Ordinal(ordinal);
        this.st_213Ordinal = typeAPI.getSt_213Ordinal(ordinal);
        this.st_214Ordinal = typeAPI.getSt_214Ordinal(ordinal);
        this.st_215Ordinal = typeAPI.getSt_215Ordinal(ordinal);
        this.st_216Ordinal = typeAPI.getSt_216Ordinal(ordinal);
        this.st_217Ordinal = typeAPI.getSt_217Ordinal(ordinal);
        this.st_218Ordinal = typeAPI.getSt_218Ordinal(ordinal);
        this.st_219Ordinal = typeAPI.getSt_219Ordinal(ordinal);
        this.st_220Ordinal = typeAPI.getSt_220Ordinal(ordinal);
        this.st_221Ordinal = typeAPI.getSt_221Ordinal(ordinal);
        this.st_222Ordinal = typeAPI.getSt_222Ordinal(ordinal);
        this.st_223Ordinal = typeAPI.getSt_223Ordinal(ordinal);
        this.st_224Ordinal = typeAPI.getSt_224Ordinal(ordinal);
        this.st_225Ordinal = typeAPI.getSt_225Ordinal(ordinal);
        this.st_226Ordinal = typeAPI.getSt_226Ordinal(ordinal);
        this.st_227Ordinal = typeAPI.getSt_227Ordinal(ordinal);
        this.st_228Ordinal = typeAPI.getSt_228Ordinal(ordinal);
        this.st_229Ordinal = typeAPI.getSt_229Ordinal(ordinal);
        this.st_230Ordinal = typeAPI.getSt_230Ordinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getId(int ordinal) {
        if(id == null)
            return Long.MIN_VALUE;
        return id.longValue();
    }

    public Long getIdBoxed(int ordinal) {
        return id;
    }

    public int getPrefixOrdinal(int ordinal) {
        return prefixOrdinal;
    }

    public int getDesOrdinal(int ordinal) {
        return desOrdinal;
    }

    public int getEvi_nOrdinal(int ordinal) {
        return evi_nOrdinal;
    }

    public int getChar_nOrdinal(int ordinal) {
        return char_nOrdinal;
    }

    public int getNav_snOrdinal(int ordinal) {
        return nav_snOrdinal;
    }

    public int getDnOrdinal(int ordinal) {
        return dnOrdinal;
    }

    public int getKc_cnOrdinal(int ordinal) {
        return kc_cnOrdinal;
    }

    public int getSt_2Ordinal(int ordinal) {
        return st_2Ordinal;
    }

    public int getBmt_nOrdinal(int ordinal) {
        return bmt_nOrdinal;
    }

    public int getSt_1Ordinal(int ordinal) {
        return st_1Ordinal;
    }

    public int getSt_4Ordinal(int ordinal) {
        return st_4Ordinal;
    }

    public int getSt_3Ordinal(int ordinal) {
        return st_3Ordinal;
    }

    public int getSt_0Ordinal(int ordinal) {
        return st_0Ordinal;
    }

    public int getSt_9Ordinal(int ordinal) {
        return st_9Ordinal;
    }

    public int getSnOrdinal(int ordinal) {
        return snOrdinal;
    }

    public int getKag_knOrdinal(int ordinal) {
        return kag_knOrdinal;
    }

    public int getRoar_nOrdinal(int ordinal) {
        return roar_nOrdinal;
    }

    public int getSt_6Ordinal(int ordinal) {
        return st_6Ordinal;
    }

    public int getSt_5Ordinal(int ordinal) {
        return st_5Ordinal;
    }

    public int getSt_8Ordinal(int ordinal) {
        return st_8Ordinal;
    }

    public int getTdnOrdinal(int ordinal) {
        return tdnOrdinal;
    }

    public int getSt_7Ordinal(int ordinal) {
        return st_7Ordinal;
    }

    public int getSt_10Ordinal(int ordinal) {
        return st_10Ordinal;
    }

    public int getSt_11Ordinal(int ordinal) {
        return st_11Ordinal;
    }

    public int getSt_12Ordinal(int ordinal) {
        return st_12Ordinal;
    }

    public int getSt_13Ordinal(int ordinal) {
        return st_13Ordinal;
    }

    public int getSt_14Ordinal(int ordinal) {
        return st_14Ordinal;
    }

    public int getSt_15Ordinal(int ordinal) {
        return st_15Ordinal;
    }

    public int getSt_16Ordinal(int ordinal) {
        return st_16Ordinal;
    }

    public int getSt_17Ordinal(int ordinal) {
        return st_17Ordinal;
    }

    public int getSt_18Ordinal(int ordinal) {
        return st_18Ordinal;
    }

    public int getSt_19Ordinal(int ordinal) {
        return st_19Ordinal;
    }

    public int getSt_20Ordinal(int ordinal) {
        return st_20Ordinal;
    }

    public int getSt_21Ordinal(int ordinal) {
        return st_21Ordinal;
    }

    public int getSt_22Ordinal(int ordinal) {
        return st_22Ordinal;
    }

    public int getSt_23Ordinal(int ordinal) {
        return st_23Ordinal;
    }

    public int getSt_24Ordinal(int ordinal) {
        return st_24Ordinal;
    }

    public int getSt_25Ordinal(int ordinal) {
        return st_25Ordinal;
    }

    public int getSt_26Ordinal(int ordinal) {
        return st_26Ordinal;
    }

    public int getSt_27Ordinal(int ordinal) {
        return st_27Ordinal;
    }

    public int getSt_28Ordinal(int ordinal) {
        return st_28Ordinal;
    }

    public int getSt_29Ordinal(int ordinal) {
        return st_29Ordinal;
    }

    public int getSt_30Ordinal(int ordinal) {
        return st_30Ordinal;
    }

    public int getSt_31Ordinal(int ordinal) {
        return st_31Ordinal;
    }

    public int getSt_32Ordinal(int ordinal) {
        return st_32Ordinal;
    }

    public int getSt_33Ordinal(int ordinal) {
        return st_33Ordinal;
    }

    public int getSt_34Ordinal(int ordinal) {
        return st_34Ordinal;
    }

    public int getSt_35Ordinal(int ordinal) {
        return st_35Ordinal;
    }

    public int getSt_36Ordinal(int ordinal) {
        return st_36Ordinal;
    }

    public int getSt_37Ordinal(int ordinal) {
        return st_37Ordinal;
    }

    public int getSt_38Ordinal(int ordinal) {
        return st_38Ordinal;
    }

    public int getSt_39Ordinal(int ordinal) {
        return st_39Ordinal;
    }

    public int getSt_40Ordinal(int ordinal) {
        return st_40Ordinal;
    }

    public int getSt_41Ordinal(int ordinal) {
        return st_41Ordinal;
    }

    public int getSt_42Ordinal(int ordinal) {
        return st_42Ordinal;
    }

    public int getSt_43Ordinal(int ordinal) {
        return st_43Ordinal;
    }

    public int getSt_44Ordinal(int ordinal) {
        return st_44Ordinal;
    }

    public int getSt_45Ordinal(int ordinal) {
        return st_45Ordinal;
    }

    public int getSt_46Ordinal(int ordinal) {
        return st_46Ordinal;
    }

    public int getSt_47Ordinal(int ordinal) {
        return st_47Ordinal;
    }

    public int getSt_48Ordinal(int ordinal) {
        return st_48Ordinal;
    }

    public int getSt_49Ordinal(int ordinal) {
        return st_49Ordinal;
    }

    public int getSt_50Ordinal(int ordinal) {
        return st_50Ordinal;
    }

    public int getSt_51Ordinal(int ordinal) {
        return st_51Ordinal;
    }

    public int getSt_52Ordinal(int ordinal) {
        return st_52Ordinal;
    }

    public int getSt_53Ordinal(int ordinal) {
        return st_53Ordinal;
    }

    public int getSt_54Ordinal(int ordinal) {
        return st_54Ordinal;
    }

    public int getSt_55Ordinal(int ordinal) {
        return st_55Ordinal;
    }

    public int getSt_56Ordinal(int ordinal) {
        return st_56Ordinal;
    }

    public int getSt_57Ordinal(int ordinal) {
        return st_57Ordinal;
    }

    public int getSt_58Ordinal(int ordinal) {
        return st_58Ordinal;
    }

    public int getSt_59Ordinal(int ordinal) {
        return st_59Ordinal;
    }

    public int getSt_60Ordinal(int ordinal) {
        return st_60Ordinal;
    }

    public int getSt_61Ordinal(int ordinal) {
        return st_61Ordinal;
    }

    public int getSt_62Ordinal(int ordinal) {
        return st_62Ordinal;
    }

    public int getSt_63Ordinal(int ordinal) {
        return st_63Ordinal;
    }

    public int getSt_64Ordinal(int ordinal) {
        return st_64Ordinal;
    }

    public int getSt_65Ordinal(int ordinal) {
        return st_65Ordinal;
    }

    public int getSt_66Ordinal(int ordinal) {
        return st_66Ordinal;
    }

    public int getSt_67Ordinal(int ordinal) {
        return st_67Ordinal;
    }

    public int getSt_68Ordinal(int ordinal) {
        return st_68Ordinal;
    }

    public int getSt_69Ordinal(int ordinal) {
        return st_69Ordinal;
    }

    public int getSt_70Ordinal(int ordinal) {
        return st_70Ordinal;
    }

    public int getSt_71Ordinal(int ordinal) {
        return st_71Ordinal;
    }

    public int getSt_72Ordinal(int ordinal) {
        return st_72Ordinal;
    }

    public int getSt_73Ordinal(int ordinal) {
        return st_73Ordinal;
    }

    public int getSt_74Ordinal(int ordinal) {
        return st_74Ordinal;
    }

    public int getSt_75Ordinal(int ordinal) {
        return st_75Ordinal;
    }

    public int getSt_76Ordinal(int ordinal) {
        return st_76Ordinal;
    }

    public int getSt_77Ordinal(int ordinal) {
        return st_77Ordinal;
    }

    public int getSt_78Ordinal(int ordinal) {
        return st_78Ordinal;
    }

    public int getSt_79Ordinal(int ordinal) {
        return st_79Ordinal;
    }

    public int getSt_80Ordinal(int ordinal) {
        return st_80Ordinal;
    }

    public int getSt_81Ordinal(int ordinal) {
        return st_81Ordinal;
    }

    public int getSt_82Ordinal(int ordinal) {
        return st_82Ordinal;
    }

    public int getSt_83Ordinal(int ordinal) {
        return st_83Ordinal;
    }

    public int getSt_84Ordinal(int ordinal) {
        return st_84Ordinal;
    }

    public int getSt_85Ordinal(int ordinal) {
        return st_85Ordinal;
    }

    public int getSt_86Ordinal(int ordinal) {
        return st_86Ordinal;
    }

    public int getSt_87Ordinal(int ordinal) {
        return st_87Ordinal;
    }

    public int getSt_88Ordinal(int ordinal) {
        return st_88Ordinal;
    }

    public int getSt_89Ordinal(int ordinal) {
        return st_89Ordinal;
    }

    public int getSt_90Ordinal(int ordinal) {
        return st_90Ordinal;
    }

    public int getSt_91Ordinal(int ordinal) {
        return st_91Ordinal;
    }

    public int getSt_92Ordinal(int ordinal) {
        return st_92Ordinal;
    }

    public int getSt_93Ordinal(int ordinal) {
        return st_93Ordinal;
    }

    public int getSt_94Ordinal(int ordinal) {
        return st_94Ordinal;
    }

    public int getSt_95Ordinal(int ordinal) {
        return st_95Ordinal;
    }

    public int getSt_96Ordinal(int ordinal) {
        return st_96Ordinal;
    }

    public int getSt_97Ordinal(int ordinal) {
        return st_97Ordinal;
    }

    public int getSt_98Ordinal(int ordinal) {
        return st_98Ordinal;
    }

    public int getSt_99Ordinal(int ordinal) {
        return st_99Ordinal;
    }

    public int getSt_100Ordinal(int ordinal) {
        return st_100Ordinal;
    }

    public int getSt_101Ordinal(int ordinal) {
        return st_101Ordinal;
    }

    public int getSt_102Ordinal(int ordinal) {
        return st_102Ordinal;
    }

    public int getSt_103Ordinal(int ordinal) {
        return st_103Ordinal;
    }

    public int getSt_104Ordinal(int ordinal) {
        return st_104Ordinal;
    }

    public int getSt_105Ordinal(int ordinal) {
        return st_105Ordinal;
    }

    public int getSt_106Ordinal(int ordinal) {
        return st_106Ordinal;
    }

    public int getSt_107Ordinal(int ordinal) {
        return st_107Ordinal;
    }

    public int getSt_108Ordinal(int ordinal) {
        return st_108Ordinal;
    }

    public int getSt_109Ordinal(int ordinal) {
        return st_109Ordinal;
    }

    public int getSt_110Ordinal(int ordinal) {
        return st_110Ordinal;
    }

    public int getSt_111Ordinal(int ordinal) {
        return st_111Ordinal;
    }

    public int getSt_112Ordinal(int ordinal) {
        return st_112Ordinal;
    }

    public int getSt_113Ordinal(int ordinal) {
        return st_113Ordinal;
    }

    public int getSt_114Ordinal(int ordinal) {
        return st_114Ordinal;
    }

    public int getSt_115Ordinal(int ordinal) {
        return st_115Ordinal;
    }

    public int getSt_116Ordinal(int ordinal) {
        return st_116Ordinal;
    }

    public int getSt_117Ordinal(int ordinal) {
        return st_117Ordinal;
    }

    public int getSt_118Ordinal(int ordinal) {
        return st_118Ordinal;
    }

    public int getSt_119Ordinal(int ordinal) {
        return st_119Ordinal;
    }

    public int getSt_120Ordinal(int ordinal) {
        return st_120Ordinal;
    }

    public int getSt_121Ordinal(int ordinal) {
        return st_121Ordinal;
    }

    public int getSt_122Ordinal(int ordinal) {
        return st_122Ordinal;
    }

    public int getSt_123Ordinal(int ordinal) {
        return st_123Ordinal;
    }

    public int getSt_124Ordinal(int ordinal) {
        return st_124Ordinal;
    }

    public int getSt_125Ordinal(int ordinal) {
        return st_125Ordinal;
    }

    public int getSt_126Ordinal(int ordinal) {
        return st_126Ordinal;
    }

    public int getSt_127Ordinal(int ordinal) {
        return st_127Ordinal;
    }

    public int getSt_128Ordinal(int ordinal) {
        return st_128Ordinal;
    }

    public int getSt_129Ordinal(int ordinal) {
        return st_129Ordinal;
    }

    public int getSt_130Ordinal(int ordinal) {
        return st_130Ordinal;
    }

    public int getSt_131Ordinal(int ordinal) {
        return st_131Ordinal;
    }

    public int getSt_132Ordinal(int ordinal) {
        return st_132Ordinal;
    }

    public int getSt_133Ordinal(int ordinal) {
        return st_133Ordinal;
    }

    public int getSt_134Ordinal(int ordinal) {
        return st_134Ordinal;
    }

    public int getSt_135Ordinal(int ordinal) {
        return st_135Ordinal;
    }

    public int getSt_136Ordinal(int ordinal) {
        return st_136Ordinal;
    }

    public int getSt_137Ordinal(int ordinal) {
        return st_137Ordinal;
    }

    public int getSt_138Ordinal(int ordinal) {
        return st_138Ordinal;
    }

    public int getSt_139Ordinal(int ordinal) {
        return st_139Ordinal;
    }

    public int getSt_140Ordinal(int ordinal) {
        return st_140Ordinal;
    }

    public int getSt_141Ordinal(int ordinal) {
        return st_141Ordinal;
    }

    public int getSt_142Ordinal(int ordinal) {
        return st_142Ordinal;
    }

    public int getSt_143Ordinal(int ordinal) {
        return st_143Ordinal;
    }

    public int getSt_144Ordinal(int ordinal) {
        return st_144Ordinal;
    }

    public int getSt_145Ordinal(int ordinal) {
        return st_145Ordinal;
    }

    public int getSt_146Ordinal(int ordinal) {
        return st_146Ordinal;
    }

    public int getSt_147Ordinal(int ordinal) {
        return st_147Ordinal;
    }

    public int getSt_148Ordinal(int ordinal) {
        return st_148Ordinal;
    }

    public int getSt_149Ordinal(int ordinal) {
        return st_149Ordinal;
    }

    public int getSt_150Ordinal(int ordinal) {
        return st_150Ordinal;
    }

    public int getSt_151Ordinal(int ordinal) {
        return st_151Ordinal;
    }

    public int getSt_152Ordinal(int ordinal) {
        return st_152Ordinal;
    }

    public int getSt_153Ordinal(int ordinal) {
        return st_153Ordinal;
    }

    public int getSt_154Ordinal(int ordinal) {
        return st_154Ordinal;
    }

    public int getSt_155Ordinal(int ordinal) {
        return st_155Ordinal;
    }

    public int getSt_156Ordinal(int ordinal) {
        return st_156Ordinal;
    }

    public int getSt_157Ordinal(int ordinal) {
        return st_157Ordinal;
    }

    public int getSt_158Ordinal(int ordinal) {
        return st_158Ordinal;
    }

    public int getSt_159Ordinal(int ordinal) {
        return st_159Ordinal;
    }

    public int getSt_160Ordinal(int ordinal) {
        return st_160Ordinal;
    }

    public int getSt_161Ordinal(int ordinal) {
        return st_161Ordinal;
    }

    public int getSt_162Ordinal(int ordinal) {
        return st_162Ordinal;
    }

    public int getSt_163Ordinal(int ordinal) {
        return st_163Ordinal;
    }

    public int getSt_164Ordinal(int ordinal) {
        return st_164Ordinal;
    }

    public int getSt_165Ordinal(int ordinal) {
        return st_165Ordinal;
    }

    public int getSt_166Ordinal(int ordinal) {
        return st_166Ordinal;
    }

    public int getSt_167Ordinal(int ordinal) {
        return st_167Ordinal;
    }

    public int getSt_168Ordinal(int ordinal) {
        return st_168Ordinal;
    }

    public int getSt_169Ordinal(int ordinal) {
        return st_169Ordinal;
    }

    public int getSt_170Ordinal(int ordinal) {
        return st_170Ordinal;
    }

    public int getSt_171Ordinal(int ordinal) {
        return st_171Ordinal;
    }

    public int getSt_172Ordinal(int ordinal) {
        return st_172Ordinal;
    }

    public int getSt_173Ordinal(int ordinal) {
        return st_173Ordinal;
    }

    public int getSt_174Ordinal(int ordinal) {
        return st_174Ordinal;
    }

    public int getSt_175Ordinal(int ordinal) {
        return st_175Ordinal;
    }

    public int getSt_176Ordinal(int ordinal) {
        return st_176Ordinal;
    }

    public int getSt_177Ordinal(int ordinal) {
        return st_177Ordinal;
    }

    public int getSt_178Ordinal(int ordinal) {
        return st_178Ordinal;
    }

    public int getSt_179Ordinal(int ordinal) {
        return st_179Ordinal;
    }

    public int getSt_180Ordinal(int ordinal) {
        return st_180Ordinal;
    }

    public int getSt_181Ordinal(int ordinal) {
        return st_181Ordinal;
    }

    public int getSt_182Ordinal(int ordinal) {
        return st_182Ordinal;
    }

    public int getSt_183Ordinal(int ordinal) {
        return st_183Ordinal;
    }

    public int getSt_184Ordinal(int ordinal) {
        return st_184Ordinal;
    }

    public int getSt_185Ordinal(int ordinal) {
        return st_185Ordinal;
    }

    public int getSt_186Ordinal(int ordinal) {
        return st_186Ordinal;
    }

    public int getSt_187Ordinal(int ordinal) {
        return st_187Ordinal;
    }

    public int getSt_188Ordinal(int ordinal) {
        return st_188Ordinal;
    }

    public int getSt_189Ordinal(int ordinal) {
        return st_189Ordinal;
    }

    public int getSt_190Ordinal(int ordinal) {
        return st_190Ordinal;
    }

    public int getSt_191Ordinal(int ordinal) {
        return st_191Ordinal;
    }

    public int getSt_192Ordinal(int ordinal) {
        return st_192Ordinal;
    }

    public int getSt_193Ordinal(int ordinal) {
        return st_193Ordinal;
    }

    public int getSt_194Ordinal(int ordinal) {
        return st_194Ordinal;
    }

    public int getSt_195Ordinal(int ordinal) {
        return st_195Ordinal;
    }

    public int getSt_196Ordinal(int ordinal) {
        return st_196Ordinal;
    }

    public int getSt_197Ordinal(int ordinal) {
        return st_197Ordinal;
    }

    public int getSt_198Ordinal(int ordinal) {
        return st_198Ordinal;
    }

    public int getSt_199Ordinal(int ordinal) {
        return st_199Ordinal;
    }

    public int getSt_200Ordinal(int ordinal) {
        return st_200Ordinal;
    }

    public int getSt_201Ordinal(int ordinal) {
        return st_201Ordinal;
    }

    public int getSt_202Ordinal(int ordinal) {
        return st_202Ordinal;
    }

    public int getSt_203Ordinal(int ordinal) {
        return st_203Ordinal;
    }

    public int getSt_204Ordinal(int ordinal) {
        return st_204Ordinal;
    }

    public int getSt_205Ordinal(int ordinal) {
        return st_205Ordinal;
    }

    public int getSt_206Ordinal(int ordinal) {
        return st_206Ordinal;
    }

    public int getSt_207Ordinal(int ordinal) {
        return st_207Ordinal;
    }

    public int getSt_208Ordinal(int ordinal) {
        return st_208Ordinal;
    }

    public int getSt_209Ordinal(int ordinal) {
        return st_209Ordinal;
    }

    public int getSt_210Ordinal(int ordinal) {
        return st_210Ordinal;
    }

    public int getSt_211Ordinal(int ordinal) {
        return st_211Ordinal;
    }

    public int getSt_212Ordinal(int ordinal) {
        return st_212Ordinal;
    }

    public int getSt_213Ordinal(int ordinal) {
        return st_213Ordinal;
    }

    public int getSt_214Ordinal(int ordinal) {
        return st_214Ordinal;
    }

    public int getSt_215Ordinal(int ordinal) {
        return st_215Ordinal;
    }

    public int getSt_216Ordinal(int ordinal) {
        return st_216Ordinal;
    }

    public int getSt_217Ordinal(int ordinal) {
        return st_217Ordinal;
    }

    public int getSt_218Ordinal(int ordinal) {
        return st_218Ordinal;
    }

    public int getSt_219Ordinal(int ordinal) {
        return st_219Ordinal;
    }

    public int getSt_220Ordinal(int ordinal) {
        return st_220Ordinal;
    }

    public int getSt_221Ordinal(int ordinal) {
        return st_221Ordinal;
    }

    public int getSt_222Ordinal(int ordinal) {
        return st_222Ordinal;
    }

    public int getSt_223Ordinal(int ordinal) {
        return st_223Ordinal;
    }

    public int getSt_224Ordinal(int ordinal) {
        return st_224Ordinal;
    }

    public int getSt_225Ordinal(int ordinal) {
        return st_225Ordinal;
    }

    public int getSt_226Ordinal(int ordinal) {
        return st_226Ordinal;
    }

    public int getSt_227Ordinal(int ordinal) {
        return st_227Ordinal;
    }

    public int getSt_228Ordinal(int ordinal) {
        return st_228Ordinal;
    }

    public int getSt_229Ordinal(int ordinal) {
        return st_229Ordinal;
    }

    public int getSt_230Ordinal(int ordinal) {
        return st_230Ordinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public TurboCollectionsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (TurboCollectionsTypeAPI) typeAPI;
    }

}
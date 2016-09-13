package com.netflix.vms.transformer.contract;

public enum ContractAssetType {
    
    AUDIO(1),
    SUBTITLES(2),
    DESCRIPTIVE_AUDIO(4);
    
    private final long bitIdentifier;
    
    private ContractAssetType(int bitIdentifier) {
        this.bitIdentifier = bitIdentifier;
    }
    
    public long getBitIdentifier() {
        return bitIdentifier;
    }
    
    public static ContractAssetType fromAssetTypeString(String assetTypeName) {
        switch(assetTypeName) {
        case "Primary Video + Audio Muxed":
        case "Secondary Audio Source":
            return AUDIO;
        case "Closed Captioning":
        case "Subtitles":
            return SUBTITLES;
        case "Descriptive Audio":
            return DESCRIPTIVE_AUDIO;
        }
        
        return null;
    }

}

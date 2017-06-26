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
    
    /*
     * Per Ryan Schroeder on 6/23/2017 when asked "can you help me understand for example why we have both 'Primary Video + Audio Muxed' and 'Secondary Audio Source' rights, but treat them the same way?  Are we doing the right thing by mapping the asset type rights to these three buckets?"
     * 
     * I think this was done mostly for historical purposes, partly due to dirty data.
     * In my mind, there's no such thing as having or not having video rights.  If we don't have video rights, it would be a podcast :)
     * Since there's an audio component to muxed files then, it's really about the audio from Primary Video Audio Muxed.  When content is setup, it's usually given wildcard (*) language rights for Primary AV, which represents the 'original language' of the content.  If we get any other audio rights, they are usually added as Secondary Audio Source rights, with an explicit language set.  I'm certain there's content where all of the Secondary Audio Rights were added as Primary AV, so we allow them to apply as well.
     * Similarly with Subtitles vs Closed Captions.  This is more of a technical distinction rather than a rights distinction.  Usually contracts give us 'timed-text' rights, without specifying whether it's really subtitles or closed captioning (that includes sounds, etc).  Same thing, there's probably little consistency in how those rights were set up.
     * Descriptive audio is a new, distinct type of audio that we've selectively licensed, which is why it's distinct.
     * One of these days, I would love to see us clean up our data to match what we actually have.
     */
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

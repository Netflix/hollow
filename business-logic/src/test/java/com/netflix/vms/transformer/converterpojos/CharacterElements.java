package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="CharacterElements")
public class CharacterElements implements Cloneable {

    public String characterName = null;
    public String bladeBottomLine = null;
    public String characterBio = null;
    public String bladeTopLine = null;

    public CharacterElements setCharacterName(String characterName) {
        this.characterName = characterName;
        return this;
    }
    public CharacterElements setBladeBottomLine(String bladeBottomLine) {
        this.bladeBottomLine = bladeBottomLine;
        return this;
    }
    public CharacterElements setCharacterBio(String characterBio) {
        this.characterBio = characterBio;
        return this;
    }
    public CharacterElements setBladeTopLine(String bladeTopLine) {
        this.bladeTopLine = bladeTopLine;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof CharacterElements))
            return false;

        CharacterElements o = (CharacterElements) other;
        if(o.characterName == null) {
            if(characterName != null) return false;
        } else if(!o.characterName.equals(characterName)) return false;
        if(o.bladeBottomLine == null) {
            if(bladeBottomLine != null) return false;
        } else if(!o.bladeBottomLine.equals(bladeBottomLine)) return false;
        if(o.characterBio == null) {
            if(characterBio != null) return false;
        } else if(!o.characterBio.equals(characterBio)) return false;
        if(o.bladeTopLine == null) {
            if(bladeTopLine != null) return false;
        } else if(!o.bladeTopLine.equals(bladeTopLine)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (characterName == null ? 1237 : characterName.hashCode());
        hashCode = hashCode * 31 + (bladeBottomLine == null ? 1237 : bladeBottomLine.hashCode());
        hashCode = hashCode * 31 + (characterBio == null ? 1237 : characterBio.hashCode());
        hashCode = hashCode * 31 + (bladeTopLine == null ? 1237 : bladeTopLine.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("CharacterElements{");
        builder.append("characterName=").append(characterName);
        builder.append(",bladeBottomLine=").append(bladeBottomLine);
        builder.append(",characterBio=").append(characterBio);
        builder.append(",bladeTopLine=").append(bladeTopLine);
        builder.append("}");
        return builder.toString();
    }

    public CharacterElements clone() {
        try {
            CharacterElements clone = (CharacterElements)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}
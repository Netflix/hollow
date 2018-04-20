package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="TranslatedTextValue")
public class TranslatedTextValue implements Cloneable {

    public String value = null;

    public TranslatedTextValue() { }

    public TranslatedTextValue(String value) {
        this.value = value;
    }

    public TranslatedTextValue setValue(String value) {
        this.value = value;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof TranslatedTextValue))
            return false;

        TranslatedTextValue o = (TranslatedTextValue) other;
        if(o.value == null) {
            if(value != null) return false;
        } else if(!o.value.equals(value)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (value == null ? 1237 : value.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("TranslatedTextValue{");
        builder.append("value=").append(value);
        builder.append("}");
        return builder.toString();
    }

    public TranslatedTextValue clone() {
        try {
            TranslatedTextValue clone = (TranslatedTextValue)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}
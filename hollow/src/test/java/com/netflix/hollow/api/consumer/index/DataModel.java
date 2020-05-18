package com.netflix.hollow.api.consumer.index;

import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.HollowMap;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.delegate.HollowMapDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectGenericDelegate;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.write.objectmapper.HollowInline;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataModel {
    static class Producer {
        public static class Values {
            final boolean _boolean;
            final byte _byte;
            final short _short;
            final char _char;
            final int _int;
            final long _long;
            final float _float;
            final double _double;
            final byte[] _bytes;
            final char[] _chars;

            public Values() {
                this._boolean = true;
                this._byte = 1;
                this._short = 1;
                this._char = 1;
                this._int = 1;
                this._long = 1L;
                this._float = 1.0f;
                this._double = 1.0d;
                this._bytes = new byte[] {1};
                this._chars = "1".toCharArray();
            }
        }

        public static class Boxes {
            final Boolean _boolean;
            final Byte _byte;
            final Short _short;
            final Character _char;
            final Integer _int;
            final Long _long;
            final Float _float;
            final Double _double;
            final String _string;

            public Boxes() {
                this._boolean = true;
                this._byte = 1;
                this._short = 1;
                this._char = 1;
                this._int = 1;
                this._long = 1L;
                this._float = 1.0f;
                this._double = 1.0d;
                this._string = "1";
            }
        }

        public static class InlineBoxes {
            @HollowInline final Boolean _boolean;
            @HollowInline final Byte _byte;
            @HollowInline final Short _short;
            @HollowInline final Character _char;
            @HollowInline final Integer _int;
            @HollowInline final Long _long;
            @HollowInline final Float _float;
            @HollowInline final Double _double;
            @HollowInline final String _string;

            public InlineBoxes() {
                this._boolean = true;
                this._byte = 1;
                this._short = 1;
                this._char = 1;
                this._int = 1;
                this._long = 1L;
                this._float = 1.0f;
                this._double = 1.0d;
                this._string = "1";
            }
        }

        public static class MappedReferencesToValues {
            enum Number {
                ONE,
                TWO
            }

            final Date date;
            final Number number;

            public MappedReferencesToValues() {
                this.date = new Date(0);
                this.number = Number.ONE;
            }
        }

        @HollowTypeName(name = "ReferenceWithStringsRenamed")
        public static class ReferenceWithStrings {
            final String _string1;

            @HollowTypeName(name = "FieldOfStringRenamed") final String _string2;

            public ReferenceWithStrings() {
                this._string1 = "1";
                this._string2 = "1";
            }
        }

        public static class TypeA {
            final int i;
            final String s;

            public TypeA(int i, String s) {
                this.i = i;
                this.s = s;
            }
        }

        public static class TypeB {
            final int i;
            final String s;

            public TypeB(int i, String s) {
                this.i = i;
                this.s = s;
            }
        }

        @HollowPrimaryKey(fields = {"i", "sub1.s", "sub2.i"})
        public static class TypeWithPrimaryKey {
            final int i;
            final SubTypeOfTypeWithPrimaryKey sub1;
            final SubTypeOfTypeWithPrimaryKey sub2;

            public TypeWithPrimaryKey(
                    int i, SubTypeOfTypeWithPrimaryKey sub1,
                    SubTypeOfTypeWithPrimaryKey sub2) {
                this.i = i;
                this.sub1 = sub1;
                this.sub2 = sub2;
            }
        }

        public static class TypeWithTypeB {
            final String foo;
            final TypeB typeB;

            public TypeWithTypeB(String foo, TypeB typeB) {
                this.foo = foo;
                this.typeB = typeB;
            }
        }

        public static class SubTypeOfTypeWithPrimaryKey {
            final String s;
            final int i;

            public SubTypeOfTypeWithPrimaryKey(String s, int i) {
                this.s = s;
                this.i = i;
            }
        }

        @HollowPrimaryKey(fields = {"i"})
        public static class TypeWithPrimaryKey2 {
            final int i;

            public TypeWithPrimaryKey2(int i) {
                this.i = i;
            }
        }

        public static class Sequences {
            final List<Boxes> list;

            final Set<Boxes> set;

            final Map<Boxes, Boxes> map;

            public Sequences() {
                this.list = new ArrayList<>(Arrays.asList(new Boxes()));
                this.set = new HashSet<>(list);
                this.map = new HashMap<>();
                map.put(new Boxes(), new Boxes());
            }
        }

        public static class References {
            final Values values;
            final Boxes boxes;
            final InlineBoxes inlineBoxes;
            final MappedReferencesToValues mapped;
            final Sequences sequences;
            final ReferenceWithStrings referenceWithStrings;
            final TypeWithPrimaryKey typeWithPrimaryKey;

            public References() {
                this.values = new Values();
                this.boxes = new Boxes();
                this.inlineBoxes = new InlineBoxes();
                this.mapped = new MappedReferencesToValues();
                this.sequences = new Sequences();
                this.referenceWithStrings = new ReferenceWithStrings();

                this.typeWithPrimaryKey = new TypeWithPrimaryKey(1,
                        new SubTypeOfTypeWithPrimaryKey("1", 1),
                        new SubTypeOfTypeWithPrimaryKey("2", 2));
            }
        }
    }

    public static class Consumer {
        public static class Api extends HollowAPI {
            private final HollowObjectTypeDataAccess stringTypeDataAccess;
            private final HollowObjectGenericDelegate stringDelegate;

            private final HollowObjectTypeDataAccess fieldOfStringRenamedTypeDataAccess;
            private final HollowObjectGenericDelegate fieldOfStringRenamedDelegate;

            public Api(HollowDataAccess dataAccess) {
                this(dataAccess, Collections.<String>emptySet());
            }

            public Api(HollowDataAccess dataAccess, Set<String> cachedTypes) {
                this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
            }

            public Api(
                    HollowDataAccess dataAccess, Set<String> cachedTypes,
                    Map<String, HollowFactory<?>> factoryOverrides) {
                this(dataAccess, cachedTypes, factoryOverrides, null);
            }

            public Api(
                    HollowDataAccess dataAccess, Set<String> cachedTypes,
                    Map<String, HollowFactory<?>> factoryOverrides, Api previousCycleAPI) {
                super(dataAccess);

                this.stringTypeDataAccess = (HollowObjectTypeDataAccess) dataAccess.getTypeDataAccess("String");
                this.stringDelegate = new HollowObjectGenericDelegate(stringTypeDataAccess);

                this.fieldOfStringRenamedTypeDataAccess = (HollowObjectTypeDataAccess) dataAccess.getTypeDataAccess(
                        "FieldOfStringRenamed");
                this.fieldOfStringRenamedDelegate = new HollowObjectGenericDelegate(fieldOfStringRenamedTypeDataAccess);
            }

            public HString getHString(int ordinal) {
                return new HString(stringDelegate, ordinal);
            }

            public Values getValues(int ordinal) {
                return new Values(null, ordinal);
            }

            public Boxes getBoxes(int ordinal) {
                return new Boxes(null, ordinal);
            }

            public InlineBoxes getInlineBoxes(int ordinal) {
                return new InlineBoxes(null, ordinal);
            }

            public MappedReferencesToValues getMappedReferencesToValues(int ordinal) {
                return new MappedReferencesToValues(null, ordinal);
            }

            public ReferenceWithStringsRenamed getReferenceWithStringsRenamed(int ordinal) {
                return new ReferenceWithStringsRenamed(null, ordinal);
            }

            public FieldOfStringRenamed getFieldOfStringRenamed(int ordinal) {
                return new FieldOfStringRenamed(fieldOfStringRenamedDelegate, ordinal);
            }

            public TypeA getTypeA(int ordinal) {
                return new TypeA(null, ordinal);
            }

            public TypeBSuffixed getTypeBSuffixed(int ordinal) {
                return new TypeBSuffixed(null, ordinal);
            }

            public TypeWithPrimaryKey getTypeWithPrimaryKey(int ordinal) {
                return new TypeWithPrimaryKey(null, ordinal);
            }

            public SubTypeOfTypeWithPrimaryKey getSubTypeOfTypeWithPrimaryKey(int ordinal) {
                return new SubTypeOfTypeWithPrimaryKey(null, ordinal);
            }

            public TypeWithPrimaryKeySuffixed getTypeWithPrimaryKeySuffixed(int ordinal) {
                return new TypeWithPrimaryKeySuffixed(null, ordinal);
            }

            public ListOfBoxes getListOfBoxes(int ordinal) {
                return new ListOfBoxes(null, ordinal);
            }

            public SetOfBoxes getSetOfBoxes(int ordinal) {
                return new SetOfBoxes(null, ordinal);
            }

            public MapOfBoxesToBoxes getMapOfBoxesToBoxes(int ordinal) {
                return new MapOfBoxesToBoxes(null, ordinal);
            }

            public References getReferences(int ordinal) {
                return new References(null, ordinal);
            }
        }

        public static class HString extends HollowObject {
            public HString(HollowObjectDelegate delegate, int ordinal) {
                super(delegate, ordinal);
            }

            public String getValue() {
                return ((HollowObjectGenericDelegate) getDelegate()).getString(getOrdinal(), "value");
            }
        }

        public static class Values extends HollowObject {
            public Values(HollowObjectDelegate delegate, int ordinal) {
                super(delegate, ordinal);
            }
        }

        public static class Boxes extends HollowObject {
            public Boxes(HollowObjectDelegate delegate, int ordinal) {
                super(delegate, ordinal);
            }
        }

        public static class InlineBoxes extends HollowObject {
            public InlineBoxes(HollowObjectDelegate delegate, int ordinal) {
                super(delegate, ordinal);
            }
        }

        public static class MappedReferencesToValues extends HollowObject {
            public MappedReferencesToValues(HollowObjectDelegate delegate, int ordinal) {
                super(delegate, ordinal);
            }
        }

        public static class ReferenceWithStringsRenamed extends HollowObject {
            public ReferenceWithStringsRenamed(HollowObjectDelegate delegate, int ordinal) {
                super(delegate, ordinal);
            }
        }

        public static class FieldOfStringRenamed extends HollowObject {
            public FieldOfStringRenamed(HollowObjectDelegate delegate, int ordinal) {
                super(delegate, ordinal);
            }

            public String getValue() {
                return ((HollowObjectGenericDelegate) getDelegate()).getString(0, "value");
            }
        }

        public static class TypeA extends HollowObject {
            public TypeA(HollowObjectDelegate delegate, int ordinal) {
                super(delegate, ordinal);
            }
        }

        @HollowTypeName(name = "TypeB")
        public static class TypeBSuffixed extends HollowObject {
            public TypeBSuffixed(HollowObjectDelegate delegate, int ordinal) {
                super(delegate, ordinal);
            }
        }

        public static class TypeWithPrimaryKey extends HollowObject {
            public TypeWithPrimaryKey(HollowObjectDelegate delegate, int ordinal) {
                super(delegate, ordinal);
            }
        }

        public static class TypeWithTypeB extends HollowObject {
            public TypeWithTypeB(HollowObjectDelegate delegate, int ordinal) {
                super(delegate, ordinal);
            }
        }

        public static class SubTypeOfTypeWithPrimaryKey extends HollowObject {
            public SubTypeOfTypeWithPrimaryKey(HollowObjectDelegate delegate, int ordinal) {
                super(delegate, ordinal);
            }
        }

        @HollowTypeName(name = "TypeWithPrimaryKey2")
        public static class TypeWithPrimaryKeySuffixed extends HollowObject {
            public TypeWithPrimaryKeySuffixed(HollowObjectDelegate delegate, int ordinal) {
                super(delegate, ordinal);
            }
        }

        public static class ListOfBoxes extends HollowList<Boxes> {
            public ListOfBoxes(HollowListDelegate<Boxes> delegate, int ordinal) {
                super(delegate, ordinal);
            }

            @Override
            public Boxes instantiateElement(int ordinal) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean equalsElement(int elementOrdinal, Object testObject) {
                throw new UnsupportedOperationException();
            }
        }

        public static class SetOfBoxes extends HollowSet<Boxes> {
            public SetOfBoxes(HollowSetDelegate<Boxes> delegate, int ordinal) {
                super(delegate, ordinal);
            }

            @Override
            public Boxes instantiateElement(int ordinal) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean equalsElement(int elementOrdinal, Object testObject) {
                throw new UnsupportedOperationException();
            }
        }

        public static class MapOfBoxesToBoxes extends HollowMap<Boxes, Boxes> {
            public MapOfBoxesToBoxes(HollowMapDelegate<Boxes, Boxes> delegate, int ordinal) {
                super(delegate, ordinal);
            }

            @Override
            public Boxes instantiateKey(int ordinal) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Boxes instantiateValue(int ordinal) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean equalsKey(int keyOrdinal, Object testObject) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean equalsValue(int valueOrdinal, Object testObject) {
                throw new UnsupportedOperationException();
            }
        }

        public static class References extends HollowObject {
            public References(HollowObjectDelegate delegate, int ordinal) {
                super(delegate, ordinal);
            }
        }
    }
}

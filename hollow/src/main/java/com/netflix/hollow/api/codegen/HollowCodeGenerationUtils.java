/*
 *
 *  Copyright 2016 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.api.codegen;

import java.util.Map;

import java.util.HashMap;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.api.objects.delegate.HollowListCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.delegate.HollowListLookupDelegate;
import com.netflix.hollow.api.objects.delegate.HollowMapCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowMapDelegate;
import com.netflix.hollow.api.objects.delegate.HollowMapLookupDelegate;
import com.netflix.hollow.api.objects.delegate.HollowSetCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.delegate.HollowSetLookupDelegate;

/**
 * A class containing convenience methods for the {@link HollowAPIGenerator}.  Not intended for external consumption.
 */
public class HollowCodeGenerationUtils {
    
    private static final Map<String,String> CLASS_NAME_SUBSTITUTIONS = new HashMap<String,String>();
    
    static {
        CLASS_NAME_SUBSTITUTIONS.put("AbstractMethodError", "HAbstractMethodError");
        CLASS_NAME_SUBSTITUTIONS.put("Appendable", "HAppendable");
        CLASS_NAME_SUBSTITUTIONS.put("ArithmeticException", "HArithmeticException");
        CLASS_NAME_SUBSTITUTIONS.put("ArrayIndexOutOfBoundsException", "HArrayIndexOutOfBoundsException");
        CLASS_NAME_SUBSTITUTIONS.put("ArrayStoreException", "HArrayStoreException");
        CLASS_NAME_SUBSTITUTIONS.put("AssertionError", "HAssertionError");
        CLASS_NAME_SUBSTITUTIONS.put("AutoCloseable", "HAutoCloseable");
        CLASS_NAME_SUBSTITUTIONS.put("Boolean", "HBoolean");
        CLASS_NAME_SUBSTITUTIONS.put("BootstrapMethodError", "HBootstrapMethodError");
        CLASS_NAME_SUBSTITUTIONS.put("Byte", "HByte");
        CLASS_NAME_SUBSTITUTIONS.put("CharSequence", "HCharSequence");
        CLASS_NAME_SUBSTITUTIONS.put("Character", "HCharacter");
        CLASS_NAME_SUBSTITUTIONS.put("Class", "HClass");
        CLASS_NAME_SUBSTITUTIONS.put("ClassCastException", "HClassCastException");
        CLASS_NAME_SUBSTITUTIONS.put("ClassCircularityError", "HClassCircularityError");
        CLASS_NAME_SUBSTITUTIONS.put("ClassFormatError", "HClassFormatError");
        CLASS_NAME_SUBSTITUTIONS.put("ClassLoader", "HClassLoader");
        CLASS_NAME_SUBSTITUTIONS.put("ClassNotFoundException", "HClassNotFoundException");
        CLASS_NAME_SUBSTITUTIONS.put("ClassValue", "HClassValue");
        CLASS_NAME_SUBSTITUTIONS.put("CloneNotSupportedException", "HCloneNotSupportedException");
        CLASS_NAME_SUBSTITUTIONS.put("Cloneable", "HCloneable");
        CLASS_NAME_SUBSTITUTIONS.put("Comparable", "HComparable");
        CLASS_NAME_SUBSTITUTIONS.put("Compiler", "HCompiler");
        CLASS_NAME_SUBSTITUTIONS.put("Deprecated", "HDeprecated");
        CLASS_NAME_SUBSTITUTIONS.put("Double", "HDouble");
        CLASS_NAME_SUBSTITUTIONS.put("Enum", "HEnum");
        CLASS_NAME_SUBSTITUTIONS.put("EnumConstantNotPresentException", "HEnumConstantNotPresentException");
        CLASS_NAME_SUBSTITUTIONS.put("Error", "HError");
        CLASS_NAME_SUBSTITUTIONS.put("Exception", "HException");
        CLASS_NAME_SUBSTITUTIONS.put("ExceptionInInitializerError", "HExceptionInInitializerError");
        CLASS_NAME_SUBSTITUTIONS.put("Float", "HFloat");
        CLASS_NAME_SUBSTITUTIONS.put("IllegalAccessError", "HIllegalAccessError");
        CLASS_NAME_SUBSTITUTIONS.put("IllegalAccessException", "HIllegalAccessException");
        CLASS_NAME_SUBSTITUTIONS.put("IllegalArgumentException", "HIllegalArgumentException");
        CLASS_NAME_SUBSTITUTIONS.put("IllegalMonitorStateException", "HIllegalMonitorStateException");
        CLASS_NAME_SUBSTITUTIONS.put("IllegalStateException", "HIllegalStateException");
        CLASS_NAME_SUBSTITUTIONS.put("IllegalThreadStateException", "HIllegalThreadStateException");
        CLASS_NAME_SUBSTITUTIONS.put("IncompatibleClassChangeError", "HIncompatibleClassChangeError");
        CLASS_NAME_SUBSTITUTIONS.put("IndexOutOfBoundsException", "HIndexOutOfBoundsException");
        CLASS_NAME_SUBSTITUTIONS.put("InheritableThreadLocal", "HInheritableThreadLocal");
        CLASS_NAME_SUBSTITUTIONS.put("InstantiationError", "HInstantiationError");
        CLASS_NAME_SUBSTITUTIONS.put("InstantiationException", "HInstantiationException");
        CLASS_NAME_SUBSTITUTIONS.put("Integer", "HInteger");
        CLASS_NAME_SUBSTITUTIONS.put("InternalError", "HInternalError");
        CLASS_NAME_SUBSTITUTIONS.put("InterruptedException", "HInterruptedException");
        CLASS_NAME_SUBSTITUTIONS.put("Iterable", "HIterable");
        CLASS_NAME_SUBSTITUTIONS.put("LinkageError", "HLinkageError");
        CLASS_NAME_SUBSTITUTIONS.put("Long", "HLong");
        CLASS_NAME_SUBSTITUTIONS.put("Math", "HMath");
        CLASS_NAME_SUBSTITUTIONS.put("NegativeArraySizeException", "HNegativeArraySizeException");
        CLASS_NAME_SUBSTITUTIONS.put("NoClassDefFoundError", "HNoClassDefFoundError");
        CLASS_NAME_SUBSTITUTIONS.put("NoSuchFieldError", "HNoSuchFieldError");
        CLASS_NAME_SUBSTITUTIONS.put("NoSuchFieldException", "HNoSuchFieldException");
        CLASS_NAME_SUBSTITUTIONS.put("NoSuchMethodError", "HNoSuchMethodError");
        CLASS_NAME_SUBSTITUTIONS.put("NoSuchMethodException", "HNoSuchMethodException");
        CLASS_NAME_SUBSTITUTIONS.put("NullPointerException", "HNullPointerException");
        CLASS_NAME_SUBSTITUTIONS.put("Number", "HNumber");
        CLASS_NAME_SUBSTITUTIONS.put("NumberFormatException", "HNumberFormatException");
        CLASS_NAME_SUBSTITUTIONS.put("Object", "HObject");
        CLASS_NAME_SUBSTITUTIONS.put("OutOfMemoryError", "HOutOfMemoryError");
        CLASS_NAME_SUBSTITUTIONS.put("Override", "HOverride");
        CLASS_NAME_SUBSTITUTIONS.put("Package", "HPackage");
        CLASS_NAME_SUBSTITUTIONS.put("Process", "HProcess");
        CLASS_NAME_SUBSTITUTIONS.put("ProcessBuilder", "HProcessBuilder");
        CLASS_NAME_SUBSTITUTIONS.put("Readable", "HReadable");
        CLASS_NAME_SUBSTITUTIONS.put("ReflectiveOperationException", "HReflectiveOperationException");
        CLASS_NAME_SUBSTITUTIONS.put("Runnable", "HRunnable");
        CLASS_NAME_SUBSTITUTIONS.put("Runtime", "HRuntime");
        CLASS_NAME_SUBSTITUTIONS.put("RuntimeException", "HRuntimeException");
        CLASS_NAME_SUBSTITUTIONS.put("RuntimePermission", "HRuntimePermission");
        CLASS_NAME_SUBSTITUTIONS.put("SafeVarargs", "HSafeVarargs");
        CLASS_NAME_SUBSTITUTIONS.put("SecurityException", "HSecurityException");
        CLASS_NAME_SUBSTITUTIONS.put("SecurityManager", "HSecurityManager");
        CLASS_NAME_SUBSTITUTIONS.put("Short", "HShort");
        CLASS_NAME_SUBSTITUTIONS.put("StackOverflowError", "HStackOverflowError");
        CLASS_NAME_SUBSTITUTIONS.put("StackTraceElement", "HStackTraceElement");
        CLASS_NAME_SUBSTITUTIONS.put("StrictMath", "HStrictMath");
        CLASS_NAME_SUBSTITUTIONS.put("String", "HString");
        CLASS_NAME_SUBSTITUTIONS.put("StringBuffer", "HStringBuffer");
        CLASS_NAME_SUBSTITUTIONS.put("StringBuilder", "HStringBuilder");
        CLASS_NAME_SUBSTITUTIONS.put("StringIndexOutOfBoundsException", "HStringIndexOutOfBoundsException");
        CLASS_NAME_SUBSTITUTIONS.put("SuppressWarnings", "HSuppressWarnings");
        CLASS_NAME_SUBSTITUTIONS.put("System", "HSystem");
        CLASS_NAME_SUBSTITUTIONS.put("Thread", "HThread");
        CLASS_NAME_SUBSTITUTIONS.put("ThreadDeath", "HThreadDeath");
        CLASS_NAME_SUBSTITUTIONS.put("ThreadGroup", "HThreadGroup");
        CLASS_NAME_SUBSTITUTIONS.put("ThreadLocal", "HThreadLocal");
        CLASS_NAME_SUBSTITUTIONS.put("Throwable", "HThrowable");
        CLASS_NAME_SUBSTITUTIONS.put("TypeNotPresentException", "HTypeNotPresentException");
        CLASS_NAME_SUBSTITUTIONS.put("UnknownError", "HUnknownError");
        CLASS_NAME_SUBSTITUTIONS.put("UnsatisfiedLinkError", "HUnsatisfiedLinkError");
        CLASS_NAME_SUBSTITUTIONS.put("UnsupportedClassVersionError", "HUnsupportedClassVersionError");
        CLASS_NAME_SUBSTITUTIONS.put("UnsupportedOperationException", "HUnsupportedOperationException");
        CLASS_NAME_SUBSTITUTIONS.put("VerifyError", "HVerifyError");
        CLASS_NAME_SUBSTITUTIONS.put("VirtualMachineError", "HVirtualMachineError");
        CLASS_NAME_SUBSTITUTIONS.put("Void", "HVoid");
    }

    public static String typeAPIClassname(String typeName) {
        return uppercase(typeName) + "TypeAPI";
    }

    public static String hollowFactoryClassname(String typeName) {
        return hollowImplClassname(typeName, "Hollow") + "Factory";
    }

    public static String hollowObjectProviderName(String typeName) {
        return substituteInvalidChars(lowercase(typeName)) + "Provider";
    }

    public static String hollowImplClassname(String typeName, String classPostfix) {
        String classname = substituteInvalidChars(uppercase(typeName)) + classPostfix;
        
        String sub = CLASS_NAME_SUBSTITUTIONS.get(classname);
        if(sub != null)
            return sub;
        
        return classname;
    }

    public static String delegateInterfaceName(String typeName) {
        return substituteInvalidChars(uppercase(typeName)) + "Delegate";
    }

    public static String delegateInterfaceName(HollowSchema schema) {
        if(schema instanceof HollowObjectSchema)
            return delegateInterfaceName(schema.getName());
        if(schema instanceof HollowListSchema)
            return HollowListDelegate.class.getSimpleName();
        if(schema instanceof HollowSetSchema)
            return HollowSetDelegate.class.getSimpleName();
        if(schema instanceof HollowMapSchema)
            return HollowMapDelegate.class.getSimpleName();
        throw new UnsupportedOperationException("What kind of schema is a " + schema.getClass().getSimpleName() + "?");
    }

    public static String delegateCachedImplName(String typeName) {
        return substituteInvalidChars(uppercase(typeName)) + "DelegateCachedImpl";
    }

    public static String delegateCachedClassname(HollowSchema schema) {
        if(schema instanceof HollowObjectSchema)
            return delegateCachedImplName(schema.getName());
        if(schema instanceof HollowListSchema)
            return HollowListCachedDelegate.class.getSimpleName();
        if(schema instanceof HollowSetSchema)
            return HollowSetCachedDelegate.class.getSimpleName();
        if(schema instanceof HollowMapSchema)
            return HollowMapCachedDelegate.class.getSimpleName();
        throw new UnsupportedOperationException("What kind of schema is a " + schema.getClass().getSimpleName() + "?");
    }

    public static String delegateLookupImplName(String typeName) {
        return substituteInvalidChars(uppercase(typeName)) + "DelegateLookupImpl";
    }

    public static String delegateLookupClassname(HollowSchema schema) {
        if(schema instanceof HollowObjectSchema)
            return delegateLookupImplName(schema.getName());
        if(schema instanceof HollowListSchema)
            return HollowListLookupDelegate.class.getSimpleName();
        if(schema instanceof HollowSetSchema)
            return HollowSetLookupDelegate.class.getSimpleName();
        if(schema instanceof HollowMapSchema)
            return HollowMapLookupDelegate.class.getSimpleName();
        throw new UnsupportedOperationException("What kind of schema is a " + schema.getClass().getSimpleName() + "?");
    }

    public static String lowercase(String str) {
        if(str == null || str.length() == 0)
            return str;

        StringBuilder builder = new StringBuilder();

        builder.append(str.substring(0, 1).toLowerCase());
        builder.append(str.substring(1));

        return builder.toString();
    }

    public static String uppercase(String str) {
        if(str == null || str.length() == 0)
            return str;

        StringBuilder builder = new StringBuilder();

        builder.append(str.substring(0, 1).toUpperCase());
        builder.append(str.substring(1));

        return builder.toString();
    }

    public static String substituteInvalidChars(String str) {
        str = str.replace(' ', '_');
        str = str.replace('.', '_');
        return str;
    }
}

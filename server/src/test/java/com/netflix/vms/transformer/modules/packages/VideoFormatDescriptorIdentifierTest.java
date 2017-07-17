package com.netflix.vms.transformer.modules.packages;

import com.google.inject.AbstractModule;
import com.netflix.cup.CupModule;
import com.netflix.governator.InjectorBuilder;
import com.netflix.governator.LifecycleInjector;
import com.netflix.runtime.lifecycle.RuntimeCoreModule;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.common.cup.CupLibrary;
import com.netflix.vms.transformer.cup.CupLibraryImpl;
import com.netflix.vms.transformer.hollowoutput.VideoFormatDescriptor;
import org.junit.Assert;
import org.junit.Test;

public class VideoFormatDescriptorIdentifierTest {

    public static void main(String[] args) {
        new VideoFormatDescriptorIdentifierTest().test_selectVideoFormatDescriptor();
    }

    //@Test
    public void test_selectVideoFormatDescriptor() {
        System.setProperty("netflix.environment", "test");
        LifecycleInjector injector = InjectorBuilder.fromModules(new RuntimeCoreModule(), new TestGuiceModule()).createInjector();

        CupLibrary cupLibrary = injector.getInstance(CupLibrary.class);
        CycleConstants cycleConstants = CycleConstants.getInstanceForTesting();

        {
            int height = 576;
            int width = 480;
            VideoFormatDescriptor result = VideoFormatDescriptorIdentifier.selectVideoFormatDescriptor(cupLibrary, cycleConstants, height, width);
            Assert.assertEquals(cycleConstants.SD, result);
        }

        {
            int height = 490;
            int width = 342;
            VideoFormatDescriptor result = VideoFormatDescriptorIdentifier.selectVideoFormatDescriptor(cupLibrary, cycleConstants, height, width);
            Assert.assertEquals(cycleConstants.SD, result);
        }

        {
            int height = 640;
            int width = 480;
            VideoFormatDescriptor result = VideoFormatDescriptorIdentifier.selectVideoFormatDescriptor(cupLibrary, cycleConstants, height, width);
            Assert.assertEquals(cycleConstants.HD, result);
        }

        {
            int height = 720;
            int width = 580;
            VideoFormatDescriptor result = VideoFormatDescriptorIdentifier.selectVideoFormatDescriptor(cupLibrary, cycleConstants, height, width);
            Assert.assertEquals(cycleConstants.HD, result);
        }

        {
            int height = 768;
            int width = 432;
            VideoFormatDescriptor result = VideoFormatDescriptorIdentifier.selectVideoFormatDescriptor(cupLibrary, cycleConstants, height, width);
            Assert.assertEquals(cycleConstants.SUPER_HD, result);
        }

        {
            int height = 1080;
            int width = 1920;
            VideoFormatDescriptor result = VideoFormatDescriptorIdentifier.selectVideoFormatDescriptor(cupLibrary, cycleConstants, height, width);
            Assert.assertEquals(cycleConstants.SUPER_HD, result);
        }

        {
            int height = 1081;
            int width = 1920;
            VideoFormatDescriptor result = VideoFormatDescriptorIdentifier.selectVideoFormatDescriptor(cupLibrary, cycleConstants, height, width);
            Assert.assertEquals(cycleConstants.ULTRA_HD, result);
        }

        {
            int height = 1440;
            int width = 2560;
            VideoFormatDescriptor result = VideoFormatDescriptorIdentifier.selectVideoFormatDescriptor(cupLibrary, cycleConstants, height, width);
            Assert.assertEquals(cycleConstants.ULTRA_HD, result);
        }

        {
            int height = 2160;
            int width = 3840;
            VideoFormatDescriptor result = VideoFormatDescriptorIdentifier.selectVideoFormatDescriptor(cupLibrary, cycleConstants, height, width);
            Assert.assertEquals(cycleConstants.ULTRA_HD, result);
        }
    }

    public static class TestGuiceModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new CupModule());
            bind(CupLibrary.class).to(CupLibraryImpl.class);
        }
    }
}

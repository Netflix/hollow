package com.netflix.hollow.core.read.engine;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemAnnouncer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemPublisher;
import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OneBilTest extends AbstractStateEngineTest {
    private HollowObjectSchema schema;
    @Override
    protected void initializeTypeStates() {
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema);
        writeStateEngine.addTypeState(writeState);
    }

    @Before
    public void setUp() {
        schema = new HollowObjectSchema("test", 1);
        schema.addField("field", HollowObjectSchema.FieldType.INT);

        super.setUp();
    }

    

    @HollowPrimaryKey(fields="id")
    public static class Movie {
        int id;
        int releaseYear;

        public Movie(int id, int releaseYear) {
            this.id = id;
            this.releaseYear = releaseYear;
        }
    }
}

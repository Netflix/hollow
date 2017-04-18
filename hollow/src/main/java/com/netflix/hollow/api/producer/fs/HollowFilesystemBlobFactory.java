/*
 *
 *  Copyright 2017 Netflix, Inc.
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
package com.netflix.hollow.api.producer.fs;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.core.write.HollowBlobWriter;

import java.io.*;

public class HollowFilesystemBlobFactory {
   public HollowProducer.Blob withNamespace(String namespace, long fromVersion, long toVersion, String dir, HollowProducer.Blob.Type type) {
      return new FilesystemBlob(namespace, fromVersion, toVersion, dir, type);
   }

   public interface FsBlob extends HollowProducer.Blob {
      File getFile();
   }

   public static class FilesystemBlob implements FsBlob {
      protected final String namespace;
      protected final long fromVersion;
      protected final long toVersion;
      protected final String dir;
      protected final HollowProducer.Blob.Type type;
      protected final File file;

      private FilesystemBlob(String namespace, long fromVersion, long toVersion, String dir, HollowProducer.Blob.Type type) {
         this.namespace = namespace;
         this.fromVersion = fromVersion;
         this.toVersion = toVersion;
         this.type = type;
         this.dir = dir;

         switch (type) {
            case SNAPSHOT:
               this.file = new File(dir, String.format("%s-%s-%d", namespace, type.prefix, toVersion));
               break;
            case DELTA:
               this.file = new File(dir, String.format("%s-%s-%d-%d", namespace, type.prefix, fromVersion, toVersion));
               break;
            case REVERSE_DELTA:
               this.file = new File(dir, String.format("%s-%s-%d-%d", namespace, type.prefix, toVersion, fromVersion));
               break;
            default:
               throw new IllegalStateException("unknown blob type, type=" + type);
         }
      }

      @Override
      public void write(HollowBlobWriter writer) throws IOException {
         this.file.getParentFile().mkdirs();
         this.file.createNewFile();
         try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
            switch (type) {
               case SNAPSHOT:
                  writer.writeSnapshot(os);
                  break;
               case DELTA:
                  writer.writeDelta(os);
                  break;
               case REVERSE_DELTA:
                  writer.writeReverseDelta(os);
                  break;
               default:
                  throw new IllegalStateException("unknown type, type=" + type);
            }
         }
      }

      @Override
      public Type getType() {
         return this.type;
      }

      @Override
      public InputStream newInputStream() throws IOException {
         return new BufferedInputStream(new FileInputStream(this.file));
      }

      @Override
      public long getFromVersion() {
         return this.fromVersion;
      }

      @Override
      public long getToVersion() {
         return this.toVersion;
      }

      @Override
      public void cleanup() {
         if (this.file != null) this.file.delete();
      }

      @Override
      public File getFile() {
         return this.file;
      }
   }
}

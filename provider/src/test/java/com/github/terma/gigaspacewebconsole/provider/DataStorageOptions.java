/*
Copyright 2015-2017 Artem Stasiuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.github.terma.gigaspacewebconsole.provider;

import com.gigaspaces.annotation.pojo.*;
import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import com.gigaspaces.metadata.index.SpaceIndexType;
import com.github.terma.gigaspacewebconsole.provider.driver.GigaSpaceUtils;
import org.junit.Assert;
import org.openspaces.core.GigaSpace;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * GS embedded space same with remote:
 * pojo - small types with embedded object - 1m - 664M
 * pojo - big types - 1m - 430m -30% (455m 403m)
 * pojo - small types - 1m - 430m -30% (455m 403m)
 * space document - small types - 1m - 610m -15% (702m)
 * space document - big types - 1m - 715m (R 700m)
 * plain small types - 1m - 71m
 * <p/>
 * 1m int index ~60m
 * 2m pojo ~900m
 */
public class DataStorageOptions {

    public static void main(String[] args) {
//        GigaSpace gigaSpace = GigaSpaceUtils.getGigaSpace("/./pojo");
        GigaSpace gigaSpace = GigaSpaceUtils.getGigaSpace("jini:/*/*/gs10?locators=127.0.0.1:4700");
        gigaSpace.clear(null);

        final int count = 1000000;

        final Generator generator;
//        generator = new SpaceDocumentSmallTypes();
//        generator = new SpaceDocumentBigTypes();
//        generator = new PojoBigTypes();
//        generator = new PojoSmallTypes();
        generator = new PojoSmallEmbeddedTypes();
//        generator = new PlainSmallTypes();

        GsMemMeter memMeter = new GsMemMeter(gigaSpace);
//        MemMeter memMeter = new MemMeter();
        Object r = generator.populate(gigaSpace, count);
        System.out.println(generator.getClass().getSimpleName() + ", count " + count + ", used " + memMeter.getUsedMb() + " mb");
        System.out.println(r.hashCode());
        System.exit(0);
    }

    private interface Generator {

        Object populate(GigaSpace gigaSpace, int count);

    }

    private static class SpaceDocumentSmallTypes implements Generator {
        @Override
        public Object populate(GigaSpace gigaSpace, int count) {
            String typeName = "Example";
            SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder(typeName)
                    .idProperty("id", true).create();
            gigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);

            List<SpaceDocument> buffer = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                SpaceDocument spaceDocument = new SpaceDocument(typeName);
                spaceDocument.setProperty("cg", 11);
                spaceDocument.setProperty("pg", 12);
                spaceDocument.setProperty("cid", 15);
                spaceDocument.setProperty("mid", 14);
                spaceDocument.setProperty("r", 3);
                spaceDocument.setProperty("or", 2);
                spaceDocument.setProperty("sb", 300);
                spaceDocument.setProperty("sv", 33);
                spaceDocument.setProperty("timestamp", System.currentTimeMillis());
                buffer.add(spaceDocument);
            }
            gigaSpace.writeMultiple(buffer.toArray(new Object[buffer.size()]));

            Assert.assertEquals(buffer.size(), gigaSpace.count(new SpaceDocument(typeName)));
            return buffer;
        }

    }

    private static class PojoSmallTypes implements Generator {

        @Override
        public Object populate(GigaSpace gigaSpace, int count) {
            Random random = new Random();
            gigaSpace.getTypeManager().registerTypeDescriptor(SmallExample.class);

            List<SmallExample> buffer = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                SmallExample example = new SmallExample();
                example.cg = random.nextInt(100);
                example.pg = random.nextInt(100);
                example.cid = random.nextInt(500);
                example.mid = random.nextInt(500);
                example.r = random.nextInt(5);
                example.or = random.nextInt(5);
                example.sb = random.nextInt(1000);
                example.sv = random.nextInt(100);
                example.timestamp = System.currentTimeMillis();
                buffer.add(example);
            }
            gigaSpace.writeMultiple(buffer.toArray(new Object[buffer.size()]));

            Assert.assertEquals(buffer.size(), gigaSpace.count(new SmallExample()));
            return buffer;
        }

        @SpaceClass
        public static class SmallExample {

            private String id;
            private int cg;
            private int pg;
            private int cid;
            private int mid;
            private int r;
            private int or;
            private int sb;
            private int sv;
            private long timestamp;

            @SpaceId(autoGenerate = true)
            @SpaceRouting
            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            @SpaceProperty(nullValue = "0")
            public int getCg() {
                return cg;
            }

            public void setCg(int cg) {
                this.cg = cg;
            }

            @SpaceProperty(nullValue = "0")
            @SpaceIndex(type = SpaceIndexType.BASIC)
            public int getPg() {
                return pg;
            }

            public void setPg(int pg) {
                this.pg = pg;
            }

            @SpaceProperty(nullValue = "0")
            @SpaceIndex(type = SpaceIndexType.BASIC)
            public int getCid() {
                return cid;
            }

            public void setCid(int cid) {
                this.cid = cid;
            }

            @SpaceProperty(nullValue = "0")
            public int getMid() {
                return mid;
            }

            public void setMid(int mid) {
                this.mid = mid;
            }

            @SpaceProperty(nullValue = "0")
            @SpaceIndex(type = SpaceIndexType.BASIC)
            public int getR() {
                return r;
            }

            public void setR(int r) {
                this.r = r;
            }

            @SpaceProperty(nullValue = "0")
            public int getOr() {
                return or;
            }

            public void setOr(int or) {
                this.or = or;
            }

            @SpaceProperty(nullValue = "0")
            public int getSb() {
                return sb;
            }

            public void setSb(int sb) {
                this.sb = sb;
            }

            @SpaceProperty(nullValue = "0")
            public int getSv() {
                return sv;
            }

            public void setSv(int sv) {
                this.sv = sv;
            }

            @SpaceProperty(nullValue = "0")
            public long getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(long timestamp) {
                this.timestamp = timestamp;
            }
        }

    }

    private static class PojoSmallEmbeddedTypes implements Generator {

        @Override
        public Object populate(GigaSpace gigaSpace, int count) {
            Random random = new Random();
            gigaSpace.getTypeManager().registerTypeDescriptor(SmallEmbeddedExample.class);

            List<SmallEmbeddedExample> buffer = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                SmallEmbeddedExample example = new SmallEmbeddedExample();
//                example.embedded = new Embedded();
//                Embedded embedded = example.embedded;
//                embedded.cg = random.nextInt(100);
//                embedded.pg = random.nextInt(100);
//                embedded.cid = random.nextInt(500);
//                embedded.mid = random.nextInt(500);
//                embedded.r = random.nextInt(5);
//                embedded.or = random.nextInt(5);
//                embedded.sb = random.nextInt(1000);
//                embedded.sv = random.nextInt(100);
//                embedded.timestamp = System.currentTimeMillis();
                buffer.add(example);
            }
            gigaSpace.writeMultiple(buffer.toArray(new Object[buffer.size()]));

            Assert.assertEquals(buffer.size(), gigaSpace.count(new SmallEmbeddedExample()));
            return buffer;
        }


        @SpaceClass
        public static class SmallEmbeddedExample {

            private String id;
//            private Embedded embedded;

            @SpaceId(autoGenerate = true)
            @SpaceRouting
            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

//            @SpaceStorageType(storageType = StorageType.BINARY)
//            public Embedded getEmbedded() {
//                return embedded;
//            }
//
//            public void setEmbedded(Embedded embedded) {
//                this.embedded = embedded;
//            }
        }

        public static class Embedded implements Serializable {

            public int cg;
            public int pg;
            public int cid;
            public int mid;
            public int r;
            public int or;
            public int sb;
            public int sv;
            public long timestamp;

        }

    }

    private static class PlainSmallTypes implements Generator {

        @Override
        public Object populate(GigaSpace gigaSpace, int count) {
            Random random = new Random();
            List<SmallExample> buffer = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                SmallExample example = new SmallExample();
                UUID uuid = UUID.randomUUID();
                example.id1 = uuid.getLeastSignificantBits();
                example.id2 = uuid.getMostSignificantBits();
                example.cg = random.nextInt(100);
                example.pg = random.nextInt(100);
                example.cid = random.nextInt(500);
                example.mid = random.nextInt(500);
                example.r = random.nextInt(5);
                example.or = random.nextInt(5);
                example.sb = random.nextInt(1000);
                example.sv = random.nextInt(100);
                example.timestamp = System.currentTimeMillis();
                buffer.add(example);
            }

//            System.out.println(ClassLayout.parseClass(SmallExample.class).toPrintable());

            return buffer;
        }

        public static class SmallExample {

            private long id1;
            private long id2;
            private int cg;
            private int pg;
            private int cid;
            private int mid;
            private int r;
            private int or;
            private int sb;
            private int sv;
            private long timestamp;

            public int getCg() {
                return cg;
            }

            public void setCg(int cg) {
                this.cg = cg;
            }

            public int getPg() {
                return pg;
            }

            public void setPg(int pg) {
                this.pg = pg;
            }

            public int getCid() {
                return cid;
            }

            public void setCid(int cid) {
                this.cid = cid;
            }

            public int getMid() {
                return mid;
            }

            public void setMid(int mid) {
                this.mid = mid;
            }

            public int getR() {
                return r;
            }

            public void setR(int r) {
                this.r = r;
            }

            public int getOr() {
                return or;
            }

            public void setOr(int or) {
                this.or = or;
            }

            public int getSb() {
                return sb;
            }

            public void setSb(int sb) {
                this.sb = sb;
            }

            public int getSv() {
                return sv;
            }

            public void setSv(int sv) {
                this.sv = sv;
            }

            public long getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(long timestamp) {
                this.timestamp = timestamp;
            }

            public long getId1() {
                return id1;
            }

            public void setId1(long id1) {
                this.id1 = id1;
            }

            public long getId2() {
                return id2;
            }

            public void setId2(long id2) {
                this.id2 = id2;
            }
        }

    }

    private static class PojoBigTypes implements Generator {

        @Override
        public Object populate(GigaSpace gigaSpace, int count) {
            Random random = new Random();
            gigaSpace.getTypeManager().registerTypeDescriptor(BigExample.class);

            List<BigExample> buffer = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                BigExample example = new BigExample();
                example.cg = random.nextInt(100);
                example.pg = random.nextInt(100);
                example.cid = random.nextInt(500);
                example.mid = random.nextInt(500);
                example.r = random.nextInt(5);
                example.or = random.nextInt(5);
                example.sb = random.nextInt(1000);
                example.sv = random.nextInt(100);
                example.timestamp = System.currentTimeMillis();
                buffer.add(example);
            }
            gigaSpace.writeMultiple(buffer.toArray(new Object[buffer.size()]));

            Assert.assertEquals(buffer.size(), gigaSpace.count(new BigExample()));
            return buffer;
        }

        @SpaceClass
        public static class BigExample {

            private String id;
            private Integer cg;
            private Integer pg;
            private Integer cid;
            private Integer mid;
            private Integer r;
            private Integer or;
            private Integer sb;
            private Integer sv;
            private Long timestamp;

            @SpaceId(autoGenerate = true)
            @SpaceRouting
            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public Integer getCg() {
                return cg;
            }

            public void setCg(int cg) {
                this.cg = cg;
            }

            public Integer getPg() {
                return pg;
            }

            public void setPg(int pg) {
                this.pg = pg;
            }

            public Integer getCid() {
                return cid;
            }

            public void setCid(int cid) {
                this.cid = cid;
            }

            public Integer getMid() {
                return mid;
            }

            public void setMid(int mid) {
                this.mid = mid;
            }

            public Integer getR() {
                return r;
            }

            public void setR(int r) {
                this.r = r;
            }

            public Integer getOr() {
                return or;
            }

            public void setOr(int or) {
                this.or = or;
            }

            public Integer getSb() {
                return sb;
            }

            public void setSb(int sb) {
                this.sb = sb;
            }

            public Integer getSv() {
                return sv;
            }

            public void setSv(int sv) {
                this.sv = sv;
            }

            public Long getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(long timestamp) {
                this.timestamp = timestamp;
            }
        }

    }

    private static class SpaceDocumentBigTypes implements Generator {
        @Override
        public Object populate(GigaSpace gigaSpace, int count) {
            String typeName = "Example";
            SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder(typeName)
                    .idProperty("id", true).create();
            gigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);

            List<SpaceDocument> buffer = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                SpaceDocument spaceDocument = new SpaceDocument(typeName);
                spaceDocument.setProperty("cg", new Integer(11));
                spaceDocument.setProperty("pg", new Integer(12));
                spaceDocument.setProperty("cid", new Integer(15));
                spaceDocument.setProperty("mid", new Integer(14));
                spaceDocument.setProperty("r", new Integer(3));
                spaceDocument.setProperty("or", new Integer(2));
                spaceDocument.setProperty("sb", new Integer(300));
                spaceDocument.setProperty("sv", new Integer(33));
                spaceDocument.setProperty("timestamp", System.currentTimeMillis());
                buffer.add(spaceDocument);
            }
            gigaSpace.writeMultiple(buffer.toArray(new Object[buffer.size()]));

            Assert.assertEquals(buffer.size(), gigaSpace.count(new SpaceDocument(typeName)));
            return buffer;
        }

    }


}

Protocol Buffer 是使用 Google 开发的一种开源数据交换格式，独立于语言，独立于平台，采用二进制传输。

# Protobuffer 语法

## 标识符

Protobuffer 协议的标识符为 message 或 enum，message 代表消息类型，enum 代表枚举类型，在通过Protobuffer 编译器编译之后，都生成 Java 的一个类。

## 修饰符

协议字段格式：role type name = tag [default value];

其中，role 有以下三种取值：

- required：该字段不能为空，必须传递值，否则 message 不能被正确初始化。
- optional：该字段可以为空，不管该字段是否传值，message 都能正确初始化。
- repeated：重复的字段，等同动态数组，编译成 Java 后即为 List，其数据可以为空。

## 数据类型

|Proto Type|Java Type|
|---|---|
|double|double|
|float|float|
|int32|int|
|int64|long|
|uint32|int|
|uint64|long|
|sint32|int|
|sint64|long|
|fixed32|int|
|fixed64|long|
|sfixed32|int|
|sfixed64|long|
|bool|boolean|
|string|string|
|bytes|bytestring|

## Package

在 Proto 文件中，可以用 Package 指定 Java 的包名，如：

```
package demo;
```

## Option

Option 可以定义一些常用选项，如：

- java_package：指定 Java 文件的包名，并输出到指定目录下。
- java_outer_classname：指定 Java 文件的类名。
- optimize_for：
    - SPEED：缺省条件，生成代码效率高，代码占用空间大。
    - CODE：生成代码占用空间小，效率低。
    - LITE_RUNTIME：生成的代码效率高，占用空间小，但反射功能较弱。

# Idea 配置 Maven 和 Protobuffer

## 安装Protobuf Support插件。
## pom.xml 文件配置

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com</groupId>
    <artifactId>Protobuffer</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <grpc.version>1.6.1</grpc.version>
        <protobuf.version>3.3.0</protobuf.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-netty</artifactId>
            <version>${grpc.version}</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-protobuf</artifactId>
            <version>${grpc.version}</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-stub</artifactId>
            <version>${grpc.version}</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.google.protobuf</groupId>
            <artifactId>protobuf-java</artifactId>
            <version>${protobuf.version}</version>
        </dependency>
    </dependencies>

    <build>
        <extensions>
            <extension>
                <groupId>kr.motd.maven</groupId>
                <artifactId>os-maven-plugin</artifactId>
                <version>1.5.0.Final</version>
            </extension>
        </extensions>
        <plugins>
            <plugin>
                <groupId>org.xolstice.maven.plugins</groupId>
                <artifactId>protobuf-maven-plugin</artifactId>
                <version>0.5.0</version>
                <configuration>
                    <protocArtifact>
                        com.google.protobuf:protoc:${protobuf.version}:exe:${os.detected.classifier}
                    </protocArtifact>
                    <pluginId>grpc-java</pluginId>
                    <pluginArtifact>
                        io.grpc:protoc-gen-grpc-java:${grpc.version}:exe:${os.detected.classifier}
                    </pluginArtifact>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>compile-custom</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

## 编译：Maven-Compile

即可在 target/generated-sources/protobuf/java 中找到编译好的 Java 类。

如果.proto文件中，显示“File not found”提示，一般是未设置.proto文件所在文件夹为源文件，需要在.proto文件所在的文件夹上右键，设置目录为源文件根目录。

# 示例

## demo.proto

```
option java_outer_classname = "DemoProto";

message Obj1 {
	required int32 val1 = 1;
	repeated Obj2 val2 = 2;
}

message Obj2 {
	required string subval1 = 1;
	optional double subval2 = 2;
}
```

## PbDemo.java

```
import proto.DemoProto.Obj1;
import proto.DemoProto.Obj2;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PbDemo {
    public static void main(String[] args) {
        PbDemo demo = new PbDemo();
        demo.run();
    }

    public void run() {
        // 构建Protocol Javabean
        Obj1.Builder obj1 = Obj1.newBuilder();
        obj1.setVal1(1);
        Obj2.Builder obj21 = Obj2.newBuilder();
        obj21.setSubval1("protocol buffer demo 1");
        obj21.setSubval2(1.9999d);
        obj1.addVal2(obj21);
        Obj2.Builder obj22 = Obj2.newBuilder();
        obj22.setSubval1("protocol buffer demo 2");
        obj22.setSubval2(1.9999d);
        obj1.addVal2(obj22);


        System.out.println("obj1:");
        System.out.println(obj1);
        System.out.println("obj1.build:");
        System.out.println(obj1.build());
        // 接收到的proto直接对应Protocol Javabean
        System.out.println("接收到的proto直接对应Protocol Javabean");
        System.out.println("val1:" + obj1.getVal1());
        System.out.println("val2:" + obj1.getVal2List());

        System.out.println("=====序列化=====");
        try {
            obj1.build().writeTo(new FileOutputStream("Protobuffer/src/main/resources/proto.ser"));
            DemoProto.Obj1 obj = DemoProto.Obj1.parseFrom(new FileInputStream("Protobuffer/src/main/resources/proto.ser"));
            System.out.println(obj);
            System.out.println(obj.getVal1());
            System.out.println(obj.getVal2List());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

```

## proto.ser

```
!
protocol buffer demo 1St$ÿÿ?!
protocol buffer demo 2St$ÿÿ?
```

## DemoProto.java（自动生成，不必修改）

```
package proto;// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: demo.proto

public final class DemoProto {
  private DemoProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface Obj1OrBuilder extends
      // @@protoc_insertion_point(interface_extends:Obj1)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>required int32 val1 = 1;</code>
     */
    boolean hasVal1();
    /**
     * <code>required int32 val1 = 1;</code>
     */
    int getVal1();

    /**
     * <code>repeated .Obj2 val2 = 2;</code>
     */
    java.util.List<Obj2>
        getVal2List();
    /**
     * <code>repeated .Obj2 val2 = 2;</code>
     */
    DemoProto.Obj2 getVal2(int index);
    /**
     * <code>repeated .Obj2 val2 = 2;</code>
     */
    int getVal2Count();
    /**
     * <code>repeated .Obj2 val2 = 2;</code>
     */
    java.util.List<? extends Obj2OrBuilder>
        getVal2OrBuilderList();
    /**
     * <code>repeated .Obj2 val2 = 2;</code>
     */
    DemoProto.Obj2OrBuilder getVal2OrBuilder(
            int index);
  }
  /**
   * Protobuf type {@code Obj1}
   */
  public  static final class Obj1 extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:Obj1)
      Obj1OrBuilder {
    // Use Obj1.newBuilder() to construct.
    private Obj1(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Obj1() {
      val1_ = 0;
      val2_ = java.util.Collections.emptyList();
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Obj1(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 8: {
              bitField0_ |= 0x00000001;
              val1_ = input.readInt32();
              break;
            }
            case 18: {
              if (!((mutable_bitField0_ & 0x00000002) == 0x00000002)) {
                val2_ = new java.util.ArrayList<Obj2>();
                mutable_bitField0_ |= 0x00000002;
              }
              val2_.add(
                  input.readMessage(DemoProto.Obj2.PARSER, extensionRegistry));
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        if (((mutable_bitField0_ & 0x00000002) == 0x00000002)) {
          val2_ = java.util.Collections.unmodifiableList(val2_);
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return DemoProto.internal_static_Obj1_descriptor;
    }

    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return DemoProto.internal_static_Obj1_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              DemoProto.Obj1.class, DemoProto.Obj1.Builder.class);
    }

    private int bitField0_;
    public static final int VAL1_FIELD_NUMBER = 1;
    private int val1_;
    /**
     * <code>required int32 val1 = 1;</code>
     */
    public boolean hasVal1() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required int32 val1 = 1;</code>
     */
    public int getVal1() {
      return val1_;
    }

    public static final int VAL2_FIELD_NUMBER = 2;
    private java.util.List<Obj2> val2_;
    /**
     * <code>repeated .Obj2 val2 = 2;</code>
     */
    public java.util.List<Obj2> getVal2List() {
      return val2_;
    }
    /**
     * <code>repeated .Obj2 val2 = 2;</code>
     */
    public java.util.List<? extends Obj2OrBuilder>
        getVal2OrBuilderList() {
      return val2_;
    }
    /**
     * <code>repeated .Obj2 val2 = 2;</code>
     */
    public int getVal2Count() {
      return val2_.size();
    }
    /**
     * <code>repeated .Obj2 val2 = 2;</code>
     */
    public DemoProto.Obj2 getVal2(int index) {
      return val2_.get(index);
    }
    /**
     * <code>repeated .Obj2 val2 = 2;</code>
     */
    public DemoProto.Obj2OrBuilder getVal2OrBuilder(
        int index) {
      return val2_.get(index);
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasVal1()) {
        memoizedIsInitialized = 0;
        return false;
      }
      for (int i = 0; i < getVal2Count(); i++) {
        if (!getVal2(i).isInitialized()) {
          memoizedIsInitialized = 0;
          return false;
        }
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeInt32(1, val1_);
      }
      for (int i = 0; i < val2_.size(); i++) {
        output.writeMessage(2, val2_.get(i));
      }
      unknownFields.writeTo(output);
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, val1_);
      }
      for (int i = 0; i < val2_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, val2_.get(i));
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof DemoProto.Obj1)) {
        return super.equals(obj);
      }
      DemoProto.Obj1 other = (DemoProto.Obj1) obj;

      boolean result = true;
      result = result && (hasVal1() == other.hasVal1());
      if (hasVal1()) {
        result = result && (getVal1()
            == other.getVal1());
      }
      result = result && getVal2List()
          .equals(other.getVal2List());
      result = result && unknownFields.equals(other.unknownFields);
      return result;
    }

    @Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (hasVal1()) {
        hash = (37 * hash) + VAL1_FIELD_NUMBER;
        hash = (53 * hash) + getVal1();
      }
      if (getVal2Count() > 0) {
        hash = (37 * hash) + VAL2_FIELD_NUMBER;
        hash = (53 * hash) + getVal2List().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static DemoProto.Obj1 parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static DemoProto.Obj1 parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static DemoProto.Obj1 parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static DemoProto.Obj1 parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static DemoProto.Obj1 parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static DemoProto.Obj1 parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static DemoProto.Obj1 parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static DemoProto.Obj1 parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static DemoProto.Obj1 parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static DemoProto.Obj1 parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static DemoProto.Obj1 parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static DemoProto.Obj1 parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(DemoProto.Obj1 prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code Obj1}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:Obj1)
        DemoProto.Obj1OrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return DemoProto.internal_static_Obj1_descriptor;
      }

      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return DemoProto.internal_static_Obj1_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                DemoProto.Obj1.class, DemoProto.Obj1.Builder.class);
      }

      // Construct using DemoProto.Obj1.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
          getVal2FieldBuilder();
        }
      }
      public Builder clear() {
        super.clear();
        val1_ = 0;
        bitField0_ = (bitField0_ & ~0x00000001);
        if (val2Builder_ == null) {
          val2_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000002);
        } else {
          val2Builder_.clear();
        }
        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return DemoProto.internal_static_Obj1_descriptor;
      }

      public DemoProto.Obj1 getDefaultInstanceForType() {
        return DemoProto.Obj1.getDefaultInstance();
      }

      public DemoProto.Obj1 build() {
        DemoProto.Obj1 result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public DemoProto.Obj1 buildPartial() {
        DemoProto.Obj1 result = new DemoProto.Obj1(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.val1_ = val1_;
        if (val2Builder_ == null) {
          if (((bitField0_ & 0x00000002) == 0x00000002)) {
            val2_ = java.util.Collections.unmodifiableList(val2_);
            bitField0_ = (bitField0_ & ~0x00000002);
          }
          result.val2_ = val2_;
        } else {
          result.val2_ = val2Builder_.build();
        }
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder clone() {
        return (Builder) super.clone();
      }
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.setField(field, value);
      }
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof DemoProto.Obj1) {
          return mergeFrom((DemoProto.Obj1)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(DemoProto.Obj1 other) {
        if (other == DemoProto.Obj1.getDefaultInstance()) return this;
        if (other.hasVal1()) {
          setVal1(other.getVal1());
        }
        if (val2Builder_ == null) {
          if (!other.val2_.isEmpty()) {
            if (val2_.isEmpty()) {
              val2_ = other.val2_;
              bitField0_ = (bitField0_ & ~0x00000002);
            } else {
              ensureVal2IsMutable();
              val2_.addAll(other.val2_);
            }
            onChanged();
          }
        } else {
          if (!other.val2_.isEmpty()) {
            if (val2Builder_.isEmpty()) {
              val2Builder_.dispose();
              val2Builder_ = null;
              val2_ = other.val2_;
              bitField0_ = (bitField0_ & ~0x00000002);
              val2Builder_ =
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                   getVal2FieldBuilder() : null;
            } else {
              val2Builder_.addAllMessages(other.val2_);
            }
          }
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        if (!hasVal1()) {
          return false;
        }
        for (int i = 0; i < getVal2Count(); i++) {
          if (!getVal2(i).isInitialized()) {
            return false;
          }
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        DemoProto.Obj1 parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (DemoProto.Obj1) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private int val1_ ;
      /**
       * <code>required int32 val1 = 1;</code>
       */
      public boolean hasVal1() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required int32 val1 = 1;</code>
       */
      public int getVal1() {
        return val1_;
      }
      /**
       * <code>required int32 val1 = 1;</code>
       */
      public Builder setVal1(int value) {
        bitField0_ |= 0x00000001;
        val1_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int32 val1 = 1;</code>
       */
      public Builder clearVal1() {
        bitField0_ = (bitField0_ & ~0x00000001);
        val1_ = 0;
        onChanged();
        return this;
      }

      private java.util.List<Obj2> val2_ =
        java.util.Collections.emptyList();
      private void ensureVal2IsMutable() {
        if (!((bitField0_ & 0x00000002) == 0x00000002)) {
          val2_ = new java.util.ArrayList<Obj2>(val2_);
          bitField0_ |= 0x00000002;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilderV3<
          Obj2, Obj2.Builder, Obj2OrBuilder> val2Builder_;

      /**
       * <code>repeated .Obj2 val2 = 2;</code>
       */
      public java.util.List<Obj2> getVal2List() {
        if (val2Builder_ == null) {
          return java.util.Collections.unmodifiableList(val2_);
        } else {
          return val2Builder_.getMessageList();
        }
      }
      /**
       * <code>repeated .Obj2 val2 = 2;</code>
       */
      public int getVal2Count() {
        if (val2Builder_ == null) {
          return val2_.size();
        } else {
          return val2Builder_.getCount();
        }
      }
      /**
       * <code>repeated .Obj2 val2 = 2;</code>
       */
      public DemoProto.Obj2 getVal2(int index) {
        if (val2Builder_ == null) {
          return val2_.get(index);
        } else {
          return val2Builder_.getMessage(index);
        }
      }
      /**
       * <code>repeated .Obj2 val2 = 2;</code>
       */
      public Builder setVal2(
          int index, DemoProto.Obj2 value) {
        if (val2Builder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureVal2IsMutable();
          val2_.set(index, value);
          onChanged();
        } else {
          val2Builder_.setMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .Obj2 val2 = 2;</code>
       */
      public Builder setVal2(
          int index, DemoProto.Obj2.Builder builderForValue) {
        if (val2Builder_ == null) {
          ensureVal2IsMutable();
          val2_.set(index, builderForValue.build());
          onChanged();
        } else {
          val2Builder_.setMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .Obj2 val2 = 2;</code>
       */
      public Builder addVal2(DemoProto.Obj2 value) {
        if (val2Builder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureVal2IsMutable();
          val2_.add(value);
          onChanged();
        } else {
          val2Builder_.addMessage(value);
        }
        return this;
      }
      /**
       * <code>repeated .Obj2 val2 = 2;</code>
       */
      public Builder addVal2(
          int index, DemoProto.Obj2 value) {
        if (val2Builder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureVal2IsMutable();
          val2_.add(index, value);
          onChanged();
        } else {
          val2Builder_.addMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .Obj2 val2 = 2;</code>
       */
      public Builder addVal2(
          DemoProto.Obj2.Builder builderForValue) {
        if (val2Builder_ == null) {
          ensureVal2IsMutable();
          val2_.add(builderForValue.build());
          onChanged();
        } else {
          val2Builder_.addMessage(builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .Obj2 val2 = 2;</code>
       */
      public Builder addVal2(
          int index, DemoProto.Obj2.Builder builderForValue) {
        if (val2Builder_ == null) {
          ensureVal2IsMutable();
          val2_.add(index, builderForValue.build());
          onChanged();
        } else {
          val2Builder_.addMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .Obj2 val2 = 2;</code>
       */
      public Builder addAllVal2(
          Iterable<? extends Obj2> values) {
        if (val2Builder_ == null) {
          ensureVal2IsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(
              values, val2_);
          onChanged();
        } else {
          val2Builder_.addAllMessages(values);
        }
        return this;
      }
      /**
       * <code>repeated .Obj2 val2 = 2;</code>
       */
      public Builder clearVal2() {
        if (val2Builder_ == null) {
          val2_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000002);
          onChanged();
        } else {
          val2Builder_.clear();
        }
        return this;
      }
      /**
       * <code>repeated .Obj2 val2 = 2;</code>
       */
      public Builder removeVal2(int index) {
        if (val2Builder_ == null) {
          ensureVal2IsMutable();
          val2_.remove(index);
          onChanged();
        } else {
          val2Builder_.remove(index);
        }
        return this;
      }
      /**
       * <code>repeated .Obj2 val2 = 2;</code>
       */
      public DemoProto.Obj2.Builder getVal2Builder(
          int index) {
        return getVal2FieldBuilder().getBuilder(index);
      }
      /**
       * <code>repeated .Obj2 val2 = 2;</code>
       */
      public DemoProto.Obj2OrBuilder getVal2OrBuilder(
          int index) {
        if (val2Builder_ == null) {
          return val2_.get(index);  } else {
          return val2Builder_.getMessageOrBuilder(index);
        }
      }
      /**
       * <code>repeated .Obj2 val2 = 2;</code>
       */
      public java.util.List<? extends Obj2OrBuilder>
           getVal2OrBuilderList() {
        if (val2Builder_ != null) {
          return val2Builder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(val2_);
        }
      }
      /**
       * <code>repeated .Obj2 val2 = 2;</code>
       */
      public DemoProto.Obj2.Builder addVal2Builder() {
        return getVal2FieldBuilder().addBuilder(
            DemoProto.Obj2.getDefaultInstance());
      }
      /**
       * <code>repeated .Obj2 val2 = 2;</code>
       */
      public DemoProto.Obj2.Builder addVal2Builder(
          int index) {
        return getVal2FieldBuilder().addBuilder(
            index, DemoProto.Obj2.getDefaultInstance());
      }
      /**
       * <code>repeated .Obj2 val2 = 2;</code>
       */
      public java.util.List<Obj2.Builder>
           getVal2BuilderList() {
        return getVal2FieldBuilder().getBuilderList();
      }
      private com.google.protobuf.RepeatedFieldBuilderV3<
          Obj2, Obj2.Builder, Obj2OrBuilder>
          getVal2FieldBuilder() {
        if (val2Builder_ == null) {
          val2Builder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
              Obj2, Obj2.Builder, Obj2OrBuilder>(
                  val2_,
                  ((bitField0_ & 0x00000002) == 0x00000002),
                  getParentForChildren(),
                  isClean());
          val2_ = null;
        }
        return val2Builder_;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:Obj1)
    }

    // @@protoc_insertion_point(class_scope:Obj1)
    private static final DemoProto.Obj1 DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new DemoProto.Obj1();
    }

    public static DemoProto.Obj1 getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    @Deprecated public static final com.google.protobuf.Parser<Obj1>
        PARSER = new com.google.protobuf.AbstractParser<Obj1>() {
      public Obj1 parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new Obj1(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Obj1> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<Obj1> getParserForType() {
      return PARSER;
    }

    public DemoProto.Obj1 getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface Obj2OrBuilder extends
      // @@protoc_insertion_point(interface_extends:Obj2)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>required string subval1 = 1;</code>
     */
    boolean hasSubval1();
    /**
     * <code>required string subval1 = 1;</code>
     */
    String getSubval1();
    /**
     * <code>required string subval1 = 1;</code>
     */
    com.google.protobuf.ByteString
        getSubval1Bytes();

    /**
     * <code>optional double subval2 = 2;</code>
     */
    boolean hasSubval2();
    /**
     * <code>optional double subval2 = 2;</code>
     */
    double getSubval2();
  }
  /**
   * Protobuf type {@code Obj2}
   */
  public  static final class Obj2 extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:Obj2)
      Obj2OrBuilder {
    // Use Obj2.newBuilder() to construct.
    private Obj2(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Obj2() {
      subval1_ = "";
      subval2_ = 0D;
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Obj2(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 10: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000001;
              subval1_ = bs;
              break;
            }
            case 17: {
              bitField0_ |= 0x00000002;
              subval2_ = input.readDouble();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return DemoProto.internal_static_Obj2_descriptor;
    }

    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return DemoProto.internal_static_Obj2_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              DemoProto.Obj2.class, DemoProto.Obj2.Builder.class);
    }

    private int bitField0_;
    public static final int SUBVAL1_FIELD_NUMBER = 1;
    private volatile Object subval1_;
    /**
     * <code>required string subval1 = 1;</code>
     */
    public boolean hasSubval1() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required string subval1 = 1;</code>
     */
    public String getSubval1() {
      Object ref = subval1_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          subval1_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string subval1 = 1;</code>
     */
    public com.google.protobuf.ByteString
        getSubval1Bytes() {
      Object ref = subval1_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        subval1_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int SUBVAL2_FIELD_NUMBER = 2;
    private double subval2_;
    /**
     * <code>optional double subval2 = 2;</code>
     */
    public boolean hasSubval2() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>optional double subval2 = 2;</code>
     */
    public double getSubval2() {
      return subval2_;
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasSubval1()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, subval1_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeDouble(2, subval2_);
      }
      unknownFields.writeTo(output);
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, subval1_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeDoubleSize(2, subval2_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof DemoProto.Obj2)) {
        return super.equals(obj);
      }
      DemoProto.Obj2 other = (DemoProto.Obj2) obj;

      boolean result = true;
      result = result && (hasSubval1() == other.hasSubval1());
      if (hasSubval1()) {
        result = result && getSubval1()
            .equals(other.getSubval1());
      }
      result = result && (hasSubval2() == other.hasSubval2());
      if (hasSubval2()) {
        result = result && (
            Double.doubleToLongBits(getSubval2())
            == Double.doubleToLongBits(
                other.getSubval2()));
      }
      result = result && unknownFields.equals(other.unknownFields);
      return result;
    }

    @Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (hasSubval1()) {
        hash = (37 * hash) + SUBVAL1_FIELD_NUMBER;
        hash = (53 * hash) + getSubval1().hashCode();
      }
      if (hasSubval2()) {
        hash = (37 * hash) + SUBVAL2_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            Double.doubleToLongBits(getSubval2()));
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static DemoProto.Obj2 parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static DemoProto.Obj2 parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static DemoProto.Obj2 parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static DemoProto.Obj2 parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static DemoProto.Obj2 parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static DemoProto.Obj2 parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static DemoProto.Obj2 parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static DemoProto.Obj2 parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static DemoProto.Obj2 parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static DemoProto.Obj2 parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static DemoProto.Obj2 parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static DemoProto.Obj2 parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(DemoProto.Obj2 prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code Obj2}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:Obj2)
        DemoProto.Obj2OrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return DemoProto.internal_static_Obj2_descriptor;
      }

      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return DemoProto.internal_static_Obj2_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                DemoProto.Obj2.class, DemoProto.Obj2.Builder.class);
      }

      // Construct using DemoProto.Obj2.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      public Builder clear() {
        super.clear();
        subval1_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        subval2_ = 0D;
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return DemoProto.internal_static_Obj2_descriptor;
      }

      public DemoProto.Obj2 getDefaultInstanceForType() {
        return DemoProto.Obj2.getDefaultInstance();
      }

      public DemoProto.Obj2 build() {
        DemoProto.Obj2 result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public DemoProto.Obj2 buildPartial() {
        DemoProto.Obj2 result = new DemoProto.Obj2(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.subval1_ = subval1_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.subval2_ = subval2_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder clone() {
        return (Builder) super.clone();
      }
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.setField(field, value);
      }
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof DemoProto.Obj2) {
          return mergeFrom((DemoProto.Obj2)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(DemoProto.Obj2 other) {
        if (other == DemoProto.Obj2.getDefaultInstance()) return this;
        if (other.hasSubval1()) {
          bitField0_ |= 0x00000001;
          subval1_ = other.subval1_;
          onChanged();
        }
        if (other.hasSubval2()) {
          setSubval2(other.getSubval2());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        if (!hasSubval1()) {
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        DemoProto.Obj2 parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (DemoProto.Obj2) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private Object subval1_ = "";
      /**
       * <code>required string subval1 = 1;</code>
       */
      public boolean hasSubval1() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required string subval1 = 1;</code>
       */
      public String getSubval1() {
        Object ref = subval1_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            subval1_ = s;
          }
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>required string subval1 = 1;</code>
       */
      public com.google.protobuf.ByteString
          getSubval1Bytes() {
        Object ref = subval1_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b =
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          subval1_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string subval1 = 1;</code>
       */
      public Builder setSubval1(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        subval1_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string subval1 = 1;</code>
       */
      public Builder clearSubval1() {
        bitField0_ = (bitField0_ & ~0x00000001);
        subval1_ = getDefaultInstance().getSubval1();
        onChanged();
        return this;
      }
      /**
       * <code>required string subval1 = 1;</code>
       */
      public Builder setSubval1Bytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        subval1_ = value;
        onChanged();
        return this;
      }

      private double subval2_ ;
      /**
       * <code>optional double subval2 = 2;</code>
       */
      public boolean hasSubval2() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>optional double subval2 = 2;</code>
       */
      public double getSubval2() {
        return subval2_;
      }
      /**
       * <code>optional double subval2 = 2;</code>
       */
      public Builder setSubval2(double value) {
        bitField0_ |= 0x00000002;
        subval2_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional double subval2 = 2;</code>
       */
      public Builder clearSubval2() {
        bitField0_ = (bitField0_ & ~0x00000002);
        subval2_ = 0D;
        onChanged();
        return this;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:Obj2)
    }

    // @@protoc_insertion_point(class_scope:Obj2)
    private static final DemoProto.Obj2 DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new DemoProto.Obj2();
    }

    public static DemoProto.Obj2 getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    @Deprecated public static final com.google.protobuf.Parser<Obj2>
        PARSER = new com.google.protobuf.AbstractParser<Obj2>() {
      public Obj2 parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new Obj2(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Obj2> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<Obj2> getParserForType() {
      return PARSER;
    }

    public DemoProto.Obj2 getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_Obj1_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_Obj1_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_Obj2_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_Obj2_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\ndemo.proto\")\n\004Obj1\022\014\n\004val1\030\001 \002(\005\022\023\n\004va" +
      "l2\030\002 \003(\0132\005.Obj2\"(\n\004Obj2\022\017\n\007subval1\030\001 \002(\t" +
      "\022\017\n\007subval2\030\002 \001(\001B\013B\tDemoProto"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_Obj1_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_Obj1_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_Obj1_descriptor,
        new String[] { "Val1", "Val2", });
    internal_static_Obj2_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_Obj2_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_Obj2_descriptor,
        new String[] { "Subval1", "Subval2", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}

```
   

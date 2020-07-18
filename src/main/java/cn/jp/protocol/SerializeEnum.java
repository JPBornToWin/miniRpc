package cn.jp.protocol;

public enum SerializeEnum {
    JSON((byte) 0, "json"), ProtoBuf((byte)1, "protobuf");
    public byte type;
    public String name;
    private SerializeEnum(byte type, String name) {
        this.type = type;
        this.name = name;
    }
}

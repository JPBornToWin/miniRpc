package cn.jp.protocol;

import lombok.Data;

public class ProtocolPackage {
    private  int MagicNumber;

    private  byte version;

    private  byte serializeAlgorithm;

    private  byte tip;

    public int getMagicNumber() {
        return MagicNumber;
    }

    public void setMagicNumber(int magicNumber) {
        MagicNumber = magicNumber;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public byte getSerializeAlgorithm() {
        return serializeAlgorithm;
    }

    public void setSerializeAlgorithm(byte serializeAlgorithm) {
        this.serializeAlgorithm = serializeAlgorithm;
    }

    public byte getTip() {
        return tip;
    }

    public void setTip(byte tip) {
        this.tip = tip;
    }
}

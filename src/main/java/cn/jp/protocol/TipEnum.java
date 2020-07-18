package cn.jp.protocol;

public enum TipEnum {
    REQUEST(0), RESPONSE(1), IDLE_CHECK(-1);
    int tip;

    private TipEnum(int tip) {
        this.tip = tip;
    }
}

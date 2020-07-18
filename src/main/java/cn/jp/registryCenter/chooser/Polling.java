package cn.jp.registryCenter.chooser;

import java.util.concurrent.atomic.AtomicLong;

public class Polling implements Chooser {
    private AtomicLong atomicLong = new AtomicLong(0);

    @Override
    public int choose(int total) {
        return (int)(atomicLong.incrementAndGet() % total);
    }
}

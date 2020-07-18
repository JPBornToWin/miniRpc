package cn.jp.client;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FutureRpc implements Future<Object> {

    private volatile Object result;

    private String requestId;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    private Lock lock = new ReentrantLock();

    private Condition condition = lock.newCondition();
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDone() {
        return result != null;
    }

    public void done(Object result) {
        if (this.result != null) {
            return;
        }
        this.result = result;
        lock.lock();
        try {
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Object get() {
        if (result != null) {
            return result;
        }

        lock.lock();
        try {
            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }


        return result;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) {
        if (result != null) {
            return result;
        }

        lock.lock();
        try {
            condition.await(timeout, unit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }


        return result;
    }

}

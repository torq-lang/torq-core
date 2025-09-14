package org.torqlang.server;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

import java.util.LinkedList;

public interface ApiBouncer {

    static ApiBouncer create(int maxActive) {
        return new ApiBouncerImpl(maxActive, Math.multiplyExact(maxActive, 10));
    }

    static ApiBouncer create(int maxActive, int maxWaiting) {
        return new ApiBouncerImpl(maxActive, maxWaiting);
    }

    int activeCount();

    ApiCall enter(Request request, Response response, Callback callback);

    ApiCall exit();

    int maxActive();

    int maxCapacity();

    int maxWaiting();

    int waitingCount();
}

final class ApiBouncerImpl implements ApiBouncer {

    private final int maxActive;
    private final int maxWaiting;
    private final int maxCapacity;

    private final Object lock = new Object();
    private final LinkedList<ApiCall> waitingQueue = new LinkedList<>();

    private int activeCount;
    private int waitingCount;

    ApiBouncerImpl(int maxActive, int maxWaiting) {
        if (maxActive <= 0 || maxWaiting < 0) {
            throw new IllegalArgumentException("maxActive must be positive, maxWaiting must be non-negative");
        }
        this.maxActive = maxActive;
        this.maxWaiting = maxWaiting;
        this.maxCapacity = Math.addExact(maxActive, maxWaiting);
    }

    @Override
    public final int activeCount() {
        synchronized (lock) {
            return activeCount;
        }
    }

    @Override
    public final ApiCall enter(final Request request, final Response response, final Callback callback) {
        synchronized (lock) {
            if (activeCount < maxActive) {
                if (waitingCount > 0) {
                    ApiCall next = waitingQueue.poll();
                    waitingQueue.add(new ApiCall(request, response, callback));
                    return next;
                } else {
                    ++activeCount;
                    return new ApiCall(request, response, callback);
                }
            } else {
                if (waitingCount < maxWaiting) {
                    waitingQueue.add(new ApiCall(request, response, callback));
                    waitingCount++;
                    return null;
                } else {
                    throw new TooManyRequestsError();
                }
            }
        }
    }

    @Override
    public final ApiCall exit() {
        synchronized (lock) {
            if (waitingCount > 0) {
                ApiCall next = waitingQueue.poll();
                if (next == null) {
                    throw new ApiIntegrityError("Waiting count is not zero and waiting queue is empty");
                }
                waitingCount--;
                return next;
            } else {
                if (activeCount <= 0) {
                    throw new ApiIntegrityError("exit() called with no active calls");
                }
                activeCount--;
                return null;
            }
        }
    }

    @Override
    public final int maxActive() {
        return maxActive;
    }

    @Override
    public final int maxCapacity() {
        return maxCapacity;
    }

    @Override
    public final int maxWaiting() {
        return maxWaiting;
    }

    @Override
    public final int waitingCount() {
        synchronized (lock) {
            return waitingCount;
        }
    }

}
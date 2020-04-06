package cn.zhiu.framework.base.api.core.compoment.kakfa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class CircuitBreaker {
    private static final Logger logger = LoggerFactory.getLogger(CircuitBreaker.class);
    /**
     * 熔断器名称
     */
    private String name;
    /**
     * 熔断器状态
     */
    private CircuitBreakerState state;
    /**
     * 失败次数阀值
     */
    private int failureThreshold;
    /**
     * 熔断状态时间窗口
     */
    private long timeout;
    /**
     * 失败次数
     */
    private AtomicInteger failureCount;
    /**
     * 成功次数 （并发不准确）
     */
    private int successCount;
    /**
     * 半开时间窗口里连续成功的次数
     */
    private AtomicInteger consecutiveSuccessCount;
    /**
     * 半开时间窗口里连续成功的次数阀值
     */
    private int consecutiveSuccessThreshold;

    public CircuitBreaker(String name, int failureThreshold, int consecutiveSuccessThreshold, long timeout) {
        if (failureThreshold <= 0) {
            failureThreshold = 1;
        }
        if (consecutiveSuccessThreshold <= 0) {
            consecutiveSuccessThreshold = 1;
        }
        if (timeout <= 0) {
            timeout = 10000;
        }
        this.name = name;
        this.failureThreshold = failureThreshold;
        this.consecutiveSuccessThreshold = consecutiveSuccessThreshold;
        this.timeout = timeout;
        this.failureCount = new AtomicInteger(0);
        this.consecutiveSuccessCount = new AtomicInteger(0);
        state = new CloseCircuitBreakerState(this);
    }

    public void increaseFailureCount() {
        failureCount.addAndGet(1);
    }

    public void increaseSuccessCount() {
        successCount++;
    }

    public void increaseConsecutiveSuccessCount() {
        consecutiveSuccessCount.addAndGet(1);
    }

    public boolean increaseFailureCountAndThresholdReached() {
        return failureCount.addAndGet(1) >= failureThreshold;
    }

    public boolean increaseConsecutiveSuccessCountAndThresholdReached() {
        return consecutiveSuccessCount.addAndGet(1) >= consecutiveSuccessThreshold;
    }

    public boolean isNotOpen() {
        return !isOpen();
    }

    /**
     * 熔断开启 关闭保护方法的调用 * @return
     */
    public boolean isOpen() {
        return state instanceof OpenCircuitBreakerState;
    }

    /**
     * 熔断关闭 保护方法正常执行
     *
     * @return
     */
    public boolean isClose() {
        return state instanceof CloseCircuitBreakerState;
    }

    /**
     * 熔断半开 保护方法允许测试调用
     *
     * @return
     */
    public boolean isHalfClose() {
        return state instanceof HalfOpenCircuitBreakerState;
    }

    public void transformToCloseState() {
        state = new CloseCircuitBreakerState(this);
    }

    public void transformToHalfOpenState() {
        state = new HalfOpenCircuitBreakerState(this);
    }

    public void transformToOpenState() {
        state = new OpenCircuitBreakerState(this);
    }

    /**
     * 重置失败次数
     */
    public void resetFailureCount() {
        failureCount.set(0);
    }

    /**
     * 重置连续成功次数
     */
    public void resetConsecutiveSuccessCount() {
        consecutiveSuccessCount.set(0);
    }

    public long getTimeout() {
        return timeout;
    }

    /**
     * 判断是否到达失败阀值 * @return
     */
    protected boolean failureThresholdReached() {
        return failureCount.get() >= failureThreshold;
    }

    /**
     * 判断连续成功次数是否达到阀值 * @return
     */
    protected boolean consecutiveSuccessThresholdReached() {
        return consecutiveSuccessCount.get() >= consecutiveSuccessThreshold;
    }

    /**
     * 保护方法失败后操作
     */
    public void actFailed() {
        state.actFailed();
    }

    /**
     * 保护方法成功后操作
     */
    public void actSuccess() {
        state.actSuccess();
    }

}


package cn.zhiu.framework.base.api.core.compoment.kakfa;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public interface CircuitBreakerState {
    /**
     * 保护方法失败后操作
     */
    void actFailed();

    /**
     * 保护方法成功后操作
     */
    void actSuccess();
}

abstract class AbstractCircuitBreakerState implements CircuitBreakerState {
    protected CircuitBreaker circuitBreaker;

    public AbstractCircuitBreakerState(CircuitBreaker circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public void actFailed() {
        circuitBreaker.increaseFailureCount();
    }

    @Override
    public void actSuccess() {
        circuitBreaker.increaseSuccessCount();
    }
}

class CloseCircuitBreakerState extends AbstractCircuitBreakerState {
    public CloseCircuitBreakerState(CircuitBreaker circuitBreaker) {
        super(circuitBreaker);
        circuitBreaker.resetFailureCount();
        circuitBreaker.resetConsecutiveSuccessCount();
    }

    @Override
    public void actFailed() {
        // 进入开启状态
        if (circuitBreaker.increaseFailureCountAndThresholdReached()) {
            circuitBreaker.transformToOpenState();
        }
    }
}

class HalfOpenCircuitBreakerState extends AbstractCircuitBreakerState {
    public HalfOpenCircuitBreakerState(CircuitBreaker circuitBreaker) {
        super(circuitBreaker);
        circuitBreaker.resetConsecutiveSuccessCount();
    }

    @Override
    public void actFailed() {
        super.actFailed();
        circuitBreaker.transformToOpenState();
    }

    @Override
    public void actSuccess() {
        // 达到成功次数的阀值 关闭熔断
        if (circuitBreaker.increaseConsecutiveSuccessCountAndThresholdReached()) {
            circuitBreaker.transformToCloseState();
        }
    }
}

class OpenCircuitBreakerState extends AbstractCircuitBreakerState {
    public OpenCircuitBreakerState(CircuitBreaker circuitBreaker) {
        super(circuitBreaker);
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                circuitBreaker.transformToHalfOpenState();
                timer.cancel();
            }
        }, circuitBreaker.getTimeout());
    }
}

/**
 * @desc 熔断器工厂 集中应用中的CircuitBreaker
 * 注意：这里一个熔断器一旦生产，生命周期和应用一样，不会被清除
 */
class CircuitBreakerFactory {

    /**
     * 用来存储熔断器的map集合 通过工程模式直接获取
     */
    private static ConcurrentHashMap<String, CircuitBreaker> circuitBreakerMap = new ConcurrentHashMap();

    public CircuitBreaker getCircuitBreaker(String name) {
        CircuitBreaker circuitBreaker = circuitBreakerMap.get(name);
        return circuitBreaker;
    }

    /**
     * @param name                        唯一名称
     * @param failureThreshold            失败次数阀值
     * @param consecutiveSuccessThreshold 时间窗内成功次数阀值
     * @param timeout                     时间窗
     *                                    1.close状态时 失败次数>=failureThreshold，进入open状态
     *                                    2.open状态时每隔timeout时间会进入halfOpen状态
     *                                    3.halfOpen状态里需要连续成功次数达到consecutiveSuccessThreshold，
     *                                    4.即可进入close状态，出现失败则继续进入open状态
     * @return
     */
    public static CircuitBreaker buildCircuitBreaker(String name, int failureThreshold, int consecutiveSuccessThreshold, long timeout) {
        CircuitBreaker circuitBreaker = new CircuitBreaker(name, failureThreshold, consecutiveSuccessThreshold, timeout);
        circuitBreakerMap.put(name, circuitBreaker);
        return circuitBreaker;
    }
}

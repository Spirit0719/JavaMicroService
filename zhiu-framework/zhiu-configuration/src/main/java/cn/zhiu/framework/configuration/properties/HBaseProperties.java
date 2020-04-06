package cn.zhiu.framework.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * The type H base properties.
 *
 * @author zhuzz
 * @time 2019 /05/13 15:25:26
 */
@Component(value = "hbaseProperties")
@ConfigurationProperties(prefix = "hbase")
public class HBaseProperties {


    private String zkQuorum;
    private String zkPort;

    public String getZkQuorum() {
        return zkQuorum;
    }

    public void setZkQuorum(String zkQuorum) {
        this.zkQuorum = zkQuorum;
    }

    public String getZkPort() {
        return zkPort;
    }

    public void setZkPort(String zkPort) {
        this.zkPort = zkPort;
    }
}

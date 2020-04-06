package cn.zhiu.framework.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * The type Elasticsearch properties.
 *
 * @author zhuzz
 * @time 2019 /04/26 10:48:43
 */
@Component(value = "elasticsearchProperties")
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticsearchProperties {

    private String clusterNodes = "127.0.0.1:9300";

    private String clusterName = "elasticsearch";

    private Boolean clientTransportSniff = true;

    private Boolean clientIgnoreClusterName = Boolean.FALSE;

    private String clientPingTimeout = "5s";

    private String clientNodesSamplerInterval = "5s";

    public String getClusterNodes() {
        return clusterNodes;
    }

    public void setClusterNodes(String clusterNodes) {
        this.clusterNodes = clusterNodes;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public Boolean getClientTransportSniff() {
        return clientTransportSniff;
    }

    public void setClientTransportSniff(Boolean clientTransportSniff) {
        this.clientTransportSniff = clientTransportSniff;
    }

    public Boolean getClientIgnoreClusterName() {
        return clientIgnoreClusterName;
    }

    public void setClientIgnoreClusterName(Boolean clientIgnoreClusterName) {
        this.clientIgnoreClusterName = clientIgnoreClusterName;
    }

    public String getClientPingTimeout() {
        return clientPingTimeout;
    }

    public void setClientPingTimeout(String clientPingTimeout) {
        this.clientPingTimeout = clientPingTimeout;
    }

    public String getClientNodesSamplerInterval() {
        return clientNodesSamplerInterval;
    }

    public void setClientNodesSamplerInterval(String clientNodesSamplerInterval) {
        this.clientNodesSamplerInterval = clientNodesSamplerInterval;
    }
}

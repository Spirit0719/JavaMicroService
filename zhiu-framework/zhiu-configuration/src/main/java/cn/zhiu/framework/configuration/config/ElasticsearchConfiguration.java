package cn.zhiu.framework.configuration.config;

import cn.zhiu.framework.configuration.properties.ElasticsearchProperties;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.util.Assert;

import java.net.InetAddress;

import static org.apache.commons.lang3.StringUtils.*;


@Configuration
@ConditionalOnClass({Client.class, ElasticsearchTemplate.class})
@EnableConfigurationProperties(ElasticsearchProperties.class)
@ConditionalOnExpression(value = "${elasticsearch.enable:false}")
public class ElasticsearchConfiguration {

    @Autowired
    private ElasticsearchProperties elasticsearchProperties;

    private static Logger logger = LoggerFactory.getLogger(ElasticsearchConfiguration.class);

    static final String COLON = ":";
    static final String COMMA = ",";

    @Bean
    public TransportClient client() throws Exception {

        TransportClient client = new PreBuiltTransportClient(settings());
        Assert.hasText(elasticsearchProperties.getClusterNodes(), "[Assertion failed] clusterNodes settings missing.");
        for (String clusterNode : split(elasticsearchProperties.getClusterNodes(), COMMA)) {
            String hostName = substringBeforeLast(clusterNode, COLON);
            String port = substringAfterLast(clusterNode, COLON);
            Assert.hasText(hostName, "[Assertion failed] missing host name in 'clusterNodes'");
            Assert.hasText(port, "[Assertion failed] missing port in 'clusterNodes'");
            logger.info("adding transport node : " + clusterNode);
            client.addTransportAddress(new TransportAddress(InetAddress.getByName(hostName), Integer.valueOf(port)));
        }
        client.connectedNodes();
        return client;
    }

    private Settings settings() {
        return Settings.builder()
                .put("cluster.name", elasticsearchProperties.getClusterName())
                .put("client.transport.sniff", elasticsearchProperties.getClientTransportSniff())
                .put("client.transport.ignore_cluster_name", elasticsearchProperties.getClientIgnoreClusterName())
                .put("client.transport.ping_timeout", elasticsearchProperties.getClientPingTimeout())
                .put("client.transport.nodes_sampler_interval", elasticsearchProperties.getClientNodesSamplerInterval())
                .build();
    }

    @Bean
    public ElasticsearchTemplate elasticsearchTemplate(TransportClient client) throws Exception {
        return new ElasticsearchTemplate(client);
    }

}

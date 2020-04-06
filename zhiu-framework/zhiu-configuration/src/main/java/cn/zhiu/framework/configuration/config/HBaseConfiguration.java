package cn.zhiu.framework.configuration.config;

import cn.zhiu.framework.configuration.properties.HBaseProperties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

import java.io.IOException;

@org.springframework.context.annotation.Configuration
@ConditionalOnExpression(value = "${hbase.enable:false}")
public class HBaseConfiguration {

    @Autowired
    private HBaseProperties hBaseProperties;

    @Bean("hadoopConf")
    public Configuration configuration() {
        Configuration hadoopConf = org.apache.hadoop.hbase.HBaseConfiguration.create();
        hadoopConf.set("hbase.zookeeper.quorum", hBaseProperties.getZkQuorum());
        hadoopConf.set("hbase.zookeeper.port", hBaseProperties.getZkPort());
        return hadoopConf;
    }

    @Bean("hbaseTemplate")
    @DependsOn("hadoopConf")
    public HbaseTemplate hbaseTemplate(Configuration hadoopConf) {
        HbaseTemplate hbaseTemplate = new HbaseTemplate();
        hbaseTemplate.setAutoFlush(true);
        hbaseTemplate.setConfiguration(hadoopConf);
        return hbaseTemplate;
    }

    @Bean("hbaseAdmin")
    @DependsOn("hadoopConf")
    public Admin admin(Configuration hadoopConf) throws IOException {
        Connection connection = ConnectionFactory.createConnection(hadoopConf);
        return connection.getAdmin();
    }


}

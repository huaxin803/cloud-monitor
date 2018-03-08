package com.jyall.config;

import com.hazelcast.config.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/** 配置hazelcast
 * @autor: Faizel
 * @date: 2017/7/12
 * @version:1.0.0
 */
@Configuration
public class HazelCastConfiguration {

    @Autowired
    private HazelCastProperties hazelCastProperties;

    /** 实例化hazelcast配置 **/
    @Bean
    public Config hazelcastConfig() {

        /** 若关闭组播 **/
        if(!hazelCastProperties.getBooleanMultiCast()) {

            Config config = new Config();

            config.setProperty("hazelcast.jmx", "true");

            /** 网络配置 **/
            NetworkConfig network = config.getNetworkConfig();

            network.setPort(hazelCastProperties.getIntPort());

            network.setPortAutoIncrement(false);

            /** 集群配置 **/
            JoinConfig join = network.getJoin();

            /** 关闭组播自发现 **/
            join.getMulticastConfig().setEnabled(hazelCastProperties.getBooleanMultiCast());

            join.getTcpIpConfig().setMembers(hazelCastProperties.getListMemebers()).setEnabled(true);

            MapConfig mapConfig = new MapConfig();

            mapConfig.setName(hazelCastProperties.getMapName()).

                    setBackupCount(hazelCastProperties.getIntMapBackUp()).setEvictionPolicy(EvictionPolicy.NONE);

            ListConfig listConfig = new ListConfig();

            listConfig.setName(hazelCastProperties.getListName()).

                    setBackupCount(hazelCastProperties.getIntListBackUp()).setMaxSize(hazelCastProperties.getIntListMaxSize());

            config.addMapConfig(mapConfig).addListConfig(listConfig);

            return config;
        }

        /** 开启组播 **/
        else {

            return new Config().setProperty("hazelcast.jmx", "true").

                addMapConfig(new MapConfig(hazelCastProperties.getMapName()).

                setBackupCount(hazelCastProperties.getIntMapBackUp()).

                setEvictionPolicy(EvictionPolicy.NONE)).

                addListConfig(new ListConfig(hazelCastProperties.getListName()).

                setBackupCount(hazelCastProperties.getIntListBackUp()).setMaxSize(hazelCastProperties.getIntListMaxSize()));
        }
    }
}

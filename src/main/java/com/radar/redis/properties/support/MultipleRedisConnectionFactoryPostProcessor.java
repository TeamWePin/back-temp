package com.radar.redis.properties.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.radar.redis.properties.HanteoRedisProperties;
import com.radar.redis.properties.RedisConstants;
import com.radar.redis.properties.template.RadarRedisTemplate;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.resource.ClientResources;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

@Slf4j
public class MultipleRedisConnectionFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    @Setter private Map<String, HanteoRedisProperties> redisServerMap;
    @Setter private ClientResources clientResources;
    @Setter private ObjectMapper objectMapper;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if(redisServerMap==null) return;

        int i = 0;
        for (String key : redisServerMap.keySet()) {
            logger.debug("Dynamic Bean Definition !!!! => Redis dataSource {} :: {}", key, redisServerMap.get(key));
            hanteoRedisSourceDefinitionBuilder(registry, key, redisServerMap.get(key), clientResources, (i == 0));
            i++;
        }
    }

    private void hanteoRedisSourceDefinitionBuilder(BeanDefinitionRegistry registry, String key, HanteoRedisProperties redisServerMeta, ClientResources clientResources, boolean isPrimary){
        final String redisSourceKey = RedisConstants.RESOURCE_PREFIX + key;
        final String redisTemplateKey = RedisConstants.TEMPLATE_PREFIX + key;

        // Factory Bean
        RedisConfiguration configuration = getRedisConfig(redisServerMeta);
        LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(clientResources, redisServerMeta);

        BeanDefinitionBuilder connectionFactoryDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(LettuceConnectionFactory.class);
        connectionFactoryDefinitionBuilder.addConstructorArgValue(configuration);
        connectionFactoryDefinitionBuilder.addConstructorArgValue(clientConfig);
        connectionFactoryDefinitionBuilder.setPrimary(isPrimary);
        registry.registerBeanDefinition(redisSourceKey, connectionFactoryDefinitionBuilder.getBeanDefinition());

        // template bean
        BeanDefinitionBuilder templateBeanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(RadarRedisTemplate.class);
        templateBeanDefinitionBuilder.addPropertyReference("connectionFactory", redisSourceKey);
        // Transactional 애노테이션 내 StubBO 사용 시 RestStub과 충돌, 별도 트랜잭션 관리 로직을 태우는 중
        templateBeanDefinitionBuilder.addPropertyValue("enableTransactionSupport", false);
        // Serializer 기본값은 StringSerializer
        // 키 및 해시키들은 대부분 반복되는 단어가 많이 없으므로, ZLIB 압축 효율이 떨어지기에 모두 일반 String으로 저장
        templateBeanDefinitionBuilder.addPropertyValue("defaultSerializer", new StringRedisSerializer());

        // 서버별로 레디스 데이터가 압축될지에 대한 여부를 결정할 수 있게 함
        // COMMENT 서버에서 압축 로직에 대한 적용의 준비가 되지 않아 만든 분기 설정
        // 압축이 되면 안되는 이유가 따로 있다면 계속 사용하고, 추후 사용처가 없어진다면 없애도 될 설정일 듯 함
        RedisSerializer<?> valueSerializer;
        if (redisServerMeta.isLegacySerializer()) {
            // 레거시 직렬화기는 압축을 지원하지 않음
            valueSerializer = new RadarJacksonRedisSerializer(objectMapper);
        } else {
            if (redisServerMeta.isCompressValue()) {
                // Value 및 Hash Value 값들은 대부분 JSON 스트링을 쓰며, 반복되는 키워드가 많은 경우가 있으므로 Zlib 압축된 byte 배열로 저장
                // 압축 효율이 최대 95% 가까이 됨
                valueSerializer = new ZlibStringRedisSerializer();
            } else {
                valueSerializer = new StringRedisSerializer();
            }
        }

        templateBeanDefinitionBuilder.addPropertyValue("valueSerializer", valueSerializer);
        templateBeanDefinitionBuilder.addPropertyValue("hashValueSerializer", valueSerializer);

        registry.registerBeanDefinition(redisTemplateKey, templateBeanDefinitionBuilder.getBeanDefinition());

    }

    /**
     * redis standalone config
     *
     *@ param redisproperties redis configuration parameters
     * @return RedisStandaloneConfiguration
     */
    private RedisConfiguration getRedisConfig(RedisProperties redisProperties) {
        if (redisProperties.getCluster() != null) {
            RedisClusterConfiguration config = new RedisClusterConfiguration();
            config.addClusterNode(new RedisNode(redisProperties.getHost(), redisProperties.getPort()));
            config.setPassword(RedisPassword.of(redisProperties.getPassword()));
            return config;
        }

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisProperties.getHost());
        config.setPort(redisProperties.getPort());
        config.setPassword(RedisPassword.of(redisProperties.getPassword()));
        config.setDatabase(redisProperties.getDatabase());
        return config;
    }

    /**
     *Build lettucleclientconfiguration
     *
     * @param clientResources clientResources
     * @param redisProperties redisProperties
     * @return LettuceClientConfiguration
     */
    private LettuceClientConfiguration getLettuceClientConfiguration(ClientResources clientResources, HanteoRedisProperties redisProperties) {
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = createBuilder(redisProperties.getLettuce().getPool());

        if (redisProperties.getCluster() != null) {
            builder.clientOptions(ClusterClientOptions.builder().topologyRefreshOptions(ClusterTopologyRefreshOptions.builder().enablePeriodicRefresh(Duration.ofSeconds(60)).build()).build());
        }

        // ReadFrom으로 어디에서 읽어올 지 지정 가능 (무조건 슬레이브에서만/슬레이브에서 읽는걸 선호/마스터에서 읽는걸 선호 등...)
        builder.readFrom(redisProperties.getReadFromValue());

        if (redisProperties.isSsl()) {
            builder.useSsl();
        }

        if (redisProperties.getTimeout() != null) {
            builder.commandTimeout(redisProperties.getTimeout());
        } else {
            // 따로 지정된 타임아웃이 없다면, 1초로 지정함: 레디스에서 응답이 없더라도 L3에 요청이 닿게끔 하기 위한 목적
            // 커맨드당 타임아웃이므로, RAO 사용 등 레디스 1회 조회시마다 최대 1초 (한 로직 안에서 레디스 조회 횟수가 많아질수록 레디스가 죽었을 때 실행시간이 길어짐)
            builder.commandTimeout(Duration.ofSeconds(10));
        }

        if (redisProperties.getLettuce() != null) {
            RedisProperties.Lettuce lettuce = redisProperties.getLettuce();
            if (lettuce.getShutdownTimeout() != null && !lettuce.getShutdownTimeout().isZero()) {
                builder.shutdownTimeout(redisProperties.getLettuce().getShutdownTimeout());
            }
        }

        builder.clientResources(clientResources);
        return builder.build();
    }

    /**
     *Create lettucleclientconfigurationbuilder
     *
     * @param pool connection pool configuration
     * @return LettuceClientConfigurationBuilder
     */
    private LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool pool) {
        if (pool == null) {
            return LettuceClientConfiguration.builder();
        }
        return LettucePoolingClientConfiguration.builder()
                .poolConfig(getPoolConfig(pool));
    }

    /**
     * pool config
     *
     * @param properties redis parameter configuration
     * @return GenericObjectPoolConfig
     */
    private GenericObjectPoolConfig<Object> getPoolConfig(RedisProperties.Pool properties) {
        GenericObjectPoolConfig<Object> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(properties.getMaxActive());
        config.setMaxIdle(properties.getMaxIdle());
        config.setMinIdle(properties.getMinIdle());
        if (properties.getMaxWait() != null) {
            config.setMaxWaitMillis(properties.getMaxWait().toMillis());
        }
        return config;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {}

}

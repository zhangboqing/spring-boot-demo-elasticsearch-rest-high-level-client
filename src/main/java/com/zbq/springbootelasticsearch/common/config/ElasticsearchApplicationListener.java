package com.zbq.springbootelasticsearch.common.config;

import com.zbq.springbootelasticsearch.common.elasticsearch.annotation.ESDocument;
import com.zbq.springbootelasticsearch.common.exception.ElasticsearchException;
import com.zbq.springbootelasticsearch.common.elasticsearch.base.BaseElasticsearchDao;
import com.zbq.springbootelasticsearch.common.elasticsearch.base.ElasticsearchUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author zhangboqing
 * @date 2019/12/10
 */
@Component
@Slf4j
public class ElasticsearchApplicationListener implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {

    @Autowired
    protected ElasticsearchUtils elasticsearchUtils;

    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        String[] beanNames = applicationContext.getBeanNamesForType(BaseElasticsearchDao.class);
        if (beanNames != null && beanNames.length > 0) {
            for (int i = 0; i < beanNames.length; i++) {
                String beanName = beanNames[i];
                if (beanName.contains("com.zbq.springbootelasticsearch.common.elasticsearch.base.BaseElasticsearchDao")) {
                    continue;
                }
                Object bean = applicationContext.getBean(beanName);
                Class<?> targetBeanClass = bean.getClass();
                Class<?> beanClass = GenericTypeResolver.resolveTypeArgument(targetBeanClass, BaseElasticsearchDao.class);
                ESDocument esDocument = AnnotationUtils.findAnnotation(beanClass, ESDocument.class);
                if (esDocument == null) {
                    throw new ElasticsearchException("ESDocument注解未指定");
                }
                String indexName = esDocument.indexName();
                if (StringUtils.isEmpty(indexName)) {
                    throw new ElasticsearchException("indexName未指定");
                }
                int shards = esDocument.shards();
                int replicas = esDocument.replicas();

                if (shards == 0 || replicas == 0) {
                    elasticsearchUtils.createIndexRequest(indexName);
                } else {
                    elasticsearchUtils.createIndexRequest(indexName, shards, replicas);
                }
                elasticsearchUtils.putMappingRequest(indexName, beanClass);
            }
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
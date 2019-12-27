package com.zbq.springbootelasticsearch.common.config;

import com.zbq.springbootelasticsearch.dao.base.BaseElasticsearchDao;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import java.util.Map;
import java.util.Set;

/**
 * @author zhangboqing
 * @date 2019/12/18
 */
public class ElasticsearchImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, BeanFactoryAware {


    private BeanFactory beanFactory;
    private BeanNameGenerator beanNameGenerator = DefaultBeanNameGenerator.INSTANCE;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        // 扫描注解
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(ComponentScan.class.getName());
        String[] basePackages = (String[]) annotationAttributes.get("basePackages");
        if (basePackages != null && basePackages.length > 0) {
            // 扫描类
            ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry, false);
            // 指定类
            TypeFilter typeFilter = new AssignableTypeFilter(BaseElasticsearchDao.class);
            // 指定注解
            scanner.addIncludeFilter(typeFilter);
            for (String basePackage : basePackages) {
                Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(basePackage);
                beanDefinitions.forEach(beanDefinition -> {
                    String beanClassName = beanDefinition.getBeanClassName();
                    try {
                        Class<?> beanClass = Class.forName(beanClassName);
//                        ESDocument esDocument = AnnotationUtils.findAnnotation(beancClass, ESDocument.class);
//                        if (esDocument != null) {
//                            String indexName = esDocument.indexName();
//                            short shards = esDocument.shards();
//                            short replicas = esDocument.replicas();
//                        }
                        GenericBeanDefinition definition = new GenericBeanDefinition();
                        definition.setBeanClass(beanClass);
                        registry.registerBeanDefinition(beanNameGenerator.generateBeanName(beanDefinition,registry),definition);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            }
        }

    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

}

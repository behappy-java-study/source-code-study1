package org.xiaowu.xiaowu.spring.spring;

import cn.hutool.core.util.StrUtil;
import org.xiaowu.xiaowu.spring.xiaowu.util.RecursiveFileUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author 小五
 */
public class AnnotationConfigApplicationContext {

    private Class configClazz;

    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(16);

    private Map<String, Object> singleTonMap = new ConcurrentHashMap<>(16);

    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>(16);


    public AnnotationConfigApplicationContext(Class configClazz) throws Exception {
        this.configClazz = configClazz;
        /**
         * 1. 获取AppConfig上ComponentScan的value值(路径)
         * 2. 用classlocader获取路径下所有类
         * 3. 遍历所有类,判断哪些是被@Component标注(即需被注入容器)
         * 4. 判断当前bean是否是单例的,如果是,创建完放到singleTonMap中(单例池)
         */
        if (configClazz.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScan = (ComponentScan) configClazz.getDeclaredAnnotation(ComponentScan.class);
            String path = componentScan.value();
            ClassLoader classLoader = AnnotationConfigApplicationContext.class.getClassLoader();
            String filePath = StrUtil.replace(path, ".", "/");
            // 绝对路径
            URL url = classLoader.getResource(filePath);
            List<String> recursiveFiles = new ArrayList<>();
            // 递归查找所有class文件
            RecursiveFileUtils.recursiveFiles(url.getFile(),recursiveFiles);
            // 获取@Component的扫描路径
            for (String absolutePath : recursiveFiles) {
                String subPath = StrUtil.sub(absolutePath, absolutePath.indexOf(path), absolutePath.lastIndexOf(".class"));
                Class<?> aClass = classLoader.loadClass(subPath);
                // 判读是否标注@Component(是否需要被咱们"spring"管理)
                if (aClass.isAnnotationPresent(Component.class)) {
                    // 如果BeanPostProcessor与aClass表示的类或接口相同，或者是参数aClass表示的类或接口的父类，则返回true。
                    if (BeanPostProcessor.class.isAssignableFrom(aClass)){
                        // todo 此创建BeanPostProcessor的方法不严谨
                        BeanPostProcessor beanPostProcessor = (BeanPostProcessor) aClass.getDeclaredConstructor().newInstance();
                        beanPostProcessors.add(beanPostProcessor);
                    }
                    Component componentAnnotation = aClass.getDeclaredAnnotation(Component.class);
                    // value为空则取类名作为beanName
                    String beanName = StrUtil.isEmpty(componentAnnotation.value()) ? StrUtil.lowerFirst(aClass.getSimpleName()) : componentAnnotation.value();
                    BeanDefinition beanDefinition = new BeanDefinition();
                    beanDefinition.setClazz(aClass);
                    // 判断是单例还是原型
                    if (aClass.isAnnotationPresent(Scope.class)) {
                        Scope scopeAnnotation = aClass.getDeclaredAnnotation(Scope.class);
                        beanDefinition.setScope(scopeAnnotation.value());
                    } else {
                        beanDefinition.setScope(Mode.SINGLETON);
                    }
                    beanDefinitionMap.put(beanName, beanDefinition);
                }
            }
            // 判断是否需要放到单例池
            for (String beanName : beanDefinitionMap.keySet()) {
                BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
                if (beanDefinition.getScope() == Mode.SINGLETON) {
                    Object bean = createBean(beanName,beanDefinition);
                    singleTonMap.put(beanName, bean);
                }
            }
        } else {
            throw new RuntimeException("未被标注");
        }


    }

    private Object createBean(String beanName,BeanDefinition beanDefinition) {
        try {
            Class clazz = beanDefinition.getClazz();
            Object newInstance = clazz.getDeclaredConstructor().newInstance();
            // todo 遍历当前bean的所有field,并判断是byname还是bytype
            for (Field declaredField : clazz.getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(Autowired.class)) {
                    Object bean = getBean(declaredField.getName());
                    if (!Optional.ofNullable(bean).isPresent()){
                        throw new RuntimeException("未找到当前bean");
                    }
                    declaredField.setAccessible(true);
                    declaredField.set(newInstance, bean);
                }
            }

            // 初始化bean之前
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                beanPostProcessor.postProcessBeforeInitialization(newInstance,beanName);
            }
            // InitializingBean
            if (newInstance instanceof InitializingBean){
                ((InitializingBean)newInstance).afterPropertiesSet();
            }
            // 初始化bean之后
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                beanPostProcessor.postProcessAfterInitialization(newInstance,beanName);
            }
            return newInstance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition != null) {
            if (beanDefinition.getScope() == Mode.SINGLETON) {
                return singleTonMap.get(beanName);
            } else {
                return createBean(beanName,beanDefinition);
            }
        }
        throw new RuntimeException("未找到当前bean对象");
    }

}

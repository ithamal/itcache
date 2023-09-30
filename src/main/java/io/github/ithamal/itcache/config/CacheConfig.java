package io.github.ithamal.itcache.config;

import lombok.Setter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: ken.lin
 * @since: 2023-09-26 15:08
 */
@Setter
public class CacheConfig {

    private Map<String, CacheSetting> templates = new HashMap<>();

    private Map<String, CacheSetting> caches = new HashMap<>();

    private Map<String, CacheSetting> instances = new ConcurrentHashMap<>();

    public void putTemplate(String name, CacheSetting setting) {
        templates.put(name, setting);
    }

    public void putCache(String name, CacheSetting setting) {
        caches.put(name, setting);
    }

    public Collection<String> getCacheNames() {
        return instances.keySet();
    }

    public CacheSetting getCacheSetting(String name) {
        return instances.computeIfAbsent(name, _key -> {
            CacheSetting setting = caches.containsKey(name) ? caches.get(name).copy() : new CacheSetting();
            initFromTemplate(setting);
            return setting;
        });
    }

    public CacheSetting getCacheSetting(String name, String templateName) {
        return instances.computeIfAbsent(name, _key -> {
            CacheSetting setting = caches.containsKey(name) ? caches.get(name).copy() : new CacheSetting();
            setting.setTemplate(templateName);
            initFromTemplate(setting);
            return setting;
        });
    }

    private void initFromTemplate(CacheSetting setting) {
        // 进行初始化
        String templateName = setting.getTemplate() == null ? "default" : setting.getTemplate();
        CacheSetting template = templates.get(templateName);
        if (template == null) {
            throw new RuntimeException("缓存配置模板不存在:" + templateName);
        }
        setting.setTemplate(templateName);
        setting.initFromTemplate(template);
    }

}

package io.github.ithamal.itcache.config;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

/**
 * @author: ken.lin
 * @since: 2023-09-26 15:04
 */
@Getter
@Setter
public class CacheSetting {

    private String prefix;

    private Boolean allowNullValues;

    private Integer timeToLiveSeconds;

    private String keySerializer;

    private String valueSerializer;

    private Boolean updateLiveAfterAccess;

    private Integer maxElements;

    private String implClass;

    private String[] preCaches;

    private String template;


    public CacheSetting copy(){
        CacheSetting obj = new CacheSetting();
        obj.prefix = prefix;
        obj.allowNullValues = this.allowNullValues;
        obj.timeToLiveSeconds = this.timeToLiveSeconds;
        obj.keySerializer = this.keySerializer;
        obj.valueSerializer = this.valueSerializer;
        obj.updateLiveAfterAccess = this.updateLiveAfterAccess;
        obj.maxElements = this.maxElements;
        obj.implClass = this.implClass;
        obj.preCaches = this.preCaches;
        obj.template = this.template;
        return obj;
    }

    public void initFromTemplate(CacheSetting template) {
        if (this.prefix == null) {
            this.prefix = template.getPrefix();
        }
        if (this.allowNullValues == null) {
            this.allowNullValues = nullToDefault(template.getAllowNullValues(), true);
        }
        if (this.timeToLiveSeconds == null) {
            this.timeToLiveSeconds = nullToDefault(template.getTimeToLiveSeconds(), -1);
        }
        if (this.keySerializer == null) {
            this.keySerializer = nullToDefault(template.getKeySerializer(), "string");
        }
        if (this.valueSerializer == null) {
            this.valueSerializer = nullToDefault(template.getValueSerializer(), "json");
        }
        if (this.updateLiveAfterAccess == null) {
            this.updateLiveAfterAccess = nullToDefault(template.getUpdateLiveAfterAccess(), false);
        }
        if (this.maxElements == null) {
            this.maxElements = nullToDefault(template.getMaxElements(), -1);
        }
        if (this.implClass == null) {
            this.implClass = template.getImplClass();
        }
        if (this.preCaches == null && template.getPreCaches() != null) {
            this.preCaches = Arrays.stream(template.getPreCaches()).map(it -> {
                return it.startsWith("$$") ? it : "$$" + it;
            }).toArray(String[]::new);
        }
    }

    private <T> T nullToDefault(T value, T defaultVal) {
        return value == null ? defaultVal : value;
    }
}

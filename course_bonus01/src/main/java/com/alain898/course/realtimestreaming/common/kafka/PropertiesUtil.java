package com.alain898.course.realtimestreaming.common.kafka;

import java.util.Properties;

/**
 * Created by alain on 15/11/20.
 */
public class PropertiesUtil {
    /**
     *
     * @param propsArray Properties array, the later properties will cover the older ones.
     * @return
     */
    static public Properties newProperties(final Properties... propsArray){
        Properties newProps = new Properties();
        for (Properties props : propsArray){
            if (props != null) {
                for (String k : props.stringPropertyNames()) {
                    newProps.put(k, props.getProperty(k));
                }
            }
        }
        return newProps;
    }
}

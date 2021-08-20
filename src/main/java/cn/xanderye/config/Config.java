package cn.xanderye.config;

import cn.xanderye.entity.Part;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author XanderYe
 * @description:
 * @date 2021/8/19 20:36
 */
@Data
public class Config {

    private static final Config CONFIG = new Config();

    private String userDir = System.getProperty("user.dir");

    private Map<Integer, List<Part>> partTypeMap;

    private Config() {
    }

    public static Config getInstance() {
        return CONFIG;
    }
}

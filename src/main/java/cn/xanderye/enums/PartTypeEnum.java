package cn.xanderye.enums;

import lombok.Getter;

/**
 * @author XanderYe
 * @description:
 * @date 2021/8/19 20:21
 */
@Getter
public enum PartTypeEnum {
    CPU("CPU", 1),
    MAIN_BOARD("主板", 2),
    RAM("内存", 3),
    GPU("显卡", 4),
    CPU_FAN("散热器", 5),
    SSD("固态", 6),
    HDD("机械", 7),
    POWER("电源", 8),
    CASE("机箱", 8),
    FAN("风扇", 9);

    private String name;

    private int value;

    PartTypeEnum (String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static Integer getValueByName(String name) {
        for (PartTypeEnum partTypeEnum : PartTypeEnum.values()) {
            if (partTypeEnum.getName().equals(name)) {
                return partTypeEnum.getValue();
            }
        }
        return null;
    }
}

package cn.xanderye.util;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.xanderye.entity.Part;
import cn.xanderye.enums.PartTypeEnum;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XanderYe
 * @description:
 * @date 2021/8/19 20:15
 */
public class PartExcelUtil {

    public static List<Part> readExcel(String path) {
        ExcelReader reader = ExcelUtil.getReader(path);
        List<List<Object>> readAll = reader.read();
        List<Part> partList = new ArrayList<>();
        if (readAll.size() > 1) {
            for (int i = 1; i < readAll.size(); i++) {
                List<Object> data = readAll.get(i);
                Part part = new Part();
                part.setName((String) data.get(0));
                part.setType(PartTypeEnum.getValueByName((String) data.get(1)));
                part.setPrice(BigDecimal.valueOf((Double) data.get(2)));
                part.setLink((String) data.get(3));
                partList.add(part);
            }
        }
        return partList;
    }

    public static void main(String[] args) {
        String userDir = System.getProperty("user.dir");
        String path = userDir + File.separator + "parts.xlsx";
        List<Part> partList = readExcel(path);
        for (Part part : partList) {
            System.out.println(part.toString());
        }
    }
}

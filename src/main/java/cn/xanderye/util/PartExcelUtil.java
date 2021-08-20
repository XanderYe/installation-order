package cn.xanderye.util;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.xanderye.entity.Part;
import cn.xanderye.enums.PartTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author XanderYe
 * @description:
 * @date 2021/8/19 20:15
 */
@Slf4j
public class PartExcelUtil {

    public static Map<Integer, List<Part>> readExcel(String path) {
        Map<Integer, List<Part>> partTypeMap = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            int type = i + 1;
            ExcelReader reader = ExcelUtil.getReader(path, i);
            List<Map<String, Object>> readAll = reader.read(0, 1, Integer.MAX_VALUE);
            List<Part> partList = new ArrayList<>();
            for (Map<String, Object> map : readAll) {
                Part part = new Part();
                part.setName((String) map.get("名称"));
                part.setPrice(BigDecimal.valueOf((Double) map.get("价格")));
                part.setLink((String) map.get("链接"));
                part.setType(type);
                partList.add(part);
            }
            partTypeMap.put(type, partList);
            reader.close();
        }
        return partTypeMap;
    }

    public static boolean exportExcel(Map<Integer, Part> partMap, String allPrice, String path) {
        try {
            List<List<Object>> rows = new ArrayList<>();
            List<Object> headers = new ArrayList<>();
            headers.add("");
            headers.add("名称");
            headers.add("单价");
            headers.add("数量");
            headers.add("总价");
            headers.add("链接");
            rows.add(headers);
            for (int i = 1; i <= 9; i++) {
                List<Object> data = new ArrayList<>();
                String typeName = PartTypeEnum.getNameByValue(i);
                data.add(typeName);
                Part part = partMap.get(i);
                if (part == null) {
                    part = new Part();
                    part.setPrice(BigDecimal.ZERO);
                    part.setNum(0);
                    part.setTotalPrice(BigDecimal.ZERO);
                }
                data.add(part.getName());
                data.add(part.getPrice());
                data.add(part.getNum());
                data.add(part.getTotalPrice());
                data.add(part.getLink());
                rows.add(data);
            }
            List<Object> allPriceData = new ArrayList<>();
            allPriceData.add("总价");
            allPriceData.add("");
            allPriceData.add(null);
            allPriceData.add(null);
            allPriceData.add(Double.valueOf(allPrice));
            rows.add(allPriceData);

            String file = path + File.separator + "装机单.xlsx";
            ExcelWriter writer = ExcelUtil.getWriter(file);
            writer.setColumnWidth(1, 30);
            writer.setColumnWidth(5, 60);
            writer.write(rows);
            writer.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return false;
    }

    public static void main(String[] args) {
        String userDir = System.getProperty("user.dir");
        String path = userDir + File.separator + "parts.xlsx";
        Map<Integer, List<Part>> partTypeMap = readExcel(path);
        for (Map.Entry<Integer, List<Part>> partType : partTypeMap.entrySet()) {
            System.out.println(partType.getKey() + ":" + partType.getValue().toString());
        }
    }
}

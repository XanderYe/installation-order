package cn.xanderye.controller;

import cn.xanderye.config.Config;
import cn.xanderye.entity.Part;
import cn.xanderye.enums.PartTypeEnum;
import cn.xanderye.util.PartExcelUtil;
import cn.xanderye.util.StringUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author XanderYe
 * @date 2020/2/6
 */
public class MainController implements Initializable {

    private final Config config = Config.getInstance();

    @FXML
    private ComboBox cpuComboBox, mainBoardComboBox, ramComboBox, gpuComboBox, cpuFanComboBox, ssdComboBox, hddComboBox, powerComboBox, caseComboBox, fanComboBox;
    @FXML
    private TextField cpuPriceText, mainBoardPriceText, ramPriceText, gpuPriceText, cpuFanPriceText, ssdPriceText, hddPriceText, powerPriceText, casePriceText, fanPriceText;

    private final String[] partNames = new String[]{"cpu", "mainBoard", "ram", "gpu", "cpuFan", "ssd", "hdd", "power", "case", "fan"};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String path = config.getUserDir() + File.separator + "parts.xlsx";
        List<Part> partList = PartExcelUtil.readExcel(path);
        config.setPartList(partList);
        Map<Integer, List<Part>> partTypeMap = partList.stream().collect(Collectors.groupingBy(Part::getType));
        config.setPartTypeMap(partTypeMap);

        for (String partName : partNames) {
            init(partName);
        }
    }

    /**
     * 通过反射赋值
     * @param partName
     * @return void
     * @author XanderYe
     * @date 2021/8/19
     */
    private void init(String partName) {
        ComboBox comboBox = (ComboBox) getObjectByName(partName + "ComboBox");
        TextField textField = (TextField) getObjectByName(partName + "PriceText");
        String typeEnumName = StringUtil.camelToUnderline(partName).toUpperCase();
        PartTypeEnum partTypeEnum = PartTypeEnum.valueOf(typeEnumName);
        List<Part> partList = config.getPartTypeMap().get(partTypeEnum.getValue());
        comboBox.getItems().addAll(getItemList(partList));
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String price = "0";
                for (Part part : partList) {
                    if (newValue.equals(part.getName())) {
                        price = String.valueOf(part.getPrice());
                        break;
                    }
                }
                textField.setText(price);
            }
        });
    }

    /**
     * Part列表转名称列表
     * @param partList
     * @return java.util.List<java.lang.String>
     * @author XanderYe
     * @date 2021/8/19
     */
    private List<String> getItemList(List<Part> partList) {
        return Optional.ofNullable(partList).orElse(new ArrayList<>()).stream().map(Part::getName).collect(Collectors.toList());
    }

    /**
     * 根据变量名获取对象
     * @param name
     * @return java.lang.Object
     * @author XanderYe
     * @date 2021/8/19
     */
    private Object getObjectByName(String name) {
        if (name == null) {
            return null;
        }
        Object obj = null;
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                String varName = field.getName();
                if (varName.equals(name)) {
                    obj = field.get(this);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return obj;
    }
}

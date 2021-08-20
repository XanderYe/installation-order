package cn.xanderye.controller;

import cn.xanderye.config.Config;
import cn.xanderye.entity.Part;
import cn.xanderye.enums.PartTypeEnum;
import cn.xanderye.util.JavaFxUtil;
import cn.xanderye.util.PartExcelUtil;
import cn.xanderye.util.StringUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author XanderYe
 * @date 2020/2/6
 */
@Slf4j
public class MainController implements Initializable {

    private final Config config = Config.getInstance();

    private Map<Integer, Part> partMap;

    @FXML
    private ComboBox cpuComboBox, mainBoardComboBox, ramComboBox, gpuComboBox, cpuFanComboBox, ssdComboBox, hddComboBox, powerComboBox, caseComboBox, fanComboBox;
    @FXML
    private TextField cpuPriceText, mainBoardPriceText, ramPriceText, gpuPriceText, cpuFanPriceText, ssdPriceText, hddPriceText, powerPriceText, casePriceText, fanPriceText;
    @FXML
    private TextField cpuNumText, mainBoardNumText, ramNumText, gpuNumText, cpuFanNumText, ssdNumText, hddNumText, powerNumText, caseNumText, fanNumText;
    @FXML
    private TextField cpuTotalPriceText, mainBoardTotalPriceText, ramTotalPriceText, gpuTotalPriceText, cpuFanTotalPriceText, ssdTotalPriceText, hddTotalPriceText, powerTotalPriceText, caseTotalPriceText, fanTotalPriceText;
    @FXML
    private Label allPriceLabel;

    private final String[] partNames = new String[]{"cpu", "mainBoard", "ram", "gpu", "cpuFan", "ssd", "hdd", "power", "case", "fan"};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String path = config.getUserDir() + File.separator + "parts.xlsx";
        Map<Integer, List<Part>> partTypeMap = PartExcelUtil.readExcel(path);
        config.setPartTypeMap(partTypeMap);
        partMap = new HashMap<>();

        for (String partName : partNames) {
            init(partName);
        }
    }

    /**
     * 导出装机单
     * @param
     * @return void
     * @author XanderYe
     * @date 2021/8/20
     */
    public void export() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("请选择保存位置");
        directoryChooser.setInitialDirectory(new File(config.getUserDir()));
        File dir = directoryChooser.showDialog(new Stage());
        boolean exportRes = PartExcelUtil.exportExcel(partMap, allPriceLabel.getText(), dir.getAbsolutePath());
        if (exportRes) {
            JavaFxUtil.alertDialog("提示", "导出成功");
        } else {
            JavaFxUtil.errorDialog("错误", "导出失败");
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
        TextField priceText = (TextField) getObjectByName(partName + "PriceText");
        TextField numText = (TextField) getObjectByName(partName + "NumText");
        TextField totalPriceText = (TextField) getObjectByName(partName + "TotalPriceText");
        String typeEnumName = StringUtil.camelToUnderline(partName).toUpperCase();
        PartTypeEnum partTypeEnum = PartTypeEnum.valueOf(typeEnumName);
        List<Part> partList = config.getPartTypeMap().get(partTypeEnum.getValue());
        comboBox.getItems().addAll(getItemList(partList));
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                BigDecimal price = BigDecimal.valueOf(0);
                for (Part part : partList) {
                    if (newValue.equals(part.getName())) {
                        price = part.getPrice();
                        partMap.put(part.getType(), part);
                        break;
                    }
                }
                int num = Integer.parseInt(numText.getText());
                BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(num));
                priceText.setText(price.toString());
                totalPriceText.setText(price.toString());
                totalPriceText.setText(totalPrice.toString());
            }
            calculateAllPrice();
        });
        priceText.textProperty().addListener((observable, oldValue, newValue) -> {
            calculateTotalPrice(partName, priceText, numText, totalPriceText);
        });
        numText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int num = 0;
                try {
                    num = Integer.parseInt(newValue);
                } catch (NumberFormatException ignored) { }
                numText.setText(String.valueOf(num));
                calculateTotalPrice(partName, priceText, numText, totalPriceText);
            }
        });
    }

    /**
     * 计算总价格
     * @param priceText
     * @param numText
     * @param totalPriceText
     * @return void
     * @author XanderYe
     * @date 2021/8/20
     */
    private void calculateTotalPrice(String partName, TextField priceText, TextField numText, TextField totalPriceText) {
        BigDecimal price = BigDecimal.valueOf(Double.parseDouble(priceText.getText()));
        int num = Integer.parseInt(numText.getText());
        BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(num));
        totalPriceText.setText(totalPrice.toString());
        String typeEnumName = StringUtil.camelToUnderline(partName).toUpperCase();
        PartTypeEnum partTypeEnum = PartTypeEnum.valueOf(typeEnumName);
        Part part = partMap.get(partTypeEnum.getValue());
        part.setNum(num);
        part.setTotalPrice(totalPrice);
        calculateAllPrice();
    }

    /**
     * 计算所有的价格
     * @param
     * @return void
     * @author XanderYe
     * @date 2021/8/20
     */
    private void calculateAllPrice() {
        BigDecimal allPrice = new BigDecimal(0);
        for (String partName : partNames) {
            TextField totalPriceText = (TextField) getObjectByName(partName + "TotalPriceText");
            BigDecimal totalPrice = BigDecimal.valueOf(Double.parseDouble(totalPriceText.getText()));
            allPrice = allPrice.add(totalPrice);
        }
        allPriceLabel.setText(allPrice.toString());
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
            log.error(e.getMessage());
        }
        return obj;
    }
}

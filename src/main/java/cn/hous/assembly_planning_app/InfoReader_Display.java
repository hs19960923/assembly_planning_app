package cn.hous.assembly_planning_app;

/**
 * @author housheng
 * @create 2023-08-30 2:13
 */
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InfoReader_Display extends Application {

    private List<Triplet> partInfoList = new ArrayList<>();
    private List<Triplet> assemblyInfoList = new ArrayList<>();
    private List<Triplet> interferenceInfoList = new ArrayList<>();
    private Text infoText = new Text();

    @Override
    public void start(Stage primaryStage) {
        VBox vbox = new VBox();

        Button readPartInfoButton = new Button("读取零件信息");
        Button readAssemblyInfoButton = new Button("读取配合信息");
        Button readInterferenceInfoButton = new Button("读取干涉信息");

        readPartInfoButton.setOnAction(e -> {
            File file = getFileFromUser();
            if (file != null) {
                partInfoList = readExcel(file);
                displayInfo(partInfoList);
            }
        });

        readAssemblyInfoButton.setOnAction(e -> {
            File file = getFileFromUser();
            if (file != null) {
                assemblyInfoList = readExcel(file);
                displayInfo(assemblyInfoList);
            }
        });

        readInterferenceInfoButton.setOnAction(e -> {
            File file = getFileFromUser();
            if (file != null) {
                interferenceInfoList = readExcel(file);
                displayInfo(interferenceInfoList);
            }
        });

        vbox.getChildren().addAll(readPartInfoButton, readAssemblyInfoButton, readInterferenceInfoButton, infoText);
        Scene scene = new Scene(vbox, 400, 400);

        primaryStage.setTitle("装配规划");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private File getFileFromUser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"));
        return fileChooser.showOpenDialog(null);
    }

    private List<Triplet> readExcel(File file) {
        List<Triplet> dataList = new ArrayList<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // 遍历表格的每一行
            for (Row row : sheet) {
                if (row.getLastCellNum() >= 3) { // 保证每行至少有三列数据
                    Cell cell1 = row.getCell(0);
                    Cell cell2 = row.getCell(1);
                    Cell cell3 = row.getCell(2);
                    if (cell1 != null && cell2 != null && cell3 != null) { // 确保单元格不为空
                        Triplet triplet = new Triplet(cell1.toString(), cell2.toString(), cell3.toString());
                        dataList.add(triplet);
                    }
                }
            }

            workbook.close();
            fileInputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return dataList;
    }

    private void displayInfo(List<Triplet> dataList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Triplet triplet : dataList) {
            stringBuilder.append(triplet.toString()).append("\n");
        }
        infoText.setText(stringBuilder.toString());
    }

    public static void main(String[] args) {
        launch(args);
    }

    // 自定义三元组类
    private static class Triplet {
        private String first;
        private String second;
        private String third;

        public Triplet(String first, String second, String third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        @Override
        public String toString() {
            return "(" + first + ", " + second + ", " + third + ")";
        }
    }
}


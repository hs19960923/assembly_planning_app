package cn.hous.assembly_planning_app;

/**
 * @author housheng
 * @create 2023-08-30 1:52
 */
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Info_Reader extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox vbox = new VBox();

        Button readPartInfoButton = new Button("读取零件信息");
        Button readAssemblyInfoButton = new Button("读取配合信息");
        Button readInterferenceInfoButton = new Button("读取干涉信息");

        readPartInfoButton.setOnAction(e -> {
            File file = getFileFromUser();
            if (file != null) {
                // 处理导入的 Excel 文件
                readExcel(file);
            }
        });

        readAssemblyInfoButton.setOnAction(e -> {
            File file = getFileFromUser();
            if (file != null) {
                // 处理导入的 Excel 文件
                readExcel(file);
            }
        });

        readInterferenceInfoButton.setOnAction(e -> {
            File file = getFileFromUser();
            if (file != null) {
                // 处理导入的 Excel 文件
                readExcel(file);
            }
        });

        vbox.getChildren().addAll(readPartInfoButton, readAssemblyInfoButton, readInterferenceInfoButton);
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

    private void readExcel(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                for (Cell cell : row) {
                    System.out.print(cell.toString() + "\t");
                }
                System.out.println();
            }

            workbook.close();
            fileInputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

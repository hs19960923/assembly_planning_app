package cn.hous.assembly_planning_app;

/**
 * @author housheng
 * @create 2023-08-30 3:18
 */
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InfoReader_Display_b extends Application {

    private List<Triplet> partInfoList = new ArrayList<>();
    private List<Triplet> assemblyInfoList = new ArrayList<>();
    private List<Triplet> interferenceInfoList = new ArrayList<>();
    private Text partInfoText = new Text();
    private Text assemblyInfoText = new Text();
    private Text interferenceInfoText = new Text();

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
                displayInfo(partInfoText, partInfoList);
            }
        });

        readAssemblyInfoButton.setOnAction(e -> {
            File file = getFileFromUser();
            if (file != null) {
                assemblyInfoList = readExcel(file);
                displayInfo(assemblyInfoText, assemblyInfoList);
            }
        });

        readInterferenceInfoButton.setOnAction(e -> {
            File file = getFileFromUser();
            if (file != null) {
                interferenceInfoList = readExcel(file);
                displayInfo(interferenceInfoText, interferenceInfoList);
            }
        });

        // 创建导出按钮
        Button exportPartButton = new Button("导出为零件信息.ttl");
        exportPartButton.setOnAction(e -> exportToTurtleFile(partInfoList, "零件信息"));

        Button exportAssemblyButton = new Button("导出为配合信息.ttl");
        exportAssemblyButton.setOnAction(e -> exportToTurtleFile(assemblyInfoList, "配合信息"));

        Button exportInterferenceButton = new Button("导出为干涉信息.ttl");
        exportInterferenceButton.setOnAction(e -> exportToTurtleFile(interferenceInfoList, "干涉信息"));

        // 创建方框和文本
        StackPane partInfoPane = new StackPane();
        VBox partInfoVBox = new VBox(partInfoText);
        HBox partInfoButtonBox = new HBox(exportPartButton);
        partInfoVBox.setAlignment(Pos.CENTER_LEFT);
        partInfoButtonBox.setAlignment(Pos.BOTTOM_RIGHT);
        partInfoPane.getChildren().addAll(partInfoVBox, partInfoButtonBox);

        StackPane assemblyInfoPane = new StackPane();
        VBox assemblyInfoVBox = new VBox(assemblyInfoText);
        HBox assemblyInfoButtonBox = new HBox(exportAssemblyButton);
        assemblyInfoVBox.setAlignment(Pos.CENTER_LEFT);
        assemblyInfoButtonBox.setAlignment(Pos.BOTTOM_RIGHT);
        assemblyInfoPane.getChildren().addAll(assemblyInfoVBox, assemblyInfoButtonBox);

        StackPane interferenceInfoPane = new StackPane();
        VBox interferenceInfoVBox = new VBox(interferenceInfoText);
        HBox interferenceInfoButtonBox = new HBox(exportInterferenceButton);
        interferenceInfoVBox.setAlignment(Pos.CENTER_LEFT);
        interferenceInfoButtonBox.setAlignment(Pos.BOTTOM_RIGHT);
        interferenceInfoPane.getChildren().addAll(interferenceInfoVBox, interferenceInfoButtonBox);

        partInfoPane.setStyle("-fx-border-color: black; -fx-padding: 10px;");
        assemblyInfoPane.setStyle("-fx-border-color: black; -fx-padding: 10px;");
        interferenceInfoPane.setStyle("-fx-border-color: black; -fx-padding: 10px;");


        // 将按钮和方框添加到布局
        HBox buttonBox = new HBox(readPartInfoButton, readAssemblyInfoButton, readInterferenceInfoButton);
        vbox.getChildren().addAll(buttonBox, new Text("零件信息"), partInfoPane, new Text("配合信息"), assemblyInfoPane, new Text("干涉信息"), interferenceInfoPane);
        Scene scene = new Scene(vbox, 800, 600);

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

            for (Row row : sheet) {
                if (row.getLastCellNum() >= 3) {
                    Cell cell1 = row.getCell(0);
                    Cell cell2 = row.getCell(1);
                    Cell cell3 = row.getCell(2);
                    if (cell1 != null && cell2 != null && cell3 != null) {
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

    private void displayInfo(Text infoText, List<Triplet> dataList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Triplet triplet : dataList) {
            stringBuilder.append(triplet.toString()).append("\n");
        }
        infoText.setText(stringBuilder.toString());
    }

    private void exportToTurtleFile(List<Triplet> dataList, String className) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择保存目录");
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            Model model = ModelFactory.createDefaultModel();
            String baseURI = "http://example.org/";

            for (Triplet triplet : dataList) {
                Resource partResource = model.createResource(baseURI + triplet.getFirst())
                        .addProperty(RDF.type, ResourceFactory.createResource(baseURI + className))
                        .addProperty(ResourceFactory.createProperty(baseURI + "hasName"), triplet.getFirst())
                        .addProperty(ResourceFactory.createProperty(baseURI + "hasPart"), triplet.getSecond())
                        .addProperty(ResourceFactory.createProperty(baseURI + "hasInterference"), triplet.getThird());
                // ... (类似地处理其他信息)
            }

            String filename = selectedDirectory.getAbsolutePath() + File.separator + className + ".ttl";
            try {
                FileOutputStream output = new FileOutputStream(filename);
                model.write(output, "TURTLE");
                output.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class Triplet {
        private String first;
        private String second;
        private String third;

        public Triplet(String first, String second, String third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public String getFirst() {
            return first;
        }

        public String getSecond() {
            return second;
        }

        public String getThird() {
            return third;
        }
        @Override
        public String toString() {
            return "(" + first + ", " + second + ", " + third + ")";
        }
    }
}






module cn.hous.assembly_planning_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.ooxml;
    requires org.apache.jena.core;


    opens cn.hous.assembly_planning_app to javafx.fxml;
    exports cn.hous.assembly_planning_app;
}
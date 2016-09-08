/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CSVboard;

import CSVboard.replace.ReplaceController;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Battadmin
 */
public class CSVmanager extends Application {
    private static ObservableList<CsvData> data = FXCollections.observableArrayList(); 
    private static String [] loadedText;
    private static int col;
    private Stage stage;
    private static String fileName;
    private static String [] headerName;
    private static double [] resetWidthValues;
    public static double [] widthValues;
    private static int [] colOrder;
    private static Stage dialogStage;
    
    public CSVmanager() {} //constructor
    
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        this.stage.setTitle("CSVboard");
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("CSVboard/style_tableview.css");
        // Dropping over surface
        scene.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                String filePath = null;
                for (File file:db.getFiles()) {
                    filePath = file.getAbsolutePath();
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
        // Give the controller access to the main app.                
        stage.setScene(scene);
        stage.show();        
    }

    public boolean showCsvDataEditDialog(CsvData csvData) {
    try {
        // Load the fxml file and create a new stage for the popup dialog.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(CSVmanager.class.getResource("replace/Replace.fxml"));
        AnchorPane page = (AnchorPane) loader.load();

        // Create the dialog Stage.
        dialogStage = new Stage();
        dialogStage.setTitle("Edit Data");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(stage);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        // Set the person into the controller.
        ReplaceController controller = loader.getController();
        controller.setDialogStage(dialogStage);
//        controller.setPerson(csvData);

        // Show the dialog and wait until the user closes it
        dialogStage.showAndWait();
        
        return true; //controller.isOkClicked();
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }
}    

    public boolean showInfo() {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(CSVmanager.class.getResource("Info.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            dialogStage = new Stage();
            dialogStage.setTitle("About CSVboard");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(stage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            InfoController controller = loader.getController();
            controller.setDialogStage(dialogStage);
    //        controller.setPerson(csvData);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return true; //controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }    
    
    public static Stage getStage() {
        return dialogStage;
    }
    
    public void resetWidthValues(double [] maxWidthValues, int colNum) {
        CSVmanager.resetWidthValues = new double[colNum];
        CSVmanager.resetWidthValues=maxWidthValues;
        System.arraycopy(maxWidthValues, 0, maxWidthValues, 0, CSVmanager.resetWidthValues.length);
    }
    
    public static void resetWidthValues(double [] maxWidthValues) {
        CSVmanager.resetWidthValues=maxWidthValues;
    }
    
   public void setWidthValues(double [] maxWidthValues, int colNum) {
        CSVmanager.widthValues = new double[colNum];
        CSVmanager.widthValues=maxWidthValues;
        System.arraycopy(maxWidthValues, 0, maxWidthValues, 0, CSVmanager.widthValues.length);
    }
    
    public static void setWidthValues(double [] widthValues) {
        CSVmanager.widthValues=widthValues;
    }    
    
    public static void setHeader(int [] colOrder) {
        String [] tmp = new String[colOrder.length];
        for(int i=0;i<colOrder.length;i++) {
            tmp[i]=headerName[colOrder[i]];
        }
        headerName = tmp;
    }
    
    public static void setHeader(String [] headerName) {
        CSVmanager.headerName=new String[headerName.length];
        CSVmanager.headerName=headerName;
        for(int i=0;i<headerName.length;i++) {
            if(headerName[i]==null) {
                headerName[i]="["+i+"]";
            }            
        }
    }

    public static String [] getHeader() {
        return headerName;
    }
    
    public static String [] getHeader(TableView<CsvData> table) {
        String [] tmpHeader = new String[table.getColumns().size()];
        for(int i=0;i<table.getColumns().size();i++) {
            tmpHeader[i]=table.getColumns().get(i).getText();
        }
        return tmpHeader;
    }
    
    public void setFileName(String fileName) {
        CSVmanager.fileName=fileName;
    }
    
    public void setCsvData(ObservableList<CsvData> data) {
        CSVmanager.data = data;
        IOFileOperations io = new IOFileOperations(data);
        loadedText = io.getLoadedText();
    }
   
    public static String getFileName() {
        return CSVmanager.fileName;
    }
    
    public static void setRawData(String[] changedText) {
        loadedText = changedText;
    }
    
    public static LinkedHashMap<Integer, String> getRawData() {
        LinkedHashMap<Integer, String> hm = new LinkedHashMap<>();
        for(int i=0;i<loadedText.length;i++) {
            hm.put(i, loadedText[i]);
        }
        return hm;
    }
    
    public static int getColNums(){
        return col;
    }
    
    public void setColNums(int col){
        CSVmanager.col = col;
        headerName=new String[col];
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

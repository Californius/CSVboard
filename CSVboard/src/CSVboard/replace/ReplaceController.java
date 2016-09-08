/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CSVboard.replace;

import CSVboard.CSVmanager;
import CSVboard.CSVmanager;
import CSVboard.CsvData;
import CSVboard.CsvData;
import CSVboard.FXMLDocumentController;
import CSVboard.IOFileOperations;
import CSVboard.IOFileOperations;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Battadmin
 */
public class ReplaceController implements Initializable {
    
    @FXML private ComboBox comboBox;
    @FXML private CheckBox caseCheckBox;
    @FXML private TextField findTextField;
    @FXML private TextField replaceTextField;
    @FXML private ObservableList<CsvData> data = FXCollections.observableArrayList();
    @FXML private ObservableList<String> columns = FXCollections.observableArrayList();
    @FXML private Button ok;
    @FXML private Button cancel;
          private Stage stage;
          private String find;
          private String replace;
          private TableColumn<CsvData, String> column;
          private TableView<CsvData> table;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        int numCol = CSVmanager.getColNums();
        for(int i=0;i<numCol;i++) {
            columns.add("Column "+(i));
        }
        columns.add("all columns");
        comboBox.getItems().addAll(columns);
        comboBox.getSelectionModel().selectFirst();
    }    
   
    @FXML private int getColID() {
        int tmp=0;
        tmp = comboBox.getSelectionModel().getSelectedIndex();
        return tmp;
    }
    
    @FXML private void actionReplaceDialog(ActionEvent event) throws UnsupportedEncodingException, IOException {
        int columnIdx = getColID();
        String [][] results = IOFileOperations.results;
        IOFileOperations io = new IOFileOperations();
        find = findTextField.getText();
        replace = replaceTextField.getText();        
        data = io.replace(results, find, replace, columnIdx, caseCheckBox.isSelected());   
        CSVmanager.setRawData(io.arrayConversion(results));
        IOFileOperations.table.setItems(data);
    }          
    
    @FXML private void closeWindow(ActionEvent event) {
        Stage editStage = CSVmanager.getStage();
        editStage.close();
    }
    
    public void setDialogStage(Stage stage) throws IOException {
        this.stage = stage;
    }
}

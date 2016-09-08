/**
 * Yusuf Yurdagel
 * Main controller class
 * 
 * Overview of all methods
 * actionSelectAll          -> select all rows of tables
 * convertFileXML           -> save table as XML
 * initialize               -> initialization 
 * openFile                 -> choose file and load file 
 * saveFile                 -> save table by updating the last saved version
 * saveFileAs               -> save table as a new specified file
 * enableDragDrop           -> enable Drag&Drop operations for CSVBoard
 * searchField              -> search field of CSVBoard 
 * setCustomCellFactory     -> sets background color for search queries, default color for results 'yellow'
 * setMenuItem              -> enables or disables menuItems
 * textfieldTableSize       -> display size of table [rows][columns]
 * 
 * Overview of all fields
 * copyData                 -> contents of selected data of CSVdata
 * data                     -> main data of loaded file
 * table                    -> main table of CSVBoard
 * 
**/

package CSVboard;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

/**
 *
 * @author Yusuf YURDAGEL (Germany)
 *          
 */
public class FXMLDocumentController implements Initializable {
    @FXML private TextField textFieldRows;
    @FXML private TextField textField;
    @FXML private TableView<CsvData> table;
    @FXML private static ObservableList<CsvData> data = FXCollections.observableArrayList();
    @FXML private BorderPane pane;
    @FXML private MenuItem save; 
    @FXML private MenuItem saveAs;
    @FXML private MenuItem exportXML;
    @FXML private MenuItem selectAll;
    @FXML private MenuItem replace;
    @FXML private MenuItem tutorial;
          private final ObservableList<CsvData> copyData;
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        enableDragDrop();
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        new Tools(data, table); 
        textfieldTableSize();
    }    
          
    public FXMLDocumentController() {
        this.copyData = null;
    } 
    
    /** 
     *  enable drag&drop to open file via dragging&dropping
     */
    @FXML 
    private void enableDragDrop() {
        pane.setOnDragOver((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.consume();
            }
        });
        pane.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            String filePath1 = null;
            if (db.hasFiles()) {
                success = true;
                for (File file : db.getFiles()) {
                    filePath1 = file.getAbsolutePath();
                }
            }
            event.setDropCompleted(success);
            event.consume();
            CSVmanager csvManager = new CSVmanager();
            csvManager.setFileName(filePath1); //
            try {
                openFile(filePath1);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }      
    
    @FXML private void actionEditDialog() {
        CSVmanager csvManager = new CSVmanager();
        csvManager.showCsvDataEditDialog(null);
    }    
    
    @FXML private void actionInfoDialog() {
        CSVmanager csvManager = new CSVmanager();
        csvManager.showInfo();
    }    
    
    @FXML private void actionTutorialDialog() throws IOException, URISyntaxException {
        Desktop.getDesktop().browse( new URI("www.yups-blog.de") );
        
    }
    
    
    /** 
     *  for drag&drop operations only
     */
    @FXML 
    private void openFile(String fileName) throws UnsupportedEncodingException {
        textField.setStyle("-fx-background-color:#99FFFF;-fx-font-weight: bold;-fx-font-style: italic");
        textFieldRows.setStyle("-fx-background-color:#99FFFF;-fx-font-weight: bold;-fx-font-style: italic");
        textField.setText("loading...");
        Task<ObservableList<CsvData>> task = new Task() {
        IOFileOperations io = new IOFileOperations(fileName, textField, textFieldRows);
        
            @Override
            protected Object call() throws Exception {
                CSVmanager csvManager = new CSVmanager();
                csvManager.setFileName(fileName);
                data = io.getCSVData(); //~200ms
                table.setOnKeyPressed(new TableKeyEventHandler(copyData,table,data,textField,textFieldRows));
                int colNumSize=io.getNumberOfColumns();
                Platform.runLater(() -> {
                    table.getColumns().clear();
                });
                csvManager.setColNums(colNumSize);  
                csvManager.setCsvData(data);
                Platform.runLater(() -> {
                    TableColumn<CsvData, String> column;
                    for(int i=0;i<colNumSize;i++) {
                        final int x=i; 
                        column = new TableColumn<>("["+x+"]");
                        //populate the columns with data
                        column.setCellValueFactory(cellData -> cellData.getValue().dataProperty(x));
                        column.setCellFactory(setCustomCellFactory("yellow"));
                        table.getColumns().add(column);
                    }
                    textField.setStyle("-fx-background-color:#FFFFFF");
                    textFieldRows.setStyle("-fx-background-color:#FFFFFF;");
                    textField.setText("");
                    table.setItems(data);
                    IOFileOperations.table=table;
                    textfieldTableSize();
                    Tools tool = new Tools(data, table);
                    double [] maxWidthValues = tool.getColumnWidth(table);
                    CSVmanager.setWidthValues(maxWidthValues);
                    setMenuItems(false);
                });
                return null;
            }
        };
        new Thread(task).start();
    }
    
    /** 
     *  to open file only over the menut item 
     */
    @FXML 
    private void openFile() throws FileNotFoundException, IOException { 
        IOFileOperations io = new IOFileOperations(textField,textFieldRows);
        Task<Void> task;
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                data = io.getCSVData();//heap space problem!!
                table.setOnKeyPressed(new TableKeyEventHandler(copyData,table,data, textField,textFieldRows));
                int colNumSize = io.getNumberOfColumns();
                if (data==null) {
                    table.setItems(null);
                }
                else {
                    CSVmanager csvManager = new CSVmanager();
                    csvManager.setCsvData(data);
                    Platform.runLater(() -> {
                        table.getColumns().clear();
                    });
                    csvManager.setColNums(colNumSize);
                    csvManager.setCsvData(data);
                    Platform.runLater(() -> {
                        TableColumn<CsvData, String> column;
                        for(int i=0;i<colNumSize;i++) {
                            final int x=i; //man kann jedes system ueberlisten.... 29.5.15
                            String tmp="["+Integer.toString(i+1)+"]";
                            column = new TableColumn<>(tmp);
                            column.setCellValueFactory(cellData -> cellData.getValue().dataProperty(x));
                            table.getColumns().add(column);
                        }
                        table.setItems(data);
                        IOFileOperations.table=table;
                        textField.setStyle("-fx-background-color:#FFFFFF");
                        textFieldRows.setStyle("-fx-background-color:#FFFFFF;");
                        textField.setText("");
                        setMenuItems(false);
                        Tools tool = new Tools(data, table);
                        double [] maxWidthValues = tool.getColumnWidth(table);
                        CSVmanager.setWidthValues(maxWidthValues);
//                        csvManager.resetWidthValues(maxWidthValues, table.getColumns().size());
                        textfieldTableSize();
                    });
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    
    //11.12.15: nullpointerexception wenn ein suchbegriff im textfield eingegeben wird und die Ergebnisse nicht dargestelltt werden
    //: die suchergebnisse werden nicht richtig dargestellt. 
    //Es werden stattdessen alle Werte gezeigt. 
    /**
     * 
     * @param color background color of table cell with search results
     */
    private Callback<TableColumn<CsvData, String>, TableCell<CsvData, String>> setCustomCellFactory(final String color) {
        return (TableColumn<CsvData, String> param) -> {
            TableCell<CsvData, String> cell;
            cell = new TableCell<CsvData, String>() {
                
                @Override
                public void updateItem(String item, boolean empty) {
                    setText(item);
                    setStyle("");
                    if (!textField.getText().isEmpty()) {
                        String tmpArray=textField.getText();
                        tmpArray=tmpArray.replaceAll("[-+]+", "");
                        String [] tfArray = tmpArray.split(" ");//textField.getText().split(" ");
                        if(item==null || empty) {}
                        else {
                            item=item.toLowerCase();
                            for (String elem : tfArray) {
                                if (item.contains(elem.toLowerCase().replaceAll("[\\u007b-\\u00bf\\s]+", ""))) {
                                    //setText(item);
                                    setStyle("-fx-background-color: " + color + ";");
                                }
                            }
                        }
                    }
                }
            };
            return cell;
        };
    }
    
    /**
     * display size of table
     */
    @FXML 
    private void textfieldTableSize() {
        int rows = table.getItems().size();
        int dataSizeRow=data.size();
        int dataSizeCol=table.getColumns().size();
        int selectedIDX = table.getSelectionModel().getSelectedIndex()+1;
        textFieldRows.setText(selectedIDX+"/"+rows+" - ["+dataSizeRow+"]["+dataSizeCol+"]\t");       
    }
    
    /**
     * text field for search queries
     */    
    @FXML 
    private void searchField() { 
        int [] colOrder = Tools.getColOrder(table);
//        CSVmanager.setHeader(colOrder); //13.8.16
        String [] headerName = CSVmanager.getHeader(table);
//        data = IOFileOperations.table.getItems(); //13.8.16
        /* todo 13.8.16: csvmanager.rawdata muss nach einer replace aktion aktualisiert werden.
         * 
         */
        String searchQuery = textField.getText().toLowerCase();		
        SearchEngine search = new SearchEngine();
        LinkedHashMap<Integer, String> searchResultsNotes = search.searchEvaluation(searchQuery, CSVmanager.getRawData());
        String [] searchResults = search.convert2StringArray(searchResultsNotes);
        if (searchResults!=null && searchResults.length!=0) {}
        else {
            searchResults = new String[1];
            int countCol;
            countCol = table.getColumns().size();
            for(int i=0;i<countCol;i++) {
                searchResults[0]=";"+searchResults[0]; //dummy row. Used when when no search querioes were found
            }
        }
        IOFileOperations io = new IOFileOperations(searchResults);
        table.getColumns().clear();
        data = io.getCSVData();
        table.setItems(data);
        int col = CSVmanager.getColNums();
        if (headerName==null) {
            headerName = new String[col];
            for(int i=0;i<headerName.length;i++) {
                headerName[i] = "["+i+"]";
            } 
        }
        TableColumn<CsvData, String> column;
        for(int i=0;i<col;i++) {
            final int x=i; //man kann jedes system ueberlisten.... 29.5.15
            column = new TableColumn<>(headerName[x]);
            table.getColumns().add(column);
            column.setCellFactory(setCustomCellFactory("yellow"));
            column.setCellValueFactory(cellData -> cellData.getValue().dataProperty(colOrder[x]));
            double [] maxWidthValues = CSVmanager.widthValues;
            table.getColumns().get(x).setPrefWidth(CSVmanager.widthValues[colOrder[i]]);
        }
        textfieldTableSize();  
    }      
    
    @FXML 
    private void actionSelectAll() {
        table.getSelectionModel().selectAll();
    }
    
    @FXML 
    private void saveFile() throws IOException {
        String fileName = CSVmanager.getFileName();
        IOFileOperations ioSave = new IOFileOperations(table, fileName);
        ioSave.writeFile(textField, textFieldRows);
    }

    @FXML 
    private void saveFileAs() throws IOException {
        IOFileOperations ioSave = new IOFileOperations(table);
        ioSave.writeFileAs();
    }

    @FXML 
    private void convertFileXML() throws IOException {
        IOFileOperations ioSave = new IOFileOperations(table);
        ioSave.XMLexport();
    }
    
    private void setMenuItems(boolean set) {
        save.setDisable(set);
        saveAs.setDisable(set);
        exportXML.setDisable(set);
        selectAll.setDisable(set);
        replace.setDisable(set);
    }
    
}

package CSVboard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

   /**
    * Yusuf Yurdagel
    * Copy/Paste keyboard event handler.
    * The handler uses the keyEvent's source for the clipboard data. The source must be of type TableView.
    * 
    */
   public class TableKeyEventHandler implements EventHandler<KeyEvent> { 
       
       @FXML private static final ObservableList<CsvData> data = FXCollections.observableArrayList(); //<-----
       @FXML private final TableView<CsvData> table;
             private double [] widthValues;
             private TextField textField;
             private TextField textFieldRows;
             
        public TableKeyEventHandler(ObservableList<CsvData> copyData, TableView<CsvData> table, ObservableList<CsvData> data, TextField textField, TextField textFieldRows) {
            this.copyData = copyData;
            this.table = table;
            this.textField = textField;
            this.textFieldRows = textFieldRows;
            TableKeyEventHandler.data.clear();
            TableKeyEventHandler.data.addAll(data);
        }
        
           private final KeyCodeCombination copyKeyCodeCompination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN); 
           private final KeyCodeCombination setColumnHeader = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN); 
           private final KeyCodeCombination resetColumnHeader = new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN); 
           private final KeyCodeCombination saveFile = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN); 
           private final KeyCodeCombination saveFileAs = new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN); 
           private ObservableList<CsvData> copyData = null;
           
        @Override
           public void handle(final KeyEvent keyEvent) {                    
                if (copyKeyCodeCompination.match(keyEvent)) { 
                     if( keyEvent.getSource() instanceof TableView) {				
                         copyData = copySelectionToClipboard( (TableView<CsvData>) keyEvent.getSource());	                            
                         keyEvent.consume();
                     } 
                     copyData = copySelectionToClipboard( (TableView<CsvData>) keyEvent.getSource());	                            
                     keyEvent.consume();
                } 
                if (setColumnHeader.match(keyEvent)) { 
                     if( keyEvent.getSource() instanceof TableView) {
                         String [] headerName = new String[table.getColumns().size()];
                         int idx = table.getSelectionModel().getSelectedIndex();
                         if (idx==-1) {
                             table.getSelectionModel().selectFirst();
                             idx=table.getSelectionModel().getSelectedIndex();
                         }
                         CsvData header = data.get(idx);
                         int [] colOrder = Tools.getColOrder(table);
                         Tools tools = new Tools(data, table);
                         double [] resetWidthValues=tools.getColumnWidth(table);
                         CSVmanager csvManager = new CSVmanager();
                         csvManager.resetWidthValues(resetWidthValues,colOrder.length);
                         widthValues = CSVmanager.widthValues;
                         for (int i=0;i<table.getColumns().size();i++) {
                            String tmp="";
                            if (header.getDataValue(colOrder[i], idx)==null) tmp="["+i+"]"; 
                            else tmp="["+colOrder[i]+"] "+header.getDataValue(colOrder[i], idx);
                            headerName[i]=tmp;
                            table.getColumns().get(i).setText(tmp);                             
                            table.getColumns().get(i).setPrefWidth(widthValues[colOrder[i]]);//nach dem resetten muss die spaltenbnreite wieder auf die ursprüngliche Größe angepasst werden
                        }
                     } 
                }
                if (resetColumnHeader.match(keyEvent)) { 
                     if( keyEvent.getSource() instanceof TableView) {
                         int [] colOrder = Tools.getColOrder(table);
                         for (int i=0;i<table.getColumns().size();i++) {
                            String tmp="["+colOrder[i]+"] ";
                            table.getColumns().get(i).setText(tmp);    
                           }
                     } 
                }
                if (saveFile.match(keyEvent)) { 
                     if( keyEvent.getSource() instanceof TableView) {				
                         String fileName = CSVmanager.getFileName();
                        IOFileOperations ioSave = new IOFileOperations(table, fileName);
                        ioSave.writeFile(textField, textFieldRows);
                     }
                } 
                if (saveFileAs.match(keyEvent)) { 
                     if( keyEvent.getSource() instanceof TableView) {				
                        IOFileOperations ioSave = new IOFileOperations(table);
                        ioSave.writeFileAs();
                     }
                }
        }          
           
    private ObservableList<CsvData> copySelectionToClipboard(TableView<CsvData> table) { 
            StringBuilder clipboardString = new StringBuilder(); 
            ObservableList<CsvData> tmpData = table.getSelectionModel().getSelectedItems(); //read only data
            ObservableList<CsvData> writeableData = FXCollections.<CsvData>observableArrayList(tmpData);
            int colNum = table.getColumns().size();
            ObservableList<String> columnHeader=FXCollections.observableArrayList();
            for(int i=0;i<colNum;i++) {
                columnHeader.add(table.getColumns().get(i).getText());
            }
            
            int foo;
            String str = columnHeader.get(0).replaceAll("\\D+","");
            foo = Integer.parseInt(str);
            String text;
            //**in the next step the the algorithm has to copy the specified contents to the clipboard and not paste the contents continuesly
            if (tmpData != null) {
            for (int i=0;i<tmpData.size();i++) { 
                for(int k=foo;k<table.getColumns().size();k++) {
                    text = tmpData.get(i).getDataValue(k, i); //**
                    clipboardString.append(text);
                    clipboardString.append(";");
                }   		
                clipboardString.append("\n");
            } 
            //***********************************************************
            // create clipboard content
            final ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(clipboardString.toString()); 
            // set clipboard content
            Clipboard.getSystemClipboard().setContent(clipboardContent);
    }
    return writeableData;
}       
    
    /**
     * Get table selection and copy it to the clipboard.
     * @param table
     * @return    
     */
    private ObservableList<CsvData> cutSelectionToClipboard(TableView<CsvData> table) {
            StringBuilder clipboardString = new StringBuilder(); 
            ObservableList<CsvData> tmpData = table.getSelectionModel().getSelectedItems(); 
            int colNum = CSVmanager.getColNums()-1;                
            String text;
            if (tmpData != null) {
            for (int i=0;i<tmpData.size();i++) { 
                for(int k=0;k<colNum;k++) {
                    text = tmpData.get(i).getDataValue(k, i);
                    clipboardString.append(text);
                    clipboardString.append(",");
                }   		
                clipboardString.append("\n");
            } 
            // create clipboard content
            final ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(clipboardString.toString()); 
            // set clipboard content
            Clipboard.getSystemClipboard().setContent(clipboardContent);
    }
            return tmpData;
    }
    }      
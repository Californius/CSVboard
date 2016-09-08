package CSVboard;

/**
 * Yusuf Yurdagel
 * This class provides methods for loading and saving csv data
 * 
 * Overview of all methods
 * checkLoadedText      -> checks wether the file has a readable format or not
 * chooseFile           -> self-explanatory. Method that opens a GUI to choose and select a file
 * detectDelimiter      -> finds the right delimiter of a csv file
 * getCSVData           -> converts a String with loaded data into an observablelist so that data can be added to a table
 * getChangedData       -> reads all data from a table and add them into an observablelist for further operations
 * getColumnHeaderOrder -> counts number of columns from a table and creates a an int of array.
 * getLoadedText        -> contains array string with data from loaded file. Each line of the loaded text is an element of string
 * getNumDelimiter      -> needed to set the number of columns for the table to be loaded
 * getNumberOfColumns   -> self-explanatory.
 * getSearchResults     -> saves search results in an array of string [row][column]
 * XMLexport            -> xml conversion. Convert csv file into an xml file
 * readCSVFile          -> read and load file and put data in an array of string [row][column]
 * setNumDelimiter      -> is needed to to set the right number of columns of a table
 * writeFile            -> saves file 
 * writeFileAs          -> saves file as a new specified name
 * 
 * Overview of all fields
 * chooser              -> instance of FileChooser. Used in method 'chooseFile'
 * fileName             -> name of selected file from filechooser will be saved in fileName. Used in method 'chooseFile'
 * loadedText           -> array of string that inculdes raw data from the loaded file. Used in 'getLoadedText', 'detectDelimiter', 'getSearchResults'
 * numDelimiter         -> contains number of columns a table has. Used in 'setNumDelimiter', 'getNumDelimiter'
 * results              -> raw data that is stored in an array of String [row][column]. Used in 'getSearchResults', 'getNumberOfColumns'
 * 
**/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public final class IOFileOperations {	
    
  @FXML private FileChooser chooser;
        private String fileName;
	public static String [][] results;
        private String [] loadedText;
        public static TableView<CsvData> table;
        private int numDelimiter;
        private ObservableList<CsvData> data;
        
    /** 
     * Used in class 'FXMLDocumentcontroller' in method 'actionTextField' for evaluating new search results.
     * Required to create a new object to get search results in an array of string and in an observablelist
     *  
     * @param searchResults search results are saved in an array of String 'results'.
     */
    public IOFileOperations(String [] searchResults) {
        results = getSearchResults(searchResults);            
    }
    
    /** 
     * Used in class 'FXMLDocumentcontroller' in method 'openFile'.
     * Required for loading file via menu
     * 
     * @param searchField textField for search queries. Needed to change background color while loading file in a task
     * @param textFieldRows textField for displaying the size of loaded csv file. Needed to change background color while loading file in a task
     */
    public IOFileOperations(TextField searchField, TextField textFieldRows) throws FileNotFoundException, IOException {
        results = readCSVFile(searchField,textFieldRows);  
    }       
    
    /** 
     * Used in class 'FXMLDocumentcontroller' in method 'openFile'.
     * Required for loading file via Drag&Drop
     * 
     * @param fileName location of the file
     * @param textField textField for search queries. Needed to change background color while loading file in a task
     * @param textFieldRows textField for displaying the size of loaded csv file. Needed to change background color while loading file in a task
     * @throws java.io.UnsupportedEncodingException 
     */
    public IOFileOperations(String fileName, TextField textField, TextField textFieldRows) throws UnsupportedEncodingException {
        results = readCSVFile(fileName, textField, textFieldRows);     
    }
    
    /** 
     * Used in class 'FXMLDocumentcontroller' in method 'openFile'.
     * Required for loading file via Drag&Drop
     * 
     * @param fileName location of the file
     * @param textField textField for search queries. Needed to change background color while loading file in a task
     * @param textFieldRows textField for displaying the size of loaded csv file. Needed to change background color while loading file in a task
     * @throws java.io.UnsupportedEncodingException 
     */
    public IOFileOperations() throws UnsupportedEncodingException {}  
    
    /** 
     * Used in class 'CSVManager' in method 'setCsvData'.
     * Used to convert observablelist to array of string
     * 
     * @param data observablelist including the data for the tableView. Required for string conversion
     */
    public IOFileOperations(ObservableList<CsvData> data) {
        loadedText = convert2String(data);
    } 
    
    /** 
     * Used in class 'FXMLDocumentcontroller' in method 'saveFileAs' and 'convertFile2XML.
     * 
     * @param table tableView that has to be shown
     */
    public IOFileOperations(TableView<CsvData> table) {                        
        this.table = table;
    } 

    /** 
     * Used in class 'FXMLDocumentcontroller' in method 'saveFile' and 'TableKeyEventHandler'.
     * Required to overwrite (save) the file
     * 
     * @param table tableView showing the data. 
     * @param fileName location of the file
     */
    public IOFileOperations(TableView<CsvData> table, String fileName) {                        
        this.fileName = fileName;
        this.table = table;
    }   
    
    /** 
     * Used in class 'FXMLDocumentcontroller' in method 'openFile'
     * Required to overwrite (save) the file
     * 
     * @return number of columns of the table
     */
    public int getNumberOfColumns() {
        return results[0].length;
    }
    
    /** 
     * Used in class 'CSVManager' in method 'setCSVdata'
     * Required to overwrite (save) the file
     * 
     * @return array of string in which tha raw data is stored. Each element contains one line of a file
     */    
    public String[] getLoadedText() {
        return loadedText;
    }
    
    /** 
     * Used in method 'readCSVfile'.
     * 
     * @param loadedText line that checks if it is a text file or not
     * @return returns true if it is a text file
     */
    private boolean checkLoadedText(String loadedText) {
        if (loadedText==null) return false;
        if (loadedText.contains("<?xml version=\"1.0\"")) return false;
        if (loadedText.contains("%PDF-")) return false;
        double tmp1 = loadedText.replaceAll("[\\w]+", "").length();
        double tmp2 = loadedText.length()-tmp1;
        double tmp3 = tmp2/tmp1;
        return tmp3>=1; //return true if ratio (all characters)/(alphanumerical characters) >=1
    }

    /** 
     * Used in method 'FXMLDocumentController' in method 'openFile' and 'actionTextField'.
     * 
     * @return results of loaded data
     */    
    public ObservableList<CsvData> getCSVData() {
        ObservableList<CsvData> tmpList = FXCollections.observableArrayList();
        int numRow=results.length;
            if (results==null) return null;
            tmpList.clear();
            int numCol=results[0].length;
            for(int i=0;i<numRow;i++) tmpList.add(new CsvData(i,numCol, results));     
        return tmpList;
    }
        
    /** 
     * Used in method 'writeFileAs' and 'XMLexport'.
     * 
     * @param option GUI for loading data if option is '1'. Otherwise open GUI for saving data
     * @return fileName location of the file that has to be saved or loaded
     */ 
    private String chooseFile(int option) {		
        ExtensionFilter fileExtensionCSV = new ExtensionFilter("Comma-seprated values", ".csv");        
        ExtensionFilter fileExtensionXML = new ExtensionFilter("XML file", ".xml");        
        chooser = new FileChooser();   
        File file;
        try {
            if(option==1) {
                chooser.setTitle("Open file");
                file = chooser.showOpenDialog(null);
                if(file!=null) {
                        String tmpFileName = file.getAbsolutePath();
                        return tmpFileName;
                }            
            } else {
                if (option==2) {
                    chooser.getExtensionFilters().add(fileExtensionCSV);               
                } else {
                    chooser.getExtensionFilters().add(fileExtensionXML);               
                }
                chooser.setTitle("Save file as");                
                file = chooser.showSaveDialog(null);    
                String tmpFileName = file.getAbsolutePath();	               
                return tmpFileName;		      
                }
        } catch (NullPointerException e) {}
    return fileName;
    }
    
    /** 
     * Used in method 'getSearchResults'
     * detects the right delimiter of the loaded csv file by comparing the number of occurances of the delimiter candidates.
     * Result is the delimeter with the highest amount of occurances
     * 
     * To save computing resources only the first 50 lines will be needed for delimiter detection in case of more than 50 lines
     * 
     * @param loadedText
     * @return returns the right delimiter as string
     */ 
    public String detectDelimiter(String [] loadedText) {
            String [] delimiters = {":",",",";","|","\t","\\"};
            int foundDelimiter=0;
            int numDelimiters=0;
            int count = loadedText.length;
            int cycle=0;
            if(count<=0) cycle=0;
            if(count>=1) cycle=count;
            if(count>100) cycle=50;
            for(int k=0;k<cycle;k++) {
                for(int i=0;i<delimiters.length;i++) {
                    numDelimiters=loadedText[k].length()-loadedText[k].replace(delimiters[i], "").length(); //get number of delimiter occurances
                    if((numDelimiters>foundDelimiter)){
                        setNumDelimiter(numDelimiters);
                        foundDelimiter=i;
                    }
                }
            }
        return delimiters[foundDelimiter];
    }
    
        /** 
     * Used in method 'readCSVFile'
     * detects the right delimiter of the loaded csv file by comparing the number of occurances of the delimiter candidates.
     * Result is the delimeter with the highest amount of occurances
     * 
     * To save computing resources only the first 50 lines will be needed for delimiter detection in case of more than 50 lines
     * 
     * @param loadedText
     * @return returns the right delimiter as string
     */ 
    private String detectDelimiter(BufferedReader bfr, int count) {
        String [] delimiters = {":",",",";","|","\t","\\"};
        int foundDelimiter=0;
        int numDelimiters=0;
        int cache=0;
        int cycle=0;
        if(count<=0) cycle=0;
        if(count>=1) cycle=count;
        if(count>100) cycle=50;
        try {
            for(int k=0;k<cycle;k++) {
                String line = bfr.readLine();
                for(int i=0;i<delimiters.length;i++) {
                    if (line!=null) numDelimiters=line.length()-line.replace(delimiters[i], "").length(); //get number of delimiter occurances
                    if((numDelimiters>cache)){
                        cache=numDelimiters;
                        setNumDelimiter(cache);
                        foundDelimiter=i;
                    }
                }
            }
            bfr.close();
        } catch (IOException ex) {
            Logger.getLogger(IOFileOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return delimiters[foundDelimiter];
    }
    
    /** 
     * Used in class 'FXMLDocumentController' in method 'openFile'
     * Used in class 'IOFileOperations' in method 'openFile'
     * Sets the number of Columns by getting the occurance of the detected delimiter in a line
     * and save it to 'numDelimiter' which is important to specify the table size
     * 
     * @param numDelimiter 
     */
    public void setNumDelimiter(int numDelimiter) {
        this.numDelimiter=numDelimiter;
    }
    
    /** 
     * Used in method 'getSearchResults', 'readCSVFile'
     * Used in class 'FXMLDocumentControll' in method 'openFile'
     * 
     * @return number of Delimiters to specify the table size
     */
     public int getNumDelimiter() {
        return (numDelimiter+1);
    }
    
     
    /** 
     * Used in constructor 'IOFileOperations'
     * 
     * @param loadedText raw data that is stored in an array of string. Each element of a string represents a line of a csv file
     * @return searchresults saved as an array of string [row][column]
     */
     private String[][] getSearchResults(String [] loadedText) {
        String detectedDel = detectDelimiter(loadedText);
        int colNumSize = getNumDelimiter();
        if (loadedText==null) return null;
        results = new String[loadedText.length][colNumSize];
        for(int row=0;row<loadedText.length;row++) {
                boolean endOfLine=false;
                int pos = 0, cache = 0, end;
                for(int col=0;col<colNumSize;col++) {
                    end = loadedText[row].indexOf(detectedDel, pos);
                    if(end<0) {
                        if(endOfLine) {
                            results[row][col]="";
                        } else {
                            endOfLine=true;
                            end = loadedText[row].length();
                            results[row][col] = loadedText[row].substring(pos, end);  
                            cache=end-1;
                        }
                    } else results[row][col] = loadedText[row].substring(pos, end);
                    pos = end + 1;
                    if (pos==0) pos = cache+1;
                }
            try {
            } catch (NullPointerException e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Not a CSV file!");
                alert.showAndWait();
            }  
        }
        return results;
    }
     
    /** 
     * Used in constructor 'IOFileOperations'
     * 
     * @param data observablelist that has to be converted into an array of string
     * @return tmp converted data
     */
    private String [] convert2String(ObservableList<CsvData> data) { 
        StringBuilder clipboardString=new StringBuilder();
        String text;
        int colNumSize = CSVmanager.getColNums();
        for (int i=0;i<data.size();i++) {
            for(int k=0;k<colNumSize;k++) {
                text = data.get(i).getDataValue(k, i); //**
                if(text!=null) {
                    clipboardString.append(text);
                    clipboardString.append(";");
                } else {
                    clipboardString.append("");
                    clipboardString.append(";");
                }
            }
            clipboardString.append("\n\r"); //<- 13.06.2016 nach der Suche wird ab der zweiten Zeile aufgrund eines linefeeds die Tabellengröße verändert
        }
        String [] tmp = clipboardString.toString().split("\n\r");
        return tmp;
    }

    /** 
     * Used in constructor 'FXMLDocumentController' in method 'saveFile'
     * Used in constructor 'TableKeyEventHandle' in method 'TableKeyEventHandler'
     * 
     * reads each table element from table and overwrites the file
     *  
     */
    public void writeFile(TextField textField, TextField textFieldRows) {
        ExecutorService executor = Executors.newCachedThreadPool();
        int newColSize = table.getColumns().size();
        ObservableList<CsvData> tmpData = getData(table);
        StringBuilder clipboardString=new StringBuilder();
        Callable<String[][]> callable = new Callable<String[][]>() {
            @Override
            public String[][] call() throws Exception {
                Platform.runLater(() -> {
                    textField.setStyle("-fx-background-color:#99FFFF;-fx-font-weight: bold;-fx-font-style: italic");
                    textFieldRows.setStyle("-fx-background-color:#99FFFF;-fx-font-weight: bold;-fx-font-style: italic");
                    textField.setText("saving...");
                });
                    FileOutputStream fos;
                    CSVmanager csvManager = new CSVmanager();
                    csvManager.setFileName(fileName);
                    File tmpFile = new File(fileName);
                    fos = new FileOutputStream(tmpFile);
                    OutputStreamWriter osw = null;
                    fos = new FileOutputStream(tmpFile); /**NullpointerException, weil vermutlich yusuf.txt nicht gefunden wird**/
                    osw = new OutputStreamWriter(fos,"ISO-8859-1");
                    String tmpText;
                    int [][] indexCol = getIDX(table);
                    long start = System.nanoTime();
                    for (int i=0;i<tmpData.size();i++) {
                        for(int k=0;k<newColSize;k++) {
                            tmpText = tmpData.get(i).getDataValue(k, i, table, indexCol); //**
                            if (tmpText==null) tmpText="\n";
                            if (!tmpText.equals("\n")) {
                                clipboardString.append(tmpText);
                                clipboardString.append(";");
                            }                        
                        }
                        clipboardString.append("\r\n");
                    }
                    long stop = System.nanoTime();
                    System.out.println("time >> "+(stop-start)/1000000+"s");
                    osw.write(clipboardString.toString());
                    osw.flush();
                    osw.close();
                    textField.setStyle("-fx-background-color:#FFFFFF");
                    textField.setText("");
                    textFieldRows.setStyle("-fx-background-color:#FFFFFF;");
        return null;
        }
        };
        executor.submit(callable);
    }
    
    public int [][] getIDX (TableView<CsvData> table) {
         int [][] indexCol = new int[2][table.getColumns().size()];
         for(int i=0;i<table.getColumns().size();i++) {
            String colText=table.getColumns().get(i).getText();
                colText = colText.replaceAll("[^\\[\\d\\]]\\w*", "");
                colText = colText.replaceAll("[\\[\\]]", "");
                int idx = Integer.parseInt(colText);
                indexCol[1][i] = idx;
         }
         return indexCol;
     }
    
    /** 
     * Used in constructor 'FXMLDocumentController' in method 'saveFileAs'
     * Used in constructor 'TableKeyEventHandle' in method 'TableKeyEventHandler'
     * 
     * reads each table element from table and saves it in a new specified file
     *  
     */
    public void writeFileAs() {
        ObservableList<CsvData> tmpData = getData(table);
        fileName = chooseFile(2);
        if (fileName!=null) {
            StringBuilder clipboardString=new StringBuilder();
            try {
                FileOutputStream fos;
                File tmpFile = new File(fileName);
                fos = new FileOutputStream(tmpFile);
                OutputStreamWriter osw = null;
                fos = new FileOutputStream(tmpFile); /**NullpointerException, weil vermutlich yusuf.txt nicht gefunden wird**/
                osw = new OutputStreamWriter(fos,"ISO-8859-1");
                int colNumSize = CSVmanager.getColNums();
                String tmpText="";
                int colIdx = table.getColumns().size(); //getColumnData(table);
                int [][] indexCol = getIDX(table);
                for (int i=0;i<tmpData.size();i++) {
                    for(int k=0;k<colIdx;k++) {
                        tmpText = tmpData.get(i).getDataValue(k, i, table, indexCol); 
                            clipboardString.append(tmpText);
                            clipboardString.append(";");   
                    }
                    clipboardString.append("\r\n");
                }
                osw.write(clipboardString.toString());
                osw.flush();
                osw.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(IOFileOperations.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(IOFileOperations.class.getName()).log(Level.SEVERE, null, ex);
            } 
            CSVmanager csvManager = new CSVmanager();
            csvManager.setFileName(fileName);
        }
    }

    /** 
     * Used in method 'XMLexport', 'writeFile', 'writeFileAs'
 get observablelist from current table
     * 
     * @param table current tableView
     * @return observablelist where all data are stored
     */
    private ObservableList<CsvData> getData(TableView<CsvData> table) {
        ObservableList<CsvData> tmpData = FXCollections.observableArrayList();            
        for(int i=0;i<table.getItems().size();i++) {                
            tmpData.add(table.getItems().get(i));
        }        
        return tmpData;
    }

    /** 
     * Used in method 'XMLexport'
     * 
     * @param table current tableView
     * @return array of int that contains the numerical column-order of table
     */    
    private int [] getColumnHeaderOrder(TableView<CsvData> table) {
        int colSize = table.getColumns().size();
        int foo [] = new int[colSize];
        for(int i=0;i<colSize;i++) foo[i] = i;    
        return foo;
    }

    /** 
     * Used in method 'FXMLDocumentController' in method 'convertToXML'
     * save current table as XML
     * 
     */ 
    public void XMLexport() {
        ObservableList<CsvData> tmpData = getData(table);
        fileName = chooseFile(3);
        StringBuilder clipboardString=new StringBuilder();
        clipboardString.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        clipboardString.append("<table>\r\n");
        try {
            FileOutputStream fos;
            File tmpFile = new File(fileName);
            fos = new FileOutputStream(tmpFile);
            OutputStreamWriter osw = null;
            fos = new FileOutputStream(tmpFile); /**NullpointerException, weil vermutlich yusuf.txt nicht gefunden wird**/
            osw = new OutputStreamWriter(fos,"ISO-8859-1");
            String tmpText="";
            int [] colIdx = getColumnHeaderOrder(table);
            for (int i=0;i<tmpData.size();i++) {
                clipboardString.append("\t<column>\r\n");
                for(int k=0;k<colIdx.length;k++) {
                    tmpText = tmpData.get(i).getDataValue(colIdx[k], i); //.trim()? 
                        clipboardString.append("\t\t<column").append(k).append(">");                        
                        clipboardString.append(tmpText);
                        clipboardString.append("</column").append(k).append(">");
                        clipboardString.append("\r\n");                            
                }                    
                clipboardString.append("\t</column>\r\n");
            }
            clipboardString.append("</table>\r\n");
            osw.write(clipboardString.toString());
            osw.flush();
            osw.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(IOFileOperations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IOFileOperations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException nu) {
        }
    }

    /** 
     * Used in constructor 'IOFileOperations' 
     * Required in 'openFile' in 'FXMLDocumentController to open file via menu
     * 
     * @param textField search field of CSVBorad to change background color while loading a (large) file
     * @param textFieldRow displays the dimensions of a table. required to cange background color while loading a (large) file
     * @return array of string [rows][colums]
     * 
     */ 
    private String [][] readCSVFile(TextField textField, TextField textFieldRows) throws FileNotFoundException, IOException {
        ExecutorService executor = Executors.newCachedThreadPool();
        String [][] partOfText=null;
        chooser = new FileChooser();
        File tmpFile = chooser.showOpenDialog(null);
        if(tmpFile==null) return null;
        fileName=tmpFile.getAbsolutePath();
        CSVmanager csvManager = new CSVmanager();
        csvManager.setFileName(fileName);
        Charset charset = StandardCharsets.UTF_8;
                        FileInputStream fis = null;
                        InputStreamReader isr = null;
                        BufferedReader bfr = null;                                
                        int i=0;
                    try {
                        fis = new FileInputStream(tmpFile);
                        isr = new InputStreamReader(fis,charset);
                        bfr = new BufferedReader(isr);
                        String line = bfr.readLine();
                        textField.setStyle("-fx-background-color:#FFFFFF");
                        textField.setText("");
                        textFieldRows.setStyle("-fx-background-color:#FFFFFF;");
                        boolean isText = checkLoadedText(line);
                        if (!isText) {
                            Alert alert = new Alert(AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(null);
                            alert.setContentText("invalid File");
                            alert.showAndWait();
                            return null;
                        }
                        bfr.close();
                        fis = new FileInputStream(tmpFile);
                        isr = new InputStreamReader(fis,charset);
                        bfr = new BufferedReader(isr);
                    } catch (FileNotFoundException e) {
                    }
        
        
        Callable<String[][]> callable = new Callable<String[][]>() {
            Charset charset = StandardCharsets.UTF_8;
            String [][] partOfText=null;
        
            @Override
            public String[][] call() throws Exception {
                Platform.runLater(() -> {
                    textField.setStyle("-fx-background-color:#99FFFF;-fx-font-weight: bold;-fx-font-style: italic");
                    textFieldRows.setStyle("-fx-background-color:#99FFFF;-fx-font-weight: bold;-fx-font-style: italic");
                    textField.setText("loading...");
                });

               File tmpFile = new File(fileName);
                        FileInputStream fis = null;
                        InputStreamReader isr = null;
                        BufferedReader bfr = null;                                
                        int i=0;
                    try {
                        fis = new FileInputStream(tmpFile);
                        isr = new InputStreamReader(fis,charset);
                        bfr = new BufferedReader(isr);
                        while(bfr.readLine()!=null) i=i+1;
                        bfr.close();
                        fis = new FileInputStream(tmpFile);
                        isr = new InputStreamReader(fis,charset);
                        bfr = new BufferedReader(isr);
                    } catch (FileNotFoundException e) {
                    }
                    try {   
                        String line=bfr.readLine();
                        fis = new FileInputStream(tmpFile);
                        isr = new InputStreamReader(fis,charset);
                        bfr = new BufferedReader(isr);
                        String delimiters=detectDelimiter(bfr, i+1);
                        bfr.close();
                        int numDelimiters=getNumDelimiter();
                        bfr.close();
                        partOfText = new String[i][numDelimiters];
                        fis = new FileInputStream(tmpFile);
                        isr = new InputStreamReader(fis,charset);
                        bfr = new BufferedReader(isr);
                       int row=0;
                       boolean endOfLine=false;

                    while((line=bfr.readLine())!=null) {
                        int end=0,pos=0,cache=0;
                            for(int col=0;col<numDelimiters;col++) {
                                end = line.indexOf(delimiters, pos);
                                if(end<0) {
                                    if(endOfLine) {
                                        partOfText[row][col]="";
                                    } else {
                                        endOfLine=true;
                                        end = line.length();
                                        partOfText[row][col] = line.substring(pos, end);  
                                        cache=end-1;
                                    }
                                } else {
                                    partOfText[row][col] = line.substring(pos, end);
                                }
                                pos = end + 1;
                                if (pos==0) pos = cache+1;
                            }
                            endOfLine=false;
                            row=row+1;
                    }
                    } catch (IOException e) {
                    } finally {
                            if(fis!=null)
                            if(bfr!=null)
                                    try {
                                        fis.close();
                                        bfr.close();
                                    } catch (IOException e) {
                                    }
                    }
                return partOfText;
            }
        };
        Future<String[][]> result = executor.submit(callable);
        try {
            partOfText = result.get();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(IOFileOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return partOfText;
            } //readFile 

    /** 
     * Used in constructor 'IOFileOperations' 
     * Required in 'openFile' in 'FXMLDocumentController to open file vie drag&drop
     * 
     * @param fileName location of file to be loaded
     * @param textField search field of CSVBorad to change background color while loading a (large) file
     * @param textFieldRow displays the dimensions of a table. required to cange background color while loading a (large) file
     * @return array of string [rows][colums]
     * 
     */
    private String [][] readCSVFile(String fileName, TextField textField, TextField textFieldRows) throws UnsupportedEncodingException  { //dragdrop
        CSVmanager csvManager = new CSVmanager();
        csvManager.setFileName(fileName);
        FileInputStream fis = null;
        InputStreamReader isr=null;
        BufferedReader bfr = null;
        String [][] partOfText=null;
        Charset charset = StandardCharsets.UTF_8;
                File tmpFile = new File(fileName); 		
                int i=0;
                    try {   
                        fis = new FileInputStream(tmpFile);
                        isr = new InputStreamReader(fis,charset);
                        bfr = new BufferedReader(isr);
                        String line=bfr.readLine();
                        boolean isText = checkLoadedText(line);
                            if (!isText) {
                                textField.setStyle("-fx-background-color:#FFFFFF");
                                textField.setText("");
                                textFieldRows.setStyle("-fx-background-color:#FFFFFF;");
                                Alert alert = new Alert(AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText(null);
                                alert.setContentText("invalid File");
                                alert.showAndWait();
                                return null;
                            }
                        while(bfr.readLine()!=null) i=i+1;
                        bfr.close();
                        fis = new FileInputStream(tmpFile);
                        isr = new InputStreamReader(fis,charset);
                        bfr = new BufferedReader(isr);
                        String delimiters=detectDelimiter(bfr,i+1);
                        int numDelimiters=getNumDelimiter();
                        partOfText = new String[i+1][numDelimiters];
                       int row=0;
                       boolean endOfLine = false;
                       bfr.close();
                        fis = new FileInputStream(tmpFile);
                        isr = new InputStreamReader(fis,charset);
                        bfr = new BufferedReader(isr);
                    while((line=bfr.readLine())!=null) {
                        int end=0,pos=0,cache=0;
                            for(int col=0;col<numDelimiters;col++) {
                                end = line.indexOf(delimiters, pos);
                                if(end<0) {
                                    if(endOfLine) {
                                        partOfText[row][col]="";
                                    } else {
                                        endOfLine=true;
                                        end = line.length();
                                        partOfText[row][col] = line.substring(pos, end);  
                                        cache=end-1;
                                    }
                                } else {
                                    partOfText[row][col] = line.substring(pos, end);
                                }
                                pos = end + 1;
                                if (pos==0) pos = cache+1;
                            }
                            endOfLine=false;
                            row=row+1;
                    }
                    } catch (IOException e) {
                    }  finally {
                            if(fis!=null)
                            if(bfr!=null)
                                    try {
                                        fis.close();
                                        bfr.close();
                                    } catch (IOException e) {
                                    }
                    }
                    return partOfText;
            } //readFile
    
     public String [] arrayConversion(String [][] result) {
        long start = System.nanoTime();
        StringBuilder rawData = new StringBuilder();
        for(int i=0;i<result.length;i++) {
            for(int k=0;k<result[0].length;k++) {
                rawData.append(result[i][k]+";");
            }
            rawData.append("\n");
        }
        int pos=0, end=0;
        String [] tmp = new String[results.length];
        for(int i=0;i<tmp.length;i++) {
            end = rawData.indexOf("\n", pos);
            tmp[i]=rawData.substring(pos, end);
            pos=end+1;
        }
//        String [] tmp=rawData.toString().split("\n");
        long stop = System.nanoTime();
        System.out.println("delay arrayconversion >> "+(stop-start)/1000000+"ms");
        return tmp;
    }
    
    public ObservableList<CsvData> replace(String [][] results, String find, String replace, int columnIdx, boolean isSensitive) {
        ObservableList<CsvData> tmp;
        System.out.println("columnIDX="+columnIdx+"\tresults.length>>"+results.length);
        int k;
        if (isSensitive) {
            if(columnIdx==results[1].length) {
                //case-sensitive and all columns
                for (String[] result : results) {
                    for (k=0; k<columnIdx; k++) { 
                        if (result[k].contains(find)) {
                            int startIDX = result[k].indexOf(find);
                            int endIDX = startIDX + find.length();
                            StringBuilder tmpBuilder = new StringBuilder(result[k]);
                            tmpBuilder.replace(startIDX, endIDX, replace);
                            result[k] = tmpBuilder.toString();
                        } 
                    }
                }
            } else {
                for (int i=0;i<results.length;i++) {
                    //case-sensitive and not all columns
                    if (results[i][columnIdx].contains(find)) {
                        int startIDX = results[i][columnIdx].indexOf(find);
                        int endIDX = startIDX + find.length();
                        StringBuilder tmpBuilder = new StringBuilder(results[i][columnIdx]);
                        tmpBuilder.replace(startIDX, endIDX, replace);
                        results[i][columnIdx] = tmpBuilder.toString();
                    } 
                }
            }
        } else {
            //not case-sensitive and all columns
            if(columnIdx==results[1].length) {
                System.out.println("not case-sensitive and all columns");
                System.out.println("column index -> "+columnIdx);
                for (int i=0;i<results.length;i++) {
                    for(int l=0;l<columnIdx;l++) {
                       if (results[i][l].toLowerCase().contains(find.toLowerCase())) {
                       int startIDX = results[i][l].toLowerCase().indexOf(find.toLowerCase());
                       int endIDX = startIDX + find.length();
                       StringBuilder tmpBuilder = new StringBuilder(results[i][l]);
                       tmpBuilder.replace(startIDX, endIDX, replace);
                       results[i][l] = tmpBuilder.toString();
                    }  
                }
                }
            } else {
                //not case-sensitive and not all columns
                System.out.println("not case-sensitive and not all columns");
                System.out.println("column index -> "+columnIdx);
                for (int i=0;i<results.length;i++) {
                    if (results[i][columnIdx].toLowerCase().contains(find.toLowerCase())) {
                        int startIDX = results[i][columnIdx].toLowerCase().indexOf(find.toLowerCase());
                        int endIDX = startIDX + find.length();
                        StringBuilder tmpBuilder = new StringBuilder(results[i][columnIdx]);
                        tmpBuilder.replace(startIDX, endIDX, replace);
                        results[i][columnIdx] = tmpBuilder.toString();
                    } 
                }
            }
        }
        tmp = getCSVData(results);
        return tmp;
    }
    
    private ObservableList<CsvData> getCSVData(String[][] results) {
        int numRow = results.length;
        int numCol = results[0].length;
        data=FXCollections.observableArrayList();
        for(int i=0;i<numRow;i++) data.add(new CsvData(i, numCol, results));            
        return data;
    }
}

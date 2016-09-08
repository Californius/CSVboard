/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CSVboard;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

/**
 * Yusuf Yurdagel
 * Main controller class
 * 
 * Overview of all methods
 * getColOrder          -> get right (changed) column order
 * getColumnWidth       -> get optimized sizes of each table column
 * setSelection         -> only allow a selection of one row
 * 
**/
public class Tools {
    @FXML private static ObservableList<CsvData> data = FXCollections.observableArrayList();
          private TableView<CsvData> table;
          
    public Tools(ObservableList<CsvData> data, TableView<CsvData> table) {
        this.data = data;
        this.table = table;
        initDragDrop();
    }; //constructor
    
    private void initDragDrop() {
        table.setRowFactory((TableView<CsvData> p) -> {
            final TableRow<CsvData> row = new TableRow<>();
            row.setOnDragEntered((DragEvent t) -> {
                setSelection(row);
            });
            row.setOnDragDetected((MouseEvent t) -> {
                StringBuilder clipboardString = new StringBuilder();
                Dragboard db = row.getTableView().startDragAndDrop(TransferMode.COPY);
                final ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(clipboardString.toString());
                // set clipboard content
                Clipboard.getSystemClipboard().setContent(clipboardContent);
                db.setContent(clipboardContent);
                setSelection(row);
                t.consume();
            });
            return row;
        });
    } 
    
    private void setSelection(TableRow row) {
        if (row.isSelected()) {
            table.getSelectionModel().clearSelection(row.getIndex());
        } else {
            table.getSelectionModel().select(row.getIndex());
        }
    }
    
    public double [] getColumnWidth(String [] headerName) {
        String textArray [] = new String[data.size()]; 
        double [][] textWidthMatrix  = new double[table.getItems().size()][table.getColumns().size()];
        AffineTransform affinetransform = new AffineTransform();     
        FontRenderContext frc = new FontRenderContext(affinetransform,true,true);     
        int loop=0;
        int dataEntries = table.getItems().size();
        if ((1<=dataEntries) & (dataEntries<=20)) loop=dataEntries;
        if ((20<dataEntries) & (dataEntries<=100)) loop=20;
        if (dataEntries>100) loop=30;        
        for (int lColumn=0;lColumn<table.getColumns().size();lColumn++) {
            String tmp=headerName[lColumn];//"["+lColumn+"]";
            Font font;
            font = new Font("Courier New", Font.PLAIN, 12);
            for(int row=0;row<loop;row++) {
                textArray[row]=data.get(row).getDataValue(lColumn, row);
                if (textArray[row]==null) textArray[row]="";
                double textwidth = (double)(font.getStringBounds(textArray[row], frc).getWidth());
                textWidthMatrix[row][lColumn]=textwidth;
            }
            table.getColumns().get(lColumn).setText(tmp); 
        }
        double [] maxWidthValues=new double[table.getColumns().size()];
        double maxTmp;
        for(int i=0;i<textWidthMatrix[0].length;i++) {
            maxTmp=0;
            for (double[] textWidthMatrix1 : textWidthMatrix) {
                maxTmp = Math.max(maxTmp, textWidthMatrix1[i]);
            }
            maxWidthValues[i]=maxTmp+50;
        }
        for(int i=0;i<maxWidthValues.length;i++) {
            table.getColumns().get(i).setPrefWidth(maxWidthValues[i]);
        }
        return maxWidthValues; //Werte der optimierten Spaltenbreite
    }

    public double [] getColumnWidth(TableView<CsvData> table) {
        String textArray [] = new String[data.size()]; 
        double [][] textWidthMatrix  = new double[table.getItems().size()][table.getColumns().size()];
        AffineTransform affinetransform = new AffineTransform();     
        FontRenderContext frc = new FontRenderContext(affinetransform,true,true);     
        int loop=0;
        int dataEntries = table.getItems().size();
        if ((1<=dataEntries) & (dataEntries<=20)) loop=dataEntries;
        if ((20<dataEntries) & (dataEntries<=100)) loop=20;
        if (dataEntries>100) loop=30;        
        for (int lColumn=0;lColumn<table.getColumns().size();lColumn++) {
            String tmp="["+lColumn+"]";
            Font font;
            font = new Font("Courier New", Font.PLAIN, 12);
            for(int row=0;row<loop;row++) {
                textArray[row]=data.get(row).getDataValue(lColumn, row);
                if (textArray[row]==null) textArray[row]="";
                double textwidth = (double)(font.getStringBounds(textArray[row], frc).getWidth());
                textWidthMatrix[row][lColumn]=textwidth;
            }
            table.getColumns().get(lColumn).setText(tmp); 
        }
        double [] maxWidthValues=new double[table.getColumns().size()];
        double maxTmp;
        for(int i=0;i<textWidthMatrix[0].length;i++) {
            maxTmp=0;
            for (double[] textWidthMatrix1 : textWidthMatrix) {
                maxTmp = Math.max(maxTmp, textWidthMatrix1[i]);
            }
            maxWidthValues[i]=maxTmp+50;
        }
        for(int i=0;i<maxWidthValues.length;i++) {
            table.getColumns().get(i).setPrefWidth(maxWidthValues[i]);
        }
        return maxWidthValues; //Werte der optimierten Spaltenbreite
    }    
    
    public static int [] getColOrder(TableView<CsvData> table) {
        int [] colOrder = new int[table.getColumns().size()];
        for(int i=0;i<table.getColumns().size();i++) {
            String tmp = table.getColumns().get(i).getText();
            tmp = tmp.replaceAll("[^\\[\\d\\]]\\w+", "");
            tmp = tmp.replaceAll("[\\[\\]]", "");
            tmp = tmp.replaceAll("[\\s]", "");
            tmp = tmp.replaceAll("[\"]+", "");
            tmp = tmp.replaceAll("[()]+", "");
            tmp = tmp.replaceAll("[^\\w]+", "");
            int idx = Integer.parseInt(tmp);
            colOrder[i]=idx;
        }
        return colOrder;
    }
    
}

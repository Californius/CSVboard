package CSVboard;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TableView;

public class CsvData {
    
    public StringProperty [] dataValue;
       
    public CsvData(int row, int numCol, String loadedText [][]) {
        this.dataValue = new StringProperty[numCol];
        for(int i=0;i<numCol;i++) {
            dataValue[i] = new SimpleStringProperty(loadedText[row][i]);
        }
    }   

    public StringProperty dataProperty(int col) { 
        if (col>=dataValue.length) return null;
        else return dataValue[col];
    }   
    
    public String getDataValue(int col, int row) {
        if(col>=dataValue.length) return null;
        if(dataValue[col].get().isEmpty()) return null;
        if(dataValue[col].get()==null) return null;
        if("".equals(dataValue[col].get())) return null;
        return dataValue[col].get();
    }
    
     public String getDataValue(int col, int row, TableView<CsvData> table, int [][] indexCol) {
         int newCol = table.getColumns().size();
         for(int i=0;i<indexCol[1].length;i++) indexCol[0][i]=i;
         StringProperty [] newValue=new StringProperty[newCol];
         for(int i=0;i<newCol;i++) {             
            newValue[i]=dataValue[indexCol[1][i]]; //to be continued 27.11.15 (save saveAs, erste Zeile bei Buchungen....csv nullpointerexception
         }
         return newValue[col].get();
    }
     
}

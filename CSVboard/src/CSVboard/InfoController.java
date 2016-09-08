/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CSVboard;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author yyu
 */
public class InfoController implements Initializable {
    
    private Stage stage;
    
    public void setDialogStage(Stage stage) throws IOException {
        this.stage = stage;
    }
    
    @FXML private void closeWindow(ActionEvent event) {
        Stage editStage = CSVmanager.getStage();
        editStage.close();
    }
    
    @FXML TextArea textArea;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        textArea.setEditable(false);
        textArea.setFocusTraversable(false);
        textArea.setText("This program is free software; you can redistribute it and/or"
                        + "\nmodify it under the terms of the GNU General Public License"
                        + "\nas published by the Free Software Foundation; either version"
                        + "\n2 of the License, or (at your option) any later version.\n" 
                        + "\nThis program is distributed in the hope that it will be useful, \nbut WITHOUT ANY WARRANTY; "
                        + "without even the implied \n"
                        + "warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR\n"
                        + "PURPOSE. See the GNU General Public License for more details.\n" 
                        + "\nYou should have received a copy of the GNU General Public\n"
                        + "License along with this program; if not, write to the \nFree Software Foundation, "
                        + "Inc., 51 Franklin Street, \nFifth Floor, Boston, MA  02110-1301, USA."
                
        );
    }    
    
}

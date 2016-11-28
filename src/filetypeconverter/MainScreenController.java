/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filetypeconverter;

import java.nio.file.Files;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainScreenController implements Initializable {

    @FXML
    private ListView listview;
    @FXML
    private TextField destFolderTextBox;

    private ArrayList<String> listOfFiles = new ArrayList<String>();
    private ObservableList<String> ov = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO  
    }

    @FXML
    protected void onDragEvent(DragEvent event) {
        event.acceptTransferModes(TransferMode.COPY);
        event.consume();
    }

    @FXML
    protected void onDropEvent(DragEvent event) {
       // System.out.println(event.toString());
        Dragboard db = event.getDragboard();
        String[] list;

        if (db.hasFiles()) {
            //System.out.println(db.getFiles().toString());
            String names = db.getFiles().toString();
            list = (names.substring(1, names.length() - 1)).split(", ");
            setListItem(list);
        } else {
            System.out.println("no files");
        }
    }

    private void setListItem(String[] files) {
        for (String file : files) {
            listOfFiles.add(file);
            ov.add(file);
        }
        listview.setItems(ov);
    }

    @FXML
    protected void executeConversion(MouseEvent event) {
        if (destFolderTextBox.getText().equals("")) {
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            VBox dialogVbox = new VBox(20);
            dialogVbox.getChildren().add(new Text("Please select the destination folder"));
            Scene dialogScene = new Scene(dialogVbox, 300, 200);
            dialog.setScene(dialogScene);
            dialog.show();
        } else {
            for (String file : ov) {
                Path FROM = Paths.get(file);
                System.out.println(file.lastIndexOf("\\"));
                Path TO = Paths.get(genDestPath(file.substring(file.lastIndexOf("\\")), destFolderTextBox.getText()));

                try {
                    Files.copy(FROM, TO, StandardCopyOption.COPY_ATTRIBUTES);
                } catch (IOException ex) {
                    Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @FXML
    protected void resetAll(MouseEvent event) {
        listOfFiles = new ArrayList<>();
        ov = FXCollections.observableArrayList();
        listview.setItems(ov);
    }

    private String genDestPath(String file, String dest) {
       String name;
       if(file.lastIndexOf('.') < 0){
           name= file;
       }else
            name = file.substring(0, file.lastIndexOf('.'));
       System.out.println( dest + name + ".txt");
        return dest + name + ".txt";
    }

    @FXML
    protected void browseDirectory(MouseEvent event) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        configuringDirectoryChooser(directoryChooser);
        Stage primaryStage = new Stage();
        File dir = directoryChooser.showDialog(primaryStage);
        if (dir != null) {
            destFolderTextBox.setText(dir.getAbsolutePath());
        } else {
            destFolderTextBox.setText(null);
        }
    }

    private void configuringDirectoryChooser(DirectoryChooser directoryChooser) {
        directoryChooser.setTitle("Select A Directory");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }
}

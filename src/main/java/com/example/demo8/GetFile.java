package com.example.demo8;

import org.apache.commons.io.FilenameUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javafx.stage.FileChooser;
import java.io.File;
import java.time.format.DateTimeFormatter;


public class GetFile extends Application {
    public void start(Stage stage)
    {
        try {
            stage.setTitle("FileChooser");
            FileChooser file_chooser = new FileChooser();
            Label label = new Label("no files selected");
            Button button = new Button("Show open dialog");
            final File[] file = new File[1];
            EventHandler<ActionEvent> event =
                    new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent e)
                        {
                            file[0] = file_chooser.showOpenDialog(stage);
                            if (file[0] != null) {
                                label.setText(file[0].getAbsolutePath()
                                        + " selected\n\n"+ FilenameUtils.getExtension(
                                        file[0].getAbsolutePath()));
                                saveFile(file);
                                System.out.println(file[0].getAbsolutePath());
                            }
                        }
                    };
            button.setOnAction(event);
            VBox vbox = new VBox(30, label, button);
            vbox.setAlignment(Pos.CENTER);
            Scene scene = new Scene(vbox, 800, 500);
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void main(String args[]) { launch(args);}
    public static void saveFile(File[] file){
        String s=FilenameUtils.getExtension(file[0].getAbsolutePath());

        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm_ss");
        LocalDateTime now = LocalDateTime.now();
        String dateTimeString = now.format(formatter);
        File newFile=new File("C:\\OOP\\"+dateTimeString+"."+s);
        try {
            Files.copy(file[0].toPath(),newFile.toPath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

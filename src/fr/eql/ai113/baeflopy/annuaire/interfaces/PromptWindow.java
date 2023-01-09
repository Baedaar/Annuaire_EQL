package fr.eql.ai113.baeflopy.annuaire.interfaces;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PromptWindow {

    private Button okBtn = new Button("OK");
    private Label label = new Label();
    private Stage stage = new Stage();

    public Stage errorWindow(String title, String message){
        label.setText(message);
        okBtn.setMinSize(80,20);

        VBox labelVbox = new VBox();
        labelVbox.getChildren().add(label);
        labelVbox.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(25);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(30));
        stage.setTitle(title);
        vBox.getChildren().addAll(labelVbox,okBtn);
        stage.getIcons().add(new javafx.scene.image.Image("icon.png"));

        Scene scene = new Scene(vBox);
        stage.setScene(scene);

        scene.getStylesheets().add(getClass().getResource("Style.css").toExternalForm());
        vBox.setId("Vboxtable");
        okBtn.setId("CancelBtn");
        label.setId("Label");

        okBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closeWindow();
            }
        });

        return stage;
    }

    private void closeWindow(){
        stage.hide();
    }

}

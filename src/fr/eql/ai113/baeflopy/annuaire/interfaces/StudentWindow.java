package fr.eql.ai113.baeflopy.annuaire.interfaces;

import fr.eql.ai113.baeflopy.annuaire.application.Student;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


public class StudentWindow {
    Button okBtn = new Button();
    TextField nameText = new TextField("");
    TextField firstNameText = new TextField("");
    TextField yearText = new TextField("");
    TextField formationText = new TextField("");
    TextField dptText = new TextField("");

    Stage stagebis = new Stage();


    public Stage studentWindow(String type,String fenetre, String valider, Student student) {
        // Création d'une instance du conteneur GridPane
        GridPane studentbox = new GridPane();
        studentbox.setId("StudentGridPane");

        // Centrer le conteneur sur l'écran, verticalement et horizontalement
        studentbox.setAlignment(Pos.CENTER);

        // Met un padding
        studentbox.setPadding(new Insets(40, 40, 40, 40));

        // Set des Label et Textfield
        Label name = new Label("Nom : \t");
        name.setId("Label");
        nameText = new TextField(student.getName());
        Label firstname = new Label("Prénom : \t");
        firstname.setId("Label");
        firstNameText = new TextField(student.getFirstName());
        Label year = new Label("Année : \t");
        year.setId("Label");
        yearText = new TextField(student.getYear());
        Label formation = new Label("Formation : \t");
        formation.setId("Label");
        formationText = new TextField(student.getFormation());
        Label dpt = new Label("Département : \t");
        dpt.setId("Label");
        dptText = new TextField(student.getDepartment());

        // Création de boutons et d'une Hbox les contenants.
        okBtn = new Button(valider);
        okBtn.setId("OkBtn");
        Button cancelBtn = new Button("Annuler");
        cancelBtn.setId("CancelBtn");
        HBox btnBox = new HBox(okBtn,cancelBtn);
        btnBox.setSpacing(20);

        // Création de la scene et paramétrage du nom ainsi que specification de l'utilisation de cette Scene.
        Scene sceneBis = new Scene(studentbox);
        stagebis.setTitle(fenetre);
        stagebis.setScene(sceneBis);
        stagebis.getIcons().add(new javafx.scene.image.Image("icon.png"));

        // Remplissage de la GridPane en fonction du type de la fenêtre.
        switch (type) {
            case "FullWindow" :
                studentbox.add(name,0,0);
                studentbox.add(nameText,1,0);
                studentbox.add(firstname,0,1);
                studentbox.add(firstNameText,1,1);
                studentbox.add(year,0,2);
                studentbox.add(yearText,1,2);
                studentbox.add(formation,0,3);
                studentbox.add(formationText,1,3);
                studentbox.add(dpt,0,4);
                studentbox.add(dptText,1,4);
                studentbox.add(btnBox,1,6);
                stagebis.setMaxHeight(350);
                stagebis.setMaxWidth(500);
                stagebis.setMinHeight(350);
                stagebis.setMinWidth(500);
                break;
            case "SearchWindow" :
                studentbox.add(name,0,3);
                studentbox.add(nameText,1,3);
                studentbox.add(btnBox,1,6);
                stagebis.setMaxHeight(250);
                stagebis.setMaxWidth(500);
                stagebis.setMinHeight(250);
                stagebis.setMinWidth(400);
                break;
            default:
        }



        // On paramètre la taille entre les colonnes de la GridPane
        studentbox.setVgap(10);

        // Lien entre la scene et le fichier CSS.
        sceneBis.getStylesheets().add(getClass().getResource("Style.css").toExternalForm());
        studentbox.setId("Vboxtable");



        // Gestion du clic sur le menu "Annuler" > fermer la fenêtre
        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closeWindow();            }
        });

        return stagebis;
    }

    public void closeWindow(){
        stagebis.hide();
    }
}

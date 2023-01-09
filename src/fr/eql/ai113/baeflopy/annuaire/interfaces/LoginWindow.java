package fr.eql.ai113.baeflopy.annuaire.interfaces;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class LoginWindow {

    // Création des boutons
    public Button okBtn = new Button();
    private Button cancelBtn = new Button("Annuler");

    // Création des TextFields
    public TextField loginTxt = new TextField();
    public PasswordField passwordPswdField = new PasswordField();
    public PasswordField newPasswordField = new PasswordField();

    // Création du Stage
    private Stage stage = new Stage();

    // Création d'une checkbox
    public CheckBox superAdminChkBox = new CheckBox("Super Admin");

    private Logger logger = LogManager.getLogger();


    public Stage loginWindow(String type, String windowName, String okBtnTxt){

        // Affectation du nom du bouton ok
        okBtn.setText(okBtnTxt);

        // Création des labels
        Label loginLbl = new Label("Login : ");
        Label passwordLbl = new Label("Password : ");
        Label newPasswordLbl = new Label("Nouveau Password : ");

        // HBox pour les boutons
        HBox btnHBox = new HBox(30);
        btnHBox.getChildren().addAll(okBtn,cancelBtn);
        btnHBox.setAlignment(Pos.CENTER);

        // Création de la HBox qui accueillera la Grid Pane
        HBox gridHBox = new HBox();
        // Création de la GridPane
        GridPane mainGridPane = new GridPane();
        mainGridPane.setHgap(10);
        mainGridPane.setVgap(10);

        // Remplissage de la GridPane en fonction de ce qui a appelé l'affichage de LoginWindow
        switch (type){
            case "connexion":
                logger.info("Ouverture de la fenêtre de connexion");
                // Ajout des éléments dans la GridPane
                mainGridPane.add(loginLbl,1,1);
                mainGridPane.add(loginTxt,2,1);
                mainGridPane.add(passwordLbl,1,2);
                mainGridPane.add(passwordPswdField,2,2);
                mainGridPane.add(superAdminChkBox,2,3);
                break;
            case "addAdmin":
                logger.info("Ouverture de la fenêtre d'ajout admin");
                // Ajout des éléments dans la GridPane
                mainGridPane.add(loginLbl,1,1);
                mainGridPane.add(loginTxt,2,1);
                mainGridPane.add(passwordLbl,1,2);
                mainGridPane.add(passwordPswdField,2,2);
                break;
            case "deleteAdmin":
                logger.info("Ouverture de la fenêtre de suppression admin");
                // Ajout des éléments dans la GridPane
                mainGridPane.add(loginLbl,1,1);
                mainGridPane.add(loginTxt,2,1);
                break;
            case "modifyCredential":
                logger.info("Ouverture de la fenêtre de modification d'identifiants");
                // Ajout des éléments dans la GridPane
                mainGridPane.add(loginLbl,1,1);
                mainGridPane.add(loginTxt,2,1);
                mainGridPane.add(passwordLbl,1,2);
                mainGridPane.add(passwordPswdField,2,2);
                mainGridPane.add(newPasswordLbl,1,3);
                mainGridPane.add(newPasswordField,2,3);
                break;
            default:
        }

        // Ajout de la GridPane déterminée dans le switch
        gridHBox.getChildren().addAll(mainGridPane);
        gridHBox.setAlignment(Pos.CENTER);

        VBox mainVBox = new VBox(30);
        mainVBox.getChildren().addAll(gridHBox,btnHBox);
        mainVBox.setAlignment(Pos.CENTER);


        Scene scene = new Scene(mainVBox);
        stage.setTitle(windowName);
        stage.setScene(scene);
        stage.getIcons().add(new javafx.scene.image.Image("icon.png"));
        // Définition de la taille min et max de la fenêtre
        stage.setMaxHeight(300);
        stage.setMinHeight(300);
        stage.setMaxWidth(380);
        stage.setMinWidth(380);

        scene.getStylesheets().add(getClass().getResource("Style.css").toExternalForm());
        mainVBox.setId("Vboxtable");
        okBtn.setId("CancelBtn");
        cancelBtn.setId("CancelBtn");
        loginLbl.setId("Label");
        passwordLbl.setId("Label");
        newPasswordLbl.setId("Label");
        superAdminChkBox.setId("Label");

        // Gestion du clic sur le bouton "Annuler"
        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closeWindow();
            }
        });


        return stage;
    }

    public void closeWindow(){
        stage.hide();
    }
}

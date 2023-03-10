package fr.eql.ai113.baeflopy.annuaire.interfaces;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import fr.eql.ai113.baeflopy.annuaire.application.AdminGestion;
import fr.eql.ai113.baeflopy.annuaire.application.AnnuaireGestion;
import fr.eql.ai113.baeflopy.annuaire.application.Student;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainInterface extends Application {

    private final AnnuaireGestion annuaire = new AnnuaireGestion();
    private final AdminGestion adminGestion = annuaire.getAdminGestion();
    private String filePath = "";
    private List<Student> students = new ArrayList<Student>();
    private static final String SE_CONNECTER = "Se connecter";
    private static final String SE_DECONNECTER = "Se d??connecter";
    private static final String INVITE = "Invit??";

    private boolean isSuperAdmin = false;
    private boolean isAdmin = false;

    // Creation des MenuItems du menu Edition
    MenuItem ajouterItem = new MenuItem("Ajouter un Stagiaire");
    MenuItem modifierItem = new MenuItem("Modifier un Stagiaire");
    MenuItem supprimerItem = new MenuItem("Supprimer un Stagiaire");

    // Creation des MenuItems du menu Gestion
    MenuItem connexionMenuItem = new MenuItem(SE_CONNECTER);
    MenuItem addAdminItem = new MenuItem("Ajouter un Administrateur");
    MenuItem modifyAdminItem = new MenuItem("Modifier un Administrateur");
    MenuItem deleteAdminItem = new MenuItem("Supprimer un Administrateur");
    MenuItem modifyCredential = new MenuItem("Modifier mes identifiants");

    // Creation du MenuItem qui sert ?? afficher si la personne connect??e est admin
    Menu adminState = new Menu();

    // Cr??ation de MenuBar
    MenuBar menuBar = new MenuBar();
    MenuBar rightBar = new MenuBar();

    // Creation des menus
    Menu fichierMenu = new Menu("Fichier");
    Menu editionMenu = new Menu("Edition");
    Menu aideMenu = new Menu("Aide");
    Menu rechercheMenu = new Menu("Recherche");

    // Partie droite de la menuBar
    Menu userNameDisplay = new Menu(INVITE);
    Menu connexionMenu = new Menu("Gestion des Comptes");

    static final Logger logger = LogManager.getLogger();

    @Override
    public void start(Stage mainStage) throws Exception {

        switchToGuestMode();

        // Cr??ation d'un s??parateur pour le menu Fichier
        SeparatorMenuItem separatorFichier = new SeparatorMenuItem();

        // Creation des MenuItems du menu Fichier
        MenuItem importTxtItem = new MenuItem("Importer un Fichier .txt");
        MenuItem supprimerBinItem = new MenuItem("Supprimer l'annuaire");
        MenuItem exportpdfItem = new MenuItem("Exporter Fichier Pdf");
        MenuItem quitterItem = new MenuItem("Quitter");

        // Creation des MenuItems du menu Aide
        MenuItem telechargerdocItem = new MenuItem("Afficher la doc");
//        MenuItem testImprim = new MenuItem();

        // Creation des MenuItems du menu Recherche
        MenuItem rechercheItem = new MenuItem("Faire une recherche");
        MenuItem reinitialiseItem = new MenuItem("R??initialiser le tableau");

        // Ajout des Menus g??n??riques aux menuBars
        rightBar.getMenus().addAll(userNameDisplay,adminState,connexionMenu);

        // Ajouter les menuItems aux Menus g??n??riques
        fichierMenu.getItems().addAll(importTxtItem,supprimerBinItem,exportpdfItem,separatorFichier,quitterItem);
        editionMenu.getItems().addAll(ajouterItem,modifierItem,supprimerItem);
        rechercheMenu.getItems().addAll(rechercheItem,reinitialiseItem);
        aideMenu.getItems().addAll(telechargerdocItem);

        // Ajout d'une Region qui permet de s??parer les 2 parties du menuBar
        Region spacer = new Region();
        spacer.getStyleClass().add("menu-bar");
        HBox.setHgrow(spacer, Priority.SOMETIMES);
        HBox menubars = new HBox(menuBar, spacer, rightBar);


        // Ajout des raccourcis clavier ("Shortcut" devient "Ctrl" sur Windows, et "Meta" sur MacOS)
        importTxtItem.setAccelerator(KeyCombination.keyCombination("Shortcut+O"));
        exportpdfItem.setAccelerator(KeyCombination.keyCombination("Shortcut+S"));
        quitterItem.setAccelerator(KeyCombination.keyCombination("Shortcut+Q"));
        rechercheItem.setAccelerator(KeyCombination.keyCombination("Shortcut+F"));
        reinitialiseItem.setAccelerator(KeyCombination.keyCombination("Shortcut+R"));
        ajouterItem.setAccelerator(KeyCombination.keyCombination("Shortcut+1"));
        modifierItem.setAccelerator(KeyCombination.keyCombination("Shortcut+2"));
        supprimerItem.setAccelerator(KeyCombination.keyCombination("Shortcut+3"));
        telechargerdocItem.setAccelerator(KeyCombination.keyCombination("F1"));
        supprimerBinItem.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+Delete"));


        //Cr??ation de la TableView
        TableView<Student> table = new TableView<Student>();
        table.setEditable(true);

        //Cr??ation des cinq colonnes de la table
        TableColumn promoCol = createTableColumn("Ann??e","year");
        TableColumn nomCol = createTableColumn("Nom","name");
        TableColumn prenomCol = createTableColumn("Pr??nom","firstName");
        TableColumn forCol = createTableColumn("Formation","formation");
        TableColumn dptCol = createTableColumn("D??partement","department");

        //On bind la table sur la taille du mainStage
        table.prefHeightProperty().bind(mainStage.heightProperty());
        table.prefWidthProperty().bind(mainStage.widthProperty());

        //On ajoute les cinq colonnes ?? la table
        table.getColumns().addAll(nomCol,prenomCol,promoCol,forCol,dptCol);
        // Permet au tableau de s'??tendre lorsqu'on modifie la taille de la fen??tre
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // On remplit la table students avec l'annuaire
        annuaire.importAnnuaire(students);
        table.getItems().addAll(students);

        //On place le menuBar et la table dans une GridPane
        VBox vBoxGlobal = new VBox();
        VBox vBoxMenu = new VBox();
        VBox vBoxTable = new VBox();
        vBoxTable.setPadding(new Insets(30));
        vBoxMenu.getChildren().add(menubars);
        vBoxTable.getChildren().add(table);
        vBoxGlobal.getChildren().addAll(vBoxMenu, vBoxTable);
        vBoxGlobal.setMinSize(1200, 800);

        //On set vBoxGlobal sur la mainStage
        Scene scene = new Scene(vBoxGlobal);
        scene.getStylesheets().add(getClass().getResource("Style.css").toExternalForm());
        mainStage.setTitle("Annuaire");
        mainStage.setScene(scene);
        mainStage.show();
        mainStage.getIcons().add(new javafx.scene.image.Image("icon.png"));

        // style de css du menuBar
        fichierMenu.setId("menu");
        editionMenu.setId("menu");
        aideMenu.setId("menu");
        userNameDisplay.setId("menu");
        adminState.setId("menu");
        connexionMenu.setId("menu");
        editionMenu.setId("menu");
        fichierMenu.setId("menu");
        rechercheMenu.setId("menu");
        userNameDisplay.setDisable(true);
        adminState.setDisable(true);
        // style de css des MenuItems du Menu Edition
            // Dans le menu Fichier
        importTxtItem.setId("menuitem");
        supprimerBinItem.setId("menuitem");
        exportpdfItem.setId("menuitem");
        quitterItem.setId("menuitem");
            // Dans le Menu Edition
        ajouterItem.setId("menuitem");
        modifierItem.setId("menuitem");
        supprimerItem.setId("menuitem");
            // Dans le menu Recherche
        rechercheItem.setId("menuitem");
        reinitialiseItem.setId("menuitem");
            // Dans le menu Aide
        telechargerdocItem.setId("menuitem");
//        testImprim.setId("menuitem");
            // Dans le menu Gestion des comptes
        connexionMenuItem.setId("menuitem");
        addAdminItem.setId("menuitem");
        modifyAdminItem.setId("menuitem");
        deleteAdminItem.setId("menuitem");
        modifyCredential.setId("menuitem");
            // Style des fen??tres
        table.setId("table");
        vBoxTable.setId("Vboxtable");



        /*                       */
        /*   Gestion des clics   */
        /*     sur les menus     */
        /*                       */

        // Gestion du clic sur "Importer un Fichier .txt"
        importTxtItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                logger.info("Clic sur le menu " + importTxtItem.getText());
                // Creation du FileChooser pour aller chercher un fichier .txt
                FileChooser fileChooser = new FileChooser();
                // Ouverture d'une fen??tre
                File selectFile = fileChooser.showOpenDialog(mainStage);
                // On r??cup??re le chemin du fichier s??lectionn??
                filePath = selectFile.getPath();
                // On cr??e le fichier binaire ?? partir de celui-ci
                annuaire.createBinFile(filePath);
                students.clear();
                // On importe le fichier binaire pour afficher dans la table
                annuaire.importAnnuaire(students);
                table.getItems().clear();
                table.getItems().addAll(students);
            }
        });

        // Gestion du clic sur le MenuItem "exporter PDF"
        exportpdfItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                exportPdf();
            }
        });


        supprimerBinItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                annuaire.deleteBinFile();
                resetTableView(table);
            }
        });



        // Gestion du clic sur le menu "Quitter"
        quitterItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                logger.info("Clic sur le menu " + quitterItem.getText());
                Platform.exit();
            }
        });

        // Gestion du clic sur "Ajouter un Stagiaire"
        ajouterItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                logger.info("Clic sur le menu " + ajouterItem.getText());
                // Appel de la fen??tre d'ajout ??tudiant
                StudentWindow addStudent =
                        displayStudentWindow(
                               "FullWindow", mainStage, "Ajouter un stagiaire", "Ajouter", new Student()
                        );

                addStudent.okBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        // Cr??ation du stagiaire et ajout dans le fichier .bin
                        createStudentFromWindow(addStudent);
                        // Mise ?? jour de la TableView
                        resetTableView(table);
                        // On ferme la fen??tre StudentWindow
                        addStudent.closeWindow();
                    }
                });
            }
        });

        // Gestion du clic sur "Modifier le Stagiaire"
        modifierItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                logger.info("Clic sur le menu " + modifierItem.getText());
                // R??cup??ration des infos de l'??tudiant au moment du clic
                Student oldStudent = table.getSelectionModel().getSelectedItem();
                if (oldStudent != null){
                    // Appel de la fen??tre d'ajout ??tudiant
                    StudentWindow changeStudent =
                            displayStudentWindow(
                                    "FullWindow",mainStage, "Modifier un Stagiaire","Modifier", oldStudent);
                    // Gestion du clic sur le bouton Valider
                    changeStudent.okBtn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                                // Suppression de l'??tudiant (et remplacement des parents/enfants etc.)
                                annuaire.delete(oldStudent);
                                // Cr??ation du stagiaire et ajout dans le fichier .bin
                                createStudentFromWindow(changeStudent);
                                // Mise ?? jour de la TableView
                                resetTableView(table);
                                // On ferme la fen??tre StudentWindow
                                changeStudent.closeWindow();
                        }
                    });
                }
            }
        });

        // Selection de Student pour la fonction Supprimer un student
        supprimerItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                logger.info("Clic sur SUPPRIMER STAGIAIRE");
                // R??cup??ration des infos de l'??tudiant au moment du clic
                Student student = table.getSelectionModel().getSelectedItem();
                if (student != null){
                    // Suppression de l'??tudiant (et remplacement des parents/enfants etc.)
                    annuaire.delete(student);
                    // Mise ?? jour de la TableView
                    resetTableView(table);
                }
            }
        });

        rechercheItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                logger.info("Clic sur RECHERCHER");
                // Appel de la fen??tre d'ajout ??tudiant
                StudentWindow searchStudent =
                        displayStudentWindow(
                                "FullWindow",mainStage, "Rechercher un stagiaire", "Rechercher", new Student()
                        );
                searchStudent.okBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        List<Student> listSave = new ArrayList<Student>();

                        if (!students.isEmpty()) {
                            // Cr??ation du String pour la recherche
                            String searchedStudentString = searchedStudentToString(searchStudent);
                            logger.info("Stagiaire recherch?? : " + searchedStudentString);
                            for (int i = 0; i < students.size(); i++) {
                                // Si le nom dans la liste correspond au nom recherch??
//                                if ((students.get(i).getName().trim()).equalsIgnoreCase(searchStudent.nameText.getText().trim())) {
//                                    listSave.add(students.get(i));
//                                    searchStudent.closeWindow();
//                                }

                                String studentFromListStudents = studentFromListToString(searchStudent, students.get(i));
                                if (searchedStudentString.equalsIgnoreCase(studentFromListStudents)){
                                    listSave.add(students.get(i));
                                    logger.info("Stagiaire trouv?? : " + students.get(i).toString());
                                }
                                searchStudent.closeWindow();
                            }
                        }
                        if (!listSave.isEmpty()) {
                            students = listSave;
                            table.getItems().clear();
                            table.getItems().addAll(students);
                        } else {
                            displayPromptWindow("Erreur",mainStage,"Aucun stagiaire trouv?? ?? ce nom.");
                        }
                    }
                });
            }
        });

        reinitialiseItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                resetTableView(table);
            }
        });

        telechargerdocItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                logger.info("Clic sur AFFICHER LA DOC");
                annuaire.openUserDoc();
            }
        });


        // Petite fonction qui permet d'afficher en console les informations d'un stagiaire s??lectionn?? dans l'app
//        testImprim.setAccelerator(KeyCombination.keyCombination("Shortcut+P"));
//        testImprim.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                Student student = table.getSelectionModel().getSelectedItem();
//                try {
//                    annuaire.infosParentEnfant(student);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });

        // Gestion du clic sur le bouton Connexion
        connexionMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Si aucun utilisateur n'est connect??
                if (!isAdmin){
                    logger.info("Clic sur le menu " + connexionMenuItem.getText());
                    // Appel de la fen??tre de connexion
                    LoginWindow loginWindow = displayLoginWindow("connexion",
                            mainStage,"Connexion", "Se Connecter");
                    // Gestion du clic sur le bouton "Valider" de la fen??tre de connexion
                    loginWindow.okBtn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            checkCredentials(mainStage,loginWindow);
                        }
                    });
                // Si un utilisateur ??tait connect??, le bouton est donc SE_DECONNECTER, et on remet le statut invit??
                } else {
                    logger.info("D??connexion de " + userNameDisplay.getText());
                    userNameDisplay.setText(INVITE);
                    switchToGuestMode();
                }
            }
        });



        addAdminItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Appel de la fen??tre de connexion
                LoginWindow loginWindow = displayLoginWindow("addAdmin",
                        mainStage,"Cr??er un Administrateur", "Cr??er");
                // Gestion du clic sur le okBtn de la fen??tre
                loginWindow.okBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        // R??cup??ration de l'identifiant et du mdp ??crits dans la fen??tre de connexion
                        String id = loginWindow.loginTxt.getText();
                        String password = loginWindow.passwordPswdField.getText();
                        // Si les champs ne sont pas vides
                        if (!(id.equals("") || password.equals(""))){
                            adminGestion.creationAdmin(id,password);
                            loginWindow.closeWindow();
                        } else {
                                displayPromptWindow("Erreur",mainStage,
                                        "Veuillez entrer un login et un mot de passe.");
                        }
                    }
                });
            }
        });

        deleteAdminItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                LoginWindow deleteAdminWindow = displayLoginWindow("deleteAdmin",
                        mainStage,"Supprimer un Administrateur","Supprimer");
                deleteAdminWindow.okBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            String adminName = deleteAdminWindow.loginTxt.getText();
                            boolean isDeleted = adminGestion.deleteAdmin(adminName);
                            if (isDeleted){
                                displayPromptWindow("Effacement",mainStage,
                                        "L'administrateur " + adminName + " a bien ??t?? supprim??.");
                                deleteAdminWindow.closeWindow();
                            } else {
                                displayPromptWindow("Effacement", mainStage,
                                        "L'administrateur " + adminName + " n'existe pas.");
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });

        modifyCredential.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                LoginWindow modifyCredentialWindow = displayLoginWindow("modifyCredential",
                        mainStage,"Modifier vos identifiants","Modifier");
            }
        });
    }

    /*          */
    /* M??thodes */
    /*          */

    private TableColumn createTableColumn(String columnName, String studentAttribute){
        TableColumn<Student, String> tableColumn =
                new TableColumn<Student, String>(columnName);
        tableColumn.setMinWidth(100);
        //specifier un "cell factory" pour cette colonne.
        tableColumn.setCellValueFactory(
                new PropertyValueFactory<Student, String>(studentAttribute));

        return tableColumn;
    }

    private PromptWindow displayPromptWindow(String title, Stage stage, String message){
        // Appel de la fen??tre d'erreur
        PromptWindow promptWindow = new PromptWindow();
        Stage errorWin = promptWindow.errorWindow(title, message);
        // La fen??te de connexion doit rester au premier plan
        errorWin.initModality(Modality.APPLICATION_MODAL);
        errorWin.initOwner(stage);
        errorWin.show();
        return promptWindow;
    }

    private StudentWindow displayStudentWindow(String type,Stage stage, String windowName, String okBtnTxt, Student student){
        // Appel de la fen??tre de connexion
        StudentWindow studentWindow = new StudentWindow();
        Stage studentWin = studentWindow.studentWindow(type, windowName, okBtnTxt, student);
        // La fen??te de connexion doit rester au premier plan
        studentWin.initModality(Modality.APPLICATION_MODAL);
        studentWin.initOwner(stage);
        studentWin.show();
        return studentWindow;
    }

    private LoginWindow displayLoginWindow(String type, Stage stage, String windowName, String okBtnTxt){
        // Appel de la fen??tre de connexion
        LoginWindow loginWindow = new LoginWindow();
        Stage loginWin = loginWindow.loginWindow(type, windowName, okBtnTxt);
        // La fen??te de connexion doit rester au premier plan
        loginWin.initModality(Modality.APPLICATION_MODAL);
        loginWin.initOwner(stage);
        loginWin.show();
        return loginWindow;
    }

    private void checkCredentials(Stage stage, LoginWindow loginWindow){
        boolean credentialIsCorrect;
        String id;
        String password;
        // R??cup??ration de l'identifiant et du mdp ??crits dans la fen??tre de connexion
        boolean isSuperAdminChecked = loginWindow.superAdminChkBox.isSelected();
//                            logger.info("Checkbox Super Admin coch??e : " + isSuperAdminChecked);
        id = loginWindow.loginTxt.getText();
        password = loginWindow.passwordPswdField.getText();
        // Si les champs ne sont pas vides
        if (!(id.equals("") || password.equals(""))){
            // V??rifie sur la combinaison id/mdp est bonne
            credentialIsCorrect = adminGestion.isValidCredential(isSuperAdminChecked,id,password);
            // Si la combinaison est bonne
            if (credentialIsCorrect){
                // On affiche le nom de l'utilisateur ?? la place de "invit??"
                userNameDisplay.setText(id);
                if (isSuperAdminChecked) {
                    // On passe en mode super admin
                    switchToSuperAdminMode();
                    logger.info("Super admin " + id + " connect??");
                } else {
                    // Sinon on passe en mode admin
                    switchToAdminMode();
                    logger.info("Admin " + id + " connect??");
                }
                // On ferme la fen??tre de connexion
                loginWindow.closeWindow();
            } else {
                // Si la combinaison
                displayPromptWindow("Erreur",stage,
                        "Combinaison login et password incorrecte");
            }
            // Si au moins un des champs est vide
        } else {
            displayPromptWindow("Erreur",stage,
                    "Veuillez entrer un login et un mot de passe");
        }
    }

    private void resetTableView(TableView table){
        // Mise ?? jour de la TableView
        logger.info("R??initialisation du TableView");
        students.clear();
        annuaire.importAnnuaire(students);
        table.getItems().clear();
        table.getItems().addAll(students);
    }

    private void createStudentFromWindow(StudentWindow window){
        try {
            String name = window.nameText.getText();
            String firstName = window.firstNameText.getText();
            String year = window.yearText.getText();
            String formation = window.formationText.getText();
            String department = window.dptText.getText();
            // On trim() toutes les entr??es si besoin
            if (name != null){
                name = name.trim();
            } else {
                name = "";
            }
            if (firstName != null){
                firstName = firstName.trim();
            } else {
                firstName = "";
            }
            if (year != null){
                year = year.trim();
            } else {
                year = "";
            }
            if (formation != null){
                formation = formation.trim();
            } else {
                formation = "";
            }
            if (department != null){
                department = department.trim();
            } else {
                department = "";
            }
            // On cr??e le nouveau stagiaire avec les donn??es trim??es
            Student newStudent = new Student(name,firstName,year,formation,department,-1);
            // On ajoute le nouveau stagiaire au fichier .bin
            annuaire.transformStudentToBinAdd(newStudent);

        } catch (IOException e) {
            logger.warn("Erreur lors de la cr??ation d'un nouvel ??tudiant depuis l'interface", e);
        }
    }

    /**
     * Transforme les champs de recherche remplis dans une fen??tre StudentWindow afin d'avoir un String sp??cifique ?? la recherche
     * @param searchStudent Fen??tre de recherche depuis laquelle la recherche est effectu??e
     * @return Le String sp??cifique n??cessaire ?? la recherche
     */
    private String searchedStudentToString(StudentWindow searchStudent){
        String studentInString;

        // Cr??er un StringBuffer qui correspondra au stagiaire recherch??
        StringBuffer buffer = new StringBuffer();
        // Si le champ "Nom" est rempli
        if (searchStudent.nameText.getText() != null && !searchStudent.nameText.getText().isEmpty()){
            buffer.append(searchStudent.nameText.getText().trim());
        }
        // Si le champ "Pr??nom" est rempli
        if (searchStudent.firstNameText.getText() != null && !searchStudent.firstNameText.getText().isEmpty()){
            buffer.append(searchStudent.firstNameText.getText().trim());
        }
        // Si le champ "Ann??e" est rempli
        if (searchStudent.yearText.getText() != null && !searchStudent.yearText.getText().isEmpty()){
            buffer.append(searchStudent.yearText.getText().trim());
        }
        // Si le champ "Formation" est rempli
        if (searchStudent.formationText.getText() != null && !searchStudent.formationText.getText().isEmpty()){
            buffer.append(searchStudent.formationText.getText().trim());
        }
        // Si le champ "D??partement" est rempli
        if (searchStudent.dptText.getText() != null && !searchStudent.dptText.getText().isEmpty()){
            buffer.append(searchStudent.dptText.getText().trim());
        }
        studentInString = buffer.toString();
        return studentInString;
    }

    /**
     * Transforme un Student en String sp??cifique selon des crit??res de recherche renseign??s dans une StudentWindow
     * @param searchStudent Fen??tre StudentWindow dans laquelle la recherche est reseign??e
     * @param student Student ?? transformer en String
     * @return Le String sp??cifique n??cessaire ?? la recherche
     */
    private String studentFromListToString(StudentWindow searchStudent, Student student){
        // Cr??er un StringBuffer qui correspondra au stagiaire recherch??
        StringBuffer buffer = new StringBuffer();
        // Si le champ "Nom" est rempli
        if (searchStudent.nameText.getText() != null && !searchStudent.nameText.getText().isEmpty()){
            buffer.append(student.getName().trim());
        }
        // Si le champ "Pr??nom" est rempli
        if (searchStudent.firstNameText.getText() != null && !searchStudent.firstNameText.getText().isEmpty()){
            buffer.append(student.getFirstName().trim());
        }
        // Si le champ "Ann??e" est rempli
        if (searchStudent.yearText.getText() != null && !searchStudent.yearText.getText().isEmpty()){
            buffer.append(student.getYear().trim());
        }
        // Si le champ "Formation" est rempli
        if (searchStudent.formationText.getText() != null && !searchStudent.formationText.getText().isEmpty()){
            buffer.append(student.getFormation().trim());
        }
        // Si le champ "D??partement" est rempli
        if (searchStudent.dptText.getText() != null && !searchStudent.dptText.getText().isEmpty()){
            buffer.append(student.getDepartment().trim());
        }

        String studentFromStudentsInString = buffer.toString();
        return studentFromStudentsInString;
    }

    private void switchToAdminMode(){
        isSuperAdmin = false;
        isAdmin = true;
        // On affiche admin
        adminState.setText("ADMIN");
        // On change le menu en SE_DECONNECTER
        connexionMenuItem.setText(SE_DECONNECTER);
        // Mise ?? jour de la menuBar
        menuBar.getMenus().clear();
        menuBar.getMenus().addAll(fichierMenu,editionMenu,rechercheMenu,aideMenu);
    }

    private void switchToSuperAdminMode(){
        switchToAdminMode();
        // On indique que l'utilisateur connect?? est un admin
        isSuperAdmin = true;
        // On affiche super admin
        adminState.setText("SUPER ADMIN");
        // Mise ?? jour des menus de Gestion des comptes
        SeparatorMenuItem separatorAdmin = new SeparatorMenuItem();

        connexionMenu.getItems().clear();
        connexionMenu.getItems().addAll(
                connexionMenuItem,separatorAdmin,addAdminItem,deleteAdminItem);
    }

    private void switchToGuestMode(){

        connexionMenuItem.setText(SE_CONNECTER);
        // On enl??ve le statut admin
        adminState.setText("");
        isSuperAdmin = false;
        isAdmin = false;

        // Mise ?? jour des menus
        menuBar.getMenus().clear();
        menuBar.getMenus().addAll(fichierMenu,rechercheMenu,aideMenu);
        connexionMenu.getItems().clear();
        connexionMenu.getItems().add(connexionMenuItem);

    }

    private void addMetaData(Document document) {
        document.addTitle("My first PDF");
        document.addSubject("Using iText");
        document.addKeywords("Java, PDF, iText");
        document.addAuthor("Admin");
        document.addCreator("Admin");
    }

    private void addTitlePage(Document document) throws DocumentException {
        Paragraph titre = new Paragraph();
        Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
        // Ecrire un titre (test)
        titre.add(new Paragraph("Annuaire EQL\r\n",subFont));
        titre.setAlignment(String.valueOf(Pos.CENTER));
        document.add(titre);
    }

    public void exportPdf(){
        Document doc = new Document();
        FileChooser pdfPath = new FileChooser();
        // Set l'extension pdf
        pdfPath.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF",".pdf"));
        // Permet de cr??e un fichier
        File pdfFile = pdfPath.showSaveDialog(null);
        try {
            // D??crire le chemin du pdf
            PdfWriter.getInstance(doc, new FileOutputStream(pdfFile));
            // Toujours ouvrir le pdf avant d'??crire dedans
            doc.open();
            // Cr??er une M??ta pour d??finir le pdf
            addMetaData(doc);
            Image img = Image.getInstance("src/mylogo.PNG");
            img.scaleToFit(100,100);
            doc.add(img);
            // Ajoute un titre sur une Page vide
            addTitlePage(doc);
            // On cr??e la table avec 5 colonnes
            PdfPTable tableExport = new PdfPTable(5);
            // On d??finit la table avec une largeur de 100%
            tableExport.setWidthPercentage(100);
            // On cr??e une cellule
            PdfPCell cell;
            // On met les ??l??ments dans la cellule puis on l'ajoute au tableau
            fillPdfCell(tableExport,"Nom");
            fillPdfCell(tableExport,"Pr??nom");
            fillPdfCell(tableExport,"Ann??e");
            fillPdfCell(tableExport,"Formation");
            fillPdfCell(tableExport,"D??partement");

            // On boucle sur students
            for(Student student : students){
                // On r??cup??re le Nom puis on l'ajoute ?? la cellule puis on ajoute la cellule au tableau
                cell = new PdfPCell(new Phrase(String.valueOf((student.getName()))));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableExport.addCell(cell);

                // On r??cup??re le FirstName puis on l'ajoute ?? la cellule puis on ajoute la cellule au tableau
                cell = new PdfPCell(new Phrase(String.valueOf(student.getFirstName())));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableExport.addCell(cell);

                // On r??cup??re l'Ann??e puis on l'ajoute ?? la cellule puis on ajoute la cellule au tableau
                cell = new PdfPCell(new Phrase(String.valueOf(student.getYear())));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableExport.addCell(cell);

                // On r??cup??re la Formation puis on l'ajoute ?? la cellule puis on ajoute la cellule au tableau
                cell = new PdfPCell(new Phrase(String.valueOf(student.getFormation())));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableExport.addCell(cell);

                // On r??cup??re le D??partement puis on l'ajoute ?? la cellule puis on ajoute la cellule au tableau
                cell = new PdfPCell(new Phrase(String.valueOf(student.getDepartment())));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableExport.addCell(cell);

            }
            // On ajoute la table au doc
            doc.add(tableExport);
            // Toujours fermer le pdf
            doc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillPdfCell(PdfPTable tableExport, String cellName){
        PdfPCell cell = new PdfPCell(new Phrase(cellName));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderColor(BaseColor.BLACK);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPaddingTop(10);
        cell.setPaddingBottom(10);
        tableExport.addCell(cell);
    }

}

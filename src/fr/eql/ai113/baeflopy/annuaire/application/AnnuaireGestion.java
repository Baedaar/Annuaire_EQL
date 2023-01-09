package fr.eql.ai113.baeflopy.annuaire.application;

import  org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * Classe utilisée pour la création et modification de l'annuaire, à partir d'un fichier texte ou lors de l'utilisation
 * de l'application par l'utilisateur.
 * @author Florent & Baedaar & Pierre-Yves
 */
public class AnnuaireGestion {

    /*                  */
    /*    Attributs     */
    /*                  */
    private static final String FOLDER_FILES = "files/";
    private static final String BIN_OUT = "annuaire.bin";
    private static final String FOLDER_ADMIN = "admin/";
    private static final String SUPER_ADMIN = "super_admin";
    private static final String DOCUMENTATION = "Notice Utilisation Annuaire EQL.pdf";
    private static final int NAME_LENGTH = 20;
    private static final int FIRSTNAME_LENGTH = 20;
    private static final int YEAR_LENGTH = 4;
    private static final int FORMATION_LENGTH = 14;
    private static final int DEPARTMENT_LENGTH = 2;
    private static final int TO_LEFT_NOD_POS = NAME_LENGTH + FIRSTNAME_LENGTH
            + YEAR_LENGTH + FORMATION_LENGTH + DEPARTMENT_LENGTH + 2;
    private static final int TO_RIGHT_NOD_POS = TO_LEFT_NOD_POS + 8;
    private static final int TO_PARENT_NOD_POS = TO_RIGHT_NOD_POS + 8;
    private static final int NO_CHILD_NOD = 0;
    // Variable qui servira à passer une information de parent lors de diverse méthodes récursives
    private long parentNod;

    private final AdminGestion adminGestion = new AdminGestion(FOLDER_ADMIN, SUPER_ADMIN);

    private static final Logger logger = LogManager.getLogger();

    /*                  */
    /*   Constructeur   */
    /*                  */
    /**
     * Constructeur générique, qui à l'instanciation créera le nécessaire pour le super administrateur (si inexistant)
     */
    public AnnuaireGestion() {
        adminGestion.creationSuperAdmin();
    }


    /*                  */
    /*     Méthodes     */
    /*                  */
    /**
     * Crée le fichier .bin qui sera utilisé par l'application, à partir d'un fichier .txt renseigné au préalable.
     * @param txtFilePath Emplacement du fichier .txt qui servira à créer le fichier .bin
     *                    <p>La méthode ne fait rien si le chemin est vide
     */
    public void createBinFile(String txtFilePath) {
        logger.info("Entrée dans createBinFile");

        try {

            File folderFiles = new File(FOLDER_FILES);
            folderFiles.mkdir();

            // Création d'un String dans lequel on va mettre une ligne du fichier .txt lors de la lecture
            String currentLine;

            // RAF pour lire le fichier .txt d'origine
            RandomAccessFile raf_in = new RandomAccessFile(txtFilePath, "r");

            // Copie de chaque ligne dans une ligne de TAILLE_BLOC
            while ((currentLine = raf_in.readLine()) != null) {

                // Création des 5 variables qui prendront les informations souhaitées
                char[] name = new char[0];
                char[] firstName = new char[0];
                char[] year = new char[0];
                char[] formation = new char[0];
                char[] department = new char[0];


                if (!currentLine.equals("*")) {
                    // Si la ligne n'est pas une '*' : on boucle 5 fois pour obtenir les informations du stagiaire
                    for (int i = 0 ; i < 5 ; i++) {
                        // On inscrit les infos dans le .bin en fonction du nombre de fois qu'on a bouclé,
                        // car les infos sont toujours écrites dans le même ordre dans le fichier .txt
                        switch (i) {
                            case 0 :
                                formation = transformData(currentLine, FORMATION_LENGTH);
                                break;
                            case 1 :
                                year = transformData(currentLine, YEAR_LENGTH);
                                break;
                            case 2 :
                                name = transformData(currentLine, NAME_LENGTH);
                                break;
                            case 3 :
                                firstName = transformData(currentLine, FIRSTNAME_LENGTH);
                                break;
                            case 4 :
                                department = transformData(currentLine, DEPARTMENT_LENGTH);
                                break;
                            default:
                                logger.info("Entré dans le default du switch : pas normal");
                        }
                        // On regarde si la ligne suivante du fichier .txt n'est pas 'null'
                        if ((currentLine = raf_in.readLine()) == null) {
                            break;
                        }
                    }
                }

                // Écriture du stagiaire lu précédemment, avec les données dans le bon ordre, dans le fichier .bin
                addStudentToBin(name, firstName, year, formation, department);
            }

            raf_in.close();

        // Si erreur
        } catch (IOException e) {
            logger.warn("Erreur : ", e);
        }

        logger.info("Création du fichier " + BIN_OUT + " terminée.");
    }

    /**
     * Copie un String d'entrée en un charArray[] d'une longueur souhaitée.
     * <p>Si la taille du tableau de sortie est insuffisante, l'opération n'est pas effectuée.
     * <p>Les éventuels éléments restants du char[] de sortie resteront 'null'.
     * @param currentLine Le String à convertir
     * @param length Taille désirée du tableau de caractères de sortie
     * @return Un tableau de caractères rempli du String d'entrée, avec d'éventuels 'null' si le tableau est trop long
     */
    private char[] transformData(String currentLine, int length) {
        // On doit travailler avec 2 char[] pour pouvoir copier l'un dans l'autre
        char[] charArrayCurrentLine = currentLine.trim().toCharArray();
        char[] charArrayOutput = new char[length];
        // On ne copie que si le tableau de sortie est plus grand, pour éviter un OutOfBounds
        if (charArrayOutput.length >= charArrayCurrentLine.length) {
            // On recopie la charArrayCurrentLine dans le charArrayOutput qui servira à remplir le fichier .bin
            for (int i = 0 ; i < charArrayCurrentLine.length ; i++) {
                charArrayOutput[i] = charArrayCurrentLine[i];
            }
        }
        return charArrayOutput;
    }

    /**
     * Ajoute un Student au fichier .bin en procédant à la transformation nécessaire
     * @param student Student à ajouter
     * @throws IOException
     */
    public void transformStudentToBinAdd(Student student) throws IOException {
        char[] transformedName = new char[NAME_LENGTH];
        char[] transformedFirstName = new char[FIRSTNAME_LENGTH];
        char[] transformedYear = new char[YEAR_LENGTH];
        char[] transformedFormation = new char[FORMATION_LENGTH];
        char[] transformedDepartment = new char[DEPARTMENT_LENGTH];

        // Récupération des infos du Student d'entrée
        char[] studentNameChar = student.getName().toCharArray();
        char[] studentFirstNameChar = student.getFirstName().toCharArray();
        char[] studentYearChar = student.getYear().toCharArray();
        char[] studentFormationChar = student.getFormation().toCharArray();
        char[] studentDptChar = student.getDepartment().toCharArray();

        // Copie des informations du student au bon format pour l'ajout au fichier .bin
        for (int i = 0 ; i < studentNameChar.length ; i++){
            transformedName[i] = studentNameChar[i];
        }

        for (int i = 0; i < studentFirstNameChar.length; ++i) {
            transformedFirstName[i] = studentFirstNameChar[i];
        }

        for (int i = 0; i < studentYearChar.length; ++i) {
            transformedYear[i] = studentYearChar[i];
        }

        for (int i = 0; i < studentFormationChar.length; ++i) {
            transformedFormation[i] = studentFormationChar[i];
        }

        for (int i = 0; i < studentDptChar.length; ++i) {
            transformedDepartment[i] = studentDptChar[i];
        }

        // Ajout du stagiaire au fichier .bin, avec ajout de sa position dans l'objet Student
        student.setPosition(
                addStudentToBin(
                        transformedName,transformedFirstName,transformedYear,transformedFormation,transformedDepartment)
        );
    }

    /**
     * Ajouter un nouvel Etudiant au fichier .bin, en le plaçant correctement dans l'arbre binaire.
     * <p>Si le stagiaire existe déjà dans l'arbre (même année, même promo, etc.), alors il n'est pas ajouté.
     * @param name Nom du stagiaire
     * @param firstName Prénom du stagiaire
     * @param year Année de sa formation
     * @param formation Intitulé de sa formation
     * @param department Département où vit le stagiaire
     * @throws IOException Si erreur rencontrée en cours d'exécution, jette l'exception à la méthode appelante
     */
    public long addStudentToBin (char[] name, char[] firstName, char[] year, char[] formation, char[] department) throws IOException {
        // Création du RAF pour écrire dans le fichier .bin
        RandomAccessFile raf_out = new RandomAccessFile(FOLDER_FILES + BIN_OUT, "rw");
        // Création d'une variable newStudentPosition, qui permettra de donner la position du stagiaire qu'on est en train
        // d'ajouter à son parent
        long newStudentPosition = raf_out.length();
        // Placement du pointeur du RAF à la fin du fichier
        raf_out.seek(newStudentPosition);

        // Enregistrement de la ligne du nouvel étudiant pour comparer et placer dans l'arbre
        raf_out.seek(newStudentPosition);
        String newStudent = new String(String.valueOf(name)
                + String.valueOf(firstName)
                + String.valueOf(year)
                + String.valueOf(formation)
                + String.valueOf(department));

        if (newStudentPosition != 0) {
            // Mise à jour de l'arbre avec les coordonnées du nouvel étudiant, si ce n'est pas le premier
            boolean alreadyExists = searchTree(newStudent, 0);
            if (!alreadyExists) {
                // Ecriture du nouveau stagiaire à la fin du fichier, avec les données dans l'ordre
                raf_out.seek(newStudentPosition);
                raf_out.writeBytes(newStudent);
                // On ajoute 1 long pour la coordonnée de la nod gauche, puis droite
                raf_out.writeBytes("\r\n");
                raf_out.writeLong(NO_CHILD_NOD);
                raf_out.writeLong(NO_CHILD_NOD);
                raf_out.writeLong(parentNod);
                raf_out.writeBytes("\r\n");
            }

        } else {
            raf_out.writeBytes(newStudent);
            // On ajoute 1 long pour la coordonnée de la nod gauche, puis droite
            raf_out.writeBytes("\r\n");
            raf_out.writeLong(NO_CHILD_NOD);
            raf_out.writeLong(NO_CHILD_NOD);
            raf_out.writeLong(parentNod);
            raf_out.writeBytes("\r\n");
        }

        raf_out.close();
        return newStudentPosition;
    }

    /**
     * Cherche la position d'un élément dans l'arbre (fonction récursive)
     * @param newStudent String de la ligne entière qui correspond à un nouveau stagiaire
     * @param cursor Position où doit chercher la fonction
     * @throws IOException
     * @return Un boolean qui indique si l'étudiant est déjà présent dans l'arbre
     */
    private boolean searchTree(String newStudent, long cursor) throws IOException {
        boolean alreadyExists = false;

        if (newStudent.isEmpty()){

        } else {
            RandomAccessFile raf_tree = new RandomAccessFile(FOLDER_FILES + BIN_OUT, "rw");
            long newStudentPosition = raf_tree.length();
//        logger.info("Valeur de cursor au début : " + cursor);
            parentNod = cursor;

            // Lecture de l'étudiant à comparer
            raf_tree.seek(cursor);
            String currentStudent = raf_tree.readLine();
            // Si avant dans l'ordre alphabétique
            if (newStudent.compareTo(currentStudent) < 0) {
//            logger.info(newStudent + " est plus petit que " + currentStudent);
                long toLeftNodValue = getLeftChildPos(cursor);
                // Si pas d'enfant à gauche, on écrit la coordonnée du nouveau stagiaire au bon endroit dans son parent
                if (toLeftNodValue == NO_CHILD_NOD) {
                    raf_tree.seek(cursor + TO_LEFT_NOD_POS);
                    raf_tree.writeLong(newStudentPosition);
                } else {
                    alreadyExists = searchTree(newStudent, toLeftNodValue);
                }
                // Sinon, si après dans l'ordre alphabétique
            } else if (newStudent.compareTo(currentStudent) > 0) {
//            logger.info(newStudent + " est plus grand que " + currentStudent);
                long toRightNodValue = getRightChildPos(cursor);
                // Si pas d'enfant à droite, on écrit la coordonnée du nouveau stagiaire au bon endroit dans son parent
                if (toRightNodValue == NO_CHILD_NOD) {
                    raf_tree.seek(cursor + TO_RIGHT_NOD_POS);
                    raf_tree.writeLong(newStudentPosition);
                } else {
                    // S'il y a un enfant à droite, on lance la fonction en récursivité
                    alreadyExists = searchTree(newStudent, toRightNodValue);
                }
            } else if (newStudent.compareTo(currentStudent) == 0) {
//            logger.info(newStudent + " est égal à " + currentStudent);
                alreadyExists = true;
            }

            raf_tree.close();
        }
        return alreadyExists;
    }

    /**
     * Importe l'annuaire (fichier binaire) dans une List de Student
     * @param students La liste qu'on souhaite remplir
     */
    public void importAnnuaire(List<Student> students) {
        // Si le fichier .bin existe bien
        if (new File(FOLDER_FILES + BIN_OUT).exists()) {
            // On traverse l'arbre et on remplit la liste students avec le résultat du parcours
            logger.info("On va traverser l'annuaire en infixe");
            students = traverseInOrder(students, FOLDER_FILES + BIN_OUT,0);
            logger.info("Annuaire traversé, liste récupérée");
        } else {
            // Si le fichier existe, on met la liste dans le TableView
            logger.info("Pas de fichier " + BIN_OUT + ". Rien à afficher");
        }
    }


    /**
     * Traverse l'arbre binaire et renvoie une List de Student.
     * @param students Une List de type Student
     * @param filePath Chemin du fichier .bin où se trouve l'arbre binaire
     * @param currentNod Noeud actuel dans lequel se trouve la fonction
     * @return La liste d'entrée, remplie par le parcours de l'arbre
     */
    private List<Student> traverseInOrder(List<Student> students, String filePath, long currentNod){

        try {

            String currentLine;
            RandomAccessFile raf_inOrder = new RandomAccessFile(filePath, "r");
            raf_inOrder.seek(currentNod);
            if ((currentLine = raf_inOrder.readLine()) != null) {
                /*
                Aller à la nod gauche
                */
                long toLeftNod = getLeftChildPos(currentNod);
                // S'il y a un enfant gauche
                if (toLeftNod != 0) {
                    // On relance la fonction à gauche
                    traverseInOrder(students, filePath, toLeftNod);
                }

                /*
                Créer un Student
                */
                raf_inOrder.seek(currentNod);
                char[] currentStudentCharArray = raf_inOrder.readLine().toCharArray();
                Student student = createStudent(currentStudentCharArray, currentNod);
                students.add(student);

                /*
                Aller à la nod droite
                */
                long toRightNod = getRightChildPos(currentNod);
                // S'il y a un enfant droit
                if (toRightNod != 0) {
                    // On relance la fonction à droite
                    traverseInOrder(students, filePath, toRightNod);
                }
            }
            raf_inOrder.close();

        } catch (IOException e) {
            logger.warn("Erreur survenue lors de la lecture inOrder", e);
        }

        return students;
    }



    /**
     * Crée un objet Student à partir du noeud de l'arbre binaire sur lequel on se trouve
     * @param currentStudentCharArray Un tableau de caractères qui comprend tous les détails de
     *                                l'étudiant tel que définit dans AnnuaireGestion
     * @return Un Student complet
     */
    private Student createStudent(char[] currentStudentCharArray, long position) throws IOException {
        char[] nameChar = new char[NAME_LENGTH];
        char[] firstNameChar = new char[FIRSTNAME_LENGTH];
        char[] yearChar = new char[YEAR_LENGTH];
        char[] formationChar = new char[FORMATION_LENGTH];
        char[] departmentChar = new char[DEPARTMENT_LENGTH];

        int j = 0;

        // Création de l'attribut name
        for (int i = 0 ; i < NAME_LENGTH ; i++) {
            nameChar[j] = currentStudentCharArray[i];
            j++;
        }
        String name = String.valueOf(nameChar);
        j = 0;

        // Création de l'attribut firstName
        for (int i = NAME_LENGTH ; i < NAME_LENGTH + FIRSTNAME_LENGTH ; i++) {
            firstNameChar[j] = currentStudentCharArray[i];
            j++;
        }
        String firstName = String.valueOf(firstNameChar);
        j = 0;

        // Création de l'attribut year
        for (int i = NAME_LENGTH + FIRSTNAME_LENGTH ; i < NAME_LENGTH + FIRSTNAME_LENGTH + YEAR_LENGTH; i++) {
            yearChar[j] = currentStudentCharArray[i];
            j++;
        }
        String year = String.valueOf(yearChar);
        j = 0;

        // Création de l'attribut formation
        for (int i = NAME_LENGTH + FIRSTNAME_LENGTH + YEAR_LENGTH ;
             i < NAME_LENGTH + FIRSTNAME_LENGTH + YEAR_LENGTH + FORMATION_LENGTH; i++) {
            formationChar[j] = currentStudentCharArray[i];
            j++;
        }
        String formation = String.valueOf(formationChar);
        j = 0;

        // Création de l'attribut department
        for (int i = NAME_LENGTH + FIRSTNAME_LENGTH + YEAR_LENGTH + FORMATION_LENGTH ;
             i < NAME_LENGTH + FIRSTNAME_LENGTH + YEAR_LENGTH + FORMATION_LENGTH + DEPARTMENT_LENGTH; i++) {
            departmentChar[j] = currentStudentCharArray[i];
            j++;
        }
        String department = String.valueOf(departmentChar);


        // Création de l'étudiant à partir des données récoltées
        Student student = new Student(name,firstName,year,formation,department, position);
//        logger.info("Etudiant créé : " + student.toString() +
//                "\r\n. Position : " + student.getPosition() + ". Position parent : " + getParentPos(student.getPosition()));

        return student;
    }

    /**
     * Supprime un stagiaire sélectionné dans l'interface
     * @param deletedStudent Stagiaire sélectionné
     * @throws IOException
     */
    public void delete(Student deletedStudent){
        try {

            boolean deletedStudentExist = false;

                deletedStudentExist = searchTree(deletedStudent.toString(), NO_CHILD_NOD);

            logger.info("Etudiant trouvé : " + deletedStudentExist);
            long deletedStudentPosition = deletedStudent.getPosition();
            long deletedStudentParentPosition = getParentPos(deletedStudentPosition);
            // On recherche l'héritier du stagiaire supprimé
            long heirPosition = searchHeir(deletedStudent.getPosition());
            // On sauvegarde l'enfant gauche et le parent actuels de l'héritier
            long heirSavedLeftChild = getLeftChildPos(heirPosition);
            long heirSavedParent = getParentPos(heirPosition);

            if (deletedStudentExist){
                // Si l'étudiant supprimé se trouve directement à gauche de son parent
                if (deletedStudentPosition == getLeftChildPos(deletedStudentParentPosition)){
                    // Si le stagiaire supprimé n'a pas d'enfant
                    if ((getLeftChildPos(deletedStudentPosition) == NO_CHILD_NOD)
                            && (getRightChildPos(deletedStudentPosition) == NO_CHILD_NOD)){
                        // On remplace l'enfant gauche du parent du stagiaire supprimé par NO_CHILD_NOD
                        replaceLeftChild(deletedStudentParentPosition,NO_CHILD_NOD);
                        return;
                    // S'il y a un héritier
                    } else if (heirPosition != NO_CHILD_NOD) {
                        // On remplace l'enfant gauche du parent du stagiaire supprimé par la position de l'héritier
                        replaceLeftChild(deletedStudentParentPosition, heirPosition);
                    // S'il n'y a pas d'héritier et que le stagiaire supprimé n'est pas la racine de l'arbre
                    // Le stagiaire supprimé a donc un enfant droit
                    } else if (deletedStudentPosition != NO_CHILD_NOD) {
                        // On remplace l'enfant gauche du parent du stagiaire supprimé par l'enfant droit de celui-ci
                        replaceLeftChild(deletedStudentParentPosition,getRightChildPos(deletedStudentPosition));
                    }
                // Si le stagiaire supprimé se trouve directement à droite de son parent
                } else if (deletedStudentPosition == getRightChildPos(deletedStudentParentPosition)){
                    // Si le stagiaire supprimé n'a pas d'enfant
                    if ((getLeftChildPos(deletedStudentPosition) == NO_CHILD_NOD)
                            && (getRightChildPos(deletedStudentPosition) == NO_CHILD_NOD)){
                        // On remplace l'enfant droit du parent du stagiaire supprimé par NO_CHILD_NOD
                        replaceRightChild(deletedStudentParentPosition, NO_CHILD_NOD);
                        return;
                    // S'il a un héritier
                    } else if (heirPosition != NO_CHILD_NOD) {
                        // On remplace l'enfant droit du parent du stagiaire supprimé par la position de l'héritier
                        replaceRightChild(deletedStudentParentPosition, heirPosition);
                    // S'il n'y a pas d'héritier et que le stagiaire supprimé n'est pas la racine de l'arbre
                    // Le stagiaire supprimé a donc un enfant droit
                    } else if (deletedStudentPosition != NO_CHILD_NOD) {
                        // On remplace l'enfant droit du parent du stagiaire supprimé par l'enfant de celui-ci
                        replaceRightChild(deletedStudentParentPosition,getRightChildPos(deletedStudentPosition));
                    }
                }

                if (heirPosition != NO_CHILD_NOD) {
                    // On remplace l'enfant droit de l'héritier par l'enfant droit du stagiaire supprimé
                    replaceRightChild(heirPosition, getRightChildPos(deletedStudentPosition));

                    // Si l'héritier n'est pas directement à gauche du stagiaire supprimé
                    if (getLeftChildPos(deletedStudentPosition) != heirPosition){
                        // On remplace l'enfant gauche de l'héritier par l'enfant gauche du stagiaire supprimé
                        replaceLeftChild(heirPosition, getLeftChildPos(deletedStudentPosition));
                        // On remplace l'enfant droit de l'ancien parent de l'héritier par l'ancien enfant gauche de celui-ci
                        replaceRightChild(heirSavedParent, heirSavedLeftChild);
                    }
                }

            }
        } catch (IOException e) {
            logger.warn("Erreur lors de l'effacement d'un stagiaire", e);
        }
    }

    /**
     * Trouve l'héritier à l'étudiant qu'on est en train de supprimer
     * @param deletedStudent L'étudiant supprimé
     * @return La position de l'héritier
     * @throws IOException
     */
    private long searchHeir(long deletedStudent) throws IOException {
        long heirPosition = NO_CHILD_NOD;
        long deletedStudentLeftChild = getLeftChildPos(deletedStudent);
//        logger.info("LeftChild de " + deletedStudent + " est " + deletedStudentLeftChild);
        // Si le stagiaire supprimé a un enfant gauche
        if (deletedStudentLeftChild != NO_CHILD_NOD) {
            heirPosition = deletedStudentLeftChild;
            // Si cet enfant gauche a lui-même un enfant droit
            if (getRightChildPos(deletedStudentLeftChild) != NO_CHILD_NOD) {
                // heirPosition devient la position de l'héritier
                heirPosition = getRightestChild(deletedStudentLeftChild);
            }
        }
        return heirPosition;
    }

    /**
     * Recherche le stagiaire le plus à droite dans l'arbre à partir d'une position donnée
     * @param currentStudent Stagiaire à partir duquel faire la recherche
     * @return La position trouvée
     * @throws IOException
     */
    private long getRightestChild(long currentStudent) throws IOException {

        while (getRightChildPos(currentStudent) != NO_CHILD_NOD){
            currentStudent = getRightChildPos(currentStudent);
        }

        return currentStudent;
    }

    /**
     * Remplace l'enfant gauche de (parentPosition) par un autre stagiaire donné (childPosition)
     * <p>Remplace le parent du stagiaire (childPosition) par le stagiaire (parentPosition)
     * @param parentPosition Le stagiaire dont on veut changer l'enfant gauche
     * @param childPosition Le stagiaire qui prendra cette place
     * @throws IOException
     */
    private void replaceLeftChild(long parentPosition, long childPosition) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(FOLDER_FILES + BIN_OUT, "rw");

        long parentLeftChild = parentPosition + TO_LEFT_NOD_POS;
        raf.seek(parentLeftChild);
        raf.writeLong(childPosition);
        if (childPosition != NO_CHILD_NOD) {
            long childParent = childPosition + TO_PARENT_NOD_POS;
            raf.seek(childParent);
            raf.writeLong(parentPosition);
        }
        raf.close();
    }

    /**
     * Remplace l'enfant droit de (parentPosition) par un autre stagiaire donné (childPosition)
     * <p>Remplace le parent du stagiaire (childPosition) par le stagiaire (parentPosition)
     * @param parentPosition Le stagiaire dont on veut changer l'enfant droit
     * @param childPosition Le stagiaire qui prendra cette place
     * @throws IOException
     */
    private void replaceRightChild(long parentPosition, long childPosition) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(FOLDER_FILES + BIN_OUT, "rw");

        long parentRightChild = parentPosition + TO_RIGHT_NOD_POS;
        raf.seek(parentRightChild);
        raf.writeLong(childPosition);
        if (childPosition != NO_CHILD_NOD) {
            long childParent = childPosition + TO_PARENT_NOD_POS;
            raf.seek(childParent);
            raf.writeLong(parentPosition);
        }
        raf.close();
    }

    /**
     * Donne la coordonnée de l'enfant gauche d'un stagiaire.
     * @param studentPos La position du noeud (stagiaire) choisi
     * @return La position de son enfant gauche
     * @throws IOException
     */
    private long getLeftChildPos(long studentPos) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(FOLDER_FILES + BIN_OUT, "r");
        raf.seek(studentPos + TO_LEFT_NOD_POS);
        long leftChildPos = raf.readLong();
        raf.close();
        return leftChildPos;
    }

    /**
     * Donne la coordonnée de l'enfant droit d'un stagiaire.
     * @param studentPos La position du noeud (stagiaire) choisi
     * @return La position de son enfant droit
     * @throws IOException
     */
    private long getRightChildPos(long studentPos) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(FOLDER_FILES + BIN_OUT, "r");
        raf.seek(studentPos + TO_RIGHT_NOD_POS);
        long rightChildPos = raf.readLong();
        raf.close();
        return rightChildPos;
    }

    /**
     * Donne la coordonnée du parent d'un stagiaire.
     * @param studentPos La position du noeud (stagiaire) choisi
     * @return La position de son parent
     * @throws IOException
     */
    private long getParentPos(long studentPos) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(FOLDER_FILES + BIN_OUT, "r");
        raf.seek(studentPos + TO_PARENT_NOD_POS);
        long parentPos = raf.readLong();
        raf.close();
        return parentPos;
    }

    /**
     * Méthode qui sert uniquement à suivre un stagiaire depuis l'interface
     * @param student Stagiaire qu'on souhaite afficher en console
     * @throws IOException
     */
    public void infosParentEnfant(Student student) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(FOLDER_FILES + BIN_OUT,"r");
        logger.info("Les enfants G/D et parent de " + student.getName() + " " + student.getFirstName() + " sont : G : "
                + getLeftChildPos(student.getPosition()) + ". D : "
                + getRightChildPos(student.getPosition()) + ". Parent : "
                + getParentPos(student.getPosition()));
        raf.close();
    }

    /**
     * Ouvre la documentation située dans le dossier files/
     */
    public void openUserDoc(){
        File file = new File(FOLDER_FILES + DOCUMENTATION);
        try {
            if (Desktop.isDesktopSupported()){
                Desktop desktop = Desktop.getDesktop();
                if (file.exists()){
                        desktop.open(file);
                } else {
                    logger.info("Documentation non trouvée");
                }
            } else {
                logger.info("Desktop non supporté sur cette machine ! Impossible d'ouvrir le fichier .pdf.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Supprime l'annuaire complet
     */
    public void deleteBinFile() {
        File binFile = new File( FOLDER_FILES + BIN_OUT );
        if (binFile.exists()) {
                boolean isDeleted = binFile.delete();
                logger.info("Annuaire effacé ? " + isDeleted);
        }
    }


    /* Getters and Setters */

    public AdminGestion getAdminGestion() {
        return adminGestion;
    }

}

package fr.eql.ai113.baeflopy.annuaire.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AdminGestion {

    private static final Logger logger = LogManager.getLogger();
    private String folderAdminLocation = "";
    private String superAdminFile = "";


    public AdminGestion(String folderAdmin, String superAdminFile) {
        this.folderAdminLocation = folderAdmin;
        this.superAdminFile = superAdminFile;
    }

    /**
     * Crée le fichier pour la connexion du super administrateur.
     * <p>Par défaut, le login est "admin" et le mot de passe "pass"
     */
    public void creationSuperAdmin() {

        try {
            // Création du dossier FOLDER_ADMIN si inexistant
            File folderAdmin = new File(folderAdminLocation);
            folderAdmin.mkdir();
            File superAdmin = new File(folderAdminLocation + superAdminFile);
            // Si le fichier super_admin n'existe pas encore, le créer et écrire dedans
            if (!superAdmin.exists()){
                superAdmin.createNewFile();
                FileWriter fw = new FileWriter(superAdmin, false);
                BufferedWriter bw = new BufferedWriter(fw);

                bw.write("admin");
                bw.newLine();
                bw.write("pass");

                bw.close();
                fw.close();
            }

        } catch (IOException e) {
            logger.warn("Erreur lors de la création ou l'écriture du fichier super_admin", e);
        }
    }
    /**
     * Crée un fichier correspondant à un administrateur selon les credentials entrés par le super admin.
     * @param id Identifiant de connexion
     * @param password Mot de passe pour se connecter
     */
    public void creationAdmin(String id, String password) {

        // On enlève les espaces au début et à la fin des id/mdp
        id = id.trim();
        password = password.trim();

        try {

            // Création du dossier FOLDER_ADMIN si inexistant
            File folderAdmin = new File(folderAdminLocation);
            folderAdmin.mkdir();
            File admin = new File(folderAdminLocation + id);
            // Si le fichier super_admin n'existe pas encore, le créer et écrire dedans
            if (!admin.exists()){
                admin.createNewFile();
                FileWriter fw = new FileWriter(admin, false);

                fw.write(password);

                fw.close();
            }

        } catch (IOException e) {
            logger.warn("Erreur lors de la création ou l'écriture du fichier super_admin", e);
        }
    }

    /**
     * Efface un administrateur dont le nom est entré par le super admin.
     * @param adminName Identifiant de l'administrateur à supprimer
     * @throws IOException
     */
    public boolean deleteAdmin(String adminName) throws IOException {

        boolean isDeleted = false;
        File adminFile = new File(folderAdminLocation + adminName);
        String adminFileCaseSensitive = null;

        if (adminFile.exists()){
            adminFileCaseSensitive = adminFile.getCanonicalFile().getName();
            if (adminFileCaseSensitive.equals(adminName)){
                isDeleted = adminFile.delete();
            }
        }
        return isDeleted;
    }

    /**
     * Vérifie si le nom d'utilisateur et le mot de passe existent et coïncident.
     * @param isSuperAdmin Si la case de connexion Super Admin est cochée (pour savoir où aller chercher)
     * @param id Nom d'utilisateur
     * @param password Mot de passe
     * @return 'true' si id existe et password correspond
     *          <p>'false' si id inexistant ou password incorrect
     */
    public boolean isValidCredential(boolean isSuperAdmin, String id, String password){
        boolean isValid = false;

        try {
            // On enlève les espaces au début et à la fin des id/mdp
            id = id.trim();
            password = password.trim();

            String fileNameCaseSensitive = null;
            String passwordInFile = null;
            File admin;

            if (isSuperAdmin){
                // Si super admin, on va chercher le fichier super_admin
                admin = new File(folderAdminLocation + superAdminFile);
            } else {
                admin = new File(folderAdminLocation + id);
                // Si admin simple, on va chercher le fichier avec 'id' comme nom
                if (admin.exists()){
                // Si le fichier existe, récupération du nom exact (case sensitive)
                    fileNameCaseSensitive = admin.getCanonicalFile().getName();
                }
            }
            // Si le nom de l'administrateur existe
            if (admin.exists()){
                // Création du BufferedReader
                FileReader fr = new FileReader(admin);
                BufferedReader br = new BufferedReader(fr);

                if (isSuperAdmin){
                    // On lit les credentials du superadmin
                    String idInFile = br.readLine();
                    passwordInFile = br.readLine();
                    if (idInFile.equals(id) && passwordInFile.equals(password)){
                        isValid = true;
                    }
                } else {
                    // Si admin simple
                    // Si le fichier existe et est écrit exactement de la même manière (case sensitive)
                    if (fileNameCaseSensitive.equals(admin.getName())) {
                        // On lit le password (la seule ligne du fichier)
                        passwordInFile = br.readLine();
                        // Si le password correspond
                        if (passwordInFile.equals(password)){
                            isValid = true;
                        }
                    }
                }
                br.close();
                fr.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return isValid;
    }
}

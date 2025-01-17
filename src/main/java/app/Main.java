package app;

import app.model.*;
import app.dao.*;
import com.github.javafaker.Faker;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Date;
import java.sql.Driver;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class Main extends Application {

    private double xOffset;
    private double yOffset;

    @Override
    public void start(Stage stage) throws IOException {


        /*
        fakerMembres();
        fakerArticle();
        fakerEnchere();

         */


        Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("index.fxml")));

        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("css/styles.css").toExternalForm());

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
        //fakerMembres();

    }

    public static void fakerArticle() {

        Random random = new Random();
        Faker faker = new Faker(new Locale("fr"));
        // DAO
        CategorieDao categorieDao = new CategorieDao();
        MembreDao membreDao = new MembreDao();
        ArticleDao articleDao = new ArticleDao();
        OptionEnchereDao optionEnchereDao = new OptionEnchereDao();

        // Data
        ArrayList<Categorie> categories = categorieDao.findAll();
        ArrayList<Membre> membres = membreDao.findAll();
        ArrayList<OptionEnchere> optionEncheres = optionEnchereDao.findAll();

        // Entity
        Article article;

        for (int i = 0; i < 20; i++) {
            article = new Article();
            article.setTitre(faker.commerce().productName());
            article.setDescription(faker.lorem().characters(10, 20));
            article.setFraisPort(random.nextFloat(20));
            article.setPas(0.1F);
            article.setPrixDepart(random.nextFloat(40, 200));
            article.setDateCloture(Date.valueOf(LocalDate.now().plusDays(30)));
            article.setVendeur(membres.get(random.nextInt(membres.size())));
            article.setCategorie(categories.get(random.nextInt(categories.size())));

            articleDao.create(article);
        }



    }

    public static void fakerMembres() {

        Faker faker = new Faker(new Locale("fr"));
        CompteDao compteDao = new CompteDao();
        MembreDao membreDao = new MembreDao();
        Compte compte;
        Membre membre;
        String nom, prenom, email;

        // Membre
        for (int i = 0; i < 200; i++) {

            do {
                nom = faker.name().firstName();
                prenom = faker.name().lastName();
                email = String.format("%s.%s@gmail.com", prenom, nom);
            } while (compteDao.findByEmail(email));
            compte = new Compte();
            compte.setEmail(email);
            compte.setMdp("pwd");
            compte.setRole(Role.MEMBRE);
            compteDao.create(compte);
            membre = new Membre();
            membre.setCompte(compte);
            membre.setNom(nom);
            membre.setPrenom(prenom);
            membre.setDateNaissance(new Date(faker.date().birthday().getTime()));
            membre.setCodePostal(String.valueOf(generateRandomDigits(5)));
            membre.setAdressePostale(faker.address().toString());
            membreDao.create(membre);

        }

    }

    public static void fakerEnchere() {

        Random random = new Random();
        // DAO
        MembreDao membreDao = new MembreDao();
        ArticleDao articleDao = new ArticleDao();
        EnchereDao enchereDao = new EnchereDao();

        // Data
        ArrayList<Membre> membres = membreDao.findAll();
        ArrayList<Article> articles = articleDao.findAll();

        // Entity
        Article article;
        Enchere enchere;
        float pas, prixDepart, prixEnCours, montantEnchere, prixLimite;


        for (int i = 0; i < membres.size(); i++){
            article = articles.get(random.nextInt(articles.size()));
            pas = article.getPas();
            prixDepart = article.getPrixDepart();
            prixEnCours = article.getPrixEnCours();
            // Début des enchères
            if (prixEnCours == 0) {
                prixLimite = prixDepart * (1 + pas);
            } else {
                prixLimite = prixEnCours * (1 + pas);
            }
            enchere = new Enchere();
            enchere.setMontant(random.nextFloat(prixLimite, prixLimite + 50));
            enchere.setDate(new Date(System.currentTimeMillis()));
            enchere.setMembre(membres.get(i));
            enchere.setArticle(article);
            enchereDao.create(enchere);
        }


    }
    // Generates a random int with n digits
    public static int generateRandomDigits(int n) {
        int m = (int) Math.pow(10, n - 1);
        return m + new Random().nextInt(9 * m);
    }

}
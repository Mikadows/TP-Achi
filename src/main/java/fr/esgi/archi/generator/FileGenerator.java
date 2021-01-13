package fr.esgi.archi.generator;

import com.github.javafaker.Faker;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class FileGenerator {
    private Faker faker = new Faker();
    
    private String generateFileName(int number) {
        String ret = "";
        switch (number) {
            case 0:
                ret = faker.animal().name();
                break;
            case 1:
                ret = faker.beer().name();
                break;
            case 2:
                ret = faker.cat().name();
                break;
            case 3:
                ret = faker.artist().name();
                break;
            case 4:
                ret = faker.country().name();
                break;
            case 5:
                ret = faker.backToTheFuture().character();
                break;
            case 6:
                ret = faker.funnyName().name();
                break;
            case 7:
                ret = faker.pokemon().name();
                break;
            case 8:
                ret = faker.programmingLanguage().name();
                break;
            case 9:
                ret = faker.zelda().character();
                break;
            case 10:
                ret = faker.superhero().name();
                break;
            case 11:
                ret = faker.space().planet();
                break;
            case 12:
                ret = faker.aviation().aircraft();
                break;
            case 13:
                ret = faker.book().author();
                break;
            case 14:
                ret = faker.esports().team();
                break;
        }
        return ret;
    }

    private int getRandomNumber() {
        Random random = new Random();
        return random.ints(0, 14).findFirst().getAsInt();
    }

    public void generateFiles() throws IOException, InterruptedException {
        while (true) {
            Thread.sleep(1000);

            File myObj = new File("input/" + this.generateFileName(this.getRandomNumber()) + ".txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }

        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        FileGenerator fileGenerator = new FileGenerator();
        fileGenerator.generateFiles();
    }
}



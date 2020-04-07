package COMP250_A3_W2020;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class RandomCats extends CatTree {
    Random rand;
    ArrayList<String> names = new ArrayList<String>();
    File Lastnames = new File("COMP250_A3_W2020_PUBLISHED_DEBUGGER/supportfiles/last_names.all.txt");

    public RandomCats(long seed) {
        super(new CatInfo("trick to access inner methods of cattree :) Ignore me", 1, 10, 243, 0));
        rand = new Random(seed);
        try {
            Scanner scanner = new Scanner(Lastnames);
            while (scanner.hasNextLine()) {
                names.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            try {
                Lastnames = new File("supportfiles/last_names.all.txt");
                Scanner scanner = new Scanner(Lastnames);
                while (scanner.hasNextLine()) {
                    names.add(scanner.nextLine());
                }
            } catch (FileNotFoundException f) {
                try {
                    Lastnames = new File("last_names.all.txt");
                    Scanner scanner = new Scanner(Lastnames);
                    while (scanner.hasNextLine()) {
                        names.add(scanner.nextLine());
                    }
                } catch (FileNotFoundException g) {
                    try {
                        Lastnames = new File("Put Your java files here.txt");
                        Scanner scanner = new Scanner(Lastnames);
                        while (scanner.hasNextLine()) {
                            names.add(scanner.nextLine());
                        }
                    } catch (FileNotFoundException h) {
                        try {
                            Lastnames = new File("supportfiles\first_names.all.txt");
                            Scanner scanner = new Scanner(Lastnames);
                            while (scanner.hasNextLine()) {
                                names.add(scanner.nextLine());
                            }
                        } catch (FileNotFoundException i) {

                        }
                    }
                }
            }
        }
    }

    public RandomCats() {
        super(new CatInfo("trick to access inner methods of cattree :) Ignore me", 1, 10, 243, 0));
        rand = new Random();
        try {
            Scanner scanner = new Scanner(Lastnames);
            while (scanner.hasNextLine()) {
                names.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
        }
    }

    public int nextInt(int i) {
        return rand.nextInt(Math.abs(i));
    }

    public CatInfo nextCatInfo() {
        return new CatInfo(nextName(), 1 + rand.nextInt(243), 10 + rand.nextInt(90), 243 + rand.nextInt(100), rand.nextInt(200));
    }

    public CatNode nextCatNode() {
        return new CatNode(nextCatInfo());
    }

    public CatTree nextCatTree(int numNodes) {
        CatTree output = new CatTree(nextCatInfo());
        if (numNodes > 1) {
            for (int i = 0; i < numNodes; i++) {
                output.addCat(nextCatInfo());
            }
        }
        return output;
    }

    public CatTree nextCatTree() {
        CatTree output = new CatTree(nextCatInfo());
        int numNodes = 1 + rand.nextInt(25);
        if (numNodes > 1) {
            for (int i = 0; i < numNodes; i++) {
                output.addCat(nextCatInfo());
            }
        }
        return output;
    }

    public String nextName() {
        try {
            int numNames = rand.nextInt(names.size());
            String output = names.get(numNames);
            names.remove(numNames);
            return output;

        } catch (Exception e) {

            int nameLength = rand.nextInt(11);
            nameLength += 1;
            char[] nameOutChar = new char[nameLength];
            for (int i = 0; i < nameLength; i++) {
                if (nameLength <= 4) {
                    nameOutChar[i] = (char) rand.nextInt(18500);
                    nameOutChar[i] += (char) rand.nextInt(5000);
                } else {
                    int nameNumber = rand.nextInt(90 - 65) + 65;
                    nameOutChar[i] = (char) nameNumber;
                    if (i == nameLength - 3) {
                        nameOutChar[i + 1] = (char) 85;
                    }
                }
            }
            //System.out.println(nameOut);
            return String.valueOf(nameOutChar);
        }


    }

    public CViz nextCViz() {
        CViz viz = new CViz(nextCatInfo());
        int numNodes = 1 + rand.nextInt(2);
        if (numNodes > 1) {
            for (int i = 0; i < numNodes; i++) {
                viz.addCat(nextCatInfo());
            }
        }
        return viz;
    }


}

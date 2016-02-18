import model.Item;
import model.User;
import utils.Loader;
import utils.Stats;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by student on 18/02/2016.
 */
public class Main {
    public static void main(String[] args) {
        HashMap<Integer, User> users = Loader.getUsers();
        HashMap<Integer, Item> items = Loader.getItems();

        Stats stats = new Stats(users, items);

        Path statsFile = Paths.get("stats.txt");
        Path userFile = Paths.get("users.json");
        Path itemFile = Paths.get("items.json");

        ArrayList<String> userResults = new ArrayList<>(), itemResults = new ArrayList<>();
        userResults.add("[");
        for (Integer userID : users.keySet()) {
            userResults.add(users.get(userID).toString() + ",");
        }
        userResults.add("]");
        itemResults.add("[");
        for (Integer itemID : items.keySet()) {
            itemResults.add(items.get(itemID).toString() + ",");
        }
        itemResults.add("]");

        try {
            System.out.print("Writing results to files...");
            Files.write(statsFile, Collections.singletonList(stats.toString()));
            Files.write(userFile, userResults);
            Files.write(itemFile, itemResults);
            System.out.println("DONE");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

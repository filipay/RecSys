package utils;

import model.Item;
import model.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Author: Filip Piskor[12331436] on 15/02/16.
 */
public class Loader {
    private static String filePath = "res/ratings.csv";
    private static HashMap<Integer, User> users = new HashMap<>();
    private static HashMap<Integer, Item> items = new HashMap<>();

    private Loader() {}

    private static void load(String file) {
        System.out.print("Loading " + filePath + "...");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while((line = br.readLine()) != null) {
                String data [] = line.split(",");

                Integer user_id = Integer.valueOf(data[0]),
                        item_id = Integer.valueOf(data[1]),
                        rating = Integer.valueOf(data[2]);

                users.putIfAbsent(user_id, new User(user_id));
                users.get(user_id).addRating(item_id, rating);

                items.putIfAbsent(item_id, new Item(item_id));
                items.get(item_id).addRating(rating);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("DONE");
    }

    public static void setDataPath(String path) {
        filePath = path;
    }

    public static HashMap<Integer, User> getUsers() {
        if (users.isEmpty()) load(filePath);
        return users;
    }

    public static HashMap<Integer, Item> getItems() {
        if (items.isEmpty()) load(filePath);
        return items;
    }

    public static void main(String[] args) {
        for (Integer integer : getUsers().get(1).getItems()) {
            System.out.println("movie id = " + integer + ", rating = " + getUsers().get(1).getRating(integer));
        }
    }
}

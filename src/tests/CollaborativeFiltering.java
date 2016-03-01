package tests;

import model.Dataset;
import model.Item;
import model.User;
import utils.Loader;
import utils.Stats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

/**
 * Author: Filip Piskor[12331436] on 28/02/16.
 */
public class CollaborativeFiltering extends Dataset{
    private final double MAX_DIFF;
    private Stats stats;
    public CollaborativeFiltering(HashMap<Integer, User> users, HashMap<Integer, Item> items) {
        super(users, items);
        stats = new Stats(users, items);
        MAX_DIFF = Math.pow(stats.calcItemsMaxRating() - stats.calcItemsMinRating(), 2);
    }


    public double corated(User u1, User u2) {
        int corated = 0;
        for (Integer itemID : u1.getItems()) {
            if (u2.getRating(itemID) != null) corated++;
        }
        return corated;
    }

    public double distance(User u1, User u2) {
        int corated = 0;
        double sum = 0;
        for (Integer itemID : u1.getItems()) {
            Integer rating1 = u1.getRating(itemID);
            Integer rating2 = u2.getRating(itemID);
            if (rating2 != null) {
                sum += Math.pow(rating1-rating2,2);
                corated++;
            }
        }
        return sum / corated;
    }

    public void findNeighbourhood(User u, int size, int minCorated) {
        HashSet<User> neighbourhood = u.getNeighbourhood();
        TreeMap<Double, User> sortedSimilarities = new TreeMap<>();
        for (Integer userID : users.keySet()) {
            if (!u.getUserID().equals(userID)) {
                User neighbour = getUser(userID);
                double similarity = distance(u, neighbour);
                if (corated(u, neighbour) > minCorated) {
                    sortedSimilarities.putIfAbsent(similarity, neighbour);
                }
            }
        }

        for (Double similarity : sortedSimilarities.keySet()) {
            if (neighbourhood.size() == size) return;
            System.out.println(similarity+", "+sortedSimilarities.get(similarity).getUserID());
            neighbourhood.add(sortedSimilarities.get(similarity));
        }

    }

    public double prediction(Integer userID, Integer itemID) {
        User user = getUser(userID);
        HashSet<User> neighbourhood = user.getNeighbourhood();
        double top = 0;
        double bottom = 0;
        for (User neighbour : neighbourhood) {
            if (neighbour.getRating(itemID) != null) {
                double weight = 1 - (distance(user, neighbour) / MAX_DIFF);
                top += weight * neighbour.getRating(itemID);
                bottom += weight;
            }
        }
        return top/bottom;
    }

    public static void main(String[] args) {
        CollaborativeFiltering cf = new CollaborativeFiltering(Loader.loadUsers(), Loader.loadItems());
        User testUser = cf.getUser(34);
        System.out.println(cf.distance(testUser, cf.getUser(2)));
        cf.findNeighbourhood(testUser, 100,5);
        for (User user : testUser.getNeighbourhood()) {
            System.out.println(user);
        }


        System.out.println(cf.prediction(1, 4));
    }
}

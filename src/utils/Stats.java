package utils;

import model.Item;
import model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Author: Filip Piskor[12331436] on 15/02/16.
 */
public class Stats {
    public static final int RATINGS = 5;
    private HashMap<Integer, User> users;
    private HashMap<Integer, Item> items;

    public Stats(HashMap<Integer, User> users, HashMap<Integer, Item> items) {
        this.users = users;
        this.items = items;
    }

    public int uniqueUsersCount() {
        return users.size();
    }
    public int uniqueItemsCount() {
        return items.size();
    }
    public int uniqueRatingsCount() {
        return RATINGS;
    }
    public int totalRatingsCount() {
        int count = 0;
        HashMap<Integer, Integer> ratings = ratingsCount();
        for (Integer rating : ratings.keySet()) {
            count += ratings.get(rating);
        }
        return count;
    }
    public User getUser(Integer userID) {
        return users.get(userID);
    }
    public Item getItem(Integer itemID) {
        return items.get(itemID);
    }

    public double calcDensityMatrix() {
        return (double) totalRatingsCount() / (uniqueUsersCount() * uniqueItemsCount());
    }


    public double calcUsersMeanRating() {
        double sum = 0;
        for (Integer userID : users.keySet()) {
            sum += getUser(userID).meanRating();
        }
        return sum / uniqueUsersCount();
    }

    public double calcUsersMedianRating() {
        double sum = 0;
        for (Integer userID : users.keySet()) {
            sum += getUser(userID).medianRating();
        }
        return sum / uniqueUsersCount();
    }

    public double calcUsersStandardDeviationRating() {
        double sum = 0;
        for (Integer userID : users.keySet()) {
            sum += getUser(userID).standardDeviationRating();
        }
        return sum / uniqueUsersCount();
    }

    public double calcUsersMaxRating() {
        double sum = 0;
        for (Integer userID : users.keySet()) {
            sum += getUser(userID).maxRating();
        }
        return sum / uniqueUsersCount();
    }

    public double calcUsersMinRating() {
        double sum = 0;
        for (Integer userID : users.keySet()) {
            sum += getUser(userID).minRating();
        }
        return sum / uniqueUsersCount();
    }

    public double calcItemsMeanRating() {
        double sum = 0;
        for (Integer itemID : items.keySet()) {
            sum += getItem(itemID).meanRating();
        }
        return sum / uniqueItemsCount();
    }

    public double calcItemsMedianRating() {
        double sum = 0;
        for (Integer itemID : items.keySet()) {
            sum += getItem(itemID).medianRating();
        }
        return sum / uniqueItemsCount();
    }

    public double calcItemsStandardDeviationRating() {
        double sum = 0;
        for (Integer itemID : items.keySet()) {
            sum += getItem(itemID).standardDeviationRating();
        }
        return sum / uniqueItemsCount();
    }

    public double calcItemsMaxRating() {
        double sum = 0;
        for (Integer itemID : items.keySet()) {
            sum += getItem(itemID).maxRating();
        }
        return sum / uniqueItemsCount();
    }

    public double calcItemsMinRating() {
        double sum = 0;
        for (Integer itemID : items.keySet()) {
            sum += getItem(itemID).minRating();
        }
        return sum / uniqueItemsCount();
    }

    public HashMap<Integer, Integer> ratingsCount() {
        HashMap<Integer, Integer> ratings = new HashMap<>();
        for (Integer itemID : items.keySet()) {
            Item item = items.get(itemID);
            for (Integer rating : item.getRatings()) {
                if (ratings.containsKey(rating)) {
                    ratings.replace(rating, ratings.get(rating) + 1);
                } else {
                    ratings.put(rating, 1);
                }
            }
        }
        return ratings;
    }

    @Override
    public String toString() {
        HashMap<Integer, Integer> ratings = ratingsCount();
        String stats =
                "========================Stats==========================\n" +
                "Total unique users: " + uniqueUsersCount() + "\n" +
                "Total unique movies: " + uniqueItemsCount() + "\n" +
                "Total unique ratings: " + uniqueRatingsCount() + "\n" +
                "=======================================================" + "\n" +
                "Rating density matrix: " + calcDensityMatrix() * 100 + "\n" +
                "=======================================================" + "\n" +
                "Mean rating of users : " + calcUsersMeanRating() + "\n" +
                "Median rating of users : " + calcUsersMedianRating() + "\n" +
                "SD rating of users : " + calcUsersStandardDeviationRating() + "\n" +
                "Max rating of users : " + calcUsersMaxRating() + "\n" +
                "Min rating of users : " + calcUsersMinRating() + "\n" +
                "=======================================================" + "\n" +
                "Mean rating of items : " + calcItemsMeanRating() + "\n" +
                "Median rating of items : " + calcItemsMedianRating() + "\n" +
                "SD rating of items : " + calcItemsStandardDeviationRating() + "\n" +
                "Max rating of items : " + calcItemsMaxRating() + "\n" +
                "Min rating of items : " + calcItemsMinRating() + "\n" +
                "=======================================================" + "\n"
                ;;
        for (Integer rating : ratings.keySet()) {
            stats += "Total amount of rating "+ rating + ": " + ratings.get(rating) + "\n";
        }
        return stats;
    }
}

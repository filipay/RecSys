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
    public Stats() {
        users = Loader.getUsers();
        items = Loader.getItems();
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

    public double calculateMean(ArrayList<Integer> ratings) {
        double sum = 0;
        for (Integer number : ratings) {
            sum += number;
        }
        return sum / ratings.size();
    }

    public double calcUsersMedianRating() {
        ArrayList<Double> sortedRatings = new ArrayList<>();
        int middle = uniqueUsersCount() / 2;
        for (Integer userID : users.keySet()) {
            sortedRatings.add(getUser(userID).medianRating());;
        }

        sortedRatings.sort(Double::compareTo);

        return (sortedRatings.get(middle - 1) + sortedRatings.get(middle)) * 0.5;
    }

    public double calcUsersStandardDeviationRating() {
        double mean = calcUsersMeanRating();
        double sum = 0;

        for (Integer userID : users.keySet()) {
            sum += Math.pow(getUser(userID).standardDeviationRating() - mean, 2);
        }
        return Math.sqrt(sum / uniqueUsersCount());
    }

    public int calcUsersMaxRating() {
        int maxRating = Integer.MIN_VALUE;
        for (Integer userID : users.keySet()) {
            maxRating = Math.max(getUser(userID).maxRating(), maxRating);
        }
        return maxRating;
    }

    public int calcUsersMinRating() {
        int minRating = Integer.MAX_VALUE;
        for (Integer userID : users.keySet()) {
            minRating = Math.min(getUser(userID).minRating(), minRating);
        }
        return minRating;
    }

    public double calcItemsMeanRating() {
        double sum = 0;
        for (Integer itemID : items.keySet()) {
            sum += getItem(itemID).meanRating();
        }
        return sum / uniqueItemsCount();
    }

    public double calcItemsMedianRating() {
        ArrayList<Double> sortedRatings = new ArrayList<>();
        int middle = uniqueItemsCount() / 2;
        for (Integer itemID : items.keySet()) {
            sortedRatings.add(getItem(itemID).medianRating());
        }

        sortedRatings.sort(Double::compareTo);

        return (sortedRatings.get(middle - 1) + sortedRatings.get(middle)) * 0.5;
    }

    public double calcItemsStandardDeviationRating() {
        double mean = calcItemsMeanRating();
        double count = 0;
        double sum = 0;

        for (Integer itemID : items.keySet()) {
            sum += Math.pow(getItem(itemID).standardDeviationRating() - mean, 2);
        }

        return Math.sqrt(sum / count);
    }

    public int calcItemsMaxRating() {
        int maxRating = Integer.MIN_VALUE;
        for (Integer itemID : items.keySet()) {
            maxRating = Math.max(getItem(itemID).maxRating(), maxRating);
        }
        return maxRating;
    }

    public int calcItemsMinRating() {
        int minRating = Integer.MAX_VALUE;
        for (Integer itemID : items.keySet()) {
            minRating = Math.min(getItem(itemID).minRating(), minRating);
        }
        return minRating;
    }

    public HashMap<Integer, Integer> ratingsCount() {
        int count = 0;
        HashMap<Integer, Integer> ratings = new HashMap<>();
        for (Integer itemID : items.keySet()) {
            Item item = items.get(itemID);
            for (Integer rating : item.getRatings()) {
                if (ratings.containsKey(rating)) {
                    ratings.replace(rating, ratings.get(rating) + 1);
                } else {
                    ratings.put(rating, 1);
                }
                count++;
            }
        }
        return ratings;
    }

    public static void main(String[] args) {
        Stats stats = new Stats();
//        Integer userID = 1;
//        User user = stats.getUser(userID);
        HashMap<Integer, Integer> ratings = stats.ratingsCount();
        System.out.println("Total unique users: " + stats.uniqueUsersCount());
        System.out.println("Total unique movies: " + stats.uniqueItemsCount());
        System.out.println("Total unique ratings: " + stats.uniqueRatingsCount());
        System.out.println("=======================================================");
        System.out.println("Rating density matrix: " + stats.calcDensityMatrix() * 100);
        System.out.println("=======================================================");
        System.out.println("Mean rating of users : " + stats.calcUsersMeanRating());
        System.out.println("Median rating of users : " + stats.calcUsersMedianRating());
        System.out.println("SD rating of users : " + stats.calcUsersStandardDeviationRating());
        System.out.println("Max rating of users : " + stats.calcUsersMaxRating());
        System.out.println("Min rating of users : " + stats.calcUsersMinRating());
        System.out.println("=======================================================");
        System.out.println("Mean rating of items : " + stats.calcItemsMeanRating());
        System.out.println("Median rating of items : " + stats.calcItemsMedianRating());
        System.out.println("SD rating of items : " + stats.calcItemsStandardDeviationRating());
        System.out.println("Max rating of items : " + stats.calcItemsMaxRating());
        System.out.println("Min rating of items : " + stats.calcItemsMinRating());
        System.out.println("=======================================================");
        for (Integer rating : ratings.keySet()) {
            System.out.println("Total amount of rating "+ rating + ": " + ratings.get(rating));
        }
    }
}

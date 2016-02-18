package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Author: Filip Piskor[12331436] on 16/02/16.
 */
public class User {
    private HashMap<Integer, Integer> ratings;

    public User() {
        ratings = new HashMap<>();
    }

    public Set<Integer> getItems() {
        return ratings.keySet();
    }

    public Integer getRating(Integer itemID) {
        return ratings.get(itemID);
    }

    public void addRating(Integer itemID, Integer rating) {
        ratings.putIfAbsent(itemID, rating);
    }

    public double meanRating() {
        double sumRating = 0;

        for (Integer itemID : ratings.keySet()) {
            sumRating += ratings.get(itemID);
        }
        return sumRating / ratings.size();
    }

    public double medianRating() {
        ArrayList<Integer> sortedRatings = new ArrayList<>();
        int middle = ratings.size() / 2;

        for (Integer itemID : ratings.keySet()) {
            sortedRatings.add(ratings.get(itemID));
        }

        sortedRatings.sort(Integer::compareTo);

        return (sortedRatings.get(middle - 1) + sortedRatings.get(middle)) * 0.5;
    }

    public double standardDeviationRating() {
        double mean = meanRating();
        double count = 0;
        double sum = 0;

        for (Integer itemID : ratings.keySet()) {
            sum += Math.pow(ratings.get(itemID) - mean, 2);
            count++;
        }
        return Math.sqrt(sum / count);
    }

    public int maxRating() {
        int maxRating = Integer.MIN_VALUE;
        for (Integer itemID : ratings.keySet()) {
            maxRating = Math.max(maxRating, ratings.get(itemID));
        }
        return maxRating;
    }

    public int minRating() {
        int minRating = Integer.MAX_VALUE;
        for (Integer itemID : ratings.keySet()) {
            minRating = Math.min(minRating, ratings.get(itemID));
        }
        return minRating;
    }
}

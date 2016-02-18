package model;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Author: Filip Piskor[12331436] on 16/02/16.
 */
public class Item {
    private ArrayList<Integer> ratings;

    public Item() {
        ratings = new ArrayList<>();
    }

    public ArrayList<Integer> getRatings() {
        return ratings;
    }
    public void addRating(Integer rating) {
        ratings.add(rating);
    }

    public double meanRating() {
        double sumRating = 0;

        for (Integer itemID : ratings) {
            sumRating += itemID;
        }
        return sumRating / ratings.size();
    }

    public double medianRating() {
        ArrayList<Integer> sortedRatings = new ArrayList<>();
        int middle = ratings.size() / 2;
        System.out.println(middle);
        for (Integer rating : ratings) {
            sortedRatings.add(rating);
        }

        sortedRatings.sort(Integer::compareTo);

        return (sortedRatings.get(middle - 1) + sortedRatings.get(middle)) * 0.5;
    }

    public double standardDeviationRating() {
        double mean = meanRating();
        double count = 0;
        double sum = 0;

        for (Integer itemID : ratings) {
            sum += Math.pow(itemID - mean, 2);
            count++;
        }
        return Math.sqrt(sum / count);
    }

    public int maxRating() {
        int maxRating = Integer.MIN_VALUE;
        for (Integer itemID : ratings) {
            maxRating = Math.max(maxRating, itemID);
        }
        return maxRating;
    }

    public int minRating() {
        int minRating = Integer.MAX_VALUE;
        for (Integer itemID : ratings) {
            minRating = Math.min(minRating, itemID);
        }
        return minRating;
    }
}
package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
/**
 * Author: Filip Piskor[12331436] on 16/02/16.
 */
public class User {

    private enum Stat {
        MEAN_RATING, MEDIAN_RATING, SD_RATING, MAX_RATING, MIN_RATING
    }

    private final Integer userID;
    private HashMap<Integer, Integer> ratings;
    private HashSet<User> neighbourhood;
    private HashMap<User, Metric> distanceToUser;
    private HashMap<User, HashSet<Integer>> corated;
    private HashMap<Stat, Double> stats;

    public User(Integer userID) {
        this.userID = userID;
        ratings = new HashMap<>();
        distanceToUser = new HashMap<>();
        corated = new HashMap<>();
        neighbourhood = new HashSet<>();
        stats = new HashMap<>();
    }

    public Integer getUserID() {
        return userID;
    }

    public Set<Integer> getItems() {
        return ratings.keySet();
    }

    public Integer getRating(Integer itemID) {
        return ratings.get(itemID);
    }

    public boolean hasRating(Integer itemID) {
        return ratings.containsKey(itemID);
    }

    public void addRating(Integer itemID, Integer rating) {
        ratings.putIfAbsent(itemID, rating);
    }

    public HashSet<Integer> getCorated(User user) {
        if (!corated.containsKey(user)) {
            HashSet<Integer> sharedItems = new HashSet<>();
            setCorated(user, sharedItems);
            user.setCorated(this, sharedItems);
            for (Integer itemID : getItems()) {
                if (user.hasRating(itemID)) {
                    sharedItems.add(itemID);
                }
            }
        }
        return corated.get(user);
    }

    private void setCorated(User user, HashSet<Integer> items) {
        corated.putIfAbsent(user, items);
    }

    public Double getMetricToUser(User user, Metric.Type type) {
        Metric metric = distanceToUser.get(user);
        return metric != null ? metric.getMetric(type) : null;
    }

    public void addMetricToUser(User user, double metric, Metric.Type type) {
        if (distanceToUser.containsKey(user)) {
            distanceToUser.get(user).addMetric(type, metric);
        } else {
            Metric sharedMetric = new Metric(metric, type);
            distanceToUser.put(user, sharedMetric);
            user.distanceToUser.put(this, sharedMetric);
        }
    }
    public HashSet<User> getNeighbourhood() {
        return neighbourhood;
    }

    public int getNeighbourhoodSize() {
        return neighbourhood.size();
    }

    public void addNeighbour(User u) {
        neighbourhood.add(u);
    }

    public boolean hasNeighbour(User u) {
        return neighbourhood.contains(u);
    }

    public void removeAllNeighbours() {
        neighbourhood = new HashSet<>();
    }

    public double meanRating() {
        if (!stats.containsKey(Stat.MEAN_RATING)) {
            double sumRating = 0;

            for (Integer itemID : ratings.keySet()) {
                sumRating += ratings.get(itemID);
            }
            stats.put(Stat.MEAN_RATING, sumRating / ratings.size());
        }

        return stats.get(Stat.MEAN_RATING);
    }

    public double medianRating() {
        if (!stats.containsKey(Stat.MEDIAN_RATING)) {
            ArrayList<Integer> sortedRatings = new ArrayList<>();
            int middle = ratings.size() / 2;

            for (Integer itemID : ratings.keySet()) {
                sortedRatings.add(ratings.get(itemID));
            }

            sortedRatings.sort(Integer::compareTo);

            double result = middle > 0 ?
                    (sortedRatings.get(middle - 1) + sortedRatings.get(middle)) * 0.5 :
                    sortedRatings.get(middle);

            stats.put(Stat.MEDIAN_RATING, result);
        }

        return stats.get(Stat.MEDIAN_RATING);
    }

    public double standardDeviationRating() {
        if (!stats.containsKey(Stat.SD_RATING)) {
            double mean = meanRating();
            double count = 0;
            double sum = 0;

            for (Integer itemID : ratings.keySet()) {
                sum += Math.pow(ratings.get(itemID) - mean, 2);
                count++;
            }
            stats.put(Stat.SD_RATING, Math.sqrt(sum / count));
        }


        return stats.get(Stat.SD_RATING);
    }

    public int maxRating() {
        if (!stats.containsKey(Stat.MAX_RATING)) {
            int maxRating = Integer.MIN_VALUE;
            for (Integer itemID : ratings.keySet()) {
                maxRating = Math.max(maxRating, ratings.get(itemID));
            }
            stats.put(Stat.MAX_RATING, (double) maxRating);
        }

        return stats.get(Stat.MAX_RATING).intValue();
    }

    public int minRating() {
        if (!stats.containsKey(Stat.MIN_RATING)) {
            int minRating = Integer.MAX_VALUE;
            for (Integer itemID : ratings.keySet()) {
                minRating = Math.min(minRating, ratings.get(itemID));
            }
            stats.put(Stat.MIN_RATING, (double) minRating);
        }

        return stats.get(Stat.MIN_RATING).intValue();
    }

    @Override
    public String toString() {
        return "{" + "\n\t" +
                "\"userID\": " + userID + ", \n\t" +
                "\"meanOfRating\": " + meanRating() + ", \n\t" +
                "\"medianOfRating\": " + medianRating() + ", \n\t" +
                "\"standardDeviationOfRatings\": " + standardDeviationRating() + ", \n\t" +
                "\"maxRating\": " + maxRating() + ", \n\t" +
                "\"minRating\": " + minRating() + "\n" +
                '}';
    }
}

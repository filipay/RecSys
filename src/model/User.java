package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
/**
 * Author: Filip Piskor[12331436] on 16/02/16.
 */
public class User {
    private final Integer userID;
    private HashMap<Integer, Integer> ratings;
    private HashSet<User> neighbourhood;
    private HashMap<User, Metric> distanceToUser;
    private HashMap<User, HashSet<Integer>> corated;
    public User(Integer userID) {
        this.userID = userID;
        ratings = new HashMap<>();
        distanceToUser = new HashMap<>();
        corated = new HashMap<>();
        neighbourhood = new HashSet<>();
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

        return middle > 0 ?
                (sortedRatings.get(middle - 1) + sortedRatings.get(middle)) * 0.5 :
                sortedRatings.get(middle);
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

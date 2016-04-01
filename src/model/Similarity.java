package model;

import java.util.*;

/**
 * Author: Filip Piskor[12331436] on 10/03/16.
 */
public abstract class Similarity extends Dataset {
    protected final double MAX_DIFF = 16;

    protected Similarity(HashMap<Integer, User> users, HashMap<Integer, Item> items) {
        super(users, items);
    }

    protected class Result {
        double coverage;
        double meanRMSE;

        Result(double coverage, double meanRMSE) {
            this.coverage = coverage;
            this.meanRMSE = meanRMSE;
        }

        public double getCoverage() {
            return coverage;
        }

        public double getMeanRMSE() {
            return meanRMSE;
        }
    }

    protected void resetAllNeighbourhoods() {
        for (Integer userID : users.keySet()) {
            getUser(userID).removeAllNeighbours();
        }
    }

    private void findNeighbourhood(User u, int minCorated, int size, Metric.Type type) {
        ArrayList<User> neighbours = new ArrayList<>();

        //Find all the possible neighbours
        for (Integer userID : users.keySet()) {
            if (!u.getUserID().equals(userID)) {
                User neighbour = getUser(userID);
                //Check if user isn't already a neighbour and that the number of corated items is above threshold
                if (!u.hasNeighbour(neighbour) && u.getCorated(neighbour).size() >= minCorated) {

                    //Get the similarity between the users
                    Double metric = u.getMetricToUser(neighbour, type);
                    if (metric == null) {
                        metric = computeSimilarity(u, neighbour);
                        u.addMetricToUser(neighbour, metric, type);
                    }
                    if (!Double.isNaN(metric)) {
                        neighbours.add(neighbour);
                    }
                }
            }
        }

        //Sort neighbours
        Collections.sort(neighbours,
                (u1, u2) -> Double.compare(u2.getMetricToUser(u, type), u1.getMetricToUser(u, type)));

        //Add neighbours until cut off point
        for (User neighbour : neighbours) {
            if (u.getNeighbourhoodSize() == size) return;
            u.addNeighbour(neighbour);
            if (neighbour.getNeighbourhoodSize() < size) {
                neighbour.addNeighbour(u);
            }
        }

    }

    protected double prediction(Integer userID, Integer itemID, Metric.Type type) {
        User user = getUser(userID);
        HashSet<User> neighbourhood = user.getNeighbourhood();
        double top = 0;
        double bottom = 0;
        //Loop through every neighbour
        for (User neighbour : neighbourhood) {
            //Check if neighbour has a rating for a specific item
            if (neighbour.hasRating(itemID)) {
                //Choose weight based on the type of metric we're using
                double weight = user.getMetricToUser(neighbour, type);
                top += weight * neighbour.getRating(itemID);
                bottom += weight;
            }
        }
        return top/bottom;
    }

    protected Result test(int minCorated, int size, Metric.Type type) {
        System.out.print("creating neighbourhoods...");
        long start = System.currentTimeMillis();

        for (Integer userID : users.keySet()) {
            User user = getUser(userID);
            findNeighbourhood(user, minCorated, size, type);
        }

        long end = System.currentTimeMillis();
        System.out.println("DONE["+(end-start)+"ms]");
        System.out.print("making predictions...");

        int canPredict = 0;
        double sum = 0;

        for (Integer userID : users.keySet()) {
            User user = getUser(userID);
            for (Integer itemID : user.getItems()) {
                double actual = user.getRating(itemID);
                double prediction = prediction(user.getUserID(), itemID, type);
                if (!Double.isNaN(prediction)) {
                    sum += rootMeanSquaredError(actual, prediction);
                    canPredict++;
                }
            }
        }

        start = System.currentTimeMillis();

        double coverage = canPredict/100000.0;
        double meanRmse = sum/canPredict;

        System.out.println("DONE[" +(start-end)+"ms]");
        System.out.println("=====================================");
        System.out.println("mean rmse: " + meanRmse);
        System.out.println("coverage: " + coverage);
        System.out.println("=====================================");

        return new Result(coverage, meanRmse);

    }

    public abstract Double computeSimilarity(User u1, User u2);
}

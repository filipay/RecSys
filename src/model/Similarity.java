package model;

import javax.xml.crypto.Data;
import java.util.*;

/**
 * Author: Filip Piskor[12331436] on 10/03/16.
 */
public abstract class Similarity extends Dataset {
    private final double MAX_DIFF = 16;

    protected Similarity(HashMap<Integer, User> users, HashMap<Integer, Item> items) {
        super(users, items);
    }

    protected class Result {
        double coverage;
        double meanRMSE;

        public Result(double coverage, double meanRMSE) {
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

    public void resetAllNeighbourhoods() {
        for (Integer userID : users.keySet()) {
            getUser(userID).removeAllNeighbours();
        }
    }

    public void findNeighbourhood(User u, int minCorated, int size, Metric.Type type) {
        ArrayList<User> neighbours = new ArrayList<>();
        for (Integer userID : users.keySet()) {
            if (!u.getUserID().equals(userID)) {
                User neighbour = getUser(userID);
                if (!u.hasNeighbour(neighbour) && u.getCorated(neighbour).size() >= minCorated) {
                    Double metric = u.getMetricToUser(neighbour, type);
                    if (metric == null) {
                        metric = computeMetric(u, neighbour);
                        u.addMetricToUser(neighbour, metric, type);
                    }
                    if (!Double.isNaN(metric)) {
                        neighbours.add(neighbour);
                    }
                }
            }
        }

        Collections.sort(neighbours, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return Double.compare(u1.getMetricToUser(u, type), u2.getMetricToUser(u, type));
            }
        });

        for (User neighbour : neighbours) {
            if (u.getNeighbourhoodSize() == size) return;
            u.addNeighbour(neighbour);
            if (neighbour.getNeighbourhoodSize() < size) {
                neighbour.addNeighbour(u);
            }
        }

    }

    public double prediction(Integer userID, Integer itemID, Metric.Type type) {
        User user = getUser(userID);
        HashSet<User> neighbourhood = user.getNeighbourhood();
        double top = 0;
        double bottom = 0;
        for (User neighbour : neighbourhood) {
            if (neighbour.hasRating(itemID)) {
                double weight = 1 - (user.getMetricToUser(neighbour, type) / MAX_DIFF);
                top += weight * neighbour.getRating(itemID);
                bottom += weight;
            }
        }
        return top/bottom;
    }

    public Result test(int minCorated, int size, Metric.Type type) {
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
//            System.out.println(userID);
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


    public abstract Double computeMetric(User u1, User u2);
}

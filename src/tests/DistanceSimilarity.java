package tests;

import com.sun.javafx.tools.resource.ResourceTraversal;
import model.Dataset;
import model.Item;
import model.User;
import utils.Loader;
import utils.Stats;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

/**
 * Author: Filip Piskor[12331436] on 28/02/16.
 */
public class DistanceSimilarity extends Dataset{
    private final double MAX_DIFF;
    private Stats stats;
    public DistanceSimilarity(HashMap<Integer, User> users, HashMap<Integer, Item> items) {
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

    public void findNeighbourhood(User u, int minCorated, int size) {
        TreeMap<Double, User> sortedDistance = new TreeMap<>();
        for (Integer userID : users.keySet()) {
            if (!u.getUserID().equals(userID)) {
                User neighbour = getUser(userID);
                if (!u.hasNeighbour(neighbour)) {
                    double similarity = distance(u, neighbour);
                    if (corated(u, neighbour) >= minCorated) {
                        sortedDistance.putIfAbsent(similarity, neighbour);
                    }
                }
            }
        }

        for (Double distance : sortedDistance.keySet()) {
            if (u.getNeighbourhoodSize() == size) return;
//            System.out.println(distance);
            User neighbour = sortedDistance.get(distance);
            u.addNeighbour(neighbour);
            if (neighbour.getNeighbourhoodSize() < size) {
                neighbour.addNeighbour(u);
            }
        }

    }

    public double prediction(Integer userID, Integer itemID) {
        User user = getUser(userID);
        HashSet<User> neighbourhood = user.getNeighbourhood();
        double top = 0;
        double bottom = 0;
        for (User neighbour : neighbourhood) {
            if (neighbour.hasRating(itemID)) {
                double weight = 1 - (distance(user, neighbour) / MAX_DIFF);
                top += weight * neighbour.getRating(itemID);
                bottom += weight;
            }
        }
        return top/bottom;
    }


    public Result test(int minCorated, int size) {
        System.out.print("creating neighbourhoods...");
        long start = System.currentTimeMillis();
        for (Integer userID : users.keySet()) {
            User user = getUser(userID);
            findNeighbourhood(user, minCorated, size);
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
                double prediction = prediction(user.getUserID(), itemID);
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

    public static void main(String[] args) throws IOException {
        int MIN_CORATED = 10;
        int SIZE = 10;
        ArrayList<String> lines = new ArrayList<>();
        lines.add("minCorated, size, coverage, meanRMSE");
        for (int i = 1; i < 7; i++) {
            int currMinCorated = MIN_CORATED * i;
            DistanceSimilarity ds = new DistanceSimilarity(Loader.loadUsers(), Loader.loadItems());
            for (int j = 1; j < 7; j++) {
                int currSize = SIZE * j;
                System.out.println("minCorated: " + currMinCorated + ", size: " + currSize);
                Result result = ds.test(MIN_CORATED * i, SIZE * j);
                ds.resetAllNeighbourhoods();
                lines.add(currMinCorated + ", " + currSize + ", " + result.getCoverage() + ", " + result.getMeanRMSE());
            }
        }
        Files.write(Paths.get("dist_sim.csv"),lines);
    }
}

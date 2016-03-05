package tests;

import model.Dataset;
import model.Distance;
import model.Item;
import model.User;
import utils.Loader;
import utils.Stats;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Author: Filip Piskor[12331436] on 28/02/16.
 */


public class DistanceSimilarity extends Dataset{
    private final double MAX_DIFF = 16;
    private Stats stats;
    public DistanceSimilarity(HashMap<Integer, User> users, HashMap<Integer, Item> items) {
        super(users, items);
        stats = new Stats(users, items);
    }

    public Distance distance(User u1, User u2) {
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
        return new Distance(sum / corated, corated);
    }

    public void findNeighbourhood(User u, int minCorated, int size) {
        ArrayList<User> neighbours = new ArrayList<>();
        for (Integer userID : users.keySet()) {
            if (!u.getUserID().equals(userID)) {
                User neighbour = getUser(userID);
                if (!u.hasNeighbour(neighbour)) {
                    Distance distance = u.getDistanceToUser(neighbour);
                    if (distance == null) {
                        distance = distance(u, neighbour);
                        u.addDistanceToUser(neighbour, distance);
                        neighbour.addDistanceToUser(u, distance);
                    }
                    if (distance.getCorated() >= minCorated && !Double.isNaN(distance.getDistance())) {
                        neighbours.add(neighbour);
                    }
                }
            }
        }

        Collections.sort(neighbours, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return Double.compare(u1.getDistanceToUser(u).getDistance(), u2.getDistanceToUser(u).getDistance());
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

    public double prediction(Integer userID, Integer itemID) {
        User user = getUser(userID);
        HashSet<User> neighbourhood = user.getNeighbourhood();
        double top = 0;
        double bottom = 0;
        for (User neighbour : neighbourhood) {
            if (neighbour.hasRating(itemID)) {
                double weight = 1 - (user.getDistanceToUser(neighbour).getDistance() / MAX_DIFF);
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
        long start = System.currentTimeMillis();
        lines.add("minCorated, size, coverage, meanRMSE");
        for (int i = 1; i < 11; i++) {
            int currMinCorated = MIN_CORATED * i;
            DistanceSimilarity ds = new DistanceSimilarity(Loader.loadUsers(), Loader.loadItems());
            for (int j = 1; j < 11; j++) {
                int currSize = SIZE * j;
                System.out.println("minCorated: " + currMinCorated + ", size: " + currSize);
                Result result = ds.test(MIN_CORATED * i, SIZE * j);
                ds.resetAllNeighbourhoods();
                lines.add(currMinCorated + ", " + currSize + ", " + result.getCoverage() + ", " + result.getMeanRMSE());
            }
            lines.add("");
        }
        Files.write(Paths.get("dist_sim_"+MIN_CORATED*SIZE+".csv"),lines);
        long end = System.currentTimeMillis();
        System.out.println("Total time: " + (end - start));
    }
}

package tests;

import model.*;
import utils.Loader;
import utils.Stats;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Author: Filip Piskor[12331436] on 28/02/16.
 */


public class DistanceSimilarity extends Similarity{
    private Stats stats;
    public DistanceSimilarity(HashMap<Integer, User> users, HashMap<Integer, Item> items) {
        super(users, items);
        stats = new Stats(users, items);
    }

    public Double computeSimilarity(User u1, User u2) {
//        System.out.println("calculating distance between: " + u1.getUserID() + ", " + u2.getUserID());
        HashSet<Integer> corated = u1.getCorated(u2);
        double sum = 0;
        for (Integer itemID : corated) {
            Integer rating1 = u1.getRating(itemID);
            Integer rating2 = u2.getRating(itemID);
            sum += Math.pow(rating1-rating2,2);
        }
        double distance = sum / corated.size();


        return 1 - (distance / MAX_DIFF);
    }

    public static void main(String[] args) throws IOException {
        int MIN_CORATED = 10;
        int SIZE = 10;
        ArrayList<String> lines = new ArrayList<>();

        long start = System.currentTimeMillis();
        lines.add("minCorated, size, coverage, meanRMSE");

        for (int stepCorated = 1; stepCorated < 11; stepCorated++) {
            int currMinCorated = MIN_CORATED * stepCorated;

            DistanceSimilarity ds = new DistanceSimilarity(Loader.loadUsers(), Loader.loadItems());
            for (int stepSize = 1; stepSize < 11; stepSize++) {

                int currSize = SIZE * stepSize;

                System.out.println("minCorated: " + currMinCorated + ", size: " + currSize);

                Result result = ds.test(currMinCorated, currSize, Metric.Type.DISTANCE);
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

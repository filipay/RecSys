package tests;

import model.Dataset;
import model.Item;
import model.User;
import utils.Loader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Author: Filip Piskor[12331436] on 20/02/16.
 */
public class MeanRatingPrediction extends Dataset{

    public MeanRatingPrediction(HashMap<Integer, User> users, HashMap<Integer, Item> items) {
        super(users, items);
    }

    public double meanItemRating(Integer userID, Integer itemID) {
        ArrayList<Integer> ratings = getItem(itemID).getRatings();
        Integer userRating = getUser(userID).getRating(itemID);
        double sum = 0;
        for (Integer rating : ratings) {
            sum += rating;
        }
        sum -= userRating;
        return sum / (ratings.size() - 1);
    }

    public void test(boolean writeToFile) {
        ArrayList<String> lines = new ArrayList<>();
        double sumError = 0;
        int canPredict = 0;
        int count = 0;

        lines.add("userID, itemID, actualRating, predictedRating, RMSE");

        for (Integer userID : users.keySet()) {
            User user = getUser(userID);
            for (Integer itemID : user.getItems()) {
                double actualRating = user.getRating(itemID);
                double predictedRating = meanItemRating(userID, itemID);
                double rmse = rootMeanSquaredError(actualRating, predictedRating);
                if (!Double.isNaN(predictedRating)) {
                    sumError += rmse;
                    canPredict++;
                }
                lines.add(userID+", "+itemID+", "+actualRating+", "+predictedRating+", "+rmse);
            }
        }
        double coverage = (double)canPredict / (lines.size() - 1); //Subtract 1 because of the first line
        double meanRMSE = sumError / canPredict;

        lines.add("coverage, meanRMSE");
        lines.add(coverage+", "+meanRMSE);

        if (writeToFile) {
            try {
                System.out.print("Saving predictions...");
                Files.write(Paths.get("predictions.csv"), lines);
                System.out.println("DONE");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        if (args[0] != null) Loader.setDataPath(args[0]);

        MeanRatingPrediction meanRatingPrediction = new MeanRatingPrediction(Loader.loadUsers(), Loader.loadItems());
        long start = System.nanoTime();
        for (int i = 0; i < 10; i++) {
            meanRatingPrediction.test(true);
        }
        long end = System.nanoTime();

        System.out.println("Time taken: " + (TimeUnit.NANOSECONDS.toMillis(end-start)/10.0) +" ms");

//        System.out.println(Math.sqrt(0));
    }
}

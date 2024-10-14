package Cargame;

import java.io.*;
import java.util.*;

public class HighScore {
    private static final String HIGH_SCORE_FILE = "high_scores.txt";
    private static final int MAX_HIGH_SCORES = 3;

    //Update the high scores with a new score with descending order by add new score to list(only top scores)

    public static void updateHighScores(int newScore) {
        List<Integer> scores = readHighScores();
        scores.add(newScore);
        Collections.sort(scores, Collections.reverseOrder());
        if (scores.size() > MAX_HIGH_SCORES) {
            scores = scores.subList(0, MAX_HIGH_SCORES);
        }
        writeHighScores(scores);
    }

     // Gets the top scores and Reads scores from file, return top scores

    public static List<Integer> getTopScores() {
        List<Integer> scores = readHighScores();
        Collections.sort(scores, Collections.reverseOrder());
        return scores.subList(0, Math.min(scores.size(), MAX_HIGH_SCORES));
    }

    private static List<Integer> readHighScores() {
        List<Integer> scores = new ArrayList<>();
        File file = new File(HIGH_SCORE_FILE);
        if (!file.exists()) {
            return scores;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                scores.add(Integer.parseInt(line.trim()));
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return scores;
    }

//    Writes high scores to file.

    private static void writeHighScores(List<Integer> scores) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE))) {
            for (int score : scores) {
                writer.write(String.valueOf(score));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

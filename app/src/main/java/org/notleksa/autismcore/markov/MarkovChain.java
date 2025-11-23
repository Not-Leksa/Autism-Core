package org.notleksa.autismcore.markov;

import java.util.*;

public class MarkovChain {

    private final int order;
    private final Random random = new Random();
    private final Map<String, List<String>> chain = new HashMap<>();
    private static final String DELIM = "\u0001";

    public MarkovChain(int order) {
        if (order < 1) throw new IllegalArgumentException("Order must be >= 1");
        this.order = order;
    }

    /**
     * Train the Markov chain with plain text
     */
    public void train(String text) {
        String[] tokens = text.split("\\s+");
        if (tokens.length <= order) return;

        for (int i = 0; i + order < tokens.length; i++) {

            // combine [i .. i+order-1] into a single key
            StringBuilder keyBuilder = new StringBuilder(tokens[i]);
            for (int j = 1; j < order; j++) {
                keyBuilder.append(DELIM).append(tokens[i + j]);
            }
            String key = keyBuilder.toString();

            String nextWord = tokens[i + order];
            chain.computeIfAbsent(key, k -> new ArrayList<>()).add(nextWord);
        }
    }

    /**
     * Generate text with N output words
     */
    public String generate(int words) {
        if (chain.isEmpty()) return "";

        List<String> keys = new ArrayList<>(chain.keySet());
        String key = keys.get(random.nextInt(keys.size()));
        String[] current = key.split(DELIM);

        StringBuilder out = new StringBuilder(String.join(" ", current));
        int generated = current.length;

        while (generated < words) {
            List<String> nextList = chain.get(key);
            if (nextList == null || nextList.isEmpty()) break;

            String next = nextList.get(random.nextInt(nextList.size()));
            out.append(" ").append(next);
            generated++;

            // slide the window
            StringBuilder newKey = new StringBuilder();
            for (int i = 1; i < current.length; i++) {
                if (i > 1) newKey.append(DELIM);
                newKey.append(current[i]);
            }
            newKey.append(DELIM).append(next);

            key = newKey.toString();
            current = key.split(DELIM);
        }

        return out.toString();
    }
}

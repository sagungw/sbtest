package io.wijaya.external.stockbit_04;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class App {

    // Only supports [a-z]+ word
    public static void main(String[] args) {
        String[] words = new String[]{"kita", "atik", "tika", "aku", "kia", "makan", "kua"};

        Map<String, List<String>> result = new HashMap<>();
        try {
            for (String w : words) {
                String key = generateMapKey(w);
                List<String> group = result.get(key);
                if (group == null)
                    group = new ArrayList<>();

                group.add(w);
                result.put(key, group);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (Entry<String, List<String>> group: result.entrySet()) {
            sb.append(" [");
            for (int i = 0; i < group.getValue().size(); i++) {
                if (i > 0)
                    sb.append(", ");

                sb.append("\"");
                sb.append(group.getValue().get(i));
                sb.append("\"");
            }
            sb.append("]\n");
        }
        sb.append("]");

        System.out.println(sb.toString());
    }

    public static String generateMapKey(String s) throws Exception {
        int[] charCount = new int[26];
        for (char c: s.toLowerCase().toCharArray()) {
            int i = c - 97;
            if(i > 25 || i < 0)
                throw new IllegalArgumentException();

            charCount[i]++;
        }

        StringBuilder sb = new StringBuilder();
        for (int i: charCount) {
            sb.append(i);
        }

        return sb.toString();
    }

}

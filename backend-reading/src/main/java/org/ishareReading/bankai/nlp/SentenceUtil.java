package org.ishareReading.bankai.nlp;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NotionalTokenizer;

import java.util.*;

public class SentenceUtil {
    private SentenceUtil() {
    }

    public static List<String> stopWords(String text) {
        List<Term> segment = NotionalTokenizer.segment(text);
        HashMap<String, Integer> map = new HashMap<>();
        for (Term term : segment) {
            int frequency = term.getFrequency(); // 改造，访问记录统计出现频率
            String word = term.word;
            if (word.length() > 1) { //短语才搜索
                map.put(word, frequency);
            }
        }
        return sortMapByValue(map);
    }

    /**
     * 去除停顿词
     * 按照出现频率返回关键字
     *
     * @param text
     * @return
     */
    public static List<String> split2stopWords(String text) {
        List<List<Term>> arr = NotionalTokenizer.seg2sentence(text);
        HashMap<String, Integer> map = new HashMap<>();
        for (List<Term> terms : arr) {
            for (Term term : terms) {
                int frequency = term.getFrequency(); // 改造，访问记录统计出现频率
                String word = term.word;
                if (word.length() > 1) { //短语才搜索
                    map.put(word, frequency);
                }
            }
        }
        return sortMapByValue(map);
    }

    public static List<String> sortMapByValue(Map<String, Integer> map) {
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(map.entrySet());
        entryList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        List<String> sortedKeys = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : entryList) {
            sortedKeys.add(entry.getKey());
        }
        return sortedKeys;
    }

}

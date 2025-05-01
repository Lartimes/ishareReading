package org.ishareReading.bankai.nlp;

import com.hankcs.hanlp.corpus.io.IOUtil;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.Vector;
import com.hankcs.hanlp.mining.word2vec.Word2VecTrainer;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import org.ishareReading.bankai.model.Types;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地模型 + 云端大模型 双重召回>>>训练本地模型，提升精准度
 */
public class WordVectorUtil {
    private static final int TOP_N_SIZE = 5;
    private static final Float WEAK_SIMILARITY = 0.26f;
    private static final String TRAIN_FILE_NAME =
            "D:\\Dev\\DataType\\Data-Type-JAVA\\data-type-algorithm\\src\\main\\resources\\" +
                    "data\\train\\wordVec.txt"; //测试环境
    private static final String MODEL_FILE_NAME =
            "D:\\Dev\\DataType\\Data-Type-JAVA\\data-type-algorithm\\src\\main\\resources\\" +
                    "data\\train\\hanlp-wiki-vec-zh.txt"; //生产环境
    private static final WordVectorUtil INSTANCE = new WordVectorUtil();
    private static final Word2VecTrainer trainerBuilder = new Word2VecTrainer();

    static {
        trainerBuilder.setLayerSize(300);
        trainerBuilder.setNumIterations(1 << 3);
        trainerBuilder.useNumThreads(Runtime.getRuntime().availableProcessors() * 2 + 1);
    }

    private WordVectorModel wordVectorModel;
    private DocVectorModel docVectorModel;

    private WordVectorUtil() {
        try {
            wordVectorModel = trainOrLoadModel();
            docVectorModel = new DocVectorModel(wordVectorModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected WordVectorModel trainOrLoadModel() throws IOException {
        if (!IOUtil.isFileExisted(MODEL_FILE_NAME)) {
            return trainerBuilder.train(TRAIN_FILE_NAME, MODEL_FILE_NAME);
        }

        return loadModel();
    }

//
//    public static void main(String[] args) throws IOException {
//        WordVectorUtil demo = WordVectorUtil.getInstance();
//        System.out.println("===========");
//        float similarity = demo.wordVectorModel.similarity("小狗", "小猫");
//        float similarity2 = demo.wordVectorModel.similarity("动漫", "宫崎骏");
//        Vector vector = demo.wordVectorModel.vector("小狗");
//        System.out.println(Arrays.toString(vector.getElementArray()));
//        System.out.println(demo.wordVectorModel.vector("小猫"));
//        System.out.println(similarity);
//        System.out.println(similarity2);
//        for (Map.Entry<String, Float> arr : demo.wordVectorModel.nearest("小狗")) { //去除topN
//            System.out.println(arr);
//        }
//        System.out.println("==========");
//        System.out.println(demo.generateVector(Arrays.asList("小狗", "小猫")));
//        System.out.println(demo.getSimilarKeywords(Arrays.asList("小狗", "小猫"), Collections.emptyList(), 10));
//    }

    protected WordVectorModel loadModel() throws IOException {
        return new WordVectorModel(MODEL_FILE_NAME);
    }

    public static WordVectorUtil getInstance() {
        return INSTANCE;
    }

    /**
     * 生成向量vector 300 layers, 训练模型设定 书籍 还是 帖子
     */
    public List<Types> generateVector(List<String> keywords, String type) {
        ArrayList<Types> result = new ArrayList<>(keywords.size());
        keywords.parallelStream().forEach(keyword -> {
            Vector vector = wordVectorModel.vector(keyword);
            Types types = new Types();
            types.setEmbedding(vector.getElementArray());
            types.setType(type);
            types.setTypeName(keyword);
            result.add(types);
        });
        return result;
    }

    /**
     * 从storage以及从训练库 中选出 与score 相关度高的keywords 并且必须包含source里的标签 >= size，某些条件下
     */
    public List<String> getSimilarKeywords(List<String> source, List<String> storage, int size) {
        if (source.isEmpty()) {
            return Collections.emptyList();
        }
        ConcurrentHashMap<Float, String> map = new ConcurrentHashMap<>();
        source.parallelStream().forEach(key -> {
            if (storage != null && !storage.isEmpty()) {
                storage.parallelStream().forEach(s -> {
                    float similarity = wordVectorModel.similarity(key, s);
                    if (similarity > WEAK_SIMILARITY) { //如果大于弱相关就取出
                        map.put(similarity, s);
                    }
                });
            }
            List<Map.Entry<String, Float>> nearest = wordVectorModel.nearest(key, TOP_N_SIZE);
            nearest.parallelStream().forEach(entry -> {
                Float value = entry.getValue();
                if (value > WEAK_SIMILARITY) { //如果大于弱相关就取出
                    map.put(value, entry.getKey());
                }
            });
        });
        List<Float> list = map.keySet().stream().sorted((aFloat, t1) ->
                Float.compare(t1, aFloat)).toList();
        List<Float> floats = list.subList(0,
                Math.min(size + size >>> 1, list.size()));
        Set<String> collect = new HashSet<>(source);
        for (Float aFloat : floats) {
            collect.add(map.get(aFloat));
        }
        return collect.stream().toList();
    }


}

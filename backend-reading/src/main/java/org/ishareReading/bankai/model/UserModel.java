package org.ishareReading.bankai.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 该用户模型只保留 七天 或者 下次用户登录之后要进行摘要最感兴趣的几个 定义加分规则？？ 三种方式Map？？？
 */
@Data
public class UserModel {

    /*总阅读时长*/
    private Integer readingTime;

    //    阅读频率 天数/周
    private Integer readingFrequency;
    //   bookTypes
    private Set<TypeScore<Books>> bookTypes = new HashSet<>(1 << 6);

    //  postsTypes
    private Set<TypeScore<Posts>> postTypes = new HashSet<>(1 << 6);

    public static void main(String[] args) {
        UserModel userModel = new UserModel();
        userModel.setReadingTime(10000);
        userModel.setReadingFrequency(5);
        HashSet<TypeScore<Books>> typeScores = new HashSet<>();
        Books books1 = new Books();
        books1.setId(1L);
        books1.setGenre("恐怖,文学");
        typeScores.add(new TypeScore<Books>(books1, 10.0, 30));
        Books books2 = new Books();
        books2.setId(2L);
        books2.setGenre("小说");
        typeScores.add(new TypeScore<Books>(books2, 5.0, 30));
        Books books3 = new Books();
        books3.setId(3L);
        books3.setGenre("C,B");
        typeScores.add(new TypeScore<Books>(books3, 3.8, 30));
        Books books4 = new Books();
        books4.setId(4L);
        books4.setGenre("B,D");
        typeScores.add(new TypeScore<Books>(books4, 2.1, 30));
        Books books5 = new Books();
        books5.setId(5L);
        books5.setGenre("E,C");
        typeScores.add(new TypeScore<Books>(books5, 5.3, 30));
        Books books6 = new Books();
        books6.setId(6L);
        books6.setGenre("E,C");
        typeScores.add(new TypeScore<Books>(books6, 1.5, 30));
        Books books7 = new Books();
        books7.setId(7L);
        books7.setGenre("E,F");
        typeScores.add(new TypeScore<Books>(books7, 1.3, 30));
        userModel.setBookTypes(typeScores);
        System.out.println(typeScores);
        String[] strings = userModel.initBooksProbabilityArray();
        System.out.println(Arrays.toString(strings));
    }

    /**
     * 对UserModel，books 生成胜率数组
     */
    private String[] initBooksProbabilityArray() {
        Set<String> set = new HashSet<>();
        for (TypeScore<Books> bookType : bookTypes) {
            set.addAll(bookType.getBooksTypes());
        }
        System.out.println("==========");
        int gapSize = set.size();
        System.out.println("size ： " + gapSize);
        ConcurrentHashMap<String, Double> map = new ConcurrentHashMap<>();
        // 为不同类型设置不同的初始权重
        Map<String, Double> initialWeights = new HashMap<>();
        Random random = new Random();
        set.forEach(s -> initialWeights.put(s, random.nextDouble()));
        set.forEach(s -> map.put(s, initialWeights.get(s)));

        LocalDate now = LocalDate.now();
        set.forEach(s -> {
            bookTypes.forEach(bookType -> {
                Books t = bookType.t;
                if (t.getGenre().contains(s)) {
                    double contribution;
                    if (random.nextBoolean() || bookType.readDate == null) {
                        // 原有的非线性计算方式
                        contribution = Math.pow(bookType.score, 2) / gapSize;
                    } else {
                        // 加入时间衰减的计算方式
                        long daysPassed = ChronoUnit.DAYS.between(bookType.readDate, now); //最近读书的时间
                        double decayFactor = Math.exp(-0.1 * daysPassed); // 时间衰减因子
                        contribution = Math.pow(bookType.score * bookType.time * decayFactor * (0.5 + random.nextDouble()), 2) / gapSize;
                    }
                    map.put(s, map.getOrDefault(s, 0.0) + Math.ceil(contribution));
                }
            });
        });

        map.forEach((k, v) -> System.out.println(k + " : " + v));
        System.out.println("==========");
        // 使用 BigDecimal 进行求和
        BigDecimal sum = map.values().stream()
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        double totalSum = sum.doubleValue();

        // 归一化处理
        map.forEach((k, v) -> map.put(k, v / totalSum * 100));

        System.out.println("==========");
        map.forEach((k, v) -> System.out.println(k + " : " + v));
        ArrayList<String> arr = new ArrayList<>();
        final AtomicInteger index = new AtomicInteger(0);
        map.forEach((k, v) -> {
            int i = index.get();
            int limit = i + v.intValue();
            while (i++ < limit) {
                arr.add(k);
            }
            index.addAndGet(v.intValue());
        });
        return arr.toArray(new String[0]);
    }

    /**
     * 初始化
     */
    private Long[] initPostsProbabilityArray() {
        ConcurrentHashMap<Long, Integer> map = new ConcurrentHashMap<>();
        for (TypeScore<Posts> postType : postTypes) {
            map.put(postType.t.getTypeId(), 0);
        }
        int size = map.size();
        final AtomicInteger count = new AtomicInteger(0);
        postTypes.stream().parallel().forEach(postsTypeScore -> {
            Posts t = postsTypeScore.t;
            Double score = postsTypeScore.score;
            Long typeId = t.getTypeId();
            int tmp =  ((int) Math.ceil(score) + size) / size; //避免一些type一直为0
            count.addAndGet(tmp);
            map.put(typeId, map.getOrDefault(typeId, 0) +tmp);
        });
        final Long[] probabilityArray = new Long[count.get()];

        final AtomicInteger index = new AtomicInteger(0);
        map.forEach((id, score) -> {
            int i = index.get();
            int limit = i + score;
            while (i++ < limit) {
                probabilityArray[i] = id;
            }
            index.set(limit);
        });
        return probabilityArray;
    }

    /**
     * map<t.id , .. 根据name向量化>
     *
     * @param <T>
     */
    public static class TypeScore<T> implements Comparable<TypeScore<T>> {
        private static final int LUCKY_NUMBER = 307314;
        public T t; //实体类
        public Double score; //分数 0-1 ，这个分数和 点赞 + 阅读时间 + 发表见解/帖子 + 收藏 有关
        public Integer time; //阅读时间
        public LocalDate readDate; //仅用于阅读

        @Override
        public String toString() {
            return "TypeScore{" +
                    "t=" + t +
                    ", score=" + score +
                    ", time=" + time +
                    ", readDate=" + readDate +
                    '}';
        }

        public TypeScore() {
        }

        public TypeScore(T t, Double score, Integer time) {
            this.t = t;
            this.score = score;
            this.time = time;
        }


        /**
         * 获取书籍类型
         *
         * @return
         */
        private Set<String> getBooksTypes() {
            HashSet<String> set = new HashSet<>();
            if (t instanceof Books book) {
                String[] split = book.getGenre().split(",");
                set.addAll(List.of(split));
            }
            return set;
        }

        private Set<Long> getPostsTypes() {
            HashSet<Long> set = new HashSet<>();
            if (t instanceof Posts post) {
                set.add(post.getId());
            }
            return set;
        }

        @Override
        public int hashCode() {
            if (t instanceof Books book) {
                return Objects.hashCode(book.getId());
            } else if (t instanceof Posts post) {
                return Objects.hashCode(post.getId());
            }
            return LUCKY_NUMBER;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            TypeScore<?> typeScore = (TypeScore<?>) o;
            if (typeScore.t instanceof Books books) {
                if (t instanceof Books current) {
                    return Objects.equals(current.getId(), books.getId());
                }
            }
            if (typeScore.t instanceof Posts posts) {
                if (t instanceof Posts current) {
                    return Objects.equals(current.getId(), posts.getId());
                }
            }
            return true;
        }

        @Override
        public int compareTo(TypeScore<T> tTypeScore) {
             return Double.compare(tTypeScore.score * 0.7 + tTypeScore.time * 0.3 ,
                     this.score * 0.7 + this.score * 0.3);
        }
    }

}


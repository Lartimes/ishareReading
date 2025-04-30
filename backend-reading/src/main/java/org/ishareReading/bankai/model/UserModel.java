package org.ishareReading.bankai.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class UserModel {
    /*阅读时长*/
    private Integer readingTime;

    //    阅读频率
    private Integer readingFrequency;
    //   bookTypes
    private Set<Map<Long, TypeScore<Types>>> bookTypes = new HashSet<>(1 << 6);
    //  postsTypes
    private Set<Map<Long, TypeScore<Types>>> postTypes = new HashSet<>(1 << 6);

    public static void main(String[] args) {
        System.out.println(1 << 6);
    }


    static class TypeScore<T> {
        private T t;
        private Double score;
    }
}


package org.ishareReading.bankai.service;

import org.ishareReading.bankai.model.AuthorFanInfo;

import java.util.List;

public interface AuthorService {
    List<AuthorFanInfo> getAuthorHotRank();
}

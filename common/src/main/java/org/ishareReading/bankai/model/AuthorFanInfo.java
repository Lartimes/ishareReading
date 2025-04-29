package org.ishareReading.bankai.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthorFanInfo {
    private Long id;
    private int fansCount;

    public AuthorFanInfo(Long id, int fansCount){
        this.id = id;
        this.fansCount = fansCount;
    }


}

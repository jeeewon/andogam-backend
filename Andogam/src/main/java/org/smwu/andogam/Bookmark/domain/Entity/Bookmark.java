package org.smwu.andogam.Bookmark.domain.Entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Bookmark {
    @Id
    @GeneratedValue
    private Long bid;

    @Column
    private String name;
    private String address;
    private String type;
    private double xPoint;
    private double yPoint;


    @Builder Bookmark(Long bid, String name, String address, String type, double xPoint, double yPoint){
        this.bid = bid;
        this.name = name;
        this.address = address;
        this.type = type;
        this.xPoint = xPoint;
        this.yPoint = yPoint;
    }


}

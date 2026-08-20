package org.smwu.andogam.Bookmark.dto;

import lombok.Getter;
import org.smwu.andogam.Bookmark.domain.Entity.Bookmark;

@Getter
public class BookmarkDetailDto {
    private Long bid;
    private String name;
    private String address;
    private String type;
    private double xPoint;
    private double yPoint;

    public  BookmarkDetailDto(Bookmark bookmark){
        this.bid = bookmark.getBid();
        this.name = bookmark.getName();
        this.address = bookmark.getAddress();
        this.type = bookmark.getType();
        this.xPoint = bookmark.getXPoint();
        this.yPoint = bookmark.getYPoint();
    }

}

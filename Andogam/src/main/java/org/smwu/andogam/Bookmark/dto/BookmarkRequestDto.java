package org.smwu.andogam.Bookmark.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BookmarkRequestDto {
    private String name;
    private String address;
    private String type;
    private double xPoint;
    private double yPoint;

    @Builder
    public BookmarkRequestDto(String name, String address, String type, double xPoint, double yPoint){
        this.name = name;
        this.address = address;
        this.type = type;
        this.xPoint = xPoint;
        this.yPoint = yPoint;
    }


}

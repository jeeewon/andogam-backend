package org.smwu.andogam.Bookmark.controller;

import lombok.RequiredArgsConstructor;
import org.smwu.andogam.Bookmark.domain.Entity.Bookmark;
import org.smwu.andogam.Bookmark.dto.BookmarkDetailDto;
import org.smwu.andogam.Bookmark.dto.BookmarkRequestDto;
import org.smwu.andogam.Bookmark.service.BookmarkService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/bookmark")
    public Bookmark registerBookmark(@RequestBody BookmarkRequestDto bookmarkRequestDto){
        return bookmarkService.registerBookmark(bookmarkRequestDto);
    }

    @DeleteMapping("/bookmark/{bid}")
    public String deleteBookmark(@PathVariable Long bid){
        return bookmarkService.deleteBookmark(bid);
    }

    @GetMapping("/bookmarks")
    public List<BookmarkDetailDto> listBookmark(){
        return bookmarkService.listBookmark();
    }
}
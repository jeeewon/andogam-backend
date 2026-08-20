package org.smwu.andogam.Bookmark.service;

import lombok.RequiredArgsConstructor;
import org.smwu.andogam.Bookmark.domain.Entity.Bookmark;
import org.smwu.andogam.Bookmark.domain.Repository.BookmarkRepository;
import org.smwu.andogam.Bookmark.dto.BookmarkDetailDto;
import org.smwu.andogam.Bookmark.dto.BookmarkRequestDto;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;

    public Bookmark registerBookmark(BookmarkRequestDto bookmarkRequestDto){
        Bookmark bookmark = Bookmark.builder()
                .name(bookmarkRequestDto.getName())
                .address(bookmarkRequestDto.getAddress())
                .type(bookmarkRequestDto.getType())
                .xPoint(bookmarkRequestDto.getXPoint())
                .yPoint(bookmarkRequestDto.getYPoint())
                .build();
        return bookmarkRepository.save(bookmark);
    }

    public String deleteBookmark(Long bid){
        Bookmark bookmark = bookmarkRepository.findById(bid).orElseThrow();
        bookmarkRepository.delete(bookmark);
        return "북마크 삭제 완료";
    }
    public List<BookmarkDetailDto> listBookmark(){
        return bookmarkRepository.findAll().stream()
                .sorted(Comparator.comparing(Bookmark::getBid).reversed())
                .map(BookmarkDetailDto::new)
                .collect(Collectors.toList());
    }
}

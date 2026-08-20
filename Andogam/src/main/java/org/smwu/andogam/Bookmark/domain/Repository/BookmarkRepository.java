package org.smwu.andogam.Bookmark.domain.Repository;

import org.smwu.andogam.Bookmark.domain.Entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
}

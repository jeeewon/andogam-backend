package org.smwu.andogam.Review.domain.repository;

import org.smwu.andogam.Review.domain.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    @Query(value = "SELECT * FROM review WHERE station_code=:stationCode ORDER BY created_date DESC",nativeQuery = true)
    List<Review> findByStation(String stationCode);
}

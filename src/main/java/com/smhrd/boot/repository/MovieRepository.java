package com.smhrd.boot.repository;

import com.smhrd.boot.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository          //JpaRepository<Entity 클래스 이름, @Id 필드의 타입>
public interface MovieRepository extends JpaRepository<Movie,Long> {
}

package com.smhrd.boot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor //기본생성자
@AllArgsConstructor //전체초기화생성자
@Getter
@Setter
@Entity //해당 클래스가 JPA Entity 임을 나타냄(필수)
@Table(name="MOVIES") //테이블 이름 설정
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieId; //영화 식별자(숫자 1~) -> pk

    @Column(nullable = false, length = 100)
    private String title; //영화 제목

    @Column(length = 100)
    private String director; //감독 이름

    @Column(name = "release_date")
    private LocalDate release; //개봉일자

    private String synopsis; //시놉시스

    @Column(length = 500)
    private String poster; //테이블 -> 포스터 이미지 파일의 제목.확장자
}

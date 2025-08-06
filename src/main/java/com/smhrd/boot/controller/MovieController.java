package com.smhrd.boot.controller;

import com.smhrd.boot.model.Movie;
import com.smhrd.boot.service.MovieService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

//Controller : 화면(.jsp, .html) 을 리턴 => 화면전환
//RestController : 데이터를 리턴 => 비동기통신
@Controller
public class MovieController {

    private final MovieService service;

    public MovieController(MovieService service){
        this.service = service;
    }

    //[GET]localhost:8089/
    @GetMapping("/")
    public String indexPage(Model model){
        //영화 리스트 불러오기 (DB연동)
        //MyBatis - Mapper(interface)
        //JPA - Repository(interface)
        //Controller(class) -> Service(class) -> Repository(interface)
        //Controller : 요청 / 응답 다루기
        //Service : 로직(요청 데이터 가공, 이미지 저장 ...)
        //Repository : DB 관련 작업(INSERT, DELETE, UPDATE, SELECT)
        List<Movie> movieList =  service.getMovieList();
        //sout => 자동완성
        //System.out.println(movieList.size());
        model.addAttribute("movieList",movieList);
        return "index";
    }

    //영화 추가 페이지 이동
    //localhost:8089/movie-add
    @GetMapping("/movie-add")
    public String addPage(){
        return "movie-add";
    }

    //영화 추가
    @PostMapping("/movies")
    public String addMovie(Movie movie, @RequestPart MultipartFile upload) throws IOException {
        //@RequestParam => 파라미터 하나하나 따로 받아줘야함
        //@ModelAttribute => 따로 정의한 Model 형태로 받을 수 있음 (ex. Movie), 생략
        //@PathVarible => 경로에 포함된 변수
        //@RequestPart => Multipart 형태 데이터 (file, json)
        Movie result = service.addMovie(movie, upload);
        
        if(result==null){ //추가 실패 -> movie-add.html
            return "redirect:/movie-add";
        }else{ //추가 성공 -> index.html
            //return "" => forwarding (재요청x)
            //redirect "/" 재요청하게 만들어야함
            return "redirect:/";
        }
    }

    @GetMapping("/movies/{movieId}")
    public String getDetailPage(@PathVariable Long movieId, Model model) throws IOException {
        //요청 데이터 받는 방법
        //@RequestParam : 데이터 1개씩 , 쿼리스트링 데이터 받을 때
        //@ModelAttribute : 객체형태로 묶어서 (생략)
        //------------form 태그
        //@PathVariable : 경로에 포함된 변수
        Movie movie = service.getMovieDetail(movieId);
        model.addAttribute("movie", movie);

        return "movie-detail";
    }

    @GetMapping("/movies/edit/{movieId}")
    public String getUpdatePage(@PathVariable Long movieId, Model model) throws IOException {
        Movie movie = service.getMovieDetail(movieId);
        model.addAttribute("movie", movie);

        return "movie-update";
    }

    @PostMapping("/movies/{movieId}")
    public String updateMovie(@PathVariable Long movieId, Movie movie){
        Movie result = service.updateMovie(movieId, movie);
        //result => null (movie-update)
        //result => Movie (movie-detail)
        if(result != null){
            return "redirect:/movies/"+movieId;
        }else{
            return "redirect:/movies/edit/"+movieId;
        }
    }

    @GetMapping("/movies/delete/{movieId}")
    public String deleteMovie(@PathVariable Long movieId){
        service.deleteMovie(movieId);
        return "redirect:/";
    }
}









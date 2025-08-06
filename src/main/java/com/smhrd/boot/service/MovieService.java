package com.smhrd.boot.service;

import com.smhrd.boot.model.Movie;
import com.smhrd.boot.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MovieService {

    //객체 관리 컨테이너 -> IoC Container -> 의존성 주입 (외부관리 객체 => 내부)
    //1. @Autowired : 필드주입 -> final (상수 선언) X
    //2. 생성자주입 (O) -> final O
    //MovieRepository mr = new MovieRepositoy();
    private final MovieRepository repository;
    private final S3Client s3Client;

    @Value("${ncp.s3.bucket}")
    private String bucketName;

    @Value("${ncp.s3.endpoint}")
    private String endpoint;

    public MovieService(MovieRepository repository, S3Client s3Client){
        this.repository = repository;
        this.s3Client = s3Client;
    }

    //영화 리스트 불러오기
    public List<Movie> getMovieList(){
        //select * from movies;
        List<Movie> movieList = repository.findAll();
        return movieList;
    }

    //영화 추가
    public Movie addMovie(Movie movie, MultipartFile upload) throws IOException {
        //1. 저장할 파일 이름+확장자 생성 (랜덤값)
        String originalFileName = upload.getOriginalFilename(); //선택한 파일 원래 이름 a.jpg
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".")); //확장자
        String uniqueFileName = "posters/"+UUID.randomUUID().toString() + extension; //중복되지 않는 파일이름 생성

//        //2. 저장 경로 설정
//        //(DB - 파일이름.확장자(문자열) / 이미지파일 -> 로컬 경로(C:/upload))
//        String uploadDir = "C:/upload/";
//        File saveDir = new File(uploadDir); //저장경로 확인
//        if(!saveDir.exists()) {
//            saveDir.mkdir(); //C:/upload 경로가 없는 경우에는 생성
//        }
//
//        //3. 실제 이미지 파일 저장
//        File uploadFile = new File(uploadDir + uniqueFileName);
//        upload.transferTo(uploadFile);

        // S3에 업로드
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .contentType(upload.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(upload.getInputStream(), upload.getSize()));

        //4. Movie객체 poster 필드값 초기화 (null -> uniqueFileName)
        movie.setPoster(uniqueFileName);

        Movie result = repository.save(movie); //result : 실제 추가(수정) 된 값
        return result;
    }

    //영화 상세보기
    public Movie getMovieDetail(Long movieId) throws IOException {
        Optional<Movie> movieOptional = repository.findById(movieId);
        //Optional : findById, 만약 없는 id를 넘겨준 경우에는 가지고올 데이터 없음 => Null
        //       => Null을 처리할 수 있는 기능이 포함되어 있는 상태

        //데이터를 정상적으로 가져왔으면 => Movie 객체 반환
        //가져오지 못하면(Null) => null 반환
        if(movieOptional.isPresent()){
            Movie movie = movieOptional.get();

//            //파일 확인 (경로)
//            String uploadDir = "C:/upload/"; //경로
//            String posterName = movie.getPoster(); //이름.확장자
//            File file = new File(uploadDir + posterName);
//
//            if(file.exists()){ //실제 있는 파일인지 확인
//                //파일 읽어오기 : 스트림(Stream) => byte (0,1)
//                byte[] fileContent = Files.readAllBytes(file.toPath());
//                //byte[] => 인코딩(base64) => String
//                String base64Image = Base64.getEncoder().encodeToString(fileContent);
//                movie.setPoster(base64Image); //이름 -> Base64로 인코딩된 이미지 문자열
//            }else{
//                movie.setPoster(null);
//            }
            String imageKey = movie.getPoster(); // posters/uuid.jpg 등

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(imageKey)
                    .build();

            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int nRead;
            while ((nRead = s3Object.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            byte[] imageBytes = buffer.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            movie.setPoster(base64Image);

            return movie;
        }else{
            return null;
        }
    }
    //select : find ~
    //delete : delete ~
    //insert/update : save~ (PK(Id) -> 없거나 혹은 null인경우 insert, 있는 경우 update)
    public Movie updateMovie(Long movieId, Movie movie){//movie -> 수정값
        //수정절차
        //1. 기존 데이터 가져오기
        Optional<Movie> optionalMovie = repository.findById(movieId);
        if(optionalMovie.isPresent()){
            //2. 기존 데이터(Movie)를 수정값으로 바꾸기
            Movie originalMovie = optionalMovie.get();
            originalMovie.setRelease(movie.getRelease());
            originalMovie.setDirector(movie.getDirector());
            originalMovie.setSynopsis(movie.getSynopsis());
            //3. 수정완료된 Movie 객체를 저장
            Movie result = repository.save(originalMovie);
            return result;
        }else{ //null
            return null;
        }
    }

    public void deleteMovie(Long movieId){
        repository.deleteById(movieId);
    }
}








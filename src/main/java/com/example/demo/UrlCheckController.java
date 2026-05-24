package com.example.demo;
import org.springframework.web.bind.annotation.*;
        import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@CrossOrigin("*") // 프론트엔드 연결 허용
public class UrlCheckController {

    @GetMapping("/api/check-url")
    public boolean checkUrl(@RequestParam String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD"); // 화면 내용 말고 상태 코드만 빠르게 요청
            connection.setConnectTimeout(3000);  // 3초 안에 응답 없으면 에러 처리
            connection.setReadTimeout(3000);

            // 사이트가 봇을 차단하는 걸 막기 위해 사람인 척(?) 속이는 헤더 추가
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = connection.getResponseCode();

            // 200번대(정상) ~ 300번대(리다이렉트)면 켜져 있는 걸로 간주
            return (200 <= responseCode && responseCode <= 399);
        } catch (Exception e) {
            return false; // 접속 실패하면 false 반환
        }
    }
}
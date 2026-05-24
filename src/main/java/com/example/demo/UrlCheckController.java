package com.example.demo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;

@RestController
@RequestMapping("/api/url")
@CrossOrigin(origins = "*")
public class UrlCheckController {

    static {
        // 공공기관 사이트들의 SSL(보안인증서) 에러로 인해 접속이 폭파되는 현상 방지 (무조건 신뢰)
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkUrl(@RequestParam String url) {
        try {
            URL targetUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();

            // 브라우저 실제 접속인 척 완벽 위장
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

            connection.setConnectTimeout(3000); // 3초 타임아웃
            connection.setReadTimeout(3000);

            int responseCode = connection.getResponseCode();

            // 명확하게 404 에러 코드를 뱉으면 확실하게 차단
            if (responseCode == 404) {
                return ResponseEntity.ok(false);
            }

            // 상태 코드가 200(정상)인데 화면만 404인 '가짜 정상' 잡아내기
            StringBuilder content = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                String inputLine;
                int lineCount = 0;
                while ((inputLine = in.readLine()) != null && lineCount < 40) { // 상위 40줄만 스캔
                    content.append(inputLine);
                    lineCount++;
                }
            } catch (Exception e) {
                // 본문을 읽다가 에러가 나면, 사이트 자체는 살아있을 확률이 높으므로 일단 살려줌
                return ResponseEntity.ok(true);
            }

            String htmlLower = content.toString().toLowerCase().replaceAll("\\s+", "");

            // 텍스트 기반 404 에러 검출
            if (htmlLower.contains("404notfound") ||
                    htmlLower.contains("페이지를찾을수") ||
                    htmlLower.contains("존재하지않는페이지") ||
                    htmlLower.contains("요청하신페이지를찾을")) {
                return ResponseEntity.ok(false); // 가짜 에러 페이지 차단
            }

            // 그 외에는 모두 정상으로 판정
            return ResponseEntity.ok(true);

        } catch (Exception e) {
            // 🚨 중요: 보안 프로그램 등으로 연결이 강제 차단당한 경우(`catch`),
            // 사이트가 죽었다고 판단하지 말고 일단 유저가 접속할 수 있게 true(생존)를 반환해 줌!
            System.out.println("⚠️ 단순 연결 오류로 패스 처리 (URL: " + url + ") -> 이유: " + e.getMessage());
            return ResponseEntity.ok(true);
        }
    }
}
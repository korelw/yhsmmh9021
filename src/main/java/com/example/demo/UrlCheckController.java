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

            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            int responseCode = connection.getResponseCode();

            if (responseCode == 404) {
                return ResponseEntity.ok(false);
            }
            StringBuilder content = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                String inputLine;
                int lineCount = 0;
                while ((inputLine = in.readLine()) != null && lineCount < 40) { // 상위 40줄만 스캔
                    content.append(inputLine);
                    lineCount++;
                }
            } catch (Exception e) {
                return ResponseEntity.ok(true);
            }

            String htmlLower = content.toString().toLowerCase().replaceAll("\\s+", "");

            if (htmlLower.contains("404notfound") ||
                    htmlLower.contains("페이지를찾을수") ||
                    htmlLower.contains("존재하지않는페이지") ||
                    htmlLower.contains("요청하신페이지를찾을")) {
                return ResponseEntity.ok(false); //
            }
            return ResponseEntity.ok(true);

        } catch (Exception e) {
            System.out.println("⚠️ 단순 연결 오류로 패스 처리 (URL: " + url + ") -> 이유: " + e.getMessage());
            return ResponseEntity.ok(true);
        }
    }
}
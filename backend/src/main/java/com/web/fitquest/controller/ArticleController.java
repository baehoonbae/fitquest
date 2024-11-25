package com.web.fitquest.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Hidden
@Slf4j
@RestController
@RequestMapping("/api/article")
@Tag(name = "Article", description = "검색 API")
public class ArticleController {

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    @Value("${youtube.api.key}")
    private String youtubeApiKey;

    @Operation(summary = "네이버 블로그 검색", description = "네이버 블로그 검색 API를 통해 블로그 검색을 수행합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "블로그 검색 성공"),
        @ApiResponse(responseCode = "400", description = "검색어 인코딩 실패")
    })
    @GetMapping("/search/blog")
    public ResponseEntity<String> searchBlog(
        @RequestParam String query,
        @RequestParam int start,
        @RequestParam int display) {
        String text;
        try {
            text = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            throw new RuntimeException("검색어 인코딩 실패", e);
        }

        String apiURL = "https://openapi.naver.com/v1/search/blog?query=" + text 
        + "&start=" + start
        + "&display=" + display
        + "&sort=sim" // 유사도 정렬
        + "&filter=all";  // 모든 블로그 포함

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        
        String responseBody = get(apiURL, requestHeaders);
        return ResponseEntity.ok(responseBody);
    }

    @Operation(summary = "네이버 이미지 검색", description = "네이버 이미지 검색 API를 통해 이미지 검색을 수행합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "이미지 검색 성공"),
        @ApiResponse(responseCode = "400", description = "검색어 인코딩 실패")
    })
    @GetMapping("/search/image")
    public ResponseEntity<String> searchImage(@RequestParam String query) {
        String text;
        try {
            text = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            throw new RuntimeException("검색어 인코딩 실패", e);
        }

        String apiURL = "https://openapi.naver.com/v1/search/image?query=" + text;

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);

        String responseBody = get(apiURL, requestHeaders);
        return ResponseEntity.ok(responseBody);
    }

    @Operation(summary = "유튜브 동영상 검색", description = "유튜브 동영상 검색 API를 통해 동영상 검색을 수행합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "동영상 검색 성공"),
        @ApiResponse(responseCode = "400", description = "검색어 인코딩 실패")
    })
    @GetMapping("/search/video")
    public ResponseEntity<String> searchVideo(
        @RequestParam String query,
        @RequestParam(defaultValue = "10") int maxResults,
        @RequestParam(required = false) String pageToken) {
        String text;
        try {
            text = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            throw new RuntimeException("검색어 인코딩 실패", e);
        }

        String apiURL = "https://www.googleapis.com/youtube/v3/search"
            + "?part=snippet"
            + "&q=" + text
            + "&type=video"
            + "&maxResults=" + maxResults
            + "&key=" + youtubeApiKey
            + "&regionCode=KR"
            + "&relevanceLanguage=ko";
        
        if (pageToken != null && !pageToken.isEmpty()) {
            apiURL += "&pageToken=" + pageToken;
        }

        Map<String, String> requestHeaders = new HashMap<>();
        String responseBody = get(apiURL, requestHeaders);
        return ResponseEntity.ok(responseBody);
    }

    private String get(String apiUrl, Map<String, String> requestHeaders) {
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return readBody(con.getInputStream());
            } else {
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestProperty("Accept-Charset", "UTF-8");
            return con;
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private String readBody(InputStream body) {
        try (BufferedReader lineReader = new BufferedReader(
                new InputStreamReader(body, StandardCharsets.UTF_8))) {
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }
            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
        }
    }
}
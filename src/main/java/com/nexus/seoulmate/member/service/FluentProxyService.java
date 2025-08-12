package com.nexus.seoulmate.member.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.seoulmate.config.FluentApiProperties;
import com.nexus.seoulmate.member.domain.enums.Languages;
import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.exception.status.ErrorStatus;
import org.springframework.http.*;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.google.cloud.storage.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FluentProxyService {
    private final Storage storage = StorageOptions.getDefaultInstance().getService();
    private final RestTemplate restTemplate;
    private final FluentApiProperties fluentApiProperties;

    public FluentProxyService(RestTemplate restTemplate, FluentApiProperties fluentApiProperties) {
        this.restTemplate = restTemplate;
        this.fluentApiProperties = fluentApiProperties;
    }

    public String fluentFlow(MultipartFile audioFile, Languages language){
        System.out.println(storage.getOptions().getProjectId());

        // 1. 로그인
        String apiKey = fluentApiProperties.getKey();
        String username = fluentApiProperties.getUsername();
        String password = fluentApiProperties.getPassword();
        String xAccessToken = getAccessToken(apiKey, username, password);

        // 2. 음성 파일 제출
        String postId = null;
        switch (language){
            case KOREAN -> postId = "P163524106";
            case ENGLISH -> postId = "P174024107";
        }

        // 3. 오디오파일 업로드
        String bucketName = "bucket-seoulmate-250826";
        String bucketFolderName = "example-folder";

        // String audioUrl = "https://storage.googleapis.com/bucket-seoulmate-250826/example-folder/english-89.wav";
        String audioUrl = uploadAudioFile(audioFile, bucketName, bucketFolderName);

        // 4. 채점 요청 및 결과 받기
        String result = getScore(xAccessToken, postId, audioUrl);

        return result;
    }

    // 1. 로그인
    public String getAccessToken(String apiKey, String username, String password){
        try{
            String url = "https://thefluent.me/api/swagger/login";
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-api-key", apiKey);
            headers.setBasicAuth(username, password);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            // JSON 응답에서 토큰만 추출
            String jsonResponse = response.getBody();
            return extractTokenFromJson(jsonResponse);
        } catch (RestClientException e) {
            throw new CustomException(ErrorStatus.FLUENT_LOGIN_FAILED);
        }
    }

    // JSON 에서 토큰 추출
    private String extractTokenFromJson(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            
            if (root.has("token")) {
                return root.get("token").asText();
            }
            throw new CustomException(ErrorStatus.FLUENT_TOKEN_NOT_FOUND);
        } catch (Exception e) {
            throw new CustomException(ErrorStatus.FLUENT_TOKEN_PARSE_FAILED);
        }
    }

    // 2. 포스트 등록
    private String createPost(String token, String postLanguageId, String postTitle, String postContent){
        String url = "https://thefluent.me/api/swagger/post";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-access-token", token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("post_language_id", postLanguageId);
            body.put("post_title", postTitle);
            body.put("post_content", postContent);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    url,
                    request,
                    String.class
            );
            return response.getBody();
        } catch (RestClientException e) {
            throw new CustomException(ErrorStatus.POST_CREATE_FAILED);
        }
    }

    // 3. 포스트 조회
    public String getAllPosts(String token, Integer page, Integer perPage) {
        try {
            String url = "https://thefluent.me/api/swagger/post"; // 실제 엔드포인트
            if (page != null || perPage != null) {
                StringBuilder sb = new StringBuilder(url);
                sb.append("?");
                if (page != null) sb.append("page=").append(page).append("&");
                if (perPage != null) sb.append("per_page=").append(perPage);
                url = sb.toString().replaceAll("&$", "");
            }
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-access-token", token);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    String.class
            );
            return response.getBody();
        } catch (RestClientException e) {
            throw new CustomException(ErrorStatus.GET_POSTS_FAILED);
        }
    }

    // 4. 오디오 파일 버킷에 업로드
    public String uploadAudioFile(MultipartFile audioFile, String bucketName, String bucketFolderName){
        String fileName = bucketFolderName + "/" + UUID.randomUUID() + ".wav";
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("audio/wav").build();

        try {
            storage.create(blobInfo, audioFile.getBytes()); // 파일 업로드
            // storage.createAcl(blobId, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER)); // 파일에 공개 권한 부여
            return "https://storage.googleapis.com/" + bucketName + "/" + fileName;
        } catch (IOException e){
            throw new CustomException(ErrorStatus.FLUENT_AUDIO_UPLOAD_FAILED);
        }
    }

    // 5. 음성 파일 제출하기 (결과 얻기) - POST /swagger/score/{post_id}
    public String getScore(String token, String postId, String audioUrl){
        try {
            String url = "https://thefluent.me/api/swagger/score/" + postId;

            HttpHeaders headers = new HttpHeaders();
            headers.set("x-access-token", token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("audio_provided", audioUrl);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    url,
                    request,
                    String.class
            );

            // JSON 응답에서 overall_points만 추출
            String jsonResponse = response.getBody();
            return extractOverallPointsFromJson(jsonResponse);
            // return response.getBody(); <- 기존 전체 결과 불러오기
        } catch (Exception e) {
            throw new CustomException(ErrorStatus.FLUENT_RESULT_PARSE_FAILED);
        }
    }

    // JSON에서 overall_points 추출
    private String extractOverallPointsFromJson(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            // overall_result_data 배열에서 첫 번째 객체의 overall_points 추출
            for (JsonNode node : root) {
                if (node.has("overall_result_data")) {
                    JsonNode overallResultData = node.get("overall_result_data");
                    if (overallResultData.isArray() && overallResultData.size() > 0) {
                        JsonNode firstResult = overallResultData.get(0);
                        if (firstResult.has("overall_points")) {
                            return firstResult.get("overall_points").asText();
                        }
                    }
                }
            }
            throw new CustomException(ErrorStatus.FLUENT_OVERALL_POINT_NOT_FOUND);
        } catch (Exception e) {
            throw new CustomException(ErrorStatus.FLUENT_OVERALL_POINT_PARSE_FAILED);
        }
    }
}

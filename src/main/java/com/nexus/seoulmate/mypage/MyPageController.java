package com.nexus.seoulmate.mypage;

import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.mypage.dto.HobbyUpdateRequest;
import com.nexus.seoulmate.mypage.dto.MyPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.nexus.seoulmate.exception.status.SuccessStatus.*;

@RestController
@RequestMapping("/my-page")
@Tag(name = "마이페이지", description = "마이페이지 관련 API")
public class MyPageController {

    private final MyPageService myPageService;

    public MyPageController(MyPageService myPageService) {
        this.myPageService = myPageService;
    }

    @Operation(summary = "마이페이지 조회 API")
    @GetMapping
    public Response<MyPageResponse> getMyProfile(){
        MyPageResponse dto = myPageService.getMyProfile();
        return Response.success(MY_PAGE_FETCH_SUCCESS, dto);
    }

    @Operation(summary = "프로필 이미지 수정 API")
    @PutMapping(value = "/update-profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<Object> updateProfileImage(@RequestPart("profileImage") MultipartFile profileImage){
        myPageService.updateProfileImage(profileImage);
        return Response.success(PROFILE_IMAGE_UPDATE_SUCCESS, null);
    }

    @Operation(summary = "프로필 한 줄 소개 수정 API")
    @PutMapping("/update-profile-bio")
    public Response<Object> updateProfileBio(@RequestBody String bio){
        myPageService.updateProfileBio(bio);
        return Response.success(PROFILE_BIO_UPDATE_SUCCESS, null);
    }

    @Operation(summary = "취미 수정 API")
    @PutMapping("/update-hobby")
    public Response<Object> updateHobbies(@RequestBody HobbyUpdateRequest hobbyUpdateRequest){
        myPageService.updateHobbies(hobbyUpdateRequest);
        return Response.success(HOBBY_UPDATE_SUCCESS, null);
    }

    @Operation(summary = "언어 레벨테스트 재응시 API")
    @PatchMapping(value = "/update-language-level", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<Object> updateLanguageLevel(@RequestPart("audioFile") MultipartFile audioFile,
                                                @RequestParam("language") String language){
        myPageService.updateLanguageLevel(audioFile, language);
        return Response.success(LANGUAGE_LEVEL_UPDATE_SUCCESS, null);
    }
}

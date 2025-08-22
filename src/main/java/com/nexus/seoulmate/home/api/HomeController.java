package com.nexus.seoulmate.home.api;

import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.global.status.SuccessStatus;
import com.nexus.seoulmate.home.api.dto.response.HomeFeedRes;
import com.nexus.seoulmate.home.api.dto.response.MeetingBasicInfoRes;
import com.nexus.seoulmate.home.application.HomeService;
import com.nexus.seoulmate.member.domain.enums.HobbyCategory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
@Tag(name = "홈", description = "서울메이트 메인 API")
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    @Operation(summary = "서울메이트 메인페이지지 조회", description = "인증된 사용자의 서울메이트 메인 정보를 조회합니다.")
    public Response<HomeFeedRes> getHomeFeed(HttpServletRequest request) {

        HomeFeedRes feed = homeService.buildHome(request);
        return Response.success(SuccessStatus.SUCCESS, feed);
    }

    @GetMapping("/categories/{category}/meetings")
    public Response<List<MeetingBasicInfoRes>> getMeetingsByCategory(
            @PathVariable("category") String categoryStr,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        var category = HobbyCategory.fromDisplayName(categoryStr);
        var list = homeService.listByCategory(category, page, size);
        return Response.success(SuccessStatus.SUCCESS, list);
    }
}

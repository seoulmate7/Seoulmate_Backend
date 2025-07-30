package com.nexus.seoulmate.meeting.api;

import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.exception.status.SuccessStatus;
import com.nexus.seoulmate.meeting.api.dto.request.MeetingSearchReq;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingListRes;
import com.nexus.seoulmate.meeting.application.search.MeetingSearchService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/meetings")
@RequiredArgsConstructor
public class MeetingSearchController {

    private final MeetingSearchService meetingSearchService;

    @Operation(summary = "모임 검색 API")
    @GetMapping("/search")
    public ResponseEntity<Response<List<MeetingListRes>>> searchMeetings(
            @ModelAttribute MeetingSearchReq req
    ){
        List<MeetingListRes> results = meetingSearchService.searchMeetings(req);
        return ResponseEntity.ok(Response.success(SuccessStatus.SEARCH_SUCCESS, results));
    }
}

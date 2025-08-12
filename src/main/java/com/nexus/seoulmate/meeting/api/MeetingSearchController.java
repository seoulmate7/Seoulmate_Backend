package com.nexus.seoulmate.meeting.api;

import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.exception.status.SuccessStatus;
import com.nexus.seoulmate.meeting.api.dto.request.MeetingSearchReq;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingListRes;
import com.nexus.seoulmate.meeting.application.search.MeetingSearchService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meetings")
@RequiredArgsConstructor
public class MeetingSearchController {

    private final MeetingSearchService meetingSearchService;

    @Operation(summary = "모임 검색 API")
    @PostMapping("/search")
    public ResponseEntity<Response<List<MeetingListRes>>> searchMeetings(
            @Valid @RequestBody MeetingSearchReq req
    ){
        List<MeetingListRes> data = meetingSearchService.searchMeetings(req);
        return ResponseEntity.ok(Response.success(SuccessStatus.SEARCH_SUCCESS, data));
    }
}

package com.nexus.seoulmate.member.controller;

import com.nexus.seoulmate.member.domain.enums.Languages;
import com.nexus.seoulmate.member.service.FluentProxyService;
import com.nexus.seoulmate.exception.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.nexus.seoulmate.exception.status.SuccessStatus.LEVEL_TEST_SUCCESS;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class FluentProxyController {

    private final FluentProxyService fluentProxyService;

    @GetMapping("/signup/language/level-test")
    public Response<String> testFluentFlow(@RequestPart("audioFile") MultipartFile audioFile,
                                           @RequestParam("language") Languages language) {
        String result = fluentProxyService.fluentFlow(audioFile, language);
        return Response.success(LEVEL_TEST_SUCCESS, result);
    }
}

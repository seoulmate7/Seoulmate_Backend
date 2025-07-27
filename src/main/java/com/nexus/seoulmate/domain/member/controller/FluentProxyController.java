package com.nexus.seoulmate.domain.member.controller;

import com.nexus.seoulmate.domain.member.domain.enums.Languages;
import com.nexus.seoulmate.domain.member.service.FluentProxyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class FluentProxyController {

    private final FluentProxyService fluentProxyService;

    @GetMapping("/signup/language/level-test")
    public ResponseEntity<String> testFluentFlow(@RequestPart("audioFile") MultipartFile audioFile,
                                                 @RequestParam("language") Languages language) {
        String result = fluentProxyService.fluentFlow(audioFile, language);
        return ResponseEntity.ok(result);
    }
}

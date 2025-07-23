//package com.nexus.seoulmate.domain.test;
//
//import com.nexus.seoulmate.exception.Response;
//import com.nexus.seoulmate.exception.status.SuccessStatus;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class TestMemberController {
//    @Autowired
//    private TestMemberRepository testMemberRepository;
//
//    @GetMapping("/test/api")
//    public String test() {
//        return "this is test.";
//    }
//
//    @PostMapping("/test/rds")
//    public Response<Object> testRds() {
//        TestMember member = new TestMember(1L, "test", "1234");
//        testMemberRepository.save(member);
//        return Response.success(SuccessStatus.SUCCESS, null);
//    }
//}

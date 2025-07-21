package com.jpabook.jpashop.api;


import com.jpabook.jpashop.domain.Member;
import com.jpabook.jpashop.service.MemberService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;


    /**
     * V1: 엔티티(Member)를 직접 RequestBody에 바인딩함
     * -> ❌ 엔티티에 검증 로직이 노출되고, API 변경 시 도메인 클래스에 영향을 줌
     */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }


    /**
     * V2: 별도의 DTO(CreateMemberRequest)를 사용해 입력값을 받음
     * -> ✅ API 스펙이 엔티티로부터 분리되어 유지보수와 확장에 유리
     */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName((request.getName()));
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
        @PathVariable("id")Long id ,
        @RequestBody @Valid UpdateMemberRequest request){

        memberService.update(id,request.getName());
        Member member = memberService.findOne(id);
        member.setName(request.getName());
        return new UpdateMemberResponse(id,member.getName());
    }



    @Data
    static class UpdateMemberRequest{
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }


    @Data
    static class CreateMemberRequest {

        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

}

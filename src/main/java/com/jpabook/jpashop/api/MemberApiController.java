package com.jpabook.jpashop.api;


import com.jpabook.jpashop.domain.Member;
import com.jpabook.jpashop.service.MemberService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 조회 V1: 응답 값으로 엔티티를 직접 외부에 노출한다.
     *
     * ❌ 문제점
     * - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
     * - 엔티티의 모든 값이 기본적으로 노출된다.
     * - 응답 스펙을 맞추기 위해 @JsonIgnore 등 추가 설정이 필요하다.
     * - 하나의 엔티티에 여러 API 요구사항을 담기 어렵다.
     * - 엔티티가 변경되면 API 스펙도 같이 변경된다.
     * - 컬렉션 직접 반환 시 추후 확장 어려움 (→ 별도 Result 클래스로 해결해야 함)
     *
     * ✅ 결론
     * - API 응답 스펙에 맞는 별도의 DTO를 사용하는 것이 바람직하다.
     */

// 조회 V1: ❌ 안 좋은 버전 - 모든 엔티티가 노출됨, @JsonIgnore로 막는 건 최악. API가 이거 하나인가? 화면에 종속되지 말자!
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();

    }


    /**
     * 조회 V2: 엔티티를 DTO로 변환하여 반환
     *
     * ✅ 장점
     * - API 스펙이 엔티티 변경에 영향을 받지 않음
     * - 필요한 정보만 선별해서 응답 가능
     * - 추후 확장 시에도 유연하게 대응 가능 (예: 페이징 추가, 필터링 조건 등)
     */

    @GetMapping("/api/v2/members")
    public Result membersV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
            .map(m -> new MemberDto(m.getName()))
            .collect(Collectors.toList());

        return new Result(collect.size(),collect);


    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto{
        private String name;
    }


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
        @PathVariable("id") Long id,
        @RequestBody @Valid UpdateMemberRequest request) {

        memberService.update(id, request.getName());
        Member member = memberService.findOne(id);
        member.setName(request.getName());
        return new UpdateMemberResponse(id, member.getName());
    }


    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
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

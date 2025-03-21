//package com.jpabook.jpashop;
//
//import com.jpabook.jpashop.domain.Member;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//
//@SpringBootTest
//class MemberRepositoryTest {
//
//
//    @Autowired MemberRepository memberRepository;
//
//    @Test
//    @Transactional // 테스트에서는 무저건 rollback함
//    @Rollback(false) // 그거 막음
//
//    public void testMember(){
//        //given
//        Member member = new Member();
//        member.setUsername("memberA");
//        //when
//        Long saveId = memberRepository.save(member);
//        Member findMember = memberRepository.find(saveId);
//
//
//        //then
//        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
//        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
//        Assertions.assertThat(findMember).isEqualTo(member); // true
//        System.out.println("findMember = member " + (findMember == member));
//    }
//
//
//}
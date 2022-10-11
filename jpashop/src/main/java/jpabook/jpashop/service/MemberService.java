package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
// final이 있는 필드만 생성자를 만들어준다
// AllArgsConstructor은 생성자하나인 경우에 생성자를 생성해준다.

public class MemberService {

    private final MemberRepository memberRepository;
//
//    // @Autowired 생성자가 하나일 경우에는 Autowired 생략해도 된다.
//    public MemberService(MemberRepository memberRepository){
//        this.memberRepository = memberRepository;
//    }

    /**
     *회원 가입
     */
    @Transactional(readOnly = false)
    public Long join(Member member){
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }


    // member의 name에 유니크 제약조건을 달아주면 더 안전하다
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }


    //회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }
}
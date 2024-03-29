package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.domain.Member;
import study.datajpa.domain.Team;
import study.datajpa.dto.MemberDto;
import study.datajpa.exception.NotMemberException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;
    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    EntityManager em;
    @Autowired
    private TeamRepository teamRepository;


    @Test
    public void 멤버저장() throws Exception {
        //given
        Member member = createMember("ko");

        //when
        Member saveMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(saveMember.getId());

        //then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void 멤버저장_findById() throws Exception {
        //given
        List<Member> members = memberCreateAndSave();

        //when
        Member findMember1 = memberJpaRepository.findById(members.get(0).getId()).orElseThrow(
                () -> {throw new NotMemberException();});
        Member findMember2 = memberJpaRepository.findById(members.get(1).getId()).orElseThrow(
                () -> {throw new NotMemberException();});

        //then
        assertThat(findMember1).isEqualTo(members.get(0));
        assertThat(findMember2).isEqualTo(members.get(1));
    }

    @Test
    public void 멤버저장_예외_findById() throws Exception {
        //given
        Member member1 = createMember("ko");

        //when
        memberJpaRepository.save(member1);

        //then
        org.junit.jupiter.api.Assertions.assertThrows(
                NotMemberException.class, () -> {
                    memberJpaRepository.findById(45L).orElseThrow(() -> {throw new NotMemberException();});
                }
        );

        Throwable exception = org.junit.jupiter.api.Assertions.assertThrows(
                NotMemberException.class, () -> {
                    memberJpaRepository.findById(45L).orElseThrow(() -> {throw new NotMemberException();});
                }
        );
        org.junit.jupiter.api.Assertions.assertEquals("member not found", exception.getMessage());

    }

    @Test
    public void 리스트_조회검증() throws Exception {
        //given
        List<Member> members = memberCreateAndSave();

        //when
        List<Member> allMembers = memberJpaRepository.findAll();
        long memberCount = memberJpaRepository.count();

        //then
        assertThat(allMembers.size()).isEqualTo(2);
        assertThat(memberCount).isEqualTo(2);

    }

    @Test
    public void 삭제_검증() throws Exception {
        //given
        List<Member> members = memberCreateAndSave();

        //when
        memberJpaRepository.delete(members.get(0));
        long count = memberJpaRepository.count();

        //then
        assertThat(count).isEqualTo(1);

    }
    

    private static Member createMember(String name) {
        Member member = Member
                .builder()
                .username(name)
                .build();

        return member;
    }

    private List<Member> memberCreateAndSave() {
        List<Member> allMembers = new ArrayList<>();

        Member member1 = createMember("ko");
        Member member2 = createMember("se");

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        allMembers.add(member1);
        allMembers.add(member2);

        return allMembers;
    }

    @Test
    public void findByUsernameAgeGreaterThen() throws Exception {
        //given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        //when
        List<Member> result = memberJpaRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        //then
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        Member findMember = memberRepository.findMemberByUsername("AAA");


    }

    //interface
    @Test
    public void paging() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        // then
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

//    @Test
//    void slice() {
//        //given
//        memberRepository.save(new Member("member1", 10));
//        memberRepository.save(new Member("member2", 10));
//        memberRepository.save(new Member("member3", 10));
//        memberRepository.save(new Member("member4", 10));
//        memberRepository.save(new Member("member5", 10));
//
//        int age = 10;
//        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
//
//        //when
//        Slice<Member> page = memberRepository.findByAge(age, pageRequest);
//
//        // then
//        List<Member> content = page.getContent();
//
//        assertThat(content.size()).isEqualTo(3);
//        assertThat(page.hasNext()).isTrue();
//    }
    // slice 적용 원칙
    // slice는 요청할 때, 3개를 요청하면 3 + 1 해서 4개를 요청함
    // total query를 날리지 않음


    @Test
    public void bulkUpdate() {
        //given
        memberJpaRepository.save(new Member("member1", 19));
        memberJpaRepository.save(new Member("member2", 19));
        memberJpaRepository.save(new Member("member3", 19));
        memberJpaRepository.save(new Member("member4", 20));
        memberJpaRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberJpaRepository.bulkAgePlus(20);

        //then
        assertThat(resultCount).isEqualTo(2);
    }

    @Test
    public void bulkUpdate2() {
        //given
        memberRepository.save(new Member("member1", 19));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 19));
        memberRepository.save(new Member("member4", 20));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);
        em.flush();
        em.clear();

        Member member5 = memberRepository.findMemberByUsername("member5");

        //then
        assertThat(resultCount).isEqualTo(2);
        assertThat(member5.getAge()).isEqualTo(40);
    }


    @Test
    public void findMemberLazy() {
        // given

        // member1 -> teamA
        // member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        // 영속성 컨텍스트를 저장하고 날림
        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }


    @Test
    public void findEntityGraphByUsername() {
        // given

        // member1 -> teamA
        // member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member1", 20, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        // 영속성 컨텍스트를 저장하고 날림
        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        //given
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.updateUsername("member2");

        em.flush();
    }

    @Test
    public void lock() {
        //given
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findLockByUsername("member1");
        findMember.updateUsername("member2");

        em.flush();

    }

    @Test
    public void callCustom() {
        List<Member> members = memberRepository.findMemberCustom();
    }


    @Test
    public void projections() {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1");

        List<UsernameOnlyDto> result2 = memberRepository.findProjectionsDtoByUsername("m1");

        List<UsernameOnlyDto> result3 = memberRepository.findProjectionsGenericByUsername("m1", UsernameOnlyDto.class);
        List<NestedClosedProjections> result4 = memberRepository.findProjectionsGenericByUsername("username", NestedClosedProjections.class);

        Page<MemberProjection> result5 = memberRepository.findByNativeProjection(PageRequest.of(1, 10));
    }

}
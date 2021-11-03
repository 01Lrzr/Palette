package com.palette;

import com.palette.domain.group.Group;
import com.palette.domain.group.MemberGroup;
import com.palette.domain.member.Member;
import com.palette.repository.GroupRepository;
import com.palette.repository.MemberGroupRepository;
import com.palette.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class GroupTests {
    @Autowired
    MemberRepository memberRepo;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    MemberGroupRepository memberGroupRepository;

    @BeforeEach
    public void setUp() {

        // 회원생성
        Member member1 = new Member("1234", "wltn", "wltnfile");
        memberRepo.save(member1);

        Member member2 = new Member("1234", "skfk", "skfkfile");
        memberRepo.save(member2);

        Member member3 = new Member("1234", "wogns", "wognsfile");
        memberRepo.save(member3);

        Member member4 = new Member("1234", "ruddls", "ruddlsfile");
        memberRepo.save(member4);

        // 그룹생성
        Group group = Group.builder().
                groupName("그룹1").
                groupIntroduction("테스트 그룹 1입니다.")
                .build();
        groupRepository.save(group);

        Group group2 = Group.builder()
                .groupName("그룹2")
                .groupIntroduction("테스트 그룹 2입니다")
                .build();
        groupRepository.save(group2);

        // 멤버 그룹 생성 후 member에 추가해주기
        MemberGroup memberGroup = new MemberGroup();
        memberGroupRepository.save(memberGroup);
        memberGroup.addMemberGroup(group,member1);

        MemberGroup memberGroup2 = new MemberGroup();
        memberGroupRepository.save(memberGroup2);
        memberGroup2.addMemberGroup(group,member2);

        MemberGroup memberGroup3 = new MemberGroup();
        memberGroupRepository.save(memberGroup3);
        memberGroup3.addMemberGroup(group2,member3);

        MemberGroup memberGroup4 = new MemberGroup();
        memberGroupRepository.save(memberGroup4);
        memberGroup4.addMemberGroup(group2,member4);

    }// end method

    @Test
    public void 그룹의_멤버확인(){
        Member findMember = memberRepo.findAll().get(0); //그룹1 의 멤버
        Member findMember2 = memberRepo.findAll().get(1); //그룹1 의 멤버
        Member findMember3 = memberRepo.findAll().get(2); //그룹2 의 멤버
        Member findMember4 = memberRepo.findAll().get(3); //그룹2 의 멤버

        Group group1 = groupRepository.findAll().get(0);
        Group group2 = groupRepository.findAll().get(1);

        assertThat(findMember.getMemberGroups().get(0).getGroup()).isEqualTo(group1);
        assertThat(findMember2.getMemberGroups().get(0).getGroup()).isEqualTo(group1);
        assertThat(findMember3.getMemberGroups().get(0).getGroup()).isEqualTo(group2);
        assertThat(findMember4.getMemberGroups().get(0).getGroup()).isEqualTo(group2);
    }

}

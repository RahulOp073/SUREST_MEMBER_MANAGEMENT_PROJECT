package org.surest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.surest.dto.MemberDto;
import org.surest.entity.Member;
import org.surest.exception.EmailAlreadyExistsException;
import org.surest.exception.MemberNotFoundException;
import org.surest.repository.MemberRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private UUID id;
    private Member member;
    private MemberDto dto;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();

        member = new Member();
        member.setId(id);
        member.setFirstName("Rittik");
        member.setLastName("Chowdhury");
        member.setDateOfBirth(LocalDate.of(1999, 1, 1));
        member.setEmail("Rittik@example.com");

        dto = new MemberDto();
        dto.setFirstName("Rittik");
        dto.setLastName("Chowdhury");
        dto.setDateOfBirth(LocalDate.of(1999, 1, 1));
        dto.setEmail("Rittik@example.com");
    }

    @Test
    void create_success() {
        when(memberRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        MemberDto created = memberService.create(dto);

        assertThat(created.getEmail()).isEqualTo(dto.getEmail());
        assertThat(created.getFirstName()).isEqualTo(dto.getFirstName());
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void create_duplicateEmail_throwsException() {
        when(memberRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> memberService.create(dto))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(memberRepository, never()).save(any());
    }

    @Test
    void getById_success() {
        when(memberRepository.findById(id)).thenReturn(Optional.of(member));

        MemberDto result = memberService.getById(id);

        assertThat(result.getEmail()).isEqualTo(member.getEmail());
        assertThat(result.getFirstName()).isEqualTo(member.getFirstName());
    }

    @Test
    void getById_notFound_throwsException() {
        when(memberRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getById(id))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void getAll_success() {
        when(memberRepository.findAll()).thenReturn(List.of(member));

        List<MemberDto> members = memberService.getAll();

        assertThat(members).hasSize(1);
        assertThat(members.get(0).getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    void update_success() {
        MemberDto updateDto = MemberDto.builder()
                .firstName("Updated")
                .lastName("Name")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .email("updated@example.com")
                .build();

        when(memberRepository.findById(id)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        MemberDto updated = memberService.update(id, updateDto);

        verify(memberRepository).save(any(Member.class));
        assertThat(updated.getFirstName()).isEqualTo(updateDto.getFirstName());
        assertThat(updated.getEmail()).isEqualTo(updateDto.getEmail());
    }


    @Test
    void update_notFound_throwsException() {
        when(memberRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.update(id, dto))
                .isInstanceOf(MemberNotFoundException.class);

        verify(memberRepository, never()).save(any());
    }

    @Test
    void delete_success() {
        when(memberRepository.findById(id)).thenReturn(Optional.of(member));

        memberService.delete(id);

        verify(memberRepository).delete(member);
    }

    @Test
    void delete_notFound_throwsException() {
        when(memberRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.delete(id))
                .isInstanceOf(MemberNotFoundException.class);

        verify(memberRepository, never()).delete(any());
    }
}

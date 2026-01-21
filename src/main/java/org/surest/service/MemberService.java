package org.surest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.surest.dto.MemberDto;
import org.surest.entity.Member;
import org.surest.exception.EmailAlreadyExistsException;
import org.surest.exception.MemberNotFoundException;
import org.surest.repository.MemberRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberDto create(MemberDto memberDto) {
        if (memberRepository.existsByEmail(memberDto.getEmail())) {
            throw new EmailAlreadyExistsException(memberDto.getEmail());
        }

        Member member = new Member();
        member.setFirstName(memberDto.getFirstName());
        member.setLastName(memberDto.getLastName());
        member.setDateOfBirth(memberDto.getDateOfBirth());
        member.setEmail(memberDto.getEmail());

        Member saved = memberRepository.save(member);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public MemberDto getById(UUID id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id));
        return toDto(member);
    }

    @Transactional(readOnly = true)
    public List<MemberDto> getAll() {
        return memberRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MemberDto update(UUID id, MemberDto memberDto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id));

        member.setFirstName(memberDto.getFirstName());
        member.setLastName(memberDto.getLastName());
        member.setDateOfBirth(memberDto.getDateOfBirth());
        member.setEmail(memberDto.getEmail());

        Member updated = memberRepository.save(member);
        return toDto(updated);
    }

    @Transactional
    public void delete(UUID id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id));
        memberRepository.delete(member);
    }

    private MemberDto toDto(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .dateOfBirth(member.getDateOfBirth())
                .email(member.getEmail())
                .build();
    }
}

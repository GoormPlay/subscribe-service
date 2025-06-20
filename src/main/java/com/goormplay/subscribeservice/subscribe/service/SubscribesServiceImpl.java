package com.goormplay.subscribeservice.subscribe.service;

import com.goormplay.subscribeservice.subscribe.dto.SubScribe.SubScribeStatusDto;
import com.goormplay.subscribeservice.subscribe.entity.Subscribe;
import com.goormplay.subscribeservice.subscribe.exception.Subscribe.SubscribeException;
import com.goormplay.subscribeservice.subscribe.repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static com.goormplay.subscribeservice.subscribe.exception.Subscribe.SubscribeExceptionType.NOT_FOUND_MEMBER;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscribesServiceImpl implements SubscribeService{

    private final SubscribeRepository subscribeRepository;




    @Override
    public SubScribeStatusDto findMemberProfile(String memberId) {
        log.info("Subscribe Service :  멤버 구독 정보 Dto 생성");

        Subscribe subscribe = findSubscribeByMemberId(memberId);
        return SubScribeStatusDto.builder().
                subscriptionStartDate(subscribe.getSubscriptionStartDate()).
                subscriptionEndDate(subscribe.getSubscriptionEndDate()).
                isCancelScheduled(subscribe.getIsCancelScheduled()).
                isSubscribed(subscribe.isSubscribed()).
                build();
    }

    @Override
    @Transactional
    public void changeSubscribeStatus(String memberId) {
        log.info("Subscribe Service :  멤버 구독 정보 변경");
        Subscribe subscribe = findSubscribeByMemberId(memberId);
        subscribe.toggleCancelScheduled();
    }

    @Transactional
    @Override
    public void joinSubscribe(String memberId) {
        log.info("Subscribe Service :  멤버 구독 정보 생성");

        subscribeRepository.save(Subscribe.builder()
                .id(UUID.randomUUID().toString())
                .subscriptionStartDate(LocalDate.now())
                .subscriptionEndDate(LocalDate.now().plusMonths(1))
                .isCancelScheduled(false)
                .memberId(memberId).build());
    }

    @Transactional
    @Override
    public void deleteSubscribe(String memberId) {
        log.info("Subscribe Service :  멤버 구독 정보 생성 보상 트랜잭션, 구독 정보 삭제");

        Subscribe subscribe = subscribeRepository.findByMemberId(memberId).orElseThrow(()->new SubscribeException(NOT_FOUND_MEMBER));
        subscribeRepository.delete(subscribe);
    }

    public Subscribe findSubscribeByMemberId(String memberId){
        log.info("Subscribe Service :  멤버 구독 정보 조회");
        return subscribeRepository.findByMemberId(memberId).orElseThrow(()->new SubscribeException(NOT_FOUND_MEMBER));
    }




 }

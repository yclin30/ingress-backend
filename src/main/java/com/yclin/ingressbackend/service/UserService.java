package com.yclin.ingressbackend.service;

import com.yclin.ingressbackend.dto.user.FactionDto;
import com.yclin.ingressbackend.dto.user.UserDetailDto;
import com.yclin.ingressbackend.dto.user.UserUpdateRequestDto;
import com.yclin.ingressbackend.entity.domain.Faction;
import com.yclin.ingressbackend.entity.domain.User;
import com.yclin.ingressbackend.repository.FactionRepository;
import com.yclin.ingressbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FactionRepository factionRepository;

    @Transactional(readOnly = true)
    public UserDetailDto getUserProfile(User userFromContext) {
        // 1. 不再信任来自 SecurityContext 的 user 对象。
        // 2. 根据它的 ID，从数据库重新加载一个全新的、实时的、受当前事务管理的 User 实体。
        User freshUser = userRepository.findById(userFromContext.getId())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));

        // 3. 在这个全新的实体上执行所有操作。
        return convertToUserDetailDto(freshUser);
    }

    @Transactional
    public UserDetailDto updateUserProfile(User userFromContext, UserUpdateRequestDto requestDto) {
        // 同样，从数据库重新加载一个实时的实体。
        User userToUpdate = userRepository.findById(userFromContext.getId())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));

        // 在这个实时的实体上进行修改
        if (requestDto.getGender() != null) userToUpdate.setGender(requestDto.getGender());
        if (requestDto.getIntroduction() != null) userToUpdate.setIntroduction(requestDto.getIntroduction());

        // 为了方便测试，我们允许更改阵营
        if (requestDto.getFactionId() != null) {
            Faction faction = factionRepository.findById(requestDto.getFactionId())
                    .orElseThrow(() -> new RuntimeException("Faction not found with id: " + requestDto.getFactionId()));
            userToUpdate.setFaction(faction);
        }

        // 由于 userToUpdate 是受管实体，这里的 save 不是必需的，但保留也无害。
        // JPA 会在事务提交时自动同步更改。

        return convertToUserDetailDto(userToUpdate);
    }

    private UserDetailDto convertToUserDetailDto(User user) {
        // 这个方法现在总是接收一个实时的、在事务内的实体，因此懒加载不会出问题。
        FactionDto factionDto = (user.getFaction() != null)
                ? new FactionDto(user.getFaction().getId(), user.getFaction().getName())
                : null;

        return UserDetailDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .gender(user.getGender())
                .introduction(user.getIntroduction())
                .level(user.getLevel())
                .experience(user.getExperience())
                .isBanned(user.getIsBanned())
                .faction(factionDto)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
package com.userservice.service;

import com.userservice.controller.UserController;
import com.userservice.dto.UserRequestDto;
import com.userservice.dto.UserResponseDto;
import com.userservice.entity.User;
import com.userservice.repository.UserRepository;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public EntityModel<UserResponseDto> createUser(UserRequestDto requestDto) {
        User user = new User();
        user.setName(requestDto.getName());
        user.setEmail(requestDto.getEmail());
        user.setAge(requestDto.getAge());

        User savedUser = userRepository.save(user);
        UserResponseDto dto = new UserResponseDto(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getAge(),
                savedUser.getCreatedAt()
        );

        dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserById(savedUser.getId())).withSelfRel());
        dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getAllUsers()).withRel("users"));

        return EntityModel.of(dto);
    }

    public EntityModel<UserResponseDto> getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        UserResponseDto dto = new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );

        dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserById(id)).withSelfRel());
        dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getAllUsers()).withRel("users"));

        return EntityModel.of(dto);
    }

    public List<EntityModel<UserResponseDto>> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserResponseDto dto = new UserResponseDto(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.getAge(),
                            user.getCreatedAt()
                    );
                    dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserById(user.getId())).withSelfRel());
                    return EntityModel.of(dto);
                })
                .collect(Collectors.toList());
    }

    public EntityModel<UserResponseDto> updateUser(Long id, UserRequestDto requestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        user.setName(requestDto.getName());
        user.setEmail(requestDto.getEmail());
        user.setAge(requestDto.getAge());

        User updatedUser = userRepository.save(user);
        UserResponseDto dto = new UserResponseDto(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getAge(),
                updatedUser.getCreatedAt()
        );

        dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserById(id)).withSelfRel());
        dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getAllUsers()).withRel("users"));

        return EntityModel.of(dto);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

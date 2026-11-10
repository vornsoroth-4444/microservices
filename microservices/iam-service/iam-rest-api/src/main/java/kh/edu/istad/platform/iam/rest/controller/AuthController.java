package kh.edu.istad.platform.iam.rest.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.iam.application.dto.RegisterUserCommand;
import kh.edu.istad.platform.iam.application.dto.UserResult;
import kh.edu.istad.platform.iam.application.usecase.user.RegisterUserUseCase;
import kh.edu.istad.platform.iam.rest.dto.RestDtoMapper;
import kh.edu.istad.platform.iam.rest.dto.request.RegisterUserRequest;
import kh.edu.istad.platform.iam.rest.dto.response.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        RegisterUserCommand cmd = RegisterUserCommand.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .confirmPassword(request.getConfirmPassword())
                .givenName(request.getGivenName())
                .familyName(request.getFamilyName())
                .locale(request.getLocale())
                .build();

        UserResult result = registerUserUseCase.execute(cmd);
        return ResponseEntity.status(HttpStatus.CREATED).body(RestDtoMapper.toUserResponse(result));
    }
}

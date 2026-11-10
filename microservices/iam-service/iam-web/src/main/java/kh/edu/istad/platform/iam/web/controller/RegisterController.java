package kh.edu.istad.platform.iam.web.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.iam.application.dto.RegisterUserCommand;
import kh.edu.istad.platform.iam.application.usecase.user.RegisterUserUseCase;
import kh.edu.istad.platform.iam.web.dto.RegisterForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    private final RegisterUserUseCase registerUserUseCase;

    public RegisterController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new RegisterForm());
        }
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("form") RegisterForm form,
                                 BindingResult bindingResult,
                                 Model model) {
        if (form.getPassword() != null && !form.getPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "register.passwordMismatch", "Passwords do not match");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            RegisterUserCommand cmd = RegisterUserCommand.builder()
                    .username(form.getUsername())
                    .email(form.getEmail())
                    .password(form.getPassword())
                    .confirmPassword(form.getConfirmPassword())
                    .givenName(form.getGivenName())
                    .familyName(form.getFamilyName())
                    .locale(form.getLocale())
                    .build();

            registerUserUseCase.execute(cmd);
            return "redirect:/login?registered=true";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "register";
        }
    }
}

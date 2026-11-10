package kh.edu.istad.platform.iam.web.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.iam.application.dto.ChangePasswordCommand;
import kh.edu.istad.platform.iam.application.usecase.user.ChangePasswordUseCase;
import kh.edu.istad.platform.iam.domain.model.User;
import kh.edu.istad.platform.iam.web.dto.ChangePasswordForm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class PasswordController {

    private final WebUserResolver webUserResolver;
    private final ChangePasswordUseCase changePasswordUseCase;

    public PasswordController(WebUserResolver webUserResolver,
                              ChangePasswordUseCase changePasswordUseCase) {
        this.webUserResolver = webUserResolver;
        this.changePasswordUseCase = changePasswordUseCase;
    }

    @GetMapping("/profile/password")
    public String changePasswordPage(Authentication auth, Model model) {
        Optional<User> userOpt = webUserResolver.resolveCurrentUser(auth);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new ChangePasswordForm());
        }
        return "change-password";
    }

    @PostMapping("/profile/password")
    public String handleChangePassword(Authentication auth,
                                       @Valid @ModelAttribute("form") ChangePasswordForm form,
                                       BindingResult bindingResult,
                                       Model model) {
        Optional<User> userOpt = webUserResolver.resolveCurrentUser(auth);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        if (form.getNewPassword() != null && !form.getNewPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "changePassword.mismatch", "New passwords do not match");
        }

        if (bindingResult.hasErrors()) {
            return "change-password";
        }

        try {
            User user = userOpt.get();
            ChangePasswordCommand cmd = new ChangePasswordCommand(
                    user.getUuid(),
                    form.getCurrentPassword(),
                    form.getNewPassword(),
                    form.getConfirmPassword()
            );
            changePasswordUseCase.execute(cmd);
            return "redirect:/?passwordChanged=true";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "change-password";
        }
    }
}

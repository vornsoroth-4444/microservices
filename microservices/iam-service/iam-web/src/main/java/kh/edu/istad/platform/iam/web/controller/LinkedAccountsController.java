package kh.edu.istad.platform.iam.web.controller;

import kh.edu.istad.platform.iam.application.dto.IdentityResult;
import kh.edu.istad.platform.iam.application.usecase.identity.ListUserIdentitiesUseCase;
import kh.edu.istad.platform.iam.application.usecase.identity.UnlinkIdentityUseCase;
import kh.edu.istad.platform.iam.domain.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/profile/accounts")
public class LinkedAccountsController {

    private final WebUserResolver webUserResolver;
    private final ListUserIdentitiesUseCase listUserIdentitiesUseCase;
    private final UnlinkIdentityUseCase unlinkIdentityUseCase;

    public LinkedAccountsController(WebUserResolver webUserResolver,
                                  ListUserIdentitiesUseCase listUserIdentitiesUseCase,
                                  UnlinkIdentityUseCase unlinkIdentityUseCase) {
        this.webUserResolver = webUserResolver;
        this.listUserIdentitiesUseCase = listUserIdentitiesUseCase;
        this.unlinkIdentityUseCase = unlinkIdentityUseCase;
    }

    @GetMapping
    public String linkedAccounts(Authentication auth, Model model) {
        Optional<User> userOpt = webUserResolver.resolveCurrentUser(auth);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();
        List<IdentityResult> identities = listUserIdentitiesUseCase.execute(user.getUuid());

        model.addAttribute("identities", identities);
        model.addAttribute("hasPassword", user.hasPassword());
        return "linked-accounts";
    }

    @PostMapping("/{identityUuid}/unlink")
    public String unlinkAccount(Authentication auth,
                                @PathVariable UUID identityUuid,
                                RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = webUserResolver.resolveCurrentUser(auth);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        try {
            User user = userOpt.get();
            unlinkIdentityUseCase.execute(user.getUuid(), identityUuid);
            redirectAttributes.addFlashAttribute("successMessage", "Account successfully unlinked");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/profile/accounts";
    }
}

package kh.edu.istad.platform.iam.web.controller;

import kh.edu.istad.platform.iam.application.dto.UpdateUserProfileCommand;
import kh.edu.istad.platform.iam.application.dto.UserAuthoritiesResult;
import kh.edu.istad.platform.iam.application.dto.UserResult;
import kh.edu.istad.platform.iam.application.usecase.user.GetUserAuthoritiesUseCase;
import kh.edu.istad.platform.iam.application.usecase.user.GetUserUseCase;
import kh.edu.istad.platform.iam.application.usecase.user.UpdateUserProfileUseCase;
import kh.edu.istad.platform.iam.domain.model.Address;
import kh.edu.istad.platform.iam.domain.model.User;
import kh.edu.istad.platform.iam.web.dto.ProfileEditForm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class ProfileController {

    private final WebUserResolver webUserResolver;
    private final GetUserUseCase getUserUseCase;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;

    public ProfileController(WebUserResolver webUserResolver,
                             GetUserUseCase getUserUseCase,
                             GetUserAuthoritiesUseCase getUserAuthoritiesUseCase,
                             UpdateUserProfileUseCase updateUserProfileUseCase) {
        this.webUserResolver = webUserResolver;
        this.getUserUseCase = getUserUseCase;
        this.getUserAuthoritiesUseCase = getUserAuthoritiesUseCase;
        this.updateUserProfileUseCase = updateUserProfileUseCase;
    }

    @GetMapping({"/", "/profile"})
    public String home(Authentication auth,
                       @RequestParam(name = "updated", required = false) String updated,
                       @RequestParam(name = "passwordChanged", required = false) String passwordChanged,
                       Model model) {
        Optional<User> userOpt = webUserResolver.resolveCurrentUser(auth);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();
        UserResult userResult = getUserUseCase.execute(user.getUuid());
        UserAuthoritiesResult authoritiesResult = getUserAuthoritiesUseCase.execute(user.getUuid());

        model.addAttribute("user", userResult);
        model.addAttribute("authorities", authoritiesResult);
        model.addAttribute("updated", updated != null);
        model.addAttribute("passwordChanged", passwordChanged != null);
        return "index";
    }

    @GetMapping("/profile/edit")
    public String editProfilePage(Authentication auth, Model model) {
        Optional<User> userOpt = webUserResolver.resolveCurrentUser(auth);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();
        UserResult userResult = getUserUseCase.execute(user.getUuid());

        ProfileEditForm form = new ProfileEditForm();
        form.setGivenName(userResult.getGivenName());
        form.setFamilyName(userResult.getFamilyName());
        form.setMiddleName(userResult.getMiddleName());
        form.setNickname(userResult.getNickname());
        form.setGender(userResult.getGender());
        form.setBirthdate(userResult.getBirthdate());
        form.setProfileUrl(userResult.getProfileUrl());
        form.setPictureUrl(userResult.getPictureUrl());
        form.setWebsiteUrl(userResult.getWebsiteUrl());
        form.setZoneinfo(userResult.getZoneinfo());
        form.setLocale(userResult.getLocale());
        form.setPhoneNumber(userResult.getPhoneNumber());

        if (userResult.getAddress() != null) {
            form.setStreetAddress(userResult.getAddress().getStreetAddress());
            form.setLocality(userResult.getAddress().getLocality());
            form.setRegion(userResult.getAddress().getRegion());
            form.setPostalCode(userResult.getAddress().getPostalCode());
            form.setCountry(userResult.getAddress().getCountry());
        }

        model.addAttribute("form", form);
        return "profile-edit";
    }

    @PostMapping("/profile/edit")
    public String handleEditProfile(Authentication auth,
                                   @ModelAttribute("form") ProfileEditForm form,
                                   Model model) {
        Optional<User> userOpt = webUserResolver.resolveCurrentUser(auth);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();
        Address address = Address.builder()
                .streetAddress(form.getStreetAddress())
                .locality(form.getLocality())
                .region(form.getRegion())
                .postalCode(form.getPostalCode())
                .country(form.getCountry())
                .build();

        UpdateUserProfileCommand cmd = UpdateUserProfileCommand.builder()
                .userUuid(user.getUuid())
                .givenName(form.getGivenName())
                .familyName(form.getFamilyName())
                .middleName(form.getMiddleName())
                .nickname(form.getNickname())
                .gender(form.getGender())
                .birthdate(form.getBirthdate())
                .profileUrl(form.getProfileUrl())
                .pictureUrl(form.getPictureUrl())
                .websiteUrl(form.getWebsiteUrl())
                .zoneinfo(form.getZoneinfo())
                .locale(form.getLocale())
                .phoneNumber(form.getPhoneNumber())
                .address(address)
                .build();

        updateUserProfileUseCase.execute(cmd);
        return "redirect:/?updated=true";
    }
}

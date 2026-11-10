package kh.edu.istad.platform.iam.web.i18n;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class I18nAdvice {

    private final I18n i18n;

    public I18nAdvice(I18n i18n) {
        this.i18n = i18n;
    }

    @ModelAttribute("i18n")
    public I18n populateI18n() {
        return i18n;
    }
}

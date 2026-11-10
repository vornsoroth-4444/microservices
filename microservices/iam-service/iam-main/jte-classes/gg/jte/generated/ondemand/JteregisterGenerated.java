package gg.jte.generated.ondemand;
@SuppressWarnings("unchecked")
@javax.annotation.processing.Generated("gg.jte.TemplateEngine")
public final class JteregisterGenerated {
	public static final String JTE_NAME = "register.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,0,0,5,5,5,5,5,5,5,5,5,5,9,9,9,9,9,9,18,22,22,22,26,26,26,26,27,27,27,29,29,29,29,30,30,30,36,36,36,37,37,37,40,40,43,43,43,45,45,50,50,50,51,51,51,51,51,51,51,51,51,55,55,55,56,56,56,56,56,56,56,56,56,62,62,62,63,63,63,63,63,63,63,63,63,68,68,68,69,69,69,69,69,69,69,69,69,75,75,75,80,80,80,87,87,87,90,90,90,90,90,90,90,90,91,91,91,91,91,91,91,91,97,97,97,102,102,102,103,103,103,108,108,108,0,1,2,2,2,2};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, kh.edu.istad.platform.iam.web.i18n.I18n i18n, kh.edu.istad.platform.iam.web.dto.RegisterForm form, String errorMessage) {
		jteOutput.writeContent("\n<!DOCTYPE html>\n<html");
		var __jte_html_attribute_0 = i18n.getLang();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_0)) {
			jteOutput.writeContent(" lang=\"");
			jteOutput.setContext("html", "lang");
			jteOutput.writeUserContent(__jte_html_attribute_0);
			jteOutput.setContext("html", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(">\n<head>\n    <meta charset=\"UTF-8\">\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n    <title>");
		jteOutput.setContext("title", null);
		jteOutput.writeUserContent(i18n.t("register.title"));
		jteOutput.writeContent(" - ");
		jteOutput.setContext("title", null);
		jteOutput.writeUserContent(i18n.t("brand"));
		jteOutput.writeContent("</title>\n    <link href=\"https://fonts.googleapis.com/css2?family=Battambang:wght@400;700&family=Inter:wght@400;500;600;700&display=swap\" rel=\"stylesheet\">\n    <script src=\"https://cdn.tailwindcss.com\"></script>\n    <style>\n        body { font-family: 'Inter', 'Battambang', sans-serif; }\n    </style>\n</head>\n<body class=\"bg-slate-50 text-slate-800 min-h-screen flex items-center justify-center p-4\">\n    <div class=\"max-w-lg w-full bg-white rounded-2xl shadow-xl border border-slate-100 p-8 my-8\">\n        ");
		jteOutput.writeContent("\n        <div class=\"flex items-center justify-between mb-6\">\n            <div class=\"flex items-center space-x-2 text-indigo-600 font-bold text-lg tracking-tight\">\n                <svg class=\"w-6 h-6\" fill=\"none\" stroke=\"currentColor\" viewBox=\"0 0 24 24\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"2\" d=\"M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z\"></path></svg>\n                <span>");
		jteOutput.setContext("span", null);
		jteOutput.writeUserContent(i18n.t("brand"));
		jteOutput.writeContent("</span>\n            </div>\n            \n            <div class=\"inline-flex rounded-lg border border-slate-200 bg-slate-50 p-1 text-xs font-medium\">\n                <a href=\"?lang=en\" class=\"px-2 py-0.5 rounded transition ");
		jteOutput.setContext("a", "class");
		jteOutput.writeUserContent("en".equals(i18n.getLang()) ? "bg-white text-indigo-600 shadow-xs font-semibold" : "text-slate-600");
		jteOutput.setContext("a", null);
		jteOutput.writeContent("\">\n                    ");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(i18n.t("lang.english"));
		jteOutput.writeContent("\n                </a>\n                <a href=\"?lang=km\" class=\"px-2 py-0.5 rounded transition ");
		jteOutput.setContext("a", "class");
		jteOutput.writeUserContent("km".equals(i18n.getLang()) ? "bg-white text-indigo-600 shadow-xs font-semibold" : "text-slate-600");
		jteOutput.setContext("a", null);
		jteOutput.writeContent("\">\n                    ");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(i18n.t("lang.khmer"));
		jteOutput.writeContent("\n                </a>\n            </div>\n        </div>\n\n        <div class=\"mb-6\">\n            <h1 class=\"text-2xl font-bold tracking-tight text-slate-900\">");
		jteOutput.setContext("h1", null);
		jteOutput.writeUserContent(i18n.t("register.title"));
		jteOutput.writeContent("</h1>\n            <p class=\"text-sm text-slate-500 mt-1\">");
		jteOutput.setContext("p", null);
		jteOutput.writeUserContent(i18n.t("register.subtitle"));
		jteOutput.writeContent("</p>\n        </div>\n\n        ");
		if (errorMessage != null) {
			jteOutput.writeContent("\n            <div class=\"mb-4 p-3 bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg flex items-center space-x-2\">\n                <svg class=\"w-5 h-5 flex-shrink-0\" fill=\"currentColor\" viewBox=\"0 0 20 20\"><path fill-rule=\"evenodd\" d=\"M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z\" clip-rule=\"evenodd\"/></svg>\n                <span>");
			jteOutput.setContext("span", null);
			jteOutput.writeUserContent(errorMessage);
			jteOutput.writeContent("</span>\n            </div>\n        ");
		}
		jteOutput.writeContent("\n\n        <form action=\"/register\" method=\"post\" class=\"space-y-4\">\n            <div class=\"grid grid-cols-2 gap-4\">\n                <div>\n                    <label for=\"givenName\" class=\"block text-xs font-semibold uppercase text-slate-600 mb-1\">");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(i18n.t("register.givenName"));
		jteOutput.writeContent("</label>\n                    <input type=\"text\" id=\"givenName\" name=\"givenName\"");
		var __jte_html_attribute_1 = form != null ? form.getGivenName() : "";
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_1)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_1);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(" required\n                           class=\"w-full px-3.5 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\n                </div>\n                <div>\n                    <label for=\"familyName\" class=\"block text-xs font-semibold uppercase text-slate-600 mb-1\">");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(i18n.t("register.familyName"));
		jteOutput.writeContent("</label>\n                    <input type=\"text\" id=\"familyName\" name=\"familyName\"");
		var __jte_html_attribute_2 = form != null ? form.getFamilyName() : "";
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_2)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_2);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(" required\n                           class=\"w-full px-3.5 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\n                </div>\n            </div>\n\n            <div>\n                <label for=\"username\" class=\"block text-xs font-semibold uppercase text-slate-600 mb-1\">");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(i18n.t("register.username"));
		jteOutput.writeContent("</label>\n                <input type=\"text\" id=\"username\" name=\"username\"");
		var __jte_html_attribute_3 = form != null ? form.getUsername() : "";
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_3)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_3);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(" required\n                       class=\"w-full px-3.5 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\n            </div>\n\n            <div>\n                <label for=\"email\" class=\"block text-xs font-semibold uppercase text-slate-600 mb-1\">");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(i18n.t("register.email"));
		jteOutput.writeContent("</label>\n                <input type=\"email\" id=\"email\" name=\"email\"");
		var __jte_html_attribute_4 = form != null ? form.getEmail() : "";
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_4)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_4);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(" required\n                       class=\"w-full px-3.5 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\n            </div>\n\n            <div class=\"grid grid-cols-2 gap-4\">\n                <div>\n                    <label for=\"password\" class=\"block text-xs font-semibold uppercase text-slate-600 mb-1\">");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(i18n.t("register.password"));
		jteOutput.writeContent("</label>\n                    <input type=\"password\" id=\"password\" name=\"password\" required\n                           class=\"w-full px-3.5 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\n                </div>\n                <div>\n                    <label for=\"confirmPassword\" class=\"block text-xs font-semibold uppercase text-slate-600 mb-1\">");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(i18n.t("register.confirmPassword"));
		jteOutput.writeContent("</label>\n                    <input type=\"password\" id=\"confirmPassword\" name=\"confirmPassword\" required\n                           class=\"w-full px-3.5 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\n                </div>\n            </div>\n\n            <div>\n                <label for=\"locale\" class=\"block text-xs font-semibold uppercase text-slate-600 mb-1\">");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(i18n.t("register.locale"));
		jteOutput.writeContent("</label>\n                <select id=\"locale\" name=\"locale\"\n                        class=\"w-full px-3.5 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\n                    <option value=\"en\"");
		var __jte_html_attribute_5 = form == null || "en".equals(form.getLocale());
		if (__jte_html_attribute_5) {
		jteOutput.writeContent(" selected");
		}
		jteOutput.writeContent(">");
		jteOutput.setContext("option", null);
		jteOutput.writeUserContent(i18n.t("lang.english"));
		jteOutput.writeContent("</option>\n                    <option value=\"km\"");
		var __jte_html_attribute_6 = form != null && "km".equals(form.getLocale());
		if (__jte_html_attribute_6) {
		jteOutput.writeContent(" selected");
		}
		jteOutput.writeContent(">");
		jteOutput.setContext("option", null);
		jteOutput.writeUserContent(i18n.t("lang.khmer"));
		jteOutput.writeContent("</option>\n                </select>\n            </div>\n\n            <button type=\"submit\"\n                    class=\"w-full mt-2 py-2.5 px-4 bg-indigo-600 hover:bg-indigo-700 text-white font-medium text-sm rounded-lg shadow-sm hover:shadow transition\">\n                ");
		jteOutput.setContext("button", null);
		jteOutput.writeUserContent(i18n.t("register.submit"));
		jteOutput.writeContent("\n            </button>\n        </form>\n\n        <div class=\"mt-6 text-center text-sm text-slate-600\">\n            ");
		jteOutput.setContext("div", null);
		jteOutput.writeUserContent(i18n.t("register.hasAccount"));
		jteOutput.writeContent("\n            <a href=\"/login\" class=\"text-indigo-600 font-semibold hover:underline\">");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(i18n.t("register.signInHere"));
		jteOutput.writeContent("</a>\n        </div>\n    </div>\n</body>\n</html>\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		kh.edu.istad.platform.iam.web.i18n.I18n i18n = (kh.edu.istad.platform.iam.web.i18n.I18n)params.get("i18n");
		kh.edu.istad.platform.iam.web.dto.RegisterForm form = (kh.edu.istad.platform.iam.web.dto.RegisterForm)params.get("form");
		String errorMessage = (String)params.getOrDefault("errorMessage",  null);
		render(jteOutput, jteHtmlInterceptor, i18n, form, errorMessage);
	}
}

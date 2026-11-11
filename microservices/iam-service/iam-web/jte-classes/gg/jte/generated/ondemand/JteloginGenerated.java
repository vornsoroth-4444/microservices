package gg.jte.generated.ondemand;
@SuppressWarnings("unchecked")
@javax.annotation.processing.Generated("gg.jte.TemplateEngine")
public final class JteloginGenerated {
	public static final String JTE_NAME = "login.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,0,0,6,6,6,6,6,6,6,6,6,6,10,10,10,10,10,10,19,23,23,23,27,27,27,27,28,28,28,30,30,30,30,31,31,31,37,37,37,38,38,38,41,42,42,45,45,45,47,47,49,49,52,52,52,54,54,56,56,59,59,59,61,61,63,66,66,66,69,69,69,69,69,69,69,69,69,74,74,74,84,84,84,90,90,90,94,97,97,97,100,105,105,105,110,110,110,115,115,115,120,120,120,125,125,125,126,126,126,131,131,131,0,1,2,3,3,3,3};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, kh.edu.istad.platform.iam.web.i18n.I18n i18n, Boolean hasError, Boolean hasLogout, Boolean hasRegistered) {
		jteOutput.writeContent("\r\n<!DOCTYPE html>\r\n<html");
		var __jte_html_attribute_0 = i18n.getLang();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_0)) {
			jteOutput.writeContent(" lang=\"");
			jteOutput.setContext("html", "lang");
			jteOutput.writeUserContent(__jte_html_attribute_0);
			jteOutput.setContext("html", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(">\r\n<head>\r\n    <meta charset=\"UTF-8\">\r\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n    <title>");
		jteOutput.setContext("title", null);
		jteOutput.writeUserContent(i18n.t("login.title"));
		jteOutput.writeContent(" - ");
		jteOutput.setContext("title", null);
		jteOutput.writeUserContent(i18n.t("brand"));
		jteOutput.writeContent("</title>\r\n    <link href=\"https://fonts.googleapis.com/css2?family=Battambang:wght@400;700&family=Inter:wght@400;500;600;700&display=swap\" rel=\"stylesheet\">\r\n    <script src=\"https://cdn.tailwindcss.com\"></script>\r\n    <style>\r\n        body { font-family: 'Inter', 'Battambang', sans-serif; }\r\n    </style>\r\n</head>\r\n<body class=\"bg-slate-50 text-slate-800 min-h-screen flex items-center justify-center p-4\">\r\n    <div class=\"max-w-md w-full bg-white rounded-2xl shadow-xl border border-slate-100 p-8\">\r\n        ");
		jteOutput.writeContent("\r\n        <div class=\"flex items-center justify-between mb-8\">\r\n            <div class=\"flex items-center space-x-2 text-indigo-600 font-bold text-lg tracking-tight\">\r\n                <svg class=\"w-6 h-6\" fill=\"none\" stroke=\"currentColor\" viewBox=\"0 0 24 24\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"2\" d=\"M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z\"></path></svg>\r\n                <span>");
		jteOutput.setContext("span", null);
		jteOutput.writeUserContent(i18n.t("brand"));
		jteOutput.writeContent("</span>\r\n            </div>\r\n            \r\n            <div class=\"inline-flex rounded-lg border border-slate-200 bg-slate-50 p-1 text-xs font-medium\">\r\n                <a href=\"?lang=en\" class=\"px-2 py-0.5 rounded transition ");
		jteOutput.setContext("a", "class");
		jteOutput.writeUserContent("en".equals(i18n.getLang()) ? "bg-white text-indigo-600 shadow-xs font-semibold" : "text-slate-600");
		jteOutput.setContext("a", null);
		jteOutput.writeContent("\">\r\n                    ");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(i18n.t("lang.english"));
		jteOutput.writeContent("\r\n                </a>\r\n                <a href=\"?lang=km\" class=\"px-2 py-0.5 rounded transition ");
		jteOutput.setContext("a", "class");
		jteOutput.writeUserContent("km".equals(i18n.getLang()) ? "bg-white text-indigo-600 shadow-xs font-semibold" : "text-slate-600");
		jteOutput.setContext("a", null);
		jteOutput.writeContent("\">\r\n                    ");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(i18n.t("lang.khmer"));
		jteOutput.writeContent("\r\n                </a>\r\n            </div>\r\n        </div>\r\n\r\n        <div class=\"mb-6\">\r\n            <h1 class=\"text-2xl font-bold tracking-tight text-slate-900\">");
		jteOutput.setContext("h1", null);
		jteOutput.writeUserContent(i18n.t("login.title"));
		jteOutput.writeContent("</h1>\r\n            <p class=\"text-sm text-slate-500 mt-1\">");
		jteOutput.setContext("p", null);
		jteOutput.writeUserContent(i18n.t("login.subtitle"));
		jteOutput.writeContent("</p>\r\n        </div>\r\n\r\n        ");
		jteOutput.writeContent("\r\n        ");
		if (hasError != null && hasError) {
			jteOutput.writeContent("\r\n            <div class=\"mb-4 p-3 bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg flex items-center space-x-2\">\r\n                <svg class=\"w-5 h-5 flex-shrink-0\" fill=\"currentColor\" viewBox=\"0 0 20 20\"><path fill-rule=\"evenodd\" d=\"M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z\" clip-rule=\"evenodd\"/></svg>\r\n                <span>");
			jteOutput.setContext("span", null);
			jteOutput.writeUserContent(i18n.t("login.badCredentials"));
			jteOutput.writeContent("</span>\r\n            </div>\r\n        ");
		}
		jteOutput.writeContent("\r\n\r\n        ");
		if (hasLogout != null && hasLogout) {
			jteOutput.writeContent("\r\n            <div class=\"mb-4 p-3 bg-blue-50 border border-blue-200 text-blue-700 text-sm rounded-lg flex items-center space-x-2\">\r\n                <svg class=\"w-5 h-5 flex-shrink-0\" fill=\"currentColor\" viewBox=\"0 0 20 20\"><path fill-rule=\"evenodd\" d=\"M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z\" clip-rule=\"evenodd\"/></svg>\r\n                <span>");
			jteOutput.setContext("span", null);
			jteOutput.writeUserContent(i18n.t("login.loggedOut"));
			jteOutput.writeContent("</span>\r\n            </div>\r\n        ");
		}
		jteOutput.writeContent("\r\n\r\n        ");
		if (hasRegistered != null && hasRegistered) {
			jteOutput.writeContent("\r\n            <div class=\"mb-4 p-3 bg-emerald-50 border border-emerald-200 text-emerald-700 text-sm rounded-lg flex items-center space-x-2\">\r\n                <svg class=\"w-5 h-5 flex-shrink-0\" fill=\"currentColor\" viewBox=\"0 0 20 20\"><path fill-rule=\"evenodd\" d=\"M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z\" clip-rule=\"evenodd\"/></svg>\r\n                <span>");
			jteOutput.setContext("span", null);
			jteOutput.writeUserContent(i18n.t("register.success"));
			jteOutput.writeContent("</span>\r\n            </div>\r\n        ");
		}
		jteOutput.writeContent("\r\n\r\n        ");
		jteOutput.writeContent("\r\n        <form action=\"/login\" method=\"post\" class=\"space-y-4\">\r\n            <div>\r\n                <label for=\"username\" class=\"block text-xs font-semibold uppercase text-slate-600 mb-1\">");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(i18n.t("login.username"));
		jteOutput.writeContent("</label>\r\n                <input type=\"text\" id=\"username\" name=\"username\" required\r\n                       class=\"w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\"\r\n                      ");
		var __jte_html_attribute_1 = i18n.t("login.username");
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_1)) {
			jteOutput.writeContent(" placeholder=\"");
			jteOutput.setContext("input", "placeholder");
			jteOutput.writeUserContent(__jte_html_attribute_1);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(">\r\n            </div>\r\n\r\n            <div>\r\n                <div class=\"flex items-center justify-between mb-1\">\r\n                    <label for=\"password\" class=\"block text-xs font-semibold uppercase text-slate-600\">");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(i18n.t("login.password"));
		jteOutput.writeContent("</label>\r\n                </div>\r\n                <input type=\"password\" id=\"password\" name=\"password\" required\r\n                       class=\"w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\"\r\n                       placeholder=\"••••••••\">\r\n            </div>\r\n\r\n            <div class=\"flex items-center justify-between text-sm\">\r\n                <label class=\"flex items-center text-slate-600\">\r\n                    <input type=\"checkbox\" name=\"remember-me\" class=\"rounded border-slate-300 text-indigo-600 focus:ring-indigo-500 mr-2\">\r\n                    <span>");
		jteOutput.setContext("span", null);
		jteOutput.writeUserContent(i18n.t("login.rememberMe"));
		jteOutput.writeContent("</span>\r\n                </label>\r\n            </div>\r\n\r\n            <button type=\"submit\"\r\n                    class=\"w-full py-2.5 px-4 bg-indigo-600 hover:bg-indigo-700 text-white font-medium text-sm rounded-lg shadow-sm hover:shadow transition\">\r\n                ");
		jteOutput.setContext("button", null);
		jteOutput.writeUserContent(i18n.t("login.signIn"));
		jteOutput.writeContent("\r\n            </button>\r\n        </form>\r\n\r\n        ");
		jteOutput.writeContent("\r\n        <div class=\"relative my-6\">\r\n            <div class=\"absolute inset-0 flex items-center\"><div class=\"w-full border-t border-slate-200\"></div></div>\r\n            <div class=\"relative flex justify-center text-xs uppercase\"><span class=\"bg-white px-2 text-slate-400\">");
		jteOutput.setContext("span", null);
		jteOutput.writeUserContent(i18n.t("login.orContinueWith"));
		jteOutput.writeContent("</span></div>\r\n        </div>\r\n\r\n        ");
		jteOutput.writeContent("\r\n        <div class=\"grid grid-cols-1 sm:grid-cols-2 gap-3\">\r\n            <a href=\"/oauth2/authorization/google\"\r\n               class=\"flex items-center justify-center space-x-2 py-2 px-3 border border-slate-200 rounded-lg text-xs font-medium text-slate-700 hover:bg-slate-50 transition\">\r\n                <svg class=\"w-4 h-4\" viewBox=\"0 0 24 24\"><path fill=\"#4285F4\" d=\"M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z\"/><path fill=\"#34A853\" d=\"M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z\"/><path fill=\"#FBBC05\" d=\"M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z\"/><path fill=\"#EA4335\" d=\"M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z\"/></svg>\r\n                <span>");
		jteOutput.setContext("span", null);
		jteOutput.writeUserContent(i18n.t("login.continueWith", i18n.t("login.google")));
		jteOutput.writeContent("</span>\r\n            </a>\r\n            <a href=\"/oauth2/authorization/github\"\r\n               class=\"flex items-center justify-center space-x-2 py-2 px-3 border border-slate-200 rounded-lg text-xs font-medium text-slate-700 hover:bg-slate-50 transition\">\r\n                <svg class=\"w-4 h-4 fill-current\" viewBox=\"0 0 24 24\"><path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.53 1.032 1.53 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z\"/></svg>\r\n                <span>");
		jteOutput.setContext("span", null);
		jteOutput.writeUserContent(i18n.t("login.continueWith", i18n.t("login.github")));
		jteOutput.writeContent("</span>\r\n            </a>\r\n            <a href=\"/oauth2/authorization/facebook\"\r\n               class=\"flex items-center justify-center space-x-2 py-2 px-3 border border-slate-200 rounded-lg text-xs font-medium text-slate-700 hover:bg-slate-50 transition\">\r\n                <svg class=\"w-4 h-4\" fill=\"#1877F2\" viewBox=\"0 0 24 24\"><path d=\"M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z\"/></svg>\r\n                <span>");
		jteOutput.setContext("span", null);
		jteOutput.writeUserContent(i18n.t("login.continueWith", i18n.t("login.facebook")));
		jteOutput.writeContent("</span>\r\n            </a>\r\n            <a href=\"/login/telegram\"\r\n               class=\"flex items-center justify-center space-x-2 py-2 px-3 border border-slate-200 rounded-lg text-xs font-medium text-slate-700 hover:bg-slate-50 transition\">\r\n                <svg class=\"w-4 h-4\" fill=\"#229ED9\" viewBox=\"0 0 24 24\"><path d=\"M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm4.64 6.8c-.15 1.58-.8 5.42-1.13 7.19-.14.75-.42 1-.68 1.03-.58.05-1.02-.38-1.58-.75-.88-.58-1.38-.94-2.23-1.5-.99-.65-.35-1.01.22-1.59.15-.15 2.71-2.48 2.76-2.69.01-.03.01-.14-.07-.19-.08-.05-.19-.02-.27 0-.12.03-1.99 1.27-5.61 3.72-.53.36-1.01.54-1.44.53-.47-.01-1.38-.27-2.06-.49-.83-.27-1.49-.42-1.43-.88.03-.24.37-.49 1.02-.75 4-1.74 6.67-2.88 8.01-3.44 3.81-1.59 4.6-1.87 5.12-1.88.11 0 .37.03.54.17.14.12.18.28.2.45-.02.07-.02.13-.03.22z\"/></svg>\r\n                <span>");
		jteOutput.setContext("span", null);
		jteOutput.writeUserContent(i18n.t("login.continueWith", i18n.t("login.telegram")));
		jteOutput.writeContent("</span>\r\n            </a>\r\n        </div>\r\n\r\n        <div class=\"mt-8 text-center text-sm text-slate-600\">\r\n            ");
		jteOutput.setContext("div", null);
		jteOutput.writeUserContent(i18n.t("login.noAccount"));
		jteOutput.writeContent("\r\n            <a href=\"/register\" class=\"text-indigo-600 font-semibold hover:underline\">");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(i18n.t("login.registerHere"));
		jteOutput.writeContent("</a>\r\n        </div>\r\n    </div>\r\n</body>\r\n</html>\r\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		kh.edu.istad.platform.iam.web.i18n.I18n i18n = (kh.edu.istad.platform.iam.web.i18n.I18n)params.get("i18n");
		Boolean hasError = (Boolean)params.getOrDefault("hasError",  false);
		Boolean hasLogout = (Boolean)params.getOrDefault("hasLogout",  false);
		Boolean hasRegistered = (Boolean)params.getOrDefault("hasRegistered",  false);
		render(jteOutput, jteHtmlInterceptor, i18n, hasError, hasLogout, hasRegistered);
	}
}

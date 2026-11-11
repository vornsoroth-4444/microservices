package gg.jte.generated.ondemand;
@SuppressWarnings("unchecked")
@javax.annotation.processing.Generated("gg.jte.TemplateEngine")
public final class JtechangepasswordGenerated {
	public static final String JTE_NAME = "change-password.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,0,0,4,4,4,4,8,8,8,9,9,9,12,12,12,16,16,19,19,19,21,21,25,25,25,31,31,31,37,37,37,44,44,44,48,48,48,53,53,53,54,54,54,0,1,2,2,2,2};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, kh.edu.istad.platform.iam.web.i18n.I18n i18n, kh.edu.istad.platform.iam.web.dto.ChangePasswordForm form, String errorMessage) {
		jteOutput.writeContent("\r\n");
		gg.jte.generated.ondemand.JtelayoutGenerated.render(jteOutput, jteHtmlInterceptor, i18n, i18n.t("changePassword.title"), new gg.jte.html.HtmlContent() {
			public void writeTo(gg.jte.html.HtmlTemplateOutput jteOutput) {
				jteOutput.writeContent("\r\n    <div class=\"max-w-md mx-auto bg-white rounded-2xl shadow-sm border border-slate-200 p-8\">\r\n        <div class=\"mb-6 pb-4 border-b border-slate-100 flex items-center justify-between\">\r\n            <div>\r\n                <h1 class=\"text-xl font-bold text-slate-900\">");
				jteOutput.setContext("h1", null);
				jteOutput.writeUserContent(i18n.t("changePassword.title"));
				jteOutput.writeContent("</h1>\r\n                <p class=\"text-xs text-slate-500 mt-0.5\">");
				jteOutput.setContext("p", null);
				jteOutput.writeUserContent(i18n.t("changePassword.subtitle"));
				jteOutput.writeContent("</p>\r\n            </div>\r\n            <a href=\"/\" class=\"text-xs font-semibold text-slate-600 hover:text-indigo-600 transition\">\r\n                &larr; ");
				jteOutput.setContext("a", null);
				jteOutput.writeUserContent(i18n.t("common.back"));
				jteOutput.writeContent("\r\n            </a>\r\n        </div>\r\n\r\n        ");
				if (errorMessage != null) {
					jteOutput.writeContent("\r\n            <div class=\"mb-4 p-3 bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg flex items-center space-x-2\">\r\n                <svg class=\"w-5 h-5 flex-shrink-0\" fill=\"currentColor\" viewBox=\"0 0 20 20\"><path fill-rule=\"evenodd\" d=\"M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z\" clip-rule=\"evenodd\"/></svg>\r\n                <span>");
					jteOutput.setContext("span", null);
					jteOutput.writeUserContent(errorMessage);
					jteOutput.writeContent("</span>\r\n            </div>\r\n        ");
				}
				jteOutput.writeContent("\r\n\r\n        <form action=\"/profile/password\" method=\"post\" class=\"space-y-4\">\r\n            <div>\r\n                <label for=\"currentPassword\" class=\"block text-xs font-semibold text-slate-700 mb-1\">");
				jteOutput.setContext("label", null);
				jteOutput.writeUserContent(i18n.t("changePassword.currentPassword"));
				jteOutput.writeContent("</label>\r\n                <input type=\"password\" id=\"currentPassword\" name=\"currentPassword\" required\r\n                       class=\"w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\r\n            </div>\r\n\r\n            <div>\r\n                <label for=\"newPassword\" class=\"block text-xs font-semibold text-slate-700 mb-1\">");
				jteOutput.setContext("label", null);
				jteOutput.writeUserContent(i18n.t("changePassword.newPassword"));
				jteOutput.writeContent("</label>\r\n                <input type=\"password\" id=\"newPassword\" name=\"newPassword\" required minlength=\"8\"\r\n                       class=\"w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\r\n            </div>\r\n\r\n            <div>\r\n                <label for=\"confirmPassword\" class=\"block text-xs font-semibold text-slate-700 mb-1\">");
				jteOutput.setContext("label", null);
				jteOutput.writeUserContent(i18n.t("changePassword.confirmPassword"));
				jteOutput.writeContent("</label>\r\n                <input type=\"password\" id=\"confirmPassword\" name=\"confirmPassword\" required minlength=\"8\"\r\n                       class=\"w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\r\n            </div>\r\n\r\n            <div class=\"flex items-center justify-end space-x-3 pt-4 border-t border-slate-100\">\r\n                <a href=\"/\" class=\"py-2.5 px-4 bg-slate-100 hover:bg-slate-200 text-slate-700 font-medium text-sm rounded-lg transition\">\r\n                    ");
				jteOutput.setContext("a", null);
				jteOutput.writeUserContent(i18n.t("common.cancel"));
				jteOutput.writeContent("\r\n                </a>\r\n                <button type=\"submit\"\r\n                        class=\"py-2.5 px-6 bg-indigo-600 hover:bg-indigo-700 text-white font-medium text-sm rounded-lg shadow-sm transition\">\r\n                    ");
				jteOutput.setContext("button", null);
				jteOutput.writeUserContent(i18n.t("changePassword.submit"));
				jteOutput.writeContent("\r\n                </button>\r\n            </div>\r\n        </form>\r\n    </div>\r\n");
			}
		});
		jteOutput.writeContent("\r\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		kh.edu.istad.platform.iam.web.i18n.I18n i18n = (kh.edu.istad.platform.iam.web.i18n.I18n)params.get("i18n");
		kh.edu.istad.platform.iam.web.dto.ChangePasswordForm form = (kh.edu.istad.platform.iam.web.dto.ChangePasswordForm)params.get("form");
		String errorMessage = (String)params.getOrDefault("errorMessage",  null);
		render(jteOutput, jteHtmlInterceptor, i18n, form, errorMessage);
	}
}

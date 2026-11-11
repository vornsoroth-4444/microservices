package gg.jte.generated.ondemand;
@SuppressWarnings("unchecked")
@javax.annotation.processing.Generated("gg.jte.TemplateEngine")
public final class JtelinkedaccountsGenerated {
	public static final String JTE_NAME = "linked-accounts.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,0,0,6,6,6,6,10,10,10,11,11,11,14,14,14,18,18,21,21,21,23,23,25,25,28,28,28,30,30,33,33,37,37,37,40,40,40,42,42,42,43,43,43,48,48,48,48,51,51,51,55,55,58,58,58,59,59,59,0,1,2,3,4,4,4,4};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, kh.edu.istad.platform.iam.web.i18n.I18n i18n, java.util.List<kh.edu.istad.platform.iam.application.dto.IdentityResult> identities, Boolean hasPassword, String successMessage, String errorMessage) {
		jteOutput.writeContent("\r\n");
		gg.jte.generated.ondemand.JtelayoutGenerated.render(jteOutput, jteHtmlInterceptor, i18n, i18n.t("linkedAccounts.title"), new gg.jte.html.HtmlContent() {
			public void writeTo(gg.jte.html.HtmlTemplateOutput jteOutput) {
				jteOutput.writeContent("\r\n    <div class=\"max-w-3xl mx-auto bg-white rounded-2xl shadow-sm border border-slate-200 p-8\">\r\n        <div class=\"mb-6 pb-4 border-b border-slate-100 flex items-center justify-between\">\r\n            <div>\r\n                <h1 class=\"text-xl font-bold text-slate-900\">");
				jteOutput.setContext("h1", null);
				jteOutput.writeUserContent(i18n.t("linkedAccounts.title"));
				jteOutput.writeContent("</h1>\r\n                <p class=\"text-xs text-slate-500 mt-0.5\">");
				jteOutput.setContext("p", null);
				jteOutput.writeUserContent(i18n.t("linkedAccounts.subtitle"));
				jteOutput.writeContent("</p>\r\n            </div>\r\n            <a href=\"/\" class=\"text-xs font-semibold text-slate-600 hover:text-indigo-600 transition\">\r\n                &larr; ");
				jteOutput.setContext("a", null);
				jteOutput.writeUserContent(i18n.t("common.back"));
				jteOutput.writeContent("\r\n            </a>\r\n        </div>\r\n\r\n        ");
				if (successMessage != null) {
					jteOutput.writeContent("\r\n            <div class=\"mb-4 p-3 bg-emerald-50 border border-emerald-200 text-emerald-700 text-sm rounded-lg flex items-center space-x-2\">\r\n                <svg class=\"w-5 h-5 flex-shrink-0\" fill=\"currentColor\" viewBox=\"0 0 20 20\"><path fill-rule=\"evenodd\" d=\"M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z\" clip-rule=\"evenodd\"/></svg>\r\n                <span>");
					jteOutput.setContext("span", null);
					jteOutput.writeUserContent(i18n.t("linkedAccounts.unlinkedSuccess"));
					jteOutput.writeContent("</span>\r\n            </div>\r\n        ");
				}
				jteOutput.writeContent("\r\n\r\n        ");
				if (errorMessage != null) {
					jteOutput.writeContent("\r\n            <div class=\"mb-4 p-3 bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg flex items-center space-x-2\">\r\n                <svg class=\"w-5 h-5 flex-shrink-0\" fill=\"currentColor\" viewBox=\"0 0 20 20\"><path fill-rule=\"evenodd\" d=\"M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z\" clip-rule=\"evenodd\"/></svg>\r\n                <span>");
					jteOutput.setContext("span", null);
					jteOutput.writeUserContent(i18n.t("linkedAccounts.cannotUnlinkLast"));
					jteOutput.writeContent("</span>\r\n            </div>\r\n        ");
				}
				jteOutput.writeContent("\r\n\r\n        <div class=\"divide-y divide-slate-100\">\r\n            ");
				for (var item : identities) {
					jteOutput.writeContent("\r\n                <div class=\"py-4 flex items-center justify-between\">\r\n                    <div class=\"flex items-center space-x-3\">\r\n                        <div class=\"w-10 h-10 rounded-full bg-slate-100 flex items-center justify-center font-bold text-slate-700 text-sm\">\r\n                            ");
					jteOutput.setContext("div", null);
					jteOutput.writeUserContent(item.getProvider().name().substring(0, 1));
					jteOutput.writeContent("\r\n                        </div>\r\n                        <div>\r\n                            <div class=\"font-medium text-slate-900\">");
					jteOutput.setContext("div", null);
					jteOutput.writeUserContent(item.getProvider().name());
					jteOutput.writeContent("</div>\r\n                            <div class=\"text-xs text-slate-500\">\r\n                                ");
					jteOutput.setContext("div", null);
					jteOutput.writeUserContent(item.getEmail() != null ? item.getEmail() : item.getDisplayName());
					jteOutput.writeContent("\r\n                                &bull; ");
					jteOutput.setContext("div", null);
					jteOutput.writeUserContent(item.getLinkedAt() != null ? item.getLinkedAt().toString() : "");
					jteOutput.writeContent("\r\n                            </div>\r\n                        </div>\r\n                    </div>\r\n\r\n                    <form action=\"/profile/accounts/");
					jteOutput.setContext("form", "action");
					jteOutput.writeUserContent(item.getUuid().toString());
					jteOutput.setContext("form", null);
					jteOutput.writeContent("/unlink\" method=\"post\">\r\n                        <button type=\"submit\"\r\n                                class=\"py-1.5 px-3 bg-red-50 hover:bg-red-100 text-red-600 text-xs font-semibold rounded-lg transition\">\r\n                            ");
					jteOutput.setContext("button", null);
					jteOutput.writeUserContent(i18n.t("linkedAccounts.unlink"));
					jteOutput.writeContent("\r\n                        </button>\r\n                    </form>\r\n                </div>\r\n            ");
				}
				jteOutput.writeContent("\r\n        </div>\r\n    </div>\r\n");
			}
		});
		jteOutput.writeContent("\r\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		kh.edu.istad.platform.iam.web.i18n.I18n i18n = (kh.edu.istad.platform.iam.web.i18n.I18n)params.get("i18n");
		java.util.List<kh.edu.istad.platform.iam.application.dto.IdentityResult> identities = (java.util.List<kh.edu.istad.platform.iam.application.dto.IdentityResult>)params.get("identities");
		Boolean hasPassword = (Boolean)params.getOrDefault("hasPassword",  true);
		String successMessage = (String)params.getOrDefault("successMessage",  null);
		String errorMessage = (String)params.getOrDefault("errorMessage",  null);
		render(jteOutput, jteHtmlInterceptor, i18n, identities, hasPassword, successMessage, errorMessage);
	}
}

package gg.jte.generated.ondemand;
@SuppressWarnings("unchecked")
@javax.annotation.processing.Generated("gg.jte.TemplateEngine")
public final class JteconnectedappsGenerated {
	public static final String JTE_NAME = "connected-apps.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,0,0,4,4,4,4,8,8,8,9,9,9,12,12,12,16,16,19,19,19,21,21,23,23,25,25,28,28,28,29,29,29,31,31,33,33,33,35,35,39,39,39,39,42,42,42,46,46,48,48,50,50,50,52,52,54,54,54,55,55,55,0,1,2,2,2,2};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, kh.edu.istad.platform.iam.web.i18n.I18n i18n, java.util.List<kh.edu.istad.platform.iam.web.dto.ConnectedAppModel> apps, String successMessage) {
		jteOutput.writeContent("\r\n");
		gg.jte.generated.ondemand.JtelayoutGenerated.render(jteOutput, jteHtmlInterceptor, i18n, i18n.t("connectedApps.title"), new gg.jte.html.HtmlContent() {
			public void writeTo(gg.jte.html.HtmlTemplateOutput jteOutput) {
				jteOutput.writeContent("\r\n    <div class=\"max-w-3xl mx-auto bg-white rounded-2xl shadow-sm border border-slate-200 p-8\">\r\n        <div class=\"mb-6 pb-4 border-b border-slate-100 flex items-center justify-between\">\r\n            <div>\r\n                <h1 class=\"text-xl font-bold text-slate-900\">");
				jteOutput.setContext("h1", null);
				jteOutput.writeUserContent(i18n.t("connectedApps.title"));
				jteOutput.writeContent("</h1>\r\n                <p class=\"text-xs text-slate-500 mt-0.5\">");
				jteOutput.setContext("p", null);
				jteOutput.writeUserContent(i18n.t("connectedApps.subtitle"));
				jteOutput.writeContent("</p>\r\n            </div>\r\n            <a href=\"/\" class=\"text-xs font-semibold text-slate-600 hover:text-indigo-600 transition\">\r\n                &larr; ");
				jteOutput.setContext("a", null);
				jteOutput.writeUserContent(i18n.t("common.back"));
				jteOutput.writeContent("\r\n            </a>\r\n        </div>\r\n\r\n        ");
				if (successMessage != null) {
					jteOutput.writeContent("\r\n            <div class=\"mb-4 p-3 bg-emerald-50 border border-emerald-200 text-emerald-700 text-sm rounded-lg flex items-center space-x-2\">\r\n                <svg class=\"w-5 h-5 flex-shrink-0\" fill=\"currentColor\" viewBox=\"0 0 20 20\"><path fill-rule=\"evenodd\" d=\"M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z\" clip-rule=\"evenodd\"/></svg>\r\n                <span>");
					jteOutput.setContext("span", null);
					jteOutput.writeUserContent(i18n.t("connectedApps.revokedSuccess"));
					jteOutput.writeContent("</span>\r\n            </div>\r\n        ");
				}
				jteOutput.writeContent("\r\n\r\n        ");
				if (apps != null && !apps.isEmpty()) {
					jteOutput.writeContent("\r\n            <div class=\"divide-y divide-slate-100\">\r\n                ");
					for (var app : apps) {
						jteOutput.writeContent("\r\n                    <div class=\"py-4 flex items-center justify-between\">\r\n                        <div>\r\n                            <div class=\"font-medium text-slate-900\">");
						jteOutput.setContext("div", null);
						jteOutput.writeUserContent(app.getClientName());
						jteOutput.writeContent("</div>\r\n                            <div class=\"text-xs text-slate-400 font-mono\">");
						jteOutput.setContext("div", null);
						jteOutput.writeUserContent(app.getClientId());
						jteOutput.writeContent("</div>\r\n                            <div class=\"flex flex-wrap gap-1 mt-2\">\r\n                                ");
						for (String s : app.getScopes()) {
							jteOutput.writeContent("\r\n                                    <span class=\"px-2 py-0.5 bg-slate-100 text-slate-600 text-xs rounded border border-slate-200\">\r\n                                        ");
							jteOutput.setContext("span", null);
							jteOutput.writeUserContent(s);
							jteOutput.writeContent("\r\n                                    </span>\r\n                                ");
						}
						jteOutput.writeContent("\r\n                            </div>\r\n                        </div>\r\n\r\n                        <form action=\"/profile/apps/");
						jteOutput.setContext("form", "action");
						jteOutput.writeUserContent(app.getClientId());
						jteOutput.setContext("form", null);
						jteOutput.writeContent("/revoke\" method=\"post\">\r\n                            <button type=\"submit\"\r\n                                    class=\"py-1.5 px-3 bg-red-50 hover:bg-red-100 text-red-600 text-xs font-semibold rounded-lg transition\">\r\n                                ");
						jteOutput.setContext("button", null);
						jteOutput.writeUserContent(i18n.t("connectedApps.revoke"));
						jteOutput.writeContent("\r\n                            </button>\r\n                        </form>\r\n                    </div>\r\n                ");
					}
					jteOutput.writeContent("\r\n            </div>\r\n        ");
				} else {
					jteOutput.writeContent("\r\n            <div class=\"py-12 text-center text-sm text-slate-400 italic\">\r\n                ");
					jteOutput.setContext("div", null);
					jteOutput.writeUserContent(i18n.t("connectedApps.noApps"));
					jteOutput.writeContent("\r\n            </div>\r\n        ");
				}
				jteOutput.writeContent("\r\n    </div>\r\n");
			}
		});
		jteOutput.writeContent("\r\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		kh.edu.istad.platform.iam.web.i18n.I18n i18n = (kh.edu.istad.platform.iam.web.i18n.I18n)params.get("i18n");
		java.util.List<kh.edu.istad.platform.iam.web.dto.ConnectedAppModel> apps = (java.util.List<kh.edu.istad.platform.iam.web.dto.ConnectedAppModel>)params.get("apps");
		String successMessage = (String)params.getOrDefault("successMessage",  null);
		render(jteOutput, jteHtmlInterceptor, i18n, apps, successMessage);
	}
}

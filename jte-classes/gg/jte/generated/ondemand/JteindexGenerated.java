package gg.jte.generated.ondemand;
@SuppressWarnings("unchecked")
@javax.annotation.processing.Generated("gg.jte.TemplateEngine")
public final class JteindexGenerated {
	public static final String JTE_NAME = "index.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,0,0,6,6,6,6,8,8,11,11,11,13,13,15,15,18,18,18,20,20,22,26,26,27,27,27,27,27,27,27,27,27,27,27,27,27,27,27,27,27,28,28,29,29,29,30,30,33,33,33,34,34,34,34,34,34,40,40,40,43,43,43,46,46,46,49,49,49,55,57,57,57,60,60,60,61,61,61,64,64,64,66,66,66,67,67,68,68,68,69,69,73,73,73,74,74,74,77,77,77,78,78,78,81,81,81,82,82,82,85,85,85,86,86,86,89,89,89,90,90,90,95,97,99,99,99,100,100,102,102,104,104,104,106,106,108,108,109,109,109,110,110,113,115,115,115,116,116,118,118,120,120,120,122,122,124,124,125,125,125,126,126,131,131,131,132,132,132,0,1,2,3,4,4,4,4};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, kh.edu.istad.platform.iam.web.i18n.I18n i18n, kh.edu.istad.platform.iam.application.dto.UserResult user, kh.edu.istad.platform.iam.application.dto.UserAuthoritiesResult authorities, Boolean updated, Boolean passwordChanged) {
		jteOutput.writeContent("\r\n");
		gg.jte.generated.ondemand.JtelayoutGenerated.render(jteOutput, jteHtmlInterceptor, i18n, i18n.t("profile.title"), new gg.jte.html.HtmlContent() {
			public void writeTo(gg.jte.html.HtmlTemplateOutput jteOutput) {
				jteOutput.writeContent("\r\n    <div class=\"space-y-6\">\r\n        ");
				if (updated != null && updated) {
					jteOutput.writeContent("\r\n            <div class=\"p-4 bg-emerald-50 border border-emerald-200 text-emerald-700 text-sm rounded-xl flex items-center space-x-2\">\r\n                <svg class=\"w-5 h-5 flex-shrink-0\" fill=\"currentColor\" viewBox=\"0 0 20 20\"><path fill-rule=\"evenodd\" d=\"M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z\" clip-rule=\"evenodd\"/></svg>\r\n                <span>");
					jteOutput.setContext("span", null);
					jteOutput.writeUserContent(i18n.t("profileEdit.success"));
					jteOutput.writeContent("</span>\r\n            </div>\r\n        ");
				}
				jteOutput.writeContent("\r\n\r\n        ");
				if (passwordChanged != null && passwordChanged) {
					jteOutput.writeContent("\r\n            <div class=\"p-4 bg-emerald-50 border border-emerald-200 text-emerald-700 text-sm rounded-xl flex items-center space-x-2\">\r\n                <svg class=\"w-5 h-5 flex-shrink-0\" fill=\"currentColor\" viewBox=\"0 0 20 20\"><path fill-rule=\"evenodd\" d=\"M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z\" clip-rule=\"evenodd\"/></svg>\r\n                <span>");
					jteOutput.setContext("span", null);
					jteOutput.writeUserContent(i18n.t("changePassword.success"));
					jteOutput.writeContent("</span>\r\n            </div>\r\n        ");
				}
				jteOutput.writeContent("\r\n\r\n        ");
				jteOutput.writeContent("\r\n        <div class=\"bg-white rounded-2xl shadow-sm border border-slate-200 p-6 flex flex-col md:flex-row items-center justify-between gap-4\">\r\n            <div class=\"flex items-center space-x-4\">\r\n                <div class=\"w-16 h-16 rounded-full bg-indigo-100 text-indigo-700 flex items-center justify-center font-bold text-2xl border-2 border-indigo-200 overflow-hidden\">\r\n                    ");
				if (user.getPictureUrl() != null && !user.getPictureUrl().isBlank()) {
					jteOutput.writeContent("\r\n                        <img");
					var __jte_html_attribute_0 = user.getPictureUrl();
					if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_0)) {
						jteOutput.writeContent(" src=\"");
						jteOutput.setContext("img", "src");
						jteOutput.writeUserContent(__jte_html_attribute_0);
						jteOutput.setContext("img", null);
						jteOutput.writeContent("\"");
					}
					var __jte_html_attribute_1 = user.getName();
					if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_1)) {
						jteOutput.writeContent(" alt=\"");
						jteOutput.setContext("img", "alt");
						jteOutput.writeUserContent(__jte_html_attribute_1);
						jteOutput.setContext("img", null);
						jteOutput.writeContent("\"");
					}
					jteOutput.writeContent(" class=\"w-full h-full object-cover\">\r\n                    ");
				} else {
					jteOutput.writeContent("\r\n                        ");
					jteOutput.setContext("div", null);
					jteOutput.writeUserContent(user.getGivenName() != null && !user.getGivenName().isEmpty() ? user.getGivenName().substring(0, 1) : "U");
					jteOutput.writeContent("\r\n                    ");
				}
				jteOutput.writeContent("\r\n                </div>\r\n                <div>\r\n                    <h1 class=\"text-xl font-bold text-slate-900\">");
				jteOutput.setContext("h1", null);
				jteOutput.writeUserContent(i18n.t("profile.welcome", user.getName()));
				jteOutput.writeContent("</h1>\r\n                    <p class=\"text-sm text-slate-500\">@");
				jteOutput.setContext("p", null);
				jteOutput.writeUserContent(user.getUsername());
				jteOutput.writeContent(" &bull; ");
				jteOutput.setContext("p", null);
				jteOutput.writeUserContent(user.getEmail());
				jteOutput.writeContent("</p>\r\n                </div>\r\n            </div>\r\n\r\n            <div class=\"flex flex-wrap gap-2\">\r\n                <a href=\"/profile/edit\" class=\"py-2 px-3.5 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-semibold rounded-lg shadow-xs transition\">\r\n                    ");
				jteOutput.setContext("a", null);
				jteOutput.writeUserContent(i18n.t("profile.editProfile"));
				jteOutput.writeContent("\r\n                </a>\r\n                <a href=\"/profile/password\" class=\"py-2 px-3.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold rounded-lg transition\">\r\n                    ");
				jteOutput.setContext("a", null);
				jteOutput.writeUserContent(i18n.t("profile.changePassword"));
				jteOutput.writeContent("\r\n                </a>\r\n                <a href=\"/profile/accounts\" class=\"py-2 px-3.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold rounded-lg transition\">\r\n                    ");
				jteOutput.setContext("a", null);
				jteOutput.writeUserContent(i18n.t("profile.linkedAccounts"));
				jteOutput.writeContent("\r\n                </a>\r\n                <a href=\"/profile/apps\" class=\"py-2 px-3.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold rounded-lg transition\">\r\n                    ");
				jteOutput.setContext("a", null);
				jteOutput.writeUserContent(i18n.t("profile.connectedApps"));
				jteOutput.writeContent("\r\n                </a>\r\n            </div>\r\n        </div>\r\n\r\n        <div class=\"grid grid-cols-1 md:grid-cols-2 gap-6\">\r\n            ");
				jteOutput.writeContent("\r\n            <div class=\"bg-white rounded-2xl shadow-sm border border-slate-200 p-6\">\r\n                <h2 class=\"text-base font-bold text-slate-900 mb-4 pb-2 border-b border-slate-100\">");
				jteOutput.setContext("h2", null);
				jteOutput.writeUserContent(i18n.t("profile.personalInfo"));
				jteOutput.writeContent("</h2>\r\n                <dl class=\"space-y-3 text-sm\">\r\n                    <div class=\"flex justify-between py-1 border-b border-slate-50\">\r\n                        <dt class=\"text-slate-500\">");
				jteOutput.setContext("dt", null);
				jteOutput.writeUserContent(i18n.t("profile.username"));
				jteOutput.writeContent("</dt>\r\n                        <dd class=\"font-medium text-slate-900\">");
				jteOutput.setContext("dd", null);
				jteOutput.writeUserContent(user.getUsername());
				jteOutput.writeContent("</dd>\r\n                    </div>\r\n                    <div class=\"flex justify-between py-1 border-b border-slate-50\">\r\n                        <dt class=\"text-slate-500\">");
				jteOutput.setContext("dt", null);
				jteOutput.writeUserContent(i18n.t("profile.email"));
				jteOutput.writeContent("</dt>\r\n                        <dd class=\"font-medium text-slate-900 flex items-center space-x-1\">\r\n                            <span>");
				jteOutput.setContext("span", null);
				jteOutput.writeUserContent(user.getEmail());
				jteOutput.writeContent("</span>\r\n                            ");
				if (user.isEmailVerified()) {
					jteOutput.writeContent("\r\n                                <span class=\"px-1.5 py-0.5 bg-emerald-100 text-emerald-800 text-xs rounded font-medium\">");
					jteOutput.setContext("span", null);
					jteOutput.writeUserContent(i18n.t("profile.emailVerified"));
					jteOutput.writeContent("</span>\r\n                            ");
				}
				jteOutput.writeContent("\r\n                        </dd>\r\n                    </div>\r\n                    <div class=\"flex justify-between py-1 border-b border-slate-50\">\r\n                        <dt class=\"text-slate-500\">");
				jteOutput.setContext("dt", null);
				jteOutput.writeUserContent(i18n.t("profile.phone"));
				jteOutput.writeContent("</dt>\r\n                        <dd class=\"font-medium text-slate-900\">");
				jteOutput.setContext("dd", null);
				jteOutput.writeUserContent(user.getPhoneNumber() != null ? user.getPhoneNumber() : "-");
				jteOutput.writeContent("</dd>\r\n                    </div>\r\n                    <div class=\"flex justify-between py-1 border-b border-slate-50\">\r\n                        <dt class=\"text-slate-500\">");
				jteOutput.setContext("dt", null);
				jteOutput.writeUserContent(i18n.t("profile.locale"));
				jteOutput.writeContent("</dt>\r\n                        <dd class=\"font-medium text-slate-900 uppercase\">");
				jteOutput.setContext("dd", null);
				jteOutput.writeUserContent(user.getLocale());
				jteOutput.writeContent("</dd>\r\n                    </div>\r\n                    <div class=\"flex justify-between py-1 border-b border-slate-50\">\r\n                        <dt class=\"text-slate-500\">");
				jteOutput.setContext("dt", null);
				jteOutput.writeUserContent(i18n.t("profile.zoneinfo"));
				jteOutput.writeContent("</dt>\r\n                        <dd class=\"font-medium text-slate-900\">");
				jteOutput.setContext("dd", null);
				jteOutput.writeUserContent(user.getZoneinfo() != null ? user.getZoneinfo() : "-");
				jteOutput.writeContent("</dd>\r\n                    </div>\r\n                    <div class=\"flex justify-between py-1 border-b border-slate-50\">\r\n                        <dt class=\"text-slate-500\">");
				jteOutput.setContext("dt", null);
				jteOutput.writeUserContent(i18n.t("profile.gender"));
				jteOutput.writeContent("</dt>\r\n                        <dd class=\"font-medium text-slate-900\">");
				jteOutput.setContext("dd", null);
				jteOutput.writeUserContent(user.getGender() != null ? user.getGender() : "-");
				jteOutput.writeContent("</dd>\r\n                    </div>\r\n                    <div class=\"flex justify-between py-1 border-b border-slate-50\">\r\n                        <dt class=\"text-slate-500\">");
				jteOutput.setContext("dt", null);
				jteOutput.writeUserContent(i18n.t("profile.birthdate"));
				jteOutput.writeContent("</dt>\r\n                        <dd class=\"font-medium text-slate-900\">");
				jteOutput.setContext("dd", null);
				jteOutput.writeUserContent(user.getBirthdate() != null ? user.getBirthdate().toString() : "-");
				jteOutput.writeContent("</dd>\r\n                    </div>\r\n                </dl>\r\n            </div>\r\n\r\n            ");
				jteOutput.writeContent("\r\n            <div class=\"space-y-6\">\r\n                ");
				jteOutput.writeContent("\r\n                <div class=\"bg-white rounded-2xl shadow-sm border border-slate-200 p-6\">\r\n                    <h2 class=\"text-base font-bold text-slate-900 mb-3 pb-2 border-b border-slate-100\">");
				jteOutput.setContext("h2", null);
				jteOutput.writeUserContent(i18n.t("profile.roles"));
				jteOutput.writeContent("</h2>\r\n                    ");
				if (authorities != null && authorities.getRoles() != null && !authorities.getRoles().isEmpty()) {
					jteOutput.writeContent("\r\n                        <div class=\"flex flex-wrap gap-2\">\r\n                            ");
					for (var role : authorities.getRoles()) {
						jteOutput.writeContent("\r\n                                <span class=\"px-3 py-1 bg-indigo-50 border border-indigo-200 text-indigo-700 text-xs font-semibold rounded-full\">\r\n                                    ROLE_");
						jteOutput.setContext("span", null);
						jteOutput.writeUserContent(role.getName());
						jteOutput.writeContent("\r\n                                </span>\r\n                            ");
					}
					jteOutput.writeContent("\r\n                        </div>\r\n                    ");
				} else {
					jteOutput.writeContent("\r\n                        <p class=\"text-xs text-slate-400 italic\">");
					jteOutput.setContext("p", null);
					jteOutput.writeUserContent(i18n.t("profile.noRoles"));
					jteOutput.writeContent("</p>\r\n                    ");
				}
				jteOutput.writeContent("\r\n                </div>\r\n\r\n                ");
				jteOutput.writeContent("\r\n                <div class=\"bg-white rounded-2xl shadow-sm border border-slate-200 p-6\">\r\n                    <h2 class=\"text-base font-bold text-slate-900 mb-3 pb-2 border-b border-slate-100\">");
				jteOutput.setContext("h2", null);
				jteOutput.writeUserContent(i18n.t("profile.effectivePermissions"));
				jteOutput.writeContent("</h2>\r\n                    ");
				if (authorities != null && authorities.getEffectivePermissions() != null && !authorities.getEffectivePermissions().isEmpty()) {
					jteOutput.writeContent("\r\n                        <div class=\"flex flex-wrap gap-1.5 max-h-48 overflow-y-auto\">\r\n                            ");
					for (String perm : authorities.getEffectivePermissions()) {
						jteOutput.writeContent("\r\n                                <span class=\"px-2.5 py-1 bg-slate-100 text-slate-700 text-xs font-mono rounded-md border border-slate-200\">\r\n                                    ");
						jteOutput.setContext("span", null);
						jteOutput.writeUserContent(perm);
						jteOutput.writeContent("\r\n                                </span>\r\n                            ");
					}
					jteOutput.writeContent("\r\n                        </div>\r\n                    ");
				} else {
					jteOutput.writeContent("\r\n                        <p class=\"text-xs text-slate-400 italic\">");
					jteOutput.setContext("p", null);
					jteOutput.writeUserContent(i18n.t("profile.noPermissions"));
					jteOutput.writeContent("</p>\r\n                    ");
				}
				jteOutput.writeContent("\r\n                </div>\r\n            </div>\r\n        </div>\r\n    </div>\r\n");
			}
		});
		jteOutput.writeContent("\r\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		kh.edu.istad.platform.iam.web.i18n.I18n i18n = (kh.edu.istad.platform.iam.web.i18n.I18n)params.get("i18n");
		kh.edu.istad.platform.iam.application.dto.UserResult user = (kh.edu.istad.platform.iam.application.dto.UserResult)params.get("user");
		kh.edu.istad.platform.iam.application.dto.UserAuthoritiesResult authorities = (kh.edu.istad.platform.iam.application.dto.UserAuthoritiesResult)params.get("authorities");
		Boolean updated = (Boolean)params.getOrDefault("updated",  false);
		Boolean passwordChanged = (Boolean)params.getOrDefault("passwordChanged",  false);
		render(jteOutput, jteHtmlInterceptor, i18n, user, authorities, updated, passwordChanged);
	}
}

package gg.jte.generated.ondemand;
@SuppressWarnings("unchecked")
@javax.annotation.processing.Generated("gg.jte.TemplateEngine")
public final class JteprofileeditGenerated {
	public static final String JTE_NAME = "profile-edit.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,0,0,3,3,3,3,7,7,7,8,8,8,11,11,11,16,18,18,18,21,21,21,22,22,22,22,22,22,22,22,22,26,26,26,27,27,27,27,27,27,27,27,27,31,31,31,32,32,32,32,32,32,32,32,32,36,36,36,37,37,37,37,37,37,37,37,37,43,46,46,46,49,49,49,49,49,49,49,49,50,50,50,50,50,50,50,50,51,51,51,51,51,51,51,51,55,55,55,56,56,56,56,56,56,56,56,56,60,60,60,61,61,61,61,61,61,61,61,61,66,69,69,69,70,70,70,70,70,70,70,70,70,74,74,74,75,75,75,75,75,75,75,75,75,79,79,79,80,80,80,80,80,80,80,80,80,84,84,84,87,87,87,87,87,87,87,87,88,88,88,88,88,88,88,88,93,95,95,95,98,98,98,99,99,99,99,99,99,99,99,99,104,104,104,105,105,105,105,105,105,105,105,105,109,109,109,110,110,110,110,110,110,110,110,110,114,114,114,115,115,115,115,115,115,115,115,115,119,119,119,120,120,120,120,120,120,120,120,120,129,129,129,133,133,133,138,138,138,139,139,139,0,1,1,1,1};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, kh.edu.istad.platform.iam.web.i18n.I18n i18n, kh.edu.istad.platform.iam.web.dto.ProfileEditForm form) {
		jteOutput.writeContent("\r\n");
		gg.jte.generated.ondemand.JtelayoutGenerated.render(jteOutput, jteHtmlInterceptor, i18n, i18n.t("profileEdit.title"), new gg.jte.html.HtmlContent() {
			public void writeTo(gg.jte.html.HtmlTemplateOutput jteOutput) {
				jteOutput.writeContent("\r\n    <div class=\"max-w-3xl mx-auto bg-white rounded-2xl shadow-sm border border-slate-200 p-8\">\r\n        <div class=\"mb-6 pb-4 border-b border-slate-100 flex items-center justify-between\">\r\n            <div>\r\n                <h1 class=\"text-xl font-bold text-slate-900\">");
				jteOutput.setContext("h1", null);
				jteOutput.writeUserContent(i18n.t("profileEdit.title"));
				jteOutput.writeContent("</h1>\r\n                <p class=\"text-xs text-slate-500 mt-0.5\">");
				jteOutput.setContext("p", null);
				jteOutput.writeUserContent(i18n.t("profileEdit.subtitle"));
				jteOutput.writeContent("</p>\r\n            </div>\r\n            <a href=\"/\" class=\"text-xs font-semibold text-slate-600 hover:text-indigo-600 transition\">\r\n                &larr; ");
				jteOutput.setContext("a", null);
				jteOutput.writeUserContent(i18n.t("common.back"));
				jteOutput.writeContent("\r\n            </a>\r\n        </div>\r\n\r\n        <form action=\"/profile/edit\" method=\"post\" class=\"space-y-6\">\r\n            ");
				jteOutput.writeContent("\r\n            <div>\r\n                <h3 class=\"text-xs font-bold uppercase tracking-wider text-slate-400 mb-3\">");
				jteOutput.setContext("h3", null);
				jteOutput.writeUserContent(i18n.t("profile.personalInfo"));
				jteOutput.writeContent("</h3>\r\n                <div class=\"grid grid-cols-1 md:grid-cols-2 gap-4\">\r\n                    <div>\r\n                        <label for=\"givenName\" class=\"block text-xs font-semibold text-slate-700 mb-1\">");
				jteOutput.setContext("label", null);
				jteOutput.writeUserContent(i18n.t("profileEdit.givenName"));
				jteOutput.writeContent("</label>\r\n                        <input type=\"text\" id=\"givenName\" name=\"givenName\"");
				var __jte_html_attribute_0 = form.getGivenName();
				if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_0)) {
					jteOutput.writeContent(" value=\"");
					jteOutput.setContext("input", "value");
					jteOutput.writeUserContent(__jte_html_attribute_0);
					jteOutput.setContext("input", null);
					jteOutput.writeContent("\"");
				}
				jteOutput.writeContent(" required\r\n                               class=\"w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\r\n                    </div>\r\n                    <div>\r\n                        <label for=\"familyName\" class=\"block text-xs font-semibold text-slate-700 mb-1\">");
				jteOutput.setContext("label", null);
				jteOutput.writeUserContent(i18n.t("profileEdit.familyName"));
				jteOutput.writeContent("</label>\r\n                        <input type=\"text\" id=\"familyName\" name=\"familyName\"");
				var __jte_html_attribute_1 = form.getFamilyName();
				if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_1)) {
					jteOutput.writeContent(" value=\"");
					jteOutput.setContext("input", "value");
					jteOutput.writeUserContent(__jte_html_attribute_1);
					jteOutput.setContext("input", null);
					jteOutput.writeContent("\"");
				}
				jteOutput.writeContent(" required\r\n                               class=\"w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\r\n                    </div>\r\n                    <div>\r\n                        <label for=\"middleName\" class=\"block text-xs font-semibold text-slate-700 mb-1\">");
				jteOutput.setContext("label", null);
				jteOutput.writeUserContent(i18n.t("profileEdit.middleName"));
				jteOutput.writeContent("</label>\r\n                        <input type=\"text\" id=\"middleName\" name=\"middleName\"");
				var __jte_html_attribute_2 = form.getMiddleName();
				if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_2)) {
					jteOutput.writeContent(" value=\"");
					jteOutput.setContext("input", "value");
					jteOutput.writeUserContent(__jte_html_attribute_2);
					jteOutput.setContext("input", null);
					jteOutput.writeContent("\"");
				}
				jteOutput.writeContent("\r\n                               class=\"w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\r\n                    </div>\r\n                    <div>\r\n                        <label for=\"nickname\" class=\"block text-xs font-semibold text-slate-700 mb-1\">");
				jteOutput.setContext("label", null);
				jteOutput.writeUserContent(i18n.t("profileEdit.nickname"));
				jteOutput.writeContent("</label>\r\n                        <input type=\"text\" id=\"nickname\" name=\"nickname\"");
				var __jte_html_attribute_3 = form.getNickname();
				if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_3)) {
					jteOutput.writeContent(" value=\"");
					jteOutput.setContext("input", "value");
					jteOutput.writeUserContent(__jte_html_attribute_3);
					jteOutput.setContext("input", null);
					jteOutput.writeContent("\"");
				}
				jteOutput.writeContent("\r\n                               class=\"w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\r\n                    </div>\r\n                </div>\r\n            </div>\r\n\r\n            ");
				jteOutput.writeContent("\r\n            <div class=\"grid grid-cols-1 md:grid-cols-3 gap-4\">\r\n                <div>\r\n                    <label for=\"gender\" class=\"block text-xs font-semibold text-slate-700 mb-1\">");
				jteOutput.setContext("label", null);
				jteOutput.writeUserContent(i18n.t("profileEdit.gender"));
				jteOutput.writeContent("</label>\r\n                    <select id=\"gender\" name=\"gender\"\r\n                            class=\"w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\r\n                        <option value=\"male\"");
				var __jte_html_attribute_4 = "male".equalsIgnoreCase(form.getGender());
				if (__jte_html_attribute_4) {
				jteOutput.writeContent(" selected");
				}
				jteOutput.writeContent(">");
				jteOutput.setContext("option", null);
				jteOutput.writeUserContent(i18n.t("profileEdit.gender.male"));
				jteOutput.writeContent("</option>\r\n                        <option value=\"female\"");
				var __jte_html_attribute_5 = "female".equalsIgnoreCase(form.getGender());
				if (__jte_html_attribute_5) {
				jteOutput.writeContent(" selected");
				}
				jteOutput.writeContent(">");
				jteOutput.setContext("option", null);
				jteOutput.writeUserContent(i18n.t("profileEdit.gender.female"));
				jteOutput.writeContent("</option>\r\n                        <option value=\"other\"");
				var __jte_html_attribute_6 = "other".equalsIgnoreCase(form.getGender());
				if (__jte_html_attribute_6) {
				jteOutput.writeContent(" selected");
				}
				jteOutput.writeContent(">");
				jteOutput.setContext("option", null);
				jteOutput.writeUserContent(i18n.t("profileEdit.gender.other"));
				jteOutput.writeContent("</option>\r\n                    </select>\r\n                </div>\r\n                <div>\r\n                    <label for=\"birthdate\" class=\"block text-xs font-semibold text-slate-700 mb-1\">");
				jteOutput.setContext("label", null);
				jteOutput.writeUserContent(i18n.t("profileEdit.birthdate"));
				jteOutput.writeContent("</label>\r\n                    <input type=\"date\" id=\"birthdate\" name=\"birthdate\"");
				var __jte_html_attribute_7 = form.getBirthdate() != null ? form.getBirthdate().toString() : "";
				if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_7)) {
					jteOutput.writeContent(" value=\"");
					jteOutput.setContext("input", "value");
					jteOutput.writeUserContent(__jte_html_attribute_7);
					jteOutput.setContext("input", null);
					jteOutput.writeContent("\"");
				}
				jteOutput.writeContent("\r\n                           class=\"w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\r\n                </div>\r\n                <div>\r\n                    <label for=\"phoneNumber\" class=\"block text-xs font-semibold text-slate-700 mb-1\">");
				jteOutput.setContext("label", null);
				jteOutput.writeUserContent(i18n.t("profileEdit.phoneNumber"));
				jteOutput.writeContent("</label>\r\n                    <input type=\"text\" id=\"phoneNumber\" name=\"phoneNumber\"");
				var __jte_html_attribute_8 = form.getPhoneNumber();
				if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_8)) {
					jteOutput.writeContent(" value=\"");
					jteOutput.setContext("input", "value");
					jteOutput.writeUserContent(__jte_html_attribute_8);
					jteOutput.setContext("input", null);
					jteOutput.writeContent("\"");
				}
				jteOutput.writeContent("\r\n                           class=\"w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\r\n                </div>\r\n            </div>\r\n\r\n            ");
				jteOutput.writeContent("\r\n            <div class=\"grid grid-cols-1 md:grid-cols-2 gap-4\">\r\n                <div>\r\n                    <label for=\"profileUrl\" class=\"block text-xs font-semibold text-slate-700 mb-1\">");
				jteOutput.setContext("label", null);
				jteOutput.writeUserContent(i18n.t("profileEdit.profileUrl"));
				jteOutput.writeContent("</label>\r\n                    <input type=\"url\" id=\"profileUrl\" name=\"profileUrl\"");
				var __jte_html_attribute_9 = form.getProfileUrl();
				if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_9)) {
					jteOutput.writeContent(" value=\"");
					jteOutput.setContext("input", "value");
					jteOutput.writeUserContent(__jte_html_attribute_9);
					jteOutput.setContext("input", null);
					jteOutput.writeContent("\"");
				}
				jteOutput.writeContent("\r\n                           class=\"w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\r\n                </div>\r\n                <div>\r\n                    <label for=\"pictureUrl\" class=\"block text-xs font-semibold text-slate-700 mb-1\">");
				jteOutput.setContext("label", null);
				jteOutput.writeUserContent(i18n.t("profileEdit.pictureUrl"));
				jteOutput.writeContent("</label>\r\n                    <input type=\"url\" id=\"pictureUrl\" name=\"pictureUrl\"");
				var __jte_html_attribute_10 = form.getPictureUrl();
				if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_10)) {
					jteOutput.writeContent(" value=\"");
					jteOutput.setContext("input", "value");
					jteOutput.writeUserContent(__jte_html_attribute_10);
					jteOutput.setContext("input", null);
					jteOutput.writeContent("\"");
				}
				jteOutput.writeContent("\r\n                           class=\"w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\r\n                </div>\r\n                <div>\r\n                    <label for=\"zoneinfo\" class=\"block text-xs font-semibold text-slate-700 mb-1\">");
				jteOutput.setContext("label", null);
				jteOutput.writeUserContent(i18n.t("profileEdit.zoneinfo"));
				jteOutput.writeContent("</label>\r\n                    <input type=\"text\" id=\"zoneinfo\" name=\"zoneinfo\"");
				var __jte_html_attribute_11 = form.getZoneinfo();
				if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_11)) {
					jteOutput.writeContent(" value=\"");
					jteOutput.setContext("input", "value");
					jteOutput.writeUserContent(__jte_html_attribute_11);
					jteOutput.setContext("input", null);
					jteOutput.writeContent("\"");
				}
				jteOutput.writeContent(" placeholder=\"Asia/Phnom_Penh\"\r\n                           class=\"w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\r\n                </div>\r\n                <div>\r\n                    <label for=\"locale\" class=\"block text-xs font-semibold text-slate-700 mb-1\">");
				jteOutput.setContext("label", null);
				jteOutput.writeUserContent(i18n.t("profileEdit.locale"));
				jteOutput.writeContent("</label>\r\n                    <select id=\"locale\" name=\"locale\"\r\n                            class=\"w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\r\n                        <option value=\"en\"");
				var __jte_html_attribute_12 = "en".equalsIgnoreCase(form.getLocale());
				if (__jte_html_attribute_12) {
				jteOutput.writeContent(" selected");
				}
				jteOutput.writeContent(">");
				jteOutput.setContext("option", null);
				jteOutput.writeUserContent(i18n.t("lang.english"));
				jteOutput.writeContent("</option>\r\n                        <option value=\"km\"");
				var __jte_html_attribute_13 = "km".equalsIgnoreCase(form.getLocale());
				if (__jte_html_attribute_13) {
				jteOutput.writeContent(" selected");
				}
				jteOutput.writeContent(">");
				jteOutput.setContext("option", null);
				jteOutput.writeUserContent(i18n.t("lang.khmer"));
				jteOutput.writeContent("</option>\r\n                    </select>\r\n                </div>\r\n            </div>\r\n\r\n            ");
				jteOutput.writeContent("\r\n            <div>\r\n                <h3 class=\"text-xs font-bold uppercase tracking-wider text-slate-400 mb-3\">");
				jteOutput.setContext("h3", null);
				jteOutput.writeUserContent(i18n.t("profileEdit.address"));
				jteOutput.writeContent("</h3>\r\n                <div class=\"space-y-4\">\r\n                    <div>\r\n                        <label for=\"streetAddress\" class=\"block text-xs font-semibold text-slate-700 mb-1\">");
				jteOutput.setContext("label", null);
				jteOutput.writeUserContent(i18n.t("profileEdit.streetAddress"));
				jteOutput.writeContent("</label>\r\n                        <input type=\"text\" id=\"streetAddress\" name=\"streetAddress\"");
				var __jte_html_attribute_14 = form.getStreetAddress();
				if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_14)) {
					jteOutput.writeContent(" value=\"");
					jteOutput.setContext("input", "value");
					jteOutput.writeUserContent(__jte_html_attribute_14);
					jteOutput.setContext("input", null);
					jteOutput.writeContent("\"");
				}
				jteOutput.writeContent("\r\n                               class=\"w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\r\n                    </div>\r\n                    <div class=\"grid grid-cols-1 md:grid-cols-4 gap-4\">\r\n                        <div>\r\n                            <label for=\"locality\" class=\"block text-xs font-semibold text-slate-700 mb-1\">");
				jteOutput.setContext("label", null);
				jteOutput.writeUserContent(i18n.t("profileEdit.locality"));
				jteOutput.writeContent("</label>\r\n                            <input type=\"text\" id=\"locality\" name=\"locality\"");
				var __jte_html_attribute_15 = form.getLocality();
				if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_15)) {
					jteOutput.writeContent(" value=\"");
					jteOutput.setContext("input", "value");
					jteOutput.writeUserContent(__jte_html_attribute_15);
					jteOutput.setContext("input", null);
					jteOutput.writeContent("\"");
				}
				jteOutput.writeContent("\r\n                                   class=\"w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\r\n                        </div>\r\n                        <div>\r\n                            <label for=\"region\" class=\"block text-xs font-semibold text-slate-700 mb-1\">");
				jteOutput.setContext("label", null);
				jteOutput.writeUserContent(i18n.t("profileEdit.region"));
				jteOutput.writeContent("</label>\r\n                            <input type=\"text\" id=\"region\" name=\"region\"");
				var __jte_html_attribute_16 = form.getRegion();
				if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_16)) {
					jteOutput.writeContent(" value=\"");
					jteOutput.setContext("input", "value");
					jteOutput.writeUserContent(__jte_html_attribute_16);
					jteOutput.setContext("input", null);
					jteOutput.writeContent("\"");
				}
				jteOutput.writeContent("\r\n                                   class=\"w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\r\n                        </div>\r\n                        <div>\r\n                            <label for=\"postalCode\" class=\"block text-xs font-semibold text-slate-700 mb-1\">");
				jteOutput.setContext("label", null);
				jteOutput.writeUserContent(i18n.t("profileEdit.postalCode"));
				jteOutput.writeContent("</label>\r\n                            <input type=\"text\" id=\"postalCode\" name=\"postalCode\"");
				var __jte_html_attribute_17 = form.getPostalCode();
				if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_17)) {
					jteOutput.writeContent(" value=\"");
					jteOutput.setContext("input", "value");
					jteOutput.writeUserContent(__jte_html_attribute_17);
					jteOutput.setContext("input", null);
					jteOutput.writeContent("\"");
				}
				jteOutput.writeContent("\r\n                                   class=\"w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition\">\r\n                        </div>\r\n                        <div>\r\n                            <label for=\"country\" class=\"block text-xs font-semibold text-slate-700 mb-1\">");
				jteOutput.setContext("label", null);
				jteOutput.writeUserContent(i18n.t("profileEdit.country"));
				jteOutput.writeContent("</label>\r\n                            <input type=\"text\" id=\"country\" name=\"country\"");
				var __jte_html_attribute_18 = form.getCountry();
				if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_18)) {
					jteOutput.writeContent(" value=\"");
					jteOutput.setContext("input", "value");
					jteOutput.writeUserContent(__jte_html_attribute_18);
					jteOutput.setContext("input", null);
					jteOutput.writeContent("\"");
				}
				jteOutput.writeContent(" placeholder=\"KH\" maxlength=\"2\"\r\n                                   class=\"w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition uppercase\">\r\n                        </div>\r\n                    </div>\r\n                </div>\r\n            </div>\r\n\r\n            <div class=\"flex items-center justify-end space-x-3 pt-4 border-t border-slate-100\">\r\n                <a href=\"/\" class=\"py-2.5 px-4 bg-slate-100 hover:bg-slate-200 text-slate-700 font-medium text-sm rounded-lg transition\">\r\n                    ");
				jteOutput.setContext("a", null);
				jteOutput.writeUserContent(i18n.t("common.cancel"));
				jteOutput.writeContent("\r\n                </a>\r\n                <button type=\"submit\"\r\n                        class=\"py-2.5 px-6 bg-indigo-600 hover:bg-indigo-700 text-white font-medium text-sm rounded-lg shadow-sm transition\">\r\n                    ");
				jteOutput.setContext("button", null);
				jteOutput.writeUserContent(i18n.t("common.save"));
				jteOutput.writeContent("\r\n                </button>\r\n            </div>\r\n        </form>\r\n    </div>\r\n");
			}
		});
		jteOutput.writeContent("\r\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		kh.edu.istad.platform.iam.web.i18n.I18n i18n = (kh.edu.istad.platform.iam.web.i18n.I18n)params.get("i18n");
		kh.edu.istad.platform.iam.web.dto.ProfileEditForm form = (kh.edu.istad.platform.iam.web.dto.ProfileEditForm)params.get("form");
		render(jteOutput, jteHtmlInterceptor, i18n, form);
	}
}

package gg.jte.generated.ondemand;
import gg.jte.Content;
@SuppressWarnings("unchecked")
@javax.annotation.processing.Generated("gg.jte.TemplateEngine")
public final class JtelayoutGenerated {
	public static final String JTE_NAME = "layout.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,1,1,1,1,6,6,6,6,6,6,6,6,6,6,10,10,10,10,10,10,20,25,25,25,29,31,31,31,32,32,32,33,33,33,34,34,34,35,35,35,38,40,40,40,40,41,41,41,43,43,43,43,44,44,44,50,50,50,57,59,59,59,62,65,65,65,70,70,70,1,2,3,3,3,3};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, kh.edu.istad.platform.iam.web.i18n.I18n i18n, String title, Content content) {
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
		jteOutput.writeUserContent(title);
		jteOutput.writeContent(" - ");
		jteOutput.setContext("title", null);
		jteOutput.writeUserContent(i18n.t("brand"));
		jteOutput.writeContent("</title>\r\n    <link href=\"https://fonts.googleapis.com/css2?family=Battambang:wght@400;700&family=Inter:wght@400;500;600;700&display=swap\" rel=\"stylesheet\">\r\n    <script src=\"https://cdn.tailwindcss.com\"></script>\r\n    <style>\r\n        body {\r\n            font-family: 'Inter', 'Battambang', sans-serif;\r\n        }\r\n    </style>\r\n</head>\r\n<body class=\"bg-slate-50 text-slate-800 min-h-screen flex flex-col\">\r\n    ");
		jteOutput.writeContent("\r\n    <header class=\"bg-white border-b border-slate-200 sticky top-0 z-50 shadow-sm\">\r\n        <div class=\"max-w-6xl mx-auto px-4 h-16 flex items-center justify-between\">\r\n            <a href=\"/\" class=\"flex items-center space-x-2 text-indigo-600 font-bold text-xl tracking-tight\">\r\n                <svg class=\"w-7 h-7\" fill=\"none\" stroke=\"currentColor\" viewBox=\"0 0 24 24\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"2\" d=\"M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z\"></path></svg>\r\n                <span>");
		jteOutput.setContext("span", null);
		jteOutput.writeUserContent(i18n.t("brand"));
		jteOutput.writeContent("</span>\r\n            </a>\r\n            \r\n            <div class=\"flex items-center space-x-4\">\r\n                ");
		jteOutput.writeContent("\r\n                <nav class=\"hidden md:flex items-center space-x-4 text-sm font-medium\">\r\n                    <a href=\"/\" class=\"text-slate-600 hover:text-indigo-600 transition\">");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(i18n.t("profile.overview"));
		jteOutput.writeContent("</a>\r\n                    <a href=\"/profile/edit\" class=\"text-slate-600 hover:text-indigo-600 transition\">");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(i18n.t("profile.editProfile"));
		jteOutput.writeContent("</a>\r\n                    <a href=\"/profile/password\" class=\"text-slate-600 hover:text-indigo-600 transition\">");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(i18n.t("profile.changePassword"));
		jteOutput.writeContent("</a>\r\n                    <a href=\"/profile/accounts\" class=\"text-slate-600 hover:text-indigo-600 transition\">");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(i18n.t("profile.linkedAccounts"));
		jteOutput.writeContent("</a>\r\n                    <a href=\"/profile/apps\" class=\"text-slate-600 hover:text-indigo-600 transition\">");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(i18n.t("profile.connectedApps"));
		jteOutput.writeContent("</a>\r\n                </nav>\r\n\r\n                ");
		jteOutput.writeContent("\r\n                <div class=\"inline-flex rounded-lg border border-slate-200 bg-slate-100 p-1 text-xs font-medium\">\r\n                    <a href=\"?lang=en\" class=\"px-2.5 py-1 rounded-md transition ");
		jteOutput.setContext("a", "class");
		jteOutput.writeUserContent("en".equals(i18n.getLang()) ? "bg-white text-indigo-600 shadow-sm font-semibold" : "text-slate-600 hover:text-slate-900");
		jteOutput.setContext("a", null);
		jteOutput.writeContent("\">\r\n                        ");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(i18n.t("lang.english"));
		jteOutput.writeContent("\r\n                    </a>\r\n                    <a href=\"?lang=km\" class=\"px-2.5 py-1 rounded-md transition ");
		jteOutput.setContext("a", "class");
		jteOutput.writeUserContent("km".equals(i18n.getLang()) ? "bg-white text-indigo-600 shadow-sm font-semibold" : "text-slate-600 hover:text-slate-900");
		jteOutput.setContext("a", null);
		jteOutput.writeContent("\">\r\n                        ");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(i18n.t("lang.khmer"));
		jteOutput.writeContent("\r\n                    </a>\r\n                </div>\r\n\r\n                <form action=\"/logout\" method=\"post\" class=\"inline\">\r\n                    <button type=\"submit\" class=\"text-sm font-medium text-slate-600 hover:text-red-600 transition\">\r\n                        ");
		jteOutput.setContext("button", null);
		jteOutput.writeUserContent(i18n.t("common.logout"));
		jteOutput.writeContent("\r\n                    </button>\r\n                </form>\r\n            </div>\r\n        </div>\r\n    </header>\r\n\r\n    ");
		jteOutput.writeContent("\r\n    <main class=\"flex-grow max-w-6xl w-full mx-auto px-4 py-8\">\r\n        ");
		jteOutput.setContext("main", null);
		jteOutput.writeUserContent(content);
		jteOutput.writeContent("\r\n    </main>\r\n\r\n    ");
		jteOutput.writeContent("\r\n    <footer class=\"bg-white border-t border-slate-200 py-6 mt-auto\">\r\n        <div class=\"max-w-6xl mx-auto px-4 text-center text-xs text-slate-500\">\r\n            &copy; 2026 ");
		jteOutput.setContext("div", null);
		jteOutput.writeUserContent(i18n.t("brand"));
		jteOutput.writeContent(". All rights reserved.\r\n        </div>\r\n    </footer>\r\n</body>\r\n</html>\r\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		kh.edu.istad.platform.iam.web.i18n.I18n i18n = (kh.edu.istad.platform.iam.web.i18n.I18n)params.get("i18n");
		String title = (String)params.get("title");
		Content content = (Content)params.get("content");
		render(jteOutput, jteHtmlInterceptor, i18n, title, content);
	}
}

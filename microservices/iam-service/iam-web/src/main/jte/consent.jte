@param kh.edu.istad.platform.iam.web.i18n.I18n i18n
@param kh.edu.istad.platform.iam.web.dto.ConsentModel consent
@param String principalName

<!DOCTYPE html>
<html lang="${i18n.getLang()}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${i18n.t("consent.title")} - ${i18n.t("brand")}</title>
    <link href="https://fonts.googleapis.com/css2?family=Battambang:wght@400;700&family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        body { font-family: 'Inter', 'Battambang', sans-serif; }
    </style>
</head>
<body class="bg-slate-50 text-slate-800 min-h-screen flex items-center justify-center p-4">
    <div class="max-w-md w-full bg-white rounded-2xl shadow-xl border border-slate-100 p-8">
        <!-- Header with Language Switcher -->
        <div class="flex items-center justify-between mb-6">
            <div class="flex items-center space-x-2 text-indigo-600 font-bold text-lg tracking-tight">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path></svg>
                <span>${i18n.t("brand")}</span>
            </div>
            
            <div class="inline-flex rounded-lg border border-slate-200 bg-slate-50 p-1 text-xs font-medium">
                <a href="?lang=en" class="px-2 py-0.5 rounded transition ${"en".equals(i18n.getLang()) ? "bg-white text-indigo-600 shadow-xs font-semibold" : "text-slate-600"}">
                    ${i18n.t("lang.english")}
                </a>
                <a href="?lang=km" class="px-2 py-0.5 rounded transition ${"km".equals(i18n.getLang()) ? "bg-white text-indigo-600 shadow-xs font-semibold" : "text-slate-600"}">
                    ${i18n.t("lang.khmer")}
                </a>
            </div>
        </div>

        <div class="text-center mb-6">
            <div class="w-16 h-16 bg-indigo-50 text-indigo-600 rounded-2xl mx-auto flex items-center justify-center mb-3">
                <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"></path></svg>
            </div>
            <h1 class="text-xl font-bold text-slate-900">${i18n.t("consent.title")}</h1>
            <p class="text-sm text-slate-600 mt-2">
                ${i18n.t("consent.clientWantsAccess", consent.getClientName())}
            </p>
        </div>

        <form action="/oauth2/authorize" method="post" class="space-y-6">
            <input type="hidden" name="client_id" value="${consent.getClientId()}">
            <input type="hidden" name="state" value="${consent.getState()}">

            <div>
                <h3 class="text-xs font-bold uppercase tracking-wider text-slate-500 mb-3">${i18n.t("consent.requestedScopes")}</h3>
                <div class="space-y-2.5 bg-slate-50 border border-slate-200 rounded-xl p-4">
                    @for(String scope : consent.getScopes())
                        <label class="flex items-start space-x-3 cursor-pointer">
                            <input type="checkbox" name="scope" value="${scope}" checked
                                   class="mt-1 rounded border-slate-300 text-indigo-600 focus:ring-indigo-500">
                            <div class="text-sm">
                                <span class="font-medium text-slate-800">${scope}</span>
                                <p class="text-xs text-slate-500">${i18n.t("consent.scope." + scope)}</p>
                            </div>
                        </label>
                    @endfor
                </div>
            </div>

            <div class="flex space-x-3">
                <button type="submit" name="consent_action" value="approve"
                        class="flex-1 py-2.5 px-4 bg-indigo-600 hover:bg-indigo-700 text-white font-medium text-sm rounded-lg shadow-sm transition">
                    ${i18n.t("consent.allow")}
                </button>
                <button type="submit" name="consent_action" value="cancel"
                        class="flex-1 py-2.5 px-4 bg-slate-100 hover:bg-slate-200 text-slate-700 font-medium text-sm rounded-lg transition">
                    ${i18n.t("consent.cancel")}
                </button>
            </div>
        </form>
    </div>
</body>
</html>

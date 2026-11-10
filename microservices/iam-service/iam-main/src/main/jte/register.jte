@param kh.edu.istad.platform.iam.web.i18n.I18n i18n
@param kh.edu.istad.platform.iam.web.dto.RegisterForm form
@param String errorMessage = null

<!DOCTYPE html>
<html lang="${i18n.getLang()}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${i18n.t("register.title")} - ${i18n.t("brand")}</title>
    <link href="https://fonts.googleapis.com/css2?family=Battambang:wght@400;700&family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        body { font-family: 'Inter', 'Battambang', sans-serif; }
    </style>
</head>
<body class="bg-slate-50 text-slate-800 min-h-screen flex items-center justify-center p-4">
    <div class="max-w-lg w-full bg-white rounded-2xl shadow-xl border border-slate-100 p-8 my-8">
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

        <div class="mb-6">
            <h1 class="text-2xl font-bold tracking-tight text-slate-900">${i18n.t("register.title")}</h1>
            <p class="text-sm text-slate-500 mt-1">${i18n.t("register.subtitle")}</p>
        </div>

        @if(errorMessage != null)
            <div class="mb-4 p-3 bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg flex items-center space-x-2">
                <svg class="w-5 h-5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/></svg>
                <span>${errorMessage}</span>
            </div>
        @endif

        <form action="/register" method="post" class="space-y-4">
            <div class="grid grid-cols-2 gap-4">
                <div>
                    <label for="givenName" class="block text-xs font-semibold uppercase text-slate-600 mb-1">${i18n.t("register.givenName")}</label>
                    <input type="text" id="givenName" name="givenName" value="${form != null ? form.getGivenName() : ""}" required
                           class="w-full px-3.5 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
                </div>
                <div>
                    <label for="familyName" class="block text-xs font-semibold uppercase text-slate-600 mb-1">${i18n.t("register.familyName")}</label>
                    <input type="text" id="familyName" name="familyName" value="${form != null ? form.getFamilyName() : ""}" required
                           class="w-full px-3.5 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
                </div>
            </div>

            <div>
                <label for="username" class="block text-xs font-semibold uppercase text-slate-600 mb-1">${i18n.t("register.username")}</label>
                <input type="text" id="username" name="username" value="${form != null ? form.getUsername() : ""}" required
                       class="w-full px-3.5 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
            </div>

            <div>
                <label for="email" class="block text-xs font-semibold uppercase text-slate-600 mb-1">${i18n.t("register.email")}</label>
                <input type="email" id="email" name="email" value="${form != null ? form.getEmail() : ""}" required
                       class="w-full px-3.5 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
            </div>

            <div class="grid grid-cols-2 gap-4">
                <div>
                    <label for="password" class="block text-xs font-semibold uppercase text-slate-600 mb-1">${i18n.t("register.password")}</label>
                    <input type="password" id="password" name="password" required
                           class="w-full px-3.5 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
                </div>
                <div>
                    <label for="confirmPassword" class="block text-xs font-semibold uppercase text-slate-600 mb-1">${i18n.t("register.confirmPassword")}</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required
                           class="w-full px-3.5 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
                </div>
            </div>

            <div>
                <label for="locale" class="block text-xs font-semibold uppercase text-slate-600 mb-1">${i18n.t("register.locale")}</label>
                <select id="locale" name="locale"
                        class="w-full px-3.5 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
                    <option value="en" selected="${form == null || "en".equals(form.getLocale())}">${i18n.t("lang.english")}</option>
                    <option value="km" selected="${form != null && "km".equals(form.getLocale())}">${i18n.t("lang.khmer")}</option>
                </select>
            </div>

            <button type="submit"
                    class="w-full mt-2 py-2.5 px-4 bg-indigo-600 hover:bg-indigo-700 text-white font-medium text-sm rounded-lg shadow-sm hover:shadow transition">
                ${i18n.t("register.submit")}
            </button>
        </form>

        <div class="mt-6 text-center text-sm text-slate-600">
            ${i18n.t("register.hasAccount")}
            <a href="/login" class="text-indigo-600 font-semibold hover:underline">${i18n.t("register.signInHere")}</a>
        </div>
    </div>
</body>
</html>

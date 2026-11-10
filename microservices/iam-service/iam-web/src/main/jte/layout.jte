@import gg.jte.Content
@param kh.edu.istad.platform.iam.web.i18n.I18n i18n
@param String title
@param Content content

<!DOCTYPE html>
<html lang="${i18n.getLang()}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title} - ${i18n.t("brand")}</title>
    <link href="https://fonts.googleapis.com/css2?family=Battambang:wght@400;700&family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        body {
            font-family: 'Inter', 'Battambang', sans-serif;
        }
    </style>
</head>
<body class="bg-slate-50 text-slate-800 min-h-screen flex flex-col">
    <!-- Navbar -->
    <header class="bg-white border-b border-slate-200 sticky top-0 z-50 shadow-sm">
        <div class="max-w-6xl mx-auto px-4 h-16 flex items-center justify-between">
            <a href="/" class="flex items-center space-x-2 text-indigo-600 font-bold text-xl tracking-tight">
                <svg class="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path></svg>
                <span>${i18n.t("brand")}</span>
            </a>
            
            <div class="flex items-center space-x-4">
                <!-- Navigation links -->
                <nav class="hidden md:flex items-center space-x-4 text-sm font-medium">
                    <a href="/" class="text-slate-600 hover:text-indigo-600 transition">${i18n.t("profile.overview")}</a>
                    <a href="/profile/edit" class="text-slate-600 hover:text-indigo-600 transition">${i18n.t("profile.editProfile")}</a>
                    <a href="/profile/password" class="text-slate-600 hover:text-indigo-600 transition">${i18n.t("profile.changePassword")}</a>
                    <a href="/profile/accounts" class="text-slate-600 hover:text-indigo-600 transition">${i18n.t("profile.linkedAccounts")}</a>
                    <a href="/profile/apps" class="text-slate-600 hover:text-indigo-600 transition">${i18n.t("profile.connectedApps")}</a>
                </nav>

                <!-- Language Switcher -->
                <div class="inline-flex rounded-lg border border-slate-200 bg-slate-100 p-1 text-xs font-medium">
                    <a href="?lang=en" class="px-2.5 py-1 rounded-md transition ${"en".equals(i18n.getLang()) ? "bg-white text-indigo-600 shadow-sm font-semibold" : "text-slate-600 hover:text-slate-900"}">
                        ${i18n.t("lang.english")}
                    </a>
                    <a href="?lang=km" class="px-2.5 py-1 rounded-md transition ${"km".equals(i18n.getLang()) ? "bg-white text-indigo-600 shadow-sm font-semibold" : "text-slate-600 hover:text-slate-900"}">
                        ${i18n.t("lang.khmer")}
                    </a>
                </div>

                <form action="/logout" method="post" class="inline">
                    <button type="submit" class="text-sm font-medium text-slate-600 hover:text-red-600 transition">
                        ${i18n.t("common.logout")}
                    </button>
                </form>
            </div>
        </div>
    </header>

    <!-- Main Content -->
    <main class="flex-grow max-w-6xl w-full mx-auto px-4 py-8">
        ${content}
    </main>

    <!-- Footer -->
    <footer class="bg-white border-t border-slate-200 py-6 mt-auto">
        <div class="max-w-6xl mx-auto px-4 text-center text-xs text-slate-500">
            &copy; 2026 ${i18n.t("brand")}. All rights reserved.
        </div>
    </footer>
</body>
</html>

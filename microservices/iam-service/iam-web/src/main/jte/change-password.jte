@param kh.edu.istad.platform.iam.web.i18n.I18n i18n
@param kh.edu.istad.platform.iam.web.dto.ChangePasswordForm form
@param String errorMessage = null

@template.layout(i18n = i18n, title = i18n.t("changePassword.title"), content = @`
    <div class="max-w-md mx-auto bg-white rounded-2xl shadow-sm border border-slate-200 p-8">
        <div class="mb-6 pb-4 border-b border-slate-100 flex items-center justify-between">
            <div>
                <h1 class="text-xl font-bold text-slate-900">${i18n.t("changePassword.title")}</h1>
                <p class="text-xs text-slate-500 mt-0.5">${i18n.t("changePassword.subtitle")}</p>
            </div>
            <a href="/" class="text-xs font-semibold text-slate-600 hover:text-indigo-600 transition">
                &larr; ${i18n.t("common.back")}
            </a>
        </div>

        @if(errorMessage != null)
            <div class="mb-4 p-3 bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg flex items-center space-x-2">
                <svg class="w-5 h-5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/></svg>
                <span>${errorMessage}</span>
            </div>
        @endif

        <form action="/profile/password" method="post" class="space-y-4">
            <div>
                <label for="currentPassword" class="block text-xs font-semibold text-slate-700 mb-1">${i18n.t("changePassword.currentPassword")}</label>
                <input type="password" id="currentPassword" name="currentPassword" required
                       class="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
            </div>

            <div>
                <label for="newPassword" class="block text-xs font-semibold text-slate-700 mb-1">${i18n.t("changePassword.newPassword")}</label>
                <input type="password" id="newPassword" name="newPassword" required minlength="8"
                       class="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
            </div>

            <div>
                <label for="confirmPassword" class="block text-xs font-semibold text-slate-700 mb-1">${i18n.t("changePassword.confirmPassword")}</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required minlength="8"
                       class="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
            </div>

            <div class="flex items-center justify-end space-x-3 pt-4 border-t border-slate-100">
                <a href="/" class="py-2.5 px-4 bg-slate-100 hover:bg-slate-200 text-slate-700 font-medium text-sm rounded-lg transition">
                    ${i18n.t("common.cancel")}
                </a>
                <button type="submit"
                        class="py-2.5 px-6 bg-indigo-600 hover:bg-indigo-700 text-white font-medium text-sm rounded-lg shadow-sm transition">
                    ${i18n.t("changePassword.submit")}
                </button>
            </div>
        </form>
    </div>
`)

@param kh.edu.istad.platform.iam.web.i18n.I18n i18n
@param java.util.List<kh.edu.istad.platform.iam.application.dto.IdentityResult> identities
@param Boolean hasPassword = true
@param String successMessage = null
@param String errorMessage = null

@template.layout(i18n = i18n, title = i18n.t("linkedAccounts.title"), content = @`
    <div class="max-w-3xl mx-auto bg-white rounded-2xl shadow-sm border border-slate-200 p-8">
        <div class="mb-6 pb-4 border-b border-slate-100 flex items-center justify-between">
            <div>
                <h1 class="text-xl font-bold text-slate-900">${i18n.t("linkedAccounts.title")}</h1>
                <p class="text-xs text-slate-500 mt-0.5">${i18n.t("linkedAccounts.subtitle")}</p>
            </div>
            <a href="/" class="text-xs font-semibold text-slate-600 hover:text-indigo-600 transition">
                &larr; ${i18n.t("common.back")}
            </a>
        </div>

        @if(successMessage != null)
            <div class="mb-4 p-3 bg-emerald-50 border border-emerald-200 text-emerald-700 text-sm rounded-lg flex items-center space-x-2">
                <svg class="w-5 h-5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/></svg>
                <span>${i18n.t("linkedAccounts.unlinkedSuccess")}</span>
            </div>
        @endif

        @if(errorMessage != null)
            <div class="mb-4 p-3 bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg flex items-center space-x-2">
                <svg class="w-5 h-5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/></svg>
                <span>${i18n.t("linkedAccounts.cannotUnlinkLast")}</span>
            </div>
        @endif

        <div class="divide-y divide-slate-100">
            @for(var item : identities)
                <div class="py-4 flex items-center justify-between">
                    <div class="flex items-center space-x-3">
                        <div class="w-10 h-10 rounded-full bg-slate-100 flex items-center justify-center font-bold text-slate-700 text-sm">
                            ${item.getProvider().name().substring(0, 1)}
                        </div>
                        <div>
                            <div class="font-medium text-slate-900">${item.getProvider().name()}</div>
                            <div class="text-xs text-slate-500">
                                ${item.getEmail() != null ? item.getEmail() : item.getDisplayName()}
                                &bull; ${item.getLinkedAt() != null ? item.getLinkedAt().toString() : ""}
                            </div>
                        </div>
                    </div>

                    <form action="/profile/accounts/${item.getUuid().toString()}/unlink" method="post">
                        <button type="submit"
                                class="py-1.5 px-3 bg-red-50 hover:bg-red-100 text-red-600 text-xs font-semibold rounded-lg transition">
                            ${i18n.t("linkedAccounts.unlink")}
                        </button>
                    </form>
                </div>
            @endfor
        </div>
    </div>
`)

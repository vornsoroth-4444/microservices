@param kh.edu.istad.platform.iam.web.i18n.I18n i18n
@param java.util.List<kh.edu.istad.platform.iam.web.dto.ConnectedAppModel> apps
@param String successMessage = null

@template.layout(i18n = i18n, title = i18n.t("connectedApps.title"), content = @`
    <div class="max-w-3xl mx-auto bg-white rounded-2xl shadow-sm border border-slate-200 p-8">
        <div class="mb-6 pb-4 border-b border-slate-100 flex items-center justify-between">
            <div>
                <h1 class="text-xl font-bold text-slate-900">${i18n.t("connectedApps.title")}</h1>
                <p class="text-xs text-slate-500 mt-0.5">${i18n.t("connectedApps.subtitle")}</p>
            </div>
            <a href="/" class="text-xs font-semibold text-slate-600 hover:text-indigo-600 transition">
                &larr; ${i18n.t("common.back")}
            </a>
        </div>

        @if(successMessage != null)
            <div class="mb-4 p-3 bg-emerald-50 border border-emerald-200 text-emerald-700 text-sm rounded-lg flex items-center space-x-2">
                <svg class="w-5 h-5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/></svg>
                <span>${i18n.t("connectedApps.revokedSuccess")}</span>
            </div>
        @endif

        @if(apps != null && !apps.isEmpty())
            <div class="divide-y divide-slate-100">
                @for(var app : apps)
                    <div class="py-4 flex items-center justify-between">
                        <div>
                            <div class="font-medium text-slate-900">${app.getClientName()}</div>
                            <div class="text-xs text-slate-400 font-mono">${app.getClientId()}</div>
                            <div class="flex flex-wrap gap-1 mt-2">
                                @for(String s : app.getScopes())
                                    <span class="px-2 py-0.5 bg-slate-100 text-slate-600 text-xs rounded border border-slate-200">
                                        ${s}
                                    </span>
                                @endfor
                            </div>
                        </div>

                        <form action="/profile/apps/${app.getClientId()}/revoke" method="post">
                            <button type="submit"
                                    class="py-1.5 px-3 bg-red-50 hover:bg-red-100 text-red-600 text-xs font-semibold rounded-lg transition">
                                ${i18n.t("connectedApps.revoke")}
                            </button>
                        </form>
                    </div>
                @endfor
            </div>
        @else
            <div class="py-12 text-center text-sm text-slate-400 italic">
                ${i18n.t("connectedApps.noApps")}
            </div>
        @endif
    </div>
`)

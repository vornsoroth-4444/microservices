@param kh.edu.istad.platform.iam.web.i18n.I18n i18n
@param kh.edu.istad.platform.iam.application.dto.UserResult user
@param kh.edu.istad.platform.iam.application.dto.UserAuthoritiesResult authorities
@param Boolean updated = false
@param Boolean passwordChanged = false

@template.layout(i18n = i18n, title = i18n.t("profile.title"), content = @`
    <div class="space-y-6">
        @if(updated != null && updated)
            <div class="p-4 bg-emerald-50 border border-emerald-200 text-emerald-700 text-sm rounded-xl flex items-center space-x-2">
                <svg class="w-5 h-5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/></svg>
                <span>${i18n.t("profileEdit.success")}</span>
            </div>
        @endif

        @if(passwordChanged != null && passwordChanged)
            <div class="p-4 bg-emerald-50 border border-emerald-200 text-emerald-700 text-sm rounded-xl flex items-center space-x-2">
                <svg class="w-5 h-5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/></svg>
                <span>${i18n.t("changePassword.success")}</span>
            </div>
        @endif

        <!-- Profile Header Card -->
        <div class="bg-white rounded-2xl shadow-sm border border-slate-200 p-6 flex flex-col md:flex-row items-center justify-between gap-4">
            <div class="flex items-center space-x-4">
                <div class="w-16 h-16 rounded-full bg-indigo-100 text-indigo-700 flex items-center justify-center font-bold text-2xl border-2 border-indigo-200 overflow-hidden">
                    @if(user.getPictureUrl() != null && !user.getPictureUrl().isBlank())
                        <img src="${user.getPictureUrl()}" alt="${user.getName()}" class="w-full h-full object-cover">
                    @else
                        ${user.getGivenName() != null && !user.getGivenName().isEmpty() ? user.getGivenName().substring(0, 1) : "U"}
                    @endif
                </div>
                <div>
                    <h1 class="text-xl font-bold text-slate-900">${i18n.t("profile.welcome", user.getName())}</h1>
                    <p class="text-sm text-slate-500">@${user.getUsername()} &bull; ${user.getEmail()}</p>
                </div>
            </div>

            <div class="flex flex-wrap gap-2">
                <a href="/profile/edit" class="py-2 px-3.5 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-semibold rounded-lg shadow-xs transition">
                    ${i18n.t("profile.editProfile")}
                </a>
                <a href="/profile/password" class="py-2 px-3.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold rounded-lg transition">
                    ${i18n.t("profile.changePassword")}
                </a>
                <a href="/profile/accounts" class="py-2 px-3.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold rounded-lg transition">
                    ${i18n.t("profile.linkedAccounts")}
                </a>
                <a href="/profile/apps" class="py-2 px-3.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold rounded-lg transition">
                    ${i18n.t("profile.connectedApps")}
                </a>
            </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- Personal Information -->
            <div class="bg-white rounded-2xl shadow-sm border border-slate-200 p-6">
                <h2 class="text-base font-bold text-slate-900 mb-4 pb-2 border-b border-slate-100">${i18n.t("profile.personalInfo")}</h2>
                <dl class="space-y-3 text-sm">
                    <div class="flex justify-between py-1 border-b border-slate-50">
                        <dt class="text-slate-500">${i18n.t("profile.username")}</dt>
                        <dd class="font-medium text-slate-900">${user.getUsername()}</dd>
                    </div>
                    <div class="flex justify-between py-1 border-b border-slate-50">
                        <dt class="text-slate-500">${i18n.t("profile.email")}</dt>
                        <dd class="font-medium text-slate-900 flex items-center space-x-1">
                            <span>${user.getEmail()}</span>
                            @if(user.isEmailVerified())
                                <span class="px-1.5 py-0.5 bg-emerald-100 text-emerald-800 text-xs rounded font-medium">${i18n.t("profile.emailVerified")}</span>
                            @endif
                        </dd>
                    </div>
                    <div class="flex justify-between py-1 border-b border-slate-50">
                        <dt class="text-slate-500">${i18n.t("profile.phone")}</dt>
                        <dd class="font-medium text-slate-900">${user.getPhoneNumber() != null ? user.getPhoneNumber() : "-"}</dd>
                    </div>
                    <div class="flex justify-between py-1 border-b border-slate-50">
                        <dt class="text-slate-500">${i18n.t("profile.locale")}</dt>
                        <dd class="font-medium text-slate-900 uppercase">${user.getLocale()}</dd>
                    </div>
                    <div class="flex justify-between py-1 border-b border-slate-50">
                        <dt class="text-slate-500">${i18n.t("profile.zoneinfo")}</dt>
                        <dd class="font-medium text-slate-900">${user.getZoneinfo() != null ? user.getZoneinfo() : "-"}</dd>
                    </div>
                    <div class="flex justify-between py-1 border-b border-slate-50">
                        <dt class="text-slate-500">${i18n.t("profile.gender")}</dt>
                        <dd class="font-medium text-slate-900">${user.getGender() != null ? user.getGender() : "-"}</dd>
                    </div>
                    <div class="flex justify-between py-1 border-b border-slate-50">
                        <dt class="text-slate-500">${i18n.t("profile.birthdate")}</dt>
                        <dd class="font-medium text-slate-900">${user.getBirthdate() != null ? user.getBirthdate().toString() : "-"}</dd>
                    </div>
                </dl>
            </div>

            <!-- Roles & Effective Permissions -->
            <div class="space-y-6">
                <!-- Roles -->
                <div class="bg-white rounded-2xl shadow-sm border border-slate-200 p-6">
                    <h2 class="text-base font-bold text-slate-900 mb-3 pb-2 border-b border-slate-100">${i18n.t("profile.roles")}</h2>
                    @if(authorities != null && authorities.getRoles() != null && !authorities.getRoles().isEmpty())
                        <div class="flex flex-wrap gap-2">
                            @for(var role : authorities.getRoles())
                                <span class="px-3 py-1 bg-indigo-50 border border-indigo-200 text-indigo-700 text-xs font-semibold rounded-full">
                                    ROLE_${role.getName()}
                                </span>
                            @endfor
                        </div>
                    @else
                        <p class="text-xs text-slate-400 italic">${i18n.t("profile.noRoles")}</p>
                    @endif
                </div>

                <!-- Effective Permissions -->
                <div class="bg-white rounded-2xl shadow-sm border border-slate-200 p-6">
                    <h2 class="text-base font-bold text-slate-900 mb-3 pb-2 border-b border-slate-100">${i18n.t("profile.effectivePermissions")}</h2>
                    @if(authorities != null && authorities.getEffectivePermissions() != null && !authorities.getEffectivePermissions().isEmpty())
                        <div class="flex flex-wrap gap-1.5 max-h-48 overflow-y-auto">
                            @for(String perm : authorities.getEffectivePermissions())
                                <span class="px-2.5 py-1 bg-slate-100 text-slate-700 text-xs font-mono rounded-md border border-slate-200">
                                    ${perm}
                                </span>
                            @endfor
                        </div>
                    @else
                        <p class="text-xs text-slate-400 italic">${i18n.t("profile.noPermissions")}</p>
                    @endif
                </div>
            </div>
        </div>
    </div>
`)

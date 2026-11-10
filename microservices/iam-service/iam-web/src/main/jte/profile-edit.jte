@param kh.edu.istad.platform.iam.web.i18n.I18n i18n
@param kh.edu.istad.platform.iam.web.dto.ProfileEditForm form

@template.layout(i18n = i18n, title = i18n.t("profileEdit.title"), content = @`
    <div class="max-w-3xl mx-auto bg-white rounded-2xl shadow-sm border border-slate-200 p-8">
        <div class="mb-6 pb-4 border-b border-slate-100 flex items-center justify-between">
            <div>
                <h1 class="text-xl font-bold text-slate-900">${i18n.t("profileEdit.title")}</h1>
                <p class="text-xs text-slate-500 mt-0.5">${i18n.t("profileEdit.subtitle")}</p>
            </div>
            <a href="/" class="text-xs font-semibold text-slate-600 hover:text-indigo-600 transition">
                &larr; ${i18n.t("common.back")}
            </a>
        </div>

        <form action="/profile/edit" method="post" class="space-y-6">
            <!-- Basic Names -->
            <div>
                <h3 class="text-xs font-bold uppercase tracking-wider text-slate-400 mb-3">${i18n.t("profile.personalInfo")}</h3>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <label for="givenName" class="block text-xs font-semibold text-slate-700 mb-1">${i18n.t("profileEdit.givenName")}</label>
                        <input type="text" id="givenName" name="givenName" value="${form.getGivenName()}" required
                               class="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
                    </div>
                    <div>
                        <label for="familyName" class="block text-xs font-semibold text-slate-700 mb-1">${i18n.t("profileEdit.familyName")}</label>
                        <input type="text" id="familyName" name="familyName" value="${form.getFamilyName()}" required
                               class="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
                    </div>
                    <div>
                        <label for="middleName" class="block text-xs font-semibold text-slate-700 mb-1">${i18n.t("profileEdit.middleName")}</label>
                        <input type="text" id="middleName" name="middleName" value="${form.getMiddleName()}"
                               class="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
                    </div>
                    <div>
                        <label for="nickname" class="block text-xs font-semibold text-slate-700 mb-1">${i18n.t("profileEdit.nickname")}</label>
                        <input type="text" id="nickname" name="nickname" value="${form.getNickname()}"
                               class="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
                    </div>
                </div>
            </div>

            <!-- Demographics & Contact -->
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                    <label for="gender" class="block text-xs font-semibold text-slate-700 mb-1">${i18n.t("profileEdit.gender")}</label>
                    <select id="gender" name="gender"
                            class="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
                        <option value="male" selected="${"male".equalsIgnoreCase(form.getGender())}">${i18n.t("profileEdit.gender.male")}</option>
                        <option value="female" selected="${"female".equalsIgnoreCase(form.getGender())}">${i18n.t("profileEdit.gender.female")}</option>
                        <option value="other" selected="${"other".equalsIgnoreCase(form.getGender())}">${i18n.t("profileEdit.gender.other")}</option>
                    </select>
                </div>
                <div>
                    <label for="birthdate" class="block text-xs font-semibold text-slate-700 mb-1">${i18n.t("profileEdit.birthdate")}</label>
                    <input type="date" id="birthdate" name="birthdate" value="${form.getBirthdate() != null ? form.getBirthdate().toString() : ""}"
                           class="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
                </div>
                <div>
                    <label for="phoneNumber" class="block text-xs font-semibold text-slate-700 mb-1">${i18n.t("profileEdit.phoneNumber")}</label>
                    <input type="text" id="phoneNumber" name="phoneNumber" value="${form.getPhoneNumber()}"
                           class="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
                </div>
            </div>

            <!-- URLs & Preferences -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                    <label for="profileUrl" class="block text-xs font-semibold text-slate-700 mb-1">${i18n.t("profileEdit.profileUrl")}</label>
                    <input type="url" id="profileUrl" name="profileUrl" value="${form.getProfileUrl()}"
                           class="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
                </div>
                <div>
                    <label for="pictureUrl" class="block text-xs font-semibold text-slate-700 mb-1">${i18n.t("profileEdit.pictureUrl")}</label>
                    <input type="url" id="pictureUrl" name="pictureUrl" value="${form.getPictureUrl()}"
                           class="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
                </div>
                <div>
                    <label for="zoneinfo" class="block text-xs font-semibold text-slate-700 mb-1">${i18n.t("profileEdit.zoneinfo")}</label>
                    <input type="text" id="zoneinfo" name="zoneinfo" value="${form.getZoneinfo()}" placeholder="Asia/Phnom_Penh"
                           class="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
                </div>
                <div>
                    <label for="locale" class="block text-xs font-semibold text-slate-700 mb-1">${i18n.t("profileEdit.locale")}</label>
                    <select id="locale" name="locale"
                            class="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
                        <option value="en" selected="${"en".equalsIgnoreCase(form.getLocale())}">${i18n.t("lang.english")}</option>
                        <option value="km" selected="${"km".equalsIgnoreCase(form.getLocale())}">${i18n.t("lang.khmer")}</option>
                    </select>
                </div>
            </div>

            <!-- Address -->
            <div>
                <h3 class="text-xs font-bold uppercase tracking-wider text-slate-400 mb-3">${i18n.t("profileEdit.address")}</h3>
                <div class="space-y-4">
                    <div>
                        <label for="streetAddress" class="block text-xs font-semibold text-slate-700 mb-1">${i18n.t("profileEdit.streetAddress")}</label>
                        <input type="text" id="streetAddress" name="streetAddress" value="${form.getStreetAddress()}"
                               class="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
                    </div>
                    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
                        <div>
                            <label for="locality" class="block text-xs font-semibold text-slate-700 mb-1">${i18n.t("profileEdit.locality")}</label>
                            <input type="text" id="locality" name="locality" value="${form.getLocality()}"
                                   class="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
                        </div>
                        <div>
                            <label for="region" class="block text-xs font-semibold text-slate-700 mb-1">${i18n.t("profileEdit.region")}</label>
                            <input type="text" id="region" name="region" value="${form.getRegion()}"
                                   class="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
                        </div>
                        <div>
                            <label for="postalCode" class="block text-xs font-semibold text-slate-700 mb-1">${i18n.t("profileEdit.postalCode")}</label>
                            <input type="text" id="postalCode" name="postalCode" value="${form.getPostalCode()}"
                                   class="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition">
                        </div>
                        <div>
                            <label for="country" class="block text-xs font-semibold text-slate-700 mb-1">${i18n.t("profileEdit.country")}</label>
                            <input type="text" id="country" name="country" value="${form.getCountry()}" placeholder="KH" maxlength="2"
                                   class="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white transition uppercase">
                        </div>
                    </div>
                </div>
            </div>

            <div class="flex items-center justify-end space-x-3 pt-4 border-t border-slate-100">
                <a href="/" class="py-2.5 px-4 bg-slate-100 hover:bg-slate-200 text-slate-700 font-medium text-sm rounded-lg transition">
                    ${i18n.t("common.cancel")}
                </a>
                <button type="submit"
                        class="py-2.5 px-6 bg-indigo-600 hover:bg-indigo-700 text-white font-medium text-sm rounded-lg shadow-sm transition">
                    ${i18n.t("common.save")}
                </button>
            </div>
        </form>
    </div>
`)

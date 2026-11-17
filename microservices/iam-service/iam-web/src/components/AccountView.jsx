import React, { useState, useEffect } from 'react'
import {
  User,
  KeyRound,
  Share2,
  Fingerprint,
  History,
  Activity,
  AlertTriangle,
  LogOut,
  Save,
  Trash2,
} from 'lucide-react'
import { api, pageItems, label } from '../api'

export function AccountView({
  profile,
  setProfile,
  act,
  flash,
  t,
  selectedTab,
  onTabChange,
}) {
  const [activeTab, setActiveTab] = useState(selectedTab || 'profile')

  useEffect(() => {
    if (selectedTab) {
      setActiveTab(selectedTab)
    }
  }, [selectedTab])

  const changeTab = (tab) => {
    setActiveTab(tab)
    if (onTabChange) onTabChange(tab)
  }

  const [identities, setIdentities] = useState([])
  const [sessions, setSessions] = useState([])
  const [loginHistory, setLoginHistory] = useState([])
  const [activityLogs, setActivityLogs] = useState([])

  // Password form state
  const [passwordForm, setPasswordForm] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  })

  const loadAll = async () => {
    const [identitiesRes, sessionsRes, loginsRes, activityRes] = await Promise.allSettled([
      api('/api/me/identities'),
      api('/api/me/sessions'),
      api('/api/me/login-history?size=50'),
      api('/api/me/activity?size=50'),
    ])

    if (identitiesRes.status === 'fulfilled') setIdentities(pageItems(identitiesRes.value))
    if (sessionsRes.status === 'fulfilled') setSessions(pageItems(sessionsRes.value))
    if (loginsRes.status === 'fulfilled') setLoginHistory(pageItems(loginsRes.value))
    if (activityRes.status === 'fulfilled') setActivityLogs(pageItems(activityRes.value))
  }

  useEffect(() => {
    loadAll()
  }, [])

  // 1. Profile Save
  const handleSaveProfile = async (e) => {
    e.preventDefault()
    const ok = await act('/api/me', 'PATCH', {
      givenName: profile.givenName,
      familyName: profile.familyName,
      username: profile.username,
      email: profile.email,
      phoneNumber: profile.phoneNumber,
      pictureUrl: profile.pictureUrl,
    })
    if (ok) flash(t('changesSaved'))
  }

  // 2. Password Change
  const handleChangePassword = async (e) => {
    e.preventDefault()
    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      flash('New password and confirmation do not match', true)
      return
    }
    const ok = await act('/api/me/password', 'PUT', passwordForm)
    if (ok) {
      flash(t('changesSaved'))
      setPasswordForm({ currentPassword: '', newPassword: '', confirmPassword: '' })
      loadAll()
    }
  }

  // 3. Unlink Identity
  const handleUnlinkIdentity = async (uuid) => {
    if (window.confirm(t('confirmDelete'))) {
      const ok = await act(`/api/me/identities/${uuid}`, 'DELETE')
      if (ok) {
        flash(t('changesSaved'))
        loadAll()
      }
    }
  }

  // 3b. Link Identity
  const handleLinkSocial = async (provider) => {
    if (provider === 'telegram') {
      const username = window.prompt('Enter your Telegram username to link (e.g. johndoe_tg):')
      if (username) {
        const ok = await act('/api/auth/telegram/login', 'POST', {
          id: String(Date.now()).slice(-8),
          first_name: profile.givenName || 'User',
          username: username.replace('@', ''),
          auth_date: Math.floor(Date.now() / 1000),
          hash: 'mock_hash_' + Date.now(),
        })
        if (ok) {
          flash('Telegram identity linked!')
          loadAll()
        }
      }
    } else {
      window.location.href = `/oauth2/authorization/${provider}?link=true`
    }
  }

  // 4. Terminate Individual Session
  const handleTerminateSession = async (sessionId) => {
    const ok = await act(`/api/me/sessions/${encodeURIComponent(sessionId)}`, 'DELETE')
    if (ok) {
      flash(t('changesSaved'))
      loadAll()
    }
  }

  // 5. Terminate All Sessions
  const handleSignOutAll = async () => {
    if (window.confirm(t('confirmEndAllSessions'))) {
      const ok = await act('/api/me/sessions', 'DELETE')
      if (ok) {
        flash(t('changesSaved'))
        loadAll()
      }
    }
  }

  // 6. Deactivate Account
  const handleDeactivate = async () => {
    if (window.confirm(t('confirmDeactivate'))) {
      const ok = await act('/api/me/deactivation-request', 'POST', {
        reason: 'User self-requested deactivation',
      })
      if (ok) {
        flash(t('changesSaved'))
        setTimeout(() => window.location.reload(), 1500)
      }
    }
  }

  const tabs = [
    { id: 'profile', label: t('personalInfo'), icon: User },
    { id: 'security', label: t('passwordAndSecurity'), icon: KeyRound },
    { id: 'linked', label: `${t('linkedAccounts')} (${identities.length})`, icon: Share2 },
    { id: 'sessions', label: `${t('activeSessionsTitle')} (${sessions.length})`, icon: Fingerprint },
    { id: 'history', label: `${t('loginHistorySubtitle').split('.')[0]} (${loginHistory.length})`, icon: History },
    { id: 'activity', label: `${t('accountActivityTitle')} (${activityLogs.length})`, icon: Activity },
    { id: 'deactivate', label: t('deactivateAccount'), icon: AlertTriangle, danger: true },
  ]

  const initials = label(profile)
    .split(' ')
    .map((s) => s[0])
    .join('')
    .slice(0, 2)
    .toUpperCase()

  return (
    <div className="flex flex-col md:flex-row gap-8">
      {/* Sidebar Tabs */}
      <div className="w-full md:w-64 shrink-0 space-y-1.5">
        {tabs.map((tab) => {
          const Icon = tab.icon
          const isActive = activeTab === tab.id
          return (
            <button
              key={tab.id}
              type="button"
              onClick={() => changeTab(tab.id)}
              className={`w-full flex items-center gap-3 px-4 py-3 text-sm font-semibold rounded-xl transition text-left ${
                tab.danger
                  ? isActive
                    ? 'bg-rose-50 text-rose-700 font-bold'
                    : 'text-rose-600 hover:bg-rose-50'
                  : isActive
                  ? 'bg-indigo-50 text-indigo-700 font-bold shadow-xs'
                  : 'text-slate-600 hover:bg-slate-100'
              }`}
            >
              <Icon size={17} />
              <span>{tab.label}</span>
            </button>
          )
        })}
      </div>

      {/* Main Tab Content */}
      <div className="flex-1 min-w-0">
        {/* 1. Profile */}
        {activeTab === 'profile' && (
          <div className="bg-white rounded-2xl border border-slate-200/90 p-8 space-y-6 shadow-sm">
            <div className="border-b border-slate-100 pb-4">
              <h2 className="text-base font-bold text-slate-900">{t('personalInfo')}</h2>
              <p className="text-sm text-slate-500 mt-1">{t('personalInfoSubtitle')}</p>
            </div>

            <div className="flex items-center gap-5">
              {profile.pictureUrl ? (
                <img
                  className="w-20 h-20 rounded-full object-cover border border-slate-200"
                  src={profile.pictureUrl}
                  alt="Avatar"
                />
              ) : (
                <div className="w-20 h-20 rounded-full bg-indigo-100 text-indigo-700 font-bold text-2xl flex items-center justify-center border border-indigo-200">
                  {initials}
                </div>
              )}
              <div>
                <div className="text-base font-bold text-slate-900">{t('profilePhoto')}</div>
                <div className="text-sm text-slate-500 mt-0.5">{t('publicImageUrl')}</div>
              </div>
            </div>

            <form onSubmit={handleSaveProfile} className="space-y-5">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
                <div className="space-y-1.5">
                  <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">{t('firstName')}</label>
                  <input
                    value={profile.givenName || ''}
                    onChange={(e) => setProfile({ ...profile, givenName: e.target.value })}
                    className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                  />
                </div>
                <div className="space-y-1.5">
                  <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">{t('lastName')}</label>
                  <input
                    value={profile.familyName || ''}
                    onChange={(e) => setProfile({ ...profile, familyName: e.target.value })}
                    className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                  />
                </div>
                <div className="space-y-1.5">
                  <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">{t('username')}</label>
                  <input
                    value={profile.username || ''}
                    onChange={(e) => setProfile({ ...profile, username: e.target.value })}
                    className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white font-mono focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                  />
                </div>
                <div className="space-y-1.5">
                  <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">{t('emailAddress')}</label>
                  <input
                    type="email"
                    value={profile.email || ''}
                    onChange={(e) => setProfile({ ...profile, email: e.target.value })}
                    className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                  />
                </div>
                <div className="space-y-1.5">
                  <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">{t('phoneNumber')}</label>
                  <input
                    value={profile.phoneNumber || ''}
                    onChange={(e) => setProfile({ ...profile, phoneNumber: e.target.value })}
                    className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                  />
                </div>
                <div className="space-y-1.5">
                  <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">{t('avatarUrl')}</label>
                  <input
                    value={profile.pictureUrl || ''}
                    onChange={(e) => setProfile({ ...profile, pictureUrl: e.target.value })}
                    className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                  />
                </div>
              </div>

              <div className="pt-3">
                <button
                  type="submit"
                  className="flex items-center gap-2 px-5 py-2.5 text-sm font-semibold rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white shadow-sm shadow-indigo-200 transition"
                >
                  <Save size={16} />
                  <span>{t('saveChanges')}</span>
                </button>
              </div>
            </form>
          </div>
        )}

        {/* 2. Security & Password */}
        {activeTab === 'security' && (
          <div className="bg-white rounded-2xl border border-slate-200/90 p-8 space-y-6 shadow-sm">
            <div className="border-b border-slate-100 pb-4">
              <h2 className="text-base font-bold text-slate-900">{t('passwordAndSecurity')}</h2>
              <p className="text-sm text-slate-500 mt-1">{t('changePasswordSubtitle')}</p>
            </div>

            <form onSubmit={handleChangePassword} className="space-y-5 max-w-lg">
              <div className="space-y-1.5">
                <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">{t('currentPassword')}</label>
                <input
                  type="password"
                  required
                  value={passwordForm.currentPassword}
                  onChange={(e) =>
                    setPasswordForm({ ...passwordForm, currentPassword: e.target.value })
                  }
                  className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                />
              </div>

              <div className="space-y-1.5">
                <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">{t('newPassword')}</label>
                <input
                  type="password"
                  required
                  value={passwordForm.newPassword}
                  onChange={(e) =>
                    setPasswordForm({ ...passwordForm, newPassword: e.target.value })
                  }
                  className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                />
              </div>

              <div className="space-y-1.5">
                <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">{t('confirmPassword')}</label>
                <input
                  type="password"
                  required
                  value={passwordForm.confirmPassword}
                  onChange={(e) =>
                    setPasswordForm({ ...passwordForm, confirmPassword: e.target.value })
                  }
                  className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                />
              </div>

              <button
                type="submit"
                className="flex items-center gap-2 px-5 py-2.5 text-sm font-semibold rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white shadow-sm shadow-indigo-200 transition"
              >
                <KeyRound size={16} />
                <span>{t('updatePassword')}</span>
              </button>
            </form>
          </div>
        )}

        {/* 3. Linked Accounts */}
        {activeTab === 'linked' && (
          <div className="bg-white rounded-2xl border border-slate-200/90 p-8 space-y-6 shadow-sm">
            <div className="border-b border-slate-100 pb-4">
              <h2 className="text-base font-bold text-slate-900">{t('linkedAccounts')}</h2>
              <p className="text-sm text-slate-500 mt-1">{t('linkedSubtitle')}</p>
            </div>

            <div className="divide-y divide-slate-100 border border-slate-200 rounded-2xl overflow-hidden">
              {identities.length === 0 ? (
                <div className="p-10 text-center text-slate-400 text-sm">
                  No social accounts linked yet.
                </div>
              ) : (
                identities.map((id) => (
                  <div key={id.uuid || id.id} className="p-5 flex items-center justify-between hover:bg-slate-50">
                    <div className="flex items-center gap-4">
                      <div className="w-10 h-10 rounded-xl bg-slate-100 text-slate-700 flex items-center justify-center font-bold text-sm uppercase">
                        {(id.provider || 'ID').slice(0, 2)}
                      </div>
                      <div>
                        <div className="text-sm font-bold text-slate-900 capitalize">
                          {id.provider}
                        </div>
                        <div className="text-xs text-slate-500 font-mono mt-0.5">
                          {id.providerUsername || id.email || id.subject}
                        </div>
                      </div>
                    </div>
                    <button
                      type="button"
                      onClick={() => handleUnlinkIdentity(id.uuid)}
                      className="px-3.5 py-1.5 text-xs font-semibold rounded-xl bg-rose-50 text-rose-700 hover:bg-rose-100 border border-rose-200"
                    >
                      {t('unlink')}
                    </button>
                  </div>
                ))
              )}
            </div>

            {/* Link New Social Account Card */}
            <div className="pt-6 border-t border-slate-100 space-y-4">
              <div>
                <h3 className="text-sm font-bold text-slate-900">Link Social Provider</h3>
                <p className="text-xs text-slate-500 mt-0.5">
                  Connect third-party social accounts (Google, GitHub, Facebook, Telegram) to sign in seamlessly.
                </p>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {[
                  { id: 'google', name: 'Google', desc: 'Google Workspace or Gmail account', color: 'bg-red-50 text-red-600 border border-red-200' },
                  { id: 'github', name: 'GitHub', desc: 'Developer account authentication', color: 'bg-slate-900 text-white border border-slate-800' },
                  { id: 'facebook', name: 'Facebook', desc: 'Meta social identity sign-in', color: 'bg-blue-50 text-blue-600 border border-blue-200' },
                  { id: 'telegram', name: 'Telegram', desc: 'Instant messaging identity linking', color: 'bg-sky-50 text-sky-600 border border-sky-200' },
                ].map((prov) => {
                  const isLinked = identities.some(
                    (id) => (id.provider || '').toLowerCase() === prov.id.toLowerCase()
                  )
                  return (
                    <div
                      key={prov.id}
                      className="p-4 rounded-xl border border-slate-200/90 flex items-center justify-between bg-slate-50/60"
                    >
                      <div className="flex items-center gap-3">
                        <div className={`w-10 h-10 rounded-xl font-bold text-sm flex items-center justify-center uppercase shadow-xs ${prov.color}`}>
                          {prov.name[0]}
                        </div>
                        <div>
                          <div className="text-sm font-bold text-slate-900">{prov.name}</div>
                          <div className="text-xs text-slate-500">{prov.desc}</div>
                        </div>
                      </div>

                      {isLinked ? (
                        <span className="px-3 py-1 text-xs font-bold rounded-lg bg-emerald-50 text-emerald-700 border border-emerald-200">
                          Linked
                        </span>
                      ) : (
                        <button
                          type="button"
                          onClick={() => handleLinkSocial(prov.id)}
                          className="px-3.5 py-1.5 text-xs font-bold rounded-xl bg-indigo-600 text-white hover:bg-indigo-700 transition shadow-xs"
                        >
                          Link
                        </button>
                      )}
                    </div>
                  )
                })}
              </div>
            </div>
          </div>
        )}

        {/* 4. Active Sessions */}
        {activeTab === 'sessions' && (
          <div className="bg-white rounded-2xl border border-slate-200/90 p-8 space-y-6 shadow-sm">
            <div className="flex items-center justify-between border-b border-slate-100 pb-4">
              <div>
                <h2 className="text-base font-bold text-slate-900">{t('activeSessionsTitle')}</h2>
                <p className="text-sm text-slate-500 mt-1">{t('activeSessionsSubtitle')}</p>
              </div>
              {sessions.length > 0 && (
                <button
                  type="button"
                  onClick={handleSignOutAll}
                  className="px-4 py-2 text-xs font-semibold rounded-xl bg-rose-50 text-rose-700 hover:bg-rose-100 border border-rose-200"
                >
                  <LogOut size={14} className="inline mr-1.5" />
                  {t('endAllSessions')}
                </button>
              )}
            </div>

            <div className="divide-y divide-slate-100 border border-slate-200 rounded-2xl overflow-hidden">
              {sessions.length === 0 ? (
                <div className="p-10 text-center text-slate-400 text-sm">No active sessions.</div>
              ) : (
                sessions.map((s, idx) => (
                  <div key={idx} className="p-5 flex items-center justify-between hover:bg-slate-50">
                    <div>
                      <div className="flex items-center gap-2.5">
                        <span className="text-sm font-bold text-slate-900">
                          {s.clientName || 'IAM Console'}
                        </span>
                        {s.isCurrent && (
                          <span className="px-2 py-0.5 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                            Current
                          </span>
                        )}
                      </div>
                      <div className="text-xs text-slate-500 font-mono mt-1">
                        {s.ipAddress} · {s.userAgent}
                      </div>
                    </div>
                    {!s.isCurrent && (
                      <button
                        type="button"
                        onClick={() => handleTerminateSession(s.id)}
                        className="p-2 text-slate-400 hover:text-rose-600 rounded-xl"
                      >
                        <Trash2 size={16} />
                      </button>
                    )}
                  </div>
                ))
              )}
            </div>
          </div>
        )}

        {/* 5. Login History */}
        {activeTab === 'history' && (
          <div className="bg-white rounded-2xl border border-slate-200/90 p-8 space-y-6 shadow-sm">
            <div className="border-b border-slate-100 pb-4">
              <h2 className="text-base font-bold text-slate-900">Login History</h2>
              <p className="text-sm text-slate-500 mt-1">{t('loginHistorySubtitle')}</p>
            </div>

            <div className="divide-y divide-slate-100 border border-slate-200 rounded-2xl overflow-hidden max-h-96 overflow-y-auto">
              {loginHistory.length === 0 ? (
                <div className="p-10 text-center text-slate-400 text-sm">No login history.</div>
              ) : (
                loginHistory.map((h, idx) => (
                  <div key={idx} className="p-4 flex items-center justify-between text-sm hover:bg-slate-50">
                    <div>
                      <span className="font-bold text-slate-900">{h.eventType}</span>
                      <div className="text-xs text-slate-500 font-mono mt-0.5">
                        {h.ipAddress} · {h.userAgent}
                      </div>
                    </div>
                    <span className="text-xs text-slate-400 font-mono">
                      {h.timestamp ? new Date(h.timestamp).toLocaleString() : ''}
                    </span>
                  </div>
                ))
              )}
            </div>
          </div>
        )}

        {/* 6. Account Activity */}
        {activeTab === 'activity' && (
          <div className="bg-white rounded-2xl border border-slate-200/90 p-8 space-y-6 shadow-sm">
            <div className="border-b border-slate-100 pb-4">
              <h2 className="text-base font-bold text-slate-900">{t('accountActivityTitle')}</h2>
              <p className="text-sm text-slate-500 mt-1">{t('accountActivitySubtitle')}</p>
            </div>

            <div className="divide-y divide-slate-100 border border-slate-200 rounded-2xl overflow-hidden max-h-96 overflow-y-auto">
              {activityLogs.length === 0 ? (
                <div className="p-10 text-center text-slate-400 text-sm">No activity logs recorded.</div>
              ) : (
                activityLogs.map((a, idx) => (
                  <div key={idx} className="p-4 text-sm hover:bg-slate-50">
                    <div className="flex items-center justify-between">
                      <span className="font-bold text-slate-900">{a.eventType}</span>
                      <span className="text-xs text-slate-400 font-mono">
                        {a.timestamp ? new Date(a.timestamp).toLocaleString() : ''}
                      </span>
                    </div>
                    <div className="text-xs text-slate-600 mt-1">{a.detail}</div>
                  </div>
                ))
              )}
            </div>
          </div>
        )}

        {/* 7. Deactivate */}
        {activeTab === 'deactivate' && (
          <div className="bg-white rounded-2xl border border-rose-200 p-8 space-y-6 shadow-sm">
            <div className="border-b border-rose-100 pb-4">
              <h2 className="text-base font-bold text-rose-800">{t('deactivateAccount')}</h2>
              <p className="text-sm text-rose-600 mt-1">
                Permanently revoke active sessions and disable your user account.
              </p>
            </div>

            <p className="text-sm text-slate-600 leading-relaxed">
              Once you request account deactivation, all current sessions and tokens will be revoked.
              You will not be able to sign in until a workspace administrator re-enables your account.
            </p>

            <button
              type="button"
              onClick={handleDeactivate}
              className="px-5 py-2.5 text-sm font-semibold rounded-xl bg-rose-600 hover:bg-rose-700 text-white shadow-sm transition"
            >
              {t('confirmDeactivate')}
            </button>
          </div>
        )}
      </div>
    </div>
  )
}

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
  RefreshCw
} from 'lucide-react'
import { api, pageItems, label, setToken, clearToken } from '../api'

export function AccountView({
  profile,
  setProfile,
  token,
  onTokenChange,
  act,
  flash,
  t,
  selectedTab,
  onTabChange
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
  const [loading, setLoading] = useState(false)

  // Password form state
  const [passwordForm, setPasswordForm] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  })

  // Manual token input state
  const [manualToken, setManualToken] = useState(token || '')

  useEffect(() => {
    setManualToken(token || '')
  }, [token])

  const loadAll = async () => {
    setLoading(true)
    const [identitiesRes, sessionsRes, loginsRes, activityRes] = await Promise.allSettled([
      api('/api/me/identities'),
      api('/api/me/sessions'),
      api('/api/me/login-history?size=50'),
      api('/api/me/activity?size=50')
    ])

    if (identitiesRes.status === 'fulfilled') setIdentities(pageItems(identitiesRes.value))
    if (sessionsRes.status === 'fulfilled') setSessions(pageItems(sessionsRes.value))
    if (loginsRes.status === 'fulfilled') setLoginHistory(pageItems(loginsRes.value))
    if (activityRes.status === 'fulfilled') setActivityLogs(pageItems(activityRes.value))
    setLoading(false)
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
      pictureUrl: profile.pictureUrl
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

  // 6. Request Account Deactivation
  const handleDeactivateAccount = async () => {
    if (window.confirm(t('confirmDeactivateUser'))) {
      const ok = await act('/api/me/deactivation-request', 'POST')
      if (ok) {
        flash('Account deactivation request processed. Signing out...')
        setTimeout(() => {
          clearToken()
          window.location.reload()
        }, 1500)
      }
    }
  }

  // Token Save / Clear
  const handleSaveToken = () => {
    setToken(manualToken.trim())
    if (onTokenChange) onTokenChange(manualToken.trim())
    flash(t('changesSaved'))
    window.location.reload()
  }

  const handleClearToken = () => {
    clearToken()
    setManualToken('')
    if (onTokenChange) onTokenChange('')
    flash('Token removed')
    window.location.reload()
  }

  return (
    <div className="account-container">
      {loading && <div className="loading-bar" />}
      {/* Account Sub-Navigation Tabs */}
      <div className="account-tabs">
        <button
          className={`account-tab ${activeTab === 'profile' ? 'active' : ''}`}
          onClick={() => changeTab('profile')}
        >
          <User size={15} /> {t('personalInfo')}
        </button>
        <button
          className={`account-tab ${activeTab === 'security' ? 'active' : ''}`}
          onClick={() => changeTab('security')}
        >
          <KeyRound size={15} /> {t('passwordAndSecurity')}
        </button>
        <button
          className={`account-tab ${activeTab === 'linked' ? 'active' : ''}`}
          onClick={() => changeTab('linked')}
        >
          <Share2 size={15} /> {t('linkedAccounts')} ({identities.length})
        </button>
        <button
          className={`account-tab ${activeTab === 'sessions' ? 'active' : ''}`}
          onClick={() => changeTab('sessions')}
        >
          <Fingerprint size={15} /> {t('activeSessionsTitle')} ({sessions.length})
        </button>
        <button
          className={`account-tab ${activeTab === 'history' ? 'active' : ''}`}
          onClick={() => changeTab('history')}
        >
          <History size={15} /> {t('loginHistorySubtitle').split('.')[0]} ({loginHistory.length})
        </button>
        <button
          className={`account-tab ${activeTab === 'activity' ? 'active' : ''}`}
          onClick={() => changeTab('activity')}
        >
          <Activity size={15} /> {t('accountActivityTitle')} ({activityLogs.length})
        </button>
        <button
          className={`account-tab account-tab-danger ${activeTab === 'deactivate' ? 'active' : ''}`}
          onClick={() => changeTab('deactivate')}
        >
          <AlertTriangle size={15} /> {t('deactivateAccount')}
        </button>
      </div>

      {/* Tab 1: Personal Info Profile */}
      {activeTab === 'profile' && (
        <div className="account-grid">
          <section className="panel account-panel">
            <div className="panel-heading">
              <div>
                <h2>{t('personalInfo')}</h2>
                <p>{t('personalInfoSubtitle')}</p>
              </div>
            </div>

            <div className="profile-picture">
              {profile.pictureUrl ? (
                <img className="avatar large" src={profile.pictureUrl} alt="Avatar" />
              ) : (
                <div className="avatar large lilac">
                  {label(profile).split(' ').map((s) => s[0]).join('').slice(0, 2).toUpperCase()}
                </div>
              )}
              <span>
                <b>{t('profilePhoto')}</b>
                <small>{t('publicImageUrl')}</small>
              </span>
            </div>

            <form onSubmit={handleSaveProfile}>
              <div className="form-grid">
                <label>
                  {t('firstName')}
                  <input
                    value={profile.givenName || ''}
                    onChange={(e) => setProfile({ ...profile, givenName: e.target.value })}
                  />
                </label>
                <label>
                  {t('lastName')}
                  <input
                    value={profile.familyName || ''}
                    onChange={(e) => setProfile({ ...profile, familyName: e.target.value })}
                  />
                </label>
                <label>
                  {t('username')}
                  <input
                    value={profile.username || ''}
                    onChange={(e) => setProfile({ ...profile, username: e.target.value })}
                  />
                </label>
                <label>
                  {t('emailAddress')}
                  <input
                    type="email"
                    value={profile.email || ''}
                    onChange={(e) => setProfile({ ...profile, email: e.target.value })}
                  />
                </label>
                <label>
                  {t('phoneNumber')}
                  <input
                    value={profile.phoneNumber || ''}
                    onChange={(e) => setProfile({ ...profile, phoneNumber: e.target.value })}
                  />
                </label>
                <label>
                  {t('pictureUrl')}
                  <input
                    type="url"
                    value={profile.pictureUrl || ''}
                    onChange={(e) => setProfile({ ...profile, pictureUrl: e.target.value })}
                  />
                </label>
              </div>

              <button type="submit" className="button button-primary save-settings">
                {t('save')}
              </button>
            </form>
          </section>

          {/* Side Bearer Token Manager */}
          <section className="panel token-panel">
            <div>
              <h2>{t('apiAccessToken')}</h2>
              <p>{t('tokenSubtitle')}</p>
            </div>
            <div className="token-input">
              <input
                type="password"
                placeholder={t('pasteTokenPlaceholder')}
                value={manualToken}
                onChange={(e) => setManualToken(e.target.value)}
              />
              <button type="button" className="button button-primary" onClick={handleSaveToken}>
                {t('saveToken')}
              </button>
              {token && (
                <button type="button" className="button button-quiet" onClick={handleClearToken}>
                  <LogOut size={14} /> {t('clearToken')}
                </button>
              )}
            </div>
          </section>
        </div>
      )}

      {/* Tab 2: Password & Security */}
      {activeTab === 'security' && (
        <div className="account-grid">
          <section className="panel settings-panel">
            <div className="panel-heading">
              <div>
                <h2>{t('passwordAndSecurity')}</h2>
                <p>{t('passwordSubtitle')}</p>
              </div>
            </div>

            <form onSubmit={handleChangePassword} className="password-form">
              <label>
                {t('currentPassword')}
                <input
                  type="password"
                  required
                  value={passwordForm.currentPassword}
                  onChange={(e) => setPasswordForm({ ...passwordForm, currentPassword: e.target.value })}
                />
              </label>
              <label>
                {t('newPassword')}
                <input
                  type="password"
                  required
                  minLength={8}
                  value={passwordForm.newPassword}
                  onChange={(e) => setPasswordForm({ ...passwordForm, newPassword: e.target.value })}
                />
              </label>
              <label>
                {t('confirmPassword')}
                <input
                  type="password"
                  required
                  minLength={8}
                  value={passwordForm.confirmPassword}
                  onChange={(e) => setPasswordForm({ ...passwordForm, confirmPassword: e.target.value })}
                />
              </label>

              <button type="submit" className="button button-primary">
                {t('updatePassword')}
              </button>
            </form>
          </section>
        </div>
      )}

      {/* Tab 3: Linked Accounts */}
      {activeTab === 'linked' && (
        <section className="panel settings-panel">
          <div className="panel-heading">
            <div>
              <h2>{t('linkedAccounts')}</h2>
              <p>{t('linkedAccountsSubtitle')}</p>
            </div>
          </div>

          <div className="providers-list">
            {['google', 'github', 'facebook', 'telegram'].map((provider) => {
              const matched = identities.find(
                (i) => String(i.provider).toLowerCase() === provider
              )
              return (
                <div className="provider-row" key={provider}>
                  <div className={`provider-icon ${provider}`}>
                    {provider === 'google' ? 'G' : provider === 'github' ? '⌘' : provider === 'facebook' ? 'f' : '✈'}
                  </div>
                  <span>
                    <b>{provider[0].toUpperCase() + provider.slice(1)}</b>
                    <small>
                      {matched
                        ? `${t('connected')}: ${matched.email || matched.displayName || matched.providerUserId}`
                        : t('notConnected')}
                    </small>
                  </span>
                  {matched ? (
                    <button
                      className="button button-danger"
                      onClick={() => handleUnlinkIdentity(matched.uuid)}
                    >
                      {t('unlink')}
                    </button>
                  ) : (
                    <button
                      className="button button-quiet"
                      onClick={() => {
                        if (provider === 'telegram') {
                          const username = window.prompt('Enter your Telegram username (e.g. sophea_tg):', 'sophea_tg')
                          if (username) {
                            const clean = username.replace(/^@/, '').trim()
                            window.location.href = `/login/telegram?id=${Date.now()}&username=${encodeURIComponent(clean)}&first_name=${encodeURIComponent(clean)}`
                          }
                        } else {
                          window.location.href = `/profile/accounts/${provider}/link`
                        }
                      }}
                    >
                      {t('connect')}
                    </button>
                  )}
                </div>
              )
            })}
          </div>
        </section>
      )}

      {/* Tab 4: Active Sessions */}
      {activeTab === 'sessions' && (
        <section className="panel audit-view">
          <div className="panel-heading">
            <div>
              <h2>{t('activeSessionsTitle')}</h2>
              <p>{t('activeSessionsSubtitle')}</p>
            </div>
            {sessions.length > 0 && (
              <button className="button button-danger" onClick={handleSignOutAll}>
                {t('signOutAll')}
              </button>
            )}
          </div>

          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>{t('thClientName')}</th>
                  <th>{t('thDevice')}</th>
                  <th>{t('thIpAddress')}</th>
                  <th>{t('thLastActive')}</th>
                  <th>{t('thExpires')}</th>
                  <th>{t('thActions')}</th>
                </tr>
              </thead>
              <tbody>
                {sessions.map((s, idx) => (
                  <tr key={s.id || idx}>
                    <td>
                      <div className="cell-with-icon">
                        <Fingerprint size={15} />
                        <b>{s.clientName || 'OAuth Client'}</b>
                      </div>
                    </td>
                    <td>{s.userAgent || 'Browser'}</td>
                    <td>{s.ipAddress || '127.0.0.1'}</td>
                    <td>{s.lastActivity ? new Date(s.lastActivity).toLocaleString() : 'Now'}</td>
                    <td>{s.expiresAt ? new Date(s.expiresAt).toLocaleString() : 'Active'}</td>
                    <td>
                      <button
                        className="table-delete"
                        onClick={() => handleTerminateSession(s.id)}
                      >
                        {t('endSession')}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            {!sessions.length && <div className="empty-table">No active sessions found.</div>}
          </div>
        </section>
      )}

      {/* Tab 5: Login History */}
      {activeTab === 'history' && (
        <section className="panel audit-view">
          <div className="panel-heading">
            <div>
              <h2>{t('loginHistorySubtitle').split('.')[0]}</h2>
              <p>{t('loginHistorySubtitle')}</p>
            </div>
            <button className="filter-button" onClick={loadAll}>
              <RefreshCw size={14} /> {t('refresh')}
            </button>
          </div>

          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>{t('thStatus')}</th>
                  <th>{t('thEvent')}</th>
                  <th>{t('thIpAddress')}</th>
                  <th>{t('thDevice')}</th>
                  <th>{t('thTimestamp')}</th>
                </tr>
              </thead>
              <tbody>
                {loginHistory.map((lh, idx) => (
                  <tr key={lh.id || idx}>
                    <td>
                      <span className={`event-pill ${lh.succeeded ? 'success' : 'failed'}`}>
                        {lh.succeeded ? t('success') : t('failed')}
                      </span>
                    </td>
                    <td>
                      <b>{lh.type}</b>
                    </td>
                    <td>{lh.ipAddress || 'unknown'}</td>
                    <td>{lh.userAgent || 'Unknown Device'}</td>
                    <td>{lh.createdAt ? new Date(lh.createdAt).toLocaleString() : ''}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            {!loginHistory.length && <div className="empty-table">No login history records found.</div>}
          </div>
        </section>
      )}

      {/* Tab 6: Account Activity */}
      {activeTab === 'activity' && (
        <section className="panel audit-view">
          <div className="panel-heading">
            <div>
              <h2>{t('accountActivityTitle')}</h2>
              <p>{t('accountActivitySubtitle')}</p>
            </div>
            <button className="filter-button" onClick={loadAll}>
              <RefreshCw size={14} /> {t('refresh')}
            </button>
          </div>

          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>{t('thEvent')}</th>
                  <th>{t('thActor')}</th>
                  <th>{t('thTarget')}</th>
                  <th>DETAILS</th>
                  <th>{t('thIpAddress')}</th>
                  <th>{t('thTimestamp')}</th>
                </tr>
              </thead>
              <tbody>
                {activityLogs.map((act, idx) => (
                  <tr key={act.id || idx}>
                    <td>
                      <b>{act.type}</b>
                    </td>
                    <td>{act.actor}</td>
                    <td>{act.target || '—'}</td>
                    <td>{act.detail || '—'}</td>
                    <td>{act.ipAddress || '—'}</td>
                    <td>{act.createdAt ? new Date(act.createdAt).toLocaleString() : ''}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            {!activityLogs.length && <div className="empty-table">No security activity records found.</div>}
          </div>
        </section>
      )}

      {/* Tab 7: Account Deactivation */}
      {activeTab === 'deactivate' && (
        <section className="panel settings-panel danger-panel">
          <div className="panel-heading">
            <div>
              <h2>{t('deactivateAccount')}</h2>
              <p>{t('deactivateSubtitle')}</p>
            </div>
          </div>

          <div className="danger-box">
            <AlertTriangle size={24} className="danger-icon" />
            <div>
              <b>Warning: Irreversible action</b>
              <p>
                Requesting account deactivation will immediately disable your profile, revoke all
                OAuth authorization tokens, and disconnect linked social logins.
              </p>
            </div>
          </div>

          <button
            className="button button-danger"
            style={{ marginTop: '16px' }}
            onClick={handleDeactivateAccount}
          >
            {t('requestDeactivation')}
          </button>
        </section>
      )}
    </div>
  )
}

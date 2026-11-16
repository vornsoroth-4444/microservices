import React, { useState, useEffect } from 'react'
import { X, ShieldCheck, KeyRound, Fingerprint, History, Lock, Unlock, UserX, AlertTriangle, CheckCircle } from 'lucide-react'
import { api, pageItems, label } from '../api'

export function UserDetailModal({ user, permissionsCatalog = [], rolesCatalog = [], onClose, onRefresh, t }) {
  const [activeTab, setActiveTab] = useState('roles')
  const [detail, setDetail] = useState(user)
  const [authorities, setAuthorities] = useState({ roles: [], directPermissions: [], effectivePermissions: [] })
  const [sessions, setSessions] = useState([])
  const [loginHistory, setLoginHistory] = useState([])
  const [selectedRoleUuid, setSelectedRoleUuid] = useState('')
  const [selectedPermUuid, setSelectedPermUuid] = useState('')
  const [permEffect, setPermEffect] = useState('GRANT')
  const [tempPassword, setTempPassword] = useState('')
  const [busy, setBusy] = useState(false)
  const [msg, setMsg] = useState({ text: '', isError: false })

  const uuid = user.uuid

  const flash = (text, isError = false) => {
    setMsg({ text, isError })
    setTimeout(() => setMsg({ text: '', isError: false }), 4000)
  }

  const loadData = async () => {
    setBusy(true)
    const results = await Promise.allSettled([
      api(`/api/users/${uuid}`),
      api(`/api/users/${uuid}/authorities`),
      api(`/api/users/${uuid}/sessions`),
      api(`/api/users/${uuid}/login-history?size=50`)
    ])

    if (results[0].status === 'fulfilled') setDetail(results[0].value)
    if (results[1].status === 'fulfilled') setAuthorities(results[1].value)
    if (results[2].status === 'fulfilled') setSessions(pageItems(results[2].value))
    if (results[3].status === 'fulfilled') setLoginHistory(pageItems(results[3].value))
    setBusy(false)
  }

  useEffect(() => {
    loadData()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [uuid])

  const handleAction = async (endpoint, method = 'POST', body = null) => {
    try {
      setBusy(true)
      await api(endpoint, {
        method,
        body: body ? JSON.stringify(body) : undefined
      })
      flash(t('changesSaved'))
      await loadData()
      if (onRefresh) onRefresh()
      return true
    } catch (e) {
      flash(e.message, true)
      return false
    } finally {
      setBusy(false)
    }
  }

  const handleAssignRole = async () => {
    if (!selectedRoleUuid) return
    const ok = await handleAction(`/api/users/${uuid}/roles`, 'POST', {
      roleUuids: [selectedRoleUuid],
      reason: 'Assigned from IAM admin console'
    })
    if (ok) setSelectedRoleUuid('')
  }

  const handleRevokeRole = async (roleUuid) => {
    if (window.confirm(t('confirmDelete'))) {
      await handleAction(`/api/users/${uuid}/roles/${roleUuid}?reason=Revoked+by+admin`, 'DELETE')
    }
  }

  const handleGrantPermission = async () => {
    if (!selectedPermUuid) return
    const ok = await handleAction(`/api/users/${uuid}/permissions`, 'POST', {
      permissionUuids: [selectedPermUuid],
      effect: permEffect,
      reason: 'Granted from IAM admin console'
    })
    if (ok) setSelectedPermUuid('')
  }

  const handleRevokePermission = async (permUuid) => {
    if (window.confirm(t('confirmDelete'))) {
      await handleAction(`/api/users/${uuid}/permissions/${permUuid}?reason=Revoked+by+admin`, 'DELETE')
    }
  }

  const handleResetPassword = async (e) => {
    e.preventDefault()
    if (!tempPassword) return
    const ok = await handleAction(`/api/users/${uuid}/reset-password`, 'POST', {
      newPassword: tempPassword
    })
    if (ok) setTempPassword('')
  }

  const handleTerminateSession = async (sessionId) => {
    await handleAction(`/api/users/${uuid}/sessions/${encodeURIComponent(sessionId)}`, 'DELETE')
  }

  const handleTerminateAllSessions = async () => {
    if (window.confirm(t('confirmEndAllSessions'))) {
      await handleAction(`/api/users/${uuid}/sessions`, 'DELETE')
    }
  }

  const handleDeleteUser = async () => {
    if (window.confirm(t('confirmDelete'))) {
      const ok = await handleAction(`/api/users/${uuid}`, 'DELETE')
      if (ok) onClose()
    }
  }

  return (
    <div className="modal-backdrop" onMouseDown={onClose}>
      <div className="modal user-admin-modal" onMouseDown={(e) => e.stopPropagation()}>
        {/* Header */}
        <div className="modal-head">
          <div className="user-admin-head">
            <div className={`avatar large ${detail.color || 'lilac'}`}>
              {label(detail).split(' ').map((s) => s[0]).join('').slice(0, 2).toUpperCase()}
            </div>
            <div>
              <h2>{label(detail)}</h2>
              <p>{detail.email} · @{detail.username} · {detail.phoneNumber || 'No phone'}</p>
            </div>
          </div>
          <button type="button" className="modal-close" onClick={onClose}>
            <X size={19} />
          </button>
        </div>

        {/* Feedback message banner */}
        {msg.text && (
          <div className={`modal-alert ${msg.isError ? 'alert-danger' : 'alert-success'}`}>
            {msg.isError ? <AlertTriangle size={15} /> : <CheckCircle size={15} />}
            <span>{msg.text}</span>
          </div>
        )}

        {/* Quick status & Admin Actions Bar */}
        <div className="admin-action-row">
          <span className={`status ${detail.enabled === false ? 'inactive' : ''}`}>
            <i />
            {detail.enabled === false ? t('disabled') : t('enabled')} · {detail.accountNonLocked === false ? t('locked') : t('unlocked')}
          </span>

          <div className="action-buttons-group">
            <button
              className="button button-quiet"
              onClick={() => handleAction(`/api/users/${uuid}/${detail.enabled === false ? 'enable' : 'disable'}`)}
              disabled={busy}
            >
              {detail.enabled === false ? t('activate') : t('deactivate')}
            </button>

            <button
              className="button button-quiet"
              onClick={() => handleAction(`/api/users/${uuid}/${detail.accountNonLocked === false ? 'unlock' : 'lock'}`)}
              disabled={busy}
            >
              {detail.accountNonLocked === false ? <Unlock size={14} /> : <Lock size={14} />}
              {detail.accountNonLocked === false ? t('unlock') : t('lock')}
            </button>

            <button
              className="button button-danger"
              onClick={handleDeleteUser}
              disabled={busy}
            >
              <UserX size={14} /> {t('delete')}
            </button>
          </div>
        </div>

        {/* Tabs Navigation */}
        <div className="modal-tabs">
          <button
            className={`tab-btn ${activeTab === 'roles' ? 'active' : ''}`}
            onClick={() => setActiveTab('roles')}
          >
            <ShieldCheck size={14} /> {t('Roles')} ({authorities.roles?.length || 0})
          </button>
          <button
            className={`tab-btn ${activeTab === 'permissions' ? 'active' : ''}`}
            onClick={() => setActiveTab('permissions')}
          >
            <KeyRound size={14} /> {t('Permissions')} ({authorities.directPermissions?.length || 0})
          </button>
          <button
            className={`tab-btn ${activeTab === 'sessions' ? 'active' : ''}`}
            onClick={() => setActiveTab('sessions')}
          >
            <Fingerprint size={14} /> {t('Sessions')} ({sessions.length})
          </button>
          <button
            className={`tab-btn ${activeTab === 'history' ? 'active' : ''}`}
            onClick={() => setActiveTab('history')}
          >
            <History size={14} /> {t('userLoginHistoryTitle')} ({loginHistory.length})
          </button>
          <button
            className={`tab-btn ${activeTab === 'security' ? 'active' : ''}`}
            onClick={() => setActiveTab('security')}
          >
            <Lock size={14} /> {t('resetPassword')}
          </button>
        </div>

        {/* Tab 1: Roles Assignment */}
        {activeTab === 'roles' && (
          <div className="admin-section">
            <h3>{t('Roles')}</h3>
            <p className="section-help">{t('rolesSubtitle')}</p>

            <div className="admin-list">
              {(authorities.roles || []).map((r) => (
                <div className="admin-item" key={r.uuid || r.name}>
                  <div>
                    <b>{r.name}</b>
                    <small>{r.description || 'Assigned system role'}</small>
                  </div>
                  <button
                    className="table-delete"
                    onClick={() => handleRevokeRole(r.uuid)}
                    disabled={busy}
                  >
                    {t('revoke')}
                  </button>
                </div>
              ))}
              {!authorities.roles?.length && (
                <div className="empty-subtext">No roles currently assigned to this user.</div>
              )}
            </div>

            <div className="admin-assign">
              <select
                value={selectedRoleUuid}
                onChange={(e) => setSelectedRoleUuid(e.target.value)}
              >
                <option value="">{t('chooseRole')}</option>
                {rolesCatalog.map((rc) => (
                  <option key={rc.uuid} value={rc.uuid}>
                    {rc.name}
                  </option>
                ))}
              </select>
              <button
                className="button button-primary"
                disabled={!selectedRoleUuid || busy}
                onClick={handleAssignRole}
              >
                {t('create')}
              </button>
            </div>
          </div>
        )}

        {/* Tab 2: Direct Permissions */}
        {activeTab === 'permissions' && (
          <div className="admin-section">
            <h3>{t('Permissions')}</h3>
            <p className="section-help">{t('permissionsSubtitle')}</p>

            <div className="admin-list">
              {(authorities.directPermissions || []).map((p) => {
                const catalogItem = permissionsCatalog.find((x) => x.name === p.name)
                return (
                  <div className="admin-item" key={p.name}>
                    <div>
                      <b>{p.name}</b>
                      <span className={`badge ${p.effect === 'GRANT' ? 'badge-green' : 'badge-orange'}`}>
                        {p.effect}
                      </span>
                    </div>
                    <button
                      className="table-delete"
                      disabled={!catalogItem || busy}
                      onClick={() => catalogItem && handleRevokePermission(catalogItem.uuid)}
                    >
                      {t('revoke')}
                    </button>
                  </div>
                )
              })}
              {!authorities.directPermissions?.length && (
                <div className="empty-subtext">No direct permissions granted or denied for this user.</div>
              )}
            </div>

            <div className="admin-assign">
              <select
                value={selectedPermUuid}
                onChange={(e) => setSelectedPermUuid(e.target.value)}
              >
                <option value="">{t('choosePermission')}</option>
                {permissionsCatalog.map((pc) => (
                  <option key={pc.uuid} value={pc.uuid}>
                    {pc.name} ({pc.resource}:{pc.action})
                  </option>
                ))}
              </select>
              <select
                value={permEffect}
                onChange={(e) => setPermEffect(e.target.value)}
                style={{ width: '110px' }}
              >
                <option value="GRANT">GRANT</option>
                <option value="DENY">DENY</option>
              </select>
              <button
                className="button button-primary"
                disabled={!selectedPermUuid || busy}
                onClick={handleGrantPermission}
              >
                {t('grant')}
              </button>
            </div>
          </div>
        )}

        {/* Tab 3: Active User Sessions */}
        {activeTab === 'sessions' && (
          <div className="admin-section">
            <div className="section-header-row">
              <div>
                <h3>{t('userSessionsTitle')}</h3>
                <p className="section-help">{t('activeSessionsSubtitle')}</p>
              </div>
              {sessions.length > 0 && (
                <button
                  className="button button-danger"
                  onClick={handleTerminateAllSessions}
                  disabled={busy}
                >
                  {t('signOutAll')}
                </button>
              )}
            </div>

            <div className="admin-list">
              {sessions.map((s) => (
                <div className="admin-item" key={s.id}>
                  <div>
                    <b>{s.clientName || 'OAuth Client'}</b>
                    <small>
                      {s.userAgent || 'Web browser'} · IP: {s.ipAddress || 'unknown'} · Expires:{' '}
                      {s.expiresAt ? new Date(s.expiresAt).toLocaleString() : 'N/A'}
                    </small>
                  </div>
                  <button
                    className="table-delete"
                    onClick={() => handleTerminateSession(s.id)}
                    disabled={busy}
                  >
                    {t('endSession')}
                  </button>
                </div>
              ))}
              {!sessions.length && (
                <div className="empty-subtext">No active sessions found for this user.</div>
              )}
            </div>
          </div>
        )}

        {/* Tab 4: User Login History */}
        {activeTab === 'history' && (
          <div className="admin-section">
            <h3>{t('userLoginHistoryTitle')}</h3>
            <p className="section-help">{t('loginHistorySubtitle')}</p>

            <div className="admin-list">
              {loginHistory.map((lh, idx) => (
                <div className="admin-item" key={lh.id || idx}>
                  <div>
                    <span className={`event-pill ${lh.succeeded ? 'success' : 'failed'}`}>
                      {lh.succeeded ? t('success') : t('failed')}
                    </span>
                    <b>{lh.type}</b>
                    <small>
                      IP: {lh.ipAddress || 'unknown'} · {lh.userAgent || 'Unknown Device'}
                    </small>
                  </div>
                  <time>{lh.createdAt ? new Date(lh.createdAt).toLocaleString() : ''}</time>
                </div>
              ))}
              {!loginHistory.length && (
                <div className="empty-subtext">No login history recorded for this user.</div>
              )}
            </div>
          </div>
        )}

        {/* Tab 5: Reset Password */}
        {activeTab === 'security' && (
          <form className="admin-section" onSubmit={handleResetPassword}>
            <h3>{t('resetPassword')}</h3>
            <p className="section-help">Set a new password for this user immediately.</p>

            <label className="modal-label">
              {t('temporaryPassword')}
              <input
                type="password"
                required
                minLength={8}
                placeholder="Enter new strong password"
                value={tempPassword}
                onChange={(e) => setTempPassword(e.target.value)}
              />
            </label>

            <div className="modal-foot-inline">
              <button
                type="submit"
                className="button button-primary"
                disabled={!tempPassword || busy}
              >
                {busy ? 'Processing...' : t('resetPassword')}
              </button>
            </div>
          </form>
        )}

        {/* Footer */}
        <div className="modal-foot">
          <button type="button" className="button button-quiet" onClick={onClose}>
            {t('close')}
          </button>
        </div>
      </div>
    </div>
  )
}

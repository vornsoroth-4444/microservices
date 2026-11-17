import React, { useState, useEffect } from 'react'
import {
  X,
  ShieldCheck,
  KeyRound,
  Fingerprint,
  History,
  Lock,
  Unlock,
  UserX,
  UserCheck,
  AlertCircle,
  Trash2,
  Plus,
} from 'lucide-react'
import { api, pageItems, label } from '../api'

export function UserDetailModal({
  user,
  initialTab = 'roles',
  permissions: permissionsCatalog = [],
  roles: rolesCatalog = [],
  onClose,
  onAction,
  t,
}) {
  const [activeTab, setActiveTab] = useState(user?.initialTab || initialTab || 'roles')
  const [detail, setDetail] = useState(user)
  const [authorities, setAuthorities] = useState({
    roles: [],
    directPermissions: [],
    effectivePermissions: [],
  })
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
      api(`/api/users/${uuid}/login-history?size=50`),
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
        body: body ? JSON.stringify(body) : undefined,
      })
      flash(t('changesSaved'))
      await loadData()
      if (onAction) onAction()
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
      reason: 'Assigned from IAM admin console',
    })
    if (ok) setSelectedRoleUuid('')
  }

  const handleRevokeRole = async (roleUuid) => {
    if (window.confirm('Revoke this role from the user?')) {
      await handleAction(`/api/users/${uuid}/roles/${roleUuid}`, 'DELETE')
    }
  }

  const handleGrantPermission = async () => {
    if (!selectedPermUuid) return
    const ok = await handleAction(`/api/users/${uuid}/permissions`, 'POST', {
      permissionUuid: selectedPermUuid,
      effect: permEffect,
      reason: 'Direct assignment from admin console',
    })
    if (ok) setSelectedPermUuid('')
  }

  const handleRevokePermission = async (permUuid) => {
    if (window.confirm('Revoke direct permission?')) {
      await handleAction(`/api/users/${uuid}/permissions/${permUuid}`, 'DELETE')
    }
  }

  const handleTerminateSession = async (sessionId) => {
    await handleAction(`/api/users/${uuid}/sessions/${encodeURIComponent(sessionId)}`, 'DELETE')
  }

  const handleTerminateAllSessions = async () => {
    if (window.confirm('Terminate all active sessions for this user?')) {
      await handleAction(`/api/users/${uuid}/sessions`, 'DELETE')
    }
  }

  const handleResetPassword = async (e) => {
    e.preventDefault()
    if (!tempPassword) return
    const ok = await handleAction(`/api/users/${uuid}/reset-password`, 'POST', {
      newPassword: tempPassword,
    })
    if (ok) setTempPassword('')
  }

  const handleToggleLock = async () => {
    const isLocked = detail.accountNonLocked === false
    await handleAction(`/api/users/${uuid}/${isLocked ? 'unlock' : 'lock'}`)
  }

  const handleToggleEnable = async () => {
    const isEnabled = detail.enabled !== false
    await handleAction(`/api/users/${uuid}/${isEnabled ? 'disable' : 'enable'}`)
  }

  const initials = label(detail)
    .split(' ')
    .map((s) => s[0])
    .join('')
    .slice(0, 2)
    .toUpperCase()

  const isLocked = detail.accountNonLocked === false
  const isEnabled = detail.enabled !== false

  return (
    <div
      className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center p-4 sm:p-6 animate-in fade-in"
      onMouseDown={onClose}
    >
      <div
        className="w-full max-w-3xl bg-white rounded-3xl border border-slate-200 shadow-2xl overflow-hidden flex flex-col max-h-[90vh]"
        onMouseDown={(e) => e.stopPropagation()}
      >
        {/* Header banner */}
        <div className="bg-slate-50 border-b border-slate-200 p-6 flex items-start justify-between">
          <div className="flex items-center gap-4">
            <div className="w-14 h-14 rounded-2xl bg-indigo-600 text-white font-bold text-lg flex items-center justify-center shadow-sm shrink-0">
              {initials}
            </div>
            <div>
              <div className="flex items-center gap-2.5">
                <h2 className="text-lg font-bold text-slate-900">{label(detail)}</h2>
                <span
                  className={`px-2.5 py-1 rounded-full text-xs font-bold ${
                    isEnabled
                      ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                      : 'bg-slate-200 text-slate-600'
                  }`}
                >
                  {isEnabled ? 'Active' : 'Disabled'}
                </span>
                {isLocked && (
                  <span className="px-2.5 py-1 rounded-full text-xs font-bold bg-amber-50 text-amber-700 border border-amber-200">
                    Locked
                  </span>
                )}
              </div>
              <div className="flex items-center gap-2 text-sm text-slate-500 font-mono mt-1">
                <span>@{detail.username}</span>
                <span>·</span>
                <span>{detail.email}</span>
              </div>
            </div>
          </div>

          <button
            type="button"
            onClick={onClose}
            className="p-2 text-slate-400 hover:text-slate-600 rounded-xl"
          >
            <X size={20} />
          </button>
        </div>

        {msg.text && (
          <div
            className={`m-5 p-4 rounded-xl text-sm flex items-center gap-2.5 ${
              msg.isError
                ? 'bg-rose-50 border border-rose-200 text-rose-700'
                : 'bg-emerald-50 border border-emerald-200 text-emerald-700'
            }`}
          >
            <AlertCircle size={17} />
            <span>{msg.text}</span>
          </div>
        )}

        {/* Tab switcher */}
        <div className="flex items-center gap-2 px-6 border-b border-slate-200 bg-white overflow-x-auto">
          {[
            { id: 'roles', label: 'Assigned Roles', icon: ShieldCheck, count: authorities.roles?.length },
            {
              id: 'permissions',
              label: 'Direct Permissions',
              icon: KeyRound,
              count: authorities.directPermissions?.length,
            },
            { id: 'sessions', label: 'Sessions', icon: Fingerprint, count: sessions.length },
            { id: 'history', label: 'Login History', icon: History, count: loginHistory.length },
            { id: 'security', label: 'Account Security', icon: Lock },
          ].map((tab) => {
            const Icon = tab.icon
            const isActive = activeTab === tab.id
            return (
              <button
                key={tab.id}
                type="button"
                onClick={() => setActiveTab(tab.id)}
                className={`flex items-center gap-2.5 py-4 px-3 text-sm font-semibold border-b-2 transition whitespace-nowrap ${
                  isActive
                    ? 'border-indigo-600 text-indigo-600'
                    : 'border-transparent text-slate-500 hover:text-slate-800'
                }`}
              >
                <Icon size={16} />
                <span>{tab.label}</span>
                {tab.count !== undefined && (
                  <span className="text-xs px-2 py-0.5 rounded-full bg-slate-100 font-bold text-slate-600 border border-slate-200">
                    {tab.count}
                  </span>
                )}
              </button>
            )
          })}
        </div>

        {/* Tab Body */}
        <div className="p-6 flex-1 overflow-y-auto space-y-6">
          {/* Tab 1: Roles */}
          {activeTab === 'roles' && (
            <div className="space-y-5">
              <div className="flex items-center gap-3">
                <select
                  value={selectedRoleUuid}
                  onChange={(e) => setSelectedRoleUuid(e.target.value)}
                  className="flex-1 px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                >
                  <option value="">Select a role to assign...</option>
                  {rolesCatalog.map((r) => (
                    <option key={r.uuid || r.name} value={r.uuid || r.name}>
                      {r.name} - {r.description || 'System role'}
                    </option>
                  ))}
                </select>
                <button
                  type="button"
                  onClick={handleAssignRole}
                  disabled={!selectedRoleUuid || busy}
                  className="flex items-center gap-2 px-5 py-2.5 text-sm font-semibold rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white transition disabled:opacity-50"
                >
                  <Plus size={16} />
                  <span>Assign</span>
                </button>
              </div>

              <div className="divide-y divide-slate-100 border border-slate-200 rounded-2xl overflow-hidden">
                {authorities.roles?.length === 0 ? (
                  <div className="p-10 text-center text-slate-400 text-sm">No roles assigned.</div>
                ) : (
                  authorities.roles.map((r, i) => (
                    <div key={i} className="p-4 flex items-center justify-between hover:bg-slate-50">
                      <div>
                        <span className="font-bold text-sm text-slate-900">{r.name}</span>
                        <div className="text-xs text-slate-500 mt-0.5">{r.description || 'RBAC Role'}</div>
                      </div>
                      <button
                        type="button"
                        onClick={() => handleRevokeRole(r.uuid || r.name)}
                        className="p-1.5 text-slate-400 hover:text-rose-600 rounded-lg transition"
                        title="Revoke Role"
                      >
                        <Trash2 size={16} />
                      </button>
                    </div>
                  ))
                )}
              </div>
            </div>
          )}

          {/* Tab 2: Permissions */}
          {activeTab === 'permissions' && (
            <div className="space-y-5">
              <div className="flex items-center gap-3">
                <select
                  value={selectedPermUuid}
                  onChange={(e) => setSelectedPermUuid(e.target.value)}
                  className="flex-1 px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                >
                  <option value="">Select direct permission...</option>
                  {permissionsCatalog.map((p) => (
                    <option key={p.uuid || p.name} value={p.uuid || p.name}>
                      {p.name} ({p.resource}:{p.action})
                    </option>
                  ))}
                </select>
                <select
                  value={permEffect}
                  onChange={(e) => setPermEffect(e.target.value)}
                  className="px-3.5 py-2.5 text-sm rounded-xl border border-slate-200 bg-white"
                >
                  <option value="GRANT">GRANT</option>
                  <option value="DENY">DENY</option>
                </select>
                <button
                  type="button"
                  onClick={handleGrantPermission}
                  disabled={!selectedPermUuid || busy}
                  className="px-5 py-2.5 text-sm font-semibold rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white transition disabled:opacity-50"
                >
                  Add
                </button>
              </div>

              <div className="divide-y divide-slate-100 border border-slate-200 rounded-2xl overflow-hidden">
                {authorities.directPermissions?.length === 0 ? (
                  <div className="p-10 text-center text-slate-400 text-sm">
                    No explicit direct permissions assigned.
                  </div>
                ) : (
                  authorities.directPermissions.map((dp, i) => (
                    <div key={i} className="p-4 flex items-center justify-between hover:bg-slate-50">
                      <div>
                        <div className="flex items-center gap-2.5">
                          <span className="font-mono text-xs font-bold text-slate-900">
                            {dp.permissionName || dp.name}
                          </span>
                          <span
                            className={`px-2 py-0.5 rounded-full text-xs font-bold ${
                              dp.effect === 'DENY'
                                ? 'bg-rose-50 text-rose-700 border border-rose-200'
                                : 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                            }`}
                          >
                            {dp.effect}
                          </span>
                        </div>
                      </div>
                      <button
                        type="button"
                        onClick={() => handleRevokePermission(dp.permissionUuid || dp.uuid)}
                        className="p-1.5 text-slate-400 hover:text-rose-600 rounded-lg transition"
                      >
                        <Trash2 size={16} />
                      </button>
                    </div>
                  ))
                )}
              </div>
            </div>
          )}

          {/* Tab 3: Sessions */}
          {activeTab === 'sessions' && (
            <div className="space-y-5">
              {sessions.length > 0 && (
                <div className="flex justify-end">
                  <button
                    type="button"
                    onClick={handleTerminateAllSessions}
                    className="px-4 py-2 text-xs font-semibold rounded-xl bg-rose-50 text-rose-700 border border-rose-200 hover:bg-rose-100 transition"
                  >
                    Terminate All Sessions
                  </button>
                </div>
              )}

              <div className="divide-y divide-slate-100 border border-slate-200 rounded-2xl overflow-hidden">
                {sessions.length === 0 ? (
                  <div className="p-10 text-center text-slate-400 text-sm">No active sessions.</div>
                ) : (
                  sessions.map((s, idx) => (
                    <div key={idx} className="p-4 flex items-center justify-between hover:bg-slate-50">
                      <div>
                        <div className="font-bold text-sm text-slate-900">
                          {s.clientName || 'Console Session'}
                        </div>
                        <div className="text-xs text-slate-500 font-mono mt-0.5">
                          {s.ipAddress} · {s.userAgent}
                        </div>
                      </div>
                      <button
                        type="button"
                        onClick={() => handleTerminateSession(s.id)}
                        className="p-2 text-slate-400 hover:text-rose-600 rounded-xl"
                      >
                        <Trash2 size={16} />
                      </button>
                    </div>
                  ))
                )}
              </div>
            </div>
          )}

          {/* Tab 4: Login History */}
          {activeTab === 'history' && (
            <div className="divide-y divide-slate-100 border border-slate-200 rounded-2xl overflow-hidden max-h-80 overflow-y-auto">
              {loginHistory.length === 0 ? (
                <div className="p-10 text-center text-slate-400 text-sm">No history recorded.</div>
              ) : (
                loginHistory.map((h, idx) => (
                  <div key={idx} className="p-4 flex items-center justify-between text-sm hover:bg-slate-50">
                    <div>
                      <div className="font-semibold text-slate-900">{h.eventType || 'LOGIN_SUCCESS'}</div>
                      <div className="text-xs text-slate-500 font-mono mt-0.5">
                        {h.ipAddress} · {h.userAgent}
                      </div>
                    </div>
                    <span className="font-mono text-xs text-slate-400">
                      {h.timestamp ? new Date(h.timestamp).toLocaleString() : ''}
                    </span>
                  </div>
                ))
              )}
            </div>
          )}

          {/* Tab 5: Account Security */}
          {activeTab === 'security' && (
            <div className="space-y-5">
              <form onSubmit={handleResetPassword} className="p-5 bg-slate-50 rounded-2xl border border-slate-200 space-y-3">
                <span className="text-sm font-bold text-slate-900">Administrative Password Reset</span>
                <div className="flex gap-3">
                  <input
                    type="password"
                    required
                    value={tempPassword}
                    onChange={(e) => setTempPassword(e.target.value)}
                    placeholder="New password for user..."
                    className="flex-1 px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                  />
                  <button
                    type="submit"
                    disabled={busy || !tempPassword}
                    className="px-5 py-2.5 text-sm font-semibold rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white transition disabled:opacity-50"
                  >
                    Reset
                  </button>
                </div>
              </form>

              <div className="grid grid-cols-2 gap-4 pt-2">
                <button
                  type="button"
                  onClick={handleToggleLock}
                  className={`p-4 rounded-2xl border text-sm font-bold flex items-center justify-center gap-2.5 transition ${
                    isLocked
                      ? 'bg-emerald-50 border-emerald-200 text-emerald-700 hover:bg-emerald-100'
                      : 'bg-amber-50 border-amber-200 text-amber-700 hover:bg-amber-100'
                  }`}
                >
                  {isLocked ? <Unlock size={18} /> : <Lock size={18} />}
                  <span>{isLocked ? 'Unlock Account' : 'Lock Account'}</span>
                </button>

                <button
                  type="button"
                  onClick={handleToggleEnable}
                  className={`p-4 rounded-2xl border text-sm font-bold flex items-center justify-center gap-2.5 transition ${
                    isEnabled
                      ? 'bg-rose-50 border-rose-200 text-rose-700 hover:bg-rose-100'
                      : 'bg-emerald-50 border-emerald-200 text-emerald-700 hover:bg-emerald-100'
                  }`}
                >
                  {isEnabled ? <UserX size={18} /> : <UserCheck size={18} />}
                  <span>{isEnabled ? 'Disable Account' : 'Enable Account'}</span>
                </button>
              </div>
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="p-5 bg-slate-50 border-t border-slate-200 flex justify-end">
          <button
            type="button"
            onClick={onClose}
            className="px-5 py-2.5 text-sm font-semibold rounded-xl bg-slate-200 hover:bg-slate-300 text-slate-800 transition"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  )
}

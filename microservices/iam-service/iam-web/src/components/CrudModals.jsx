import React, { useState } from 'react'
import { X, Check, Copy, AlertCircle } from 'lucide-react'
import { splitList } from '../api'

export function CrudModal({ type: rawType, roles = [], permissions = [], onClose, onAction, t }) {
  const modalType = typeof rawType === 'string' ? rawType : rawType?.type
  const item = typeof rawType === 'object' ? rawType?.item || {} : {}

  const [revealedSecret, setRevealedSecret] = useState(null)
  const [copied, setCopied] = useState(false)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')

  // Form states
  const [userForm, setUserForm] = useState({
    username: item.username || '',
    email: item.email || '',
    givenName: item.givenName || item.name?.split(' ')[0] || '',
    familyName: item.familyName || item.name?.split(' ').slice(1).join(' ') || '',
    phoneNumber: item.phoneNumber || '',
    pictureUrl: item.pictureUrl || '',
    password: '',
    locale: item.locale || 'en',
  })

  const [roleForm, setRoleForm] = useState({
    name: item.name || '',
    description: item.description || '',
    permissionUuids: (item.permissions || []).map((p) => (typeof p === 'string' ? p : p.uuid || p.id)),
  })

  const [permFilter, setPermFilter] = useState('')

  const [permForm, setPermForm] = useState({
    resource: item.resource || '',
    action: item.action || '',
    description: item.description || '',
  })

  const [clientForm, setClientForm] = useState({
    clientId: item.clientId || '',
    clientName: item.clientName || '',
    clientSecret: '',
    authorizationGrantTypes: item.authorizationGrantTypes || item.grantTypes || [
      'authorization_code',
      'refresh_token',
    ],
    redirectUris: Array.isArray(item.redirectUris)
      ? item.redirectUris.join(', ')
      : item.redirectUris || 'http://localhost:8080/login/oauth2/code/iam-client',
    scopes: Array.isArray(item.scopes)
      ? item.scopes.join(', ')
      : item.scopes || 'openid, profile, email, user:read',
  })

  const [assignedRoleUuids, setAssignedRoleUuids] = useState(
    (item.roles || []).map((r) => (typeof r === 'string' ? r : r.uuid || r.name))
  )

  const [newPassword, setNewPassword] = useState('')

  const handleCopy = (text) => {
    navigator.clipboard.writeText(text)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  const handleUserSubmit = async (e) => {
    e.preventDefault()
    setBusy(true)
    setError('')
    try {
      if (modalType === 'edit-user') {
        const ok = await onAction(`/api/users/${item.uuid}`, 'PATCH', {
          givenName: userForm.givenName,
          familyName: userForm.familyName,
          email: userForm.email,
          phoneNumber: userForm.phoneNumber,
          pictureUrl: userForm.pictureUrl,
        })
        if (ok) onClose()
        else setError('Failed to update user profile')
      } else {
        const payload = { ...userForm }
        if (!payload.password) delete payload.password
        const ok = await onAction('/api/users', 'POST', payload)
        if (ok) onClose()
        else setError('Failed to create user')
      }
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  const handleRoleSubmit = async (e) => {
    e.preventDefault()
    setBusy(true)
    setError('')
    try {
      if (modalType === 'edit-role') {
        const ok = await onAction(`/api/roles/${item.uuid}`, 'PATCH', {
          name: roleForm.name,
          description: roleForm.description,
        })
        if (ok) onClose()
        else setError('Failed to update role')
      } else {
        const ok = await onAction('/api/roles', 'POST', roleForm)
        if (ok) onClose()
        else setError('Failed to create role')
      }
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  const handleAssignPermissions = async (e) => {
    e.preventDefault()
    setBusy(true)
    setError('')
    try {
      const ok = await onAction(`/api/roles/${item.uuid}/permissions`, 'POST', {
        permissionUuids: roleForm.permissionUuids,
      })
      if (ok) onClose()
      else setError('Failed to assign permissions')
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  const handleAssignRolesToUser = async (e) => {
    e.preventDefault()
    setBusy(true)
    setError('')
    try {
      const ok = await onAction(`/api/users/${item.uuid}/roles`, 'POST', {
        roleUuids: assignedRoleUuids,
        reason: 'Assigned via admin modal',
      })
      if (ok) onClose()
      else setError('Failed to assign roles')
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  const handlePermSubmit = async (e) => {
    e.preventDefault()
    setBusy(true)
    setError('')
    try {
      const ok = await onAction('/api/permissions', 'POST', permForm)
      if (ok) onClose()
      else setError('Failed to create permission')
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  const handleClientSubmit = async (e) => {
    e.preventDefault()
    setBusy(true)
    setError('')
    try {
      const payload = {
        clientName: clientForm.clientName,
        redirectUris: splitList(clientForm.redirectUris),
        authorizationGrantTypes: Array.isArray(clientForm.authorizationGrantTypes)
          ? clientForm.authorizationGrantTypes
          : splitList(clientForm.authorizationGrantTypes),
        scopes: Array.isArray(clientForm.scopes)
          ? clientForm.scopes
          : splitList(clientForm.scopes),
      }
      if (modalType === 'edit-client') {
        const ok = await onAction(`/api/clients/${item.clientId}`, 'PUT', payload)
        if (ok) onClose()
        else setError('Failed to update OAuth client')
      } else {
        payload.clientId = clientForm.clientId
        const res = await onAction('/api/clients', 'POST', payload)
        if (res && res.clientSecret) {
          setRevealedSecret(res.clientSecret)
        } else {
          onClose()
        }
      }
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  const handleResetPassword = async (e) => {
    e.preventDefault()
    setBusy(true)
    setError('')
    try {
      const ok = await onAction(`/api/users/${item.uuid}/reset-password`, 'POST', {
        newPassword,
      })
      if (ok) onClose()
      else setError('Failed to reset password')
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <div
      className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center p-4 sm:p-6 animate-in fade-in"
      onMouseDown={onClose}
    >
      <div
        className="w-full max-w-xl bg-white rounded-3xl border border-slate-200 p-8 shadow-2xl space-y-6 max-h-[90vh] overflow-y-auto"
        onMouseDown={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between border-b border-slate-100 pb-4">
          <h2 className="text-lg font-bold text-slate-900">
            {modalType === 'user' && t('inviteUser')}
            {modalType === 'edit-user' && `Edit Profile: @${item.username}`}
            {modalType === 'role' && t('createRole')}
            {modalType === 'edit-role' && `Edit Role: ${item.name}`}
            {modalType === 'assign-perm' && `Assign Permissions: ${item.name}`}
            {modalType === 'assign-role' && `Assign Roles: @${item.username}`}
            {modalType === 'permission' && t('createPermission')}
            {modalType === 'client' && t('registerClient')}
            {modalType === 'edit-client' && `Edit Client: ${item.clientId}`}
            {modalType === 'reset-password' && `Reset Password: @${item.username}`}
          </h2>
          <button
            type="button"
            onClick={onClose}
            className="p-1.5 text-slate-400 hover:text-slate-600 rounded-xl"
          >
            <X size={20} />
          </button>
        </div>

        {error && (
          <div className="p-4 rounded-xl bg-rose-50 border border-rose-200 text-rose-700 text-sm flex items-center gap-2.5">
            <AlertCircle size={17} />
            <span>{error}</span>
          </div>
        )}

        {/* 1. User Form (Create / Edit) */}
        {(modalType === 'user' || modalType === 'edit-user') && (
          <form onSubmit={handleUserSubmit} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1.5">
                <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">{t('firstName')}</label>
                <input
                  required
                  value={userForm.givenName}
                  onChange={(e) => setUserForm({ ...userForm, givenName: e.target.value })}
                  placeholder="John"
                  className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                />
              </div>
              <div className="space-y-1.5">
                <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">{t('lastName')}</label>
                <input
                  required
                  value={userForm.familyName}
                  onChange={(e) => setUserForm({ ...userForm, familyName: e.target.value })}
                  placeholder="Doe"
                  className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                />
              </div>
            </div>

            <div className="space-y-1.5">
              <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">{t('username')}</label>
              <input
                required
                disabled={modalType === 'edit-user'}
                value={userForm.username}
                onChange={(e) => setUserForm({ ...userForm, username: e.target.value })}
                placeholder="johndoe"
                className={`w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 font-mono focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 ${
                  modalType === 'edit-user' ? 'bg-slate-100 text-slate-500 cursor-not-allowed' : 'bg-white'
                }`}
              />
            </div>

            <div className="space-y-1.5">
              <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">{t('emailAddress')}</label>
              <input
                type="email"
                required
                value={userForm.email}
                onChange={(e) => setUserForm({ ...userForm, email: e.target.value })}
                placeholder="john@istad.co"
                className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
              />
            </div>

            <div className="space-y-1.5">
              <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">{t('phoneNumber')}</label>
              <input
                type="tel"
                value={userForm.phoneNumber}
                onChange={(e) => setUserForm({ ...userForm, phoneNumber: e.target.value })}
                placeholder="+855 12 345 678"
                className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
              />
            </div>

            {modalType === 'user' && (
              <div className="space-y-1.5">
                <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">Initial Password</label>
                <input
                  type="password"
                  required
                  value={userForm.password}
                  onChange={(e) => setUserForm({ ...userForm, password: e.target.value })}
                  placeholder="••••••••••••"
                  className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                />
                <p className="text-xs text-slate-500 mt-1">Minimum 8 characters, with at least 1 uppercase letter and 1 number.</p>
              </div>
            )}

            <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
              <button
                type="button"
                onClick={onClose}
                className="px-4 py-2.5 text-sm font-semibold rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 transition"
              >
                {t('cancel')}
              </button>
              <button
                type="submit"
                disabled={busy}
                className="px-5 py-2.5 text-sm font-semibold rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white shadow-sm shadow-indigo-200 transition disabled:opacity-50"
              >
                {busy ? 'Saving...' : modalType === 'edit-user' ? 'Update Profile' : t('save')}
              </button>
            </div>
          </form>
        )}

        {/* 2. Role Form */}
        {(modalType === 'role' || modalType === 'edit-role') && (
          <form onSubmit={handleRoleSubmit} className="space-y-4">
            <div className="space-y-1.5">
              <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">Role Name</label>
              <input
                required
                value={roleForm.name}
                onChange={(e) => setRoleForm({ ...roleForm, name: e.target.value })}
                placeholder="ROLE_MANAGER"
                className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white font-mono focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
              />
            </div>

            <div className="space-y-1.5">
              <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">Description</label>
              <textarea
                rows={3}
                value={roleForm.description}
                onChange={(e) => setRoleForm({ ...roleForm, description: e.target.value })}
                placeholder="Describe role responsibilities..."
                className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
              />
            </div>

            <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
              <button
                type="button"
                onClick={onClose}
                className="px-4 py-2.5 text-sm font-semibold rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 transition"
              >
                {t('cancel')}
              </button>
              <button
                type="submit"
                disabled={busy}
                className="px-5 py-2.5 text-sm font-semibold rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white shadow-sm shadow-indigo-200 transition disabled:opacity-50"
              >
                {busy ? 'Saving...' : t('save')}
              </button>
            </div>
          </form>
        )}

        {/* 3. Assign Permissions Modal */}
        {modalType === 'assign-perm' && (
          <form onSubmit={handleAssignPermissions} className="space-y-4">
            <input
              type="text"
              placeholder="Filter permissions..."
              value={permFilter}
              onChange={(e) => setPermFilter(e.target.value)}
              className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
            />

            <div className="max-h-72 overflow-y-auto space-y-1.5 border border-slate-200 rounded-2xl p-3">
              {permissions
                .filter((p) => !permFilter || p.name.toLowerCase().includes(permFilter.toLowerCase()))
                .map((p) => {
                  const pId = p.uuid || p.id
                  const isChecked = roleForm.permissionUuids.includes(pId)
                  return (
                    <label
                      key={pId}
                      className="flex items-center justify-between p-3 rounded-xl hover:bg-slate-50 cursor-pointer text-sm"
                    >
                      <div>
                        <span className="font-mono font-bold text-slate-900">{p.name}</span>
                        <div className="text-xs text-slate-500 mt-0.5">{p.description}</div>
                      </div>
                      <input
                        type="checkbox"
                        checked={isChecked}
                        onChange={(e) => {
                          const next = e.target.checked
                            ? [...roleForm.permissionUuids, pId]
                            : roleForm.permissionUuids.filter((id) => id !== pId)
                          setRoleForm({ ...roleForm, permissionUuids: next })
                        }}
                        className="w-5 h-5 text-indigo-600 rounded-md border-slate-300 focus:ring-indigo-500"
                      />
                    </label>
                  )
                })}
            </div>

            <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
              <button
                type="button"
                onClick={onClose}
                className="px-4 py-2.5 text-sm font-semibold rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 transition"
              >
                {t('cancel')}
              </button>
              <button
                type="submit"
                disabled={busy}
                className="px-5 py-2.5 text-sm font-semibold rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white shadow-sm shadow-indigo-200 transition disabled:opacity-50"
              >
                {busy ? 'Saving...' : 'Apply Permissions'}
              </button>
            </div>
          </form>
        )}

        {/* 4. Assign Roles to User Modal */}
        {modalType === 'assign-role' && (
          <form onSubmit={handleAssignRolesToUser} className="space-y-4">
            <div className="max-h-72 overflow-y-auto space-y-1.5 border border-slate-200 rounded-2xl p-3">
              {roles.map((r) => {
                const rId = r.uuid || r.name
                const isChecked = assignedRoleUuids.includes(rId) || assignedRoleUuids.includes(r.name)
                return (
                  <label
                    key={rId}
                    className="flex items-center justify-between p-3 rounded-xl hover:bg-slate-50 cursor-pointer text-sm"
                  >
                    <div>
                      <span className="font-bold text-slate-900">{r.name}</span>
                      <div className="text-xs text-slate-500 mt-0.5">{r.description}</div>
                    </div>
                    <input
                      type="checkbox"
                      checked={isChecked}
                      onChange={(e) => {
                        const next = e.target.checked
                          ? [...assignedRoleUuids, rId]
                          : assignedRoleUuids.filter((id) => id !== rId && id !== r.name)
                        setAssignedRoleUuids(next)
                      }}
                      className="w-5 h-5 text-indigo-600 rounded-md border-slate-300 focus:ring-indigo-500"
                    />
                  </label>
                )
              })}
            </div>

            <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
              <button
                type="button"
                onClick={onClose}
                className="px-4 py-2.5 text-sm font-semibold rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 transition"
              >
                {t('cancel')}
              </button>
              <button
                type="submit"
                disabled={busy}
                className="px-5 py-2.5 text-sm font-semibold rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white shadow-sm shadow-indigo-200 transition disabled:opacity-50"
              >
                {busy ? 'Saving...' : 'Apply Roles'}
              </button>
            </div>
          </form>
        )}

        {/* 5. Permission Create Form */}
        {modalType === 'permission' && (
          <form onSubmit={handlePermSubmit} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1.5">
                <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">Resource</label>
                <input
                  required
                  value={permForm.resource}
                  onChange={(e) => setPermForm({ ...permForm, resource: e.target.value })}
                  placeholder="user"
                  className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white font-mono focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                />
              </div>
              <div className="space-y-1.5">
                <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">Action</label>
                <input
                  required
                  value={permForm.action}
                  onChange={(e) => setPermForm({ ...permForm, action: e.target.value })}
                  placeholder="export"
                  className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white font-mono focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                />
              </div>
            </div>

            <div className="space-y-1.5">
              <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">Description</label>
              <textarea
                rows={3}
                value={permForm.description}
                onChange={(e) => setPermForm({ ...permForm, description: e.target.value })}
                placeholder="Permission to export users report"
                className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
              />
            </div>

            <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
              <button
                type="button"
                onClick={onClose}
                className="px-4 py-2.5 text-sm font-semibold rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 transition"
              >
                {t('cancel')}
              </button>
              <button
                type="submit"
                disabled={busy}
                className="px-5 py-2.5 text-sm font-semibold rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white shadow-sm shadow-indigo-200 transition disabled:opacity-50"
              >
                {busy ? 'Saving...' : t('save')}
              </button>
            </div>
          </form>
        )}

        {/* 6. OAuth Client Register & Edit Form */}
        {(modalType === 'client' || modalType === 'edit-client') && (
          <div>
            {revealedSecret ? (
              <div className="p-6 bg-emerald-50 rounded-2xl border border-emerald-200 space-y-4">
                <div className="text-base font-bold text-emerald-900">OAuth Client Registered!</div>
                <p className="text-sm text-emerald-700">
                  Please copy the client secret now. It will not be shown again.
                </p>
                <div className="p-4 bg-white rounded-xl border border-emerald-200 flex items-center justify-between font-mono text-sm text-slate-800">
                  <span>{revealedSecret}</span>
                  <button
                    type="button"
                    onClick={() => handleCopy(revealedSecret)}
                    className="flex items-center gap-1.5 text-xs font-bold text-emerald-600 hover:text-emerald-800 p-1"
                  >
                    {copied ? <Check size={16} /> : <Copy size={16} />}
                    <span>{copied ? 'Copied' : 'Copy'}</span>
                  </button>
                </div>
                <button
                  type="button"
                  onClick={onClose}
                  className="w-full py-2.5 text-sm font-semibold bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl shadow-sm transition"
                >
                  Done
                </button>
              </div>
            ) : (
              <form onSubmit={handleClientSubmit} className="space-y-4">
                <div className="space-y-1.5">
                  <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">Client ID</label>
                  <input
                    required
                    disabled={modalType === 'edit-client'}
                    value={clientForm.clientId}
                    onChange={(e) => setClientForm({ ...clientForm, clientId: e.target.value })}
                    placeholder="my-portal-client"
                    className={`w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 font-mono focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 ${
                      modalType === 'edit-client' ? 'bg-slate-100 text-slate-500 cursor-not-allowed' : 'bg-white'
                    }`}
                  />
                </div>

                <div className="space-y-1.5">
                  <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">Client Name</label>
                  <input
                    required
                    value={clientForm.clientName}
                    onChange={(e) => setClientForm({ ...clientForm, clientName: e.target.value })}
                    placeholder="My Customer Portal"
                    className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                  />
                </div>

                <div className="space-y-1.5">
                  <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">Redirect URIs (comma-separated)</label>
                  <input
                    required
                    value={clientForm.redirectUris}
                    onChange={(e) => setClientForm({ ...clientForm, redirectUris: e.target.value })}
                    placeholder="http://localhost:3000/callback, http://localhost:8080/login/oauth2/code/iam-client"
                    className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white font-mono focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                  />
                </div>

                <div className="space-y-1.5">
                  <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">Scopes (comma-separated)</label>
                  <input
                    value={typeof clientForm.scopes === 'string' ? clientForm.scopes : clientForm.scopes?.join(', ')}
                    onChange={(e) => setClientForm({ ...clientForm, scopes: e.target.value })}
                    placeholder="openid, profile, email, user:read"
                    className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white font-mono focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                  />
                </div>

                <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
                  <button
                    type="button"
                    onClick={onClose}
                    className="px-4 py-2.5 text-sm font-semibold rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 transition"
                  >
                    {t('cancel')}
                  </button>
                  <button
                    type="submit"
                    disabled={busy}
                    className="px-5 py-2.5 text-sm font-semibold rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white shadow-sm shadow-indigo-200 transition disabled:opacity-50"
                  >
                    {busy ? 'Saving...' : modalType === 'edit-client' ? 'Update Client' : 'Register Client'}
                  </button>
                </div>
              </form>
            )}
          </div>
        )}

        {/* 7. Reset Password Form */}
        {modalType === 'reset-password' && (
          <form onSubmit={handleResetPassword} className="space-y-4">
            <div className="space-y-1.5">
              <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">New Password</label>
              <input
                type="password"
                required
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                placeholder="NewTempPassword123!"
                className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
              />
            </div>

            <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
              <button
                type="button"
                onClick={onClose}
                className="px-4 py-2.5 text-sm font-semibold rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 transition"
              >
                {t('cancel')}
              </button>
              <button
                type="submit"
                disabled={busy}
                className="px-5 py-2.5 text-sm font-semibold rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white shadow-sm shadow-indigo-200 transition disabled:opacity-50"
              >
                {busy ? 'Resetting...' : 'Reset Password'}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  )
}

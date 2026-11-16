import React, { useState } from 'react'
import { X, Check, Copy, AlertCircle } from 'lucide-react'
import { splitList } from '../api'

const STANDARD_GRANTS = [
  'authorization_code',
  'refresh_token',
  'client_credentials',
  'urn:ietf:params:oauth:grant-type:device_code'
]

const STANDARD_SCOPES = [
  'openid',
  'profile',
  'email',
  'phone',
  'address',
  'user:read',
  'user:create',
  'user:update',
  'user:delete',
  'user:assign',
  'role:read',
  'role:create',
  'role:update',
  'role:delete',
  'permission:read',
  'permission:create'
]

export function CrudModal({ modal, permissions = [], onClose, onSave, t }) {
  const type = typeof modal === 'string' ? modal : modal.type
  const initial = modal.initial || {}
  const edit = Boolean(modal.id)

  // Secret reveal state for registered OAuth client
  const [revealedSecret, setRevealedSecret] = useState(null)
  const [copied, setCopied] = useState(false)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')

  // Form states based on type
  const [userForm, setUserForm] = useState({
    username: initial.username || '',
    email: initial.email || '',
    givenName: initial.givenName || initial.name?.split(' ')[0] || '',
    familyName: initial.familyName || initial.name?.split(' ').slice(1).join(' ') || '',
    phoneNumber: initial.phoneNumber || '',
    pictureUrl: initial.pictureUrl || '',
    password: '',
    locale: initial.locale || 'en'
  })

  const [roleForm, setRoleForm] = useState({
    name: initial.name || '',
    description: initial.description || '',
    permissionUuids: (initial.permissions || []).map((p) => p.uuid)
  })

  const [permFilter, setPermFilter] = useState('')

  const [permForm, setPermForm] = useState({
    resource: initial.resource || (initial.name ? initial.name.split(':')[0] : ''),
    action: initial.action || (initial.name ? initial.name.split(':')[1] : ''),
    description: initial.description || ''
  })

  const [clientForm, setClientForm] = useState({
    clientName: initial.clientName || '',
    redirectUris: (initial.redirectUris || ['http://localhost:8080/login/oauth2/code/iam-client']).join('\n'),
    grantTypes: initial.grantTypes || ['authorization_code', 'refresh_token'],
    scopes: initial.scopes || ['openid', 'profile', 'email']
  })

  const handleCopySecret = () => {
    if (revealedSecret?.clientSecret) {
      navigator.clipboard?.writeText(revealedSecret.clientSecret)
      setCopied(true)
      setTimeout(() => setCopied(false), 2500)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setBusy(true)

    try {
      let body
      if (type === 'user') {
        body = { ...userForm }
        if (!body.password && !edit) {
          setError('Password is required for new users')
          setBusy(false)
          return
        }
      } else if (type === 'role') {
        body = { ...roleForm }
      } else if (type === 'permission') {
        body = { ...permForm }
      } else if (type === 'client') {
        body = {
          clientName: clientForm.clientName,
          redirectUris: splitList(clientForm.redirectUris),
          grantTypes: clientForm.grantTypes,
          scopes: clientForm.scopes
        }
      }

      const result = await onSave(type, body, modal.id)
      setBusy(false)

      if (result === false) return

      if (type === 'client' && result?.clientSecret) {
        setRevealedSecret(result)
        return
      }

      onClose()
    } catch (err) {
      setError(err.message || 'Operation failed')
      setBusy(false)
    }
  }

  // If secret is revealed after client creation
  if (revealedSecret) {
    return (
      <div className="modal-backdrop">
        <div className="modal">
          <div className="modal-head">
            <div>
              <h2>{t('clientRegistered')}</h2>
              <p>{t('copySecretWarning')}</p>
            </div>
          </div>

          <label className="modal-label">
            {t('thClientId')}
            <input readOnly value={revealedSecret.clientId || ''} />
          </label>

          <label className="modal-label">
            Client Secret
            <div className="token-input">
              <input readOnly value={revealedSecret.clientSecret || ''} />
              <button type="button" className="button button-quiet" onClick={handleCopySecret}>
                {copied ? <Check size={14} /> : <Copy size={14} />} {copied ? 'Copied!' : 'Copy'}
              </button>
            </div>
          </label>

          <div className="modal-foot">
            <button type="button" className="button button-primary" onClick={onClose}>
              {t('copySecret')}
            </button>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="modal-backdrop" onMouseDown={onClose}>
      <form className="modal" onSubmit={handleSubmit} onMouseDown={(e) => e.stopPropagation()}>
        {/* Head */}
        <div className="modal-head">
          <div>
            <h2>
              {type === 'user' && (edit ? t('editUser') : t('createUser'))}
              {type === 'role' && (edit ? t('editRole') : t('createRole'))}
              {type === 'permission' && (edit ? t('editPermission') : t('createPermission'))}
              {type === 'client' && (edit ? t('editClient') : t('registerClient'))}
            </h2>
            <p>
              {type === 'client'
                ? t('clientsSubtitle')
                : edit
                ? 'Update the selected resource properties.'
                : 'Fill in the information below to add to your IAM workspace.'}
            </p>
          </div>
          <button type="button" className="modal-close" onClick={onClose}>
            <X size={19} />
          </button>
        </div>

        {error && (
          <div className="modal-alert alert-danger">
            <AlertCircle size={15} />
            <span>{error}</span>
          </div>
        )}

        {/* 1. User Form */}
        {type === 'user' && (
          <>
            <div className="form-grid">
              <label>
                {t('firstName')}
                <input
                  required
                  value={userForm.givenName}
                  onChange={(e) => setUserForm({ ...userForm, givenName: e.target.value })}
                />
              </label>
              <label>
                {t('lastName')}
                <input
                  required
                  value={userForm.familyName}
                  onChange={(e) => setUserForm({ ...userForm, familyName: e.target.value })}
                />
              </label>
              <label>
                {t('username')}
                <input
                  required
                  value={userForm.username}
                  onChange={(e) => setUserForm({ ...userForm, username: e.target.value })}
                />
              </label>
              <label>
                {t('emailAddress')}
                <input
                  type="email"
                  required
                  value={userForm.email}
                  onChange={(e) => setUserForm({ ...userForm, email: e.target.value })}
                />
              </label>
              <label>
                {t('phoneNumber')}
                <input
                  value={userForm.phoneNumber}
                  onChange={(e) => setUserForm({ ...userForm, phoneNumber: e.target.value })}
                />
              </label>
              <label>
                Language
                <select
                  value={userForm.locale}
                  onChange={(e) => setUserForm({ ...userForm, locale: e.target.value })}
                >
                  <option value="en">English (en)</option>
                  <option value="km">ខ្មែរ / Khmer (km)</option>
                </select>
              </label>
            </div>

            {!edit && (
              <label className="modal-label">
                Initial password
                <input
                  type="password"
                  required
                  minLength={8}
                  placeholder="At least 8 characters"
                  value={userForm.password}
                  onChange={(e) => setUserForm({ ...userForm, password: e.target.value })}
                />
              </label>
            )}

            <label className="modal-label">
              {t('pictureUrl')}
              <input
                type="url"
                placeholder="https://example.com/avatar.jpg"
                value={userForm.pictureUrl}
                onChange={(e) => setUserForm({ ...userForm, pictureUrl: e.target.value })}
              />
            </label>
          </>
        )}

        {/* 2. Role Form with Permission Picker */}
        {type === 'role' && (
          <>
            <label className="modal-label">
              {t('thName')}
              <input
                required
                placeholder="e.g. FINANCE_MANAGER"
                value={roleForm.name}
                onChange={(e) => setRoleForm({ ...roleForm, name: e.target.value })}
              />
            </label>
            <label className="modal-label">
              {t('thDescription')}
              <textarea
                placeholder="Describe access privileges granted by this role"
                value={roleForm.description}
                onChange={(e) => setRoleForm({ ...roleForm, description: e.target.value })}
              />
            </label>

            <div className="permission-picker-wrapper">
              <div className="permission-picker-header">
                <b>Permissions ({roleForm.permissionUuids.length} selected)</b>
                <input
                  type="text"
                  placeholder="Filter permissions..."
                  value={permFilter}
                  onChange={(e) => setPermFilter(e.target.value)}
                  className="filter-input-small"
                />
              </div>

              <div className="permission-picker">
                {permissions
                  .filter((p) => p.name.toLowerCase().includes(permFilter.toLowerCase()))
                  .map((p) => {
                    const isChecked = roleForm.permissionUuids.includes(p.uuid)
                    return (
                      <label key={p.uuid} className={isChecked ? 'selected' : ''}>
                        <input
                          type="checkbox"
                          checked={isChecked}
                          onChange={(e) => {
                            const updated = e.target.checked
                              ? [...roleForm.permissionUuids, p.uuid]
                              : roleForm.permissionUuids.filter((id) => id !== p.uuid)
                            setRoleForm({ ...roleForm, permissionUuids: updated })
                          }}
                        />
                        <span>
                          <b>{p.name}</b>
                          <small>{p.description}</small>
                        </span>
                      </label>
                    )
                  })}
              </div>
            </div>
          </>
        )}

        {/* 3. Permission Form */}
        {type === 'permission' && (
          <>
            <div className="form-grid">
              <label>
                {t('thResource')}
                <input
                  required
                  placeholder="e.g. user, invoice, report"
                  value={permForm.resource}
                  onChange={(e) => setPermForm({ ...permForm, resource: e.target.value })}
                />
              </label>
              <label>
                {t('thAction')}
                <input
                  required
                  placeholder="e.g. read, create, approve"
                  value={permForm.action}
                  onChange={(e) => setPermForm({ ...permForm, action: e.target.value })}
                />
              </label>
            </div>
            <label className="modal-label">
              {t('thDescription')}
              <textarea
                placeholder="Detailed description of access authority"
                value={permForm.description}
                onChange={(e) => setPermForm({ ...permForm, description: e.target.value })}
              />
            </label>
          </>
        )}

        {/* 4. Client Form */}
        {type === 'client' && (
          <>
            <label className="modal-label">
              {t('thClientName')}
              <input
                required
                placeholder="e.g. Customer Portal App"
                value={clientForm.clientName}
                onChange={(e) => setClientForm({ ...clientForm, clientName: e.target.value })}
              />
            </label>

            <label className="modal-label">
              {t('thRedirectUris')} (One HTTPS or localhost URI per line)
              <textarea
                required
                rows={3}
                placeholder="http://localhost:8080/login/oauth2/code/iam-client&#10;https://oauth.pstmn.io/v1/callback"
                value={clientForm.redirectUris}
                onChange={(e) => setClientForm({ ...clientForm, redirectUris: e.target.value })}
              />
            </label>

            <div className="tag-picker-group">
              <label className="modal-label">{t('thGrantTypes')}</label>
              <div className="chip-picker">
                {STANDARD_GRANTS.map((g) => {
                  const has = clientForm.grantTypes.includes(g)
                  return (
                    <button
                      type="button"
                      key={g}
                      className={`chip ${has ? 'active' : ''}`}
                      onClick={() => {
                        const updated = has
                          ? clientForm.grantTypes.filter((x) => x !== g)
                          : [...clientForm.grantTypes, g]
                        setClientForm({ ...clientForm, grantTypes: updated })
                      }}
                    >
                      {g}
                    </button>
                  )
                })}
              </div>
            </div>

            <div className="tag-picker-group">
              <label className="modal-label">{t('thScopes')}</label>
              <div className="chip-picker">
                {STANDARD_SCOPES.map((s) => {
                  const has = clientForm.scopes.includes(s)
                  return (
                    <button
                      type="button"
                      key={s}
                      className={`chip ${has ? 'active' : ''}`}
                      onClick={() => {
                        const updated = has
                          ? clientForm.scopes.filter((x) => x !== s)
                          : [...clientForm.scopes, s]
                        setClientForm({ ...clientForm, scopes: updated })
                      }}
                    >
                      {s}
                    </button>
                  )
                })}
              </div>
            </div>
          </>
        )}

        {/* Footer */}
        <div className="modal-foot">
          <button type="button" className="button button-quiet" onClick={onClose}>
            {t('cancel')}
          </button>
          <button type="submit" className="button button-primary" disabled={busy}>
            {busy ? 'Saving...' : edit ? t('save') : t('create')}
          </button>
        </div>
      </form>
    </div>
  )
}

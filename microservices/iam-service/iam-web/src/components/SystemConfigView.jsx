import React, { useState, useEffect } from 'react'
import { ShieldCheck, KeyRound, AlertCircle, Save } from 'lucide-react'
import { api } from '../api'

const DEFAULT_CONFIGS = {
  'password.minLength': '12',
  'password.requireUppercase': 'true',
  'password.requireNumber': 'true',
  'password.requireSymbol': 'false',
  'token.accessMinutes': '60',
  'token.refreshDays': '30',
  'social.enabled': 'true'
}

export function SystemConfigView({ act, flash, t }) {
  const [values, setValues] = useState(DEFAULT_CONFIGS)
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  const loadConfig = async () => {
    try {
      const data = await api('/api/system-config')
      setValues((prev) => ({ ...prev, ...data }))
    } catch (e) {
      setError(e.message)
    }
  }

  useEffect(() => {
    loadConfig()
  }, [])

  const updateKey = (key, val) => {
    setValues((prev) => ({ ...prev, [key]: String(val) }))
  }

  const handleSavePasswordPolicy = async () => {
    setBusy(true)
    const payload = Object.fromEntries(
      Object.entries(values).filter(([k]) => k.startsWith('password.'))
    )
    const ok = await act('/api/system-config', 'PATCH', payload)
    if (ok) flash(t('changesSaved'))
    setBusy(false)
  }

  const handleSaveAuthPolicy = async () => {
    setBusy(true)
    const payload = Object.fromEntries(
      Object.entries(values).filter(([k]) => !k.startsWith('password.'))
    )
    const ok = await act('/api/system-config', 'PATCH', payload)
    if (ok) flash(t('changesSaved'))
    setBusy(false)
  }

  return (
    <div className="settings-grid">
      {error && (
        <div className="modal-alert alert-danger" style={{ gridColumn: '1 / -1' }}>
          <AlertCircle size={16} />
          <span>{error}</span>
        </div>
      )}

      {/* Password Policy Card */}
      <section className="panel settings-panel">
        <div className="panel-heading">
          <div>
            <h2>{t('passwordPolicyTitle')}</h2>
            <p>{t('passwordPolicySub')}</p>
          </div>
          <ShieldCheck size={20} className="settings-icon" />
        </div>

        <label className="setting-field">
          <span>{t('minLength')}</span>
          <input
            type="number"
            min="8"
            max="128"
            value={values['password.minLength']}
            onChange={(e) => updateKey('password.minLength', e.target.value)}
          />
        </label>

        {[
          ['password.requireUppercase', t('requireUppercase')],
          ['password.requireNumber', t('requireNumber')],
          ['password.requireSymbol', t('requireSymbol')]
        ].map(([key, labelTitle]) => (
          <label className="setting-row" key={key}>
            <span>
              <b>{labelTitle}</b>
              <small>Validated during registration and password change</small>
            </span>
            <input
              type="checkbox"
              checked={values[key] === 'true'}
              onChange={(e) => updateKey(key, e.target.checked)}
            />
          </label>
        ))}

        <button
          className="button button-primary save-settings"
          onClick={handleSavePasswordPolicy}
          disabled={busy}
        >
          <Save size={14} /> {t('savePasswordPolicy')}
        </button>
      </section>

      {/* Authentication and Token Lifetimes Card */}
      <section className="panel settings-panel">
        <div className="panel-heading">
          <div>
            <h2>{t('authAndTokensTitle')}</h2>
            <p>{t('authAndTokensSub')}</p>
          </div>
          <KeyRound size={20} className="settings-icon" />
        </div>

        <label className="setting-row">
          <span>
            <b>{t('allowSocial')}</b>
            <small>Enable federated login buttons on login screen</small>
          </span>
          <input
            type="checkbox"
            checked={values['social.enabled'] === 'true'}
            onChange={(e) => updateKey('social.enabled', e.target.checked)}
          />
        </label>

        <label className="setting-field">
          <span>{t('accessTokenLifetime')}</span>
          <select
            value={values['token.accessMinutes']}
            onChange={(e) => updateKey('token.accessMinutes', e.target.value)}
          >
            <option value="15">15 {t('minutes')}</option>
            <option value="30">30 {t('minutes')}</option>
            <option value="60">1 {t('hours')} (60 min)</option>
            <option value="120">2 {t('hours')} (120 min)</option>
          </select>
        </label>

        <label className="setting-field">
          <span>{t('refreshTokenLifetime')}</span>
          <select
            value={values['token.refreshDays']}
            onChange={(e) => updateKey('token.refreshDays', e.target.value)}
          >
            <option value="1">1 {t('days')}</option>
            <option value="7">7 {t('days')}</option>
            <option value="30">30 {t('days')}</option>
            <option value="90">90 {t('days')}</option>
          </select>
        </label>

        <p className="policy-note">
          Token lifetime parameters are applied dynamically to newly registered OAuth clients.
        </p>

        <button
          className="button button-primary save-settings"
          onClick={handleSaveAuthPolicy}
          disabled={busy}
        >
          <Save size={14} /> {t('saveAuthPolicy')}
        </button>
      </section>
    </div>
  )
}

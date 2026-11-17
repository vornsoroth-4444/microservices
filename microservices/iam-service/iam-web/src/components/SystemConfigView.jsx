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
  'social.enabled': 'true',
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
    <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
      {error && (
        <div className="md:col-span-2 p-4 rounded-xl bg-rose-50 border border-rose-200 text-rose-700 text-sm flex items-center gap-2.5">
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      {/* Password Policy Card */}
      <section className="bg-slate-50/70 rounded-2xl border border-slate-200/90 p-7 space-y-6 flex flex-col justify-between shadow-xs">
        <div className="space-y-5">
          <div className="flex items-center justify-between border-b border-slate-200/80 pb-4">
            <div>
              <h2 className="text-base font-bold text-slate-900">{t('passwordPolicyTitle')}</h2>
              <p className="text-sm text-slate-500 mt-1">{t('passwordPolicySub')}</p>
            </div>
            <div className="w-10 h-10 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center border border-indigo-100 shrink-0">
              <ShieldCheck size={20} />
            </div>
          </div>

          <div className="space-y-1.5">
            <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">
              {t('minLength')}
            </label>
            <input
              type="number"
              min="8"
              max="128"
              value={values['password.minLength']}
              onChange={(e) => updateKey('password.minLength', e.target.value)}
              className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
            />
          </div>

          <div className="space-y-2.5 pt-1">
            {[
              ['password.requireUppercase', t('requireUppercase')],
              ['password.requireNumber', t('requireNumber')],
              ['password.requireSymbol', t('requireSymbol')],
            ].map(([key, labelTitle]) => (
              <label
                key={key}
                className="flex items-center justify-between p-4 rounded-xl bg-white border border-slate-200/90 cursor-pointer hover:bg-slate-50 transition"
              >
                <div>
                  <div className="text-sm font-bold text-slate-900">{labelTitle}</div>
                  <div className="text-xs text-slate-500 mt-0.5">
                    Validated during registration and password change
                  </div>
                </div>
                <input
                  type="checkbox"
                  checked={values[key] === 'true'}
                  onChange={(e) => updateKey(key, e.target.checked)}
                  className="w-5 h-5 text-indigo-600 rounded-md border-slate-300 focus:ring-indigo-500"
                />
              </label>
            ))}
          </div>
        </div>

        <button
          type="button"
          onClick={handleSavePasswordPolicy}
          disabled={busy}
          className="w-full mt-6 flex items-center justify-center gap-2 py-3 text-sm font-semibold rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white shadow-sm shadow-indigo-200 transition disabled:opacity-50"
        >
          <Save size={16} />
          <span>{t('savePasswordPolicy')}</span>
        </button>
      </section>

      {/* Authentication & Token Lifetimes Card */}
      <section className="bg-slate-50/70 rounded-2xl border border-slate-200/90 p-7 space-y-6 flex flex-col justify-between shadow-xs">
        <div className="space-y-5">
          <div className="flex items-center justify-between border-b border-slate-200/80 pb-4">
            <div>
              <h2 className="text-base font-bold text-slate-900">{t('authAndTokensTitle')}</h2>
              <p className="text-sm text-slate-500 mt-1">{t('authAndTokensSub')}</p>
            </div>
            <div className="w-10 h-10 rounded-xl bg-purple-50 text-purple-600 flex items-center justify-center border border-purple-100 shrink-0">
              <KeyRound size={20} />
            </div>
          </div>

          <label className="flex items-center justify-between p-4 rounded-xl bg-white border border-slate-200/90 cursor-pointer hover:bg-slate-50 transition">
            <div>
              <div className="text-sm font-bold text-slate-900">{t('allowSocial')}</div>
              <div className="text-xs text-slate-500 mt-0.5">
                Enable federated login buttons on login screen
              </div>
            </div>
            <input
              type="checkbox"
              checked={values['social.enabled'] === 'true'}
              onChange={(e) => updateKey('social.enabled', e.target.checked)}
              className="w-5 h-5 text-indigo-600 rounded-md border-slate-300 focus:ring-indigo-500"
            />
          </label>

          <div className="space-y-1.5">
            <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">
              {t('accessTokenLifetime')}
            </label>
            <select
              value={values['token.accessMinutes']}
              onChange={(e) => updateKey('token.accessMinutes', e.target.value)}
              className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
            >
              <option value="15">15 {t('minutes')}</option>
              <option value="30">30 {t('minutes')}</option>
              <option value="60">1 {t('hours')} (60 min)</option>
              <option value="120">2 {t('hours')} (120 min)</option>
            </select>
          </div>

          <div className="space-y-1.5">
            <label className="text-xs font-bold uppercase tracking-wider text-slate-600 block">
              {t('refreshTokenLifetime')}
            </label>
            <select
              value={values['token.refreshDays']}
              onChange={(e) => updateKey('token.refreshDays', e.target.value)}
              className="w-full px-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
            >
              <option value="1">1 {t('days')}</option>
              <option value="7">7 {t('days')}</option>
              <option value="30">30 {t('days')}</option>
              <option value="90">90 {t('days')}</option>
            </select>
          </div>

          <p className="text-xs text-slate-500 italic">
            Token lifetime parameters are applied dynamically to newly registered OAuth clients.
          </p>
        </div>

        <button
          type="button"
          onClick={handleSaveAuthPolicy}
          disabled={busy}
          className="w-full mt-6 flex items-center justify-center gap-2 py-3 text-sm font-semibold rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white shadow-sm shadow-indigo-200 transition disabled:opacity-50"
        >
          <Save size={16} />
          <span>{t('saveAuthPolicy')}</span>
        </button>
      </section>
    </div>
  )
}

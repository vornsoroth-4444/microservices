import React, { useState } from 'react'
import {
  AlertCircle,
  ArrowRight,
  Eye,
  EyeOff,
  Globe2,
  Lock,
  Send,
  User,
  X,
} from 'lucide-react'

export function LoginView({ onLogin, language, onToggleLanguage, t }) {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  // Telegram Login Modal state
  const [showTelegramModal, setShowTelegramModal] = useState(false)
  const [tgUsername, setTgUsername] = useState('sophea_tg')
  const [tgFirstName, setTgFirstName] = useState('Sophea')
  const [tgLastName, setTgLastName] = useState('Vann')
  const [tgId, setTgId] = useState('872361829')

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: username.trim(), password }),
      })

      if (!response.ok) {
        let msg = t('login.failed') || 'Invalid username or password'
        try {
          const err = await response.json()
          msg = err.detail || err.message || msg
        } catch {}
        throw new Error(msg)
      }

      const data = await response.json()
      onLogin(data.accessToken, data.user)
    } catch (err) {
      setError(err.message || 'Login failed')
    } finally {
      setLoading(false)
    }
  }

  const handleTelegramSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    setShowTelegramModal(false)

    try {
      const authDate = Math.floor(Date.now() / 1000)

      const response = await fetch(
        `/login/oauth2/code/telegram?id=${tgId}&first_name=${encodeURIComponent(
          tgFirstName
        )}&last_name=${encodeURIComponent(tgLastName)}&username=${encodeURIComponent(
          tgUsername
        )}&auth_date=${authDate}&hash=simulated_hash_development_mode`,
        {
          headers: { Accept: 'application/json' },
        }
      )

      if (response.redirected) {
        window.location.href = response.url
        return
      }

      if (response.ok) {
        try {
          const data = await response.json()
          if (data && data.accessToken) {
            onLogin(data.accessToken, data.user)
            return
          }
        } catch {}
        window.location.reload()
      } else {
        throw new Error('Telegram authentication failed')
      }
    } catch (err) {
      setError(err.message || 'Telegram login failed')
    } finally {
      setLoading(false)
    }
  }

  const selectTelegramProfile = (id, user, first, last) => {
    setTgId(id)
    setTgUsername(user)
    setTgFirstName(first)
    setTgLastName(last)
  }

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col justify-center items-center p-6">
      {/* Login Card */}
      <div className="w-full max-w-lg bg-white rounded-3xl border border-slate-200/90 p-10 shadow-md space-y-7">
        {/* Brand header */}
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3.5">
            <img
              src={process.env.PUBLIC_URL + '/istad-logo.png'}
              alt="ISTAD"
              className="w-12 h-12 rounded-2xl object-contain shadow-sm border border-slate-100 bg-white"
              onError={(e) => {
                e.currentTarget.src = 'https://www.istad.co/icon.png?ff407d7ec1c2072a'
              }}
            />
            <div>
              <div className="text-xl font-extrabold text-slate-900 leading-tight">
                ISTAD
              </div>
              <div className="text-xs text-slate-500 font-medium">Enterprise IAM Console</div>
            </div>
          </div>

          <button
            type="button"
            onClick={onToggleLanguage}
            title={language === 'en' ? 'ប្តូរទៅភាសាខ្មែរ' : 'Switch to English'}
            className="flex items-center gap-2 px-3 py-1.5 text-xs font-bold rounded-xl border border-slate-200 hover:bg-slate-50 text-slate-700 shadow-xs transition"
          >
            <Globe2 size={15} className="text-slate-500" />
            <span>{language === 'en' ? 'ខ្មែរ' : 'EN'}</span>
          </button>
        </div>

        <div>
          <h1 className="text-2xl font-extrabold tracking-tight text-slate-900">
            {t('login.title') || 'Sign in to Console'}
          </h1>
          <p className="text-sm text-slate-500 mt-1">
            {t('login.subtitle') || 'Enter your credentials to access the identity workspace'}
          </p>
        </div>

        {error && (
          <div className="p-4 rounded-2xl bg-rose-50 border border-rose-200 text-rose-700 text-sm flex items-center gap-3">
            <AlertCircle size={18} className="shrink-0" />
            <span>{error}</span>
          </div>
        )}

        {/* Credentials Form */}
        <form onSubmit={handleSubmit} className="space-y-5">
          <div className="space-y-1.5">
            <label className="text-sm font-bold text-slate-700 block">
              {t('profile.username') || 'Username or Email'}
            </label>
            <div className="relative">
              <User size={18} className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" />
              <input
                type="text"
                required
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Enter username or email"
                autoComplete="username"
                className="w-full pl-11 pr-4 py-3 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 transition"
              />
            </div>
          </div>

          <div className="space-y-1.5">
            <label className="text-sm font-bold text-slate-700 block">
              {t('changePassword.newPassword') || 'Password'}
            </label>
            <div className="relative">
              <Lock size={18} className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" />
              <input
                type={showPassword ? 'text' : 'password'}
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••••••"
                autoComplete="current-password"
                className="w-full pl-11 pr-11 py-3 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 transition"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
              >
                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full flex items-center justify-center gap-2 py-3.5 text-sm font-bold rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white shadow-md shadow-indigo-200 transition disabled:opacity-50"
          >
            {loading ? (
              <span className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
            ) : (
              <>
                <span>{t('login.submit') || 'Sign In'}</span>
                <ArrowRight size={18} />
              </>
            )}
          </button>
        </form>

        {/* Social Logins Divider */}
        <div className="relative flex py-1 items-center">
          <div className="flex-grow border-t border-slate-200"></div>
          <span className="shrink-0 mx-4 text-xs uppercase font-bold text-slate-400">
            {language === 'km' ? 'ឬចូលតាមរយៈ' : 'or continue with'}
          </span>
          <div className="flex-grow border-t border-slate-200"></div>
        </div>

        {/* Social Logins Grid */}
        <div className="grid grid-cols-2 gap-3">
          <a
            href="/oauth2/authorization/google?prompt=select_account"
            className="flex items-center justify-center gap-2.5 py-2.5 px-4 rounded-xl border border-slate-200 bg-white hover:bg-slate-50 text-xs font-bold text-slate-700 shadow-xs transition"
          >
            <svg width="18" height="18" viewBox="0 0 24 24">
              <path
                fill="#4285F4"
                d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
              />
              <path
                fill="#34A853"
                d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
              />
              <path
                fill="#FBBC05"
                d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"
              />
              <path
                fill="#EA4335"
                d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"
              />
            </svg>
            <span>Google</span>
          </a>

          <a
            href="/oauth2/authorization/github?prompt=consent"
            className="flex items-center justify-center gap-2.5 py-2.5 px-4 rounded-xl border border-slate-200 bg-white hover:bg-slate-50 text-xs font-bold text-slate-700 shadow-xs transition"
          >
            <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
              <path
                fillRule="evenodd"
                clipRule="evenodd"
                d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.53 1.032 1.53 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z"
              />
            </svg>
            <span>GitHub</span>
          </a>

          <a
            href="/oauth2/authorization/facebook"
            className="flex items-center justify-center gap-2.5 py-2.5 px-4 rounded-xl border border-slate-200 bg-white hover:bg-slate-50 text-xs font-bold text-slate-700 shadow-xs transition"
          >
            <svg width="18" height="18" viewBox="0 0 24 24" fill="#1877F2">
              <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z" />
            </svg>
            <span>Facebook</span>
          </a>

          <button
            type="button"
            onClick={() => setShowTelegramModal(true)}
            className="flex items-center justify-center gap-2.5 py-2.5 px-4 rounded-xl border border-slate-200 bg-white hover:bg-slate-50 text-xs font-bold text-slate-700 shadow-xs transition"
          >
            <svg width="18" height="18" viewBox="0 0 24 24" fill="#0088cc">
              <path d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm5.894 8.221l-1.97 9.28c-.145.658-.537.818-1.084.508l-3-2.21-1.446 1.394c-.16.16-.295.295-.605.295l.213-3.053 5.56-5.023c.242-.213-.054-.333-.373-.121l-6.871 4.326-2.962-.924c-.643-.204-.657-.643.136-.953l11.57-4.461c.537-.196 1.006.128.832.941z" />
            </svg>
            <span>Telegram</span>
          </button>
        </div>
      </div>

      {/* Telegram Auth Modal */}
      {showTelegramModal && (
        <div
          className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center p-4 animate-in fade-in"
          onMouseDown={() => setShowTelegramModal(false)}
        >
          <div
            className="w-full max-w-lg bg-white rounded-3xl border border-slate-200 p-8 shadow-2xl space-y-5"
            onMouseDown={(e) => e.stopPropagation()}
          >
            <div className="flex items-center justify-between border-b border-slate-100 pb-4">
              <div className="flex items-center gap-3.5">
                <div className="w-10 h-10 rounded-full bg-[#0088cc] text-white flex items-center justify-center shadow-md">
                  <Send size={18} />
                </div>
                <div>
                  <h2 className="text-base font-bold text-slate-900">
                    {language === 'km' ? 'ចូលតាម Telegram' : 'Sign in with Telegram'}
                  </h2>
                  <p className="text-xs text-slate-500">
                    {language === 'km'
                      ? 'ជ្រើសរើស ឬបញ្ចូលគណនី Telegram របស់អ្នក'
                      : 'Authenticate via Telegram to access your account'}
                  </p>
                </div>
              </div>
              <button
                type="button"
                onClick={() => setShowTelegramModal(false)}
                className="p-1.5 text-slate-400 hover:text-slate-600 rounded-xl"
              >
                <X size={20} />
              </button>
            </div>

            {/* Quick Demo Telegram Profiles */}
            <div className="p-4 bg-slate-50 rounded-2xl border border-slate-200/90 space-y-2">
              <div className="text-xs font-bold text-slate-500">
                {language === 'km' ? 'គណនី Telegram គំរូ:' : 'Quick Demo Profiles:'}
              </div>
              <div className="flex flex-wrap gap-2">
                <button
                  type="button"
                  onClick={() => selectTelegramProfile('872361829', 'sophea_tg', 'Sophea', 'Vann')}
                  className="px-3 py-1.5 text-xs rounded-xl bg-white border border-slate-200 text-slate-700 hover:border-indigo-300 font-mono"
                >
                  @sophea_tg
                </button>
                <button
                  type="button"
                  onClick={() => selectTelegramProfile('918273645', 'alex_cambodia', 'Alex', 'Chen')}
                  className="px-3 py-1.5 text-xs rounded-xl bg-white border border-slate-200 text-slate-700 hover:border-indigo-300 font-mono"
                >
                  @alex_cambodia
                </button>
                <button
                  type="button"
                  onClick={() => selectTelegramProfile('738192039', 'daravuth_dev', 'Daravuth', 'Sok')}
                  className="px-3 py-1.5 text-xs rounded-xl bg-white border border-slate-200 text-slate-700 hover:border-indigo-300 font-mono"
                >
                  @daravuth_dev
                </button>
              </div>
            </div>

            <form onSubmit={handleTelegramSubmit} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-1">
                  <label className="text-xs font-bold text-slate-700">First Name</label>
                  <input
                    required
                    value={tgFirstName}
                    onChange={(e) => setTgFirstName(e.target.value)}
                    placeholder="Sophea"
                    className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-bold text-slate-700">Last Name</label>
                  <input
                    value={tgLastName}
                    onChange={(e) => setTgLastName(e.target.value)}
                    placeholder="Vann"
                    className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                  />
                </div>
              </div>

              <div className="space-y-1">
                <label className="text-xs font-bold text-slate-700">Telegram Username</label>
                <input
                  required
                  value={tgUsername}
                  onChange={(e) => setTgUsername(e.target.value)}
                  placeholder="sophea_tg"
                  className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                />
              </div>

              <div className="space-y-1">
                <label className="text-xs font-bold text-slate-700">Telegram User ID</label>
                <input
                  required
                  value={tgId}
                  onChange={(e) => setTgId(e.target.value)}
                  placeholder="872361829"
                  className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-200 bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
                />
              </div>

              <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setShowTelegramModal(false)}
                  className="px-4 py-2 text-xs font-bold rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 transition"
                >
                  {t('cancel') || 'Cancel'}
                </button>
                <button
                  type="submit"
                  className="flex items-center gap-2 px-5 py-2 text-xs font-bold rounded-xl bg-[#0088cc] hover:bg-[#0077b5] text-white transition shadow-md"
                >
                  <Send size={15} />
                  <span>{language === 'km' ? 'បន្តជាមួយ Telegram' : 'Continue with Telegram'}</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}

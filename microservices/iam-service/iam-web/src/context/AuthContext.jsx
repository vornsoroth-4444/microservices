import React, { createContext, useContext, useEffect, useMemo, useState, useCallback } from 'react'
import { translations } from '../translations'
import {
  api,
  getToken,
  setToken,
  getUser,
  setUser,
  clearToken,
  isUserAdmin,
  pageItems,
  completeLoginCallback,
} from '../api'

const AuthContext = createContext(null)

export const useAuth = () => {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within an AuthProvider')
  return ctx
}

export function AuthProvider({ children }) {
  const [token, setTokenState] = useState(() => getToken())
  const [profile, setProfile] = useState(() => {
    return getUser() || {
      givenName: 'System',
      familyName: 'Admin',
      username: 'admin',
      email: 'admin@istad.co',
      phoneNumber: '',
      pictureUrl: '',
    }
  })

  const [language, setLanguage] = useState(() => localStorage.getItem('iam_lang') || 'en')
  const [apiReady, setApiReady] = useState(false)
  const [busy, setBusy] = useState(false)
  const [toast, setToast] = useState({ message: '', isError: false })

  // Data Stores
  const [users, setUsers] = useState([])
  const [userTotal, setUserTotal] = useState(0)
  const [roles, setRoles] = useState([])
  const [permissions, setPermissions] = useState([])
  const [clients, setClients] = useState([])
  const [events, setEvents] = useState([])
  const [sessions, setSessions] = useState([])

  // Modal controls
  const [modal, setModal] = useState(null)
  const [detailUser, setDetailUser] = useState(null)

  const isAdmin = useMemo(() => isUserAdmin(profile), [profile])

  const t = useCallback(
    (key) => translations[language]?.[key] || translations.en?.[key] || key,
    [language]
  )

  const toggleLanguage = () => {
    const next = language === 'en' ? 'km' : 'en'
    setLanguage(next)
    localStorage.setItem('iam_lang', next)
  }

  const flash = (message, isError = false) => {
    setToast({ message, isError })
    setTimeout(() => setToast({ message: '', isError: false }), 3500)
  }

  const loadWorkspaceData = useCallback(async (userOverride) => {
    const cur = userOverride || profile || getUser()
    const admin = isUserAdmin(cur)
    setBusy(true)

    try {
      if (admin) {
        const results = await Promise.allSettled([
          api('/api/users?size=100'),
          api('/api/roles?size=100'),
          api('/api/permissions?size=100'),
          api('/api/me'),
          api('/api/clients'),
          api('/api/audit-logs?size=30'),
          api('/api/me/sessions'),
        ])

        let anyLive = false

        if (results[0].status === 'fulfilled') {
          anyLive = true
          const userData = results[0].value
          setUsers(pageItems(userData))
          setUserTotal(userData?.totalElements ?? pageItems(userData).length)
        }
        if (results[1].status === 'fulfilled') {
          anyLive = true
          setRoles(pageItems(results[1].value))
        }
        if (results[2].status === 'fulfilled') {
          anyLive = true
          setPermissions(pageItems(results[2].value))
        }
        if (results[3].status === 'fulfilled') {
          anyLive = true
          const p = results[3].value
          const [gName, ...fName] = String(p.name || '').split(' ')
          setProfile({
            ...p,
            givenName: p.givenName || gName || '',
            familyName: p.familyName || fName.join(' ') || '',
          })
        }
        if (results[4].status === 'fulfilled') {
          anyLive = true
          setClients(pageItems(results[4].value))
        }
        if (results[5].status === 'fulfilled') {
          anyLive = true
          setEvents(pageItems(results[5].value))
        }
        if (results[6].status === 'fulfilled') {
          anyLive = true
          setSessions(pageItems(results[6].value))
        }

        setApiReady(anyLive)
      } else {
        const results = await Promise.allSettled([
          api('/api/me'),
          api('/api/me/sessions'),
        ])

        if (results[0].status === 'fulfilled') {
          const p = results[0].value
          const [gName, ...fName] = String(p.name || '').split(' ')
          setProfile({
            ...p,
            givenName: p.givenName || gName || '',
            familyName: p.familyName || fName.join(' ') || '',
          })
        }
        if (results[1].status === 'fulfilled') {
          setSessions(pageItems(results[1].value))
        }

        setApiReady(true)
      }
    } catch {
      // Offline fallback
    } finally {
      setBusy(false)
    }
  }, [profile])

  // Initial mount check
  useEffect(() => {
    const init = async () => {
      let activeToken = token
      try {
        if (!activeToken) {
          try {
            const res = await fetch('/api/auth/session', { credentials: 'include' })
            if (res.ok) {
              const sessionData = await res.json()
              if (sessionData && sessionData.accessToken) {
                activeToken = sessionData.accessToken
                setToken(activeToken)
                setUser(sessionData.user)
                setTokenState(activeToken)
                flash('Signed in via social account session!')
                await loadWorkspaceData(sessionData.user)
                return
              }
            }
          } catch {}
        }

        const exchangedToken = await completeLoginCallback()
        if (exchangedToken) {
          activeToken = exchangedToken
          setTokenState(exchangedToken)
          flash('Authenticated successfully via OAuth 2.1 PKCE!')
        }
      } catch (err) {
        flash(`Sign-in notice: ${err.message}`, true)
      }
      await loadWorkspaceData()
    }
    init()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const act = async (endpoint, method = 'POST', body = null) => {
    try {
      const result = await api(endpoint, {
        method,
        body: body ? JSON.stringify(body) : undefined,
      })
      flash(t('changesSaved'))
      await loadWorkspaceData()
      return result ?? true
    } catch (e) {
      flash(e.message.slice(0, 160), true)
      return false
    }
  }

  const handleLogin = (newToken, newUser) => {
    setToken(newToken)
    setUser(newUser)
    setTokenState(newToken)
    if (newUser) setProfile(newUser)
    flash(language === 'km' ? 'បានចូលគណនីដោយជោគជ័យ' : 'Signed in successfully')
    loadWorkspaceData(newUser)
  }

  const handleLogout = () => {
    clearToken()
    setTokenState('')
    setProfile(null)
    setApiReady(false)
  }

  const value = {
    token,
    profile,
    setProfile,
    isAdmin,
    language,
    toggleLanguage,
    t,
    apiReady,
    busy,
    setBusy,
    toast,
    flash,
    users,
    setUsers,
    userTotal,
    setUserTotal,
    roles,
    setRoles,
    permissions,
    setPermissions,
    clients,
    setClients,
    events,
    setEvents,
    sessions,
    setSessions,
    modal,
    setModal,
    detailUser,
    setDetailUser,
    loadWorkspaceData,
    act,
    handleLogin,
    handleLogout,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

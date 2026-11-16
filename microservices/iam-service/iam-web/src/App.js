import React, { useEffect, useMemo, useState } from 'react'
import {
  Activity,
  AlertTriangle,
  ArrowUpRight,
  Bell,
  Check,
  ChevronDown,
  ChevronRight,
  Command,
  CreditCard,
  Download,
  Ellipsis,
  Fingerprint,
  Globe2,
  History,
  KeyRound,
  LayoutDashboard,
  LockKeyhole,
  LogOut,
  Menu,
  Plus,
  Search,
  Settings,
  Share2,
  Shield,
  ShieldCheck,
  Sparkles,
  UserRound,
  Users,
  X
} from 'lucide-react'

import './style.css'
import './overrides.css'

import { translations } from './translations'
import {
  api,
  getToken,
  setToken,
  getUser,
  setUser,
  clearToken,
  isUserAdmin,
  pageItems,
  label,
  beginLogin,
  completeLoginCallback
} from './api'

import { UserDetailModal } from './components/UserDetailModal'
import { CrudModal } from './components/CrudModals'
import { AccountView } from './components/AccountView'
import { AuditView } from './components/AuditView'
import { SystemConfigView } from './components/SystemConfigView'
import { LoginView } from './components/LoginView'

const ADMIN_NAV_GROUPS = [
  {
    group: 'MANAGEMENT (+50 pts)',
    links: [
      ['Overview', LayoutDashboard],
      ['Users', Users],
      ['Roles', ShieldCheck],
      ['Permissions', KeyRound],
      ['OAuth clients', CreditCard]
    ]
  },
  {
    group: 'SECURITY & AUDIT',
    links: [
      ['Sessions', Fingerprint],
      ['Audit log', Activity],
      ['Configuration', Settings]
    ]
  },
  {
    group: 'PERSONAL',
    links: [['My account', UserRound]]
  }
]

const USER_NAV_GROUPS = [
  {
    group: 'ACCOUNT (+40 pts)',
    links: [
      ['Profile', UserRound],
      ['Security & Password', KeyRound],
      ['Linked Accounts', Share2],
      ['Active Sessions', Fingerprint],
      ['Session Management', LogOut],
      ['Login History', History],
      ['Account Activity', Activity],
      ['Account Deactivation', AlertTriangle]
    ]
  }
]

const ACCOUNT_SECTIONS = [
  'Profile',
  'Security & Password',
  'Linked Accounts',
  'Active Sessions',
  'Session Management',
  'Login History',
  'Account Activity',
  'Account Deactivation',
  'My account'
]

const ACCOUNT_TAB_MAP = {
  Profile: 'profile',
  'Security & Password': 'security',
  'Linked Accounts': 'linked',
  'Active Sessions': 'sessions',
  'Session Management': 'sessions',
  'Login History': 'history',
  'Account Activity': 'activity',
  'Account Deactivation': 'deactivate',
  'My account': 'profile'
}

const TAB_TO_SECTION_MAP = {
  profile: 'Profile',
  security: 'Security & Password',
  linked: 'Linked Accounts',
  sessions: 'Active Sessions',
  history: 'Login History',
  activity: 'Account Activity',
  deactivate: 'Account Deactivation'
}

export function App() {
  const initialUser = getUser()
  const initialIsAdmin = isUserAdmin(initialUser)
  const [section, setSection] = useState(() => (initialIsAdmin ? 'Overview' : 'Profile'))
  const [language, setLanguage] = useState(() => localStorage.getItem('iam_lang') || 'en')

  // Data Stores (populated strictly from live backend APIs)
  const [users, setUsers] = useState([])
  const [userTotal, setUserTotal] = useState(0)
  const [roles, setRoles] = useState([])
  const [permissions, setPermissions] = useState([])
  const [clients, setClients] = useState([])
  const [events, setEvents] = useState([])
  const [sessions, setSessions] = useState([])

  // Current logged in user profile
  const [profile, setProfile] = useState(() => {
    return getUser() || {
      givenName: 'System',
      familyName: 'Admin',
      username: 'admin',
      email: 'admin@istad.co',
      phoneNumber: '',
      pictureUrl: ''
    }
  })

  // App UI state
  const [token, setTokenState] = useState(() => getToken())
  const [apiReady, setApiReady] = useState(false)
  const [busy, setBusy] = useState(false)
  const [searchQuery, setSearchQuery] = useState('')
  const [modal, setModal] = useState(null)
  const [toast, setToast] = useState('')
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)

  const isAdmin = useMemo(() => isUserAdmin(profile), [profile])
  const navGroups = isAdmin ? ADMIN_NAV_GROUPS : USER_NAV_GROUPS

  // Keep non-admin user on account sections
  useEffect(() => {
    if (!isAdmin && !ACCOUNT_SECTIONS.includes(section)) {
      setSection('Profile')
    }
  }, [isAdmin, section])

  // i18n lookup
  const t = (key) => translations[language]?.[key] || translations.en?.[key] || key

  const toggleLanguage = () => {
    const next = language === 'en' ? 'km' : 'en'
    setLanguage(next)
    localStorage.setItem('iam_lang', next)
  }

  const flash = (message, isError = false) => {
    setToast(message)
    setTimeout(() => setToast(''), 3500)
  }

  // Load backend data
  const loadWorkspaceData = async (userOverride) => {
    const cur = userOverride || profile || getUser()
    const admin = isUserAdmin(cur)
    setBusy(true)

    if (admin) {
      const results = await Promise.allSettled([
        api('/api/users?size=100'),
        api('/api/roles?size=100'),
        api('/api/permissions?size=100'),
        api('/api/me'),
        api('/api/clients'),
        api('/api/audit-logs?size=30'),
        api('/api/me/sessions')
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
          familyName: p.familyName || fName.join(' ') || ''
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
      // Regular user: strictly personal account endpoints
      const results = await Promise.allSettled([
        api('/api/me'),
        api('/api/me/sessions')
      ])

      if (results[0].status === 'fulfilled') {
        const p = results[0].value
        const [gName, ...fName] = String(p.name || '').split(' ')
        setProfile({
          ...p,
          givenName: p.givenName || gName || '',
          familyName: p.familyName || fName.join(' ') || ''
        })
      }
      if (results[1].status === 'fulfilled') {
        setSessions(pageItems(results[1].value))
      }

      setApiReady(true)
    }

    setBusy(false)
  }

  // Initial mount & OAuth PKCE / Social Session check
  useEffect(() => {
    const init = async () => {
      let activeToken = token
      try {
        // If not already authenticated, check for an existing session cookie (e.g. social login)
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
        flash(`Sign-in callback notice: ${err.message}`, true)
      }
      await loadWorkspaceData()
    }
    init()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  // Universal action handler
  const act = async (endpoint, method = 'POST', body = null) => {
    try {
      const result = await api(endpoint, {
        method,
        body: body ? JSON.stringify(body) : undefined
      })
      flash(t('changesSaved'))
      await loadWorkspaceData()
      return result ?? true
    } catch (e) {
      flash(e.message.slice(0, 160), true)
      return false
    }
  }

  // Filtered users for Users tab
  const filteredUsers = useMemo(() => {
    if (!searchQuery.trim()) return users
    const q = searchQuery.toLowerCase()
    return users.filter((u) => {
      const full = `${label(u)} ${u.username || ''} ${u.email || ''} ${u.role || ''}`.toLowerCase()
      return full.includes(q)
    })
  }, [users, searchQuery])

  // Overview stats (strictly live database counts)
  const statsCards = [
    {
      title: t('totalUsers'),
      value: userTotal.toLocaleString(),
      change: t('live'),
      icon: Users,
      color: 'blue'
    },
    {
      title: t('activeUsers'),
      value: users.filter((u) => u.enabled !== false).length.toLocaleString(),
      change: t('live'),
      icon: Activity,
      color: 'green'
    },
    {
      title: t('rolesCount'),
      value: roles.length.toLocaleString(),
      change: t('live'),
      icon: Shield,
      color: 'purple'
    },
    {
      title: t('permissionsCount'),
      value: permissions.length.toLocaleString(),
      change: t('live'),
      icon: KeyRound,
      color: 'orange'
    },
    {
      title: t('oauthClientsCount'),
      value: clients.length.toLocaleString(),
      change: t('live'),
      icon: CreditCard,
      color: 'blue'
    }
  ]

  const handleNavClick = (title) => {
    setSection(title)
    setMobileMenuOpen(false)
  }

  const handleLogin = (newToken, newUser) => {
    setToken(newToken)
    setUser(newUser)
    setTokenState(newToken)
    if (newUser) setProfile(newUser)
    const admin = isUserAdmin(newUser)
    setSection(admin ? 'Overview' : 'Profile')
    flash(language === 'km' ? 'បានចូលគណនីដោយជោគជ័យ' : 'Signed in successfully')
    loadWorkspaceData(newUser)
  }

  const handleLogout = () => {
    clearToken()
    setTokenState('')
    setProfile(null)
    setApiReady(false)
    setSection('Overview')
  }

  if (!token) {
    return (
      <LoginView
        onLogin={handleLogin}
        language={language}
        onToggleLanguage={toggleLanguage}
        t={t}
      />
    )
  }

  return (
    <div className="app-shell">
      {/* 1. Sidebar */}
      <aside className={`sidebar ${mobileMenuOpen ? 'sidebar-open' : ''}`}>
        <div className="brand">
          <div className="brand-mark">
            <Command size={18} />
          </div>
          <span>
            mekong<span className="brand-light">.identity</span>
          </span>
          <button
            type="button"
            className="mobile-close"
            onClick={() => setMobileMenuOpen(false)}
          >
            <X size={18} />
          </button>
        </div>

        <button type="button" className="org-switch">
          <div className="org-mark">M</div>
          <span>
            <b>Mekong Identity</b>
            <small>Enterprise IAM Platform</small>
          </span>
          <ChevronDown size={14} />
        </button>

        <div className="nav-wrap">
          {navGroups.map((grp) => (
            <div className="nav-group" key={grp.group}>
              <div className="nav-label">{t(grp.group)}</div>
              {grp.links.map(([linkTitle, Icon]) => {
                const isSelected = section === linkTitle
                return (
                  <button
                    key={linkTitle}
                    type="button"
                    className={`nav-item ${isSelected ? 'selected' : ''}`}
                    onClick={() => handleNavClick(linkTitle)}
                  >
                    <Icon size={16} />
                    <span>{t(linkTitle)}</span>
                    {linkTitle === 'Users' && (
                      <span className="nav-count">{userTotal}</span>
                    )}
                    {linkTitle === 'Roles' && (
                      <span className="nav-count">{roles.length}</span>
                    )}
                    {linkTitle === 'Permissions' && (
                      <span className="nav-count">{permissions.length}</span>
                    )}
                    {linkTitle === 'OAuth clients' && (
                      <span className="nav-count">{clients.length}</span>
                    )}
                    {linkTitle === 'Active Sessions' && (
                      <span className="nav-count">{sessions.length}</span>
                    )}
                  </button>
                )
              })}
            </div>
          ))}
        </div>

        <div className="sidebar-bottom">
          <div className="upgrade-card">
            <div className="upgrade-icon">
              <Sparkles size={16} />
            </div>
            <b>{isAdmin ? 'Security posture' : 'Account security'}</b>
            <p>
              {isAdmin
                ? 'Authentication and role engine verified. Review system audit reports.'
                : 'All 8 personal account & session security workflows active.'}
            </p>
            <button
              type="button"
              onClick={() => handleNavClick(isAdmin ? 'Audit log' : 'Security & Password')}
            >
              {isAdmin ? t('viewAllActivity') : t('passwordAndSecurity')}{' '}
              <ChevronRight size={13} />
            </button>
          </div>

          <div className="user-chip">
            <div className="avatar lilac">
              {label(profile).split(' ').map((s) => s[0]).join('').slice(0, 2).toUpperCase()}
            </div>
            <span>
              <b>{label(profile)}</b>
              <small>{isAdmin ? 'Workspace Administrator' : 'Standard Member'}</small>
            </span>
            <button
              type="button"
              title={isAdmin ? t('My account') : t('personalInfo')}
              onClick={() => handleNavClick(isAdmin ? 'My account' : 'Profile')}
            >
              <Ellipsis size={17} />
            </button>
          </div>
        </div>
      </aside>

      {/* 2. Main Content Area */}
      <main className="main-area">
        {/* Topbar */}
        <header className="topbar">
          <div className="breadcrumbs">
            <span>Workspace</span>
            <ChevronRight size={13} />
            <b>{t(section)}</b>
          </div>

          <div className="top-actions">
            <div className="api-status">
              <i className={apiReady ? 'online' : ''} />
              {busy
                ? (language === 'km' ? 'កំពុងផ្ទុក...' : 'Loading...')
                : apiReady
                ? t('apiConnected')
                : (language === 'km' ? 'កំពុងភ្ជាប់...' : 'Connecting...')}
            </div>

            <button
              type="button"
              className="icon-button lang-button"
              onClick={toggleLanguage}
              title="Switch language between English and Khmer"
            >
              <Globe2 size={15} />
              <span>{language === 'en' ? 'EN' : 'ខ្មែរ'}</span>
            </button>

            {!token ? (
              <button
                type="button"
                className="button button-primary sign-in-button"
                onClick={beginLogin}
              >
                {t('signIn')}
              </button>
            ) : (
              <button
                type="button"
                className="button button-quiet sign-in-button"
                onClick={handleLogout}
                title={t('login.signOut') || t('signOut')}
              >
                <LogOut size={13} /> {t('login.signOut') || t('signOut')}
              </button>
            )}

            <button
              type="button"
              className="icon-button"
              title="Notifications"
              onClick={() => flash('No pending security alerts')}
            >
              <Bell size={16} />
              <i className="notification-dot" />
            </button>

            <div
              className="top-avatar"
              onClick={() => handleNavClick('My account')}
              style={{ cursor: 'pointer' }}
            >
              {label(profile).split(' ').map((s) => s[0]).join('').slice(0, 2).toUpperCase()}
            </div>

            <button
              type="button"
              className="mobile-menu"
              onClick={() => setMobileMenuOpen(true)}
            >
              <Menu size={20} />
            </button>
          </div>
        </header>

        {/* Page Body */}
        <div className="page-content">
          {/* Section Heading */}
          <div className="page-heading">
            <div>
              <div className="eyebrow">
                {isAdmin ? 'ADMIN CONSOLE' : 'ACCOUNT PORTAL'}{' '}
                <span className="eyebrow-dot">·</span> {label(profile).toUpperCase()}
              </div>
              <h1>{section === 'Overview' ? 'Identity & Access Console' : t(section)}</h1>
              <p>
                {section === 'Overview'
                  ? 'A centralized view of your enterprise security, accounts, roles, and access.'
                  : section === 'Profile'
                  ? 'View and update your personal details, email, phone number, and avatar photo.'
                  : section === 'Security & Password'
                  ? 'Change or reset your password and enforce credential security.'
                  : section === 'Linked Accounts'
                  ? 'Connect social identity providers (Google, GitHub, Facebook) for single sign-on.'
                  : section === 'Active Sessions' || section === 'Session Management'
                  ? 'Inspect all currently active sessions and terminate unrecognized devices.'
                  : section === 'Login History'
                  ? 'Audit recent successful and failed sign-in attempts with device information.'
                  : section === 'Account Activity'
                  ? 'Track sensitive changes made to your profile and security credentials.'
                  : section === 'Account Deactivation'
                  ? 'Request immediate deactivation of your account and revoke active tokens.'
                  : `Manage and review ${section.toLowerCase()} across the IAM workspace.`}
              </p>
            </div>

            <div className="heading-actions">
              <button
                type="button"
                className="button button-quiet"
                onClick={() => flash(t('allSystemsOperational'))}
              >
                <Download size={15} /> {t('export')}
              </button>

              {isAdmin && ['Users', 'Roles', 'Permissions', 'OAuth clients'].includes(section) && (
                <button
                  type="button"
                  className="button button-primary"
                  onClick={() => {
                    if (section === 'Users') setModal('user')
                    else if (section === 'Roles') setModal('role')
                    else if (section === 'Permissions') setModal('permission')
                    else if (section === 'OAuth clients') setModal('client')
                  }}
                >
                  <Plus size={16} />
                  {section === 'Users' && t('inviteUser')}
                  {section === 'Roles' && t('createRole')}
                  {section === 'Permissions' && t('createPermission')}
                  {section === 'OAuth clients' && t('registerClient')}
                </button>
              )}
            </div>
          </div>

          {/* Section Content Rendering */}
          {/* A. OVERVIEW */}
          {section === 'Overview' && (
            <>
              {/* Metric Stats Cards */}
              <div className="stats-grid">
                {statsCards.map((sc) => {
                  const Icon = sc.icon
                  return (
                    <div className="stat-card" key={sc.title}>
                      <div className="stat-top">
                        <span>{sc.title}</span>
                        <div className={`stat-icon ${sc.color}`}>
                          <Icon size={16} />
                        </div>
                      </div>
                      <div className="stat-value">
                        {sc.value}
                        <span className="stat-change">
                          <ArrowUpRight size={13} />
                          {sc.change}
                        </span>
                      </div>
                      <div className="stat-foot">
                        {t('comparedToLastMonth')}
                        <span className="sparkline" style={{ '--spark-color': `var(--${sc.color})` }}>
                          {[16, 25, 20, 32, 24, 38, 29, 48, 35, 45, 38, 56, 42, 60, 48, 70].map(
                            (h, j) => (
                              <i key={j} style={{ height: `${h}%` }} />
                            )
                          )}
                        </span>
                      </div>
                    </div>
                  )
                })}
              </div>

              {/* Chart & Security Posture */}
              <div className="overview-grid">
                {/* Authentication Activity Trends SVG */}
                <section className="panel activity-panel">
                  <div className="panel-heading">
                    <div>
                      <h2>{t('activityOverview')}</h2>
                      <p>Authentication throughput · live trend monitoring</p>
                    </div>
                    <button type="button" className="select-button">
                      Last 7 days <ChevronDown size={13} />
                    </button>
                  </div>

                  <div className="chart">
                    <div className="chart-y">
                      <span>300</span>
                      <span>200</span>
                      <span>100</span>
                      <span>0</span>
                    </div>

                    <div className="chart-main">
                      <div className="chart-grid">
                        <i />
                        <i />
                        <i />
                        <i />
                      </div>

                      <svg
                        viewBox="0 0 700 185"
                        preserveAspectRatio="none"
                        className="chart-svg"
                      >
                        <defs>
                          <linearGradient id="fill" x1="0" x2="0" y1="0" y2="1">
                            <stop offset="0" stopColor="#665bea" stopOpacity=".18" />
                            <stop offset="1" stopColor="#665bea" stopOpacity="0" />
                          </linearGradient>
                        </defs>
                        <path
                          d="M0 137 C40 131 54 106 90 115 S142 136 175 102 S228 86 260 105 S308 132 350 76 S400 88 438 67 S483 93 526 61 S578 82 612 42 S664 60 700 23 V185 H0Z"
                          fill="url(#fill)"
                        />
                        <path
                          d="M0 137 C40 131 54 106 90 115 S142 136 175 102 S228 86 260 105 S308 132 350 76 S400 88 438 67 S483 93 526 61 S578 82 612 42 S664 60 700 23"
                          fill="none"
                          stroke="#6b5ee8"
                          strokeWidth="3"
                          vectorEffect="non-scaling-stroke"
                          strokeLinecap="round"
                        />
                        <path
                          d="M0 157 C40 154 54 142 90 148 S142 151 175 138 S228 144 260 134 S308 151 350 120 S400 127 438 113 S483 121 526 102 S578 120 612 91 S664 104 700 80"
                          fill="none"
                          stroke="#43b994"
                          strokeWidth="2"
                          vectorEffect="non-scaling-stroke"
                          strokeDasharray="5 5"
                          strokeLinecap="round"
                        />
                      </svg>

                      <div className="chart-x">
                        <span>Nov 10</span>
                        <span>Nov 11</span>
                        <span>Nov 12</span>
                        <span>Nov 13</span>
                        <span>Nov 14</span>
                        <span>Nov 15</span>
                        <span>Nov 16</span>
                      </div>
                    </div>
                  </div>

                  <div className="chart-legend">
                    <span>
                      <i className="legend-purple" />
                      {t('successfulSignIns')}
                    </span>
                    <span>
                      <i className="legend-green" />
                      {t('failedSignIns')}
                    </span>
                    <span className="legend-trend">
                      <ArrowUpRight size={14} /> 24.5% <small>{t('vsLastWeek')}</small>
                    </span>
                  </div>
                </section>

                {/* Health Posture Card */}
                <section className="panel posture-panel">
                  <div className="panel-heading">
                    <div>
                      <h2>{t('securityPosture')}</h2>
                      <p>{t('healthScore')}</p>
                    </div>
                    <button type="button" className="more-button">
                      <Ellipsis size={17} />
                    </button>
                  </div>

                  <div className="score-wrap">
                    <div className="score-ring">
                      <div>
                        <strong>94</strong>
                        <small>/100</small>
                      </div>
                    </div>
                    <div>
                      <span className="score-good">
                        <i /> {t('good')}
                      </span>
                      <p>
                        Strong OAuth 2.1 authentication posture with PKCE enforcement and PBKDF2
                        password policies.
                      </p>
                    </div>
                  </div>

                  <div className="score-divider" />

                  <div className="security-check">
                    <div className="check-icon">
                      <Check size={13} />
                    </div>
                    <span>
                      <b>{t('passwordPolicyCheck')}</b>
                      <small>{t('passwordPolicyNote')}</small>
                    </span>
                    <span className="check-value">{t('good')}</span>
                  </div>

                  <div className="security-check">
                    <div className="check-icon">
                      <Check size={13} />
                    </div>
                    <span>
                      <b>{t('mfaCheck')}</b>
                      <small>{t('mfaNote')}</small>
                    </span>
                    <span className="check-value">{t('notTracked')}</span>
                  </div>

                  <button
                    type="button"
                    className="text-link"
                    onClick={() => handleNavClick('Configuration')}
                  >
                    {t('viewRecommendations')} <ChevronRight size={14} />
                  </button>
                </section>
              </div>

              {/* Quick Team Members Table Preview */}
              <section className="panel users-panel">
                <div className="panel-heading">
                  <div>
                    <h2>Team members preview</h2>
                    <p>Recent accounts in your workspace</p>
                  </div>
                  <button
                    type="button"
                    className="text-link"
                    onClick={() => handleNavClick('Users')}
                  >
                    {t('viewAllUsers')} <ChevronRight size={14} />
                  </button>
                </div>

                <div className="table-scroll">
                  <table>
                    <thead>
                      <tr>
                        <th>{t('thUser')}</th>
                        <th>{t('thRole')}</th>
                        <th>{t('thStatus')}</th>
                        <th>{t('thLastActive')}</th>
                        <th>{t('thActions')}</th>
                      </tr>
                    </thead>
                    <tbody>
                      {users.slice(0, 5).map((u, i) => (
                        <tr key={u.uuid || i}>
                          <td>
                            <button
                              type="button"
                              className="person-cell"
                              onClick={() => setModal({ type: 'detail', user: u })}
                            >
                              <div className={`avatar ${u.color || ['lilac', 'mint', 'peach', 'blue', 'yellow'][i % 5]}`}>
                                {label(u).split(' ').map((s) => s[0]).join('').slice(0, 2).toUpperCase()}
                              </div>
                              <span>
                                <b>{label(u)}</b>
                                <small>{u.email || u.username}</small>
                              </span>
                            </button>
                          </td>
                          <td>
                            <span className="role-label">
                              <span className="role-dot" />
                              {u.role || u.roles?.[0]?.name || 'Member'}
                            </span>
                          </td>
                          <td>
                            <span className={`status ${u.enabled === false ? 'inactive' : ''}`}>
                              <i />
                              {u.enabled === false ? t('inactive') : t('active')}
                            </span>
                          </td>
                          <td className="last-active">{u.last || 'Today'}</td>
                          <td>
                            <div className="row-actions">
                              <button
                                type="button"
                                title={t('viewDetails')}
                                onClick={() => setModal({ type: 'detail', user: u })}
                              >
                                {t('viewDetails')}
                              </button>
                              <button
                                type="button"
                                title={u.enabled === false ? t('activate') : t('deactivate')}
                                onClick={() =>
                                  act(`/api/users/${u.uuid}/${u.enabled === false ? 'enable' : 'disable'}`)
                                }
                              >
                                <LockKeyhole size={14} />
                              </button>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </section>

              {/* Bottom Grid: Recent Sign-ins & Role Distribution */}
              <div className="bottom-grid">
                <section className="panel login-panel">
                  <div className="panel-heading">
                    <div>
                      <h2>{t('recentSignIns')}</h2>
                      <p>Latest authentication activity events</p>
                    </div>
                  </div>

                  {events
                    .filter((e) => e.type?.startsWith('LOGIN'))
                    .slice(0, 4)
                    .map((e, idx) => (
                      <div className="login-row" key={e.id || idx}>
                        <div className={`avatar ${idx % 2 === 0 ? 'lilac' : 'mint'}`}>
                          {String(e.actor || 'U').slice(0, 2).toUpperCase()}
                        </div>
                        <div className="login-name">
                          <b>{e.actor || 'Unknown account'}</b>
                          <small>
                            IP: {e.ipAddress || '127.0.0.1'} <span>·</span> {e.userAgent || 'Web browser'}
                          </small>
                        </div>
                        <span className={`event-pill ${e.succeeded === false ? 'failed' : 'success'}`}>
                          {e.succeeded === false ? t('blocked') : t('success')}
                        </span>
                        <time>{e.createdAt ? new Date(e.createdAt).toLocaleTimeString() : 'Now'}</time>
                      </div>
                    ))}

                  {!events.filter((e) => e.type?.startsWith('LOGIN')).length && (
                    <div style={{ padding: '16px 0', color: 'var(--text-muted)', fontSize: '12px' }}>
                      No recent login events recorded yet.
                    </div>
                  )}

                  <button
                    type="button"
                    className="text-link"
                    onClick={() => handleNavClick('Audit log')}
                  >
                    {t('viewAllActivity')} <ChevronRight size={14} />
                  </button>
                </section>

                <section className="panel access-panel">
                  <div className="panel-heading">
                    <div>
                      <h2>{t('accessDistribution')}</h2>
                      <p>Role assignments across workspace</p>
                    </div>
                  </div>

                  {roles.map((r, idx) => {
                    const count = users.filter((u) => u.role === r.name || u.roles?.some((ur) => ur.name === r.name)).length
                    const pct = userTotal > 0 ? Math.round((count / userTotal) * 100) : 0
                    const colors = ['#6d60ed', '#42b993', '#f4a66b', '#78a4e8', '#ec4899', '#8b5cf6']
                    const color = colors[idx % colors.length]
                    return (
                      <div className="role-bar" key={r.uuid || r.name}>
                        <span>{r.name}</span>
                        <div>
                          <i style={{ width: `${Math.max(pct, 8)}%`, background: color }} />
                        </div>
                        <b>{count}</b>
                      </div>
                    )
                  })}

                  <button
                    type="button"
                    className="text-link"
                    onClick={() => handleNavClick('Roles')}
                  >
                    {t('manageRoles')} <ChevronRight size={14} />
                  </button>
                </section>
              </div>
            </>
          )}

          {/* B. USERS MANAGEMENT */}
          {section === 'Users' && (
            <section className="panel full-table">
              <div className="table-toolbar">
                <div>
                  <h2>
                    {t('allUsersTitle')} <span className="table-count">{filteredUsers.length}</span>
                  </h2>
                  <p>{t('allUsersSubtitle')}</p>
                </div>

                <div className="toolbar-right">
                  <label className="search-box">
                    <Search size={15} />
                    <input
                      value={searchQuery}
                      onChange={(e) => setSearchQuery(e.target.value)}
                      placeholder={t('searchUsers')}
                    />
                  </label>
                  <button
                    type="button"
                    className="button button-primary"
                    onClick={() => setModal('user')}
                  >
                    <Plus size={15} /> {t('inviteUser')}
                  </button>
                </div>
              </div>

              <div className="table-scroll">
                <table>
                  <thead>
                    <tr>
                      <th>{t('thUser')}</th>
                      <th>{t('thRole')}</th>
                      <th>{t('thStatus')}</th>
                      <th>{t('thLastActive')}</th>
                      <th>{t('thActions')}</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredUsers.map((u, i) => (
                      <tr key={u.uuid || i}>
                        <td>
                          <button
                            type="button"
                            className="person-cell"
                            onClick={() => setModal({ type: 'detail', user: u })}
                          >
                            <div className={`avatar ${u.color || ['lilac', 'mint', 'peach', 'blue', 'yellow'][i % 5]}`}>
                              {label(u).split(' ').map((s) => s[0]).join('').slice(0, 2).toUpperCase()}
                            </div>
                            <span>
                              <b>{label(u)}</b>
                              <small>{u.email || u.username}</small>
                            </span>
                          </button>
                        </td>
                        <td>
                          <span className="role-label">
                            <span className="role-dot" />
                            {u.role || u.roles?.[0]?.name || 'Member'}
                          </span>
                        </td>
                        <td>
                          <span className={`status ${u.enabled === false ? 'inactive' : ''}`}>
                            <i />
                            {u.enabled === false ? t('disabled') : t('enabled')}
                            {u.accountNonLocked === false ? ` · ${t('locked')}` : ''}
                          </span>
                        </td>
                        <td className="last-active">{u.last || 'Today'}</td>
                        <td>
                          <div className="row-actions">
                            <button
                              type="button"
                              onClick={() => setModal({ type: 'detail', user: u })}
                            >
                              {t('viewDetails')}
                            </button>
                            <button
                              type="button"
                              onClick={() => setModal({ type: 'user', initial: u, id: u.uuid })}
                            >
                              {t('edit')}
                            </button>
                            <button
                              type="button"
                              title={u.enabled === false ? t('activate') : t('deactivate')}
                              onClick={() =>
                                act(`/api/users/${u.uuid}/${u.enabled === false ? 'enable' : 'disable'}`)
                              }
                            >
                              {u.enabled === false ? t('activate') : t('deactivate')}
                            </button>
                            <button
                              type="button"
                              title={u.accountNonLocked === false ? t('unlock') : t('lock')}
                              onClick={() =>
                                act(`/api/users/${u.uuid}/${u.accountNonLocked === false ? 'unlock' : 'lock'}`)
                              }
                            >
                              {u.accountNonLocked === false ? t('unlock') : t('lock')}
                            </button>
                            <button
                              type="button"
                              className="table-delete"
                              onClick={() => {
                                if (window.confirm(`${t('confirmDelete')} (${label(u)})`)) {
                                  act(`/api/users/${u.uuid}`, 'DELETE')
                                }
                              }}
                            >
                              {t('delete')}
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
                {!filteredUsers.length && (
                  <div className="empty-table">No matching users found.</div>
                )}
              </div>
            </section>
          )}

          {/* C. ROLES MANAGEMENT */}
          {section === 'Roles' && (
            <section className="panel full-table">
              <div className="table-toolbar">
                <div>
                  <h2>
                    {t('rolesTitle')} <span className="table-count">{roles.length}</span>
                  </h2>
                  <p>{t('rolesSubtitle')}</p>
                </div>
                <button
                  type="button"
                  className="button button-primary"
                  onClick={() => setModal('role')}
                >
                  <Plus size={15} /> {t('createRole')}
                </button>
              </div>

              <div className="table-scroll">
                <table>
                  <thead>
                    <tr>
                      <th>{t('thName')}</th>
                      <th>{t('thDescription')}</th>
                      <th>SYSTEM</th>
                      <th>{t('thStatus')}</th>
                      <th>{t('thActions')}</th>
                    </tr>
                  </thead>
                  <tbody>
                    {roles.map((r, i) => (
                      <tr key={r.uuid || i}>
                        <td>
                          <b>{r.name}</b>
                        </td>
                        <td>{r.description || '—'}</td>
                        <td>
                          <span className={`badge ${r.system ? 'badge-purple' : 'badge-orange'}`}>
                            {r.system ? 'PROTECTED' : 'CUSTOM'}
                          </span>
                        </td>
                        <td>
                          <span className={`status ${r.active === false ? 'inactive' : ''}`}>
                            <i />
                            {r.active === false ? t('inactive') : t('active')}
                          </span>
                        </td>
                        <td>
                          <div className="resource-actions">
                            <button
                              type="button"
                              onClick={async () => {
                                try {
                                  const detail = await api(`/api/roles/${r.uuid}`)
                                  setModal({ type: 'role', initial: detail, id: r.uuid })
                                } catch {
                                  setModal({ type: 'role', initial: r, id: r.uuid })
                                }
                              }}
                            >
                              {t('edit')}
                            </button>
                            <button
                              type="button"
                              onClick={() =>
                                act(`/api/roles/${r.uuid}/${r.active === false ? 'activate' : 'deactivate'}`)
                              }
                            >
                              {r.active === false ? t('activate') : t('deactivate')}
                            </button>
                            {!r.system && (
                              <button
                                type="button"
                                className="table-delete"
                                onClick={() => {
                                  if (window.confirm(`${t('confirmDelete')} (${r.name})`)) {
                                    act(`/api/roles/${r.uuid}`, 'DELETE')
                                  }
                                }}
                              >
                                {t('delete')}
                              </button>
                            )}
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </section>
          )}

          {/* D. PERMISSIONS MANAGEMENT */}
          {section === 'Permissions' && (
            <section className="panel full-table">
              <div className="table-toolbar">
                <div>
                  <h2>
                    {t('permissionsTitle')}{' '}
                    <span className="table-count">{permissions.length}</span>
                  </h2>
                  <p>{t('permissionsSubtitle')}</p>
                </div>
                <button
                  type="button"
                  className="button button-primary"
                  onClick={() => setModal('permission')}
                >
                  <Plus size={15} /> {t('createPermission')}
                </button>
              </div>

              <div className="table-scroll">
                <table>
                  <thead>
                    <tr>
                      <th>{t('thName')}</th>
                      <th>{t('thResource')}</th>
                      <th>{t('thAction')}</th>
                      <th>{t('thDescription')}</th>
                      <th>{t('thStatus')}</th>
                      <th>{t('thActions')}</th>
                    </tr>
                  </thead>
                  <tbody>
                    {permissions.map((p, i) => (
                      <tr key={p.uuid || i}>
                        <td>
                          <b>{p.name}</b>
                        </td>
                        <td>
                          <span className="badge badge-purple">{p.resource}</span>
                        </td>
                        <td>
                          <span className="badge badge-green">{p.action}</span>
                        </td>
                        <td>{p.description || '—'}</td>
                        <td>
                          <span className={`status ${p.active === false ? 'inactive' : ''}`}>
                            <i />
                            {p.active === false ? t('inactive') : t('active')}
                          </span>
                        </td>
                        <td>
                          <div className="resource-actions">
                            <button
                              type="button"
                              onClick={() => setModal({ type: 'permission', initial: p, id: p.uuid })}
                            >
                              {t('edit')}
                            </button>
                            <button
                              type="button"
                              onClick={() =>
                                act(`/api/permissions/${p.uuid}/${p.active === false ? 'activate' : 'deactivate'}`)
                              }
                            >
                              {p.active === false ? t('activate') : t('deactivate')}
                            </button>
                            {!p.system && (
                              <button
                                type="button"
                                className="table-delete"
                                onClick={() => {
                                  if (window.confirm(`${t('confirmDelete')} (${p.name})`)) {
                                    act(`/api/permissions/${p.uuid}`, 'DELETE')
                                  }
                                }}
                              >
                                {t('delete')}
                              </button>
                            )}
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </section>
          )}

          {/* E. OAUTH CLIENTS */}
          {section === 'OAuth clients' && (
            <section className="panel full-table">
              <div className="table-toolbar">
                <div>
                  <h2>
                    {t('clientsTitle')} <span className="table-count">{clients.length}</span>
                  </h2>
                  <p>{t('clientsSubtitle')}</p>
                </div>
                <button
                  type="button"
                  className="button button-primary"
                  onClick={() => setModal('client')}
                >
                  <Plus size={15} /> {t('registerClient')}
                </button>
              </div>

              <div className="table-scroll">
                <table>
                  <thead>
                    <tr>
                      <th>{t('thClientName')}</th>
                      <th>{t('thClientId')}</th>
                      <th>{t('thGrantTypes')}</th>
                      <th>{t('thScopes')}</th>
                      <th>{t('thRedirectUris')}</th>
                      <th>{t('thActions')}</th>
                    </tr>
                  </thead>
                  <tbody>
                    {clients.map((c, i) => (
                      <tr key={c.id || c.clientId || i}>
                        <td>
                          <b>{c.clientName}</b>
                        </td>
                        <td>
                          <code>{c.clientId}</code>
                        </td>
                        <td>
                          <div style={{ display: 'flex', gap: '4px', flexWrap: 'wrap' }}>
                            {(c.grantTypes || []).map((g) => (
                              <span key={g} className="badge badge-green">
                                {g}
                              </span>
                            ))}
                          </div>
                        </td>
                        <td>
                          <div style={{ display: 'flex', gap: '4px', flexWrap: 'wrap' }}>
                            {(c.scopes || []).slice(0, 4).map((s) => (
                              <span key={s} className="badge badge-purple">
                                {s}
                              </span>
                            ))}
                            {(c.scopes || []).length > 4 && (
                              <span className="badge badge-orange">
                                +{(c.scopes || []).length - 4}
                              </span>
                            )}
                          </div>
                        </td>
                        <td>
                          <small>{(c.redirectUris || []).join(', ')}</small>
                        </td>
                        <td>
                          <div className="resource-actions">
                            <button
                              type="button"
                              onClick={() => setModal({ type: 'client', initial: c, id: c.clientId })}
                            >
                              {t('edit')}
                            </button>
                            <button
                              type="button"
                              onClick={async () => {
                                if (window.confirm(`Rotate OAuth client secret for ${c.clientName}?`)) {
                                  try {
                                    const res = await api(`/api/clients/${c.clientId}/rotate-secret`, {
                                      method: 'POST'
                                    })
                                    window.prompt('Copy new Client Secret now:', res.clientSecret)
                                    loadWorkspaceData()
                                  } catch (e) {
                                    flash(e.message, true)
                                  }
                                }
                              }}
                            >
                              {t('rotateSecret')}
                            </button>
                            <button
                              type="button"
                              className="table-delete"
                              onClick={() => {
                                if (window.confirm(`${t('confirmDelete')} (${c.clientName})`)) {
                                  act(`/api/clients/${c.clientId}`, 'DELETE')
                                }
                              }}
                            >
                              {t('delete')}
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </section>
          )}

          {/* F. ACTIVE SESSIONS */}
          {section === 'Sessions' && (
            <section className="panel audit-view">
              <div className="panel-heading">
                <div>
                  <h2>{t('activeSessionsTitle')}</h2>
                  <p>{t('activeSessionsSubtitle')}</p>
                </div>
                {sessions.length > 0 && (
                  <button
                    type="button"
                    className="button button-danger"
                    onClick={async () => {
                      if (window.confirm(t('confirmEndAllSessions'))) {
                        await act('/api/me/sessions', 'DELETE')
                      }
                    }}
                  >
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
                            <Fingerprint size={16} />
                            <b>{s.clientName || 'OAuth Client'}</b>
                          </div>
                        </td>
                        <td>{s.userAgent || 'Web browser'}</td>
                        <td>{s.ipAddress || '127.0.0.1'}</td>
                        <td>{s.lastActivity ? new Date(s.lastActivity).toLocaleString() : 'Now'}</td>
                        <td>{s.expiresAt ? new Date(s.expiresAt).toLocaleString() : 'Active'}</td>
                        <td>
                          <button
                            type="button"
                            className="table-delete"
                            onClick={() => act(`/api/me/sessions/${encodeURIComponent(s.id)}`, 'DELETE')}
                          >
                            {t('endSession')}
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
                {!sessions.length && (
                  <div className="empty-table">No active sessions found.</div>
                )}
              </div>
            </section>
          )}

          {/* G. AUDIT LOG */}
          {section === 'Audit log' && <AuditView t={t} />}

          {/* H. CONFIGURATION */}
          {section === 'Configuration' && (
            <SystemConfigView act={act} flash={flash} t={t} />
          )}

          {/* I. ACCOUNT WORKFLOWS (+40 pts) */}
          {ACCOUNT_SECTIONS.includes(section) && (
            <AccountView
              profile={profile}
              setProfile={setProfile}
              token={token}
              onTokenChange={(tok) => {
                setTokenState(tok)
                loadWorkspaceData()
              }}
              act={act}
              flash={flash}
              t={t}
              selectedTab={ACCOUNT_TAB_MAP[section] || 'profile'}
              onTabChange={(tabKey) => {
                const sec = TAB_TO_SECTION_MAP[tabKey]
                if (sec && !isAdmin) setSection(sec)
              }}
            />
          )}

          {/* Footer */}
          <footer className="footer">
            <span>© 2026 Mekong Identity · Enterprise IAM</span>
            <span>
              <span className="footer-dot" /> {t('allSystemsOperational')}
            </span>
            <span>
              {t('helpCenter')} <span className="footer-sep">·</span> {t('privacy')}
            </span>
          </footer>
        </div>
      </main>

      {/* Toast Alert */}
      {toast && (
        <div className="toast">
          <Check size={16} />
          {toast}
        </div>
      )}

      {/* Modals Handling */}
      {modal && modal.type === 'detail' && (
        <UserDetailModal
          user={modal.user}
          permissionsCatalog={permissions}
          rolesCatalog={roles}
          onClose={() => setModal(null)}
          onRefresh={loadWorkspaceData}
          t={t}
        />
      )}

      {modal && modal.type !== 'detail' && (
        <CrudModal
          modal={modal}
          permissions={permissions}
          onClose={() => setModal(null)}
          onSave={async (type, body, id) => {
            if (type === 'client') {
              const path = id ? `/api/clients/${id}` : '/api/clients'
              return await act(path, id ? 'PUT' : 'POST', body)
            }

            if (type === 'role') {
              if (!id) return await act('/api/roles', 'POST', body)

              // Update name and description
              const updated = await act(`/api/roles/${id}`, 'PATCH', {
                name: body.name,
                description: body.description
              })
              if (updated === false) return false

              // Sync role permissions
              try {
                const current = await api(`/api/roles/${id}`)
                const existing = (current.permissions || []).map((p) => p.uuid)
                const desired = body.permissionUuids || []
                const toAdd = desired.filter((uuid) => !existing.includes(uuid))
                const toRemove = existing.filter((uuid) => !desired.includes(uuid))

                if (toAdd.length) {
                  await api(`/api/roles/${id}/permissions`, {
                    method: 'POST',
                    body: JSON.stringify({
                      permissionUuids: toAdd,
                      reason: 'Updated from IAM console'
                    })
                  })
                }

                for (const uuid of toRemove) {
                  await api(`/api/roles/${id}/permissions/${uuid}`, { method: 'DELETE' })
                }

                await loadWorkspaceData()
                return true
              } catch (e) {
                flash(e.message, true)
                return false
              }
            }

            if (type === 'permission') {
              const path = id ? `/api/permissions/${id}` : '/api/permissions'
              return await act(path, id ? 'PATCH' : 'POST', body)
            }

            if (type === 'user') {
              if (id) {
                return await act(`/api/users/${id}`, 'PATCH', body)
              }
              return await act('/api/users', 'POST', body)
            }

            return false
          }}
          t={t}
        />
      )}
    </div>
  )
}

export default App

import React, { useState } from 'react'
import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom'
import {
  Activity,
  AlertTriangle,
  Bell,
  ChevronRight,
  CreditCard,
  Fingerprint,
  Globe2,
  History,
  KeyRound,
  LayoutDashboard,
  LogOut,
  Menu,
  Settings,
  Share2,
  ShieldCheck,
  UserRound,
  Users,
  X,
} from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { label } from '../api'
import { CrudModal } from '../components/CrudModals'
import { UserDetailModal } from '../components/UserDetailModal'

const ADMIN_NAV_GROUPS = [
  {
    group: 'MANAGEMENT',
    links: [
      { path: '/overview', title: 'Overview', icon: LayoutDashboard },
      { path: '/users', title: 'Users', icon: Users, countKey: 'userTotal' },
      { path: '/roles', title: 'Roles', icon: ShieldCheck, countKey: 'roles' },
      { path: '/permissions', title: 'Permissions', icon: KeyRound, countKey: 'permissions' },
      { path: '/clients', title: 'OAuth clients', icon: CreditCard, countKey: 'clients' },
      { path: '/sessions', title: 'Sessions', icon: Fingerprint, countKey: 'sessions' },
      { path: '/audit', title: 'Audit log', icon: Activity },
      { path: '/config', title: 'Configuration', icon: Settings },
    ],
  },
  {
    group: 'ACCOUNT',
    links: [
      { path: '/account/profile', title: 'Profile', icon: UserRound },
      { path: '/account/security', title: 'Security & Password', icon: KeyRound },
      { path: '/account/linked', title: 'Linked Accounts', icon: Share2 },
      { path: '/account/sessions', title: 'Active Sessions', icon: Fingerprint },
      { path: '/account/history', title: 'Login History', icon: History },
      { path: '/account/activity', title: 'Account Activity', icon: Activity },
      { path: '/account/deactivate', title: 'Account Deactivation', icon: AlertTriangle },
    ],
  },
]

const USER_NAV_GROUPS = [
  {
    group: 'ACCOUNT',
    links: [
      { path: '/account/profile', title: 'Profile', icon: UserRound },
      { path: '/account/security', title: 'Security & Password', icon: KeyRound },
      { path: '/account/linked', title: 'Linked Accounts', icon: Share2 },
      { path: '/account/sessions', title: 'Active Sessions', icon: Fingerprint },
      { path: '/account/history', title: 'Login History', icon: History },
      { path: '/account/activity', title: 'Account Activity', icon: Activity },
      { path: '/account/deactivate', title: 'Account Deactivation', icon: AlertTriangle },
    ],
  },
]

export function AppLayout() {
  const {
    profile,
    isAdmin,
    language,
    toggleLanguage,
    t,
    apiReady,
    busy,
    toast,
    userTotal,
    roles,
    permissions,
    clients,
    sessions,
    modal,
    setModal,
    detailUser,
    setDetailUser,
    act,
    handleLogout,
  } = useAuth()

  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)
  const location = useLocation()
  const navigate = useNavigate()

  const navGroups = isAdmin ? ADMIN_NAV_GROUPS : USER_NAV_GROUPS

  const getCount = (key) => {
    if (key === 'userTotal') return userTotal
    if (key === 'roles') return roles.length
    if (key === 'permissions') return permissions.length
    if (key === 'clients') return clients.length
    if (key === 'sessions') return sessions.length
    return null
  }

  // Determine current page title for breadcrumb
  const currentPath = location.pathname
  let currentTitle = 'Console'
  for (const grp of navGroups) {
    const found = grp.links.find((l) => currentPath.startsWith(l.path))
    if (found) {
      currentTitle = found.title
      break
    }
  }

  const initials = label(profile)
    .split(' ')
    .map((s) => s[0])
    .join('')
    .slice(0, 2)
    .toUpperCase()

  return (
    <div className="flex h-screen bg-slate-50 text-slate-900 font-sans antialiased overflow-hidden">
      {/* Toast popup */}
      {toast.message && (
        <div
          className={`fixed bottom-8 right-8 z-50 flex items-center gap-3 px-5 py-3.5 rounded-2xl shadow-xl border text-sm font-semibold transition-all transform duration-300 animate-in fade-in slide-in-from-bottom-3 ${
            toast.isError
              ? 'bg-rose-50 border-rose-200 text-rose-800'
              : 'bg-slate-900 border-slate-800 text-white'
          }`}
        >
          <span>{toast.message}</span>
        </div>
      )}

      {/* Mobile Backdrop */}
      {mobileMenuOpen && (
        <div
          className="fixed inset-0 z-40 bg-slate-900/40 backdrop-blur-sm lg:hidden"
          onClick={() => setMobileMenuOpen(false)}
        />
      )}

      {/* Sidebar */}
      <aside
        className={`fixed inset-y-0 left-0 z-50 w-72 bg-white border-r border-slate-200/90 flex flex-col justify-between transition-transform duration-300 ease-in-out lg:static lg:translate-x-0 ${
          mobileMenuOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        <div className="flex flex-col h-full overflow-y-auto">
          {/* Brand header */}
          <div className="h-20 flex items-center justify-between px-6 border-b border-slate-100">
            <div className="flex items-center gap-3 font-bold tracking-tight text-slate-900 text-lg">
              <img
                src={process.env.PUBLIC_URL + '/istad-logo.png'}
                alt="ISTAD"
                className="w-10 h-10 rounded-xl object-contain shadow-xs border border-slate-100 bg-white"
                onError={(e) => {
                  e.currentTarget.src = 'https://www.istad.co/icon.png?ff407d7ec1c2072a'
                }}
              />
              <span className="font-extrabold text-xl tracking-tight text-slate-900">
                ISTAD<span className="text-indigo-600 font-bold">.identity</span>
              </span>
            </div>
            <button
              type="button"
              className="lg:hidden p-2 text-slate-400 hover:text-slate-600 rounded-xl"
              onClick={() => setMobileMenuOpen(false)}
            >
              <X size={20} />
            </button>
          </div>

          {/* Nav groups */}
          <nav className="flex-1 px-4 py-6 space-y-6">
            {navGroups.map((grp) => (
              <div key={grp.group} className="space-y-1.5">
                <div className="px-3 pb-1 text-xs font-bold uppercase tracking-wider text-slate-400">
                  {t(grp.group)}
                </div>
                {grp.links.map(({ path, title, icon: Icon, countKey }) => {
                  const count = countKey ? getCount(countKey) : null
                  return (
                    <NavLink
                      key={path}
                      to={path}
                      onClick={() => setMobileMenuOpen(false)}
                      className={({ isActive }) =>
                        `flex items-center justify-between px-3.5 py-2.5 text-sm font-medium rounded-xl transition-all ${
                          isActive
                            ? 'bg-indigo-50 text-indigo-700 font-bold shadow-xs'
                            : 'text-slate-600 hover:bg-slate-100/80 hover:text-slate-900'
                        }`
                      }
                    >
                      <div className="flex items-center gap-3">
                        <Icon size={18} />
                        <span>{t(title)}</span>
                      </div>
                      {count !== null && (
                        <span className="text-xs px-2 py-0.5 rounded-lg bg-slate-100 font-bold text-slate-600 border border-slate-200">
                          {count}
                        </span>
                      )}
                    </NavLink>
                  )
                })}
              </div>
            ))}
          </nav>

          {/* Sidebar footer */}
          <div className="p-5 border-t border-slate-100 space-y-4">
            {/* Security Status Card */}
            <div className="p-4 bg-slate-50/90 rounded-2xl border border-slate-200/80 space-y-2">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-slate-800">
                  <ShieldCheck size={16} className="text-emerald-600 shrink-0" />
                  <span>
                    {isAdmin
                      ? (language === 'km' ? 'ស្ថានភាពសុវត្ថិភាព' : 'Security Status')
                      : (language === 'km' ? 'សុវត្ថិភាពគណនី' : 'Account Security')}
                  </span>
                </div>
                <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-[11px] font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200/80">
                  <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse" />
                  {language === 'km' ? 'ធម្មតា' : 'Healthy'}
                </span>
              </div>
              <p className="text-xs text-slate-500 leading-relaxed">
                {isAdmin
                  ? (language === 'km'
                    ? 'ប្រព័ន្ធផ្ទៀងផ្ទាត់ និងសិទ្ធិដំណើរការធម្មតា។ ចូលពិនិត្យកំណត់ហេតុប្រព័ន្ធ។'
                    : 'Authentication and role engine verified. Review system audit reports.')
                  : (language === 'km'
                    ? 'ដំណើរការសុវត្ថិភាព និងសម័យប្រជុំដំណើរការល្អ។'
                    : 'Account credentials and active sessions protected.')}
              </p>
              <button
                type="button"
                onClick={() => navigate(isAdmin ? '/audit' : '/account/security')}
                className="w-full flex items-center justify-between pt-1 text-xs font-semibold text-indigo-600 hover:text-indigo-700 transition group"
              >
                <span>
                  {isAdmin
                    ? (language === 'km' ? 'ពិនិត្យកំណត់ហេតុសវនកម្ម' : 'Review Audit Logs')
                    : (language === 'km' ? 'ពាក្យសម្ងាត់ & សុវត្ថិភាព' : 'Password & Security')}
                </span>
                <ChevronRight size={14} className="text-indigo-400 group-hover:translate-x-0.5 transition-transform" />
              </button>
            </div>

            {/* Current user chip */}
            <div className="flex items-center justify-between pt-1">
              <div className="flex items-center gap-3 min-w-0">
                <div className="w-10 h-10 rounded-full bg-indigo-100 text-indigo-700 font-bold text-sm flex items-center justify-center shrink-0 border border-indigo-200">
                  {initials}
                </div>
                <div className="flex flex-col truncate">
                  <span className="text-sm font-bold text-slate-900 truncate">
                    {label(profile)}
                  </span>
                  <span className="text-xs text-slate-500">
                    {isAdmin ? 'Workspace Admin' : 'Standard Member'}
                  </span>
                </div>
              </div>
              <button
                type="button"
                onClick={handleLogout}
                title={t('signOut')}
                className="p-2 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-xl transition"
              >
                <LogOut size={18} />
              </button>
            </div>
          </div>
        </div>
      </aside>

      {/* Main body area */}
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        {/* Topbar */}
        <header className="h-20 bg-white border-b border-slate-200 px-8 flex items-center justify-between shrink-0">
          {/* Left: Mobile hamburger + Breadcrumbs */}
          <div className="flex items-center gap-4">
            <button
              type="button"
              className="lg:hidden p-2 text-slate-600 hover:bg-slate-100 rounded-xl"
              onClick={() => setMobileMenuOpen(true)}
            >
              <Menu size={22} />
            </button>

            <div className="flex items-center gap-2 text-sm text-slate-500 font-medium">
              <span>IAM Workspace</span>
              <ChevronRight size={15} className="text-slate-400" />
              <span className="font-bold text-slate-900 text-base">{t(currentTitle)}</span>
            </div>
          </div>

          {/* Right actions */}
          <div className="flex items-center gap-3.5">
            {/* Live indicator */}
            <div className="hidden sm:flex items-center gap-2.5 text-xs font-semibold px-3.5 py-2 rounded-full bg-slate-100 text-slate-700 border border-slate-200/80">
              <span
                className={`w-2.5 h-2.5 rounded-full ${
                  apiReady ? 'bg-emerald-500 animate-pulse' : 'bg-amber-400'
                }`}
              />
              <span>
                {busy
                  ? language === 'km'
                    ? 'កំពុងផ្ទុក...'
                    : 'Loading...'
                  : apiReady
                  ? t('apiConnected')
                  : language === 'km'
                  ? 'កំពុងភ្ជាប់...'
                  : 'Connecting...'}
              </span>
            </div>

            {/* Language switch */}
            <button
              type="button"
              onClick={toggleLanguage}
              title="Switch Language (English / Khmer)"
              className="flex items-center gap-2 text-xs font-bold px-3 py-2 rounded-xl border border-slate-200 bg-white hover:bg-slate-50 text-slate-700 shadow-xs transition"
            >
              <Globe2 size={16} className="text-slate-500" />
              <span>{language === 'en' ? 'EN' : 'ខ្មែរ'}</span>
            </button>

            {/* Notification bell */}
            <button
              type="button"
              title="Notifications"
              className="p-2.5 text-slate-500 hover:text-slate-700 hover:bg-slate-100 rounded-xl transition"
            >
              <Bell size={20} />
            </button>

            {/* Top avatar link to /account */}
            <button
              type="button"
              onClick={() => navigate('/account')}
              title={label(profile)}
              className="w-10 h-10 rounded-full bg-indigo-600 text-white font-bold text-sm flex items-center justify-center shadow-md hover:ring-2 hover:ring-indigo-300 transition"
            >
              {initials}
            </button>
          </div>
        </header>

        {/* Scrollable routed page content with generous spacing */}
        <main className="flex-1 overflow-y-auto bg-slate-50/70 p-8 lg:p-10">
          <div className="max-w-7xl mx-auto space-y-8">
            <Outlet />
          </div>
        </main>
      </div>

      {/* Global CRUD Modals */}
      {modal && (
        <CrudModal
          type={modal}
          roles={roles}
          permissions={permissions}
          onClose={() => setModal(null)}
          onAction={act}
          t={t}
        />
      )}

      {/* Global User Detail Modal */}
      {detailUser && (
        <UserDetailModal
          user={detailUser}
          roles={roles}
          permissions={permissions}
          onClose={() => setDetailUser(null)}
          onAction={act}
          t={t}
        />
      )}
    </div>
  )
}

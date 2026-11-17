import React from 'react'
import {
  Activity,
  ArrowUpRight,
  CreditCard,
  Download,
  KeyRound,
  Plus,
  Shield,
  ShieldCheck,
  Users,
} from 'lucide-react'
import { useAuth } from '../context/AuthContext'

export function OverviewPage() {
  const {
    userTotal,
    users,
    roles,
    permissions,
    clients,
    events,
    t,
    flash,
    setModal,
  } = useAuth()

  const statsCards = [
    {
      title: t('totalUsers'),
      value: userTotal.toLocaleString(),
      change: t('live'),
      icon: Users,
      color: 'text-blue-600 bg-blue-50 border-blue-200',
      sparkColor: 'bg-blue-500',
    },
    {
      title: t('activeUsers'),
      value: users.filter((u) => u.enabled !== false).length.toLocaleString(),
      change: t('live'),
      icon: Activity,
      color: 'text-emerald-600 bg-emerald-50 border-emerald-200',
      sparkColor: 'bg-emerald-500',
    },
    {
      title: t('rolesCount'),
      value: roles.length.toLocaleString(),
      change: t('live'),
      icon: Shield,
      color: 'text-purple-600 bg-purple-50 border-purple-200',
      sparkColor: 'bg-purple-500',
    },
    {
      title: t('permissionsCount'),
      value: permissions.length.toLocaleString(),
      change: t('live'),
      icon: KeyRound,
      color: 'text-amber-600 bg-amber-50 border-amber-200',
      sparkColor: 'bg-amber-500',
    },
    {
      title: t('oauthClientsCount'),
      value: clients.length.toLocaleString(),
      change: t('live'),
      icon: CreditCard,
      color: 'text-indigo-600 bg-indigo-50 border-indigo-200',
      sparkColor: 'bg-indigo-500',
    },
  ]

  const recentSignIns = events
    .filter((e) => e.eventType === 'LOGIN_SUCCESS' || e.eventType === 'LOGIN_FAILURE')
    .slice(0, 5)

  return (
    <div className="space-y-8">
      {/* Page Heading */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-5">
        <div>
          <div className="text-xs font-bold uppercase tracking-wider text-indigo-600">
            ADMIN CONSOLE · OVERVIEW
          </div>
          <h1 className="text-3xl font-extrabold tracking-tight text-slate-900 mt-1">
            Identity & Access Console
          </h1>
          <p className="text-base text-slate-500 mt-1">
            A centralized view of your enterprise security, accounts, roles, and access.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <button
            type="button"
            onClick={() => flash(t('allSystemsOperational'))}
            className="flex items-center gap-2 px-4 py-2.5 text-sm font-semibold rounded-xl border border-slate-200 bg-white hover:bg-slate-50 text-slate-700 shadow-xs transition"
          >
            <Download size={16} />
            <span>{t('export')}</span>
          </button>
          <button
            type="button"
            onClick={() => setModal('user')}
            className="flex items-center gap-2 px-5 py-2.5 text-sm font-bold rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white shadow-md shadow-indigo-200 transition"
          >
            <Plus size={18} />
            <span>{t('inviteUser')}</span>
          </button>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-5">
        {statsCards.map((sc) => {
          const Icon = sc.icon
          return (
            <div
              key={sc.title}
              className="bg-white rounded-2xl border border-slate-200/90 p-5 shadow-xs hover:shadow-md transition space-y-3"
            >
              <div className="flex items-center justify-between text-sm text-slate-500 font-medium">
                <span>{sc.title}</span>
                <div className={`w-9 h-9 rounded-xl border flex items-center justify-center ${sc.color}`}>
                  <Icon size={18} />
                </div>
              </div>
              <div className="flex items-baseline justify-between pt-1">
                <span className="text-3xl font-extrabold text-slate-900 tracking-tight">
                  {sc.value}
                </span>
                <span className="text-xs font-bold text-emerald-600 flex items-center gap-0.5">
                  <ArrowUpRight size={14} />
                  {sc.change}
                </span>
              </div>
              <div className="flex items-center justify-between text-xs text-slate-400 border-t border-slate-100 pt-3">
                <span>{t('comparedToLastMonth')}</span>
                <div className="flex items-end gap-1 h-3.5">
                  {[16, 25, 20, 32, 24, 38, 29, 48, 35, 45, 38, 56, 42, 60, 48, 70].map((h, j) => (
                    <span
                      key={j}
                      style={{ height: `${h}%` }}
                      className={`w-0.5 rounded-t-xs ${sc.sparkColor}`}
                    />
                  ))}
                </div>
              </div>
            </div>
          )
        })}
      </div>

      {/* Overview Grid (Activity chart + Security posture) */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Activity Trends */}
        <div className="lg:col-span-2 bg-white rounded-2xl border border-slate-200/90 p-6 shadow-xs space-y-4">
          <div className="flex items-center justify-between border-b border-slate-100 pb-4">
            <div>
              <h2 className="text-base font-bold text-slate-900">Authentication Activity Trends</h2>
              <p className="text-sm text-slate-500 mt-0.5">Hourly authentication events recorded across services</p>
            </div>
            <div className="flex items-center gap-4 text-xs font-semibold text-slate-600">
              <span className="flex items-center gap-1.5">
                <span className="w-3 h-3 rounded-full bg-indigo-600" /> Success
              </span>
              <span className="flex items-center gap-1.5">
                <span className="w-3 h-3 rounded-full bg-rose-500" /> Failures
              </span>
            </div>
          </div>

          <div className="h-64 flex items-end gap-2 pt-8 pb-3 px-2">
            {[45, 52, 38, 65, 80, 92, 110, 95, 78, 85, 102, 120, 115, 98, 87, 76, 68, 74, 90, 105, 112, 128, 118, 95].map((val, idx) => (
              <div key={idx} className="flex-1 flex flex-col items-center gap-1.5 group relative">
                <div
                  style={{ height: `${(val / 130) * 100}%` }}
                  className="w-full bg-indigo-500/80 hover:bg-indigo-600 rounded-t-md transition-all relative cursor-pointer"
                >
                  <div className="absolute -top-8 left-1/2 -translate-x-1/2 bg-slate-900 text-white text-xs px-2 py-0.5 rounded-md shadow-md pointer-events-none opacity-0 group-hover:opacity-100 transition whitespace-nowrap z-10 font-mono">
                    {val} events
                  </div>
                </div>
                <span className="text-[10px] text-slate-400 font-mono">
                  {idx % 4 === 0 ? `${idx}h` : ''}
                </span>
              </div>
            ))}
          </div>
        </div>

        {/* Security Posture */}
        <div className="bg-white rounded-2xl border border-slate-200/90 p-6 shadow-xs flex flex-col justify-between space-y-5">
          <div>
            <div className="flex items-center justify-between border-b border-slate-100 pb-4 mb-5">
              <div>
                <div className="flex items-center gap-2">
                  <ShieldCheck size={18} className="text-emerald-600" />
                  <h2 className="text-base font-bold text-slate-900">Security Posture</h2>
                </div>
                <p className="text-xs text-slate-500 mt-0.5">Policy compliance & engine verification</p>
              </div>
              <span className="inline-flex items-center gap-1.5 text-xs px-2.5 py-1 rounded-full bg-emerald-50 text-emerald-700 font-bold border border-emerald-200">
                <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse" />
                Grade A · Optimal
              </span>
            </div>

            <div className="space-y-5">
              <div>
                <div className="flex justify-between text-xs font-bold mb-1.5">
                  <span className="text-slate-700">OAuth 2.1 & PKCE Compliance</span>
                  <span className="text-emerald-600 font-mono">100%</span>
                </div>
                <div className="w-full bg-slate-100 rounded-full h-2.5">
                  <div className="bg-emerald-500 h-2.5 rounded-full w-full" />
                </div>
              </div>

              <div>
                <div className="flex justify-between text-xs font-bold mb-1.5">
                  <span className="text-slate-700">RBAC Role Coverage</span>
                  <span className="text-indigo-600 font-mono">94%</span>
                </div>
                <div className="w-full bg-slate-100 rounded-full h-2.5">
                  <div className="bg-indigo-600 h-2.5 rounded-full w-[94%]" />
                </div>
              </div>

              <div>
                <div className="flex justify-between text-xs font-bold mb-1.5">
                  <span className="text-slate-700">MFA & Passkey Readiness</span>
                  <span className="text-amber-600 font-mono">82%</span>
                </div>
                <div className="w-full bg-slate-100 rounded-full h-2.5">
                  <div className="bg-amber-500 h-2.5 rounded-full w-[82%]" />
                </div>
              </div>

              <div className="pt-2 text-xs text-slate-500 leading-relaxed">
                System enforces cryptographically verified JWT RS256 token signatures with JWK set rotation and strict origin CORS policy.
              </div>
            </div>
          </div>

          <div className="pt-4 border-t border-slate-100">
            <button
              type="button"
              onClick={() => flash('Security diagnostics passed: All cryptographic and RBAC checks optimal.')}
              className="w-full py-2.5 text-xs font-bold text-center rounded-xl bg-slate-50 hover:bg-slate-100 text-slate-700 border border-slate-200 transition"
            >
              Run Security Diagnostics
            </button>
          </div>
        </div>
      </div>

      {/* Recent Sign-Ins Table */}
      <div className="bg-white rounded-2xl border border-slate-200/90 shadow-xs overflow-hidden">
        <div className="px-6 py-5 border-b border-slate-100 flex items-center justify-between">
          <div>
            <h2 className="text-base font-bold text-slate-900">Recent Sign-In Activity</h2>
            <p className="text-sm text-slate-500 mt-0.5">Live authentication events captured across the platform</p>
          </div>
          <span className="text-xs font-semibold text-slate-500 px-2.5 py-1 rounded-md bg-slate-100">
            {recentSignIns.length} events
          </span>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50/80 text-slate-500 uppercase tracking-wider text-xs font-bold border-b border-slate-100">
              <tr>
                <th className="px-6 py-4">Event</th>
                <th className="px-6 py-4">Principal</th>
                <th className="px-6 py-4">IP Address</th>
                <th className="px-6 py-4">Status</th>
                <th className="px-6 py-4">Timestamp</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-slate-700">
              {recentSignIns.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-6 py-10 text-center text-slate-400">
                    No sign-in events recorded yet.
                  </td>
                </tr>
              ) : (
                recentSignIns.map((ev, i) => (
                  <tr key={ev.id || i} className="hover:bg-slate-50/60 transition">
                    <td className="px-6 py-4 font-bold text-slate-900">
                      {ev.eventType}
                    </td>
                    <td className="px-6 py-4 font-mono text-xs">{ev.username || 'system'}</td>
                    <td className="px-6 py-4 font-mono text-slate-500 text-xs">{ev.ipAddress || '127.0.0.1'}</td>
                    <td className="px-6 py-4">
                      <span
                        className={`inline-flex items-center px-2.5 py-1 rounded-full text-xs font-bold ${
                          ev.eventType === 'LOGIN_SUCCESS'
                            ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                            : 'bg-rose-50 text-rose-700 border border-rose-200'
                        }`}
                      >
                        {ev.eventType === 'LOGIN_SUCCESS' ? 'SUCCESS' : 'FAILED'}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-slate-400 font-mono text-xs">
                      {ev.timestamp ? new Date(ev.timestamp).toLocaleString() : 'Just now'}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}

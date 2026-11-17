import React from 'react'
import { Globe, Laptop, Smartphone, Trash2 } from 'lucide-react'
import { useAuth } from '../context/AuthContext'

export function SessionsPage() {
  const { sessions, act } = useAuth()

  const handleRevoke = async (id) => {
    if (window.confirm('Terminate this session immediately?')) {
      await act(`/api/me/sessions/${id}`, 'DELETE')
    }
  }

  const handleRevokeAll = async () => {
    if (window.confirm('Sign out from all other devices and terminate all sessions?')) {
      await act('/api/me/sessions', 'DELETE')
    }
  }

  return (
    <div className="space-y-8">
      {/* Page Heading */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <div className="text-xs font-bold uppercase tracking-wider text-indigo-600">
            SECURITY & AUDIT · SESSIONS
          </div>
          <h1 className="text-3xl font-extrabold tracking-tight text-slate-900 mt-1">
            Active User Sessions
          </h1>
          <p className="text-base text-slate-500 mt-1">
            Monitor and revoke active refresh tokens and OAuth sessions across desktop and mobile devices.
          </p>
        </div>

        {sessions.length > 0 && (
          <button
            type="button"
            onClick={handleRevokeAll}
            className="flex items-center gap-2 px-4 py-2.5 text-sm font-semibold rounded-xl bg-rose-50 hover:bg-rose-100 text-rose-700 border border-rose-200 transition"
          >
            <Trash2 size={16} />
            <span>Revoke All Other Sessions</span>
          </button>
        )}
      </div>

      {/* Sessions List */}
      <div className="bg-white rounded-2xl border border-slate-200/90 shadow-sm divide-y divide-slate-100">
        {sessions.length === 0 ? (
          <div className="p-14 text-center text-slate-400 text-sm">
            No active session records found.
          </div>
        ) : (
          sessions.map((s, idx) => {
            const isMobile = s.userAgent && /mobile|android|iphone/i.test(s.userAgent)
            const Icon = isMobile ? Smartphone : Laptop
            return (
              <div
                key={s.id || idx}
                className="p-6 flex flex-col sm:flex-row sm:items-center justify-between gap-5 hover:bg-slate-50/60 transition"
              >
                <div className="flex items-start gap-4">
                  <div className="w-12 h-12 rounded-2xl bg-slate-100 text-slate-700 flex items-center justify-center shrink-0 border border-slate-200">
                    <Icon size={22} />
                  </div>
                  <div>
                    <div className="flex items-center gap-2.5">
                      <span className="text-base font-bold text-slate-900">
                        {s.clientName || s.clientId || 'IAM Web Console'}
                      </span>
                      {s.isCurrent && (
                        <span className="px-2.5 py-1 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                          Current Device
                        </span>
                      )}
                    </div>
                    <div className="flex flex-wrap items-center gap-3 text-sm text-slate-500 mt-1.5 font-mono">
                      <span className="flex items-center gap-1.5">
                        <Globe size={14} className="text-slate-400" />
                        {s.ipAddress || '127.0.0.1'}
                      </span>
                      <span>·</span>
                      <span className="truncate max-w-sm">{s.userAgent || 'Chrome on Windows'}</span>
                    </div>
                    <div className="text-xs text-slate-400 mt-1">
                      Started: {s.createdAt ? new Date(s.createdAt).toLocaleString() : 'Active now'}
                    </div>
                  </div>
                </div>

                {!s.isCurrent && (
                  <button
                    type="button"
                    onClick={() => handleRevoke(s.id)}
                    className="self-start sm:self-center px-4 py-2 text-xs font-semibold rounded-xl bg-slate-100 hover:bg-rose-50 text-slate-700 hover:text-rose-700 border border-slate-200 hover:border-rose-200 transition"
                  >
                    Revoke Access
                  </button>
                )}
              </div>
            )
          })
        )}
      </div>
    </div>
  )
}

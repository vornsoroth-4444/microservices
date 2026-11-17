import React, { useState, useEffect } from 'react'
import { Search, RefreshCw, AlertCircle } from 'lucide-react'
import { api, pageItems } from '../api'

const EVENT_TYPES = [
  { value: '', label: 'All event types' },
  { value: 'LOGIN_SUCCESS', label: 'Successful sign-in (LOGIN_SUCCESS)' },
  { value: 'LOGIN_FAILURE', label: 'Failed sign-in (LOGIN_FAILURE)' },
  { value: 'USER_CREATE', label: 'User created' },
  { value: 'USER_UPDATE', label: 'User updated' },
  { value: 'USER_DELETE', label: 'User deleted' },
  { value: 'USER_ROLE_ASSIGN', label: 'Role assigned' },
  { value: 'USER_ROLE_REVOKE', label: 'Role revoked' },
  { value: 'PERMISSION_GRANT', label: 'Permission granted' },
  { value: 'CONFIG_UPDATE', label: 'Configuration updated' },
]

export function AuditView({ t }) {
  const [logs, setLogs] = useState([])
  const [search, setSearch] = useState('')
  const [selectedType, setSelectedType] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const loadLogs = async () => {
    setLoading(true)
    setError('')
    try {
      const params = new URLSearchParams()
      if (search.trim()) params.append('search', search.trim())
      if (selectedType) params.append('type', selectedType)
      params.append('size', '100')

      const result = await api(`/api/audit-logs?${params}`)
      setLogs(pageItems(result))
    } catch (e) {
      setError(e.message)
      setLogs([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadLogs()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedType])

  const handleSearchKeyDown = (e) => {
    if (e.key === 'Enter') loadLogs()
  }

  return (
    <div className="space-y-6">
      {/* Toolbar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-100">
        <div>
          <div className="flex items-center gap-2.5">
            <h2 className="text-base font-bold text-slate-900">{t('systemAuditLog')}</h2>
            <span className="text-xs font-bold px-2.5 py-0.5 rounded-full bg-slate-100 text-slate-700 border border-slate-200">
              {logs.length}
            </span>
          </div>
          <p className="text-sm text-slate-500 mt-1">{t('auditSubtitle')}</p>
        </div>

        <div className="flex flex-wrap items-center gap-2.5">
          <div className="relative">
            <Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              onKeyDown={handleSearchKeyDown}
              placeholder={t('searchLogs')}
              className="pl-9 pr-3 py-2 text-sm rounded-xl border border-slate-200 bg-slate-50/50 focus:bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 transition w-48"
            />
          </div>

          <select
            value={selectedType}
            onChange={(e) => setSelectedType(e.target.value)}
            className="px-3 py-2 text-sm rounded-xl border border-slate-200 bg-white text-slate-700 focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500"
          >
            {EVENT_TYPES.map((et) => (
              <option key={et.value} value={et.value}>
                {et.label}
              </option>
            ))}
          </select>

          <button
            type="button"
            onClick={loadLogs}
            disabled={loading}
            className="flex items-center gap-2 px-4 py-2 text-sm font-semibold rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 transition disabled:opacity-50"
          >
            <RefreshCw size={14} className={loading ? 'animate-spin' : ''} />
            <span>{t('refresh')}</span>
          </button>
        </div>
      </div>

      {error && (
        <div className="p-4 rounded-xl bg-rose-50 border border-rose-200 text-rose-700 text-sm flex items-center gap-2.5">
          <AlertCircle size={16} />
          <span>
            {error.includes('401') || error.includes('403')
              ? 'Sign in with an account having user:read or admin authority to inspect system audit logs.'
              : error}
          </span>
        </div>
      )}

      {/* Audit Log Table */}
      <div className="overflow-x-auto rounded-xl border border-slate-100">
        <table className="w-full text-left text-sm">
          <thead className="bg-slate-50/80 text-slate-500 uppercase tracking-wider text-xs font-bold border-b border-slate-100">
            <tr>
              <th className="px-5 py-4">{t('thStatus')}</th>
              <th className="px-5 py-4">{t('thEvent')}</th>
              <th className="px-5 py-4">{t('thActor')}</th>
              <th className="px-5 py-4">{t('thTarget')}</th>
              <th className="px-5 py-4">Details</th>
              <th className="px-5 py-4">{t('thIpAddress')}</th>
              <th className="px-5 py-4">{t('thDevice')}</th>
              <th className="px-5 py-4">{t('thTimestamp')}</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100 text-slate-700">
            {logs.length === 0 ? (
              <tr>
                <td colSpan={8} className="px-5 py-14 text-center text-slate-400 text-sm">
                  No audit events match your filter criteria.
                </td>
              </tr>
            ) : (
              logs.map((log, idx) => {
                const isFail = log.succeeded === false
                return (
                  <tr key={log.id || idx} className="hover:bg-slate-50/60 transition">
                    <td className="px-5 py-4">
                      <span
                        className={`inline-flex items-center px-2.5 py-1 rounded-full text-xs font-bold ${
                          isFail
                            ? 'bg-rose-50 text-rose-700 border border-rose-200'
                            : 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                        }`}
                      >
                        {isFail ? t('failed') : t('success')}
                      </span>
                    </td>
                    <td className="px-5 py-4 font-semibold text-slate-900 font-mono text-xs">
                      {log.type}
                    </td>
                    <td className="px-5 py-4 font-mono text-xs text-slate-600">
                      {log.actor || 'System'}
                    </td>
                    <td className="px-5 py-4 font-mono text-xs text-slate-600">
                      {log.target || '—'}
                    </td>
                    <td className="px-5 py-4 max-w-xs truncate text-slate-600 text-xs" title={log.detail}>
                      {log.detail || '—'}
                    </td>
                    <td className="px-5 py-4 font-mono text-xs text-slate-500">{log.ipAddress || '—'}</td>
                    <td className="px-5 py-4 max-w-[140px] truncate text-slate-400 font-mono text-xs" title={log.userAgent}>
                      {log.userAgent || '—'}
                    </td>
                    <td className="px-5 py-4 font-mono text-xs text-slate-400 whitespace-nowrap">
                      {log.createdAt ? new Date(log.createdAt).toLocaleString() : ''}
                    </td>
                  </tr>
                )
              })
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}

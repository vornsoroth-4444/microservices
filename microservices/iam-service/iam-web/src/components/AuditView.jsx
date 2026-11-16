import React, { useState, useEffect } from 'react'
import { Search, RefreshCw } from 'lucide-react'
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
  { value: 'CONFIG_UPDATE', label: 'Configuration updated' }
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
    <section className="panel full-table">
      <div className="table-toolbar">
        <div>
          <h2>
            {t('systemAuditLog')} <span className="table-count">{logs.length}</span>
          </h2>
          <p>{t('auditSubtitle')}</p>
        </div>

        <div className="toolbar-right">
          <label className="search-box">
            <Search size={15} />
            <input
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              onKeyDown={handleSearchKeyDown}
              placeholder={t('searchLogs')}
            />
          </label>

          <select
            className="filter-select"
            value={selectedType}
            onChange={(e) => setSelectedType(e.target.value)}
          >
            {EVENT_TYPES.map((et) => (
              <option key={et.value} value={et.value}>
                {et.label}
              </option>
            ))}
          </select>

          <button className="button button-quiet" onClick={loadLogs} disabled={loading}>
            <RefreshCw size={14} className={loading ? 'spin' : ''} /> {t('refresh')}
          </button>
        </div>
      </div>

      {error && (
        <div className="empty-table">
          {error.includes('401') || error.includes('403')
            ? 'Sign in with an account having user:read or admin authority to inspect system audit logs.'
            : error}
        </div>
      )}

      <div className="table-scroll">
        <table>
          <thead>
            <tr>
              <th>{t('thStatus')}</th>
              <th>{t('thEvent')}</th>
              <th>{t('thActor')}</th>
              <th>{t('thTarget')}</th>
              <th>DETAILS</th>
              <th>{t('thIpAddress')}</th>
              <th>{t('thDevice')}</th>
              <th>{t('thTimestamp')}</th>
            </tr>
          </thead>
          <tbody>
            {logs.map((log, idx) => (
              <tr key={log.id || idx}>
                <td>
                  <span className={`event-pill ${log.succeeded === false ? 'failed' : 'success'}`}>
                    {log.succeeded === false ? t('failed') : t('success')}
                  </span>
                </td>
                <td>
                  <b>{log.type}</b>
                </td>
                <td>
                  <span className="actor-badge">{log.actor || 'System'}</span>
                </td>
                <td>{log.target || '—'}</td>
                <td>
                  <span className="log-detail-cell">{log.detail || '—'}</span>
                </td>
                <td>{log.ipAddress || '—'}</td>
                <td>
                  <span className="device-cell">{log.userAgent || '—'}</span>
                </td>
                <td>{log.createdAt ? new Date(log.createdAt).toLocaleString() : ''}</td>
              </tr>
            ))}
          </tbody>
        </table>
        {!loading && !error && !logs.length && (
          <div className="empty-table">No audit events match your filter criteria.</div>
        )}
      </div>
    </section>
  )
}

import React, { useState } from 'react'
import {
  Check,
  Copy,
  CreditCard,
  Eye,
  EyeOff,
  Key,
  Pencil,
  Plus,
  RefreshCw,
  Trash2,
} from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { splitList } from '../api'

export function ClientsPage() {
  const { clients, t, flash, setModal, act } = useAuth()
  const [revealedSecrets, setRevealedSecrets] = useState({})
  const [copiedId, setCopiedId] = useState(null)

  const toggleReveal = (clientId) => {
    setRevealedSecrets((prev) => ({
      ...prev,
      [clientId]: !prev[clientId],
    }))
  }

  const copyToClipboard = (text, id) => {
    navigator.clipboard.writeText(text)
    setCopiedId(id)
    flash('Copied to clipboard!')
    setTimeout(() => setCopiedId(null), 2000)
  }

  const handleRotateSecret = async (clientId) => {
    if (window.confirm(`Rotate secret for client "${clientId}"? Existing tokens will remain valid until expiry.`)) {
      await act(`/api/clients/${clientId}/rotate-secret`)
    }
  }

  const handleDelete = async (clientId) => {
    if (window.confirm(`Delete client "${clientId}"? This cannot be undone.`)) {
      await act(`/api/clients/${clientId}`, 'DELETE')
    }
  }

  return (
    <div className="space-y-8">
      {/* Page Heading */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <div className="text-xs font-bold uppercase tracking-wider text-indigo-600">
            ADMIN CONSOLE · OAUTH CLIENTS
          </div>
          <h1 className="text-3xl font-extrabold tracking-tight text-slate-900 mt-1">
            Registered OAuth2 / OIDC Clients
          </h1>
          <p className="text-base text-slate-500 mt-1">
            Manage third-party and native client applications, allowed grant types, and redirect URIs.
          </p>
        </div>

        <button
          type="button"
          onClick={() => setModal('client')}
          className="flex items-center gap-2 px-5 py-2.5 text-sm font-semibold rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white shadow-sm shadow-indigo-200 transition"
        >
          <Plus size={16} />
          <span>{t('registerClient')}</span>
        </button>
      </div>

      {/* Clients Cards */}
      <div className="space-y-6">
        {clients.length === 0 ? (
          <div className="bg-white rounded-2xl border border-slate-200/90 p-14 text-center text-slate-400 text-sm">
            No OAuth clients registered yet.
          </div>
        ) : (
          clients.map((c) => {
            const grantTypes = splitList(c.authorizationGrantTypes || c.grantTypes)
            const redirectUris = splitList(c.redirectUris)
            const scopes = splitList(c.scopes)
            const isRevealed = !!revealedSecrets[c.clientId]

            return (
              <div
                key={c.id || c.clientId}
                className="bg-white rounded-2xl border border-slate-200/90 p-7 shadow-sm space-y-6 hover:shadow-md transition"
              >
                {/* Header row */}
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 pb-4">
                  <div className="flex items-center gap-4">
                    <div className="w-12 h-12 rounded-2xl bg-indigo-50 text-indigo-600 flex items-center justify-center border border-indigo-100 shrink-0">
                      <CreditCard size={22} />
                    </div>
                    <div>
                      <h2 className="text-base font-bold text-slate-900">{c.clientName || c.clientId}</h2>
                      <div className="flex items-center gap-2 mt-1">
                        <span className="font-mono text-xs text-slate-600 bg-slate-100 px-2.5 py-1 rounded-lg border border-slate-200">
                          {c.clientId}
                        </span>
                        <button
                          type="button"
                          onClick={() => copyToClipboard(c.clientId, c.clientId)}
                          className="text-slate-400 hover:text-slate-600 p-1"
                          title="Copy Client ID"
                        >
                          {copiedId === c.clientId ? (
                            <Check size={14} className="text-emerald-600" />
                          ) : (
                            <Copy size={14} />
                          )}
                        </button>
                      </div>
                    </div>
                  </div>

                  <div className="flex items-center gap-2.5">
                    <button
                      type="button"
                      onClick={() => setModal({ type: 'edit-client', item: c })}
                      className="flex items-center gap-2 px-4 py-2 text-xs font-semibold rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 transition"
                      title="Edit Client"
                    >
                      <Pencil size={14} />
                      <span>Edit</span>
                    </button>
                    <button
                      type="button"
                      onClick={() => handleRotateSecret(c.clientId)}
                      className="flex items-center gap-2 px-4 py-2 text-xs font-semibold rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 transition"
                      title="Rotate Client Secret"
                    >
                      <RefreshCw size={14} />
                      <span>Rotate Secret</span>
                    </button>
                    <button
                      type="button"
                      onClick={() => handleDelete(c.clientId)}
                      className="p-2 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-xl transition"
                      title="Delete Client"
                    >
                      <Trash2 size={16} />
                    </button>
                  </div>
                </div>

                {/* Secret row */}
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 p-4 bg-slate-50/80 rounded-xl border border-slate-200/70 text-sm">
                  <div className="flex items-center gap-2.5">
                    <Key size={16} className="text-slate-400 shrink-0" />
                    <span className="font-bold text-slate-700">Client Secret:</span>
                    <span className="font-mono text-slate-600 tracking-wider">
                      {isRevealed
                        ? c.clientSecret || '••••••••••••••••••••••••••••••••'
                        : '••••••••••••••••••••••••••••••••'}
                    </span>
                  </div>
                  <div className="flex items-center gap-3">
                    <button
                      type="button"
                      onClick={() => toggleReveal(c.clientId)}
                      className="flex items-center gap-1.5 text-xs font-bold text-indigo-600 hover:text-indigo-800"
                    >
                      {isRevealed ? <EyeOff size={14} /> : <Eye size={14} />}
                      <span>{isRevealed ? 'Hide Secret' : 'Reveal Secret'}</span>
                    </button>
                    {c.clientSecret && (
                      <button
                        type="button"
                        onClick={() => copyToClipboard(c.clientSecret, `${c.clientId}-sec`)}
                        className="text-slate-400 hover:text-slate-600 p-1"
                        title="Copy Secret"
                      >
                        {copiedId === `${c.clientId}-sec` ? (
                          <Check size={14} className="text-emerald-600" />
                        ) : (
                          <Copy size={14} />
                        )}
                      </button>
                    )}
                  </div>
                </div>

                {/* Details grid */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 text-sm pt-2">
                  <div>
                    <span className="text-xs font-bold uppercase tracking-wider text-slate-500 block mb-2">
                      Grant Types
                    </span>
                    <div className="flex flex-wrap gap-1.5">
                      {grantTypes.map((gt, i) => (
                        <span
                          key={i}
                          className="px-2.5 py-1 rounded-lg bg-purple-50 text-purple-700 font-mono text-xs font-semibold border border-purple-100"
                        >
                          {gt}
                        </span>
                      ))}
                    </div>
                  </div>

                  <div>
                    <span className="text-xs font-bold uppercase tracking-wider text-slate-500 block mb-2">
                      Redirect URIs
                    </span>
                    <div className="space-y-1.5 font-mono text-xs text-slate-600">
                      {redirectUris.length > 0 ? (
                        redirectUris.map((u, i) => (
                          <div key={i} className="truncate bg-slate-50 px-2.5 py-1 rounded border border-slate-200/60" title={u}>
                            {u}
                          </div>
                        ))
                      ) : (
                        <span className="text-slate-400 italic">None configured</span>
                      )}
                    </div>
                  </div>

                  <div>
                    <span className="text-xs font-bold uppercase tracking-wider text-slate-500 block mb-2">
                      Scopes
                    </span>
                    <div className="flex flex-wrap gap-1.5">
                      {scopes.map((sc, i) => (
                        <span
                          key={i}
                          className="px-2.5 py-1 rounded-lg bg-slate-100 text-slate-700 font-mono text-xs font-semibold border border-slate-200"
                        >
                          {sc}
                        </span>
                      ))}
                    </div>
                  </div>
                </div>
              </div>
            )
          })
        )}
      </div>
    </div>
  )
}

import React from 'react'
import { useAuth } from '../context/AuthContext'
import { AuditView } from '../components/AuditView'

export function AuditPage() {
  const { t } = useAuth()

  return (
    <div className="space-y-8">
      <div>
        <div className="text-xs font-bold uppercase tracking-wider text-indigo-600">
          SECURITY & AUDIT · AUDIT LOG
        </div>
        <h1 className="text-3xl font-extrabold tracking-tight text-slate-900 mt-1">
          System Audit Trail
        </h1>
        <p className="text-base text-slate-500 mt-1">
          Immutable audit record of all authentication events, administrative modifications, and security state changes.
        </p>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200/90 p-7 shadow-sm">
        <AuditView t={t} />
      </div>
    </div>
  )
}

import React from 'react'
import { useAuth } from '../context/AuthContext'
import { SystemConfigView } from '../components/SystemConfigView'

export function ConfigPage() {
  const { act, flash, t } = useAuth()

  return (
    <div className="space-y-8">
      <div>
        <div className="text-xs font-bold uppercase tracking-wider text-indigo-600">
          SECURITY & AUDIT · CONFIGURATION
        </div>
        <h1 className="text-3xl font-extrabold tracking-tight text-slate-900 mt-1">
          System & Security Policy Configuration
        </h1>
        <p className="text-base text-slate-500 mt-1">
          Global policies for password complexity, token lifetimes, social logins, and authentication settings.
        </p>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200/90 p-7 shadow-sm">
        <SystemConfigView act={act} flash={flash} t={t} />
      </div>
    </div>
  )
}

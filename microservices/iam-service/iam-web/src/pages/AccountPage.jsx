import React from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { AccountView } from '../components/AccountView'

export function AccountPage() {
  const { profile, setProfile, token, act, flash, t } = useAuth()
  const { tab } = useParams()
  const navigate = useNavigate()

  const currentTab = tab || 'profile'

  const handleTabChange = (newTab) => {
    navigate(`/account/${newTab}`)
  }

  return (
    <div className="space-y-8">
      <div>
        <div className="text-xs font-bold uppercase tracking-wider text-indigo-600">
          PERSONAL · MY ACCOUNT
        </div>
        <h1 className="text-3xl font-extrabold tracking-tight text-slate-900 mt-1">
          Account & Security Settings
        </h1>
        <p className="text-base text-slate-500 mt-1">
          Manage your personal identity profile, security credentials, social sign-ins, and active sessions.
        </p>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200/90 p-7 shadow-sm">
        <AccountView
          profile={profile}
          setProfile={setProfile}
          token={token}
          onTokenChange={() => {}}
          act={act}
          flash={flash}
          t={t}
          selectedTab={currentTab}
          onTabChange={handleTabChange}
        />
      </div>
    </div>
  )
}

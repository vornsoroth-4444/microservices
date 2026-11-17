import React, { useMemo, useState } from 'react'
import {
  Download,
  KeyRound,
  Lock,
  MoreHorizontal,
  Pencil,
  Plus,
  Search,
  Shield,
  Trash2,
  Unlock,
  UserCheck,
  UserX,
} from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { label } from '../api'

export function UsersPage() {
  const {
    users,
    userTotal,
    t,
    flash,
    setModal,
    setDetailUser,
    act,
  } = useAuth()

  const [searchQuery, setSearchQuery] = useState('')
  const [activeMenuUuid, setActiveMenuUuid] = useState(null)

  const filteredUsers = useMemo(() => {
    if (!searchQuery.trim()) return users
    const q = searchQuery.toLowerCase()
    return users.filter((u) => {
      const full = `${label(u)} ${u.username || ''} ${u.email || ''} ${u.role || ''}`.toLowerCase()
      return full.includes(q)
    })
  }, [users, searchQuery])

  const toggleLock = async (u) => {
    const isLocked = u.accountNonLocked === false
    const endpoint = `/api/users/${u.uuid}/${isLocked ? 'unlock' : 'lock'}`
    await act(endpoint)
    setActiveMenuUuid(null)
  }

  const toggleEnabled = async (u) => {
    const isEnabled = u.enabled !== false
    const endpoint = `/api/users/${u.uuid}/${isEnabled ? 'disable' : 'enable'}`
    await act(endpoint)
    setActiveMenuUuid(null)
  }

  const handleDelete = async (u) => {
    if (window.confirm(`Are you sure you want to delete user ${u.username}?`)) {
      await act(`/api/users/${u.uuid}`, 'DELETE')
      setActiveMenuUuid(null)
    }
  }

  return (
    <div className="space-y-8">
      {/* Page Heading */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-5">
        <div>
          <div className="text-xs font-bold uppercase tracking-wider text-indigo-600">
            ADMIN CONSOLE · USERS
          </div>
          <h1 className="text-3xl font-extrabold tracking-tight text-slate-900 mt-1">
            User Directory
          </h1>
          <p className="text-base text-slate-500 mt-1">
            Manage users, assign security roles, enforce credentials, and monitor permissions.
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

      {/* Search & Filter Toolbar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-5 rounded-2xl border border-slate-200/90 shadow-xs">
        <div className="relative flex-1 max-w-md">
          <Search size={18} className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            placeholder={t('searchUsers')}
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-11 pr-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-slate-50/60 focus:bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 transition"
          />
        </div>

        <div className="flex items-center gap-2 text-sm text-slate-500 font-medium">
          <span>Total users:</span>
          <span className="px-3 py-1 rounded-lg bg-slate-100 font-bold text-slate-800 border border-slate-200">
            {userTotal}
          </span>
        </div>
      </div>

      {/* Users Data Table */}
      <div className="bg-white rounded-2xl border border-slate-200/90 shadow-xs overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50/80 text-slate-500 uppercase tracking-wider text-xs font-bold border-b border-slate-100">
              <tr>
                <th className="px-6 py-4">User</th>
                <th className="px-6 py-4">Username</th>
                <th className="px-6 py-4">Roles</th>
                <th className="px-6 py-4">Status</th>
                <th className="px-6 py-4">Created</th>
                <th className="px-6 py-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-slate-700">
              {filteredUsers.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-6 py-14 text-center text-slate-400">
                    No users matching search query.
                  </td>
                </tr>
              ) : (
                filteredUsers.map((u) => {
                  const isLocked = u.accountNonLocked === false
                  const isEnabled = u.enabled !== false
                  const initials = label(u)
                    .split(' ')
                    .map((s) => s[0])
                    .join('')
                    .slice(0, 2)
                    .toUpperCase()

                  return (
                    <tr key={u.id || u.uuid} className="hover:bg-slate-50/60 transition">
                      {/* Name & Avatar */}
                      <td className="px-6 py-4">
                        <div
                          className="flex items-center gap-3.5 cursor-pointer group"
                          onClick={() => setDetailUser(u)}
                        >
                          <div className="w-10 h-10 rounded-full bg-indigo-50 text-indigo-700 font-bold text-sm flex items-center justify-center border border-indigo-100 group-hover:scale-105 transition shadow-xs">
                            {initials}
                          </div>
                          <div>
                            <div className="font-bold text-slate-900 group-hover:text-indigo-600 transition">
                              {label(u)}
                            </div>
                            <div className="text-slate-400 text-xs">{u.email}</div>
                          </div>
                        </div>
                      </td>

                      {/* Username */}
                      <td className="px-6 py-4 font-mono text-xs text-slate-600">
                        @{u.username}
                      </td>

                      {/* Roles */}
                      <td className="px-6 py-4">
                        <div className="flex flex-wrap gap-1.5">
                          {(u.roles && u.roles.length > 0) ? (
                            u.roles.map((r, i) => {
                              const rName = typeof r === 'string' ? r : r.name
                              const isAdminRole = rName?.toUpperCase().includes('ADMIN')
                              return (
                                <span
                                  key={i}
                                  className={`inline-flex items-center px-2.5 py-0.5 rounded-lg text-xs font-semibold ${
                                    isAdminRole
                                      ? 'bg-purple-50 text-purple-700 border border-purple-200'
                                      : 'bg-slate-100 text-slate-700 border border-slate-200'
                                  }`}
                                >
                                  {rName}
                                </span>
                              )
                            })
                          ) : (
                            <span className="text-slate-400 text-xs italic">No roles</span>
                          )}
                        </div>
                      </td>

                      {/* Status */}
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-2">
                          <span
                            className={`inline-flex items-center px-2.5 py-1 rounded-full text-xs font-bold ${
                              isEnabled
                                ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                                : 'bg-slate-100 text-slate-500 border border-slate-200'
                            }`}
                          >
                            {isEnabled ? 'Active' : 'Disabled'}
                          </span>
                          {isLocked && (
                            <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-bold bg-amber-50 text-amber-700 border border-amber-200">
                              <Lock size={12} /> Locked
                            </span>
                          )}
                        </div>
                      </td>

                      {/* Created */}
                      <td className="px-6 py-4 text-slate-400 font-mono text-xs">
                        {u.createdAt ? new Date(u.createdAt).toLocaleDateString() : 'N/A'}
                      </td>

                      {/* Action Menu */}
                      <td className="px-6 py-4 text-right relative">
                        <div className="flex items-center justify-end gap-2">
                          <button
                            type="button"
                            onClick={() => setDetailUser(u)}
                            className="px-3 py-1.5 text-xs font-semibold rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-700 transition"
                          >
                            Details
                          </button>

                          <div className="relative">
                            <button
                              type="button"
                              onClick={() =>
                                setActiveMenuUuid(activeMenuUuid === u.uuid ? null : u.uuid)
                              }
                              className="p-1.5 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-lg transition"
                            >
                              <MoreHorizontal size={18} />
                            </button>

                            {activeMenuUuid === u.uuid && (
                              <div className="absolute right-0 top-full mt-2 w-48 bg-white rounded-2xl shadow-xl border border-slate-200 py-2 z-20 text-left animate-in fade-in zoom-in-95 duration-100">
                                <button
                                  type="button"
                                  onClick={() => {
                                    setModal({ type: 'edit-user', item: u })
                                    setActiveMenuUuid(null)
                                  }}
                                  className="w-full px-4 py-2 text-xs text-slate-700 hover:bg-slate-50 flex items-center gap-2.5 font-medium"
                                >
                                  <Pencil size={14} className="text-slate-400" /> Edit Profile
                                </button>
                                <button
                                  type="button"
                                  onClick={() => {
                                    setModal({ type: 'assign-role', item: u })
                                    setActiveMenuUuid(null)
                                  }}
                                  className="w-full px-4 py-2 text-xs text-slate-700 hover:bg-slate-50 flex items-center gap-2.5 font-medium"
                                >
                                  <Shield size={14} className="text-slate-400" /> Assign Roles
                                </button>
                                <button
                                  type="button"
                                  onClick={() => {
                                    setDetailUser({ ...u, initialTab: 'permissions' })
                                    setActiveMenuUuid(null)
                                  }}
                                  className="w-full px-4 py-2 text-xs text-slate-700 hover:bg-slate-50 flex items-center gap-2.5 font-medium"
                                >
                                  <KeyRound size={14} className="text-slate-400" /> Direct Permissions
                                </button>
                                <button
                                  type="button"
                                  onClick={() => {
                                    setModal({ type: 'reset-password', item: u })
                                    setActiveMenuUuid(null)
                                  }}
                                  className="w-full px-4 py-2 text-xs text-slate-700 hover:bg-slate-50 flex items-center gap-2.5 font-medium"
                                >
                                  <KeyRound size={14} className="text-slate-400" /> Reset Password
                                </button>
                                <div className="border-t border-slate-100 my-1.5" />
                                <button
                                  type="button"
                                  onClick={() => toggleLock(u)}
                                  className="w-full px-4 py-2 text-xs text-slate-700 hover:bg-slate-50 flex items-center gap-2.5 font-medium"
                                >
                                  {isLocked ? (
                                    <>
                                      <Unlock size={14} className="text-emerald-500" /> Unlock Account
                                    </>
                                  ) : (
                                    <>
                                      <Lock size={14} className="text-amber-500" /> Lock Account
                                    </>
                                  )}
                                </button>
                                <button
                                  type="button"
                                  onClick={() => toggleEnabled(u)}
                                  className="w-full px-4 py-2 text-xs text-slate-700 hover:bg-slate-50 flex items-center gap-2.5 font-medium"
                                >
                                  {isEnabled ? (
                                    <>
                                      <UserX size={14} className="text-slate-400" /> Disable User
                                    </>
                                  ) : (
                                    <>
                                      <UserCheck size={14} className="text-emerald-500" /> Enable User
                                    </>
                                  )}
                                </button>
                                <div className="border-t border-slate-100 my-1.5" />
                                <button
                                  type="button"
                                  onClick={() => handleDelete(u)}
                                  className="w-full px-4 py-2 text-xs text-rose-600 hover:bg-rose-50 flex items-center gap-2.5 font-bold"
                                >
                                  <Trash2 size={14} /> Delete User
                                </button>
                              </div>
                            )}
                          </div>
                        </div>
                      </td>
                    </tr>
                  )
                })
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}

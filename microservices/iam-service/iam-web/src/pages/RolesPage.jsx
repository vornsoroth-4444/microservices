import React from 'react'
import { Plus, ShieldCheck, Trash2, KeyRound } from 'lucide-react'
import { useAuth } from '../context/AuthContext'

export function RolesPage() {
  const { roles, t, setModal, act } = useAuth()

  const handleDelete = async (r) => {
    if (r.system) {
      alert('System roles cannot be deleted.')
      return
    }
    if (window.confirm(`Are you sure you want to delete role ${r.name}?`)) {
      await act(`/api/roles/${r.uuid}`, 'DELETE')
    }
  }

  return (
    <div className="space-y-8">
      {/* Page Heading */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <div className="text-xs font-bold uppercase tracking-wider text-indigo-600">
            ADMIN CONSOLE · ROLES
          </div>
          <h1 className="text-3xl font-extrabold tracking-tight text-slate-900 mt-1">
            Role Management
          </h1>
          <p className="text-base text-slate-500 mt-1">
            Configure system and custom RBAC security roles and attach permission policies.
          </p>
        </div>

        <button
          type="button"
          onClick={() => setModal('role')}
          className="flex items-center gap-2 px-5 py-2.5 text-sm font-semibold rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white shadow-sm shadow-indigo-200 transition"
        >
          <Plus size={16} />
          <span>{t('createRole')}</span>
        </button>
      </div>

      {/* Roles Grid / Table */}
      <div className="bg-white rounded-2xl border border-slate-200/90 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50/80 text-slate-500 uppercase tracking-wider text-xs font-bold border-b border-slate-100">
              <tr>
                <th className="px-6 py-4">Role Name</th>
                <th className="px-6 py-4">Description</th>
                <th className="px-6 py-4">Permissions</th>
                <th className="px-6 py-4">Type</th>
                <th className="px-6 py-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-slate-700">
              {roles.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-6 py-14 text-center text-slate-400 text-sm">
                    No roles created yet.
                  </td>
                </tr>
              ) : (
                roles.map((r) => {
                  const permCount = r.permissions?.length || 0
                  return (
                    <tr key={r.id || r.uuid} className="hover:bg-slate-50/60 transition">
                      <td className="px-6 py-4.5 font-bold text-slate-900 flex items-center gap-3">
                        <div className="w-9 h-9 rounded-xl bg-purple-50 text-purple-700 flex items-center justify-center border border-purple-100 shrink-0">
                          <ShieldCheck size={18} />
                        </div>
                        <span className="text-sm font-bold">{r.name}</span>
                      </td>

                      <td className="px-6 py-4.5 text-slate-500 max-w-sm truncate text-sm">
                        {r.description || 'No description provided.'}
                      </td>

                      <td className="px-6 py-4.5">
                        <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-lg text-xs font-semibold bg-slate-100 text-slate-700 border border-slate-200">
                          <KeyRound size={13} className="text-slate-400" />
                          {permCount} permissions
                        </span>
                      </td>

                      <td className="px-6 py-4.5">
                        {r.system ? (
                          <span className="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-bold bg-amber-50 text-amber-700 border border-amber-200">
                            SYSTEM
                          </span>
                        ) : (
                          <span className="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-bold bg-slate-100 text-slate-600 border border-slate-200">
                            CUSTOM
                          </span>
                        )}
                      </td>

                      <td className="px-6 py-4.5 text-right">
                        <div className="flex items-center justify-end gap-2">
                          <button
                            type="button"
                            onClick={() => setModal({ type: 'assign-perm', item: r })}
                            className="px-3 py-1.5 text-xs font-semibold rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-700 transition"
                          >
                            Permissions
                          </button>
                          <button
                            type="button"
                            onClick={() => setModal({ type: 'edit-role', item: r })}
                            className="px-3 py-1.5 text-xs font-semibold rounded-lg bg-indigo-50 hover:bg-indigo-100 text-indigo-700 border border-indigo-200 transition"
                          >
                            Edit
                          </button>
                          {!r.system && (
                            <button
                              type="button"
                              onClick={() => handleDelete(r)}
                              className="p-1.5 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition"
                              title="Delete Role"
                            >
                              <Trash2 size={16} />
                            </button>
                          )}
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

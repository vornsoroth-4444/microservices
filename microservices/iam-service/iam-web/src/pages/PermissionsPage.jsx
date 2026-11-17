import React, { useMemo, useState } from 'react'
import { KeyRound, Plus, Search, Trash2 } from 'lucide-react'
import { useAuth } from '../context/AuthContext'

export function PermissionsPage() {
  const { permissions, t, setModal, act } = useAuth()
  const [search, setSearch] = useState('')
  const [selectedResource, setSelectedResource] = useState('ALL')

  const resources = useMemo(() => {
    const set = new Set()
    permissions.forEach((p) => {
      if (p.resource) set.add(p.resource)
    })
    return ['ALL', ...Array.from(set)]
  }, [permissions])

  const filtered = useMemo(() => {
    return permissions.filter((p) => {
      const matchResource = selectedResource === 'ALL' || p.resource === selectedResource
      const matchSearch =
        !search.trim() ||
        (p.name && p.name.toLowerCase().includes(search.toLowerCase())) ||
        (p.description && p.description.toLowerCase().includes(search.toLowerCase()))
      return matchResource && matchSearch
    })
  }, [permissions, selectedResource, search])

  const handleDelete = async (p) => {
    if (p.system) {
      alert('System permissions cannot be deleted.')
      return
    }
    if (window.confirm(`Are you sure you want to delete permission ${p.name}?`)) {
      await act(`/api/permissions/${p.uuid}`, 'DELETE')
    }
  }

  return (
    <div className="space-y-8">
      {/* Page Heading */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <div className="text-xs font-bold uppercase tracking-wider text-indigo-600">
            ADMIN CONSOLE · PERMISSIONS
          </div>
          <h1 className="text-3xl font-extrabold tracking-tight text-slate-900 mt-1">
            Permission Catalog
          </h1>
          <p className="text-base text-slate-500 mt-1">
            Fine-grained resource-action permission entries enforced by IAM policy evaluators.
          </p>
        </div>

        <button
          type="button"
          onClick={() => setModal('permission')}
          className="flex items-center gap-2 px-5 py-2.5 text-sm font-semibold rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white shadow-sm shadow-indigo-200 transition"
        >
          <Plus size={16} />
          <span>{t('createPermission')}</span>
        </button>
      </div>

      {/* Filters Toolbar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-5 rounded-2xl border border-slate-200/90 shadow-sm">
        <div className="flex flex-wrap gap-2">
          {resources.map((r) => (
            <button
              key={r}
              type="button"
              onClick={() => setSelectedResource(r)}
              className={`px-3.5 py-2 rounded-xl text-xs font-bold transition ${
                selectedResource === r
                  ? 'bg-indigo-600 text-white shadow-sm'
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
              }`}
            >
              {r.toUpperCase()}
            </button>
          ))}
        </div>

        <div className="relative w-full sm:w-72">
          <Search size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            placeholder="Search permissions..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full pl-10 pr-4 py-2.5 text-sm rounded-xl border border-slate-200 bg-slate-50/50 focus:bg-white focus:outline-hidden focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 transition"
          />
        </div>
      </div>

      {/* Permissions Table */}
      <div className="bg-white rounded-2xl border border-slate-200/90 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50/80 text-slate-500 uppercase tracking-wider text-xs font-bold border-b border-slate-100">
              <tr>
                <th className="px-6 py-4">Permission Name</th>
                <th className="px-6 py-4">Resource</th>
                <th className="px-6 py-4">Action</th>
                <th className="px-6 py-4">Description</th>
                <th className="px-6 py-4">Type</th>
                <th className="px-6 py-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-slate-700">
              {filtered.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-6 py-14 text-center text-slate-400 text-sm">
                    No permissions found.
                  </td>
                </tr>
              ) : (
                filtered.map((p) => (
                  <tr key={p.id || p.uuid} className="hover:bg-slate-50/60 transition">
                    <td className="px-6 py-4.5 font-mono text-xs font-bold text-slate-900 flex items-center gap-2.5">
                      <KeyRound size={15} className="text-amber-500 shrink-0" />
                      <span>{p.name}</span>
                    </td>

                    <td className="px-6 py-4.5">
                      <span className="px-2.5 py-1 rounded-lg bg-slate-100 text-slate-700 font-mono text-xs font-semibold border border-slate-200">
                        {p.resource}
                      </span>
                    </td>

                    <td className="px-6 py-4.5">
                      <span className="px-2.5 py-1 rounded-lg bg-indigo-50 text-indigo-700 font-mono text-xs font-semibold border border-indigo-100">
                        {p.action}
                      </span>
                    </td>

                    <td className="px-6 py-4.5 text-slate-500 max-w-sm truncate text-sm">
                      {p.description || 'System auto-generated permission.'}
                    </td>

                    <td className="px-6 py-4.5">
                      {p.system ? (
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
                      {!p.system && (
                        <button
                          type="button"
                          onClick={() => handleDelete(p)}
                          className="p-1.5 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition"
                          title="Delete Permission"
                        >
                          <Trash2 size={16} />
                        </button>
                      )}
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

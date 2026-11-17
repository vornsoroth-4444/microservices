import React from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import { AppLayout } from './layouts/AppLayout'
import { LoginPage } from './pages/LoginPage'
import { OverviewPage } from './pages/OverviewPage'
import { UsersPage } from './pages/UsersPage'
import { RolesPage } from './pages/RolesPage'
import { PermissionsPage } from './pages/PermissionsPage'
import { ClientsPage } from './pages/ClientsPage'
import { SessionsPage } from './pages/SessionsPage'
import { AuditPage } from './pages/AuditPage'
import { ConfigPage } from './pages/ConfigPage'
import { AccountPage } from './pages/AccountPage'

function ProtectedRoutes() {
  const { token, isAdmin } = useAuth()

  if (!token) {
    return <Navigate to="/login" replace />
  }

  return (
    <Routes>
      <Route element={<AppLayout />}>
        {/* Redirect root based on role */}
        <Route
          index
          element={<Navigate to={isAdmin ? '/overview' : '/account/profile'} replace />}
        />

        {/* Management Routes */}
        <Route
          path="overview"
          element={isAdmin ? <OverviewPage /> : <Navigate to="/account/profile" replace />}
        />
        <Route
          path="users"
          element={isAdmin ? <UsersPage /> : <Navigate to="/account/profile" replace />}
        />
        <Route
          path="roles"
          element={isAdmin ? <RolesPage /> : <Navigate to="/account/profile" replace />}
        />
        <Route
          path="permissions"
          element={isAdmin ? <PermissionsPage /> : <Navigate to="/account/profile" replace />}
        />
        <Route
          path="clients"
          element={isAdmin ? <ClientsPage /> : <Navigate to="/account/profile" replace />}
        />
        <Route path="sessions" element={<SessionsPage />} />
        <Route
          path="audit"
          element={isAdmin ? <AuditPage /> : <Navigate to="/account/profile" replace />}
        />
        <Route
          path="config"
          element={isAdmin ? <ConfigPage /> : <Navigate to="/account/profile" replace />}
        />

        {/* Account Routes */}
        <Route path="account" element={<AccountPage />} />
        <Route path="account/:tab" element={<AccountPage />} />

        {/* Catch-all */}
        <Route
          path="*"
          element={<Navigate to={isAdmin ? '/overview' : '/account/profile'} replace />}
        />
      </Route>
    </Routes>
  )
}

export function App() {
  const basename = window.location.pathname.startsWith('/iam-ui') ? '/iam-ui' : ''

  return (
    <BrowserRouter basename={basename}>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/*" element={<ProtectedRoutes />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}

export default App

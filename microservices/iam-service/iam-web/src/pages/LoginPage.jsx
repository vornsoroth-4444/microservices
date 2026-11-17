import React from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { LoginView } from '../components/LoginView'

export function LoginPage() {
  const { handleLogin, language, toggleLanguage, t } = useAuth()
  const navigate = useNavigate()

  const onLoginSuccess = (token, user) => {
    handleLogin(token, user)
    if (user?.username === 'admin' || user?.roles?.some(r => String(r).toUpperCase().includes('ADMIN'))) {
      navigate('/overview')
    } else {
      navigate('/account')
    }
  }

  return (
    <LoginView
      onLogin={onLoginSuccess}
      language={language}
      onToggleLanguage={toggleLanguage}
      t={t}
    />
  )
}

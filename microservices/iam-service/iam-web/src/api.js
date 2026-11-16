// API Client and Authentication Helpers

const TOKEN_KEY = 'iam-token'

export const getToken = () => {
  if (typeof window !== 'undefined') {
    // 1. Check URL query parameters (passed from OAuth2 social login redirect)
    try {
      const params = new URLSearchParams(window.location.search)
      const urlToken = params.get('token')
      if (urlToken) {
        const cleanToken = String(urlToken).replace(/\\-/g, '-')
        sessionStorage.setItem(TOKEN_KEY, cleanToken)
        localStorage.setItem(TOKEN_KEY, cleanToken)
        // Clean up URL address bar without page reload
        params.delete('token')
        params.delete('social')
        const newSearch = params.toString() ? `?${params.toString()}` : ''
        window.history.replaceState({}, document.title, window.location.pathname + newSearch + window.location.hash)
        return cleanToken
      }
    } catch {}

    // 2. Check window.__INITIAL_TOKEN__
    if (window.__INITIAL_TOKEN__) {
      const t = String(window.__INITIAL_TOKEN__).replace(/\\-/g, '-')
      sessionStorage.setItem(TOKEN_KEY, t)
      localStorage.setItem(TOKEN_KEY, t)
      return t
    }
  }
  const saved = sessionStorage.getItem(TOKEN_KEY) || localStorage.getItem(TOKEN_KEY) || ''
  return saved ? String(saved).replace(/\\-/g, '-') : ''
}

export const setToken = (token) => {
  if (token) {
    sessionStorage.setItem(TOKEN_KEY, token)
    localStorage.setItem(TOKEN_KEY, token)
  } else {
    sessionStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(TOKEN_KEY)
  }
}

const USER_KEY = 'iam-user'

export const getUser = () => {
  if (typeof window !== 'undefined' && window.__INITIAL_USER__) {
    return window.__INITIAL_USER__
  }
  try {
    const raw = localStorage.getItem(USER_KEY) || sessionStorage.getItem(USER_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export const setUser = (user) => {
  if (user) {
    const str = JSON.stringify(user)
    sessionStorage.setItem(USER_KEY, str)
    localStorage.setItem(USER_KEY, str)
  } else {
    sessionStorage.removeItem(USER_KEY)
    localStorage.removeItem(USER_KEY)
  }
}

export const clearToken = () => {
  sessionStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(TOKEN_KEY)
  sessionStorage.removeItem(USER_KEY)
  localStorage.removeItem(USER_KEY)
}

export const isUserAdmin = (user) => {
  if (!user) return false
  if (user.username === 'admin') return true
  const roles = user.roles || []
  return roles.some((r) => {
    if (typeof r === 'string') {
      const upper = r.toUpperCase()
      return upper === 'ADMIN' || upper === 'ROLE_ADMIN'
    }
    if (r && r.name) {
      const upper = r.name.toUpperCase()
      return upper === 'ADMIN' || upper === 'ROLE_ADMIN'
    }
    return false
  })
}

export const api = async (path, options = {}) => {
  const token = getToken()
  const headers = {
    ...(options.body ? { 'Content-Type': 'application/json' } : {}),
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options.headers,
  }

  const response = await fetch(path, {
    ...options,
    credentials: 'include',
    headers,
  })

  if (!response.ok) {
    let errorMessage = `${response.status} ${response.statusText}`
    try {
      const errorJson = await response.json()
      errorMessage = errorJson.detail || errorJson.message || errorJson.error_description || errorJson.error || errorMessage
    } catch {
      try {
        const errorText = await response.text()
        if (errorText) errorMessage = errorText
      } catch {
        // use default status message
      }
    }
    const err = new Error(errorMessage)
    err.status = response.status
    throw err
  }

  if (response.status === 204) return null
  return response.json()
}

export const pageItems = (data) => (Array.isArray(data) ? data : data?.items || data?.content || [])

export const splitList = (value) => (Array.isArray(value) ? value : String(value || '').split(/[\n,]+/).map((item) => item.trim()).filter(Boolean))

export const label = (user) => {
  if (!user) return 'Unknown user'
  const full = `${user.givenName || ''} ${user.familyName || ''}`.trim()
  return full || user.name || user.username || user.email || 'Unknown user'
}

const base64Url = (bytes) =>
  btoa(String.fromCharCode(...new Uint8Array(bytes)))
    .replaceAll('+', '-')
    .replaceAll('/', '_')
    .replaceAll('=', '')

export const beginLogin = async () => {
  const verifier = base64Url(crypto.getRandomValues(new Uint8Array(32)))
  const challenge = base64Url(await crypto.subtle.digest('SHA-256', new TextEncoder().encode(verifier)))
  const state = base64Url(crypto.getRandomValues(new Uint8Array(24)))
  const redirectUri = `${window.location.origin}/iam-ui/`

  sessionStorage.setItem('iam-pkce-verifier', verifier)
  sessionStorage.setItem('iam-oauth-state', state)

  const scopes = [
    'openid',
    'profile',
    'email',
    'user:read',
    'user:create',
    'user:update',
    'user:delete',
    'user:assign',
    'role:read',
    'role:create',
    'role:update',
    'role:delete',
    'permission:read',
    'permission:create',
    'permission:update',
    'permission:delete'
  ].join(' ')

  const params = new URLSearchParams({
    response_type: 'code',
    client_id: 'iam-web-spa',
    redirect_uri: redirectUri,
    scope: scopes,
    state,
    code_challenge: challenge,
    code_challenge_method: 'S256',
  })

  window.location.assign(`/oauth2/authorize?${params}`)
}

export const completeLoginCallback = async () => {
  const params = new URLSearchParams(window.location.search)
  const code = params.get('code')
  if (!code) return null

  const state = params.get('state')
  const savedState = sessionStorage.getItem('iam-oauth-state')
  const verifier = sessionStorage.getItem('iam-pkce-verifier')

  if (!state || state !== savedState || !verifier) {
    throw new Error('OAuth2 state validation failed')
  }

  const redirectUri = `${window.location.origin}/iam-ui/`
  const body = new URLSearchParams({
    grant_type: 'authorization_code',
    client_id: 'iam-web-spa',
    redirect_uri: redirectUri,
    code,
    code_verifier: verifier,
  })

  const response = await fetch('/oauth2/token', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body,
  })

  if (!response.ok) {
    const errorText = await response.text()
    throw new Error(errorText || 'Token exchange failed')
  }

  const tokenResponse = await response.json()
  setToken(tokenResponse.access_token)
  sessionStorage.removeItem('iam-pkce-verifier')
  sessionStorage.removeItem('iam-oauth-state')

  // Clean the URL
  window.history.replaceState({}, document.title, window.location.pathname)
  return tokenResponse.access_token
}

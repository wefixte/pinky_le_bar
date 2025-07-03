export function getAuthHeaders() {
  const token = localStorage.getItem('token');
  return token ? { Authorization: `Bearer ${token}` } : {};
}

export function getUserRole(): string {
  return localStorage.getItem('role') || '';
}

export function hasRole(role: string): boolean {
  return getUserRole() === role;
}

export function getUserInfoFromToken() {
  const token = localStorage.getItem('token')
  if (!token) return null

  const payloadBase64 = token.split('.')[1]
  const payload = JSON.parse(atob(payloadBase64))

  return payload
}
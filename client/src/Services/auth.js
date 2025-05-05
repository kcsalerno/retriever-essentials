const API_URL = 'http://localhost:8080/api/auth';

export async function login(credentials) {
  try {
    const response = await fetch(`${API_URL}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(credentials)
    });

    if (!response.ok) {
      const error = await response.text();
      return { ok: false, error };
    }

    const data = await response.json();
    return {
      ok: true,
      token: data.token,
      email: data.email,
      role: data.role
    };
  } catch (err) {
    return { ok: false, error: err.message };
  }
}

export async function refresh() {
  try {
    const response = await fetch(`${API_URL}/refresh`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      }
    });

    if (!response.ok) {
      const error = await response.text();
      return { ok: false, error };
    }

    const data = await response.json();
    return {
      ok: true,
      token: data.token,
      email: data.email,
      role: data.role
    };
  } catch (err) {
    return { ok: false, error: err.message };
  }
}

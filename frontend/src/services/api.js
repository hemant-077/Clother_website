const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api';

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  });

  if (!response.ok) {
    let message = 'Request failed';
    try {
      const error = await response.json();
      message = error.details?.[0] ?? error.error ?? message;
    } catch {
      message = response.statusText || message;
    }
    throw new Error(message);
  }

  if (response.status === 204) {
    return null;
  }
  return response.json();
}

export function fetchProducts({ search = '', category = '' } = {}) {
  const params = new URLSearchParams();
  if (search.trim()) params.set('search', search.trim());
  if (category) params.set('category', category);
  const suffix = params.toString() ? `?${params}` : '';
  return request(`/products${suffix}`);
}

export function createCheckout(payload) {
  return request('/checkout', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export function fetchOrder(orderNumber, { sessionId = '' } = {}) {
  const params = new URLSearchParams();
  if (sessionId) params.set('session_id', sessionId);
  const suffix = params.toString() ? `?${params}` : '';
  return request(`/orders/${encodeURIComponent(orderNumber)}${suffix}`);
}

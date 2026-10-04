const API_URL = import.meta.env.VITE_API_URL || "";
const CUSTOMER_API_KEY =
  import.meta.env.VITE_CUSTOMER_API_KEY || "financiaplus-test-key";

async function request(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...options.headers,
    },
  });

  const data = await response.json().catch(() => null);

  if (!response.ok) {
    throw new Error(
      data?.message || data?.error || "No fue posible completar la solicitud",
    );
  }

  return data;
}

export function checkAml(document) {
  return request(`/api/aml/by-document/${encodeURIComponent(document)}`);
}

export function getCustomer(document) {
  return request(
    `/api/customers/${encodeURIComponent(document)}/general`,
    {
      headers: { "X-API-Key": CUSTOMER_API_KEY },
    },
  );
}

export function getApplication(id) {
  return request(`/api/applications/${id}`);
}

export function createApplication(application) {
  return request("/api/applications", {
    method: "POST",
    body: JSON.stringify(application),
  });
}

export function updateApplication(id, application) {
  return request(`/api/applications/${id}`, {
    method: "PATCH",
    body: JSON.stringify(application),
  });
}

export function evaluateApplication(id) {
  return request(`/api/applications/${id}/evaluate`, {
    method: "POST",
  });
}

export function originateApplication(id) {
  return request(`/api/applications/${id}/originate`, {
    method: "POST",
  });
}

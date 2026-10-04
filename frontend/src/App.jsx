import { useEffect, useState } from "react";
import {
  checkAml,
  createApplication,
  evaluateApplication,
  getApplication,
  getCustomer,
  originateApplication,
  updateApplication,
} from "./api";
import PersonalDataForm from "./components/PersonalDataForm";
import ResultPanel from "./components/ResultPanel";

const emptyForm = {
  fullName: "",
  address: "",
  birthDate: "",
  gender: "",
  identityNumber: "",
  email: "",
  phone: "",
  biometricScore: 92,
};

const storageKey = "financiaplus-application-id";

function App() {
  const [step, setStep] = useState(1);
  const [form, setForm] = useState(emptyForm);
  const [applicationId, setApplicationId] = useState(null);
  const [application, setApplication] = useState(null);
  const [customerFound, setCustomerFound] = useState(false);
  const [identityVerified, setIdentityVerified] = useState(false);
  const [documentReady, setDocumentReady] = useState(false);
  const [selfieReady, setSelfieReady] = useState(false);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    const savedId = localStorage.getItem(storageKey);

    if (!savedId) return;

    getApplication(savedId)
      .then((savedApplication) => {
        setApplicationId(savedApplication.id);
        setForm(applicationToForm(savedApplication));
        setStep(2);
        setMessage("Recuperamos el borrador guardado en este dispositivo.");
      })
      .catch(() => localStorage.removeItem(storageKey));
  }, []);

  function handleChange(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  }

  async function verifyIdentity(event) {
    event.preventDefault();
    clearFeedback();

    const document = form.identityNumber.trim().toUpperCase();

    if (!/^1234/.test(document)) {
      setError(
        "Documento no válido: los primeros 4 caracteres deben ser 1234.",
      );
      return;
    }

    setForm((current) => ({ ...current, identityNumber: document }));
    setLoading(true);

    try {
      const [aml, customer] = await Promise.all([
        checkAml(document),
        getCustomer(document),
      ]);

      if (aml.matched) {
        setError("La persona aparece en la lista restrictiva y el proceso no puede continuar.");
        return;
      }

      setIdentityVerified(true);
      setCustomerFound(customer.existing);

      if (customer.existing) {
        setForm((current) => ({
          ...current,
          fullName: customer.fullName || "",
          address: customer.address || "",
          birthDate: customer.birthDate || "",
          gender: customer.gender || "",
          email: customer.email || "",
          phone: customer.phone || "",
        }));
        setMessage("Cliente encontrado. Completamos sus datos automáticamente.");
      } else {
        setMessage("No encontramos un cliente previo. Puedes registrar tus datos.");
      }

      setStep(2);
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  async function saveDraft() {
    clearFeedback();
    setLoading(true);

    try {
      const saved = await persistApplication();
      setMessage("Avance guardado. Podrás continuar desde este dispositivo.");
      return saved;
    } catch (requestError) {
      setError(requestError.message);
      return null;
    } finally {
      setLoading(false);
    }
  }

  async function goToValidation(event) {
    event.preventDefault();
    clearFeedback();

    if (!documentReady || !selfieReady) {
      setError("Completa la captura del documento y la prueba de vida.");
      return;
    }

    const saved = await saveDraft();
    if (saved) setStep(3);
  }

  async function finishApplication() {
    clearFeedback();
    setLoading(true);

    try {
      const evaluated = await evaluateApplication(applicationId);

      if (evaluated.status !== "APPROVED") {
        finishWith(evaluated);
        return;
      }

      const completed = await originateApplication(applicationId);
      finishWith(completed);
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  async function persistApplication() {
    const saved = applicationId
      ? await updateApplication(applicationId, form)
      : await createApplication(form);

    setApplicationId(saved.id);
    localStorage.setItem(storageKey, saved.id);
    return saved;
  }

  function finishWith(result) {
    setApplication(result);
    setStep(4);
    localStorage.removeItem(storageKey);
  }

  function restart() {
    localStorage.removeItem(storageKey);
    setStep(1);
    setForm(emptyForm);
    setApplicationId(null);
    setApplication(null);
    setCustomerFound(false);
    setIdentityVerified(false);
    setDocumentReady(false);
    setSelfieReady(false);
    clearFeedback();
  }

  function returnToIdentity() {
    localStorage.removeItem(storageKey);
    setStep(1);
    setForm(emptyForm);
    setApplicationId(null);
    setApplication(null);
    setCustomerFound(false);
    setIdentityVerified(false);
    setDocumentReady(false);
    setSelfieReady(false);
    clearFeedback();
  }

  function clearFeedback() {
    setError("");
    setMessage("");
  }

  return (
    <div className="app-shell">
      <header className="topbar">
        <a className="brand" href="#top" aria-label="FinanciaPlus, inicio">
          <span className="brand-mark">F</span>
          <span>FinanciaPlus</span>
        </a>
        <span className="secure-label"><LockIcon /> Proceso seguro</span>
      </header>

      <main id="top" className="page">
        <section className="intro">
          <div>
            <p className="eyebrow">Cuenta Digital + Tarjeta de Débito</p>
            <h1>Tu nueva cuenta, sin filas y en pocos pasos</h1>
            <p>
              Completa tus datos, valida tu identidad y recibe el resultado de
              tu solicitud en línea.
            </p>
          </div>
          <div className="trust-card">
            <ShieldIcon />
            <div><strong>Información protegida</strong><span>Validamos tu identidad antes de continuar.</span></div>
          </div>
        </section>

        <div className="flow-card">
          {message && <div className="notice notice-success" role="status">{message}</div>}
          {error && <div className="notice notice-error" role="alert">{error}</div>}

          {step === 1 && (
            <section className="step-content">
              <p className="eyebrow">Paso 1 · Identidad</p>
              <h2>Empecemos por tu identidad</h2>
              <p className="section-copy">
                Consultaremos si ya eres cliente y verificaremos la lista de prevención AML.
              </p>
              <form className="identity-form" onSubmit={verifyIdentity}>
                <label className="field">
                  <span>Número de documento</span>
                  <input
                    name="identityNumber"
                    value={form.identityNumber}
                    onChange={handleChange}
                    placeholder="Ej. 1234-ALTO"
                    autoComplete="off"
                    required
                  />
                  <small>Para probar: 1234-ALTO, 1234-BAJO o 1234-AML1.</small>
                </label>
                <button className="button button-primary" disabled={loading}>
                  {loading ? "Verificando..." : "Verificar y continuar"}
                </button>
              </form>
            </section>
          )}

          {step === 2 && (
            <section className="step-content">
              <button className="back-link" type="button" onClick={returnToIdentity}>
                ← Volver a identidad
              </button>
              <p className="eyebrow">Paso 2 · Datos personales</p>
              <div className="section-heading">
                <div>
                  <h2>Completa tu información</h2>
                  <p className="section-copy">
                    {customerFound
                      ? "Revisa los datos que encontramos y corrige lo necesario."
                      : "Ingresa tus datos como aparecen en tu documento."}
                  </p>
                </div>
                {identityVerified && <span className="verified-badge">✓ Identidad consultada</span>}
              </div>

              <form onSubmit={goToValidation}>
                <div className="details-layout">
                  <PersonalDataForm form={form} onChange={handleChange} />

                  <div className="verification-grid">
                    <div className={`upload-card ${documentReady ? "done" : ""}`}>
                      <DocumentIcon />
                      <div>
                        <strong>Documento de identidad</strong>
                        <span>{documentReady ? "Captura legible" : "Evita reflejos y bordes cortados"}</span>
                      </div>
                      <button type="button" className="button button-secondary" onClick={() => setDocumentReady(true)}>
                        {documentReady ? "✓ Capturado" : "Simular captura"}
                      </button>
                    </div>

                    <div className={`upload-card ${selfieReady ? "done" : ""}`}>
                      <FaceIcon />
                      <div>
                        <strong>Selfie y prueba de vida</strong>
                        <span>{selfieReady ? "Coincidencia biométrica: 92%" : "Busca un lugar con buena luz"}</span>
                      </div>
                      <button type="button" className="button button-secondary" onClick={() => setSelfieReady(true)}>
                        {selfieReady ? "✓ Verificada" : "Simular prueba"}
                      </button>
                    </div>
                  </div>
                </div>

                <div className="actions">
                  <button className="button button-ghost" type="button" onClick={saveDraft} disabled={loading}>
                    Guardar y continuar luego
                  </button>
                  <button className="button button-primary" disabled={loading}>
                    {loading ? "Guardando..." : "Revisar solicitud"}
                  </button>
                </div>
              </form>
            </section>
          )}

          {step === 3 && (
            <section className="step-content review-section">
              <p className="eyebrow">Paso 3 · Validación</p>
              <h2>Revisa y envía tu solicitud</h2>
              <p className="section-copy">
                Al enviar validaremos AML, score crediticio y ubicación de origen.
              </p>

              <div className="summary-card">
                <SummaryRow label="Documento" value={form.identityNumber} />
                <SummaryRow label="Nombre" value={form.fullName} />
                <SummaryRow label="Correo" value={form.email} />
                <SummaryRow label="Biometría" value={`${form.biometricScore}%`} />
              </div>

              <label className="terms">
                <input type="checkbox" required defaultChecked />
                <span>Acepto los términos y autorizo la evaluación de la solicitud.</span>
              </label>

              <div className="actions">
                <button className="button button-ghost" type="button" onClick={() => setStep(2)}>
                  Volver
                </button>
                <button className="button button-primary" type="button" onClick={finishApplication} disabled={loading}>
                  {loading ? "Evaluando solicitud..." : "Enviar solicitud"}
                </button>
              </div>
            </section>
          )}

          {step === 4 && application && (
            <ResultPanel application={application} onRestart={restart} />
          )}
        </div>
      </main>

      <footer>
        <span>© 2026 FinanciaPlus</span>
        <span>Tu información viaja de forma segura</span>
      </footer>
    </div>
  );
}

function applicationToForm(application) {
  return {
    fullName: application.fullName || "",
    address: application.address || "",
    birthDate: application.birthDate || "",
    gender: application.gender || "",
    identityNumber: application.identityNumber || "",
    email: application.email || "",
    phone: application.phone || "",
    biometricScore: application.biometricScore || 92,
  };
}

function SummaryRow({ label, value }) {
  return <div><span>{label}</span><strong>{value}</strong></div>;
}

function LockIcon() {
  return <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M7 10V7a5 5 0 0 1 10 0v3m-9 0h8a2 2 0 0 1 2 2v7H6v-7a2 2 0 0 1 2-2Z" /></svg>;
}

function ShieldIcon() {
  return <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 3 4.5 6v5c0 4.8 3 8.2 7.5 10 4.5-1.8 7.5-5.2 7.5-10V6L12 3Z" /><path d="m9 12 2 2 4-4" /></svg>;
}

function DocumentIcon() {
  return <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M6 3h8l4 4v14H6V3Z" /><path d="M14 3v5h4M9 13h6M9 17h4" /></svg>;
}

function FaceIcon() {
  return <svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="12" r="9" /><path d="M9 10h.01M15 10h.01M8.5 15c2.2 1.8 4.8 1.8 7 0" /></svg>;
}

export default App;

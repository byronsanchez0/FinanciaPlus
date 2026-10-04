const statusContent = {
  COMPLETED: {
    icon: "✓",
    title: "¡Solicitud completada!",
    text: "Tu Cuenta Digital y Tarjeta de Débito fueron originadas correctamente.",
    className: "success",
  },
  REJECTED_AML: {
    icon: "!",
    title: "No podemos continuar",
    text: "La validación de lista restrictiva no fue aprobada.",
    className: "danger",
  },
  REJECTED_SCORE: {
    icon: "!",
    title: "Solicitud no aprobada",
    text: "El score crediticio mínimo requerido es 7.0.",
    className: "danger",
  },
  APPROVED: {
    icon: "✓",
    title: "Solicitud aprobada",
    text: "La solicitud superó las validaciones y está lista para originarse.",
    className: "success",
  },
};

function ResultPanel({ application, onRestart }) {
  const content = statusContent[application.status] || statusContent.APPROVED;

  return (
    <section className={`result-card ${content.className}`}>
      <div className="result-icon" aria-hidden="true">{content.icon}</div>
      <p className="eyebrow">Paso 4 · Resultado</p>
      <h2>{content.title}</h2>
      <p>{content.text}</p>

      <dl className="result-details">
        <div>
          <dt>Número de solicitud</dt>
          <dd>{application.id}</dd>
        </div>
        {application.creditScore !== null && (
          <div>
            <dt>Score crediticio</dt>
            <dd>{application.creditScore}</dd>
          </div>
        )}
        {application.country && (
          <div>
            <dt>Origen de la solicitud</dt>
            <dd>{[application.city, application.region, application.country].filter(Boolean).join(", ")}</dd>
          </div>
        )}
      </dl>

      <button className="button button-primary" type="button" onClick={onRestart}>
        Crear otra solicitud
      </button>
    </section>
  );
}

export default ResultPanel;

const steps = ["Identidad", "Datos", "Validación", "Resultado"];

function ProgressSteps({ currentStep }) {
  return (
    <ol className="progress" aria-label="Progreso de la solicitud">
      {steps.map((step, index) => {
        const number = index + 1;
        const state =
          number < currentStep
            ? "complete"
            : number === currentStep
              ? "active"
              : "";

        return (
          <li className={state} key={step} aria-current={state === "active" ? "step" : undefined}>
            <span className="progress-number">
              {number}.
            </span>
            <span>{step}</span>
          </li>
        );
      })}
    </ol>
  );
}

export default ProgressSteps;

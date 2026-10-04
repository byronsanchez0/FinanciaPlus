function PersonalDataForm({ form, onChange }) {
  return (
    <div className="form-grid">
      <label className="field field-wide">
        <span>Nombre completo</span>
        <input
          name="fullName"
          value={form.fullName}
          onChange={onChange}
          placeholder="Ej. Andrea López"
          required
        />
      </label>

      <label className="field field-wide">
        <span>Dirección</span>
        <input
          name="address"
          value={form.address}
          onChange={onChange}
          placeholder="Zona, ciudad y departamento"
          required
        />
      </label>

      <label className="field">
        <span>Fecha de nacimiento</span>
        <input
          type="date"
          name="birthDate"
          value={form.birthDate}
          onChange={onChange}
          required
        />
      </label>

      <label className="field">
        <span>Género</span>
        <select name="gender" value={form.gender} onChange={onChange} required>
          <option value="">Selecciona una opción</option>
          <option value="Femenino">Femenino</option>
          <option value="Masculino">Masculino</option>
          <option value="No especificado">Prefiero no indicar</option>
        </select>
      </label>

      <label className="field">
        <span>Correo electrónico</span>
        <input
          type="email"
          name="email"
          value={form.email}
          onChange={onChange}
          placeholder="nombre@correo.com"
          required
        />
      </label>

      <label className="field">
        <span>Teléfono</span>
        <input
          type="tel"
          name="phone"
          value={form.phone}
          onChange={onChange}
          placeholder="5555 0000"
          required
        />
      </label>
    </div>
  );
}

export default PersonalDataForm;

const Input = ({
  label,
  type,
  placeholder,
  value,
  onChange = () => {},
  hasError,
  error,
}) => {
  let inputClassName = "form-control";

  if (hasError !== undefined) {
    inputClassName += hasError ? " is-invalid" : " is-valid";
  }

  return (
    <div>
      {label && <label>{label}</label>}
      <input
        className={inputClassName}
        type={type || "text"}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
      />
      {hasError && <span className="invalid-feedback">{error}</span>}
    </div>
  );
};

export default Input;

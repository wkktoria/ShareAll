import React from "react";

const ButtonWithProgress = ({ onClick, disabled, pendingApiCall, text }) => {
  return (
    <button className="btn btn-primary" onClick={onClick} disabled={disabled}>
      {pendingApiCall && (
        <div className="spinner-border spinner-border-sm" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      )}
      {text}
    </button>
  );
};

export default ButtonWithProgress;

import React from "react";
import Input from "../components/Input";

export class UserSignupPage extends React.Component {
  state = {
    displayName: "",
    username: "",
    password: "",
    passwordRepeat: "",
    pendingApiCall: false,
    errors: {},
    passwordRepeatConfirmed: true,
  };

  onChangeDisplayName = (event) => {
    const value = event.target.value;
    this.setState({ displayName: value });
  };

  onChangeUsername = (event) => {
    const value = event.target.value;
    this.setState({ username: value });
  };

  onChangePassword = (event) => {
    const value = event.target.value;
    const passwordRepeatConfirmed = this.state.passwordRepeat === value;
    const errors = { ...this.state.errors };
    errors.passwordRepeat = passwordRepeatConfirmed
      ? ""
      : "Does not match the password";

    this.setState({ password: value, passwordRepeatConfirmed, errors });
  };

  onChangePasswordRepeat = (event) => {
    const value = event.target.value;
    const passwordRepeatConfirmed = this.state.password === value;
    const errors = { ...this.state.errors };
    errors.passwordRepeat = passwordRepeatConfirmed
      ? ""
      : "Does not match the password";

    this.setState({ passwordRepeat: value, passwordRepeatConfirmed, errors });
  };

  onClickSignup = () => {
    const user = {
      username: this.state.username,
      displayName: this.state.displayName,
      password: this.state.password,
    };
    this.setState({ pendingApiCall: true });
    this.props.actions
      .postSignup(user)
      .then((_) => {
        this.setState({ pendingApiCall: false });
      })
      .catch((apiError) => {
        let errors = { ...this.state.errors };

        if (apiError.response.data && apiError.response.data.validationErrors) {
          errors = { ...apiError.response.data.validationErrors };
        }

        this.setState({ pendingApiCall: false, errors });
      });
  };

  render() {
    return (
      <div className="container">
        <h1 className="text-center">Sign Up</h1>
        <div className="col-12 mb-3">
          <Input
            label="Display Name"
            placeholder="Your display name"
            value={this.state.displayName}
            onChange={this.onChangeDisplayName}
            hasError={this.state.errors.displayName && true}
            error={this.state.errors.displayName}
          />
        </div>
        <div className="col-12 mb-3">
          <Input
            label="Username"
            className="form-control"
            placeholder="Your username"
            value={this.state.username}
            onChange={this.onChangeUsername}
            hasError={this.state.errors.username && true}
            error={this.state.errors.username}
          />
        </div>
        <div className="col-12 mb-3">
          <Input
            label="Password"
            className="form-control"
            placeholder="Your password"
            type="password"
            value={this.state.password}
            onChange={this.onChangePassword}
            hasError={this.state.errors.password && true}
            error={this.state.errors.password}
          />
        </div>
        <div className="col-12 mb-3">
          <Input
            label="Password Repeat"
            className="form-control"
            placeholder="Repeat your password"
            type="password"
            value={this.state.passwordRepeat}
            onChange={this.onChangePasswordRepeat}
            hasError={this.state.errors.passwordRepeat && true}
            error={this.state.errors.passwordRepeat}
          />
        </div>
        <div className="text-center">
          <button
            className="btn btn-primary"
            onClick={this.onClickSignup}
            disabled={
              this.state.pendingApiCall || !this.state.passwordRepeatConfirmed
            }
          >
            {this.state.pendingApiCall && (
              <div className="spinner-border spinner-border-sm" role="status">
                <span className="visually-hidden">Loading...</span>
              </div>
            )}
            Sign Up
          </button>
        </div>
      </div>
    );
  }
}

UserSignupPage.defaultProps = {
  actions: {
    postSignup: () => new Promise((resolve, _) => resolve({})),
  },
};

export default UserSignupPage;

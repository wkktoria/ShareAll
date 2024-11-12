import React from "react";
import * as apiCalls from "../api/apiCalls";

class UserPage extends React.Component {
  state = {
    user: undefined,
    userNotFound: false,
  };

  loadUser = () => {
    const username = this.props.match.params.username;

    if (!username) {
      return;
    }

    this.setState({ userNotFound: false });

    apiCalls
      .getUser(username)
      .then((response) => {
        this.setState({ user: response.data });
      })
      .catch((_) => {
        this.setState({ userNotFound: true });
      });
  };

  componentDidMount() {
    this.loadUser();
  }

  componentDidUpdate(prevProps) {
    if (prevProps.match.params.username !== this.props.match.params.username) {
      this.loadUser();
    }
  }

  render() {
    if (this.state.userNotFound) {
      return (
        <div className="alert alert-danger text-center" role="alert">
          <div className="alert-heading">
            <i className="fa-solid fa-triangle-exclamation fa-3x"></i>
          </div>
          <h5>User not found</h5>
        </div>
      );
    }

    return (
      <div data-testid="userpage">
        {this.state.user && (
          <span>{`${this.state.user.displayName}@${this.state.user.username}`}</span>
        )}
      </div>
    );
  }
}

UserPage.defaultProps = {
  match: {
    params: {},
  },
};

export default UserPage;

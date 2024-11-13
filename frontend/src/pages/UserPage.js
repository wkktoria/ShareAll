import React from "react";
import * as apiCalls from "../api/apiCalls";
import ProfileCard from "../components/ProfileCard";

class UserPage extends React.Component {
  state = {
    user: undefined,
    userNotFound: false,
    isLoadingUser: false,
  };

  loadUser = () => {
    const username = this.props.match.params.username;

    if (!username) {
      return;
    }

    this.setState({ userNotFound: false, isLoadingUser: true });

    apiCalls
      .getUser(username)
      .then((response) => {
        this.setState({ user: response.data, isLoadingUser: false });
      })
      .catch((_) => {
        this.setState({ userNotFound: true, isLoadingUser: false });
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
    let pageContent;

    if (this.state.userNotFound) {
      pageContent = (
        <div className="alert alert-danger text-center" role="alert">
          <div className="alert-heading">
            <i className="fa-solid fa-triangle-exclamation fa-3x"></i>
          </div>
          <h5>User not found</h5>
        </div>
      );
    } else if (this.state.isLoadingUser) {
      pageContent = (
        <div className="d-flex">
          <div className="spinner-border text-black-50 m-auto" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
        </div>
      );
    } else {
      pageContent = this.state.user && <ProfileCard user={this.state.user} />;
    }

    return <div data-testid="userpage">{pageContent}</div>;
  }
}

UserPage.defaultProps = {
  match: {
    params: {},
  },
};

export default UserPage;

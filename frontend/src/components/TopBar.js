import React from "react";
import logo from "../assets/shareall-logo.svg";
import { Link } from "react-router-dom";
import { connect } from "react-redux";
import ProfileImageWithDefault from "./ProfileImageWithDefault";

class TopBar extends React.Component {
  state = {
    dropdownVisible: false,
  };

  componentDidMount() {
    document.addEventListener("click", this.onClickTracker);
  }

  componentWillUnmount() {
    document.removeEventListener("click", this.onClickTracker);
  }

  onClickTracker = (event) => {
    if (this.actionArea && !this.actionArea.contains(event.target)) {
      this.setState({ dropdownVisible: false });
    }
  };

  onClickDisplayName = () => {
    this.setState({ dropdownVisible: true });
  };

  onClickLogout = () => {
    this.setState({ dropdownVisible: false });
    const action = {
      type: "logout-success",
    };
    this.props.dispatch(action);
  };

  onClickMyProfile = () => {
    this.setState({ dropdownVisible: false });
  };

  assignActionArea = (area) => {
    this.actionArea = area;
  };

  render() {
    let links = (
      <ul className="nav navbar-nav ms-auto">
        <li className="nav-item">
          <Link to="/signup" className="nav-link">
            Sign Up
          </Link>
        </li>
        <li className="nav-item">
          <Link to="/login" className="nav-link">
            Login
          </Link>
        </li>
      </ul>
    );

    if (this.props.user.isLoggedIn) {
      let dropdownClass = "p-0 shadow dropdown-menu";

      if (this.state.dropdownVisible) {
        dropdownClass += " show";
      }

      links = (
        <ul className="nav navbar-nav ms-auto">
          <li className="nav-item dropdown" ref={this.assignActionArea}>
            <div
              className="d-flex"
              style={{ cursor: "pointer" }}
              onClick={this.onClickDisplayName}
            >
              <ProfileImageWithDefault
                className="rounded-circle m-auto"
                width="32"
                height="32"
                image={this.props.user.image}
              />
              <span className="nav-link dropdown-toggle">
                {this.props.user.displayName}
              </span>
            </div>
            <div className={dropdownClass} data-testid="dropdown-menu">
              <Link
                className="dropdown-item"
                to={`/${this.props.user.username}`}
                onClick={this.onClickMyProfile}
              >
                <i className="fa-solid fa-user text-info"></i> My Profile
              </Link>
              <span
                className="dropdown-item"
                onClick={this.onClickLogout}
                style={{ cursor: "pointer" }}
              >
                <i className="fa-solid fa-right-from-bracket text-danger"></i>{" "}
                Logout
              </span>
            </div>
          </li>
        </ul>
      );
    }

    return (
      <div className="bg-white shadow-sm mb-2">
        <div className="container">
          <nav className="navbar navbar-light navbar-expand">
            <Link to="/" className="navbar-brand">
              <img src={logo} width="64" alt="ShareAll" className="me-2" />
              ShareAll
            </Link>
            {links}
          </nav>
        </div>
      </div>
    );
  }
}

const mapStateToProps = (state) => {
  return {
    user: state,
  };
};

export default connect(mapStateToProps)(TopBar);

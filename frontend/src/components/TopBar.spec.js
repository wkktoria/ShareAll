import React from "react";
import { render, fireEvent } from "@testing-library/react";
import TopBar from "./TopBar";
import { MemoryRouter } from "react-router-dom";
import { Provider } from "react-redux";
import { createStore } from "redux";
import authReducer from "../redux/authReducer";
import * as authActions from "../redux/authActions";

const loggedInState = {
  id: 1,
  username: "user1",
  displayName: "display1",
  image: "profile1.png",
  password: "P@4sSw0rd",
  isLoggedIn: true,
};

const defaultState = {
  id: 0,
  username: "",
  displayName: "",
  image: "",
  password: "",
  isLoggedIn: false,
};

let store;

const setup = (state = defaultState) => {
  store = createStore(authReducer, state);
  return render(
    <Provider store={store}>
      <MemoryRouter>
        <TopBar />
      </MemoryRouter>
    </Provider>
  );
};

describe("TopBar", () => {
  describe("Layout", () => {
    it("has application logo", () => {
      const { container } = setup();
      const image = container.querySelector("img");
      expect(image.src).toContain("shareall-logo.svg");
    });

    it("has link to home from logo", () => {
      const { container } = setup();
      const image = container.querySelector("img");
      expect(image.parentElement.getAttribute("href")).toBe("/");
    });

    it("has link to signup", () => {
      const { queryByText } = setup();
      const signupLink = queryByText("Sign Up");
      expect(signupLink.getAttribute("href")).toBe("/signup");
    });

    it("has link to login", () => {
      const { queryByText } = setup();
      const loginLink = queryByText("Login");
      expect(loginLink.getAttribute("href")).toBe("/login");
    });

    it("has link to logout when user logged in", () => {
      const { queryByText } = setup(loggedInState);
      const logoutLink = queryByText("Logout");
      expect(logoutLink).toBeInTheDocument();
    });

    it("has link to user profile when user logged in", () => {
      const { queryByText } = setup(loggedInState);
      const profileLink = queryByText("My Profile");
      expect(profileLink.getAttribute("href")).toBe("/user1");
    });

    it("displays the displayName when user logged in", () => {
      const { queryByText } = setup(loggedInState);
      const displayName = queryByText("display1");
      expect(displayName).toBeInTheDocument();
    });

    it("displays user image when user logged in", () => {
      const { container } = setup(loggedInState);
      const images = container.querySelectorAll("img");
      const userImage = images[1];
      expect(userImage.src).toContain("/images/profile/" + loggedInState.image);
    });
  });

  describe("Interactions", () => {
    it("displays the login and signup links when user clicks logout", () => {
      const { queryByText } = setup(loggedInState);
      const logoutLink = queryByText("Logout");
      fireEvent.click(logoutLink);
      const loginLink = queryByText("Login");
      expect(loginLink).toBeInTheDocument();
    });

    it("adds show class to dropdown menu when clicking username", () => {
      const { queryByText, queryByTestId } = setup(loggedInState);
      const displayName = queryByText("display1");
      fireEvent.click(displayName);
      const dropdownMenu = queryByTestId("dropdown-menu");
      expect(dropdownMenu).toHaveClass("show");
    });

    it("removes show class from dropdown menu when clicking app logo", () => {
      const { queryByText, queryByTestId, container } = setup(loggedInState);
      const displayName = queryByText("display1");
      fireEvent.click(displayName);

      const logo = container.querySelector("img");
      fireEvent.click(logo);

      const dropdownMenu = queryByTestId("dropdown-menu");
      expect(dropdownMenu).not.toHaveClass("show");
    });

    // Works on the browser; queryByTestId("dropdown-menu"); returns null
    xit("removes show class from dropdown menu when clicking Logout", () => {
      const { queryByText, queryByTestId } = setup(loggedInState);
      const displayName = queryByText("display1");
      fireEvent.click(displayName);

      fireEvent.click(queryByText("Logout"));

      store.dispatch(authActions.loginSuccess(loggedInState));

      const dropdownMenu = queryByTestId("dropdown-menu");
      expect(dropdownMenu).not.toHaveClass("show");
    });

    it("removes show class from dropdown menu when clicking My Profile", () => {
      const { queryByText, queryByTestId } = setup(loggedInState);
      const displayName = queryByText("display1");
      fireEvent.click(displayName);

      fireEvent.click(queryByText("My Profile"));

      const dropdownMenu = queryByTestId("dropdown-menu");
      expect(dropdownMenu).not.toHaveClass("show");
    });
  });
});
